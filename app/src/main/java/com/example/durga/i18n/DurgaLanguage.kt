package com.example.durga.i18n

enum class DurgaLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val scriptTagline: String
) {
    GUJARATI("gu", "ગુજરાતી", "Gujarati", "તમારી ભાષા કોઈ પણ હોય, તમારી વાત પહોંચશે."),
    HINDI("hi", "हिन्दी", "Hindi", "आपकी भाषा कोई भी हो, आपकी बात सुनी जाएगी।"),
    ENGLISH("en", "English", "English", "Whatever your language, your voice will be heard."),
    BENGALI("bn", "বাংলা", "Bengali", "আপনার ভাষা যাই হোক, আপনার কথা শোনা হবে।"),
    MARATHI("mr", "मराठी", "Marathi", "तुमची भाषा कोणतीही असो, तुमचा आवाज ऐकला जाईल."),
    TAMIL("ta", "தமிழ்", "Tamil", "உங்கள் மொழி எதுவாக இருந்தாலும், உங்கள் குரல் கேட்கப்படும்."),
    TELUGU("te", "తెలుగు", "Telugu", "మీ భాష ఏదైనా, మీ స్వరం వినబడుతుంది."),
    KANNADA("kn", "ಕನ್ನಡ", "Kannada", "ನಿಮ್ಮ ಭಾಷೆ ಯಾವುದೇ ಇರಲಿ, ನಿಮ್ಮ ಧ್ವನಿ ತಲುಪುತ್ತದೆ."),
    MALAYALAM("ml", "മലയാളം", "Malayalam", "നിങ്ങളുടെ ഭാഷ ഏതുമാകട്ടെ, നിങ്ങളുടെ ശബ്ദം കേൾക്കും."),
    PUNJABI("pa", "ਪੰਜਾਬੀ", "Punjabi", "ਤੁਹਾਡੀ ਭਾਸ਼ਾ ਕੋਈ ਵੀ ਹੋਵੇ, ਤੁਹਾਡੀ ਗੱਲ ਸੁਣੀ ਜਾਵੇਗੀ।"),
    ODIA("or", "ଓଡ଼ିଆ", "Odia", "ଆପଣଙ୍କ ଭାଷା ଯାହା ବି ହେଉ, ଆପଣଙ୍କ ସ୍ୱର ଶୁଣାଯିବ।"),
    ASSAMESE("as", "অসমীয়া", "Assamese", "আপোনাৰ ভাষা যিয়েই নহওক, আপোনাৰ কথা শুনা যাব।"),
    URDU("ur", "اردو", "Urdu", "آپ کی زبان کوئی بھی ہو، آپ کی آواز سنی جائے گی۔")
}
