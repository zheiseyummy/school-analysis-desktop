[CmdletBinding()]
param(
    [string[]]$MavenArguments = @('verify'),
    [switch]$Offline
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$toolsRoot = Join-Path $projectRoot '.tools'
$backendRoot = Join-Path $projectRoot 'backend'
$previousJavaHome = $env:JAVA_HOME
$previousPath = $env:PATH

function Find-LocalTool([string]$RelativeExecutable) {
    if (-not (Test-Path -LiteralPath $toolsRoot -PathType Container)) {
        return $null
    }

    foreach ($directory in (Get-ChildItem -LiteralPath $toolsRoot -Directory | Sort-Object Name -Descending)) {
        $candidate = Join-Path $directory.FullName $RelativeExecutable
        if (Test-Path -LiteralPath $candidate -PathType Leaf) {
            return $candidate
        }
    }
    return $null
}

$javaPath = Find-LocalTool 'bin\java.exe'
if (-not $javaPath -and $env:JAVA_HOME) {
    $candidate = Join-Path $env:JAVA_HOME 'bin\java.exe'
    if (Test-Path -LiteralPath $candidate -PathType Leaf) { $javaPath = $candidate }
}
if (-not $javaPath) {
    $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($javaCommand) { $javaPath = $javaCommand.Source }
}

$mavenPath = Find-LocalTool 'bin\mvn.cmd'
if (-not $mavenPath) {
    $mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($mavenCommand) { $mavenPath = $mavenCommand.Source }
}

if (-not $javaPath -or -not $mavenPath) {
    throw 'Java 17 and Maven are required. Extract their official ZIP distributions into .tools, or configure JAVA_HOME and PATH. See scripts/README.md.'
}

$localRepository = Join-Path $toolsRoot 'maven-repository'
$buildExitCode = 1
try {
    $env:JAVA_HOME = Split-Path -Parent (Split-Path -Parent $javaPath)
    $env:PATH = (Split-Path -Parent $javaPath) + [IO.Path]::PathSeparator + $previousPath
    Write-Host ('Java: ' + $javaPath)
    Write-Host ('Maven: ' + $mavenPath)
    Write-Host ('Dependency cache: ' + $localRepository)
    $arguments = @('--batch-mode', '--no-transfer-progress', "-Dmaven.repo.local=$localRepository")
    if ($Offline) { $arguments += '--offline' }
    $arguments += $MavenArguments
    Push-Location -LiteralPath $backendRoot
    try {
        & $mavenPath @arguments
        $buildExitCode = $LASTEXITCODE
    } finally {
        Pop-Location
    }
} finally {
    $env:JAVA_HOME = $previousJavaHome
    $env:PATH = $previousPath
}

exit $buildExitCode
