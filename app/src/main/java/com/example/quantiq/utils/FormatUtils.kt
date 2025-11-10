package com.example.quantiq.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for formatting display values
 */
object FormatUtils {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val percentFormat = NumberFormat.getPercentInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    /**
     * Format currency value
     */
    fun formatCurrency(value: Double): String {
        return currencyFormat.format(value)
    }

    /**
     * Format percentage with + or - sign
     */
    fun formatPercentage(value: Double, includeSign: Boolean = true): String {
        val formatted = String.format(Locale.US, "%.2f%%", value)
        return if (includeSign && value > 0) "+$formatted" else formatted
    }

    /**
     * Format return value with sign
     */
    fun formatReturn(returnValue: Double): String {
        val sign = if (returnValue >= 0) "+" else ""
        return String.format(Locale.US, "%s%.2f%%", sign, returnValue)
    }

    /**
     * Format number with decimals
     */
    fun formatNumber(value: Double, decimals: Int = 2): String {
        return String.format(Locale.US, "%.${decimals}f", value)
    }

    /**
     * Format large numbers with K, M, B suffixes
     */
    fun formatCompactNumber(value: Double): String {
        return when {
            value >= 1_000_000_000 -> String.format(Locale.US, "%.1fB", value / 1_000_000_000)
            value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000)
            value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000)
            else -> formatNumber(value, 0)
        }
    }

    /**
     * Format date from string
     */
    fun formatDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(dateString)
            date?.let { dateFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Format timestamp to date
     */
    fun formatTimestamp(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    /**
     * Format timestamp to time
     */
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    /**
     * Get days ago text
     */
    fun getDaysAgoText(days: Int): String {
        return when (days) {
            1 -> "Yesterday"
            in 2..6 -> "$days days ago"
            7 -> "1 week ago"
            in 8..13 -> "Over a week ago"
            14 -> "2 weeks ago"
            in 15..29 -> "${days / 7} weeks ago"
            30 -> "1 month ago"
            in 31..59 -> "Over a month ago"
            60 -> "2 months ago"
            in 61..364 -> "${days / 30} months ago"
            365 -> "1 year ago"
            else -> "${days / 365} years ago"
        }
    }

    /**
     * Get rating text for QuantScore
     */
    fun getQuantScoreRating(score: Double): String {
        return when {
            score >= 90 -> "OUTSTANDING ⭐⭐⭐"
            score >= 80 -> "EXCELLENT ⭐⭐⭐"
            score >= 70 -> "VERY GOOD ⭐⭐"
            score >= 60 -> "GOOD ⭐⭐"
            score >= 50 -> "FAIR ⭐"
            score >= 40 -> "BELOW AVERAGE"
            else -> "POOR"
        }
    }

    /**
     * Get Sharpe ratio interpretation
     */
    fun getSharpeRatioText(sharpe: Double): String {
        return when {
            sharpe >= 3.0 -> "Exceptional"
            sharpe >= 2.0 -> "Excellent"
            sharpe >= 1.0 -> "Good"
            sharpe >= 0.5 -> "Acceptable"
            sharpe >= 0.0 -> "Sub-optimal"
            else -> "Poor"
        }
    }

    /**
     * Get color for return value
     */
    fun getReturnColorResId(returnValue: Double): Int {
        return if (returnValue >= 0) {
            android.R.color.holo_green_dark
        } else {
            android.R.color.holo_red_dark
        }
    }

    /**
     * Abbreviate strategy name
     */
    fun abbreviateStrategyName(name: String): String {
        return when {
            name.contains("SMA", ignoreCase = true) -> "SMA"
            name.contains("RSI", ignoreCase = true) -> "RSI"
            name.contains("MACD", ignoreCase = true) -> "MACD"
            name.contains("Mean Reversion", ignoreCase = true) -> "MR"
            else -> name.take(10)
        }
    }
}
