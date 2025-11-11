# 📦 How to Share Your QuantIQ APK

## 🎯 **Why APKs Aren't in GitHub**

APK files are **NOT** stored in git repositories because:

- ❌ Large files (50-100MB+)
- ❌ Binary files (not source code)
- ❌ Generated from source code
- ❌ Change with every build

**Instead:** Build APK locally → Share separately

---

## 🚀 **METHOD 1: Build in Android Studio** ⭐ RECOMMENDED

### **Steps:**

1. **Open Android Studio** with QuantIQ project

2. **Build APK:**
    - Menu: **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
    - Wait 1-2 minutes
    - Look for "APK(s) generated successfully" notification

3. **Locate APK:**
    - Click **"locate"** in the notification bubble
    - Or navigate to: `app\build\outputs\apk\debug\app-debug.apk`

4. **Rename it:**
    - Copy `app-debug.apk` to your Desktop
    - Rename to: `QuantIQ-v1.0.apk`

5. **Share it:**
    - Upload to Google Drive/Dropbox
    - Or create GitHub Release (see below)

---

## 💻 **METHOD 2: Build Using Script**

### **Quick Way:**

Run this in PowerShell from project folder:

```powershell
.\build_apk.ps1
```

This script will:

- ✅ Build the APK
- ✅ Show you exactly where it is
- ✅ Show file size
- ✅ Offer to open the folder

### **Manual Way:**

```powershell
.\gradlew.bat assembleDebug
```

Then find APK at: `app\build\outputs\apk\debug\app-debug.apk`

---

## 🌐 **METHOD 3: Create GitHub Release**

This is the **PROFESSIONAL** way to share APKs!

### **Steps:**

1. **Build APK** (using Method 1 or 2 above)

2. **Go to GitHub:**
    - Visit: https://github.com/Jethin10/QuantIQ
    - Click **"Releases"** (right sidebar)
    - Click **"Create a new release"**

3. **Fill in Release Details:**
    - **Tag version:** `v1.0.0`
    - **Release title:** `QuantIQ v1.0.0 - Hackathon Submission`
    - **Description:**
   ```markdown
   ## 🧠 QuantIQ - AI-Powered Stock Backtesting Platform
   
   ### Features:
   - ✅ On-device AI (SmolLM2 360M)
   - ✅ 4 professional trading strategies
   - ✅ QuantScore™ algorithm
   - ✅ Buy-and-hold benchmarking
   - ✅ Strategy comparison
   - ✅ Beautiful Material Design UI
   
   ### Installation:
   1. Download `QuantIQ-v1.0.apk` below
   2. Enable "Install from unknown sources" on your Android device
   3. Install the APK
   4. First launch downloads AI model (360MB, one-time)
   5. Enjoy!
   
   ### System Requirements:
   - Android 7.0+ (API 24+)
   - 500MB free space (for AI model)
   - Internet connection (for stock data)
   ```

4. **Attach APK:**
    - Scroll down to **"Attach binaries"**
    - Drag your `QuantIQ-v1.0.apk` file
    - Wait for upload

5. **Publish:**
    - Click **"Publish release"**
    - Done! ✅

6. **Share Link:**
    - Your APK is now at: `https://github.com/Jethin10/QuantIQ/releases/latest`
    - Share this link with judges!

---

## 📤 **METHOD 4: Upload to Cloud Storage**

### **Google Drive:**

1. Upload APK to Google Drive
2. Right-click → Get shareable link
3. Set to "Anyone with the link can view"
4. Share link with judges

### **Dropbox:**

1. Upload APK to Dropbox
2. Get shareable link
3. Share with judges

### **OneDrive:**

1. Upload to OneDrive
2. Right-click → Share
3. Copy link and share

---

## 🎓 **FOR HACKATHON JUDGES:**

### **What to Give Them:**

#### **Option A: GitHub Release** (Most Professional)

```
APK Download: https://github.com/Jethin10/QuantIQ/releases/latest
Source Code: https://github.com/Jethin10/QuantIQ
```

#### **Option B: Direct Link**

```
APK Download: [Your Google Drive/Dropbox Link]
Source Code: https://github.com/Jethin10/QuantIQ
```

#### **Option C: Email Attachment**

- Attach the APK file directly to email
- Include source code link in email body

---

## ⚠️ **IMPORTANT NOTES:**

### **APK Size:**

- **Debug APK:** ~50-80 MB
- **With AI model:** Total ~400MB on device
- This is normal for AI-powered apps!

### **Installation:**

Judges need to:

1. Enable "Install from unknown sources"
2. Download APK
3. Tap to install
4. Wait for AI model download on first launch (2-5 minutes)

### **First Launch:**

- App downloads SmolLM2 model (360MB)
- Takes 2-5 minutes on first launch
- All subsequent launches are instant

---

## 🔒 **SECURITY NOTE:**

Tell judges the app is safe:

- ✅ Source code is open on GitHub
- ✅ No malicious code
- ✅ On-device AI (no data sent to cloud)
- ✅ Only accesses: Internet (for stock data) and Storage (for AI model)

---

## 📊 **APK File Info:**

```
Filename: QuantIQ-v1.0.apk
Size: ~50-80 MB
Min Android: 7.0 (API 24)
Target Android: 14 (API 34)
Permissions: Internet, Storage
```

---

## ✅ **CHECKLIST FOR SUBMISSION:**

- [ ] APK built successfully
- [ ] APK renamed to `QuantIQ-v1.0.apk`
- [ ] APK uploaded to GitHub Release or Cloud Storage
- [ ] Download link tested and works
- [ ] README.md has download link
- [ ] Hackathon submission form includes APK link
- [ ] Judges can download without issues

---

## 🎉 **YOU'RE READY!**

Choose your preferred method above and share your APK with the hackathon judges!

**Recommended:** GitHub Release (most professional) or Google Drive (easiest)

---

_Pro Tip: Always test the download link yourself before sharing!_
