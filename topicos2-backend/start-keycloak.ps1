$ErrorActionPreference = 'Stop'

$keycloakHome = $env:KEYCLOAK_HOME
if ([string]::IsNullOrWhiteSpace($keycloakHome)) {
    throw 'Defina KEYCLOAK_HOME com a pasta da distribuicao local do Keycloak.'
}

$keycloakCommand = Join-Path $keycloakHome 'bin\kc.bat'
$realmSource = Join-Path $PSScriptRoot 'src\main\resources\keycloak\ecommerce-realm.json'
if (-not (Test-Path -LiteralPath $keycloakCommand)) {
    throw "Keycloak nao encontrado: $keycloakCommand"
}
if (-not (Test-Path -LiteralPath $realmSource)) {
    throw "Arquivo do realm nao encontrado: $realmSource"
}

if ([string]::IsNullOrWhiteSpace($env:KC_BOOTSTRAP_ADMIN_USERNAME)) {
    $env:KC_BOOTSTRAP_ADMIN_USERNAME = Read-Host 'Usuario administrador inicial do Keycloak'
}
if ([string]::IsNullOrWhiteSpace($env:KC_BOOTSTRAP_ADMIN_PASSWORD)) {
    $securePassword = Read-Host 'Senha do administrador inicial do Keycloak' -AsSecureString
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    try {
        $env:KC_BOOTSTRAP_ADMIN_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
}

$secretDirectory = Join-Path $env:LOCALAPPDATA 'ecommerce-keycloak'
$secretFile = Join-Path $secretDirectory 'client-secret'
New-Item -ItemType Directory -Path $secretDirectory -Force | Out-Null
if (-not (Test-Path -LiteralPath $secretFile)) {
    $secretBytes = New-Object byte[] 32
    $random = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    try {
        $random.GetBytes($secretBytes)
    } finally {
        $random.Dispose()
    }
    $clientSecret = [Convert]::ToBase64String($secretBytes).TrimEnd('=').Replace('+', '-').Replace('/', '_')
    [System.IO.File]::WriteAllText($secretFile, $clientSecret, [System.Text.Encoding]::ASCII)
}

$clientSecret = [System.IO.File]::ReadAllText($secretFile).Trim()
$realmJson = [System.IO.File]::ReadAllText($realmSource)
if (-not $realmJson.Contains('__KEYCLOAK_CLIENT_SECRET__')) {
    throw 'O arquivo do realm nao possui o marcador de segredo esperado.'
}
$realmJson = $realmJson.Replace('__KEYCLOAK_CLIENT_SECRET__', $clientSecret)

$importDirectory = Join-Path $keycloakHome 'data\import'
New-Item -ItemType Directory -Path $importDirectory -Force | Out-Null
$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Join-Path $importDirectory 'ecommerce-realm.json'), $realmJson, $utf8WithoutBom)

Write-Host 'Iniciando Keycloak e importando o realm ecommerce, se ele ainda nao existir...'
& $keycloakCommand start-dev --http-port=8180 --import-realm
if ($LASTEXITCODE -ne 0) {
    throw "Keycloak terminou com codigo $LASTEXITCODE."
}
