// Static wiring check for the staged Vue / Spring Boot migration.
// This does not replace database integration or browser testing.
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const front = path.join(root, "frontend", "src");
const controllers = path.join(root, "backend", "src", "main", "java", "com", "youlai", "system", "controller");
const failures = [];
const files = (dir) => fs.existsSync(dir)
  ? fs.readdirSync(dir, { withFileTypes: true }).flatMap((entry) => {
      const full = path.join(dir, entry.name);
      return entry.isDirectory() ? files(full) : [full];
    })
  : [];
const read = (file) => fs.readFileSync(file, "utf8");
const relative = (file) => path.relative(root, file);
const normalize = (url) => url.replace(/\{[^}]*\}/g, "{}").replace(/\/+$/, "");

// All locally referenced source modules and route components must exist.
let localImports = 0;
for (const file of files(front).filter((f) => /\.(vue|ts)$/.test(f) && !f.endsWith(".d.ts"))) {
  const source = read(file);
  const imports = source.matchAll(/(?:from\s*|import\s*\(|import\s*)["'](@\/[^"']+|\.{1,2}\/[^"']+)["']/g);
  for (const [, specifier] of imports) {
    const resolved = specifier.startsWith("@/")
      ? path.join(front, specifier.slice(2))
      : path.resolve(path.dirname(file), specifier);
    const candidates = [resolved, ...[".ts", ".js", ".vue", ".json", "/index.ts", "/index.vue"].map((suffix) => resolved + suffix)];
    if (!candidates.some((candidate) => fs.existsSync(candidate) && fs.statSync(candidate).isFile())) {
      failures.push(`${relative(file)}: missing module ${specifier}`);
    }
    localImports++;
  }
}

// Compare the actual Vue REST adapters with Spring controller mappings.
// Dynamic IDs are normalized; unsupported expressions fail instead of being skipped.
const endpoints = new Set();
const controllerFiles = files(controllers).filter((file) => file.endsWith(".java"));
for (const file of controllerFiles) {
  const source = read(file);
  const classIndex = source.indexOf("public class ");
  const prefix = source.slice(0, classIndex).match(/@RequestMapping\(\s*"([^"]+)"\s*\)/)?.[1];
  if (!prefix) continue;
  for (const match of source.slice(classIndex).matchAll(/@(Get|Post|Put|Patch|Delete|Request)Mapping(?:\(([^)]*)\))?/g)) {
    const suffix = match[2]?.match(/"([^"]*)"/)?.[1] ?? "";
    endpoints.add(`${match[1] === "Request" ? "*" : match[1].toUpperCase()} ${normalize(prefix + suffix)}`);
  }
}
let adapters = 0;
for (const file of files(path.join(front, "api")).filter((f) => f.endsWith(".ts") && !f.endsWith("types.ts"))) {
  const source = read(file);
  for (const match of source.matchAll(/url:\s*([^\r\n]+)([\s\S]*?)(?=\}\);)/g)) {
    const expression = match[1].replace(/,\s*$/, "").trim();
    const method = match[2].match(/method:\s*["']([^"']+)["']/)?.[1]?.toUpperCase() ?? "GET";
    const tokens = expression.split(/\s*\+\s*/);
    const valid = tokens.every((token) => /^(["']).*\1$/.test(token) || /^[\w.]+$/.test(token));
    if (!valid) {
      failures.push(`${relative(file)}: unsupported URL expression ${expression}`);
      continue;
    }
    const url = normalize(tokens.map((token) => /^["']/.test(token) ? token.slice(1, -1) : "{}").join(""));
    if (!endpoints.has(`${method} ${url}`) && !endpoints.has(`* ${url}`)) {
      failures.push(`${relative(file)}: no backend endpoint for ${method} ${url}`);
    }
    adapters++;
  }
}

console.log(JSON.stringify({ localImports, controllers: controllerFiles.length, apiAdapters: adapters, errors: failures.length }, null, 2));
if (failures.length) {
  for (const failure of failures) console.error(failure);
  process.exitCode = 1;
}
