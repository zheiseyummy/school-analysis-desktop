@echo off
setlocal
cd /d "%~dp0frontend"
where pnpm >nul 2>nul
if errorlevel 1 (
  echo 未找到 pnpm。请先安装 Node.js 18+ 和 pnpm，或在已有开发环境中运行：pnpm dev
  pause
  exit /b 1
)
start "学情分析系统-前端" cmd /c "pnpm dev"
timeout /t 3 /nobreak >nul
start "" "http://127.0.0.1:3000"
echo 前端已启动：http://127.0.0.1:3000
echo 关闭弹出的“学情分析系统-前端”窗口即可停止。
endlocal
