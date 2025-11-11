package com.example.quantiq.data.models

data class BacktestContext(
    val ticker: String = "N/A",
    val strategy: String = "N/A",
    val totalReturn: Double = 0.0,
    val sharpeRatio: Double = 0.0,
    val maxDrawdown: Double = 0.0,
    val winRate: Double = 0.0,
    val volatility: Double = 0.0,
    val totalTrades: Int = 0,
    val quantScore: Double = 0.0,
    val timestamp: Long = 0L
) {
    fun isValid(): Boolean = timestamp > 0 && ticker != "N/A"

    fun toContextString(): String = """
Recent Backtest Results:
- Stock: $ticker
- Strategy: $strategy
- Total Return: ${"%.2f".format(totalReturn)}%
- Sharpe Ratio: ${"%.2f".format(sharpeRatio)}
- Max Drawdown: ${"%.2f".format(maxDrawdown)}%
- Win Rate: ${"%.2f".format(winRate)}%
- Volatility: ${"%.2f".format(volatility)}%
- Total Trades: $totalTrades
- QuantScore: ${quantScore.toInt()}/100
    """.trimIndent()

    fun getTimeAgo(): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val minutes = diff / (1000 * 60)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes minutes ago"
            else -> {
                val hours = minutes / 60
                if (hours < 24) "$hours hours ago" else "${hours / 24} days ago"
            }
        }
    }
}
