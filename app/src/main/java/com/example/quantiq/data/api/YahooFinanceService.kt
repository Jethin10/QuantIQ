package com.example.quantiq.data.api

import com.example.quantiq.data.models.StockData
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

interface YahooFinanceApi {
    @GET("v8/finance/chart/{symbol}")
    suspend fun getHistoricalData(
        @retrofit2.http.Path("symbol") symbol: String,
        @Query("period1") period1: Long,
        @Query("period2") period2: Long,
        @Query("interval") interval: String = "1d"
    ): JsonObject
}

class YahooFinanceService {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://query1.finance.yahoo.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(YahooFinanceApi::class.java)

    /**
     * Fetch historical stock data from Yahoo Finance
     * @param symbol Stock ticker symbol (e.g., "AAPL", "TSLA")
     * @param days Number of days of historical data to fetch
     * @return List of StockData objects
     */
    suspend fun getHistoricalData(symbol: String, days: Int = 365): List<StockData> {
        try {
            val endTime = System.currentTimeMillis() / 1000
            val startTime = endTime - (days * 24 * 60 * 60)

            val response = api.getHistoricalData(
                symbol = symbol,
                period1 = startTime,
                period2 = endTime,
                interval = "1d"
            )

            return parseYahooResponse(response)
        } catch (e: Exception) {
            throw Exception("Failed to fetch data for $symbol: ${e.message}")
        }
    }

    private fun parseYahooResponse(response: JsonObject): List<StockData> {
        val stockDataList = mutableListOf<StockData>()

        try {
            val chart = response.getAsJsonObject("chart")
            val result = chart.getAsJsonArray("result").get(0).asJsonObject
            val timestamps = result.getAsJsonArray("timestamp")
            val indicators = result.getAsJsonObject("indicators")
            val quote = indicators.getAsJsonArray("quote").get(0).asJsonObject

            val opens = quote.getAsJsonArray("open")
            val highs = quote.getAsJsonArray("high")
            val lows = quote.getAsJsonArray("low")
            val closes = quote.getAsJsonArray("close")
            val volumes = quote.getAsJsonArray("volume")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            for (i in 0 until timestamps.size()) {
                try {
                    val timestamp = timestamps.get(i).asLong * 1000
                    val date = dateFormat.format(Date(timestamp))

                    val open = opens.get(i).let { if (it.isJsonNull) 0.0 else it.asDouble }
                    val high = highs.get(i).let { if (it.isJsonNull) 0.0 else it.asDouble }
                    val low = lows.get(i).let { if (it.isJsonNull) 0.0 else it.asDouble }
                    val close = closes.get(i).let { if (it.isJsonNull) 0.0 else it.asDouble }
                    val volume = volumes.get(i).let { if (it.isJsonNull) 0L else it.asLong }

                    // Skip entries with invalid data
                    if (close > 0) {
                        stockDataList.add(
                            StockData(
                                date = date,
                                open = open,
                                high = high,
                                low = low,
                                close = close,
                                volume = volume
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Skip invalid entries
                    continue
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to parse Yahoo Finance response: ${e.message}")
        }

        return stockDataList
    }

    companion object {
        @Volatile
        private var instance: YahooFinanceService? = null

        fun getInstance(): YahooFinanceService {
            return instance ?: synchronized(this) {
                instance ?: YahooFinanceService().also { instance = it }
            }
        }
    }
}
