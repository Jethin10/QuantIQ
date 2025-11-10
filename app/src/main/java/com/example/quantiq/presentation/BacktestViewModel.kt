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

class BacktestViewModel : ViewModel() {

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

                _uiState.value = BacktestUiState.Success(result)

            } catch (e: Exception) {
                _uiState.value = BacktestUiState.Error("Error: ${e.message}")
            }
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
