package com.example.quantiq

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
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

        // Check if model is available
        checkModelAvailability()
    }

    private fun checkModelAvailability() {
        lifecycleScope.launch {
            try {
                // Try to get available models
                val models = listAvailableModels()
                if (models.isEmpty() || !models.first().isDownloaded) {
                    // Show warning in chat
                    messages.add(
                        ChatMessage(
                            "⚠️ AI model not found. Please go back to the main screen and tap 'Download' to load the AI model first.",
                            isUser = false
                        )
                    )
                    adapter.notifyItemInserted(0)
                    btnSend.isEnabled = false
                    btnSend.alpha = 0.5f
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Show error in chat
                messages.add(
                    ChatMessage(
                        "⚠️ Unable to check AI model status. Error: ${e.message}",
                        isUser = false
                    )
                )
                adapter.notifyItemInserted(0)
            }
        }
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
    }

    private fun sendMessage() {
        val messageText = etMessage.text.toString().trim()
        if (messageText.isEmpty()) return

        // Hide empty state
        emptyState.visibility = View.GONE

        // Add user message
        messages.add(ChatMessage(messageText, isUser = true))
        adapter.notifyItemInserted(messages.size - 1)
        rvChatMessages.smoothScrollToPosition(messages.size - 1)

        // Clear input
        etMessage.text.clear()

        // Get AI response
        getAIResponse(messageText)
    }

    private fun getAIResponse(userMessage: String) {
        lifecycleScope.launch {
            try {
                // Add placeholder for AI response
                val aiMessageIndex = messages.size
                messages.add(ChatMessage("Thinking...", isUser = false))
                adapter.notifyItemInserted(aiMessageIndex)
                rvChatMessages.smoothScrollToPosition(aiMessageIndex)

                var response = ""

                // Simple, direct prompt - no templates
                val prompt =
                    "You are a helpful trading assistant. Answer this question briefly (2-3 sentences):\n\n$userMessage"

                try {
                    RunAnywhere.generateStream(prompt).collect { token ->
                        response += token
                        messages[aiMessageIndex] = ChatMessage(response, isUser = false)
                        adapter.notifyItemChanged(aiMessageIndex)
                    }

                    // Scroll to show complete response
                    rvChatMessages.smoothScrollToPosition(aiMessageIndex)

                } catch (streamError: Exception) {
                    streamError.printStackTrace()
                    messages[aiMessageIndex] = ChatMessage(
                        "Error: ${streamError.message}\n\nThe AI model may not be loaded correctly. Please restart the app and make sure the green banner shows 'Ready ✓'",
                        isUser = false
                    )
                    adapter.notifyItemChanged(aiMessageIndex)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                val errorIndex = messages.size - 1
                messages[errorIndex] = ChatMessage(
                    "AI Error: ${e.javaClass.simpleName}\n\nTry:\n1. Restart app\n2. Check model is downloaded\n3. Banner should be green",
                    isUser = false
                )
                adapter.notifyItemChanged(errorIndex)
            }
        }
    }
}

// Simple Adapter for Chat Messages
class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userMessageCard: View = view.findViewById(R.id.userMessageCard)
        val aiMessageLayout: View = view.findViewById(R.id.aiMessageLayout)
        val tvUserMessage: android.widget.TextView = view.findViewById(R.id.tvUserMessage)
        val tvAiMessage: android.widget.TextView = view.findViewById(R.id.tvAiMessage)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ChatViewHolder {
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