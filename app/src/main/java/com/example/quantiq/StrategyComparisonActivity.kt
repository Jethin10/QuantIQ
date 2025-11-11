package com.example.quantiq

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quantiq.data.models.BacktestResult
import com.example.quantiq.data.models.Strategy
import com.example.quantiq.domain.BacktestEngine
import com.example.quantiq.data.repository.StockRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class StrategyComparisonActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var comparisonTable: TableLayout
    private lateinit var tvWinnerStrategy: TextView
    private lateinit var tvWinnerReturn: TextView
    private lateinit var winnerCard: MaterialCardView
    private lateinit var tableCard: MaterialCardView
    private lateinit var progressCard: MaterialCardView
    private lateinit var tvProgress: TextView

    private val backtestEngine = BacktestEngine()
    private val stockRepository = StockRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strategy_comparison)

        toolbar = findViewById(R.id.toolbar)
        comparisonTable = findViewById(R.id.comparisonTable)
        tvWinnerStrategy = findViewById(R.id.tvWinnerStrategy)
        tvWinnerReturn = findViewById(R.id.tvWinnerReturn)
        winnerCard = findViewById(R.id.winnerCard)
        tableCard = findViewById(R.id.tableCard)
        progressCard = findViewById(R.id.progressCard)
        tvProgress = findViewById(R.id.tvProgress)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val ticker = intent.getStringExtra("ticker") ?: "AAPL"
        val days = intent.getIntExtra("days", 90)

        findViewById<TextView>(R.id.tvComparisonSubtitle).text = "$ticker • $days Days"

        runComparison(ticker, days)
    }

    private fun runComparison(ticker: String, days: Int) {
        lifecycleScope.launch {
            try {
                tvProgress.text = "Fetching data for $ticker..."

                // Fetch stock data once
                val result = stockRepository.getHistoricalData(ticker, days)
                if (result.isFailure) {
                    tvProgress.text = "Error: ${result.exceptionOrNull()?.message}"
                    return@launch
                }

                val stockData = result.getOrNull() ?: return@launch

                tvProgress.text = "Running all 4 strategies..."

                // Run all 4 strategies in parallel
                val strategies = listOf(
                    Strategy("SMA Crossover", shortPeriod = 20, longPeriod = 50),
                    Strategy("RSI Strategy", rsiOverbought = 70, rsiOversold = 30),
                    Strategy("MACD Strategy"),
                    Strategy("Mean Reversion", shortPeriod = 20)
                )

                val results = strategies.map { strategy ->
                    async {
                        backtestEngine.runBacktest(stockData, strategy, ticker)
                    }
                }.awaitAll()

                // Hide progress, show results
                progressCard.visibility = View.GONE

                // Display results
                displayComparisonTable(results)

                // Find and display winner
                val winner = results.maxByOrNull { it.totalReturn } ?: results[0]
                displayWinner(winner)

                // Show cards with animation
                winnerCard.alpha = 0f
                winnerCard.visibility = View.VISIBLE
                winnerCard.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .start()

                tableCard.alpha = 0f
                tableCard.visibility = View.VISIBLE
                tableCard.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay(200)
                    .start()

            } catch (e: Exception) {
                e.printStackTrace()
                tvProgress.text = "Error: ${e.message}"
            }
        }
    }

    private fun displayComparisonTable(results: List<BacktestResult>) {
        comparisonTable.removeAllViews()

        // Header row
        val headerRow = TableRow(this).apply {
            setBackgroundColor(Color.parseColor("#F1F5F9"))
            setPadding(16, 16, 16, 16)
        }

        listOf("Strategy", "Return", "Sharpe", "Drawdown", "Trades", "Score").forEach { header ->
            headerRow.addView(TextView(this).apply {
                text = header
                setTypeface(null, Typeface.BOLD)
                textSize = 12f
                setTextColor(Color.parseColor("#64748B"))
                setPadding(12, 12, 12, 12)
                gravity = Gravity.CENTER
                minWidth = 100
            })
        }
        comparisonTable.addView(headerRow)

        // Data rows
        results.forEach { result ->
            val row = TableRow(this).apply {
                setBackgroundColor(Color.WHITE)
                setPadding(16, 12, 16, 12)
            }

            // Strategy name
            row.addView(createCell(result.strategy, bold = true))

            // Total Return
            row.addView(
                createCell(
                    String.format("%.1f%%", result.totalReturn),
                    color = if (result.totalReturn > 0) "#4CAF50" else "#F44336"
                )
            )

            // Sharpe
            row.addView(createCell(String.format("%.2f", result.sharpeRatio)))

            // Drawdown
            row.addView(createCell(String.format("%.1f%%", result.maxDrawdown)))

            // Trades
            row.addView(createCell(result.totalTrades.toString()))

            // QuantScore
            row.addView(
                createCell(
                    result.quantScore.toInt().toString(),
                    bold = true,
                    color = "#00796B"
                )
            )

            comparisonTable.addView(row)
        }
    }

    private fun createCell(text: String, bold: Boolean = false, color: String? = null): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(Color.parseColor(color ?: "#1E293B"))
            if (bold) setTypeface(null, Typeface.BOLD)
            setPadding(12, 12, 12, 12)
            gravity = Gravity.CENTER
            minWidth = 100
        }
    }

    private fun displayWinner(winner: BacktestResult) {
        tvWinnerStrategy.text = winner.strategy
        tvWinnerReturn.text = String.format("%.1f%% Total Return", winner.totalReturn)
    }
}
