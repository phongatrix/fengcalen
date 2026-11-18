package fengcalen

import fengcalen.util.LunarCalendar
import fengcalen.util.MoonPhaseUtils
import org.junit.Test
import java.time.LocalDate

class AlgorithmTest {
    @Test
    fun testLunar() {
        println("Testing LunarCalendar.solarToLunar...")
        val res = LunarCalendar.solarToLunar("17/11/2026")
        println("Result: ${res.solarDate} -> ${res.lunarDate}, ${res.canChiYear}, ${res.canChiMonth}, ${res.canChiDay}")

        println("Testing LunarCalendar.calculateFengShuiDetail...")
        val fs = LunarCalendar.calculateFengShuiDetail("17/11/2026")
        println("FengShui: ${fs.element}, ${fs.branch}, ${fs.direction}")

        println("Testing MoonPhaseUtils.getMoonPhase...")
        val mp = MoonPhaseUtils.getMoonPhase(LocalDate.of(2026, 11, 17))
        println("MoonPhase: ${mp.phaseNameVi}, ${mp.illumination}%, ${mp.ageDays} days")
    }
}
