$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$out = Join-Path $root "out\test"

if (Test-Path $out) {
    Remove-Item -LiteralPath $out -Recurse -Force
}

New-Item -ItemType Directory -Path $out | Out-Null

$mainSources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter "*.java" |
    ForEach-Object { $_.FullName }
$testSources = Get-ChildItem -Path (Join-Path $root "src\test\java") -Recurse -Filter "*.java" |
    ForEach-Object { $_.FullName }

javac -d $out $mainSources $testSources
java -ea -cp $out com.example.subscription.SubscriptionCalculatorTest

