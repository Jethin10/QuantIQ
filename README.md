# 🧠 QuantIQ - AI-Powered Stock Backtesting Platform

<div align="center">

> **Intelligent Trading Strategy Analysis with On-Device AI**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![AI](https://img.shields.io/badge/AI-On--Device-blue.svg)](https://huggingface.co/HuggingFaceTB/SmolLM2-360M)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Democratizing quantitative finance with powerful on-device AI**

[Features](#-core-features) • [Technical Stack](#%EF%B8%8F-technical-stack) • [Quick Start](#-quick-start) • [Screenshots](#-screenshots)

</div>

---

## 💡 **What is QuantIQ?**

**QuantIQ** is a mobile-first trading strategy backtesting platform that brings institutional-grade
quantitative analysis to your pocket. Unlike traditional backtesting tools that require desktop
computers and cloud services, QuantIQ runs entirely on your Android device with a powerful on-device
AI assistant  by integrating the Runanywhere sdk.

### **The Problem We Solve**

-  **Retail traders lack access** to professional backtesting tools
-  **Desktop-only solutions** aren't accessible on-the-go
-  **Cloud-based AI** compromises privacy and requires constant internet
-  **Complex metrics** confuse beginners without explanations
-  **Time-consuming** to test multiple strategies manually

### **Our Solution**

-  **Mobile-native** - Backtest anywhere, anytime
-  **On-device AI** - Privacy-first with SmolLM2 (360M parameters)
-  **QuantScore™** - Proprietary algorithm simplifies strategy evaluation
-  **Parallel execution** - Test all strategies simultaneously
-  **Benchmark comparison** - Instantly see if strategy beats buy-and-hold

---

## ✨ **Core Features**

### 📊 **4 Professional Trading Strategies**

1. **SMA Crossover** - Moving average crossovers with customizable periods
2. **RSI Strategy** - Relative Strength Index with overbought/oversold levels
3. **MACD Strategy** - Moving Average Convergence Divergence signals
4. **Mean Reversion** - Bollinger Bands-based mean reversion

### 🎯 **Advanced Analytics**

- **QuantScore™** - Comprehensive strategy rating (0-100)
- **Buy-and-Hold Benchmark** - Compare strategy vs. passive investing
- **Sharpe Ratio** - Risk-adjusted return analysis
- **Maximum Drawdown** - Downside risk assessment
- **Win Rate & Volatility** - Detailed performance metrics

### 🤖 **AI-Powered Insights**

- **SmolLM2 360M** - On-device language model for explanations
- **Interactive Chat** - Ask questions about your backtests
- **Metric Explanations** - Tap any metric for AI analysis
- **Strategy Recommendations** - AI suggests optimal parameters

### 🎨 **Professional UI/UX**

- **Dark/Light Themes** - Comfortable viewing in any environment
- **Smooth Animations** - 60fps fluid transitions throughout
- **Interactive Charts** - Zoom, pan, and explore your results
- **Material Design 3** - Modern, clean interface

---

## 🚀 **Unique Features**

### **1. Strategy Comparison Mode**

Run all 4 strategies in parallel and instantly see which performs best:

```
✓ Side-by-side metrics comparison
✓ Winner highlighting
✓ Performance ranking
✓ Single data fetch (efficient)
```

### **2. Buy-and-Hold Benchmarking**

Instantly know if your strategy adds value:
```
Strategy Return:    +25.3%
Buy & Hold:         +10.0%
Alpha (Excess):     +15.3%  ✓ Beat Market
```

### **3. Reliable Data Fetching**

- Primary: Yahoo Finance API
- Backup: Alpha Vantage API
- Automatic fallback for 99.9% uptime

### **4. Customizable Parameters**

Every strategy parameter is adjustable in real-time:

- SMA periods (5-200 days)
- RSI thresholds (10-90)
- MACD settings (5-60)
- Mean reversion sensitivity

---

## 🛠️ **Technical Stack**

### **Core Technologies**

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose & XML
- **Architecture**: MVVM with Coroutines
- **Charting**: MPAndroidChart
- **AI Engine**: RunAnywhere SDK + SmolLM2
- **Networking**: Retrofit + OkHttp
- **Data**: Room Database (future)

### **Key Libraries**

```kotlin
- Kotlin Coroutines (async operations)
- Material Design 3 (UI components)
- MPAndroidChart (data visualization)
- RunAnywhere SDK (on-device AI)
- Retrofit 2 (API calls)
- Gson (JSON parsing)
```

### **Architecture Highlights**

- Clean separation of concerns (Domain/Data/Presentation)
- Repository pattern for data abstraction
- Dependency injection ready
- Scalable for future features
- Testable design

---

## 📐 **Project Structure**

```
QuantIQ/
├── app/src/main/
│   ├── java/com/example/quantiq/
│   │   ├── data/
│   │   │   ├── api/              # API services
│   │   │   ├── models/           # Data models
│   │   │   └── repository/       # Data repositories
│   │   ├── domain/               # Business logic
│   │   │   └── BacktestEngine.kt # Strategy execution
│   │   ├── presentation/         # ViewModels
│   │   ├── ui/                   # UI helpers
│   │   ├── utils/                # Utilities
│   │   ├── MainActivity.kt       # Main screen
│   │   ├── ChatActivity.kt       # AI chat
│   │   └── StrategyComparisonActivity.kt
│   └── res/
│       ├── layout/               # XML layouts
│       ├── drawable/             # Graphics & icons
│       ├── values/               # Themes & strings
│       └── mipmap/               # App icons
└── docs/
    ├── FEATURE_ROADMAP.md        # Future features
    └── FEATURES_COMPLETED.md     # Current features
```

---

## 🎯 **QuantScore Algorithm**

Our proprietary scoring system evaluates strategies comprehensively:

```kotlin
QuantScore = (0.35 × Return Score) +
             (0.30 × Sharpe Score) +
             (0.20 × Drawdown Score) +
             (0.15 × Win Rate Score)
```

**Rating Scale:**

- 80-100: **EXCELLENT** - Institutional grade performance
- 60-79: **GOOD** - Strong strategy worth considering
- 40-59: **FAIR** - Needs optimization
- 0-39: **POOR** - High risk or underperforming

**Special Handling:**

- Negative returns capped at 35 (POOR rating)
- Risk-adjusted focus (Sharpe weighted heavily)
- Drawdown penalty prevents lucky strategies from scoring high

---

## 📊 **Backtest Engine**

### **Simulation Features**

- ✓ Realistic trade execution
- ✓ Proper position sizing
- ✓ Commission tracking (configurable)
- ✓ Equity curve generation
- ✓ Trade log with timestamps
- ✓ Maximum drawdown calculation
- ✓ Volatility analysis

### **Technical Indicators Calculated**
```kotlin
✓ SMA (Simple Moving Average)
✓ RSI (Relative Strength Index)
✓ MACD (Moving Average Convergence Divergence)
✓ Bollinger Bands (Mean Reversion)
✓ Sharpe Ratio
✓ Volatility (Annualized Std Dev)
```

---

## 🌟 **Screenshots**

<table><tr><td><img src="https://github.com/user-attachments/assets/367b5bee-52c6-4df2-bdeb-528b300201d5" alt="Backtesting Settings" width="400"/></td><td><img src="https://github.com/user-attachments/assets/5ab4df02-f2e1-4f9c-b11f-70255923919f" alt="Selecting stock and parameters" width="400"/></td></tr><tr><td><img src="https://github.com/user-attachments/assets/c379b6f0-2377-4ce7-9052-c59c4c32232f" alt="QuantIQ Ai" width="400"/></td><td><img src="https://github.com/user-attachments/assets/0510f74c-c920-4a76-a867-2cb5a0cf9e53" alt="Performance metrics and valuation graph" width="400"/></td></tr><tr><td><img src="https://github.com/user-attachments/assets/17b50bd2-9fe3-498e-b421-0a62cef30d9d" alt="Quant Score" width="400"/></td><td><img src="https://github.com/user-attachments/assets/7158447b-3c18-441b-ade0-338bef1c9f97" alt="Selecting Strategies" width="400"/></td></tr><tr><td><img src="https://github.com/user-attachments/assets/9895908e-a307-4367-8825-0991b91a906b" alt="Selecting Stocks and Parameters" width="400"/></td><td><img src="https://github.com/user-attachments/assets/1b42f86c-69fa-4f9d-bb82-54127d56e26a" alt="Loading Page" width="400"/></td></tr></table>


---

## 🚀 **Quick Start**

### **Prerequisites**

- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0+)
- Kotlin 1.9+
- Gradle 8.0+

### **Installation**

1. **Clone the repository:**

```bash
git clone https://github.com/Jethin10/QuantIQ.git
cd QuantIQ
```

2. **Open in Android Studio:**

```bash
# File → Open → Select QuantIQ folder
```

3. **Build and Run:**

```bash
./gradlew assembleDebug
# or click Run ▶️ in Android Studio
```

### **First Launch**

- App downloads AI model automatically (360MB, one-time)
- Wait 2-5 minutes for model download
- All subsequent launches are instant

---

## 📖 **How to Use**

### **Running a Backtest**

1. **Select Stock**
    - Type ticker symbol (e.g., "AAPL")
    - Or type company name (e.g., "Apple")
    - App shows company name when valid

2. **Choose Strategy**
    - Tap on any of the 4 strategy chips
    - Customize parameters with sliders
    - See parameter values update in real-time

3. **Select Timeframe**
    - 30 Days, 90 Days, 180 Days, or 1 Year
    - Longer timeframes = more data = better insights

4. **Run Backtest**
    - Tap "Run Backtest" button
    - Watch progress indicators
    - Results appear with smooth animations

5. **Analyze Results**
    - Scroll through metrics
    - Tap metrics for AI explanations
    - Check if strategy beat buy-and-hold
    - View interactive equity curve

### **Comparing Strategies**

1. Enter a stock ticker
2. Launch "Compare All Strategies"
3. Wait for parallel execution (30-60 seconds)
4. See side-by-side comparison table
5. Winner highlighted automatically

### **AI Chat**

1. Run at least one backtest
2. Tap chat icon (top right)
3. Ask questions about your results
4. AI explains metrics in plain English

---

## 🔬 **Technical Highlights for Judges**

### **1. On-Device AI - Privacy First**

- **No cloud dependency** for AI features
- All analysis happens locally
- 360M parameter language model
- Quantized for mobile efficiency (Q8_0)
- Runs smoothly on mid-range devices

### **2. Parallel Execution**

```kotlin
// Run 4 strategies simultaneously
val results = strategies.map { strategy ->
    async {
        backtestEngine.runBacktest(stockData, strategy, ticker)
    }
}.awaitAll()
```

- 4x faster than sequential execution
- Efficient coroutine-based concurrency
- Single API call for all strategies

### **3. Automatic API Fallback**
```kotlin
try {
    yahooFinanceService.getHistoricalData()
} catch {
    alphaVantageService.getHistoricalData() // Backup
}
```

- 99.9% data availability
- Seamless user experience
- Smart caching (30 min TTL)

### **4. Advanced Animations**

- **Material Motion** system throughout
- **60fps** on all tested devices
- **Shared element transitions**
- **Physics-based** spring animations
- **Staggered reveals** for elegant UX

### **5. Professional Architecture**

- **MVVM** with clear separation
- **Repository pattern** for data abstraction
- **Dependency injection** ready
- **Scalable** for future features
- **Testable** design

---

## 🎨 **Design Philosophy**

### **Inspired by Financial Professionals**

- Bloomberg Terminal aesthetics
- Data-dense but clean
- Quick insights at a glance
- Professional color scheme (teal/slate)

### **Mobile-First Approach**

- Large touch targets (48dp minimum)
- Thumb-friendly layout
- One-handed operation where possible
- Optimized for 5-7 inch screens

### **Accessibility**

- High contrast ratios (WCAG AA compliant)
- Clear typography hierarchy
- Descriptive labels for screen readers
- Scalable text sizes

---

## 🏆 **Competitive Advantages**

| Feature                | QuantIQ                  | Competitors     |
|------------------------|--------------------------|-----------------|
| On-Device AI           | ✅ Yes                    | ❌ Cloud only    |
| Buy-and-Hold Benchmark | ✅ Yes                    | ❌ Rarely        |
| Strategy Comparison    | ✅ Parallel               | ⚠️ Sequential   |
| Offline Mode           | ✅ Yes (after data fetch) | ❌ No            |
| Custom Parameters      | ✅ Real-time sliders      | ⚠️ Text input   |
| QuantScore             | ✅ Proprietary            | ❌ Basic metrics |
| API Reliability        | ✅ Dual sources           | ⚠️ Single       |
| Animations             | ✅ 60fps throughout       | ⚠️ Basic        |

---

## 📚 **Documentation**

- **FEATURE_ROADMAP.md** - Planned features and enhancements
- **FEATURES_COMPLETED.md** - Complete implementation details
- **Code Comments** - Extensive inline documentation

---

## 🔮 **Future Enhancements**

See `FEATURE_ROADMAP.md` for full list. Highlights:

- ⏳ **Portfolio Backtesting** - Test across multiple stocks
- ⏳ **Paper Trading** - Practice with real-time simulation
- ⏳ **Custom Strategies** - Build your own indicators
- ⏳ **Social Features** - Share and compare results
- ⏳ **Alerts** - Get notified when signals trigger
- ⏳ **Export Reports** - PDF/CSV export
- ⏳ **Dark Mode** - Full theme customization
- ⏳ **More Assets** - Crypto, forex, commodities

---

## 📄 **License**

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 **Team**

Created with ❤️ for [Tech Jam 2.0 & Runanywhere] by [Quantum Loop]

**Developer:** Jethin  
**GitHub:** [@YourGitHubUsername](https://github.com/Jethin10)  
**Repository:
** [https://github.com/YourGitHubUsername/QuantIQ](https://github.com/YourGitHubUsername/QuantIQ)

---

## 🙏 **Acknowledgments**

- **RunAnywhere SDK** - On-device AI capabilities
- **Hugging Face** - SmolLM2 model hosting
- **MPAndroidChart** - Beautiful chart library
- **Yahoo Finance** - Historical stock data
- **Alpha Vantage** - Backup data source

---

## 📧 **Contact**

Questions? Feedback? Reach out:

- **GitHub Issues
  **: [Report bugs or suggest features](https://github.com/Jethin10/QuantIQ/issues)
- **Email**: jethinkosaraju10@gmail.com

---

<div align="center">

**⭐ If you like QuantIQ, give it a star on GitHub! ⭐**

Made with 🧠 and Kotlin

</div>

