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

interface AlphaVantageApi {
    @GET("query")
    suspend fun getTimeSeriesDaily(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("apikey") apikey: String,
        @Query("outputsize") outputsize: String = "full"
    ): JsonObject
}

class AlphaVantageService {

    // Free API key (limited to 25 requests/day)
    // For production, users should provide their own key from alphavantage.co
    private val API_KEY = "demo"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.alphavantage.co/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(AlphaVantageApi::class.java)

    /**
     * Fetch historical stock data from Alpha Vantage
     * @param symbol Stock ticker symbol (e.g., "AAPL", "TSLA")
     * @param days Number of days of historical data to fetch
     * @return List of StockData objects
     */
    suspend fun getHistoricalData(symbol: String, days: Int = 365): List<StockData> {
        try {
            val response = api.getTimeSeriesDaily(
                symbol = symbol,
                apikey = API_KEY,
                outputsize = if (days > 100) "full" else "compact"
            )

            return parseAlphaVantageResponse(response, days)
        } catch (e: Exception) {
            throw Exception("Failed to fetch data from Alpha Vantage for $symbol: ${e.message}")
        }
    }

    private fun parseAlphaVantageResponse(response: JsonObject, days: Int): List<StockData> {
        val stockDataList = mutableListOf<StockData>()

        try {
            // Check for error message
            if (response.has("Error Message")) {
                throw Exception("Invalid ticker symbol")
            }

            if (response.has("Note")) {
                throw Exception("API call limit reached. Please try again later.")
            }

            val timeSeries = response.getAsJsonObject("Time Series (Daily)")
                ?: throw Exception("Invalid response format")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val cutoffDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -days)
            }

            // Parse each day's data
            for ((dateStr, dataObj) in timeSeries.entrySet()) {
                try {
                    val date = dateFormat.parse(dateStr) ?: continue

                    // Skip dates older than requested days
                    if (date.before(cutoffDate.time)) continue

                    val data = dataObj.asJsonObject
                    val open = data.get("1. open").asDouble
                    val high = data.get("2. high").asDouble
                    val low = data.get("3. low").asDouble
                    val close = data.get("4. close").asDouble
                    val volume = data.get("5. volume").asLong

                    stockDataList.add(
                        StockData(
                            date = dateStr,
                            open = open,
                            high = high,
                            low = low,
                            close = close,
                            volume = volume
                        )
                    )
                } catch (e: Exception) {
                    // Skip invalid entries
                    continue
                }
            }

            // Sort by date ascending (oldest first)
            stockDataList.sortBy { it.date }

        } catch (e: Exception) {
            throw Exception("Failed to parse Alpha Vantage response: ${e.message}")
        }

        return stockDataList
    }

    companion object {
        @Volatile
        private var instance: AlphaVantageService? = null

        fun getInstance(): AlphaVantageService {
            return instance ?: synchronized(this) {
                instance ?: AlphaVantageService().also { instance = it }
            }
        }
    }
}
