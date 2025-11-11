# How to Add Your Actual Brain Logo

Your brain logo image is perfect! Here's how to use it in the app:

---

## **METHOD 1: Add Logo to Splash Screen** (Easiest)

### Step 1: Prepare Your Logo

- Save your logo as PNG with transparent background
- Recommended size: **512x512px** or **1024x1024px**
- Name it: `brain_logo.png`

### Step 2: Add to Project

1. Copy `brain_logo.png`
2. Paste into: `app/src/main/res/drawable/`
3. That's it!

### Step 3: Update Layout

The app will automatically use it (already configured to use `@drawable/logo_quantiq`)

Just replace the `logo_quantiq.xml` file with your PNG!

---

## **METHOD 2: Generate App Icon** (For Home Screen)

### Using Android Studio:

1. Right-click on `res` folder
2. **New → Image Asset**
3. In the wizard:
    - **Icon Type:** Launcher Icons (Adaptive and Legacy)
    - **Foreground Layer:**
        - **Source Asset:** Image
        - **Path:** Browse to your brain logo PNG
        - **Trim:** Yes (removes empty space)
        - **Resize:** 75-80% (to add padding)
    - **Background Layer:**
        - **Source Asset:** Color
        - **Color:** #00897B (your teal color)
4. Click **Next** → **Finish**

This automatically generates all sizes for:

- `mipmap-mdpi`
- `mipmap-hdpi`
- `mipmap-xhdpi`
- `mipmap-xxhdpi`
- `mipmap-xxxhdpi`

---

## **METHOD 3: Quick Manual Replace**

If you just want to test it quickly:

1. **For Splash Screen:**
    - Delete: `app/src/main/res/drawable/logo_quantiq.xml`
    - Add your PNG: `app/src/main/res/drawable/logo_quantiq.png`
    - Done! The layout already references it

2. **For App Icon:**
    - Use Method 2 above (Android Studio Image Asset tool)

---

## **CURRENT SETUP:**

### What's Already Configured:

- ✅ Splash screen uses: `@drawable/logo_quantiq`
- ✅ Animations are ready (pulsing, bouncing IQ)
- ✅ App icon points to adaptive icons
- ✅ Teal theme colors throughout

### What You Need to Do:

- 📁 Just add your brain logo PNG to replace the vector

---

## **RECOMMENDED APPROACH:**

**For Best Results:**

1. Save your brain logo as PNG (transparent background)
2. Size: 1024x1024px
3. Use **Android Studio Image Asset** tool (Method 2)
    - This handles all sizes automatically
    - Adds proper padding
    - Creates adaptive icons for modern Android

---

## **File Locations:**

```
app/src/main/res/
├── drawable/
│   └── logo_quantiq.png          ← Your brain logo for splash
│
├── mipmap-mdpi/
│   └── ic_launcher.png            ← 48x48 (auto-generated)
├── mipmap-hdpi/
│   └── ic_launcher.png            ← 72x72 (auto-generated)
├── mipmap-xhdpi/
│   └── ic_launcher.png            ← 96x96 (auto-generated)
├── mipmap-xxhdpi/
│   └── ic_launcher.png            ← 144x144 (auto-generated)
└── mipmap-xxxhdpi/
    └── ic_launcher.png            ← 192x192 (auto-generated)
```

---

## **TIPS:**

### Logo Design Tips:

- ✅ PNG with **transparent background**
- ✅ Square aspect ratio (1:1)
- ✅ **Minimum 512x512px** (1024x1024px better)
- ✅ Keep important elements **away from edges** (20% padding)
- ✅ Test on both light and dark backgrounds

### Colors:

Your brain logo already uses perfect colors:

- Green-to-Blue gradient ✅
- White circuit lines ✅
- Matches app theme perfectly ✅

---

## **QUICK TEST:**

After adding your logo:

1. **Clean build:** Build → Clean Project
2. **Rebuild:** Build → Rebuild Project
3. **Uninstall old app** from phone
4. **Install new APK**
5. Check:
    - Home screen icon
    - Splash screen
    - Animations working

---

## **NEED HELP?**

If your logo still looks off:

1. Check PNG is **square** (not rectangular)
2. Check PNG has **transparent background** (not white)
3. Try increasing **resize percentage** in Image Asset tool (80-90%)
4. Make sure PNG is at least **512x512px**

The animated effects (pulsing, bouncing IQ) will work automatically with your image!
