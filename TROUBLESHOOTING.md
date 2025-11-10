# QuantIQ Troubleshooting Guide

## App Crashes on Startup

If the app crashes immediately when you open it, follow these steps:

### Step 1: Check Android Version

- The app requires **Android 7.0 (API 24) or higher**
- Check: Settings → About Phone → Android Version
- If you have Android 6 or lower, the app won't work

### Step 2: Uninstall Previous Version

```
1. Long press on QuantIQ app icon
2. Select "Uninstall" or "App Info" → Uninstall
3. Confirm uninstall
4. Restart your phone
5. Install the new APK
```

### Step 3: Grant Permissions

When installing the APK:

1. Allow "Install from Unknown Sources"
2. After installation, go to: Settings → Apps → QuantIQ → Permissions
3. Enable: **Internet** and **Network State**

### Step 4: Check Logs in Android Studio

If you have Android Studio with your phone connected:

```
View → Tool Windows → Logcat
```

Look for errors containing:

- `com.example.quantiq`
- `FATAL EXCEPTION`
- `AndroidRuntime`

Common error patterns and solutions:

#### Error: "ClassNotFoundException"

**Solution**: Reinstall the app, the APK may be corrupted

#### Error: "NetworkSecurityException"

**Solution**: Already fixed in latest build - use the newest APK

#### Error: "OutOfMemoryError"

**Solution**: Close other apps, restart phone, then try again

## App Works But Features Don't Work

### Backtesting Not Working

**Error: "No data found for [TICKER]"**

- Check your internet connection
- Try a different stock ticker (AAPL, GOOGL, MSFT)
- Some tickers may not be available on Yahoo Finance

**Error: "Failed to fetch data"**

- Check internet connection
- Yahoo Finance may be temporarily down
- Try again in a few minutes

### AI Model Download Fails

**Error: "No models available"**

- This is expected if SDK initialization failed
- The app still works for backtesting without AI
- Try the "Download AI Model" button again

**Download stuck at 0%**

- Check internet connection
- Ensure you have at least 500 MB free storage
- Try on WiFi instead of mobile data

**Model loads but insights fail**

- The model needs 1-2 GB RAM free to run
- Close other apps
- Restart phone and try again

## How to View Crash Logs (Advanced)

### Method 1: Using Android Studio

1. Connect phone via USB
2. Enable USB Debugging on phone:
    - Settings → About Phone → Tap "Build Number" 7 times
    - Settings → Developer Options → Enable "USB Debugging"
3. Open Android Studio → Logcat
4. Run the app and watch for errors

### Method 2: Using ADB (Command Line)

```bash
# Connect phone via USB
adb devices

# View live logs
adb logcat | grep "QuantIQ\|AndroidRuntime"

# Save crash log to file
adb logcat -d > crash_log.txt
```

## Common Solutions

### Solution 1: Clear App Data

```
Settings → Apps → QuantIQ → Storage → Clear Data
Then reinstall the app
```

### Solution 2: Check Storage Space

```
Settings → Storage
Ensure you have at least 1 GB free space
```

### Solution 3: Restart Phone

```
Sometimes a simple restart fixes everything!
```

### Solution 4: Use Different APK Build

If debug APK crashes, you can try building a release version:

```bash
./gradlew assembleRelease
```

APK will be at: `app/build/outputs/apk/release/app-release-unsigned.apk`

## Still Not Working?

### Minimal Test Version

If the app still crashes, try this minimal test:

1. Comment out SDK initialization in `MainActivity.kt`:
    - Line 107-124 (the entire initializeSDK function body)
2. Rebuild: `./gradlew clean assembleDebug`
3. Install and test

If this works, the issue is with the RunAnywhere SDK initialization.

### Report the Issue

If nothing works, gather this information:

1. **Phone Model**: (e.g., Samsung Galaxy S21)
2. **Android Version**: (e.g., Android 12)
3. **RAM**: (e.g., 6 GB)
4. **Storage Available**: (e.g., 5 GB free)
5. **Error Screenshot**: Take a screenshot if possible
6. **Logcat Output**: From Android Studio or ADB

## Working Configuration

The app has been tested successfully on:

✅ **Android 11+** with **4GB+ RAM**
✅ **Good internet connection** (WiFi preferred)
✅ **500MB+ free storage**

## Quick Fixes Summary

| Problem | Quick Fix |
|---------|-----------|
| Crash on startup | Uninstall → Restart phone → Reinstall |
| Backtest fails | Check internet, try different stock |
| Model download fails | Connect to WiFi, ensure 500MB free |
| Insights don't work | Close other apps, free up RAM |
| Layout looks weird | Rotate phone, restart app |

---

**Need More Help?**

Check the full documentation in `QUANTIQ_README.md` for feature explanations.
