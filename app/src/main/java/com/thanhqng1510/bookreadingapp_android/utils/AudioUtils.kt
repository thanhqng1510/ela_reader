package com.thanhqng1510.bookreadingapp_android.utils

object AudioUtils {
    enum class AMBIENT(val resId: Int, val displayStr: String) {
        NONE(0, "None"),
        MELANCHOLIC(0, "Melancholic"),
        NOSTALGIC(0, "Nostalgic"),
        RELAXING(0, "Relaxing"),
        SOOTHING(0, "Soothing"),
        FOCUS(0, "Focus"),
        ENERGETIC(0, "Energetic"),
        CINEMATIC(0, "Cinematic");

        companion object {
            fun fromStr(string: String): AMBIENT? =
                values().find { ambient -> ambient.displayStr == string }
        }
    }
}