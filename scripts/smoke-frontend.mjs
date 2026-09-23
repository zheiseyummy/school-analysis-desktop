// Browser smoke check of the real production default page, without mocked APIs.
// Run `pnpm preview` in frontend first. This is not a database integration test.
import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { spawn } from "node:child_process";
import assert from "node:assert/strict";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const output = path.join(root, ".test-results");
await fs.mkdir(output, { recursive: true });
const profile = await fs.mkdtemp(path.join(output, "browser-profile-"));
const candidates = [
  process.env.LOCAL_BROWSER_PATH,
  "C:/Program Files/Google/Chrome/Application/chrome.exe",
  "C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe",
].filter(Boolean);
let executable;
for (const candidate of candidates) {
  if (await fs.access(candidate).then(() => true, () => false)) { executable = candidate; break; }
}
assert.ok(executable, "Install Chrome/Edge or set LOCAL_BROWSER_PATH");
const browser = spawn(executable, [
  "--headless=new", "--disable-gpu", "--no-first-run", "--no-default-browser-check",
  "--disable-background-networking", "--remote-debugging-port=0",
  "--remote-debugging-address=127.0.0.1", `--user-data-dir=${profile}`, "about:blank",
], { windowsHide: true, stdio: "ignore" });
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
let socket;
let command;
try {
  let port;
  for (let attempt = 0; attempt < 100; attempt++) {
    const active = await fs.readFile(path.join(profile, "DevToolsActivePort"), "utf8").catch(() => "");
    if (active) { port = Number(active.split("\n")[0]); break; }
    await delay(100);
  }
  assert.ok(port, "Browser debugging endpoint did not start");
  const tabs = await fetch(`http://127.0.0.1:${port}/json/list`).then((r) => r.json());
  const page = tabs.find((tab) => tab.type === "page");
  socket = new WebSocket(page.webSocketDebuggerUrl);
  await new Promise((resolve, reject) => {
    socket.addEventListener("open", resolve, { once: true });
    socket.addEventListener("error", reject, { once: true });
  });
  let sequence = 0;
  const pending = new Map();
  const requests = [];
  const errors = [];
  socket.addEventListener("message", (event) => {
    const packet = JSON.parse(event.data);
    if (packet.id) {
      const handler = pending.get(packet.id);
      if (handler) { pending.delete(packet.id); packet.error ? handler.reject(packet.error) : handler.resolve(packet.result); }
    } else if (packet.method === "Network.requestWillBeSent") {
      requests.push(packet.params.request.url);
    } else if (packet.method === "Runtime.exceptionThrown") {
      errors.push(packet.params.exceptionDetails.exception?.description || packet.params.exceptionDetails.text);
    }
  });
  command = (method, params = {}) => new Promise((resolve, reject) => {
    const id = ++sequence;
    const timer = setTimeout(() => { pending.delete(id); reject(new Error(`Timed out: ${method}`)); }, 10000);
    pending.set(id, {
      resolve: (value) => { clearTimeout(timer); resolve(value); },
      reject: (value) => { clearTimeout(timer); reject(value); },
    });
    socket.send(JSON.stringify({ id, method, params }));
  });
  await command("Page.enable");
  await command("Network.enable");
  await command("Runtime.enable");
  await command("Emulation.setDeviceMetricsOverride", { width: 1440, height: 1000, deviceScaleFactor: 1, mobile: false });
  await command("Page.navigate", { url: "http://127.0.0.1:4173/#/base/grade" });
  let body = "";
  for (let attempt = 0; attempt < 100; attempt++) {
    const result = await command("Runtime.evaluate", { expression: "document.body.innerText", returnByValue: true });
    body = result.result.value || "";
    if (body.includes("本地用户") && body.includes("基础信息")) break;
    await delay(100);
  }
  await delay(500);
  for (const label of ["本地用户", "基础信息", "年级管理", "成绩管理", "学情分析"]) assert.ok(body.includes(label), `Missing ${label}`);
  const location = await command("Runtime.evaluate", { expression: "location.hash", returnByValue: true });
  assert.equal(location.result.value, "#/base/grade", "Default business route unexpectedly redirected");
  const runtimeErrors = errors.filter((error) => !error.startsWith("AxiosError:"));
  assert.deepEqual(runtimeErrors, [], "Browser raised an uncaught non-API exception");
  assert.deepEqual(requests.filter((url) => /^https?:/.test(url) && !url.startsWith("http://127.0.0.1:4173/")), [], "Default page requested a remote resource");
  const screenshot = await command("Page.captureScreenshot", { format: "png" });
  await fs.writeFile(path.join(output, "default-page.png"), Buffer.from(screenshot.data, "base64"));
  const report = { passed: true, scope: "default business page shell; no database integration", requests: requests.length, apiErrors: errors.length - runtimeErrors.length, browserErrors: runtimeErrors.length, screenshot: ".test-results/default-page.png" };
  await fs.writeFile(path.join(output, "frontend-smoke.json"), JSON.stringify(report, null, 2) + "\n");
  console.log(JSON.stringify(report, null, 2));
} finally {
  if (command && socket?.readyState === WebSocket.OPEN) await command("Browser.close").catch(() => {});
  socket?.close();
  if (browser.exitCode === null) browser.kill();
}
