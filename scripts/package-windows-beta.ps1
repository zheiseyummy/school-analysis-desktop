[CmdletBinding()]
param(
    [switch]$ReplaceExisting
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$frontendRoot = Join-Path $repoRoot 'frontend'
$backendRoot = Join-Path $repoRoot 'backend'
$backendTarget = Join-Path $backendRoot 'target'
$outputsRoot = Join-Path $repoRoot 'outputs'
$releaseName = '成绩分析系统_1.1(beta)_Windows_x64'
$releaseRoot = Join-Path $outputsRoot $releaseName
$archivePath = "$releaseRoot.zip"
$stagingRoot = Join-Path $outputsRoot ('.staging-' + [Guid]::NewGuid().ToString('N'))

if (-not $IsWindows -and $env:OS -ne 'Windows_NT') {
    throw 'This package script must run on Windows to create the Tauri desktop build.'
}
if ((Test-Path -LiteralPath $releaseRoot) -or (Test-Path -LiteralPath $archivePath)) {
    if (-not $ReplaceExisting) {
        throw "Release output already exists. Rebuild with -ReplaceExisting to preserve the current output as a timestamped backup."
    }
    $previousRoot = Join-Path $outputsRoot ('.previous-' + (Get-Date -Format 'yyyyMMdd-HHmmss'))
    New-Item -ItemType Directory -Path $previousRoot | Out-Null
    if (Test-Path -LiteralPath $releaseRoot) { Move-Item -LiteralPath $releaseRoot -Destination (Join-Path $previousRoot $releaseName) }
    if (Test-Path -LiteralPath $archivePath) { Move-Item -LiteralPath $archivePath -Destination (Join-Path $previousRoot (Split-Path -Leaf $archivePath)) }
}

$cargoBin = Join-Path $env:USERPROFILE '.cargo\bin'
if (Test-Path -LiteralPath $cargoBin) { $env:PATH = "$cargoBin$([IO.Path]::PathSeparator)$env:PATH" }
$rustc = Get-Command rustc -ErrorAction SilentlyContinue
$cargo = Get-Command cargo -ErrorAction SilentlyContinue
if (-not $rustc -or -not $cargo) {
    throw 'Rust/Cargo are required. Install the stable x86_64-pc-windows-msvc toolchain first.'
}

$vswhere = 'C:\Program Files (x86)\Microsoft Visual Studio\Installer\vswhere.exe'
if (-not (Test-Path -LiteralPath $vswhere)) { throw 'Visual Studio Build Tools with the C++ workload are required.' }
$vsRoot = & $vswhere -latest -products '*' -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath
if (-not $vsRoot) { throw 'Visual Studio C++ x64 build tools were not found.' }
$vsDevCmd = Join-Path $vsRoot 'Common7\Tools\VsDevCmd.bat'
if (-not (Test-Path -LiteralPath $vsDevCmd)) { throw "Visual Studio developer environment script not found: $vsDevCmd" }

# Cargo needs the MSVC linker and Windows SDK paths in the current process.
$devEnvironment = & cmd.exe /d /s /c "`"$vsDevCmd`" -no_logo -arch=x64 -host_arch=x64 >nul && set"
if ($LASTEXITCODE -ne 0) { throw 'Unable to load the Visual Studio C++ build environment.' }
foreach ($line in $devEnvironment) {
    if ($line -match '^([^=]+)=(.*)$') { [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process') }
}

$jdk = Get-ChildItem -LiteralPath (Join-Path $repoRoot '.tools') -Directory -ErrorAction SilentlyContinue |
    Where-Object { Test-Path (Join-Path $_.FullName 'bin\jlink.exe') } |
    Sort-Object Name -Descending | Select-Object -First 1
if (-not $jdk) { throw 'A local JDK 17 with jlink is required to package the backend runtime.' }
$jlink = Join-Path $jdk.FullName 'bin\jlink.exe'

New-Item -ItemType Directory -Path $outputsRoot, $stagingRoot -Force | Out-Null
try {
    Write-Host 'Building the local web backend and its tests...'
    Push-Location -LiteralPath $frontendRoot
    try {
        & pnpm build:desktop
        if ($LASTEXITCODE -ne 0) { throw "Frontend desktop build failed with exit code $LASTEXITCODE" }
    } finally { Pop-Location }

    $staticPath = Join-Path $backendTarget 'classes\static'
    if (Test-Path -LiteralPath $staticPath) { Remove-Item -LiteralPath $staticPath -Recurse -Force }
    New-Item -ItemType Directory -Path $staticPath | Out-Null
    Copy-Item -Path (Join-Path $frontendRoot 'dist\*') -Destination $staticPath -Recurse -Force

    & pwsh -NoProfile -File (Join-Path $repoRoot 'scripts\build-backend.ps1') -MavenArguments 'package'
    if ($LASTEXITCODE -ne 0) { throw "Backend package/tests failed with exit code $LASTEXITCODE" }

    Write-Host 'Building the Tauri desktop window...'
    Push-Location -LiteralPath $frontendRoot
    try {
        & pnpm tauri build --no-bundle
        if ($LASTEXITCODE -ne 0) { throw "Tauri build failed with exit code $LASTEXITCODE" }
    } finally { Pop-Location }

    $tauriExe = Join-Path $frontendRoot 'src-tauri\target\release\score-analysis-system.exe'
    if (-not (Test-Path -LiteralPath $tauriExe)) { throw "Tauri executable not found: $tauriExe" }
    $jarPath = Join-Path $backendTarget 'school-analysis-desktop-backend.jar'
    if (-not (Test-Path -LiteralPath $jarPath)) { throw "Backend JAR not found: $jarPath" }

    $runtimePath = Join-Path $stagingRoot 'runtime'
    Write-Host 'Creating the portable Java backend runtime...'
    & $jlink --add-modules 'java.se,jdk.crypto.ec,jdk.unsupported,jdk.charsets,jdk.localedata' --strip-debug --no-man-pages --no-header-files --compress=2 --output $runtimePath
    if ($LASTEXITCODE -ne 0) { throw "jlink failed with exit code $LASTEXITCODE" }

    New-Item -ItemType Directory -Path $releaseRoot | Out-Null
    Copy-Item -LiteralPath $tauriExe -Destination (Join-Path $releaseRoot '成绩分析系统.exe')
    Copy-Item -LiteralPath $jarPath -Destination (Join-Path $releaseRoot 'backend.jar')
    Move-Item -LiteralPath $runtimePath -Destination (Join-Path $releaseRoot 'runtime')

    $guide = @'
# 成绩分析系统 1.1 beta 桌面版

## 启动

1. 将 ZIP 完整解压到本地可写目录。
2. 双击 `成绩分析系统.exe`，系统将在独立桌面窗口中打开，不会启动外部浏览器。
3. 关闭应用窗口即可退出桌面程序并停止它启动的本地服务。

本应用使用 Windows WebView2 呈现界面。Windows 10/11 通常已预装 WebView2 Runtime；如果提示缺少运行时，请安装 Microsoft Edge WebView2 Evergreen Runtime 后重试。

## 数据位置

数据库、上传文件、备份、导出和日志保存在当前 Windows 用户目录：

`%LOCALAPPDATA%\成绩分析系统`

升级程序前，请先通过系统的数据备份功能创建备份。升级不会删除上述数据目录。

## 版本说明

这是 1.1 beta 第一代 Tauri 窗口版试用包。系统只在本机运行，无需部署服务器、安装 Node.js、Java 或 Maven。正式成绩样表和学校规则仍需后续实际核对。
'@
    Set-Content -LiteralPath (Join-Path $releaseRoot '使用说明.md') -Value $guide -Encoding UTF8

    Write-Host 'Creating the portable ZIP...'
    Compress-Archive -Path (Join-Path $releaseRoot '*') -DestinationPath $archivePath -CompressionLevel Optimal

    $archive = Get-Item -LiteralPath $archivePath
    $imageExe = Get-Item -LiteralPath (Join-Path $releaseRoot '成绩分析系统.exe')
    Write-Host "Release folder: $releaseRoot"
    Write-Host "Release archive: $($archive.FullName) ($([Math]::Round($archive.Length / 1MB, 1)) MB)"
    Write-Host "App launcher: $($imageExe.FullName)"
} catch {
    if (Test-Path -LiteralPath $releaseRoot) { Remove-Item -LiteralPath $releaseRoot -Recurse -Force }
    if (Test-Path -LiteralPath $archivePath) { Remove-Item -LiteralPath $archivePath -Force }
    throw
} finally {
    if (Test-Path -LiteralPath $stagingRoot) { Remove-Item -LiteralPath $stagingRoot -Recurse -Force }
}
