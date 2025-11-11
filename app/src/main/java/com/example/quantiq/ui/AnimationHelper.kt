package com.example.quantiq.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible

/**
 * Helper class for smooth animations throughout the app
 */
object AnimationHelper {

    /**
     * Fade in animation
     */
    fun fadeIn(view: View, duration: Long = 300, startDelay: Long = 0) {
        view.alpha = 0f
        view.isVisible = true
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setStartDelay(startDelay)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    /**
     * Fade out animation
     */
    fun fadeOut(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.isVisible = false
                    onEnd?.invoke()
                }
            })
            .start()
    }

    /**
     * Scale in animation with overshoot
     */
    fun scaleIn(view: View, duration: Long = 400, startDelay: Long = 0) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.isVisible = true
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .setStartDelay(startDelay)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    /**
     * Scale out animation
     */
    fun scaleOut(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.isVisible = false
                    onEnd?.invoke()
                }
            })
            .start()
    }

    /**
     * Slide up animation
     */
    fun slideUp(view: View, duration: Long = 400, startDelay: Long = 0) {
        view.translationY = 200f
        view.alpha = 0f
        view.isVisible = true
        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .setStartDelay(startDelay)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    /**
     * Slide down animation
     */
    fun slideDown(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        view.animate()
            .translationY(200f)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.isVisible = false
                    view.translationY = 0f
                    onEnd?.invoke()
                }
            })
            .start()
    }

    /**
     * Pulse animation for highlighting
     */
    fun pulse(view: View, repeatCount: Int = 1) {
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(150)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    /**
     * Animate number counting
     */
    fun animateNumber(
        from: Float,
        to: Float,
        duration: Long = 1000,
        onUpdate: (Float) -> Unit
    ) {
        ValueAnimator.ofFloat(from, to).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                onUpdate(animation.animatedValue as Float)
            }
            start()
        }
    }

    /**
     * Shake animation for errors
     */
    fun shake(view: View) {
        view.animate()
            .translationX(-10f)
            .setDuration(50)
            .withEndAction {
                view.animate()
                    .translationX(10f)
                    .setDuration(50)
                    .withEndAction {
                        view.animate()
                            .translationX(-10f)
                            .setDuration(50)
                            .withEndAction {
                                view.animate()
                                    .translationX(0f)
                                    .setDuration(50)
                                    .start()
                            }
                            .start()
                    }
                    .start()
            }
            .start()
    }

    /**
     * Animate a list of cards with staggered timing
     */
    fun animateCardsStaggered(cards: List<View>, delayBetween: Long = 100L) {
        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.translationY = 50f
            card.isVisible = true

            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(index * delayBetween)
                .setInterpolator(OvershootInterpolator(0.8f))
                .start()
        }
    }

    /**
     * Animate QuantScore card based on score value
     */
    fun animateQuantScore(card: View, scoreView: View, score: Double) {
        // Fade in
        card.alpha = 0f
        card.scaleX = 0.8f
        card.scaleY = 0.8f
        card.isVisible = true

        card.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setInterpolator(OvershootInterpolator(1.2f))
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Add pulse if score is excellent
                    if (score >= 80) {
                        pulse(scoreView, 3)  // Pulse 3 times
                    }
                }
            })
            .start()
    }

    /**
     * Scale down animation for button press
     */
    fun scaleDown(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    /**
     * Scale up animation for button release
     */
    fun scaleUp(view: View) {
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(100)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
}
