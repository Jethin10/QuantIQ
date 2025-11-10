package com.example.quantiq.domain

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Calculator for technical indicators used in trading strategies
 */
class TechnicalIndicatorsCalculator {

    /**
     * Calculate Simple Moving Average (SMA)
     * @param prices List of closing prices
     * @param period SMA period (e.g., 20, 50, 200)
     * @return List of SMA values (null for initial values where period not met)
     */
    fun calculateSMA(prices: List<Double>, period: Int): List<Double?> {
        val sma = MutableList<Double?>(prices.size) { null }

        for (i in period - 1 until prices.size) {
            val sum = prices.subList(i - period + 1, i + 1).sum()
            sma[i] = sum / period
        }

        return sma
    }

    /**
     * Calculate Exponential Moving Average (EMA)
     * @param prices List of closing prices
     * @param period EMA period
     * @return List of EMA values
     */
    fun calculateEMA(prices: List<Double>, period: Int): List<Double?> {
        if (prices.size < period) return List(prices.size) { null }

        val ema = MutableList<Double?>(prices.size) { null }
        val multiplier = 2.0 / (period + 1)

        // First EMA is SMA
        val firstSMA = prices.take(period).average()
        ema[period - 1] = firstSMA

        // Calculate EMA for remaining values
        for (i in period until prices.size) {
            ema[i] = (prices[i] - (ema[i - 1] ?: 0.0)) * multiplier + (ema[i - 1] ?: 0.0)
        }

        return ema
    }

    /**
     * Calculate Relative Strength Index (RSI)
     * @param prices List of closing prices
     * @param period RSI period (default 14)
     * @return List of RSI values (0-100)
     */
    fun calculateRSI(prices: List<Double>, period: Int = 14): List<Double?> {
        if (prices.size < period + 1) return List(prices.size) { null }

        val rsi = MutableList<Double?>(prices.size) { null }

        // Calculate price changes
        val changes = mutableListOf<Double>()
        for (i in 1 until prices.size) {
            changes.add(prices[i] - prices[i - 1])
        }

        // Calculate average gains and losses
        var avgGain = 0.0
        var avgLoss = 0.0

        // Initial average
        for (i in 0 until period) {
            if (changes[i] > 0) avgGain += changes[i]
            else avgLoss += abs(changes[i])
        }
        avgGain /= period
        avgLoss /= period

        // Calculate RSI for first period
        val rs = if (avgLoss != 0.0) avgGain / avgLoss else 100.0
        rsi[period] = 100.0 - (100.0 / (1.0 + rs))

        // Calculate RSI for remaining periods
        for (i in period until changes.size) {
            val change = changes[i]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) abs(change) else 0.0

            avgGain = ((avgGain * (period - 1)) + gain) / period
            avgLoss = ((avgLoss * (period - 1)) + loss) / period

            val currentRS = if (avgLoss != 0.0) avgGain / avgLoss else 100.0
            rsi[i + 1] = 100.0 - (100.0 / (1.0 + currentRS))
        }

        return rsi
    }

    /**
     * Calculate MACD (Moving Average Convergence Divergence)
     * @param prices List of closing prices
     * @param fastPeriod Fast EMA period (default 12)
     * @param slowPeriod Slow EMA period (default 26)
     * @param signalPeriod Signal line period (default 9)
     * @return Triple of MACD line, Signal line, and Histogram
     */
    fun calculateMACD(
        prices: List<Double>,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26,
        signalPeriod: Int = 9
    ): Triple<List<Double?>, List<Double?>, List<Double?>> {
        val fastEMA = calculateEMA(prices, fastPeriod)
        val slowEMA = calculateEMA(prices, slowPeriod)

        // Calculate MACD line
        val macdLine = MutableList<Double?>(prices.size) { null }
        for (i in prices.indices) {
            if (fastEMA[i] != null && slowEMA[i] != null) {
                macdLine[i] = fastEMA[i]!! - slowEMA[i]!!
            }
        }

        // Calculate signal line (EMA of MACD)
        val macdValues = macdLine.filterNotNull()
        val signalEMA = if (macdValues.size >= signalPeriod) {
            calculateEMA(macdValues, signalPeriod)
        } else {
            List(macdLine.size) { null }
        }

        // Align signal line with macdLine
        val signalLine = MutableList<Double?>(prices.size) { null }
        var signalIndex = 0
        for (i in macdLine.indices) {
            if (macdLine[i] != null && signalIndex < signalEMA.size) {
                signalLine[i] = signalEMA[signalIndex]
                signalIndex++
            }
        }

        // Calculate histogram
        val histogram = MutableList<Double?>(prices.size) { null }
        for (i in prices.indices) {
            if (macdLine[i] != null && signalLine[i] != null) {
                histogram[i] = macdLine[i]!! - signalLine[i]!!
            }
        }

        return Triple(macdLine, signalLine, histogram)
    }

    /**
     * Calculate Bollinger Bands
     * @param prices List of closing prices
     * @param period SMA period (default 20)
     * @param stdDevMultiplier Standard deviation multiplier (default 2)
     * @return Triple of Upper Band, Middle Band (SMA), Lower Band
     */
    fun calculateBollingerBands(
        prices: List<Double>,
        period: Int = 20,
        stdDevMultiplier: Double = 2.0
    ): Triple<List<Double?>, List<Double?>, List<Double?>> {
        val sma = calculateSMA(prices, period)
        val upperBand = MutableList<Double?>(prices.size) { null }
        val lowerBand = MutableList<Double?>(prices.size) { null }

        for (i in period - 1 until prices.size) {
            val subset = prices.subList(i - period + 1, i + 1)
            val mean = sma[i]!!
            val variance = subset.map { (it - mean).pow(2) }.average()
            val stdDev = sqrt(variance)

            upperBand[i] = mean + (stdDevMultiplier * stdDev)
            lowerBand[i] = mean - (stdDevMultiplier * stdDev)
        }

        return Triple(upperBand, sma, lowerBand)
    }
}
