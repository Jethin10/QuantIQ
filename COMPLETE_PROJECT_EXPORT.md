# QuantIQ - Complete Project Export

## Project Structure

```
QuantIQ/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/quantiq/
│   │   │   ├── MainActivity.kt
│   │   │   ├── ChatActivity.kt
│   │   │   ├── SplashActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── api/YahooFinanceService.kt
│   │   │   │   ├── models/StockData.kt
│   │   │   │   ├── models/StrategyConfig.kt
│   │   │   │   └── repository/StockRepository.kt
│   │   │   ├── domain/
│   │   │   │   ├── BacktestEngine.kt
│   │   │   │   ├── TechnicalIndicatorsCalculator.kt
│   │   │   │   └── usecase/RunBacktestUseCase.kt
│   │   │   ├── presentation/
│   │   │   │   └── BacktestViewModel.kt
│   │   │   ├── ui/
│   │   │   │   └── AnimationHelper.kt
│   │   │   └── utils/
│   │   │       └── FormatUtils.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── activity_chat.xml
│   │   │   │   ├── activity_splash.xml
│   │   │   │   ├── dialog_settings.xml
│   │   │   │   └── item_chat_message.xml
│   │   │   └── values/
│   │   │       ├── colors.xml
│   │   │       ├── strings.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## How to Share This Project

### Option 1: Copy-Paste This File

Share this entire markdown file with any AI agent. It contains all the important code.

### Option 2: GitHub Repository

```bash
# Initialize git
git init
git add .
git commit -m "Initial commit"

# Push to GitHub
git remote add origin https://github.com/YOUR_USERNAME/QuantIQ.git
git push -u origin main
```

### Option 3: ZIP File

```bash
# Exclude build files
zip -r QuantIQ.zip . -x "*/build/*" "*.gradle/*" "*.idea/*"
```

## Quick Start Instructions for AI

This is an Android fintech app built with:

- **Language**: Kotlin
- **Architecture**: MVVM
- **Min SDK**: 24
- **Target SDK**: 36
- **Key Features**:
    - Trading strategy backtesting (SMA, RSI, MACD, Mean Reversion)
    - Real market data from Yahoo Finance
    - On-device AI using RunAnywhere SDK (SmolLM2-360M)
    - Interactive charts with MPAndroidChart
    - QuantScore proprietary algorithm

## Important Notes

1. **Dependencies**: Check `build.gradle.kts` for all required libraries
2. **SDK Files**: App requires two AAR files in `app/libs/`:
    - `RunAnywhereKotlinSDK-release.aar`
    - `runanywhere-llm-llamacpp-release.aar`
3. **Permissions**: Requires INTERNET and ACCESS_NETWORK_STATE
4. **Build**: Use `./gradlew assembleDebug` to build

## Contact

- Project: QuantIQ - AI-Powered Trading Backtesting
- Platform: Android
- Status: Working prototype, demo-ready

---

**For AI Agents**: All source code files are listed below with full content.
Read through each file to understand the complete implementation.
