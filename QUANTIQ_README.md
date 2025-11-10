# QuantIQ - AI-Powered Backtesting & Risk Scoring Engine

## 📊 Overview

QuantIQ is a fully self-contained Android FinTech app that allows users to backtest trading
strategies on real market data, calculate risk-adjusted performance metrics, and get AI-powered
insights using the RunAnywhere SDK.

## ✨ Features

### Core Features

- ✅ **Real-time Market Data**: Fetch historical stock data from Yahoo Finance (no API key needed)
- ✅ **Multiple Trading Strategies**:
    - SMA Crossover (20/50 periods)
    - RSI Strategy (Overbought/Oversold)
    - MACD Strategy
    - Mean Reversion (Bollinger Bands)
- ✅ **On-Device Backtesting**: Complete simulation of buy/sell trades
- ✅ **Performance Metrics**:
    - Total Return & Annualized Return
    - Sharpe Ratio
    - Maximum Drawdown
    - Volatility (Annualized Standard Deviation)
    - Win Rate
    - **QuantScore** (0-100): Composite risk-adjusted performance score
- ✅ **AI Insights**: On-device AI analysis using RunAnywhere SDK
- ✅ **No Backend Required**: 100% local processing

### Technical Indicators Calculated

- Simple Moving Average (SMA)
- Exponential Moving Average (EMA)
- Relative Strength Index (RSI)
- MACD (Moving Average Convergence Divergence)
- Bollinger Bands

## 🏗️ Architecture

### Tech Stack

- **Language**: Kotlin
- **IDE**: Android Studio
- **Architecture**: MVVM (Model-View-ViewModel)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36

### Dependencies

```kotlin
// API & Networking
- Retrofit 2.9.0
- OkHttp 4.12.0
- Gson 2.10.1

// Coroutines
- kotlinx-coroutines-android 1.7.3

// Charts (MPAndroidChart)
- MPAndroidChart v3.1.0

// Lifecycle & ViewModel
- lifecycle-viewmodel-ktx 2.7.0
- lifecycle-livedata-ktx 2.7.0

// RunAnywhere SDK
- RunAnywhereKotlinSDK-release.aar
- runanywhere-llm-llamacpp-release.aar
```

## 📁 Project Structure

```
app/src/main/java/com/example/quantiq/
├── data/
│   ├── api/
│   │   └── YahooFinanceService.kt      # Yahoo Finance API integration
│   └── models/
│       └── StockData.kt                 # Data models
├── domain/
│   ├── BacktestEngine.kt                # Core backtesting logic
│   └── TechnicalIndicatorsCalculator.kt # Technical indicators
├── presentation/
│   └── BacktestViewModel.kt             # ViewModel for UI
└── MainActivity.kt                       # Main UI
```

## 🔧 How It Works

### 1. Data Fetching

```kotlin
YahooFinanceService.getHistoricalData(symbol: "AAPL", days: 365)
```

- Fetches historical OHLCV data from Yahoo Finance
- No API key required
- Returns list of `StockData` objects

### 2. Technical Indicators

```kotlin
TechnicalIndicatorsCalculator
├── calculateSMA(prices, period)
├── calculateEMA(prices, period)
├── calculateRSI(prices, period)
├── calculateMACD(prices)
└── calculateBollingerBands(prices, period)
```

### 3. Strategy Signal Generation

```kotlin
// Example: SMA Crossover
- Buy Signal: Short SMA crosses above Long SMA
- Sell Signal: Short SMA crosses below Long SMA
```

### 4. Backtesting Simulation

```kotlin
BacktestEngine.runBacktest(
    stockData: List<StockData>,
    strategy: Strategy,
    ticker: String,
    initialCapital: 10000.0
)
```

- Simulates buy/sell trades based on strategy signals
- Tracks cash, shares, and equity over time
- Records all trades with timestamps

### 5. Performance Metrics Calculation

#### **QuantScore Formula**

```
QuantScore = (0.4 × Normalized_Sharpe) + 
             (0.3 × Normalized_Return) + 
             (0.3 × Normalized_Drawdown)
```

Where:

- **Normalized_Sharpe**: Maps Sharpe ratio from [-2, 3] to [0, 100]
- **Normalized_Return**: Maps annual return from [-50%, 100%] to [0, 100]
- **Normalized_Drawdown**: `100 - MaxDrawdown` (inverted, higher is better)

#### **Sharpe Ratio**

```
Sharpe = Annualized_Return / Volatility
```

- Measures risk-adjusted return
- Higher is better (>1 is good, >2 is excellent)

#### **Maximum Drawdown**

```
MaxDrawdown = Max((Peak - Trough) / Peak) × 100
```

- Largest peak-to-trough decline
- Lower is better

#### **Volatility**

```
Volatility = StdDev(Daily_Returns) × √252 × 100
```

- Annualized standard deviation
- Measures price fluctuation

#### **Win Rate**

```
WinRate = (Winning_Trades / Total_Trades) × 100
```

## 📈 Supported Strategies

### 1. SMA Crossover Strategy

- **Short Period**: 20 days
- **Long Period**: 50 days
- **Buy**: When 20-day SMA crosses above 50-day SMA
- **Sell**: When 20-day SMA crosses below 50-day SMA
- **Best For**: Trending markets

