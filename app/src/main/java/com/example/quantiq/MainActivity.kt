package com.example.quantiq

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.quantiq.data.models.BacktestResult
import com.example.quantiq.data.models.StrategyConfig
import com.example.quantiq.data.models.StrategyParameters
import com.example.quantiq.data.models.StrategyType
import com.example.quantiq.data.models.Trade
import com.example.quantiq.presentation.BacktestUiState
import com.example.quantiq.presentation.BacktestViewModel
import com.example.quantiq.ui.AnimationHelper
import com.example.quantiq.utils.FormatUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
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
    private lateinit var etStockTicker: MaterialAutoCompleteTextView
    private lateinit var tvStockName: TextView
    private lateinit var companyNameContainer: View
    private lateinit var chipGroupStrategy: ChipGroup
    private lateinit var chipSMA: Chip
    private lateinit var chipRSI: Chip
    private lateinit var chipMACD: Chip
    private lateinit var chipMeanReversion: Chip

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
    private lateinit var fabRunBacktest: MaterialButton
    private lateinit var progressOverlay: FrameLayout
    private lateinit var tvProgressText: TextView
    private lateinit var tvModelStatus: TextView
    private lateinit var tvModelDetails: TextView
    private lateinit var modelStatusBanner: MaterialCardView
    private lateinit var btnDownloadModel: MaterialButton
    private lateinit var btnChat: ImageButton
    private lateinit var btnSettings: ImageButton

    // Chart and Results
    private lateinit var chartCard: MaterialCardView
    private lateinit var equityChart: LineChart
    private lateinit var metricsContainer: LinearLayout
    private lateinit var scrollView: NestedScrollView
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

    // Reverse mapping for company name → ticker
    private val companyToTicker = stockSuggestions.entries.associate { (ticker, company) ->
        company.lowercase() to ticker
    }

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
        chipGroupStrategy = findViewById(R.id.chipGroupStrategy)
        chipSMA = findViewById(R.id.chipSMA)
        chipRSI = findViewById(R.id.chipRSI)
        chipMACD = findViewById(R.id.chipMACD)
        chipMeanReversion = findViewById(R.id.chipMeanReversion)

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
        scrollView = findViewById(R.id.scrollView)
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

        // Enable smooth scrolling with nice easing
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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
        // Create combined list of tickers and company names for suggestions
        val allSuggestions = stockSuggestions.keys.toList() + stockSuggestions.values.toList()
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, allSuggestions)
        etStockTicker.setAdapter(adapter)
        etStockTicker.threshold = 1

        // Add subtle focus animation to the input
        etStockTicker.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                stockInputLayout.animate()
                    .scaleX(1.01f)
                    .scaleY(1.01f)
                    .setDuration(250)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            } else {
                stockInputLayout.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(250)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()

                // When user finishes editing, convert to ticker if needed
                val input = etStockTicker.text.toString().trim()
                val ticker = findTicker(input)
                if (ticker != null && ticker != input.uppercase()) {
                    etStockTicker.setText(ticker)
                }
            }
        }

        // Handle item selection from dropdown
        etStockTicker.setOnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position) as String
            val ticker = findTicker(selected)
            if (ticker != null) {
                etStockTicker.setText(ticker)
                tvStockName.text = stockSuggestions[ticker]
                showCompanyName()
            }
        }

        etStockTicker.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().trim()

                // Just show company name below, don't modify input
                val ticker = findTicker(input)
                val companyName = if (ticker != null) stockSuggestions[ticker] else null

                if (companyName != null) {
                    tvStockName.text = companyName
                    showCompanyName()
                } else {
                    hideCompanyName()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showCompanyName() {
        // Subtle fade-in animation for company name
        if (companyNameContainer.visibility != View.VISIBLE) {
            companyNameContainer.alpha = 0f
            companyNameContainer.translationY = -10f
            companyNameContainer.visibility = View.VISIBLE
            companyNameContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(250)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }
    }

    private fun hideCompanyName() {
        // Subtle fade-out animation
        if (companyNameContainer.visibility == View.VISIBLE) {
            companyNameContainer.animate()
                .alpha(0f)
                .translationY(-10f)
                .setDuration(200)
                .withEndAction {
                    companyNameContainer.visibility = View.GONE
                }
                .start()
        }
    }

    // Helper function to find ticker from company name or ticker
    private fun findTicker(input: String): String? {
        if (input.isEmpty()) return null

        val upperInput = input.uppercase()

        // Check if it's a direct ticker match
        if (stockSuggestions.containsKey(upperInput)) {
            return upperInput
        }

        // Check if it's a company name (fuzzy match)
        val lowerInput = input.lowercase()

        // Exact company name match
        companyToTicker[lowerInput]?.let { return it }

        // Partial match - check if input matches start of company name
        companyToTicker.entries.find { (company, _) ->
            company.startsWith(lowerInput) ||
                    company.contains(lowerInput) ||
                    company.split(" ").any { it.startsWith(lowerInput) }
        }?.let { return it.value }

        return null
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

        // Show/hide parameters based on strategy with smooth animations
        chipGroupStrategy.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val checkedId = checkedIds[0]

            // Fade out current params
            val currentVisible = when {
                smaParams.visibility == View.VISIBLE -> smaParams
                rsiParams.visibility == View.VISIBLE -> rsiParams
                macdParams.visibility == View.VISIBLE -> macdParams
                meanReversionParams.visibility == View.VISIBLE -> meanReversionParams
                else -> null
            }

            val targetParams = when (checkedId) {
                R.id.chipSMA -> smaParams
                R.id.chipRSI -> rsiParams
                R.id.chipMACD -> macdParams
                R.id.chipMeanReversion -> meanReversionParams
                else -> smaParams
            }

            if (currentVisible == targetParams) return@setOnCheckedStateChangeListener

            // Animate out current
            currentVisible?.animate()
                ?.alpha(0f)
                ?.translationX(-30f)
                ?.setDuration(200)
                ?.withEndAction {
                    currentVisible.visibility = View.GONE
                    currentVisible.translationX = 0f

                    // Animate in new
                    targetParams.alpha = 0f
                    targetParams.translationX = 30f
                    targetParams.visibility = View.VISIBLE
                    targetParams.animate()
                        .alpha(1f)
                        .translationX(0f)
                        .setDuration(300)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .start()
                }
                ?.start()
        }

        // Add press animation to chips
        val chipPressAnimation: (Chip) -> Unit = { chip ->
            chip.setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        view.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .start()
                    }

                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
        }

        chipPressAnimation(chipSMA)
        chipPressAnimation(chipRSI)
        chipPressAnimation(chipMACD)
        chipPressAnimation(chipMeanReversion)

        // Add press animation to timeframe chips too
        val chip30 = findViewById<Chip>(R.id.chip30Days)
        val chip90 = findViewById<Chip>(R.id.chip90Days)
        val chip180 = findViewById<Chip>(R.id.chip180Days)
        val chip365 = findViewById<Chip>(R.id.chip365Days)

        chipPressAnimation(chip30)
        chipPressAnimation(chip90)
        chipPressAnimation(chip180)
        chipPressAnimation(chip365)
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
        // Add touch animation to run button
        fabRunBacktest.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    view.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(100)
                        .start()
                }

                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
            }
            false
        }

        fabRunBacktest.setOnClickListener {
            runBacktest()
        }

        // Compare strategies button (add functionality for future button)
        // Note: Button will be added to layout later
        // Uncomment when button is added to activity_main.xml:
        /*
        findViewById<MaterialButton>(R.id.btnCompareStrategies)?.setOnClickListener {
            val ticker = etStockTicker.text.toString().uppercase().trim()
            if (ticker.isEmpty()) {
                Toast.makeText(this, "Please enter a stock ticker", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val days = getSelectedTimeframe()
            
            val intent = android.content.Intent(this, StrategyComparisonActivity::class.java).apply {
                putExtra("ticker", ticker)
                putExtra("days", days)
            }
            startActivity(intent)
        }
        */

        btnChat = findViewById(R.id.btnChat)
        btnSettings = findViewById(R.id.btnSettings)

        // Add icon press animations
        val iconAnimator: (ImageButton) -> Unit = { button ->
            button.setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        view.animate()
                            .scaleX(0.85f)
                            .scaleY(0.85f)
                            .alpha(0.7f)
                            .setDuration(100)
                            .start()
                    }

                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
        }

        iconAnimator(btnChat)
        iconAnimator(btnSettings)

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
        return when (chipGroupStrategy.checkedChipId) {
            R.id.chipSMA -> {
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

            R.id.chipRSI -> {
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

            R.id.chipMACD -> {
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
        // Animate showing containers with fade in and slide up
        metricsContainer.alpha = 0f
        metricsContainer.translationY = 50f
        metricsContainer.visibility = View.VISIBLE
        metricsContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()

        chartCard.alpha = 0f
        chartCard.translationY = 50f
        chartCard.visibility = View.VISIBLE
        chartCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setStartDelay(100)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()

        // Update QuantScore with animation
        tvQuantScore.text = String.format("%.0f", result.quantScore)
        val rating = when {
            result.quantScore >= 80 -> "EXCELLENT"
            result.quantScore >= 60 -> "GOOD"
            result.quantScore >= 40 -> "FAIR"
            else -> "POOR"
        }
        tvQuantRating.text = rating

        // Animate QuantScore card scale
        quantScoreCard.scaleX = 0.9f
        quantScoreCard.scaleY = 0.9f
        quantScoreCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(200)
            .setInterpolator(android.view.animation.OvershootInterpolator(1.5f))
            .start()

        // Display buy-and-hold benchmark comparison
        BenchmarkHelper.displayBenchmarkComparison(metricsContainer, result, layoutInflater)

        // Update metrics with colors
        tvTotalReturn.text = FormatUtils.formatReturn(result.totalReturn)
        tvTotalReturn.setTextColor(getReturnColor(result.totalReturn))

        tvSharpe.text = String.format("%.2f", result.sharpeRatio)
        tvDrawdown.text = String.format("%.2f%%", result.maxDrawdown)
        tvVolatility.text = String.format("%.2f%%", result.volatility)
        tvWinRate.text = String.format("%.1f%%", result.winRate)
        tvTrades.text = result.totalTrades.toString()

        // Draw chart with animation
        drawEquityCurve(result.equityCurve, result.trades)

        // Smooth scroll to metrics section after a short delay
        scrollView.post {
            scrollView.smoothScrollTo(0, metricsContainer.top - 50, 800)
        }
    }

    private fun getReturnColor(returnValue: Double): Int {
        return if (returnValue >= 0) Color.parseColor("#00C851") else Color.parseColor("#FF4444")
    }

    private fun drawEquityCurve(equityCurve: List<Double>, trades: List<Trade> = emptyList()) {
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
            aiInsightsCard.alpha = 0f
            aiInsightsCard.translationY = 30f
            aiInsightsCard.visibility = View.VISIBLE
            aiInsightsCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()

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
}