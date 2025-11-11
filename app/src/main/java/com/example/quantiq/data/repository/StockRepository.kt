package com.example.quantiq.data.repository

import com.example.quantiq.data.api.YahooFinanceService
import com.example.quantiq.data.api.AlphaVantageService
import com.example.quantiq.data.models.StockData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Repository for managing stock data with caching and automatic API fallback
 */
class StockRepository private constructor() {

    private val yahooFinanceService = YahooFinanceService.getInstance()
    private val alphaVantageService = AlphaVantageService.getInstance()
    private val cache = ConcurrentHashMap<String, CachedData>()

    data class CachedData(
        val data: List<StockData>,
        val timestamp: Long,
        val days: Int
    )

    /**
     * Get historical stock data with caching and automatic fallback
     * Tries Yahoo Finance first, falls back to Alpha Vantage if it fails
     */
    suspend fun getHistoricalData(
        ticker: String,
        days: Int,
        forceRefresh: Boolean = false
    ): Result<List<StockData>> = withContext(Dispatchers.IO) {
        try {
            val cacheKey = "${ticker}_$days"
            val cached = cache[cacheKey]

            // Check cache validity (30 minutes)
            if (!forceRefresh && cached != null &&
                System.currentTimeMillis() - cached.timestamp < 30 * 60 * 1000
            ) {
                return@withContext Result.success(cached.data)
            }

            // Try Yahoo Finance first
            var data: List<StockData>? = null
            var lastError: Exception? = null

            try {
                data = yahooFinanceService.getHistoricalData(ticker, days)
                if (data.isNotEmpty()) {
                    // Cache the data
                    cache[cacheKey] = CachedData(data, System.currentTimeMillis(), days)
                    return@withContext Result.success(data)
                }
            } catch (e: Exception) {
                lastError = e
                // Yahoo Finance failed, try Alpha Vantage
            }

            // Fallback to Alpha Vantage
            try {
                data = alphaVantageService.getHistoricalData(ticker, days)
                if (data.isNotEmpty()) {
                    // Cache the data
                    cache[cacheKey] = CachedData(data, System.currentTimeMillis(), days)
                    return@withContext Result.success(data)
                }
            } catch (e: Exception) {
                // Both APIs failed
                return@withContext Result.failure(
                    Exception("Failed to fetch data from both Yahoo Finance and Alpha Vantage for $ticker. ${lastError?.message ?: e.message}")
                )
            }

            // No data available
            return@withContext Result.failure(
                Exception("No data available for $ticker. Please check the ticker symbol.")
            )

        } catch (e: Exception) {
            Result.failure(
                Exception("Failed to fetch data for $ticker: ${e.message}", e)
            )
        }
    }

    /**
     * Validate if a ticker exists (quick check)
     */
    suspend fun validateTicker(ticker: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Try Yahoo first
            try {
                val data = yahooFinanceService.getHistoricalData(ticker, 5)
                if (data.isNotEmpty()) return@withContext true
            } catch (e: Exception) {
                // Try Alpha Vantage as fallback
            }

            // Try Alpha Vantage
            val data = alphaVantageService.getHistoricalData(ticker, 5)
            data.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Clear cache for a specific ticker
     */
    fun clearCache(ticker: String? = null) {
        if (ticker != null) {
            cache.keys.removeAll { it.startsWith(ticker) }
        } else {
            cache.clear()
        }
    }

    companion object {
        @Volatile
        private var instance: StockRepository? = null

        fun getInstance(): StockRepository {
            return instance ?: synchronized(this) {
                instance ?: StockRepository().also { instance = it }
            }
        }
    }
}
