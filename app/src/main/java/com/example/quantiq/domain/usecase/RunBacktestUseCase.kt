package com.example.quantiq.domain.usecase

import com.example.quantiq.data.models.BacktestResult
import com.example.quantiq.data.models.StrategyConfig
import com.example.quantiq.data.models.toStrategy
import com.example.quantiq.data.repository.StockRepository
import com.example.quantiq.domain.BacktestEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for running backtests with proper error handling
 */
class RunBacktestUseCase(
    private val stockRepository: StockRepository = StockRepository.getInstance(),
    private val backtestEngine: BacktestEngine = BacktestEngine()
) {

    /**
     * Execute backtest for a given ticker and strategy
     */
    suspend operator fun invoke(
        ticker: String,
        strategyConfig: StrategyConfig,
        days: Int,
        initialCapital: Double = 10000.0
    ): Result<BacktestResult> = withContext(Dispatchers.IO) {
        try {
            // Validate inputs
            if (ticker.isBlank()) {
                return@withContext Result.failure(
                    IllegalArgumentException("Stock ticker cannot be empty")
                )
            }

            if (days <= 0 || days > 3650) {
                return@withContext Result.failure(
                    IllegalArgumentException("Days must be between 1 and 3650")
                )
            }

            if (initialCapital <= 0) {
                return@withContext Result.failure(
                    IllegalArgumentException("Initial capital must be positive")
                )
            }

            // Fetch stock data
            val stockDataResult = stockRepository.getHistoricalData(ticker, days)

            if (stockDataResult.isFailure) {
                return@withContext Result.failure(
                    stockDataResult.exceptionOrNull() ?: Exception("Unknown error")
                )
            }

            val stockData = stockDataResult.getOrThrow()

            // Validate sufficient data
            if (stockData.size < 50) {
                return@withContext Result.failure(
                    Exception("Insufficient data for backtesting (minimum 50 data points required)")
                )
            }

            // Run backtest
            val result = backtestEngine.runBacktest(
                stockData = stockData,
                strategy = strategyConfig.toStrategy(),
                ticker = ticker.uppercase(),
                initialCapital = initialCapital
            )

            Result.success(result)

        } catch (e: Exception) {
            Result.failure(
                Exception("Backtest failed: ${e.message}", e)
            )
        }
    }
}
