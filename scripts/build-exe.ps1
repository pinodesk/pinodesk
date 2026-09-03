# scripts\build-exe.ps1

$ErrorActionPreference = "Stop"

Write-Host "Mode: $env:RELEASE_MODE"

# === Mode: snapshot atau release ===
if ($env:RELEASE_MODE -eq "release") {
    $configFile = $env:APPLICATION_PROPERTIES_RELEASE_EXE
    $apiUrl = "https://api.pinodesk.com/v1/releases"
    $targetPath = "releases/windows/"
    $apiKey = $env:PINODESK_API_KEY_PRODUCTION
} else {
    $configFile = $env:APPLICATION_PROPERTIES_SNAPSHOT_EXE
    $apiUrl = "https://api-staging.pinodesk.com/v1/releases"
    $targetPath = "snapshots/windows/"
    $apiKey = $env:PINODESK_API_KEY_STAGING
}

# === Pastikan Maven tersedia ===
$mvn = "$env:USERPROFILE\.sdkman\candidates\maven\current\bin\mvn.cmd"
if (-Not (Test-Path $mvn)) {
    Write-Error "mvn.cmd not found at $mvn"
    exit 1
}
Write-Host "Using Maven from: $mvn"

# === Ambil versi dari pom.xml ===
Write-Host "Getting version from pom.xml..."
$projectVersion = & $mvn "help:evaluate" "-Dexpression=project.version" "-q" "-DforceStdout"
if (-not $projectVersion) {
    Write-Error "Gagal mendapatkan versi project dari Maven"
    exit 1
}
Write-Host "Version: $projectVersion"

# === Salin config dan logback ===
Write-Host "Copying application.properties..."
Copy-Item $configFile -Destination "src\main\resources\application.properties" -Force
Copy-Item "src\main\resources\logback.xml.example" -Destination "src\main\resources\logback.xml" -Force
Remove-Item "src\main\resources\*.example" -Force

# === Jalankan build Maven ===
Write-Host "Building project..."
& $mvn clean package -P exe -DskipTests

# === Temukan file EXE ===
Set-Location target
$exeFile = Get-ChildItem -Filter *.exe | Select-Object -First 1
if (-not $exeFile) {
    Write-Error "File EXE tidak ditemukan setelah build"
    exit 1
}
Write-Host "EXE File: $($exeFile.Name)"

# === Upload ke FTP via WinSCP ===
Write-Host "Uploading to FTP via WinSCP..."

$winscpPath = "C:\Program Files (x86)\WinSCP\WinSCP.com"
$ftpUser = $env:FTP_USERNAME
$ftpPassRaw = $env:FTP_PASSWORD
$ftpHost = $env:FTP_HOST
$remotePath = "domains/download.pinodesk.com/public_html/$targetPath"

# URL encode password
Add-Type -AssemblyName System.Web
$ftpPass = [System.Web.HttpUtility]::UrlEncode($ftpPassRaw)

# Lokasi sementara untuk script WinSCP
$tempScriptPath = Join-Path $env:TEMP "winscp-upload.txt"

# Buat script WinSCP
$ftpScript = @"
open ftp://${ftpUser}:${ftpPass}@${ftpHost}
lcd $(Get-Location)
cd $remotePath
put "$($exeFile.Name)"
exit
"@

# Simpan script ke file sementara
Set-Content -Path $tempScriptPath -Value $ftpScript -Encoding ASCII

# Jalankan WinSCP dengan script
& $winscpPath "/script=$tempScriptPath"

# Bersihkan file script jika mau
Remove-Item $tempScriptPath -Force

# === Kirim info ke API ===
Write-Host "Calling release API..."
$downloadUrl = "https://download.pinodesk.com/$targetPath$($exeFile.Name)"
$body = @{
    name = "Pinodesk for Windows v$projectVersion"
    platform = "windows"
    version = "$projectVersion"
    download_url = $downloadUrl
} | ConvertTo-Json -Compress

try {
    $response = Invoke-RestMethod -Uri $apiUrl `
        -Headers @{ "Content-Type" = "application/json"; "X-Pinodesk-Api-Key" = "$apiKey" } `
        -Method POST `
        -Body $body
    Write-Host "Release API call successful."
    Write-Host "Response: $response" 
} catch {
    $errorMsg = $_.Exception.Message
    $responseBody = $null
    if ($_.ErrorDetails -and $_.ErrorDetails.Message) {
        $responseBody = $_.ErrorDetails.Message
    }
    Write-Warning "Release API call failed, but continuing anyway."
    Write-Warning "Error: $errorMsg"
    Write-Warning "Response body: $responseBody"
}

Write-Host "Done!"
