package de.astronarren.allsky.utils

import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.cos
import kotlin.math.PI
import de.astronarren.allsky.R

class MoonPhaseCalculator {
    companion object {
        // Lunar cycle constant in days
        private const val LUNAR_CYCLE = 29.53058770576
        
        // First new moon of 2000 was on January 6 at 18:14 UTC
        private val NEW_MOON_2000 = LocalDateTime.of(2000, 1, 6, 18, 14)
            .toEpochSecond(ZoneOffset.UTC)

        fun calculateMoonPhase(): MoonPhase {
            val fraction = getCurrentMoonCycleFraction()
            
            return when {
                fraction < 0.033863193308711 -> MoonPhase.NEW_MOON
                fraction < 0.216136806691289 -> MoonPhase.WAXING_CRESCENT
                fraction < 0.283863193308711 -> MoonPhase.FIRST_QUARTER
                fraction < 0.466136806691289 -> MoonPhase.WAXING_GIBBOUS
                fraction < 0.533863193308711 -> MoonPhase.FULL_MOON
                fraction < 0.716136806691289 -> MoonPhase.WANING_GIBBOUS
                fraction < 0.783863193308711 -> MoonPhase.LAST_QUARTER
                fraction < 0.966136806691289 -> MoonPhase.WANING_CRESCENT
                else -> MoonPhase.NEW_MOON
            }
        }

        fun getDaysUntilNewMoon(): Double {
            val fraction = getCurrentMoonCycleFraction()
            return LUNAR_CYCLE * (1.0 - fraction)
        }

        private fun getCurrentMoonCycleFraction(): Double {
            val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            val totalSeconds = now - NEW_MOON_2000
            val lunarSeconds = LUNAR_CYCLE * 24 * 60 * 60
            
            // Get position in current cycle
            var currentSeconds = totalSeconds % lunarSeconds
            
            // Handle dates before 2000
            if (currentSeconds < 0) {
                currentSeconds += lunarSeconds
            }
            
            return currentSeconds / lunarSeconds
        }

        fun getIllumination(): Double {
            val fraction = getCurrentMoonCycleFraction()
            val phase = 2.0 * PI * fraction
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