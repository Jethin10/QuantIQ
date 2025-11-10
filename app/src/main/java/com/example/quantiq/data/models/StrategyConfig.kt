package com.example.quantiq.data.models

/**
 * Enhanced strategy configuration with customizable parameters
 */
data class StrategyConfig(
    val type: StrategyType,
    val name: String,
    val description: String,
    val parameters: StrategyParameters
)

enum class StrategyType {
    SMA_CROSSOVER,
    RSI,
    MACD,
    MEAN_REVERSION,
    CUSTOM
}

sealed class StrategyParameters {
    data class SMAParameters(
        val shortPeriod: Int = 20,      // 5-50
        val longPeriod: Int = 50        // 20-200
    ) : StrategyParameters()

    data class RSIParameters(
        val period: Int = 14,           // 5-30
        val overbought: Int = 70,       // 60-90
        val oversold: Int = 30          // 10-40
    ) : StrategyParameters()

    data class MACDParameters(
        val fastPeriod: Int = 12,       // 5-20
        val slowPeriod: Int = 26,       // 15-40
        val signalPeriod: Int = 9       // 5-15
    ) : StrategyParameters()

    data class MeanReversionParameters(
        val period: Int = 20,           // 10-50
        val stdDevMultiplier: Double = 2.0  // 1.0-3.0
    ) : StrategyParameters()
}

/**
 * Strategy presets for quick access
 */
object StrategyPresets {
    val SMA_AGGRESSIVE = StrategyConfig(
        type = StrategyType.SMA_CROSSOVER,
        name = "SMA Crossover (Aggressive)",
        description = "Fast-moving crossover for quick signals",
        parameters = StrategyParameters.SMAParameters(shortPeriod = 10, longPeriod = 30)
    )

    val SMA_STANDARD = StrategyConfig(
        type = StrategyType.SMA_CROSSOVER,
        name = "SMA Crossover (Standard)",
        description = "Balanced approach for trending markets",
        parameters = StrategyParameters.SMAParameters(shortPeriod = 20, longPeriod = 50)
    )

    val SMA_CONSERVATIVE = StrategyConfig(
        type = StrategyType.SMA_CROSSOVER,
        name = "SMA Crossover (Conservative)",
        description = "Slow-moving for reliable signals",
        parameters = StrategyParameters.SMAParameters(shortPeriod = 50, longPeriod = 200)
    )

    val RSI_STANDARD = StrategyConfig(
        type = StrategyType.RSI,
        name = "RSI Strategy (Standard)",
        description = "Classic RSI overbought/oversold",
        parameters = StrategyParameters.RSIParameters(period = 14, overbought = 70, oversold = 30)
    )

    val RSI_SENSITIVE = StrategyConfig(
        type = StrategyType.RSI,
        name = "RSI Strategy (Sensitive)",
        description = "More signals with tighter bounds",
        parameters = StrategyParameters.RSIParameters(period = 14, overbought = 65, oversold = 35)
    )

    val MACD_STANDARD = StrategyConfig(
        type = StrategyType.MACD,
        name = "MACD Strategy (Standard)",
        description = "Classic MACD momentum strategy",
        parameters = StrategyParameters.MACDParameters(
            fastPeriod = 12,
            slowPeriod = 26,
            signalPeriod = 9
        )
    )

    val MEAN_REVERSION_STANDARD = StrategyConfig(
        type = StrategyType.MEAN_REVERSION,
        name = "Mean Reversion (Standard)",
        description = "Bollinger Bands based mean reversion",
        parameters = StrategyParameters.MeanReversionParameters(period = 20, stdDevMultiplier = 2.0)
    )

    fun getAllPresets(): List<StrategyConfig> = listOf(
        SMA_AGGRESSIVE,
        SMA_STANDARD,
        SMA_CONSERVATIVE,
        RSI_STANDARD,
        RSI_SENSITIVE,
        MACD_STANDARD,
        MEAN_REVERSION_STANDARD
    )
}

/**
 * Convert StrategyConfig to old Strategy model for backward compatibility
 */
fun StrategyConfig.toStrategy(): Strategy {
    return when (parameters) {
        is StrategyParameters.SMAParameters -> Strategy(
            name = when (type) {
                StrategyType.SMA_CROSSOVER -> "SMA Crossover"
                else -> name
            },
            shortPeriod = parameters.shortPeriod,
            longPeriod = parameters.longPeriod
        )

        is StrategyParameters.RSIParameters -> Strategy(
            name = "RSI Strategy",
            rsiOverbought = parameters.overbought,
            rsiOversold = parameters.oversold
        )

        is StrategyParameters.MACDParameters -> Strategy(
            name = "MACD Strategy"
        )

        is StrategyParameters.MeanReversionParameters -> Strategy(
            name = "Mean Reversion",
            shortPeriod = parameters.period
        )
    }
}
