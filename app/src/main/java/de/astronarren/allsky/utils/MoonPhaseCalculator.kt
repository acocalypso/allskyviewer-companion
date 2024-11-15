package de.astronarren.allsky.utils

import kotlin.math.floor
import kotlin.math.cos
import java.time.LocalDateTime
import kotlin.math.PI
import de.astronarren.allsky.R

class MoonPhaseCalculator {
    companion object {
        private const val LUNAR_MONTH = 29.53059

        fun calculateMoonPhase(): MoonPhase {
            val moonAge = getMoonAge()
            
            return when {
                moonAge < 1.84566 -> MoonPhase.NEW_MOON
                moonAge < 5.53699 -> MoonPhase.WAXING_CRESCENT
                moonAge < 9.22831 -> MoonPhase.FIRST_QUARTER
                moonAge < 12.91963 -> MoonPhase.WAXING_GIBBOUS
                moonAge < 16.61096 -> MoonPhase.FULL_MOON
                moonAge < 20.30228 -> MoonPhase.WANING_GIBBOUS
                moonAge < 23.99361 -> MoonPhase.LAST_QUARTER
                moonAge < 27.68493 -> MoonPhase.WANING_CRESCENT
                else -> MoonPhase.NEW_MOON
            }
        }

        fun getDaysUntilNewMoon(): Double {
            val moonAge = getMoonAge()
            return LUNAR_MONTH - moonAge
        }

        private fun getMoonAge(): Double {
            val now = LocalDateTime.now()
            
            // JDN calculation
            val year = now.year
            val month = now.monthValue
            val day = now.dayOfMonth
            
            val jdn = (367 * year -
                    floor(7 * (year + floor((month + 9) / 12.0)) / 4.0) +
                    floor(275 * month / 9.0) +
                    day - 730530).toDouble()
            
            // Calculate moon's age in days
            return (jdn % LUNAR_MONTH).toDouble()
        }

        fun getIllumination(): Double {
            val now = LocalDateTime.now()
            val daysSince2000 = (now.toLocalDate().toEpochDay() - 
                    LocalDateTime.of(2000, 1, 1, 0, 0).toLocalDate().toEpochDay())
            
            val phase = 2.0 * PI * ((daysSince2000 % LUNAR_MONTH) / LUNAR_MONTH)
            return ((1.0 - cos(phase)) / 2.0) * 100.0
        }
    }
}

enum class MoonPhase(val stringResId: Int, val emoji: String) {
    NEW_MOON(R.string.moon_phase_new_moon, "🌑"),
    WAXING_CRESCENT(R.string.moon_phase_waxing_crescent, "🌒"),
    FIRST_QUARTER(R.string.moon_phase_first_quarter, "🌓"),
    WAXING_GIBBOUS(R.string.moon_phase_waxing_gibbous, "🌔"),
    FULL_MOON(R.string.moon_phase_full_moon, "🌕"),
    WANING_GIBBOUS(R.string.moon_phase_waning_gibbous, "🌖"),
    LAST_QUARTER(R.string.moon_phase_last_quarter, "🌗"),
    WANING_CRESCENT(R.string.moon_phase_waning_crescent, "🌘")
} 