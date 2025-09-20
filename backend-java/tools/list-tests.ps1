Get-ChildItem -Recurse src/test/java -Filter *.java |
  ForEach-Object {
    $p = $_.FullName
    $cls = (Get-Content $p | Select-String -Pattern 'class\s+([A-Za-z0-9_]+)').Matches.Value
    [PSCustomObject]@{ Path = $p; Class = $cls }
  } | Sort-Object Path | Format-Table -Auto
