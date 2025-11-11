package com.example.quantiq.domain

import com.example.quantiq.data.models.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Backtesting engine that simulates trading strategies and calculates performance metrics
 */
class BacktestEngine(
    private val indicatorsCalculator: TechnicalIndicatorsCalculator = TechnicalIndicatorsCalculator()
) {

    /**
     * Run backtest for a given strategy on stock data
     * @param stockData Historical stock data
     * @param strategy Trading strategy to test
     * @param initialCapital Starting capital (default $10,000)
     * @return BacktestResult with all performance metrics
     */
    fun runBacktest(
        stockData: List<StockData>,
        strategy: Strategy,
        ticker: String,
        initialCapital: Double = 10000.0
    ): BacktestResult {
        val prices = stockData.map { it.close }

        // Calculate buy-and-hold return for benchmark
        val buyAndHoldReturn = if (prices.isNotEmpty()) {
            ((prices.last() - prices.first()) / prices.first()) * 100
        } else {
            0.0
        }

        // Calculate technical indicators based on strategy
        val signals = when (strategy.name) {
            "SMA Crossover" -> generateSMACrossoverSignals(prices, strategy)
            "RSI Strategy" -> generateRSISignals(prices, strategy)
            "MACD Strategy" -> generateMACDSignals(prices)
            "Mean Reversion" -> generateMeanReversionSignals(prices, strategy)
            else -> generateSMACrossoverSignals(prices, strategy)
        }

        // Simulate trading
        val (trades, equityCurve) = simulateTrading(
            stockData = stockData,
            signals = signals,
            initialCapital = initialCapital
        )

        // Calculate performance metrics
        val metrics = calculateMetrics(
            equityCurve = equityCurve,
            trades = trades,
            stockData = stockData,
            initialCapital = initialCapital
        )

        // Calculate alpha (excess return over buy-and-hold)
        val alphaVsBuyAndHold = metrics.totalReturn - buyAndHoldReturn

        return BacktestResult(
            strategy = strategy.name,
            ticker = ticker,
            totalReturn = metrics.totalReturn,
            annualizedReturn = metrics.annualizedReturn,
            sharpeRatio = metrics.sharpeRatio,
            maxDrawdown = metrics.maxDrawdown,
            volatility = metrics.volatility,
            winRate = metrics.winRate,
            totalTrades = trades.size,
            quantScore = calculateQuantScore(metrics),
            equityCurve = equityCurve,
            trades = trades,
            buyAndHoldReturn = buyAndHoldReturn,
            alphaVsBuyAndHold = alphaVsBuyAndHold
        )
    }

    /**
     * Generate buy/sell signals using SMA Crossover strategy
     */
    private fun generateSMACrossoverSignals(prices: List<Double>, strategy: Strategy): List<Int> {
        val shortSMA = indicatorsCalculator.calculateSMA(prices, strategy.shortPeriod)
        val longSMA = indicatorsCalculator.calculateSMA(prices, strategy.longPeriod)

        val signals = MutableList(prices.size) { 0 }

        for (i in 1 until prices.size) {
            if (shortSMA[i] != null && longSMA[i] != null &&
                shortSMA[i - 1] != null && longSMA[i - 1] != null
            ) {

                // Buy signal: short SMA crosses above long SMA
                if (shortSMA[i]!! > longSMA[i]!! && shortSMA[i - 1]!! <= longSMA[i - 1]!!) {
                    signals[i] = 1
                }
                // Sell signal: short SMA crosses below long SMA
                else if (shortSMA[i]!! < longSMA[i]!! && shortSMA[i - 1]!! >= longSMA[i - 1]!!) {
                    signals[i] = -1
                }
            }
        }

        return signals
    }

    /**
     * Generate buy/sell signals using RSI strategy
     */
    private fun generateRSISignals(prices: List<Double>, strategy: Strategy): List<Int> {
        val rsi = indicatorsCalculator.calculateRSI(prices)
        val signals = MutableList(prices.size) { 0 }

        for (i in 1 until prices.size) {
            if (rsi[i] != null) {
                // Buy signal: RSI crosses above oversold level
                if (rsi[i]!! > strategy.rsiOversold &&
                    (rsi[i - 1] ?: 100.0) <= strategy.rsiOversold
                ) {
                    signals[i] = 1
                }
                // Sell signal: RSI crosses below overbought level
                else if (rsi[i]!! < strategy.rsiOverbought &&
                    (rsi[i - 1] ?: 0.0) >= strategy.rsiOverbought
                ) {
                    signals[i] = -1
                }
            }
        }

        return signals
    }

    /**
     * Generate buy/sell signals using MACD strategy
     */
    private fun generateMACDSignals(prices: List<Double>): List<Int> {
        val (macdLine, signalLine, _) = indicatorsCalculator.calculateMACD(prices)
        val signals = MutableList(prices.size) { 0 }

        for (i in 1 until prices.size) {
            if (macdLine[i] != null && signalLine[i] != null &&
                macdLine[i - 1] != null && signalLine[i - 1] != null
            ) {

                // Buy signal: MACD crosses above signal line
                if (macdLine[i]!! > signalLine[i]!! && macdLine[i - 1]!! <= signalLine[i - 1]!!) {
                    signals[i] = 1
                }
                // Sell signal: MACD crosses below signal line
                else if (macdLine[i]!! < signalLine[i]!! && macdLine[i - 1]!! >= signalLine[i - 1]!!) {
                    signals[i] = -1
                }
            }
        }

        return signals
    }

    /**
     * Generate buy/sell signals using Mean Reversion strategy
     */
    private fun generateMeanReversionSignals(prices: List<Double>, strategy: Strategy): List<Int> {
        val (upperBand, middleBand, lowerBand) = indicatorsCalculator.calculateBollingerBands(
            prices, strategy.shortPeriod
        )
        val signals = MutableList(prices.size) { 0 }

        for (i in prices.indices) {
            if (upperBand[i] != null && lowerBand[i] != null) {
                // Buy signal: price touches lower band
                if (prices[i] <= lowerBand[i]!!) {
                    signals[i] = 1
                }
                // Sell signal: price touches upper band
                else if (prices[i] >= upperBand[i]!!) {
                    signals[i] = -1
                }
            }
        }

        return signals
    }

    /**
     * Simulate trading based on signals
     */
    private fun simulateTrading(
        stockData: List<StockData>,
        signals: List<Int>,
        initialCapital: Double
    ): Pair<List<Trade>, List<Double>> {
        val trades = mutableListOf<Trade>()
        val equityCurve = mutableListOf<Double>()

        var cash = initialCapital
        var shares = 0
        var position = 0 // 0 = no position, 1 = long position

        for (i in stockData.indices) {
            val price = stockData[i].close
            val signal = signals[i]

            // Buy signal and no position
            if (signal == 1 && position == 0) {
                shares = (cash / price).toInt()
                if (shares > 0) {
                    val cost = shares * price
                    cash -= cost
                    position = 1

                    trades.add(
                        Trade(
                            date = stockData[i].date,
                            type = TradeType.BUY,
                            price = price,
                            shares = shares,
                            value = cost
                        )
                    )
                }
            }
            // Sell signal and has position
            else if (signal == -1 && position == 1) {
                val value = shares * price
                cash += value

                trades.add(
                    Trade(
                        date = stockData[i].date,
                        type = TradeType.SELL,
                        price = price,
                        shares = shares,
                        value = value
                    )
                )

                shares = 0
                position = 0
            }

            // Calculate current equity
            val equity = cash + (shares * price)
            equityCurve.add(equity)
        }

        // Close any remaining position at the end
        if (position == 1 && shares > 0) {
            val finalPrice = stockData.last().close
            val value = shares * finalPrice
            cash += value

            trades.add(
                Trade(
                    date = stockData.last().date,
                    type = TradeType.SELL,
                    price = finalPrice,
                    shares = shares,
                    value = value
                )
            )
        }

        return Pair(trades, equityCurve)
    }

    /**
     * Calculate performance metrics
     */
    private fun calculateMetrics(
        equityCurve: List<Double>,
        trades: List<Trade>,
        stockData: List<StockData>,
        initialCapital: Double
    ): PerformanceMetrics {
        // Total return
        val finalEquity = equityCurve.lastOrNull() ?: initialCapital
        val totalReturn = ((finalEquity - initialCapital) / initialCapital) * 100

        // Annualized return
        val years = stockData.size / 252.0 // Assuming 252 trading days per year
        val annualizedReturn = if (years > 0) {
            (((finalEquity / initialCapital).pow(1.0 / years)) - 1) * 100
        } else {
            0.0
        }

        // Calculate returns for Sharpe ratio and volatility
        val returns = mutableListOf<Double>()
        for (i in 1 until equityCurve.size) {
            val dailyReturn = (equityCurve[i] - equityCurve[i - 1]) / equityCurve[i - 1]
            returns.add(dailyReturn)
        }

        // Volatility (annualized standard deviation)
        val avgReturn = returns.average()
        val variance = returns.map { (it - avgReturn).pow(2) }.average()
        val volatility = sqrt(variance) * sqrt(252.0) * 100 // Annualized

        // Sharpe ratio (assuming risk-free rate of 0 for simplicity)
        val sharpeRatio = if (volatility > 0) {
            (annualizedReturn / volatility)
        } else {
            0.0
        }

        // Maximum drawdown
        val maxDrawdown = calculateMaxDrawdown(equityCurve)

        // Win rate
        val winRate = if (trades.size >= 2) {
            var wins = 0
            for (i in 1 until trades.size step 2) {
                if (i < trades.size && trades[i].value > trades[i - 1].value) {
                    wins++
                }
            }
            (wins.toDouble() / (trades.size / 2)) * 100
        } else {
            0.0
        }

        return PerformanceMetrics(
            totalReturn = totalReturn,
            annualizedReturn = annualizedReturn,
            sharpeRatio = sharpeRatio,
            maxDrawdown = maxDrawdown,
            volatility = volatility,
            winRate = winRate
        )
    }

    /**
     * Calculate maximum drawdown
     */
    private fun calculateMaxDrawdown(equityCurve: List<Double>): Double {
        var maxDrawdown = 0.0
        var peak = equityCurve.firstOrNull() ?: 0.0

        for (value in equityCurve) {
            if (value > peak) {
                peak = value
            }
            val drawdown = ((peak - value) / peak) * 100
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown
            }
        }

        return maxDrawdown
    }

    /**
     * Calculate QuantScore (0-100)
     * Improved formula that properly penalizes losses:
     * - Heavy penalty for negative returns
     * - Sharpe ratio importance
     * - Drawdown penalty
     * - Win rate consideration
     */
    private fun calculateQuantScore(metrics: PerformanceMetrics): Double {
        // If total return is negative, cap score at 35 (POOR rating)
        if (metrics.totalReturn < 0) {
            // Slight adjustment based on how bad the loss is
            val lossScore = 35.0 - (metrics.totalReturn.coerceIn(-50.0, 0.0) / 50.0 * 15.0)
            return lossScore.coerceIn(0.0, 35.0)
        }

        // For positive returns, use weighted formula
        // Sharpe ratio: -2 to 3 is normal range
        val sharpeScore = (metrics.sharpeRatio.coerceIn(-2.0, 3.0) + 2.0) / 5.0 * 100

        // Annualized return: 0% to 50% is excellent
        val returnScore = (metrics.annualizedReturn.coerceIn(0.0, 50.0) / 50.0) * 100

        // Drawdown: Lower is better (0% drawdown = 100 points, 50% drawdown = 0 points)
        val drawdownScore = 100.0 - (metrics.maxDrawdown.coerceIn(0.0, 50.0) * 2.0)

        // Win rate: 50% is neutral, higher is better
        val winRateScore = metrics.winRate.coerceIn(0.0, 100.0)

        // Weighted combination:
        // 35% Return, 30% Sharpe, 20% Drawdown, 15% Win Rate
        val rawScore = (0.35 * returnScore) +
                (0.30 * sharpeScore) +
                (0.20 * drawdownScore) +
                (0.15 * winRateScore)

        return rawScore.coerceIn(0.0, 100.0)
    }
}

/**
 * Internal data class for performance metrics
 */
private data class PerformanceMetrics(
    val totalReturn: Double,
    val annualizedReturn: Double,
    val sharpeRatio: Double,
    val maxDrawdown: Double,
    val volatility: Double,
    val winRate: Double
)
