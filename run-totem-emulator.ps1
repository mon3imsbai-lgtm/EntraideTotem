$ErrorActionPreference = "Stop"

$sdk = "C:\Android\sdk"
$avdName = "EntraideTotemTablet"
$apk = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
$driver = Join-Path $sdk "extras\google\Android_Emulator_Hypervisor_Driver\silent_install.bat"
$emulator = Join-Path $sdk "emulator\emulator.exe"
$adb = Join-Path $sdk "platform-tools\adb.exe"

function Test-Admin {
    $identity = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($identity)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

if (-not (Test-Admin)) {
    Write-Host "Requesting Administrator rights to install Android Emulator Hypervisor Driver..."
    Start-Process powershell.exe -Verb RunAs -ArgumentList @(
        "-ExecutionPolicy", "Bypass",
        "-NoExit",
        "-File", "`"$PSCommandPath`""
    )
    exit
}

Write-Host "Installing Android Emulator Hypervisor Driver..."
& $driver

Write-Host "Checking emulator acceleration..."
& (Join-Path $sdk "emulator\emulator-check.exe") accel

Write-Host "Starting emulator $avdName..."
Start-Process -FilePath $emulator -ArgumentList "@$avdName", "-no-snapshot-load"

Write-Host "Waiting for emulator..."
& $adb wait-for-device

$deadline = (Get-Date).AddMinutes(5)
do {
    Start-Sleep -Seconds 3
    $bootCompleted = (& $adb shell getprop sys.boot_completed).Trim()
    $packageService = (& $adb shell service check package 2>$null)
    Write-Host "Boot state: $bootCompleted"
    if ($bootCompleted -eq "1" -and ($packageService -join " ") -match "found") {
        break
    }
} while ((Get-Date) -lt $deadline)

if ($bootCompleted -ne "1") {
    throw "Emulator did not finish booting before timeout."
}

Write-Host "Installing APK..."
& $adb install -r $apk

Write-Host "Launching Entraide Totem..."
& $adb shell monkey -p ma.entraide.totem 1
