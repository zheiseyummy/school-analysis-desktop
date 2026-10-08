#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use std::fs;
use std::io::{Read, Write};
use std::net::{SocketAddr, TcpStream};
use std::path::{Path, PathBuf};
use std::process::{Child, Command, Stdio};
use std::sync::Mutex;
use std::thread;
use std::time::{Duration, Instant};
use tauri::Manager;

const BACKEND_PORT: u16 = 8989;
const PRODUCT_NAME: &str = "成绩分析系统";

struct BackendProcess(Mutex<Option<Child>>);

fn project_root() -> PathBuf {
    PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .parent()
        .and_then(Path::parent)
        .expect("Tauri source must be nested under frontend/src-tauri")
        .to_path_buf()
}

fn app_data_dir() -> PathBuf {
    let base = std::env::var_os("LOCALAPPDATA")
        .map(PathBuf::from)
        .or_else(|| std::env::var_os("USERPROFILE").map(PathBuf::from))
        .unwrap_or_else(std::env::temp_dir);
    base.join(PRODUCT_NAME)
}

fn bundled_backend_paths() -> (PathBuf, PathBuf) {
    let executable_dir = std::env::current_exe()
        .ok()
        .and_then(|path| path.parent().map(Path::to_path_buf));

    if let Some(directory) = executable_dir {
        let java = directory.join("runtime").join("bin").join("javaw.exe");
        let jar = directory.join("backend.jar");
        if java.exists() && jar.exists() {
            return (java, jar);
        }
    }

    let root = project_root();
    let jdk_root = fs::read_dir(root.join(".tools"))
        .ok()
        .and_then(|entries| {
            entries
                .filter_map(Result::ok)
                .map(|entry| entry.path())
                .find(|path| path.join("bin").join("javaw.exe").exists())
        })
        .unwrap_or_else(|| root.join(".tools").join("jdk"));
    (
        jdk_root.join("bin").join("javaw.exe"),
        root.join("backend")
            .join("target")
            .join("school-analysis-desktop-backend.jar"),
    )
}

fn local_app_http_response() -> Result<Option<String>, String> {
    let address = SocketAddr::from(([127, 0, 0, 1], BACKEND_PORT));
    let mut stream = match TcpStream::connect_timeout(&address, Duration::from_millis(250)) {
        Ok(stream) => stream,
        Err(_) => return Ok(None),
    };
    stream
        .set_read_timeout(Some(Duration::from_secs(2)))
        .map_err(|error| error.to_string())?;
    stream
        .write_all(b"GET / HTTP/1.1\r\nHost: 127.0.0.1\r\nConnection: close\r\n\r\n")
        .map_err(|error| error.to_string())?;
    let mut response = String::new();
    stream
        .read_to_string(&mut response)
        .map_err(|error| error.to_string())?;
    if response.contains("<title>成绩分析系统</title>") {
        Ok(Some(response))
    } else {
        Err(format!(
            "本机端口 {BACKEND_PORT} 已被其他程序占用。请关闭占用该端口的程序后重试。"
        ))
    }
}

fn start_or_reuse_backend() -> Result<Option<Child>, String> {
    if local_app_http_response()?.is_some() {
        return Ok(None);
    }

    let (java, jar) = bundled_backend_paths();
    if !java.exists() {
        return Err(format!(
            "未找到随程序附带的 Java 运行环境：{}",
            java.display()
        ));
    }
    if !jar.exists() {
        return Err(format!("未找到本地服务程序：{}", jar.display()));
    }

    let data_dir = app_data_dir();
    for directory in ["data", "backup", "export", "files", "logs"] {
        fs::create_dir_all(data_dir.join(directory))
            .map_err(|error| format!("无法创建本地数据目录：{error}"))?;
    }

    let child = Command::new(&java)
        .arg("-jar")
        .arg(&jar)
        .arg("--app.desktop.open-browser=false")
        .env("SCORE_ANALYSIS_HOME", &data_dir)
        .env("LOCAL_FILES_DIR", data_dir.join("files"))
        .env("LOCAL_LOG_DIR", data_dir.join("logs"))
        .env("SERVER_ADDRESS", "127.0.0.1")
        .env("SERVER_PORT", BACKEND_PORT.to_string())
        .stdin(Stdio::null())
        .stdout(Stdio::null())
        .stderr(Stdio::null())
        .spawn()
        .map_err(|error| format!("成绩分析系统本地服务启动失败：{error}"))?;

    let mut child = child;
    let started = Instant::now();
    while started.elapsed() < Duration::from_secs(90) {
        match local_app_http_response() {
            Ok(Some(_)) => return Ok(Some(child)),
            Ok(None) => {}
            Err(error) => {
                let _ = child.kill();
                let _ = child.wait();
                return Err(error);
            }
        }
        if let Some(status) = child
            .try_wait()
            .map_err(|error| format!("检查本地服务状态失败：{error}"))?
        {
            return Err(format!(
                "本地服务已退出（状态 {status}）。请确认程序文件已完整解压，且本机 8989 端口未被占用。"
            ));
        }
        thread::sleep(Duration::from_millis(500));
    }

    let _ = child.kill();
    let _ = child.wait();
    Err("本地服务启动超时。请稍后重试；若仍失败，请检查本机 8989 端口。".to_string())
}

#[cfg(windows)]
fn show_startup_error(message: &str) {
    use std::os::windows::ffi::OsStrExt;
    use std::ptr::null_mut;

    #[link(name = "user32")]
    extern "system" {
        fn MessageBoxW(
            hwnd: *mut std::ffi::c_void,
            text: *const u16,
            caption: *const u16,
            kind: u32,
        ) -> i32;
    }

    let text: Vec<u16> = std::ffi::OsStr::new(message)
        .encode_wide()
        .chain(std::iter::once(0))
        .collect();
    let caption: Vec<u16> = std::ffi::OsStr::new(PRODUCT_NAME)
        .encode_wide()
        .chain(std::iter::once(0))
        .collect();
    unsafe {
        MessageBoxW(null_mut(), text.as_ptr(), caption.as_ptr(), 0x10);
    }
}

#[cfg(not(windows))]
fn show_startup_error(message: &str) {
    eprintln!("{message}");
}

fn run() -> Result<(), String> {
    let child = start_or_reuse_backend()?;
    let app = tauri::Builder::default()
        .manage(BackendProcess(Mutex::new(child)))
        .build(tauri::generate_context!())
        .map_err(|error| format!("桌面窗口初始化失败：{error}"))?;

    app.run(|handle, event| {
        if matches!(event, tauri::RunEvent::Exit) {
            if let Some(state) = handle.try_state::<BackendProcess>() {
                if let Ok(mut process) = state.0.lock() {
                    if let Some(mut child) = process.take() {
                        let _ = child.kill();
                        let _ = child.wait();
                    }
                }
            }
        }
    });
    Ok(())
}

fn main() {
    if let Err(error) = run() {
        show_startup_error(&error);
    }
}