### 2. RSI Strategy

- **Period**: 14 days
- **Overbought**: 70
- **Oversold**: 30
- **Buy**: When RSI crosses above 30 (oversold)
- **Sell**: When RSI crosses below 70 (overbought)
- **Best For**: Range-bound markets

### 3. MACD Strategy

- **Fast EMA**: 12 days
- **Slow EMA**: 26 days
- **Signal Line**: 9 days
- **Buy**: When MACD crosses above signal line
- **Sell**: When MACD crosses below signal line
- **Best For**: Momentum trading

### 4. Mean Reversion Strategy

- **Period**: 20 days (Bollinger Bands)
- **Buy**: When price touches lower band
- **Sell**: When price touches upper band
- **Best For**: Oscillating markets

## 🤖 AI Integration (RunAnywhere SDK)

### Model Download & Loading

```kotlin
// Register models from HuggingFace
addModelFromURL(
    url = "https://huggingface.co/prithivMLmods/SmolLM2-360M-GGUF/...",
    name = "SmolLM2 360M Q8_0",
    type = "LLM"
)

// Download model
RunAnywhere.downloadModel(modelId).collect { progress -> }

// Load model
RunAnywhere.loadModel(modelId)
```

### AI Insights Generation

```kotlin
val prompt = """
Analyze backtest results for $ticker using $strategy:
- Total Return: $totalReturn%
- Sharpe Ratio: $sharpeRatio
- Max Drawdown: $maxDrawdown%
- QuantScore: $quantScore/100

Provide insights on strategy performance and market suitability.
"""

RunAnywhere.generateStream(prompt).collect { token ->
    // Display AI insights in real-time
}
```

## 📱 UI Workflow

### User Flow

```
1. Select Stock (AAPL, TSLA, MSFT, etc.)
   ↓
2. Choose Strategy (SMA Crossover, RSI, MACD, Mean Reversion)
   ↓
3. Set Timeframe (30, 90, 180, 365 days)
   ↓
4. Run Backtest
   ↓
5. View Results:
   - Performance Metrics
   - QuantScore
   - Equity Curve Chart
   - Trade History
   ↓
6. Get AI Insights (Optional)
```

## 🎨 Design Guidelines

### Color Palette

- **Primary**: Teal-Blue `#00BFA5`
- **Secondary**: Navy-Blue `#007BFF`
- **Background**: White `#FFFFFF`
- **Text**: Dark Gray `#212121`
- **Success**: Green `#4CAF50`
- **Error**: Red `#F44336`

### Material 3 Components

- CardView for metrics display
- ProgressBar for loading states
- Spinner for dropdown selections
- Buttons with elevation

## 🚀 Performance Optimizations

1. **Async Operations**: All network calls and calculations use coroutines
2. **Data Caching**: Stock data can be cached locally (Room DB - optional)
3. **Efficient Calculations**: Optimized technical indicator algorithms
4. **Lazy Loading**: Charts only rendered when needed
5. **Memory Management**: Large datasets handled with proper lifecycle awareness

## 📊 Example Output

### Backtest Result for AAPL with SMA Crossover

```
═══════════════════════════════════
📊 QUANTIQ BACKTEST RESULTS
═══════════════════════════════════
Stock: AAPL
Strategy: SMA Crossover (20/50)
Period: 365 days
Initial Capital: $10,000

📈 PERFORMANCE METRICS
───────────────────────────────────
Total Return:      +24.5%
Annualized Return: +24.5%
Sharpe Ratio:      1.45
Max Drawdown:      -12.3%
Volatility:        16.9%
Win Rate:          62.5%
Total Trades:      8

🎯 QUANTSCORE: 75/100
───────────────────────────────────
Rating: GOOD
Risk-Adjusted Performance: Above Average

💡 AI INSIGHTS (Powered by RunAnywhere)
───────────────────────────────────
The SMA Crossover strategy performed well
for AAPL over the past year, capturing
several uptrends while maintaining
reasonable drawdowns. The Sharpe ratio
of 1.45 indicates good risk-adjusted
returns. Consider this strategy for
trending tech stocks.
═══════════════════════════════════
```

## 🔐 Privacy & Security

- **100% On-Device**: All calculations happen locally
- **No User Data Collection**: No personal data sent to servers
- **No Login Required**: Anonymous usage
- **Open Source SDK**: RunAnywhere SDK is transparent

## 📝 TODO / Future Enhancements

- [ ] MPAndroidChart integration for equity curve visualization
- [ ] Drawdown chart visualization
- [ ] Room database for caching historical data
- [ ] Multiple strategy comparison
- [ ] Parameter optimization (grid search)
- [ ] Portfolio backtesting (multiple stocks)
- [ ] Export results to PDF/CSV
- [ ] Push notifications for strategy signals
- [ ] Dark mode support

## 📄 License

MIT License - See LICENSE file for details

## 🙏 Acknowledgments

- **Yahoo Finance**: For free market data API
- **RunAnywhere SDK**: For on-device AI inference
- **MPAndroidChart**: For beautiful charts
- **Retrofit & OkHttp**: For networking

---

**Powered by RunAnywhere SDK**

Made with ❤️ for retail traders and quant enthusiasts
