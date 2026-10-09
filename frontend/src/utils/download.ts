type DownloadResponse = {
  data?: ArrayBuffer | Uint8Array | Blob;
  headers?: unknown;
};

const XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

function filenameFromResponse(response: DownloadResponse, fallbackName: string) {
  const disposition = header(response, "content-disposition");
  const encoded = disposition.match(/filename\*\s*=\s*UTF-8''([^;]+)/i)?.[1];
  const plain = disposition.match(/filename\s*=\s*"?([^";]+)"?/i)?.[1];
  const value = encoded ?? plain;
  if (!value) return fallbackName;
  try {
    return decodeURIComponent(value.replace(/\+/g, " ")).replace(/[<>:"/\\|?*\x00-\x1f]/g, "_");
  } catch {
    return fallbackName;
  }
}

function extensionOf(filename: string) {
  return filename.split(".").pop()?.toLowerCase() || "bin";
}

function filterName(extension: string) {
  if (["xlsx", "xls"].includes(extension)) return "Excel 工作簿";
  if (extension === "pdf") return "PDF 文件";
  if (extension === "zip") return "ZIP 压缩包";
  return "导出文件";
}

function toBlob(payload: DownloadResponse["data"], mimeType: string) {
  if (payload instanceof Blob) return payload;
  if (payload instanceof ArrayBuffer || payload instanceof Uint8Array) {
    return new Blob([payload], { type: mimeType });
  }
  return undefined;
}

function header(response: DownloadResponse, name: string) {
  if (!response.headers || typeof response.headers !== "object") return "";
  const value = Object.entries(response.headers as Record<string, unknown>).find(([key]) => key.toLowerCase() === name)?.[1];
  return typeof value === "string" ? value : "";
}

function readableError(blob: Blob) {
  return blob.slice(0, 4096).text().then((text) => {
    try {
      const body = JSON.parse(text) as { msg?: unknown; message?: unknown; error?: unknown };
      for (const value of [body.msg, body.message, body.error]) {
        if (typeof value === "string" && value.trim()) return value;
      }
    } catch {
      // The payload may be an HTML error page or a real binary file.
    }
    return "服务器返回的内容不是有效导出文件，请检查本地服务或导出条件";
  });
}

async function assertFilePayload(response: DownloadResponse, blob: Blob, filename: string) {
  const contentType = header(response, "content-type").toLowerCase();
  const extension = extensionOf(filename);
  const bytes = new Uint8Array(await blob.slice(0, 8).arrayBuffer());
  const startsWith = (...signature: number[]) => signature.every((value, index) => bytes[index] === value);
  const isZip = startsWith(0x50, 0x4b);
  const isPdf = startsWith(0x25, 0x50, 0x44, 0x46);
  const isLegacyExcel = startsWith(0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1);
  const isRecognizedFile = ["xlsx", "xlsm", "zip"].includes(extension)
    ? isZip
    : extension === "xls"
      ? isLegacyExcel
      : extension === "pdf"
        ? isPdf
        : true;

  if (contentType.includes("application/json") || contentType.includes("text/html") || !isRecognizedFile) {
    throw new Error(await readableError(blob));
  }
}

/** Save binary exports through a native save dialog in Tauri, or the browser download flow in web mode. */
export async function downloadFile(response: DownloadResponse, fallbackName: string, mimeType = XLSX_MIME) {
  const filename = filenameFromResponse(response, fallbackName);
  const blob = toBlob(response.data, mimeType);
  if (!blob || blob.size === 0) {
    ElMessage.error("没有收到有效文件，请检查本地服务后重试");
    return;
  }

  try {
    await assertFilePayload(response, blob, filename);
    if (import.meta.env.MODE === "tauri") {
      const [{ save }, { writeFile }] = await Promise.all([
        import("@tauri-apps/plugin-dialog"),
        import("@tauri-apps/plugin-fs"),
      ]);
      const extension = extensionOf(filename);
      const path = await save({
        title: "保存导出文件",
        defaultPath: filename,
        filters: [{ name: filterName(extension), extensions: [extension] }],
      });
      if (!path) {
        ElMessage.info("已取消保存");
        return;
      }
      await writeFile(path, new Uint8Array(await blob.arrayBuffer()));
      await ElMessageBox.alert(`文件已保存到：\n${path}`, "导出完成", {
        confirmButtonText: "知道了",
        type: "success",
      });
      return;
    }

    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    link.style.display = "none";
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.setTimeout(() => URL.revokeObjectURL(url), 3000);
    ElMessage.success("下载已开始；浏览器版文件通常在“下载”文件夹，也可按 Ctrl+J 查看具体位置");
  } catch (error) {
    console.error("保存导出文件失败", error);
    const detail = error instanceof Error ? error.message : String(error);
    ElMessage.error(`文件保存失败：${detail}`);
  }
}
