package ivan.mineev.githubviewer

import android.animation.Animator

open class AnimatorListenerAdapter : Animator.AnimatorListener {
    var doOnCancel: () -> Unit = {}
    var doOnEnd: () -> Unit = {}
    var doOnRepeat: () -> Unit = {}
    var doOnStart: () -> Unit = {}

    override fun onAnimationCancel(animation: Animator) {
        doOnCancel()
    }

    override fun onAnimationEnd(animation: Animator) {
        doOnEnd()
    }

    override fun onAnimationRepeat(animation: Animator) {
        doOnRepeat()
    }

    override fun onAnimationStart(animation: Animator) {
        doOnStart()
    }
}