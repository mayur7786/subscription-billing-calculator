$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$out = Join-Path $root "out\main"
$port = if ($args.Count -gt 0) { $args[0] } else { "8080" }

if (Test-Path $out) {
    Remove-Item -LiteralPath $out -Recurse -Force
}

New-Item -ItemType Directory -Path $out | Out-Null

$sources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter "*.java" |
    ForEach-Object { $_.FullName }

javac -d $out $sources

$resources = Join-Path $root "src\main\resources"
if (Test-Path $resources) {
    Copy-Item -Path (Join-Path $resources "*") -Destination $out -Recurse -Force
}

java -cp $out com.example.subscription.SubscriptionCalculatorApplication $port
