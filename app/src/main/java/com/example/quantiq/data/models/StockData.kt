package com.example.quantiq.data.models

data class StockData(
    val date: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

data class TechnicalIndicators(
    val sma20: Double? = null,
    val sma50: Double? = null,
    val rsi: Double? = null,
    val macd: Double? = null,
    val macdSignal: Double? = null,
    val macdHistogram: Double? = null
)

data class BacktestResult(
    val strategy: String,
    val ticker: String,
    val totalReturn: Double,
    val annualizedReturn: Double,
    val sharpeRatio: Double,
    val maxDrawdown: Double,
    val volatility: Double,
    val winRate: Double,
    val totalTrades: Int,
    val quantScore: Double,
    val equityCurve: List<Double>,
    val trades: List<Trade>
)

data class Trade(
    val date: String,
    val type: TradeType,
    val price: Double,
    val shares: Int,
    val value: Double
)

enum class TradeType {
    BUY, SELL
}

data class Strategy(
    val name: String,
    val shortPeriod: Int = 20,
    val longPeriod: Int = 50,
    val rsiOverbought: Int = 70,
    val rsiOversold: Int = 30
)
