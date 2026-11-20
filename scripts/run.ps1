param(
    [ValidateSet('web', 'api', 'all')][string]$Suite = 'all',
    [ValidatePattern('^[a-zA-Z0-9_-]+$')][string]$Label = 'run',
    [string[]]$MavenArguments = @()
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
try {
    $started = Get-Date
    $destination = Join-Path $projectRoot ('evidence/runs/' + $started.ToString('yyyyMMdd-HHmmss') + '-' + $Suite + '-' + $Label)
    New-Item -ItemType Directory -Path $destination -Force | Out-Null
    if ($env:GITHUB_OUTPUT) { "evidence_path=$destination" | Out-File -FilePath $env:GITHUB_OUTPUT -Encoding utf8 -Append }
    $arguments = @('-B', '-ntp', '-fae')
    if ($Suite -ne 'all') { $arguments += @('-pl', ($Suite + '-tests')) }
    $arguments += @('clean', 'verify') + $MavenArguments
    & "$projectRoot/mvnw.cmd" @arguments 2>&1 | Tee-Object -FilePath "$destination/console.log"
    $result = $LASTEXITCODE
    $modules = if ($Suite -eq 'all') { @('web', 'api') } else { @($Suite) }
    foreach ($module in $modules) {
        $target = "$projectRoot/$module-tests/target"
        foreach ($folder in @('failsafe-reports', 'surefire-reports', 'site/serenity', 'karate-reports', 'cleanup-verification')) {
            $source = "$target/$folder"
            if ((Test-Path $source) -and (Get-ChildItem $source -File -Recurse | Where-Object LastWriteTime -ge $started | Select-Object -First 1)) {
                $output = New-Item -ItemType Directory -Path "$destination/$module" -Force
                Copy-Item $source $output.FullName -Recurse
            }
        }
    }
    [ordered]@{ started = $started.ToString('o'); finished = (Get-Date).ToString('o'); command = 'mvnw.cmd ' + ($arguments -join ' '); exitCode = $result } |
        ConvertTo-Json | Set-Content "$destination/execution.json" -Encoding utf8
    Write-Output "Evidencias: $destination"
} finally { Pop-Location }
exit $result
