package com.example.quantiq

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quantiq.data.models.BacktestContext
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatActivity : AppCompatActivity() {

    private lateinit var rvChatMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: FloatingActionButton
    private lateinit var emptyState: View

    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setupViews()
        setupRecyclerView()
        setupListeners()

        // Show welcome message with context
        showWelcomeMessage()
    }

    private fun showWelcomeMessage() {
        val context = getBacktestContext()
        val welcomeMessage = if (context.isValid()) {
            "👋 Hi! I can help you understand your recent backtest:\n\n" +
                    "📊 ${context.ticker} - ${context.strategy}\n" +
                    "📈 Return: ${"%.2f".format(context.totalReturn)}%\n" +
                    "⭐ QuantScore: ${context.quantScore.toInt()}/100\n\n" +
                    "Ask me anything about these results!"
        } else {
            "👋 Hi! I'm your QuantIQ AI assistant.\n\n" +
                    "💡 Run a backtest first, then come back to chat with me about your results!\n\n" +
                    "I can explain metrics, suggest improvements, and answer trading questions."
        }

        messages.add(ChatMessage(welcomeMessage, isUser = false))
        adapter.notifyItemInserted(messages.size - 1)
        rvChatMessages.scrollToPosition(messages.size - 1)
    }

    /**
     * Get backtest context from shared preferences
     */
    private fun getBacktestContext(): BacktestContext {
        val prefs = getSharedPreferences("quantiq_backtest_cache", MODE_PRIVATE)
        return BacktestContext(
            ticker = prefs.getString("last_ticker", "N/A") ?: "N/A",
            strategy = prefs.getString("last_strategy", "N/A") ?: "N/A",
            totalReturn = prefs.getFloat("last_total_return", 0f).toDouble(),
            sharpeRatio = prefs.getFloat("last_sharpe_ratio", 0f).toDouble(),
            maxDrawdown = prefs.getFloat("last_max_drawdown", 0f).toDouble(),
            winRate = prefs.getFloat("last_win_rate", 0f).toDouble(),
            volatility = prefs.getFloat("last_volatility", 0f).toDouble(),
            totalTrades = prefs.getInt("last_total_trades", 0),
            quantScore = prefs.getFloat("last_quant_score", 0f).toDouble(),
            timestamp = prefs.getLong("last_timestamp", 0L)
        )
    }

    private fun setupViews() {
        rvChatMessages = findViewById(R.id.rvChatMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        emptyState = findViewById(R.id.emptyState)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).apply {
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages)
        rvChatMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = this@ChatActivity.adapter
        }
    }

    private fun setupListeners() {
        btnSend.setOnClickListener {
            sendMessage()
        }

        etMessage.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }

        // Auto-scroll when keyboard appears
        etMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && messages.isNotEmpty()) {
                rvChatMessages.postDelayed({
                    rvChatMessages.smoothScrollToPosition(messages.size - 1)
                }, 300)
            }
        }
    }

    private fun sendMessage() {
        val messageText = etMessage.text.toString().trim()
        if (messageText.isEmpty()) return

        // Hide empty state
        emptyState.visibility = View.GONE

        // Add user message
        messages.add(ChatMessage(messageText, isUser = true))
        adapter.notifyItemInserted(messages.size - 1)

        // Scroll to user message immediately
        rvChatMessages.post {
            rvChatMessages.smoothScrollToPosition(messages.size - 1)
        }

        // Clear input
        etMessage.text.clear()

        // Get AI response
        getAIResponse(messageText)
    }

    private fun getAIResponse(userMessage: String) {
        lifecycleScope.launch {
            try {
                // Add placeholder for response
                val aiMessageIndex = messages.size
                messages.add(ChatMessage("💭", isUser = false))
                adapter.notifyItemInserted(aiMessageIndex)
                rvChatMessages.smoothScrollToPosition(aiMessageIndex)

                // Small delay to show "thinking" (feels more natural)
                delay(300)

                // Get backtest context
                val context = getBacktestContext()

                // Generate smart rule-based response
                val response = if (context.isValid()) {
                    getSmartResponse(userMessage.lowercase(), context)
                } else {
                    getNoContextResponse(userMessage.lowercase())
                }

                // Update with response
                messages[aiMessageIndex] = ChatMessage(response, isUser = false)
                adapter.notifyItemChanged(aiMessageIndex)
                rvChatMessages.smoothScrollToPosition(aiMessageIndex)

            } catch (e: Exception) {
                e.printStackTrace()
                val errorIndex = messages.size - 1
                if (errorIndex >= 0) {
                    messages[errorIndex] = ChatMessage(
                        "Sorry, I encountered an error. Please try again.",
                        isUser = false
                    )
                    adapter.notifyItemChanged(errorIndex)
                }
            }
        }
    }

    /**
     * Smart rule-based responses using actual backtest data
     */
    private fun getSmartResponse(question: String, context: BacktestContext): String {
        val returnPct = String.format("%.2f%%", context.totalReturn)
        val sharpe = String.format("%.2f", context.sharpeRatio)
        val drawdown = String.format("%.1f%%", context.maxDrawdown)
        val winRate = String.format("%.0f%%", context.winRate)
        val vol = String.format("%.1f%%", context.volatility)
        val score = context.quantScore.toInt()

        return when {
            // Greetings
            question.matches(Regex("^(hi|hello|hey|yo|sup).*")) -> {
                "Hi! 👋 I can help you understand your ${context.ticker} backtest using ${context.strategy}. Your QuantScore was ${score}/100. What would you like to know?"
            }

            // Return questions
            question.contains("return") || question.contains("profit") || question.contains("gain") ||
                    question.contains("money") || question.contains("earn") -> {
                val assessment = when {
                    context.totalReturn >= 20 -> "excellent 🎉"
                    context.totalReturn >= 10 -> "good 👍"
                    context.totalReturn >= 0 -> "positive ✅"
                    else -> "negative 📉"
                }
                "Your total return was **$returnPct** for ${context.ticker}, which is $assessment performance. ${
                    if (context.totalReturn >= 10) "This shows strong profitability!"
                    else if (context.totalReturn >= 0) "The strategy made money overall."
                    else "Consider adjusting parameters or trying a different strategy."
                }"
            }

            // Sharpe questions
            question.contains("sharpe") || question.contains("risk adjust") -> {
                val assessment = when {
                    context.sharpeRatio >= 2.0 -> "outstanding 🌟"
                    context.sharpeRatio >= 1.0 -> "excellent ⭐"
                    context.sharpeRatio >= 0.5 -> "acceptable ✓"
                    else -> "weak"
                }
                "Your Sharpe ratio was **$sharpe**, which is $assessment. ${
                    if (context.sharpeRatio >= 1.0) "This means you earned $sharpe units of return for every unit of risk - that's efficient!"
                    else "You're taking relatively high risk for the returns. Consider ways to reduce volatility."
                }"
            }

            // Drawdown questions  
            question.contains("drawdown") || question.contains("loss") || question.contains("decline") || question.contains(
                "worst"
            ) -> {
                val assessment = when {
                    context.maxDrawdown <= 10 -> "low 🟢"
                    context.maxDrawdown <= 20 -> "moderate 🟡"
                    context.maxDrawdown <= 30 -> "high 🟠"
                    else -> "very high 🔴"
                }
                "Your max drawdown was **$drawdown**, which is $assessment risk. This means at one point you would've been down ${
                    String.format(
                        "%.0f%%",
                        context.maxDrawdown
                    )
                } from your peak. ${
                    if (context.maxDrawdown <= 20) "Most traders can handle this level of drawdown."
                    else "This requires strong psychological tolerance and risk management."
                }"
            }

            // Win rate questions
            question.contains("win") && (question.contains("rate") || question.contains("ratio")) ||
                    question.contains("winning") || question.contains("how many") && question.contains(
                "trade"
            ) -> {
                val winningTrades = (context.winRate * context.totalTrades / 100).toInt()
                val assessment = when {
                    context.winRate >= 60 -> "excellent 🎯"
                    context.winRate >= 50 -> "balanced ⚖️"
                    context.winRate >= 40 -> "acceptable ✓"
                    else -> "concerning 📊"
                }
                "Your win rate was **$winRate** ($winningTrades out of ${context.totalTrades} trades), which is $assessment. ${
                    if (context.winRate >= 50) "You won more often than you lost - that's great!"
                    else "With this win rate, you need your winners to be significantly larger than your losers."
                }"
            }

            // Volatility questions
            question.contains("volatil") || question.contains("stable") || (question.contains("risky") || question.contains(
                "risk"
            )) && !question.contains("sharpe") -> {
                val assessment = when {
                    context.volatility <= 15 -> "low - very stable 🟢"
                    context.volatility <= 25 -> "moderate - typical 🟡"
                    context.volatility <= 35 -> "high - unpredictable 🟠"
                    else -> "very high - extremely risky 🔴"
                }
                "Your volatility was **$vol**, which is $assessment. ${
                    if (context.volatility <= 25) "This is normal for active trading strategies."
                    else "High volatility means unpredictable returns. Use smaller position sizes to manage risk."
                }"
            }

            // QuantScore questions
            question.contains("quantscore") || question.contains("score") || question.contains("rating") -> {
                val assessment = when {
                    score >= 80 -> "excellent ⭐⭐⭐"
                    score >= 60 -> "good ⭐⭐"
                    score >= 40 -> "fair ⭐"
                    else -> "weak"
                }
                "Your QuantScore was **${score}/100**, which is $assessment. This combines returns (30%), Sharpe ratio (25%), drawdown (20%), volatility (15%), and win rate (10%). ${
                    if (score >= 70) "This strategy is worth testing with real money!"
                    else if (score >= 50) "The strategy has potential but needs refinement."
                    else "Consider trying different parameters or a different strategy entirely."
                }"
            }

            // Strategy assessment questions
            question.contains("good") || question.contains("should i") || question.contains("recommend") ||
                    question.contains("worth") || question.contains("use this") -> {
                val recommendation = when {
                    score >= 80 -> "**Yes!** 🎉 Your QuantScore of ${score}/100 shows excellent potential. Consider testing with small live positions to validate these results."
                    score >= 60 -> "**Probably!** 👍 With a score of ${score}/100, this strategy has merit. Try optimizing parameters or testing different timeframes to improve it further."
                    score >= 40 -> "**Maybe.** 🤔 Your score of ${score}/100 suggests the strategy needs work. Experiment with different settings before going live."
                    else -> "**Not yet.** 📉 A score of ${score}/100 indicates this strategy underperforms. Try a completely different approach or significantly adjust parameters."
                }
                "$recommendation\n\nKey stats: Return $returnPct | Sharpe $sharpe | Drawdown $drawdown"
            }

            // Summary/overview questions
            question.contains("summar") || question.contains("overview") || question.contains("tell me about") ||
                    question.contains("results") || question.contains("how did") -> {
                """📊 **${context.ticker} Backtest Summary**

**Strategy:** ${context.strategy}
**QuantScore:** ${score}/100 ${if (score >= 70) "⭐" else ""}

**Performance:**
• Return: $returnPct ${if (context.totalReturn >= 10) "🟢" else if (context.totalReturn >= 0) "🟡" else "🔴"}
• Sharpe: $sharpe ${if (context.sharpeRatio >= 1.0) "🟢" else "🟡"}
• Drawdown: $drawdown ${if (context.maxDrawdown <= 20) "🟢" else "🟠"}

**Trading:**
• Win Rate: $winRate
• Total Trades: ${context.totalTrades}
• Volatility: $vol

${
                    if (score >= 70) "✅ Strong strategy worth further testing!"
                    else if (score >= 50) "⚠️ Has potential but needs refinement."
                    else "❌ Needs significant improvement."
                }"""
            }

            // Help/what can you do
            question.contains("help") || question.contains("what can") || question.contains("options") -> {
                """I can help you understand your backtest! Ask me:

💰 **About Returns:** "What was my return?" "Did I make money?"
📈 **About Risk:** "How's my Sharpe ratio?" "Was the drawdown bad?"
🎯 **About Performance:** "What's my win rate?" "How volatile is it?"
⭐ **Overall:** "What's my QuantScore?" "Is this strategy good?"
📊 **Summary:** "Give me an overview" "Tell me about my results"

Your ${context.ticker} backtest has a QuantScore of ${score}/100. What would you like to know?"""
            }

            // Thank you
            question.contains("thank") || question.contains("thanks") -> {
                "You're welcome! 😊 Feel free to ask me anything else about your backtest or run a new one to compare strategies!"
            }

            // Default - show key stats and prompt
            else -> {
                "I'm not sure what you're asking about. Here are your key results:\n\n" +
                        "• Return: $returnPct\n" +
                        "• Sharpe: $sharpe\n" +
                        "• QuantScore: ${score}/100\n\n" +
                        "Try asking: \"Is this strategy good?\" or \"What was my return?\" or \"Explain my Sharpe ratio\""
            }
        }
    }

    /**
     * Response when no backtest has been run
     */
    private fun getNoContextResponse(question: String): String {
        return when {
            question.matches(Regex("^(hi|hello|hey|yo|sup).*")) -> {
                "Hi! 👋 I'm your QuantIQ assistant. I can help you understand backtesting results, but you need to run a backtest first!\n\nGo back to the main screen, pick a stock and strategy, then tap \"Run Analysis\". Come back here and I'll explain your results! 📈"
            }

            question.contains("help") || question.contains("what can") -> {
                """I can help you understand backtesting! 🎯

**First:** Run a backtest from the main screen
**Then ask me:**
• "What was my return?"
• "Is my strategy good?"
• "Explain my Sharpe ratio"
• "Should I use this strategy?"

I'll analyze YOUR specific results with personalized insights! 📊"""
            }

            question.contains("return") -> "Total Return measures profit/loss as a percentage. Values above 10% are generally good. **Run a backtest first** so I can tell you YOUR specific return! 📈"

            question.contains("sharpe") -> "Sharpe Ratio measures risk-adjusted returns. Above 1.0 is good, above 2.0 is excellent. **Run a backtest first** to see your Sharpe ratio! ⭐"

            question.contains("drawdown") -> "Max Drawdown shows the worst peak-to-trough decline. Under 20% is manageable for most traders. **Run a backtest** to see your drawdown! 📉"

            question.contains("quantscore") -> "QuantScore is a 0-100 rating combining all metrics. 80+ is excellent, 60+ is good. **Run a backtest** to get your score! 🎯"

            else -> {
                "To get personalized insights, please **run a backtest first** from the main screen! 🚀\n\nPick a stock (like AAPL), choose a strategy (SMA, RSI, MACD, or Mean Reversion), then tap \"Run Analysis\". Come back here and ask me anything about your results!"
            }
        }
    }

    // Simple Adapter for Chat Messages
    class ChatAdapter(private val messages: MutableList<ChatMessage>) :
        RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

        class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val userMessageCard: View = view.findViewById(R.id.userMessageCard)
            val aiMessageLayout: View = view.findViewById(R.id.aiMessageLayout)
            val tvUserMessage: android.widget.TextView = view.findViewById(R.id.tvUserMessage)
            val tvAiMessage: android.widget.TextView = view.findViewById(R.id.tvAiMessage)
        }

        override fun onCreateViewHolder(
            parent: android.view.ViewGroup,
            viewType: Int
        ): ChatViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_message, parent, false)
            return ChatViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            val message = messages[position]

            if (message.isUser) {
                holder.userMessageCard.visibility = View.VISIBLE
                holder.aiMessageLayout.visibility = View.GONE
                holder.tvUserMessage.text = message.text
            } else {
                holder.userMessageCard.visibility = View.GONE
                holder.aiMessageLayout.visibility = View.VISIBLE
                holder.tvAiMessage.text = message.text
            }
        }

        override fun getItemCount() = messages.size
    }
}