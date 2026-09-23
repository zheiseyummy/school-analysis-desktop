[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$frontendRoot = Join-Path $projectRoot 'frontend'
if (-not (Get-Command pnpm.cmd -ErrorAction SilentlyContinue)) {
    throw '未找到 pnpm。请先安装 Node.js 18+ 和 pnpm，或手动进入 frontend 运行 pnpm dev。'
}
Push-Location $frontendRoot
try {
    Write-Host '前端地址：http://127.0.0.1:3000'
    Start-Process 'http://127.0.0.1:3000'
    & pnpm.cmd dev
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
