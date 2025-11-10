# QuantIQ - Export Project for AI Agents
# This script creates a clean package of your project

Write-Host "🚀 QuantIQ Project Exporter" -ForegroundColor Cyan
Write-Host "=========================" -ForegroundColor Cyan
Write-Host ""

# Get project directory
$projectDir = $PSScriptRoot
$exportDir = Join-Path $projectDir "QuantIQ-Export"
$zipFile = Join-Path (Split-Path $projectDir) "QuantIQ-Complete.zip"

# Create export directory
Write-Host "📁 Creating export directory..." -ForegroundColor Yellow
if (Test-Path $exportDir) {
    Remove-Item $exportDir -Recurse -Force
}
New-Item -ItemType Directory -Path $exportDir | Out-Null

# Copy important files
Write-Host "📋 Copying source files..." -ForegroundColor Yellow

$filesToCopy = @(
    "app/src/main/java",
    "app/src/main/res",
    "app/src/main/AndroidManifest.xml",
    "app/build.gradle.kts",
    "build.gradle.kts",
    "settings.gradle.kts",
    "gradle.properties",
    "README.md",
    "BUGS_FIXED.md",
    "QUICK_START.md",
    "COMPLETE_PROJECT_EXPORT.md"
)

foreach ($file in $filesToCopy) {
    $source = Join-Path $projectDir $file
    if (Test-Path $source) {
        $dest = Join-Path $exportDir $file
        $destDir = Split-Path $dest -Parent
        if (!(Test-Path $destDir)) {
            New-Item -ItemType Directory -Path $destDir -Force | Out-Null
        }
        Copy-Item $source $dest -Recurse -Force
        Write-Host "  ✓ Copied: $file" -ForegroundColor Green
    }
}

# Create a summary file
Write-Host "📝 Creating project summary..." -ForegroundColor Yellow

$summary = @"
# QuantIQ - Project Export Summary

## What's Included
- All Kotlin source files (`.kt`)
- All XML layout files (`.xml`)
- Build configuration files
- Documentation files

## What's Excluded
- Build outputs (`.apk`, `.dex`, etc.)
- Gradle cache
- IDE settings (`.idea`)
- AAR library files (too large)

## To Share with AI Agents

### Method 1: GitHub (Recommended)
``````bash
cd QuantIQ-Export
git init
git add .
git commit -m "QuantIQ - Complete Android App"
# Create repo on GitHub, then:
git remote add origin YOUR_GITHUB_URL
git push -u origin main
``````
Share the GitHub URL with any AI agent.

### Method 2: ZIP File
The complete ZIP file is at:
``````
$zipFile
``````
Upload to Google Drive/Dropbox and share the link.

### Method 3: Copy-Paste
Open ``COMPLETE_PROJECT_EXPORT.md`` and copy all content.
Paste into AI chat - it contains the full codebase.

## File Count
- Kotlin files: $(((Get-ChildItem -Path $exportDir -Filter "*.kt" -Recurse).Count))
- XML files: $(((Get-ChildItem -Path $exportDir -Filter "*.xml" -Recurse).Count))
- Total files: $(((Get-ChildItem -Path $exportDir -File -Recurse).Count))

## Build Instructions
``````bash
./gradlew assembleDebug
``````

## Important Notes
⚠️ The AAR library files in ``app/libs/`` are NOT included due to size.
   AI agents can understand the code without them.

✅ All business logic, UI, and configuration files are included.
"@

$summary | Out-File -FilePath (Join-Path $exportDir "README.md") -Encoding UTF8

# Create ZIP file
Write-Host "📦 Creating ZIP archive..." -ForegroundColor Yellow
if (Test-Path $zipFile) {
    Remove-Item $zipFile -Force
}
Compress-Archive -Path "$exportDir/*" -DestinationPath $zipFile -CompressionLevel Optimal

# Get file size
$zipSize = (Get-Item $zipFile).Length / 1MB

Write-Host ""
Write-Host "✅ Export Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📂 Export Location:" -ForegroundColor Cyan
Write-Host "   Directory: $exportDir" -ForegroundColor White
Write-Host "   ZIP File:  $zipFile" -ForegroundColor White
Write-Host "   Size:      $([math]::Round($zipSize, 2)) MB" -ForegroundColor White
Write-Host ""
Write-Host "🤖 To Share with AI Agents:" -ForegroundColor Cyan
Write-Host "   1. Upload ZIP to Google Drive/Dropbox" -ForegroundColor Yellow
Write-Host "   2. Share the link with AI" -ForegroundColor Yellow
Write-Host "   OR" -ForegroundColor Yellow
Write-Host "   3. Push to GitHub and share the repo URL" -ForegroundColor Yellow
Write-Host ""
Write-Host "📄 For Quick Copy-Paste:" -ForegroundColor Cyan
Write-Host "   Open: COMPLETE_PROJECT_EXPORT.md" -ForegroundColor Yellow
Write-Host "   Copy all content and paste into AI chat" -ForegroundColor Yellow
Write-Host ""

# Open explorer
Start-Process explorer.exe (Split-Path $zipFile)
