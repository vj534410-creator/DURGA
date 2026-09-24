package com.example.durga.i18n

enum class DurgaLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val scriptTagline: String
) {
    ENGLISH("en", "English", "English", "Your safety. Your voice. Your choice."),
    GUJARATI("gu", "ગુજરાતી", "Gujarati", "તમારી સુરક્ષા. તમારો અવાજ. તમારી પસંદગી."),
    HINDI("hi", "हिन्दी", "Hindi", "आपकी सुरक्षा। आपकी आवाज़। आपकी पसंद।")
}
