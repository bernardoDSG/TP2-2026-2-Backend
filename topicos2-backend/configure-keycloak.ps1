param(
    [Parameter(Mandatory = $true)]
    [string]$AdminEmail
)

$ErrorActionPreference = 'Stop'
$AdminEmail = $AdminEmail.Trim().ToLowerInvariant()

function Convert-JsonOutput([string[]]$lines) {
    $jsonText = $lines -join [Environment]::NewLine
    return ConvertFrom-Json -InputObject $jsonText
}

function Convert-SecureStringToPlainText([System.Security.SecureString]$secureString) {
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureString)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
}

$keycloakHome = $env:KEYCLOAK_HOME
if ([string]::IsNullOrWhiteSpace($keycloakHome)) {
    throw 'Defina KEYCLOAK_HOME com a pasta da distribuicao local do Keycloak.'
}

$kcadm = Join-Path $keycloakHome 'bin\kcadm.bat'
if (-not (Test-Path -LiteralPath $kcadm)) {
    throw "Ferramenta de administracao do Keycloak nao encontrada: $kcadm"
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

$secretFile = Join-Path $env:LOCALAPPDATA 'ecommerce-keycloak\client-secret'
if (-not (Test-Path -LiteralPath $secretFile)) {
    throw 'Segredo local nao encontrado. Inicie o Keycloak com start-keycloak.ps1 primeiro.'
}
$clientSecret = [System.IO.File]::ReadAllText($secretFile).Trim()

& $kcadm config credentials --server 'http://localhost:8180' --realm master `
    --user $env:KC_BOOTSTRAP_ADMIN_USERNAME --password $env:KC_BOOTSTRAP_ADMIN_PASSWORD
if ($LASTEXITCODE -ne 0) {
    throw 'Nao foi possivel autenticar a ferramenta administrativa no Keycloak.'
}

$clientResults = & $kcadm get clients -r ecommerce -q 'clientId=ecommerce-backend'
if ($LASTEXITCODE -ne 0) {
    throw 'Nao foi possivel localizar o client ecommerce-backend.'
}
$client = @(Convert-JsonOutput $clientResults)[0]
if (-not $client.id) {
    throw 'Client ecommerce-backend nao encontrado no realm ecommerce.'
}
$client.secret = $clientSecret
$client.directAccessGrantsEnabled = $true
$client.standardFlowEnabled = $false
$clientFile = Join-Path $env:LOCALAPPDATA 'ecommerce-keycloak\client.json'
$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($clientFile, ($client | ConvertTo-Json -Depth 100), $utf8WithoutBom)
try {
    & $kcadm update "clients/$($client.id)" -r ecommerce -f $clientFile
    if ($LASTEXITCODE -ne 0) {
        throw 'Nao foi possivel atualizar o client ecommerce-backend.'
    }
} finally {
    Remove-Item -LiteralPath $clientFile -Force -ErrorAction SilentlyContinue
}

$serviceAccount = 'service-account-ecommerce-backend'
$managementClientResults = & $kcadm get clients -r ecommerce -q 'clientId=realm-management'
if ($LASTEXITCODE -ne 0) {
    throw 'Nao foi possivel localizar o client realm-management.'
}
$managementClient = @(Convert-JsonOutput $managementClientResults)[0]
$serviceAccountResults = & $kcadm get users -r ecommerce -q "username=$serviceAccount"
if ($LASTEXITCODE -ne 0) {
    throw 'Nao foi possivel localizar a conta de servico do backend.'
}
$serviceAccountUser = @(Convert-JsonOutput $serviceAccountResults)[0]
if (-not $managementClient.id -or -not $serviceAccountUser.id) {
    throw 'A conta de servico ou o client realm-management nao foi encontrado.'
}

$serviceRoles = & $kcadm get "users/$($serviceAccountUser.id)/role-mappings/clients/$($managementClient.id)" -r ecommerce
if ($LASTEXITCODE -ne 0) {
    throw 'Nao foi possivel consultar as permissoes da conta de servico.'
}
$hasManageUsers = @(Convert-JsonOutput $serviceRoles | ForEach-Object { $_.name }) -contains 'manage-users'
if (-not $hasManageUsers) {
    & $kcadm add-roles -r ecommerce --uusername $serviceAccount `
        --cclientid realm-management --rolename manage-users
    if ($LASTEXITCODE -ne 0) {
        throw 'Nao foi possivel conceder manage-users a conta de servico.'
    }
}

$adminResults = & $kcadm get users -r ecommerce -q "username=$AdminEmail"
if ($LASTEXITCODE -ne 0) {
    throw "Nao foi possivel localizar o usuario $AdminEmail no realm ecommerce."
}
$admin = @(Convert-JsonOutput $adminResults | Where-Object { $_.username -eq $AdminEmail })[0]
if (-not $admin.id) {
    $secureAdminPassword = Read-Host "Crie a senha da conta ADMIN $AdminEmail" -AsSecureString
    $adminPassword = Convert-SecureStringToPlainText $secureAdminPassword
    $adminFile = Join-Path $env:LOCALAPPDATA 'ecommerce-keycloak\admin-user.json'
    $adminDefinition = @{
        username = $AdminEmail
        email = $AdminEmail
        enabled = $true
        emailVerified = $true
        firstName = 'Administrador'
        credentials = @(@{
            type = 'password'
            value = $adminPassword
            temporary = $false
        })
    }
    $adminJson = $adminDefinition | ConvertTo-Json -Depth 10
    $adminPassword = $null
    $adminDefinition = $null
    [System.IO.File]::WriteAllText($adminFile, $adminJson, $utf8WithoutBom)
    $adminJson = $null
    try {
        & $kcadm create users -r ecommerce -f $adminFile
        if ($LASTEXITCODE -ne 0) {
            throw "Nao foi possivel criar a conta ADMIN $AdminEmail."
        }
    } finally {
        Remove-Item -LiteralPath $adminFile -Force -ErrorAction SilentlyContinue
    }
    $adminResults = & $kcadm get users -r ecommerce -q "username=$AdminEmail"
    if ($LASTEXITCODE -ne 0) {
        throw "Nao foi possivel consultar a conta ADMIN $AdminEmail."
    }
    $admin = @(Convert-JsonOutput $adminResults | Where-Object { $_.username -eq $AdminEmail })[0]
}
$adminRoles = & $kcadm get "users/$($admin.id)/role-mappings/realm" -r ecommerce
if ($LASTEXITCODE -ne 0) {
    throw "Nao foi possivel consultar os papeis de $AdminEmail."
}
$hasAdminRole = @(Convert-JsonOutput $adminRoles | ForEach-Object { $_.name }) -contains 'ADMIN'
if (-not $hasAdminRole) {
    & $kcadm add-roles -r ecommerce --uusername $AdminEmail --rolename ADMIN
    if ($LASTEXITCODE -ne 0) {
        throw "Nao foi possivel atribuir o papel ADMIN a $AdminEmail."
    }
}

Write-Host "Client e papeis configurados. $AdminEmail possui ADMIN."
