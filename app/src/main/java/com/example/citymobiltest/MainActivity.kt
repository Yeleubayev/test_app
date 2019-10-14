package com.example.citymobiltest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import kotlin.math.atan2


class MainActivity : AppCompatActivity() {

    private var isCarMoving: Boolean = false
    private val rotation = "rotation"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        race_track.setOnTouchListener { _, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (!isCarMoving) {
                    moveCar(Point(motionEvent.x.toInt(), motionEvent.y.toInt()))
                    isCarMoving = true
                }
            }
            true
        }
    }


    private fun moveCar(newPoint: Point) {

        val currentPoint = Point((car.x + car.width / 2).toInt(), (car.y + car.height / 2).toInt())
        var currentRotation = Math.toRadians(car.rotation.toDouble())
        var newRotation = atan2(
            (newPoint.x - currentPoint.x).toDouble(),
            (-(newPoint.y - currentPoint.y)).toDouble()
        )


        if (Math.PI < abs(currentRotation - newRotation)) {
            when {
                newRotation < 0 -> newRotation += Math.PI.toFloat() * 2
                currentRotation < 0 -> currentRotation += Math.PI.toFloat() * 2
                currentRotation > Math.PI -> currentRotation -= Math.PI.toFloat() * 2
            }
        }



        val currentRotationDegrees = Math.toDegrees(currentRotation).toFloat()
        val newRotationDegrees = Math.toDegrees(newRotation).toFloat()
        val rotateAnimation =
            ObjectAnimator.ofFloat(car, rotation, currentRotationDegrees, newRotationDegrees)
        rotateAnimation.duration =
            Math.toDegrees(abs(newRotationDegrees - currentRotationDegrees).toDouble() / 10)
                .toLong()
        rotateAnimation.interpolator = LinearInterpolator()



        val path = Path()
        path.setLastPoint(
            (currentPoint.x - car.width / 2).toFloat(),
            (currentPoint.y - car.height / 2).toFloat()
        )
        path.lineTo(
            (newPoint.x - car.width / 2).toFloat(),
            (newPoint.y - car.height / 2).toFloat()
        )

        val moveAnimation = ObjectAnimator.ofFloat(car, FrameLayout.X, FrameLayout.Y, path)
            .apply {
                interpolator = LinearInterpolator()
                duration = PathMeasure(path, false).length.toLong()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        isCarMoving = false
                    }
                })

            }

        AnimatorSet().apply {
            playSequentially(rotateAnimation, moveAnimation)
            start()
        }
    }
}
