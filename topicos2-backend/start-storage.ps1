$ErrorActionPreference = 'Stop'

$version = '4.54'
$installDirectory = Join-Path $env:LOCALAPPDATA "SeaweedFS\$version"
$dataDirectory = Join-Path $env:LOCALAPPDATA 'SeaweedFS\data'
$weedExecutable = Join-Path $installDirectory 'weed.exe'
$archivePath = Join-Path $env:TEMP "seaweedfs-windows-amd64-$version.zip"
$downloadUrl = "https://github.com/seaweedfs/seaweedfs/releases/download/$version/windows_amd64.zip"
$expectedSha256 = '6d02c72bda207320b69236d34280d827bbedf6cef79d6c37da5b0db2299f28bc'

if (-not (Test-Path -LiteralPath $weedExecutable)) {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    New-Item -ItemType Directory -Path $installDirectory -Force | Out-Null
    Write-Host "Baixando SeaweedFS $version do repositório oficial..."
    Invoke-WebRequest -Uri $downloadUrl -OutFile $archivePath -UseBasicParsing

    $actualSha256 = (Get-FileHash -LiteralPath $archivePath -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actualSha256 -ne $expectedSha256) {
        Remove-Item -LiteralPath $archivePath -Force
        throw 'O SHA-256 do download do SeaweedFS não corresponde ao valor oficial.'
    }

    Expand-Archive -LiteralPath $archivePath -DestinationPath $installDirectory -Force
    Remove-Item -LiteralPath $archivePath -Force
}

if (-not (Test-Path -LiteralPath $weedExecutable)) {
    throw "O arquivo weed.exe não foi encontrado em $installDirectory."
}

New-Item -ItemType Directory -Path $dataDirectory -Force | Out-Null
$env:AWS_ACCESS_KEY_ID = 'admin'
$env:AWS_SECRET_ACCESS_KEY = 'secret'

Write-Host 'SeaweedFS S3: http://localhost:8333'
Write-Host "Dados persistidos em: $dataDirectory"
Write-Host 'Mantenha este terminal aberto; use Ctrl+C para encerrar.'

& $weedExecutable mini "-dir=$dataDirectory" '-s3.port=8333' '-bucket=car-images'
if ($LASTEXITCODE -ne 0) {
    throw "SeaweedFS encerrou com código $LASTEXITCODE."
}
