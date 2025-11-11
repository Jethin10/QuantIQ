package com.example.quantiq.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quantiq.data.api.YahooFinanceService
import com.example.quantiq.data.models.BacktestResult
import com.example.quantiq.data.models.Strategy
import com.example.quantiq.domain.BacktestEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class BacktestViewModel(application: Application) : AndroidViewModel(application) {

    private val yahooFinanceService = YahooFinanceService.getInstance()
    private val backtestEngine = BacktestEngine()

    private val _uiState = MutableStateFlow<BacktestUiState>(BacktestUiState.Idle)
    val uiState: StateFlow<BacktestUiState> = _uiState

    /**
     * Run backtest for a given stock ticker and strategy
     */
    fun runBacktest(ticker: String, strategyName: String, days: Int = 365) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = BacktestUiState.Loading("Fetching stock data for $ticker...")

                // Fetch stock data
                val stockData = yahooFinanceService.getHistoricalData(ticker, days)

                if (stockData.isEmpty()) {
                    _uiState.value = BacktestUiState.Error("No data found for $ticker")
                    return@launch
                }

                _uiState.value =
                    BacktestUiState.Loading("Running backtest with $strategyName strategy...")

                // Create strategy
                val strategy = when (strategyName) {
                    "SMA Crossover" -> Strategy(
                        name = "SMA Crossover",
                        shortPeriod = 20,
                        longPeriod = 50
                    )
                    "RSI Strategy" -> Strategy(
                        name = "RSI Strategy",
                        rsiOverbought = 70,
                        rsiOversold = 30
                    )
                    "MACD Strategy" -> Strategy(
                        name = "MACD Strategy"
                    )
                    "Mean Reversion" -> Strategy(
                        name = "Mean Reversion",
                        shortPeriod = 20
                    )
                    else -> Strategy(name = "SMA Crossover")
                }

                // Run backtest
                val result = backtestEngine.runBacktest(
                    stockData = stockData,
                    strategy = strategy,
                    ticker = ticker
                )

                // Save context for AI chat
                saveBacktestContext(result)

                _uiState.value = BacktestUiState.Success(result)

            } catch (e: Exception) {
                _uiState.value = BacktestUiState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Save backtest context to shared preferences for AI chat access
     */
    private fun saveBacktestContext(result: BacktestResult) {
        val prefs = getApplication<Application>()
            .getSharedPreferences("quantiq_backtest_cache", Context.MODE_PRIVATE)

        prefs.edit().apply {
            putString("last_ticker", result.ticker)
            putString("last_strategy", result.strategy)
            putFloat("last_total_return", result.totalReturn.toFloat())
            putFloat("last_sharpe_ratio", result.sharpeRatio.toFloat())
            putFloat("last_max_drawdown", result.maxDrawdown.toFloat())
            putFloat("last_win_rate", result.winRate.toFloat())
            putFloat("last_volatility", result.volatility.toFloat())
            putInt("last_total_trades", result.totalTrades)
            putFloat("last_quant_score", result.quantScore.toFloat())
            putLong("last_timestamp", System.currentTimeMillis())
            apply()
        }
    }
}

/**
 * UI State for backtest screen
 */
sealed class BacktestUiState {
    object Idle : BacktestUiState()
    data class Loading(val message: String) : BacktestUiState()
    data class Success(val result: BacktestResult) : BacktestUiState()
    data class Error(val message: String) : BacktestUiState()
}
