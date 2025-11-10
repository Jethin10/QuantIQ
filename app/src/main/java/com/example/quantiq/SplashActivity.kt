package com.example.quantiq

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.public.extensions.addModelFromURL
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private lateinit var tvLoadingText: TextView
    private var isModelLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tvLoadingText = findViewById(R.id.tvLoadingText)

        // Animate the logo
        findViewById<View>(android.R.id.content).apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        // Initialize SDK
        initializeSDK()
    }

    private fun initializeSDK() {
        lifecycleScope.launch {
            try {
                updateLoadingText("Initializing AI Engine...")
                delay(500)

                withContext(Dispatchers.IO) {
                    try {
                        RunAnywhere.initialize(
                            context = this@SplashActivity,
                            apiKey = "dev",
                            environment = SDKEnvironment.DEVELOPMENT
                        )

                        withContext(Dispatchers.Main) {
                            updateLoadingText("Registering AI models...")
                        }

                        LlamaCppServiceProvider.register()
                        registerModels()
                        RunAnywhere.scanForDownloadedModels()

                        // Check if model is already downloaded
                        val models = com.runanywhere.sdk.public.extensions.listAvailableModels()
                        if (models.isNotEmpty() && models.first().isDownloaded) {
                            withContext(Dispatchers.Main) {
                                updateLoadingText("Loading AI model...")
                            }
                            RunAnywhere.loadModel(models.first().id)
                            isModelLoaded = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                updateLoadingText("Ready! ✓")
                delay(500)

                // Navigate to main activity
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.putExtra("modelLoaded", isModelLoaded)
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            } catch (e: Exception) {
                e.printStackTrace()
                updateLoadingText("Starting app...")
                delay(1000)

                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.putExtra("modelLoaded", false)
                startActivity(intent)
                finish()
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

    private fun updateLoadingText(text: String) {
        tvLoadingText.text = text
    }
}