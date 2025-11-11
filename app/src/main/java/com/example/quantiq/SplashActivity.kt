package com.example.quantiq

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
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
    private lateinit var ivLogo: ImageView
    private lateinit var tvI: TextView
    private lateinit var tvQ: TextView
    private var isModelLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tvLoadingText = findViewById(R.id.tvLoadingText)
        ivLogo = findViewById(R.id.ivLogo)
        tvI = findViewById(R.id.tvI)
        tvQ = findViewById(R.id.tvQ)

        // Fade in the whole screen
        findViewById<View>(android.R.id.content).apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        // Start animations
        animateLogo()
        animateIQLetters()

        // Initialize SDK
        initializeSDK()
    }

    private fun animateLogo() {
        // Pulsing/breathing effect for the brain logo
        val scaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 1f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 1f, 1.1f, 1f)

        scaleX.duration = 2000
        scaleY.duration = 2000
        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        scaleX.interpolator = DecelerateInterpolator()
        scaleY.interpolator = DecelerateInterpolator()

        scaleX.start()
        scaleY.start()

        // Subtle rotation for tech feel
        val rotate = ObjectAnimator.ofFloat(ivLogo, "rotation", 0f, 5f, 0f, -5f, 0f)
        rotate.duration = 4000
        rotate.repeatCount = ObjectAnimator.INFINITE
        rotate.interpolator = DecelerateInterpolator()
        rotate.start()
    }

    private fun animateIQLetters() {
        // Make IQ letters bounce in sequence
        lifecycleScope.launch {
            delay(400) // Wait for fade in

            // Bounce "I" letter
            val bounceI = ObjectAnimator.ofFloat(tvI, "translationY", 0f, -30f, 0f)
            bounceI.duration = 600
            bounceI.interpolator = BounceInterpolator()
            bounceI.start()

            delay(150) // Stagger the animation

            // Bounce "Q" letter
            val bounceQ = ObjectAnimator.ofFloat(tvQ, "translationY", 0f, -30f, 0f)
            bounceQ.duration = 600
            bounceQ.interpolator = BounceInterpolator()
            bounceQ.start()

            // Color pulse effect for IQ letters
            delay(300)
            startColorPulse()
        }
    }

    private fun startColorPulse() {
        lifecycleScope.launch {
            while (true) {
                // Pulse the IQ letters with alpha
                val alphaI = ObjectAnimator.ofFloat(tvI, "alpha", 1f, 0.5f, 1f)
                val alphaQ = ObjectAnimator.ofFloat(tvQ, "alpha", 1f, 0.5f, 1f)

                alphaI.duration = 1500
                alphaQ.duration = 1500
                alphaI.startDelay = 100 // Slight offset for wave effect

                alphaI.start()
                alphaQ.start()

                delay(3000) // Repeat every 3 seconds
            }
        }
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