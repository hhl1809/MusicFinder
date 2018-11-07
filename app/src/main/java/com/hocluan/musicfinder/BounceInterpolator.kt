package com.hocluan.musicfinder

import android.view.animation.Interpolator

class BounceInterpolator(amplitude: Double, frequency: Double): Interpolator {
    internal var mAmplitude: Double = amplitude
    internal var mFrequency: Double = frequency

    constructor(): this(1.0, 10.0 )

    override fun getInterpolation(input: Float): Float {
        return (-1.0 * Math.pow(Math.E, -input / mAmplitude) * Math.cos(mFrequency * input) + 1).toFloat()
    }

}