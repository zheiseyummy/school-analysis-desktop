#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use std::fs::{self, OpenOptions};
use std::io::{Read, Write};
use std::net::{SocketAddr, TcpStream};
use std::path::{Path, PathBuf};
use std::process::{Child, Command, Stdio};
use std::sync::Mutex;
use std::thread;
use std::time::{Duration, Instant, SystemTime, UNIX_EPOCH};
use tauri::Manager;

const BACKEND_PORT: u16 = 8989;
const PRODUCT_NAME: &str = "成绩分析系统";
const APP_TITLE: &[u8] = "<title>成绩分析系统</title>".as_bytes();

#[derive(Debug, PartialEq, Eq)]
enum BackendProbe {
    Ready,
    Free,
    Busy,
}

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

fn log_launcher(message: &str) {
    let log_dir = app_data_dir().join("logs");
    if fs::create_dir_all(&log_dir).is_err() {
        return;
    }
    if let Ok(mut file) = OpenOptions::new()
        .create(true)
        .append(true)
        .open(log_dir.join("launcher.log"))
    {
        let seconds = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .map(|duration| duration.as_secs())
            .unwrap_or_default();
        let _ = writeln!(file, "[{seconds}] {message}");
    }
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

fn transient_socket_error(error: &std::io::Error) -> bool {
    matches!(
        error.kind(),
        std::io::ErrorKind::TimedOut
            | std::io::ErrorKind::WouldBlock
            | std::io::ErrorKind::Interrupted
            | std::io::ErrorKind::ConnectionReset
            | std::io::ErrorKind::ConnectionAborted
            | std::io::ErrorKind::BrokenPipe
    ) || error.raw_os_error() == Some(10060)
}

fn probe_local_backend(port: u16) -> Result<BackendProbe, String> {
    let address = SocketAddr::from(([127, 0, 0, 1], port));
    let mut stream = match TcpStream::connect_timeout(&address, Duration::from_millis(250)) {
        Ok(stream) => stream,
        // Windows 上空闲的回环端口也可能返回 10060；连接未建立时先尝试启动本地服务。
        Err(_) => return Ok(BackendProbe::Free),
    };
    stream
        .set_write_timeout(Some(Duration::from_secs(1)))
        .map_err(|error| format!("设置本地服务连接超时失败：{error}"))?;
    if let Err(error) = stream
        .write_all(b"GET / HTTP/1.1\r\nHost: 127.0.0.1\r\nConnection: close\r\n\r\n")
    {
        return if transient_socket_error(&error) {
            Ok(BackendProbe::Busy)
        } else {
            Err(format!("请求本地服务时连接失败：{error}"))
        };
    }

    // 只读取到页面标题即可，不等待 HTTP keep-alive 连接关闭。
    let deadline = Instant::now() + Duration::from_secs(3);
    let mut response = Vec::new();
    let mut chunk = [0u8; 4096];
    loop {
        let remaining = deadline.saturating_duration_since(Instant::now());
        if remaining.is_zero() {
            return Ok(BackendProbe::Busy);
        }
        stream
            .set_read_timeout(Some(remaining.min(Duration::from_secs(1))))
            .map_err(|error| format!("设置本地服务读取超时失败：{error}"))?;
        match stream.read(&mut chunk) {
            Ok(0) => break,
            Ok(count) => {
                response.extend_from_slice(&chunk[..count]);
                if response.windows(APP_TITLE.len()).any(|part| part == APP_TITLE) {
                    return Ok(BackendProbe::Ready);
                }
                if response.len() >= 64 * 1024 {
                    break;
                }
            }
            Err(error) if transient_socket_error(&error) => return Ok(BackendProbe::Busy),
            Err(error) => return Err(format!("读取本地服务响应失败：{error}")),
        }
    }
    if response.is_empty() {
        Ok(BackendProbe::Busy)
    } else {
        Err(format!(
            "本机端口 {port} 已被其他程序占用。请关闭占用该端口的程序后重试。"
        ))
    }
}

fn start_or_reuse_backend() -> Result<Option<Child>, String> {
    let existing_started = Instant::now();
    let mut logged_busy_port = false;
    loop {
        match probe_local_backend(BACKEND_PORT)? {
            BackendProbe::Ready => {
                log_launcher("复用已运行的本地服务");
                return Ok(None);
            }
            BackendProbe::Free => {
                log_launcher("本地端口未连接，准备启动随包后端");
                break;
            }
            BackendProbe::Busy => {
                if !logged_busy_port {
                    log_launcher("本地端口可连接但尚未返回应用页面，等待已有服务响应");
                    logged_busy_port = true;
                }
                if existing_started.elapsed() >= Duration::from_secs(30) {
                    return Err(format!(
                        "本机 {BACKEND_PORT} 端口已有服务，但 30 秒内没有响应。请关闭旧版成绩分析系统或占用该端口的程序后重试。"
                    ));
                }
                thread::sleep(Duration::from_millis(500));
            }
        }
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
    log_launcher(&format!("已启动随包后端，进程 ID {}", child.id()));
    let started = Instant::now();
    while started.elapsed() < Duration::from_secs(90) {
        match probe_local_backend(BACKEND_PORT) {
            Ok(BackendProbe::Ready) => {
                log_launcher("本地服务已就绪");
                return Ok(Some(child));
            }
            Ok(BackendProbe::Free | BackendProbe::Busy) => {}
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
    log_launcher("桌面启动器开始运行");
    let child = start_or_reuse_backend()?;
    let app = tauri::Builder::default()
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_fs::init())
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
        log_launcher(&format!("启动失败：{error}"));
        let log_path = app_data_dir().join("logs").join("launcher.log");
        show_startup_error(&format!("{error}\n\n启动记录：{}", log_path.display()));
    }
}

#[cfg(test)]
mod tests {
    use super::{probe_local_backend, transient_socket_error, BackendProbe};
    use std::io::{Read, Write};
    use std::net::TcpListener;
    use std::thread;
    use std::time::Duration;

    #[test]
    fn windows_connection_timeout_is_retryable() {
        assert!(transient_socket_error(&std::io::Error::from_raw_os_error(10060)));
    }

    #[test]
    fn closed_port_is_available_for_backend_startup() {
        let listener = TcpListener::bind("127.0.0.1:0").unwrap();
        let port = listener.local_addr().unwrap().port();
        drop(listener);
        assert_eq!(probe_local_backend(port).unwrap(), BackendProbe::Free);
    }

    #[test]
    fn unresponsive_listener_is_retryable() {
        let listener = TcpListener::bind("127.0.0.1:0").unwrap();
        let port = listener.local_addr().unwrap().port();
        let server = thread::spawn(move || {
            let (mut connection, _) = listener.accept().unwrap();
            let mut request = [0u8; 256];
            connection.read(&mut request).unwrap();
            thread::sleep(Duration::from_millis(1200));
        });
        assert_eq!(probe_local_backend(port).unwrap(), BackendProbe::Busy);
        server.join().unwrap();
    }

    #[test]
    fn page_title_is_enough_without_waiting_for_connection_close() {
        let listener = TcpListener::bind("127.0.0.1:0").unwrap();
        let port = listener.local_addr().unwrap().port();
        let server = thread::spawn(move || {
            let (mut connection, _) = listener.accept().unwrap();
            let mut request = [0u8; 256];
            connection.read(&mut request).unwrap();
            connection
                .write_all("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<title>成绩分析系统</title>".as_bytes())
                .unwrap();
            thread::sleep(Duration::from_millis(1200));
        });
        assert_eq!(probe_local_backend(port).unwrap(), BackendProbe::Ready);
        server.join().unwrap();
    }
}
