package com.example.quantiq

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.quantiq.data.models.BacktestResult
import com.example.quantiq.utils.FormatUtils
import com.google.android.material.card.MaterialCardView

object BenchmarkHelper {

    fun displayBenchmarkComparison(
        metricsContainer: LinearLayout,
        result: BacktestResult,
        layoutInflater: LayoutInflater
    ) {
        // Find or create benchmark card
        var benchmarkCard = metricsContainer.findViewWithTag<MaterialCardView>("benchmarkCard")

        if (benchmarkCard == null) {
            // Inflate benchmark banner
            benchmarkCard = layoutInflater.inflate(
                R.layout.benchmark_banner,
                metricsContainer,
                false
            ) as MaterialCardView
            benchmarkCard.tag = "benchmarkCard"

            // Insert after QuantScore card (index 1)
            metricsContainer.addView(benchmarkCard, 1)
        }

        // Find views
        val tvStrategyReturn = benchmarkCard.findViewById<TextView>(R.id.tvStrategyReturn)
        val tvBuyHoldReturn = benchmarkCard.findViewById<TextView>(R.id.tvBuyHoldReturn)
        val tvAlphaMessage = benchmarkCard.findViewById<TextView>(R.id.tvAlphaMessage)
        val tvAlphaArrow = benchmarkCard.findViewById<TextView>(R.id.tvAlphaArrow)
        val alphaCard = benchmarkCard.findViewById<MaterialCardView>(R.id.alphaCard)

        // Update values
        tvStrategyReturn.text = FormatUtils.formatReturn(result.totalReturn)
        tvStrategyReturn.setTextColor(getReturnColor(result.totalReturn))

        tvBuyHoldReturn.text = FormatUtils.formatReturn(result.buyAndHoldReturn)
        tvBuyHoldReturn.setTextColor(getReturnColor(result.buyAndHoldReturn))

        // Update alpha message and colors
        val alpha = result.alphaVsBuyAndHold
        if (alpha > 0) {
            tvAlphaMessage.text = "Beat Market by ${FormatUtils.formatReturn(alpha)}"
            alphaCard.setCardBackgroundColor(Color.parseColor("#E8F5E9")) // Green
            tvAlphaMessage.setTextColor(Color.parseColor("#2E7D32"))
            tvAlphaArrow.text = "↑"
            tvAlphaArrow.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            tvAlphaMessage.text = "Underperformed by ${FormatUtils.formatReturn(Math.abs(alpha))}"
            alphaCard.setCardBackgroundColor(Color.parseColor("#FFEBEE")) // Red
            tvAlphaMessage.setTextColor(Color.parseColor("#C62828"))
            tvAlphaArrow.text = "↓"
            tvAlphaArrow.setTextColor(Color.parseColor("#C62828"))
        }

        // Animate in
        benchmarkCard.alpha = 0f
        benchmarkCard.translationY = 30f
        benchmarkCard.visibility = View.VISIBLE
        benchmarkCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setStartDelay(150)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()
    }

    private fun getReturnColor(returnValue: Double): Int {
        return if (returnValue >= 0) Color.parseColor("#00C851") else Color.parseColor("#FF4444")
    }
}
