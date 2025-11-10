# 🚀 QuantIQ - Production-Ready AI-Powered Trading Backtesting Platform

<div align="center">

![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)
![License](https://img.shields.io/badge/license-MIT-purple.svg)

**A sophisticated Android fintech application for backtesting trading strategies with on-device AI
analysis**

[Features](#-features) • [Architecture](#-architecture) • [Getting Started](#-getting-started) • [Technical Stack](#-technical-stack) • [Screenshots](#-screenshots)

</div>

---

## 📋 Overview

QuantIQ is a production-grade Android application that empowers traders and investors to backtest
their trading strategies using real market data. With integrated on-device AI powered by RunAnywhere
SDK, users get intelligent insights and explanations about their trading performance - all without
requiring an internet connection after initial setup.

### 🎯 What Makes QuantIQ Special

- **🔒 100% Private**: All calculations and AI inference happen on-device
- **📊 Real Market Data**: Live data from Yahoo Finance API
- **🤖 Smart AI**: On-device LLM (SmolLM2-360M) for instant insights
- **⚡ Fast & Responsive**: Clean architecture with MVVM pattern
- **🎨 Modern UI**: Material 3 Design with smooth animations
- **📈 Professional Metrics**: Industry-standard performance indicators

---

## ✨ Features

### Core Features

#### 📈 Strategy Backtesting

- **4 Built-in Strategies**:
    - SMA Crossover (Simple Moving Average)
    - RSI (Relative Strength Index)
    - MACD (Moving Average Convergence Divergence)
    - Mean Reversion (Bollinger Bands)
- **Customizable Parameters**: Adjust strategy parameters in real-time with intuitive sliders
- **Multiple Timeframes**: 30, 90, 180, 365 days
- **Initial Capital Configuration**: Set your starting investment amount

#### 📊 Performance Metrics

- **QuantScore™**: Proprietary 0-100 scoring system combining:
    - Risk-adjusted returns (Sharpe Ratio)
    - Total and annualized returns
    - Maximum drawdown
    - Win rate and trade count
- **Detailed Analytics**:
    - Total Return (%)
    - Annualized Return (%)
    - Sharpe Ratio
    - Maximum Drawdown (%)
    - Volatility (Annualized)
    - Win Rate (%)
    - Total Trades

#### 📉 Visualization

- **Interactive Equity Curve**: Powered by MPAndroidChart
    - Smooth bezier interpolation
    - Pinch-to-zoom and pan
    - Animated chart rendering
    - Gradient fill below curve

#### 🤖 AI-Powered Insights

- **Auto-Generated Analysis**: AI automatically analyzes backtest results
- **Clickable Metrics**: Tap any metric card to get AI explanation
- **Contextual Intelligence**: AI explains metrics in the context of your specific stock and
  strategy
- **Streaming Responses**: See AI thinking in real-time

#### 💾 Additional Features

- **Stock Autocomplete**: 20+ popular stocks with company names
- **Backtest History**: Access your last 10 backtests
- **Caching System**: Smart data caching (30-minute TTL)
- **Error Handling**: Comprehensive validation and error messages
- **Progress Tracking**: Real-time progress updates during backtest

---

## 🏗️ Architecture

### Clean Architecture Pattern

```
┌─────────────────────────────────────────────┐
│           Presentation Layer                │
│  ┌──────────────┐      ┌─────────────────┐ │
│  │  Activities  │ ◄──► │   ViewModels    │ │
│  │   (Views)    │      │  (UI State)     │ │
│  └──────────────┘      └─────────────────┘ │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│            Domain Layer                     │
│  ┌──────────────┐      ┌─────────────────┐ │
│  │  Use Cases   │ ◄──► │ Backtest Engine │ │
│  │              │      │   (Business)    │ │
│  └──────────────┘      └─────────────────┘ │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│             Data Layer                      │
│  ┌──────────────┐      ┌─────────────────┐ │
│  │ Repositories │ ◄──► │   API Service   │ │
│  │   (Cache)    │      │  (Yahoo Fin.)   │ │
│  └──────────────┘      └─────────────────┘ │
└─────────────────────────────────────────────┘
```

### Key Components

#### Presentation Layer

- **MainActivity**: Main backtesting interface
- **ChatActivity**: AI chat interface
- **SplashActivity**: Initial loading screen
- **BacktestViewModel**: State management with StateFlow

#### Domain Layer

- **RunBacktestUseCase**: Orchestrates backtest execution
- **BacktestEngine**: Core backtesting logic
- **TechnicalIndicatorsCalculator**: Calculates trading indicators

#### Data Layer

- **StockRepository**: Data fetching with caching
- **YahooFinanceService**: External API integration
- **Models**: Data classes for app entities

#### Utilities

- **AnimationHelper**: Smooth UI animations
- **FormatUtils**: Number and date formatting

---

## 🔧 Technical Stack

### Languages & Frameworks

- **Kotlin**: 100% Kotlin codebase
- **Android SDK**: Target API 36, Min API 24
- **Coroutines**: Asynchronous programming
- **StateFlow**: Reactive state management

### Architecture & Patterns

- **MVVM**: Model-View-ViewModel
- **Clean Architecture**: Separation of concerns
- **Repository Pattern**: Data abstraction
- **Use Case Pattern**: Business logic encapsulation
- **Singleton Pattern**: Service instances

### Key Libraries

#### Networking & Data

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.google.code.gson:gson:2.10.1")
```

#### UI & Visualization

```kotlin
implementation("com.google.android.material:material:1.12.0")
implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
implementation("com.airbnb.android:lottie:6.4.0")
```

#### AI & Machine Learning

```kotlin
implementation(files("libs/RunAnywhereKotlinSDK-release.aar"))
implementation(files("libs/runanywhere-llm-llamacpp-release.aar"))
implementation("io.ktor:ktor-client-core:3.0.3")
```

#### Lifecycle & State

```kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 36
- Minimum 2GB RAM on device for AI model

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/quantiq.git
   cd quantiq
   ```

2. **Open in Android Studio**
    - File → Open → Select `quantiq` directory
    - Wait for Gradle sync to complete

3. **Add RunAnywhere SDK AARs**
    - Place `RunAnywhereKotlinSDK-release.aar` in `app/libs/`
    - Place `runanywhere-llm-llamacpp-release.aar` in `app/libs/`

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### First Launch

1. **Splash Screen**: App initializes SDK (5-10 seconds)
2. **Download AI Model** (Optional): Tap "Download" on orange banner
    - Model size: ~360MB
    - Download time: 2-5 minutes
    - Required for AI features
3. **Run Your First Backtest**:
    - Enter stock ticker (e.g., "AAPL")
    - Select strategy (e.g., "SMA Crossover")
    - Adjust parameters with sliders
    - Choose timeframe
    - Tap "🚀 Run Backtest"

---

## 📊 How It Works

### Backtesting Process

```
User Input → Validation → Data Fetch → Indicators → Signals → Simulation → Results
```

1. **User Input**: Ticker, strategy, parameters, timeframe
2. **Validation**: Input validation, ticker verification
3. **Data Fetch**: Historical OHLCV data from Yahoo Finance
4. **Indicators**: Calculate technical indicators (SMA, RSI, MACD, etc.)
5. **Signals**: Generate buy/sell signals based on strategy
6. **Simulation**: Simulate trades with starting capital
7. **Results**: Calculate metrics, generate AI insights

### QuantScore™ Algorithm

```kotlin
// Weighted scoring formula
score = (0.35 × returnScore) +
        (0.30 × sharpeScore) +
        (0.20 × drawdownScore) +
        (0.15 × winRateScore)

// Penalty for losses
if (totalReturn < 0) {
    score = min(score, 35) // Cap at "POOR" rating
}
```

**Rating Scale**:

- 90-100: OUTSTANDING ⭐⭐⭐
- 80-89: EXCELLENT ⭐⭐⭐
- 70-79: VERY GOOD ⭐⭐
- 60-69: GOOD ⭐⭐
- 50-59: FAIR ⭐
- 40-49: BELOW AVERAGE
- 0-39: POOR

---

## 🎨 UI/UX Highlights

### Design Principles

- **Material 3**: Latest Material Design guidelines
- **Progressive Disclosure**: Show information when needed
- **Feedback**: Visual feedback for every action
- **Accessibility**: High contrast, readable fonts
- **Consistency**: Uniform spacing, colors, typography

### Animations

- **Fade In/Out**: Smooth transitions
- **Scale**: Overshoot effect for emphasis
- **Slide**: Bottom-up reveals
- **Pulse**: Attention-grabbing highlights
- **Shake**: Error indication
- **Number Counting**: Animated metric updates

### Color Palette

```kotlin
Primary:    #00BFA5 (Teal)
Secondary:  #007BFF (Blue)
Accent:     #8A2BE2 (Purple)
Success:    #00C851 (Green)
Error:      #FF4444 (Red)
Background: #F5F7FA (Light Gray)
Surface:    #FFFFFF (White)
```

---

## 📱 Screenshots

*(Add screenshots here)*

| Main Screen | Results | AI Insights | Chart |
|------------|---------|-------------|-------|
| ![Main](screenshots/main.png) | ![Results](screenshots/results.png) | ![AI](screenshots/ai.png) | ![Chart](screenshots/chart.png) |

---

## 🧪 Testing

### Manual Testing Checklist

- [ ] Stock ticker autocomplete works
- [ ] All 4 strategies execute successfully
- [ ] Sliders update parameter values
- [ ] Charts render correctly
- [ ] AI insights generate (if model loaded)
- [ ] Error handling shows appropriate messages
- [ ] Progress indicators display during operations
- [ ] App handles network errors gracefully

### Test Scenarios

1. **Valid Backtest**: AAPL, SMA Crossover, 365 days
2. **Invalid Ticker**: "INVALID123" → Error message
3. **Network Error**: Airplane mode → Retry option
4. **AI Model**: Download, load, generate insights
5. **Parameter Limits**: Test min/max slider values

---

## 🔐 Privacy & Security

### Data Privacy

- ✅ **No User Accounts**: No login required
- ✅ **No Data Collection**: Zero telemetry or analytics
- ✅ **No Cloud Storage**: All data stays on device
- ✅ **Local AI**: On-device inference, no API calls
- ✅ **Market Data Only**: Only fetches public stock data

### Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**Why these permissions?**

- `INTERNET`: Fetch historical stock data from Yahoo Finance
- `ACCESS_NETWORK_STATE`: Check connectivity before API calls

---

## 🚧 Roadmap

### Version 2.1 (Next Release)

- [ ] Comparison view (side-by-side backtests)
- [ ] Portfolio backtesting (multiple stocks)
- [ ] Export to PDF/CSV
- [ ] Dark mode
- [ ] More technical indicators

### Version 2.2

- [ ] Strategy optimization (grid search)
- [ ] Walk-forward analysis
- [ ] Monte Carlo simulation
- [ ] Custom strategy builder

### Version 3.0

- [ ] Real-time alerts
- [ ] Paper trading mode
- [ ] Community strategies
- [ ] Cloud sync (optional)

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Write unit tests for new features

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Yahoo Finance**: Free market data API
- **RunAnywhere SDK**: On-device AI inference
- **MPAndroidChart**: Beautiful chart library
- **Material Design**: UI/UX guidelines
- **Kotlin Community**: Excellent language and ecosystem

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/quantiq/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/quantiq/discussions)
- **Email**: support@quantiq.app (coming soon)

---

## 🌟 Star History

If you find QuantIQ useful, please consider giving it a star! ⭐

---

<div align="center">

**Built with ❤️ by traders, for traders**

[⬆ Back to Top](#-quantiq---production-ready-ai-powered-trading-backtesting-platform)

</div>
