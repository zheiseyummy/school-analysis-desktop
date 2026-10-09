// Browser smoke check of the built frontend, optionally against the packaged desktop WebView.
import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { spawn } from "node:child_process";
import assert from "node:assert/strict";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const output = path.join(root, ".test-results");
const externalCdpPort = Number(process.env.SMOKE_CDP_PORT || 0);
const deleteGradeCode = process.env.SMOKE_DELETE_GRADE_CODE;
await fs.mkdir(output, { recursive: true });
let profile;
let browser;
if (!externalCdpPort) {
  profile = await fs.mkdtemp(path.join(output, "browser-profile-"));
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
  browser = spawn(executable, [
    "--headless=new", "--disable-gpu", "--no-first-run", "--no-default-browser-check",
    "--disable-background-networking", "--remote-debugging-port=0",
    "--remote-debugging-address=127.0.0.1", `--user-data-dir=${profile}`, "about:blank",
  ], { windowsHide: true, stdio: "ignore" });
}
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
let socket;
let command;
try {
  let port = externalCdpPort;
  if (!port) {
    for (let attempt = 0; attempt < 100; attempt++) {
      const active = await fs.readFile(path.join(profile, "DevToolsActivePort"), "utf8").catch(() => "");
      if (active) { port = Number(active.split("\n")[0]); break; }
      await delay(100);
    }
  }
  assert.ok(port, "Browser debugging endpoint did not start");
  const tabs = await fetch(`http://127.0.0.1:${port}/json/list`).then((r) => r.json());
  const page = tabs.find((tab) => tab.type === "page");
  assert.ok(page, "No browser page available for smoke test");
  const baseUrl = process.env.SMOKE_BASE_URL || (externalCdpPort ? new URL(page.url).origin : "http://127.0.0.1:4173");
  const apiUrl = process.env.SMOKE_API_URL || baseUrl;
  socket = new WebSocket(page.webSocketDebuggerUrl);
  await new Promise((resolve, reject) => {
    socket.addEventListener("open", resolve, { once: true });
    socket.addEventListener("error", reject, { once: true });
  });
  let sequence = 0;
  const pending = new Map();
  const requests = [];
  const errors = [];
  const failedRequests = [];
  socket.addEventListener("message", (event) => {
    const packet = JSON.parse(event.data);
    if (packet.id) {
      const handler = pending.get(packet.id);
      if (handler) { pending.delete(packet.id); packet.error ? handler.reject(packet.error) : handler.resolve(packet.result); }
    } else if (packet.method === "Network.requestWillBeSent") {
      requests.push(packet.params.request.url);
    } else if (packet.method === "Runtime.exceptionThrown") {
      errors.push(packet.params.exceptionDetails.exception?.description || packet.params.exceptionDetails.text);
    } else if (packet.method === "Network.loadingFailed") {
      failedRequests.push(packet.params.errorText);
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
  if (externalCdpPort) {
    await command("Page.reload", { ignoreCache: true });
  } else {
    await command("Page.navigate", { url: `${baseUrl}/#/base/grade` });
  }
  let body = "";
  for (let attempt = 0; attempt < 100; attempt++) {
    const result = await command("Runtime.evaluate", { expression: "document.body.innerText", returnByValue: true });
    body = result.result.value || "";
    if (body.includes("基础信息") && body.includes("年级编号") && body.includes("年级名称")) break;
    await delay(100);
  }
  await delay(500);
  for (const label of ["基础信息", "年级管理", "年级编号", "年级名称", "新增", "成绩管理", "学情分析"]) {
    assert.ok(body.includes(label), `Missing ${label}: ${JSON.stringify({body: body.slice(0, 500), errors, failedRequests, lastRequests: requests.slice(-10).map((url) => url.slice(0, 200))})}`);
  }
  const location = await command("Runtime.evaluate", { expression: "location.hash", returnByValue: true });
  assert.equal(location.result.value, "#/base/grade", "Default business route unexpectedly redirected");
  if (deleteGradeCode) {
    let gradeVisible = false;
    for (let attempt = 0; attempt < 100; attempt++) {
      const result = await command("Runtime.evaluate", {
        expression: `document.querySelector('.app-main')?.innerText.includes(${JSON.stringify(deleteGradeCode)}) || false`,
        returnByValue: true,
      });
      gradeVisible = result.result.value;
      if (gradeVisible) break;
      await delay(100);
    }
    const gradeState = await command("Runtime.evaluate", { expression: "({hash: location.hash, content: document.querySelector('.app-main')?.innerText?.slice(0, 500), href: location.href})", returnByValue: true });
    assert.ok(gradeVisible, `Synthetic grade ${deleteGradeCode} was not shown: ${JSON.stringify({state: gradeState.result.value, errors, failedRequests, lastRequests: requests.slice(-10).map((url) => url.slice(0, 200))})}`);
    const clicked = await command("Runtime.evaluate", {
      expression: `(() => { const row = [...document.querySelectorAll('.el-table__body tr')].find(item => item.innerText.includes(${JSON.stringify(deleteGradeCode)})); const button = row && [...row.querySelectorAll('button')].find(item => item.innerText.includes('删除')); button?.click(); return !!button; })()`,
      returnByValue: true,
    });
    assert.ok(clicked.result.value, "Synthetic grade delete button was not found");
    let confirmed = false;
    for (let attempt = 0; attempt < 100; attempt++) {
      const result = await command("Runtime.evaluate", {
        expression: "(() => { const button = document.querySelector('.el-message-box__btns .el-button--primary'); if (!button) return false; button.click(); return true; })()",
        returnByValue: true,
      });
      confirmed = result.result.value;
      if (confirmed) break;
      await delay(100);
    }
    assert.ok(confirmed, "Delete confirmation dialog did not appear");
  }
  const routeChecks = [
    ["/base/clazz", "班级编号"],
    ["/base/student", "学号"],
    ["/score/exam", "考试名称"],
    ["/quality/evaluation", "综合素质评价"],
    ["/base/grade", "年级编号"],
  ];
  for (const [route, label] of routeChecks) {
    await command("Runtime.evaluate", { expression: `location.hash = ${JSON.stringify(`#${route}`)}` });
    let content = "";
    for (let attempt = 0; attempt < 100; attempt++) {
      const result = await command("Runtime.evaluate", {
        expression: "document.querySelector('.app-main')?.innerText || ''",
        returnByValue: true,
      });
      content = result.result.value || "";
      if (content.includes(label)) break;
      await delay(100);
    }
    const dom = await command("Runtime.evaluate", { expression: "document.querySelector('.app-main')?.outerHTML.slice(0, 1000)", returnByValue: true });
    const currentHash = await command("Runtime.evaluate", { expression: "location.hash", returnByValue: true });
    assert.ok(content.includes(label), `Route ${route} rendered blank or missed ${label}: ${JSON.stringify({content: content.slice(0, 300), hash: currentHash.result.value, dom: dom.result.value, errors, failedRequests, lastRequests: requests.slice(-10).map((url) => url.slice(0, 200))})}`);
  }
  const runtimeErrors = errors.filter((error) => !error.startsWith("AxiosError:"));
  assert.deepEqual(runtimeErrors, [], "Browser raised an uncaught non-API exception");
  assert.deepEqual(requests.filter((url) => /^https?:/.test(url) && !url.startsWith(`${baseUrl}/`) && !url.startsWith("http://127.0.0.1:8989/")), [], "Application requested a remote resource");
  if (deleteGradeCode) {
    const response = await fetch(`${apiUrl}/api/v1/grades/page?pageNum=1&pageSize=100`);
    const page = await response.json();
    assert.ok(!page.data.list.some((grade) => grade.code === deleteGradeCode), "Confirmed synthetic grade was not deleted");
  }
  const screenshot = await command("Page.captureScreenshot", { format: "png" });
  await fs.writeFile(path.join(output, "default-page.png"), Buffer.from(screenshot.data, "base64"));
  const report = { passed: true, scope: deleteGradeCode ? "confirmed grade deletion and module switching on isolated backend" : "default page and module switching; no database integration", routes: routeChecks.length + 1, requests: requests.length, apiErrors: errors.length - runtimeErrors.length, browserErrors: runtimeErrors.length, screenshot: ".test-results/default-page.png" };
  await fs.writeFile(path.join(output, "frontend-smoke.json"), JSON.stringify(report, null, 2) + "\n");
  console.log(JSON.stringify(report, null, 2));
} finally {
  if (browser && command && socket?.readyState === WebSocket.OPEN) await command("Browser.close").catch(() => {});
  socket?.close();
  if (browser?.exitCode === null) {
    browser.kill();
    await new Promise((resolve) => browser.once("exit", resolve));
  }
  if (profile) {
    for (let attempt = 0; attempt < 5; attempt++) {
      try { await fs.rm(profile, { recursive: true, force: true }); break; }
      catch { await delay(100); }
    }
  }
}
