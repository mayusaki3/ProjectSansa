chcp.com 65001
[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding($false)
$OutputEncoding = [Console]::OutputEncoding

# mvn -q -DskipTests clean package
# mvn -q test
# mvn -q verify
