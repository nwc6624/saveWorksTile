package de.westnordost.streetmeasure

import kotlin.math.floor
import kotlin.math.round

/** In which unit the measurement is displayed */
sealed class MeasureDisplayUnit {
    abstract fun format(distanceMeters: Double): String
}

/** Measurement displayed in meters rounded to the nearest [cmStep] */
data class MeasureDisplayUnitMeter(val cmStep: Int) : MeasureDisplayUnit() {
    init {
        require(cmStep > 0)
    }

    override fun format(distanceMeters: Double): String {
        val decimals = when {
            cmStep % 100 == 0 -> 0
            cmStep % 10 == 0 -> 1
            else -> 2
        }
        return "%.${decimals}f m".format(getRounded(distanceMeters))
    }

    /** Returns the given distance in meters rounded to the given precision */
    fun getRounded(distanceMeters: Double): Double {
        return round(distanceMeters * 100 / cmStep) * cmStep / 100
    }
}

/** Measurement displayed in feet+inch, inches rounded to nearest [inchStep]. Must be between 1-12 */
data class MeasureDisplayUnitFeetInch(val inchStep: Int) : MeasureDisplayUnit() {
    init {
        require(inchStep in 1..12)
    }

    override fun format(distanceMeters: Double): String {
        val (feet, inches) = getRounded(distanceMeters)
        return if (inches < 10) "$feet′ $inches″" else "$feet′$inches″"
    }

    /** Returns the given distance in meters as feet + inch */
    fun getRounded(distanceMeters: Double): Pair<Int, Int> {
        val distanceFeet = distanceMeters / 0.3048
        var feet = floor(distanceFeet).toInt()
        val inches = (distanceFeet - feet) * 12
        var inchesStepped = round(inches / inchStep).toInt() * inchStep
        if (inchesStepped == 12) {
            ++feet
            inchesStepped = 0
        }
        return Pair(feet, inchesStepped)
    }
}
