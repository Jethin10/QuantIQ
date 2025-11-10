package com.example.quantiq

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.quantiq.data.models.BacktestResult
import com.example.quantiq.data.models.StrategyConfig
import com.example.quantiq.data.models.StrategyParameters
import com.example.quantiq.data.models.StrategyType
import com.example.quantiq.presentation.BacktestUiState
import com.example.quantiq.presentation.BacktestViewModel
import com.example.quantiq.ui.AnimationHelper
import com.example.quantiq.utils.FormatUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.public.extensions.addModelFromURL
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var stockInputLayout: TextInputLayout
    private lateinit var etStockTicker: AutoCompleteTextView
    private lateinit var tvStockName: TextView
    private lateinit var companyNameContainer: View
    private lateinit var radioGroupStrategy: RadioGroup
    private lateinit var radioSMA: MaterialRadioButton
    private lateinit var radioRSI: MaterialRadioButton
    private lateinit var radioMACD: MaterialRadioButton
    private lateinit var radioMeanReversion: MaterialRadioButton

    // Strategy Parameters
    private lateinit var smaParams: LinearLayout
    private lateinit var rsiParams: LinearLayout
    private lateinit var sliderSmaShort: Slider
    private lateinit var sliderSmaLong: Slider
    private lateinit var tvSmaShort: TextView
    private lateinit var tvSmaLong: TextView
    private lateinit var sliderRsiPeriod: Slider
    private lateinit var tvRsiPeriod: TextView
    private lateinit var sliderRsiOverbought: Slider
    private lateinit var tvRsiOverbought: TextView
    private lateinit var sliderRsiOversold: Slider
    private lateinit var tvRsiOversold: TextView

    // MACD Parameters
    private lateinit var macdParams: LinearLayout
    private lateinit var sliderMacdFast: Slider
    private lateinit var tvMacdFast: TextView
    private lateinit var sliderMacdSlow: Slider
    private lateinit var tvMacdSlow: TextView
    private lateinit var sliderMacdSignal: Slider
    private lateinit var tvMacdSignal: TextView

    // Mean Reversion Parameters
    private lateinit var meanReversionParams: LinearLayout
    private lateinit var sliderMeanReversionThreshold: Slider
    private lateinit var tvMeanReversionThreshold: TextView
    private lateinit var sliderMeanReversionPeriod: Slider
    private lateinit var tvMeanReversionPeriod: TextView

    private lateinit var chipGroupTimeframe: ChipGroup
    private lateinit var fabRunBacktest: ExtendedFloatingActionButton
    private lateinit var progressOverlay: FrameLayout
    private lateinit var tvProgressText: TextView
    private lateinit var tvModelStatus: TextView
    private lateinit var tvModelDetails: TextView
    private lateinit var modelStatusBanner: MaterialCardView
    private lateinit var btnDownloadModel: com.google.android.material.button.MaterialButton
    private lateinit var btnChat: ImageButton
    private lateinit var btnSettings: ImageButton

    // Chart and Results
    private lateinit var chartCard: MaterialCardView
    private lateinit var equityChart: LineChart
    private lateinit var metricsContainer: LinearLayout
    private lateinit var quantScoreCard: MaterialCardView
    private lateinit var tvQuantScore: TextView
    private lateinit var tvQuantRating: TextView
    private lateinit var tvTotalReturn: TextView
    private lateinit var tvSharpe: TextView
    private lateinit var tvDrawdown: TextView
    private lateinit var tvVolatility: TextView
    private lateinit var tvWinRate: TextView
    private lateinit var tvTrades: TextView
    private lateinit var aiInsightsCard: MaterialCardView
    private lateinit var tvAiInsights: TextView

    // ViewModel
    private lateinit var viewModel: BacktestViewModel

    // AI Model state
    private var isModelLoaded = false
    private var currentModelId: String? = null
    private var lastBacktestResult: BacktestResult? = null

    // Stock suggestions with full names
    private val stockSuggestions = mapOf(
        "AAPL" to "Apple Inc.",
        "GOOGL" to "Alphabet Inc.",
        "MSFT" to "Microsoft Corporation",
        "AMZN" to "Amazon.com Inc.",
        "TSLA" to "Tesla Inc.",
        "META" to "Meta Platforms Inc.",
        "NVDA" to "NVIDIA Corporation",
        "NFLX" to "Netflix Inc.",
        "AMD" to "Advanced Micro Devices",
        "INTC" to "Intel Corporation",
        "DIS" to "Walt Disney Company",
        "PYPL" to "PayPal Holdings",
        "COIN" to "Coinbase Global Inc.",
        "SQ" to "Block Inc.",
        "UBER" to "Uber Technologies",
        "ABNB" to "Airbnb Inc.",
        "SNAP" to "Snap Inc.",
        "SPOT" to "Spotify Technology",
        "ROKU" to "Roku Inc.",
        "ZM" to "Zoom Video Communications"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[BacktestViewModel::class.java]

        // Initialize views
        initializeViews()

        // Setup stock autocomplete
        setupStockAutocomplete()

        // Setup strategy customization
        setupStrategyCustomization()

        // Initialize SDK
        initializeSDK()

        // Setup listeners
        setupListeners()

        // Observe ViewModel state
        observeBacktestState()
    }

    private fun initializeViews() {
        // Stock input
        stockInputLayout = findViewById(R.id.stockInputLayout)
        etStockTicker = findViewById(R.id.etStockTicker)
        tvStockName = findViewById(R.id.tvStockName)
        companyNameContainer = findViewById(R.id.companyNameContainer)

        // Strategy selection
        radioGroupStrategy = findViewById(R.id.radioGroupStrategy)
        radioSMA = findViewById(R.id.radioSMA)
        radioRSI = findViewById(R.id.radioRSI)
        radioMACD = findViewById(R.id.radioMACD)
        radioMeanReversion = findViewById(R.id.radioMeanReversion)

        // Strategy parameters
        smaParams = findViewById(R.id.smaParams)
        rsiParams = findViewById(R.id.rsiParams)
        sliderSmaShort = findViewById(R.id.sliderSmaShort)
        sliderSmaLong = findViewById(R.id.sliderSmaLong)
        tvSmaShort = findViewById(R.id.tvSmaShort)
        tvSmaLong = findViewById(R.id.tvSmaLong)
        sliderRsiPeriod = findViewById(R.id.sliderRsiPeriod)
        tvRsiPeriod = findViewById(R.id.tvRsiPeriod)
        sliderRsiOverbought = findViewById(R.id.sliderRsiOverbought)
        tvRsiOverbought = findViewById(R.id.tvRsiOverbought)
        sliderRsiOversold = findViewById(R.id.sliderRsiOversold)
        tvRsiOversold = findViewById(R.id.tvRsiOversold)

        // MACD Parameters
        macdParams = findViewById(R.id.macdParams)
        sliderMacdFast = findViewById(R.id.sliderMacdFast)
        tvMacdFast = findViewById(R.id.tvMacdFast)
        sliderMacdSlow = findViewById(R.id.sliderMacdSlow)
        tvMacdSlow = findViewById(R.id.tvMacdSlow)
        sliderMacdSignal = findViewById(R.id.sliderMacdSignal)
        tvMacdSignal = findViewById(R.id.tvMacdSignal)

        // Mean Reversion Parameters
        meanReversionParams = findViewById(R.id.meanReversionParams)
        sliderMeanReversionThreshold = findViewById(R.id.sliderMeanReversionThreshold)
        tvMeanReversionThreshold = findViewById(R.id.tvMeanReversionThreshold)
        sliderMeanReversionPeriod = findViewById(R.id.sliderMeanReversionPeriod)
        tvMeanReversionPeriod = findViewById(R.id.tvMeanReversionPeriod)

        // Timeframe
        chipGroupTimeframe = findViewById(R.id.chipGroupTimeframe)

        // FAB and progress
        fabRunBacktest = findViewById(R.id.fabRunBacktest)
        progressOverlay = findViewById(R.id.progressOverlay)
        tvProgressText = findViewById(R.id.tvProgressText)

        // Model status
        tvModelStatus = findViewById(R.id.tvModelStatus)
        tvModelDetails = findViewById(R.id.tvModelDetails)
        modelStatusBanner = findViewById(R.id.modelStatusBanner)
        btnDownloadModel = findViewById(R.id.btnDownloadModel)

        // Chart and results
        chartCard = findViewById(R.id.chartCard)
        equityChart = findViewById(R.id.equityChart)
        metricsContainer = findViewById(R.id.metricsContainer)
        quantScoreCard = findViewById(R.id.quantScoreCard)
        tvQuantScore = findViewById(R.id.tvQuantScore)
        tvQuantRating = findViewById(R.id.tvQuantRating)
        tvTotalReturn = findViewById(R.id.tvTotalReturn)
        tvSharpe = findViewById(R.id.tvSharpe)
        tvDrawdown = findViewById(R.id.tvDrawdown)
        tvVolatility = findViewById(R.id.tvVolatility)
        tvWinRate = findViewById(R.id.tvWinRate)
        tvTrades = findViewById(R.id.tvTrades)
        aiInsightsCard = findViewById(R.id.aiInsightsCard)
        tvAiInsights = findViewById(R.id.tvAiInsights)

        // Set default stock
        etStockTicker.setText("AAPL")
        tvStockName.text = stockSuggestions["AAPL"]
        companyNameContainer.visibility = View.VISIBLE

        // Check if model was loaded in splash
        isModelLoaded = intent.getBooleanExtra("modelLoaded", false)
        updateModelStatus()
    }

    private fun updateModelStatus() {
        if (isModelLoaded) {
            tvModelStatus.text = "AI Model: Ready "
            tvModelDetails.text = "SmolLM2 360M loaded"
            modelStatusBanner.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
            btnDownloadModel.visibility = View.GONE
        } else {
            tvModelStatus.text = "AI Model: Not loaded"
            tvModelDetails.text = "Tap to download for AI features"
            modelStatusBanner.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
            btnDownloadModel.visibility = View.VISIBLE
            btnDownloadModel.text = "Download"
            btnDownloadModel.setOnClickListener {
                downloadAndLoadModel()
            }
        }
    }

    private fun setupStockAutocomplete() {
        val tickers = stockSuggestions.keys.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tickers)
        etStockTicker.setAdapter(adapter)
        etStockTicker.threshold = 1

        etStockTicker.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ticker = s.toString().uppercase()
                val companyName = stockSuggestions[ticker]
                if (companyName != null) {
                    tvStockName.text = companyName
                    companyNameContainer.visibility = View.VISIBLE
                } else {
                    companyNameContainer.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupStrategyCustomization() {
        // SMA sliders
        sliderSmaShort.addOnChangeListener { _, value, _ ->
            tvSmaShort.text = value.toInt().toString()
        }
        sliderSmaLong.addOnChangeListener { _, value, _ ->
            tvSmaLong.text = value.toInt().toString()
        }

        // RSI sliders
        sliderRsiPeriod.addOnChangeListener { _, value, _ ->
            tvRsiPeriod.text = value.toInt().toString()
        }
        sliderRsiOverbought.addOnChangeListener { _, value, _ ->
            tvRsiOverbought.text = value.toInt().toString()
        }
        sliderRsiOversold.addOnChangeListener { _, value, _ ->
            tvRsiOversold.text = value.toInt().toString()
        }

        // MACD sliders
        sliderMacdFast.addOnChangeListener { _, value, _ ->
            tvMacdFast.text = value.toInt().toString()
        }
        sliderMacdSlow.addOnChangeListener { _, value, _ ->
            tvMacdSlow.text = value.toInt().toString()
        }
        sliderMacdSignal.addOnChangeListener { _, value, _ ->
            tvMacdSignal.text = value.toInt().toString()
        }

        // Mean Reversion sliders
        sliderMeanReversionThreshold.addOnChangeListener { _, value, _ ->
            tvMeanReversionThreshold.text = value.toInt().toString()
        }
        sliderMeanReversionPeriod.addOnChangeListener { _, value, _ ->
            tvMeanReversionPeriod.text = value.toInt().toString()
        }

        // Show/hide parameters based on strategy
        radioGroupStrategy.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioSMA -> {
                    smaParams.visibility = View.VISIBLE
                    rsiParams.visibility = View.GONE
                    macdParams.visibility = View.GONE
                    meanReversionParams.visibility = View.GONE
                }

                R.id.radioRSI -> {
                    smaParams.visibility = View.GONE
                    rsiParams.visibility = View.VISIBLE
                    macdParams.visibility = View.GONE
                    meanReversionParams.visibility = View.GONE
                }

                R.id.radioMACD -> {
                    smaParams.visibility = View.GONE
                    rsiParams.visibility = View.GONE
                    macdParams.visibility = View.VISIBLE
                    meanReversionParams.visibility = View.GONE
                }

                else -> {
                    smaParams.visibility = View.GONE
                    rsiParams.visibility = View.GONE
                    macdParams.visibility = View.GONE
                    meanReversionParams.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initializeSDK() {
        lifecycleScope.launch {
            try {
                tvModelStatus.text = "Initializing QuantIQ..."
                tvModelDetails.text = "AI Model: Initializing..."

                withContext(Dispatchers.IO) {
                    try {
                        RunAnywhere.initialize(
                            context = this@MainActivity,
                            apiKey = "dev",
                            environment = SDKEnvironment.DEVELOPMENT
                        )
                        LlamaCppServiceProvider.register()
                        registerModels()
                        RunAnywhere.scanForDownloadedModels()

                        // Check if model is already downloaded
                        val models = com.runanywhere.sdk.public.extensions.listAvailableModels()
                        if (models.isNotEmpty() && models.first().isDownloaded) {
                            currentModelId = models.first().id
                            RunAnywhere.loadModel(currentModelId!!)
                            withContext(Dispatchers.Main) {
                                isModelLoaded = true
                                updateModelStatus()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                tvModelStatus.text = "Ready to backtest"
                if (!isModelLoaded) {
                    tvModelDetails.text = "AI Model: Not loaded (optional)"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                tvModelStatus.text = "Ready to backtest"
                tvModelDetails.text = "AI features unavailable"
            }
        }
    }

    private suspend fun registerModels() {
        addModelFromURL(
            url = "https://huggingface.co/prithivMLmods/SmolLM2-360M-GGUF/resolve/main/SmolLM2-360M.Q8_0.gguf",
            name = "SmolLM2 360M Q8_0",
            type = "LLM"
        )
    }

    private fun setupListeners() {
        fabRunBacktest.setOnClickListener {
            runBacktest()
        }

        btnChat = findViewById(R.id.btnChat)
        btnSettings = findViewById(R.id.btnSettings)

        btnChat.setOnClickListener {
            if (!isModelLoaded) {
                Toast.makeText(this, "Please download AI model first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = android.content.Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            showSettingsDialog()
        }

        // Make metric cards clickable for AI explanations
        setupMetricCardListeners()
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Initialize dialog views
        val etInitialCapital =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etInitialCapital)
        val sliderPositionSize = dialogView.findViewById<Slider>(R.id.sliderPositionSize)
        val tvPositionSize = dialogView.findViewById<TextView>(R.id.tvPositionSize)
        val btnSave =
            dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)
        val btnCancel =
            dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)

        sliderPositionSize.addOnChangeListener { _, value, _ ->
            tvPositionSize.text = "${value.toInt()}%"
        }

        btnSave.setOnClickListener {
            val capital = etInitialCapital.text.toString()
            Toast.makeText(this, "Settings saved: $$capital", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun downloadAndLoadModel() {
        lifecycleScope.launch {
            try {
                btnDownloadModel.isEnabled = false
                btnDownloadModel.text = "Downloading..."
                tvModelStatus.text = "Downloading AI Model..."
                tvModelDetails.text = "This may take 2-5 minutes"

                progressOverlay.visibility = View.VISIBLE
                tvProgressText.text = "Downloading AI model..."

                withContext(Dispatchers.IO) {
                    val models = com.runanywhere.sdk.public.extensions.listAvailableModels()

                    if (models.isEmpty()) {
                        throw Exception("No models available")
                    }

                    val model = models.first()
                    currentModelId = model.id

                    if (!model.isDownloaded) {
                        RunAnywhere.downloadModel(model.id).collect { progress ->
                            val percentage = (progress * 100).toInt()
                            withContext(Dispatchers.Main) {
                                tvProgressText.text = "Downloading: $percentage%"
                                tvModelDetails.text = "Downloaded: $percentage%"
                            }
                        }
                    }

                    RunAnywhere.loadModel(model.id)
                }

                withContext(Dispatchers.Main) {
                    isModelLoaded = true
                    progressOverlay.visibility = View.GONE
                    updateModelStatus()
                    Toast.makeText(
                        this@MainActivity,
                        "AI Model loaded successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressOverlay.visibility = View.GONE
                    btnDownloadModel.isEnabled = true
                    btnDownloadModel.text = "Retry"
                    tvModelStatus.text = "Download failed"
                    tvModelDetails.text = "Tap to retry: ${e.message}"
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun setupMetricCardListeners() {
        quantScoreCard.setOnClickListener { explainMetric("QuantScore") }
        (tvTotalReturn.parent as View).setOnClickListener { explainMetric("Total Return") }
        (tvSharpe.parent as View).setOnClickListener { explainMetric("Sharpe Ratio") }
        (tvDrawdown.parent as View).setOnClickListener { explainMetric("Max Drawdown") }
        (tvVolatility.parent as View).setOnClickListener { explainMetric("Volatility") }
        (tvWinRate.parent as View).setOnClickListener { explainMetric("Win Rate") }
    }

    private fun runBacktest() {
        val ticker = etStockTicker.text.toString().uppercase().trim()
        if (ticker.isEmpty()) {
            Toast.makeText(this, "Please enter a stock ticker", Toast.LENGTH_SHORT).show()
            return
        }

        val strategyConfig = getSelectedStrategy()
        val days = getSelectedTimeframe()

        // Clear previous results
        metricsContainer.visibility = View.GONE
        chartCard.visibility = View.GONE
        aiInsightsCard.visibility = View.GONE

        viewModel.runBacktest(ticker, strategyConfig.name, days)
    }

    private fun getSelectedStrategy(): StrategyConfig {
        return when (radioGroupStrategy.checkedRadioButtonId) {
            R.id.radioSMA -> {
                val shortPeriod = sliderSmaShort.value.toInt()
                val longPeriod = sliderSmaLong.value.toInt()
                StrategyConfig(
                    type = StrategyType.SMA_CROSSOVER,
                    name = "SMA Crossover",
                    description = "SMA Crossover ($shortPeriod/$longPeriod)",
                    parameters = StrategyParameters.SMAParameters(
                        shortPeriod = shortPeriod,
                        longPeriod = longPeriod
                    )
                )
            }

            R.id.radioRSI -> {
                val period = sliderRsiPeriod.value.toInt()
                val overbought = sliderRsiOverbought.value.toInt()
                val oversold = sliderRsiOversold.value.toInt()
                StrategyConfig(
                    type = StrategyType.RSI,
                    name = "RSI Strategy",
                    description = "RSI Strategy ($period, $oversold/$overbought)",
                    parameters = StrategyParameters.RSIParameters(
                        period = period,
                        overbought = overbought,
                        oversold = oversold
                    )
                )
            }

            R.id.radioMACD -> {
                val fast = sliderMacdFast.value.toInt()
                val slow = sliderMacdSlow.value.toInt()
                val signal = sliderMacdSignal.value.toInt()
                StrategyConfig(
                    type = StrategyType.MACD,
                    name = "MACD Strategy",
                    description = "MACD Strategy ($fast/$slow/$signal)",
                    parameters = StrategyParameters.MACDParameters(
                        fastPeriod = fast,
                        slowPeriod = slow,
                        signalPeriod = signal
                    )
                )
            }

            else -> {
                val threshold = sliderMeanReversionThreshold.value
                val period = sliderMeanReversionPeriod.value.toInt()
                StrategyConfig(
                    type = StrategyType.MEAN_REVERSION,
                    name = "Mean Reversion",
                    description = "Mean Reversion ($threshold/$period)",
                    parameters = StrategyParameters.MeanReversionParameters(
                        period = period,
                        stdDevMultiplier = threshold.toDouble()
                    )
                )
            }
        }
    }

    private fun getSelectedTimeframe(): Int {
        return when (chipGroupTimeframe.checkedChipId) {
            R.id.chip30Days -> 30
            R.id.chip90Days -> 90
            R.id.chip180Days -> 180
            R.id.chip365Days -> 365
            else -> 90
        }
    }

    private fun observeBacktestState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is BacktestUiState.Idle -> {
                        progressOverlay.visibility = View.GONE
                        fabRunBacktest.isEnabled = true
                    }
                    is BacktestUiState.Loading -> {
                        progressOverlay.visibility = View.VISIBLE
                        tvProgressText.text = state.message
                        fabRunBacktest.isEnabled = false
                        tvModelStatus.text = state.message
                    }
                    is BacktestUiState.Success -> {
                        progressOverlay.visibility = View.GONE
                        fabRunBacktest.isEnabled = true
                        lastBacktestResult = state.result
                        displayBacktestResults(state.result)
                        tvModelStatus.text = "Backtest complete!"
                    }
                    is BacktestUiState.Error -> {
                        progressOverlay.visibility = View.GONE
                        fabRunBacktest.isEnabled = true
                        tvModelStatus.text = "Error: ${state.message}"
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun displayBacktestResults(result: BacktestResult) {
        // Show containers
        metricsContainer.visibility = View.VISIBLE
        chartCard.visibility = View.VISIBLE

        // Update QuantScore
        tvQuantScore.text = String.format("%.0f", result.quantScore)
        val rating = when {
            result.quantScore >= 80 -> "EXCELLENT "
            result.quantScore >= 60 -> "GOOD "
            result.quantScore >= 40 -> "FAIR "
            else -> "POOR "
        }
        tvQuantRating.text = rating

        // Update metrics with colors
        tvTotalReturn.text = FormatUtils.formatReturn(result.totalReturn)
        tvTotalReturn.setTextColor(getReturnColor(result.totalReturn))

        tvSharpe.text = String.format("%.2f", result.sharpeRatio)
        tvDrawdown.text = String.format("%.2f%%", result.maxDrawdown)
        tvVolatility.text = String.format("%.2f%%", result.volatility)
        tvWinRate.text = String.format("%.1f%%", result.winRate)
        tvTrades.text = result.totalTrades.toString()

        // Draw chart
        drawEquityCurve(result.equityCurve)

        // Auto-generate AI insights if model is loaded
        if (isModelLoaded) {
            generateAIInsights(result)
        }
    }

    private fun getReturnColor(returnValue: Double): Int {
        return if (returnValue >= 0) Color.parseColor("#00C851") else Color.parseColor("#FF4444")
    }

    private fun drawEquityCurve(equityCurve: List<Double>) {
        val entries = equityCurve.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val dataSet = LineDataSet(entries, "Portfolio Value").apply {
            color = Color.parseColor("#00BFA5")
            setDrawCircles(false)
            lineWidth = 2.5f
            setDrawFilled(true)
            fillColor = Color.parseColor("#00BFA5")
            fillAlpha = 50
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(dataSet)
        equityChart.data = lineData

        // Customize chart
        equityChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = Color.GRAY
            }

            axisLeft.apply {
                setDrawGridLines(true)
                textColor = Color.GRAY
            }

            axisRight.isEnabled = false

            animateX(1000)
            invalidate()
        }
    }

    private fun explainMetric(metricName: String) {
        if (!isModelLoaded) {
            Toast.makeText(
                this,
                "AI model not loaded. Load model for explanations.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val result = lastBacktestResult ?: return

        lifecycleScope.launch {
            aiInsightsCard.visibility = View.VISIBLE
            tvAiInsights.text = "AI is analyzing $metricName..."

            val metricValue = when (metricName) {
                "QuantScore" -> "${result.quantScore.toInt()}/100"
                "Total Return" -> String.format("%.2f%%", result.totalReturn)
                "Sharpe Ratio" -> String.format("%.2f", result.sharpeRatio)
                "Max Drawdown" -> String.format("%.2f%%", result.maxDrawdown)
                "Volatility" -> String.format("%.2f%%", result.volatility)
                "Win Rate" -> String.format("%.1f%%", result.winRate)
                else -> "N/A"
            }

            // Simple, direct prompt
            val prompt =
                "Explain $metricName in trading. ${result.ticker} scored $metricValue. Is this good or bad? Answer in 2-3 sentences."

            var response = ""
            try {
                RunAnywhere.generateStream(prompt).collect { token ->
                    response += token
                    tvAiInsights.text = response
                }
            } catch (e: Exception) {
                tvAiInsights.text = "Error: ${e.message}"
            }
        }
    }

    private fun generateAIInsights(result: BacktestResult) {
        lifecycleScope.launch {
            aiInsightsCard.visibility = View.VISIBLE
            tvAiInsights.text = "AI is analyzing your backtest..."

            // Simple, direct prompt
            val prompt = """Analyze this backtest result in 3 sentences:
${result.ticker} with ${result.strategy}
Returns: ${String.format("%.1f%%", result.totalReturn)}, Sharpe: ${
                String.format(
                    "%.2f",
                    result.sharpeRatio
                )
            }, Drawdown: ${String.format("%.1f%%", result.maxDrawdown)}
Is this strategy good? Why or why not?"""

            var response = ""
            try {
                RunAnywhere.generateStream(prompt).collect { token ->
                    response += token
                    tvAiInsights.text = response
                }
            } catch (e: Exception) {
                tvAiInsights.text = "AI insights unavailable: ${e.message}"
            }
        }
    }
}