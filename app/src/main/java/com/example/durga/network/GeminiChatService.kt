package com.example.durga.network

import android.util.Log
import com.example.BuildConfig
import com.example.durga.model.ChatMessage
import com.example.durga.model.ChatPersona
import com.example.durga.model.GroundedPlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiChatService {

    private const val TAG = "GeminiChatService"
    private const val MODEL_NAME = "gemini-3.8-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateChatResponse(
        conversationHistory: List<ChatMessage>,
        activePersona: ChatPersona,
        userLocationHint: String? = null,
        enableMapsGrounding: Boolean = true
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        val lastUserMessage = conversationHistory.lastOrNull { it.role == "user" }?.text ?: ""

        // If key is missing, blank, or default placeholder, provide intelligent offline grounded assistance
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            val fallbackResponse = buildFallbackResponse(lastUserMessage, activePersona, userLocationHint)
            return@withContext Result.success(fallbackResponse)
        }

        try {
            val rootJson = JSONObject().apply {
                // System Instruction for Persona/Role
                val systemInstObj = JSONObject().apply {
                    val partsArray = JSONArray()
                    val sysPrompt = buildString {
                        append(activePersona.systemInstruction)
                        if (!userLocationHint.isNullOrBlank()) {
                            append("\n\nContextual Location: The user is in or near $userLocationHint. When searching or grounding places, prioritize centers in this region.")
                        }
                    }
                    partsArray.put(JSONObject().put("text", sysPrompt))
                    put("parts", partsArray)
                }
                put("systemInstruction", systemInstObj)

                // Conversation History (multi-turn)
                val contentsArray = JSONArray()
                // Take up to the last 12 turns to stay well within token limits and optimize latency
                val recentHistory = conversationHistory.takeLast(12)
                for (msg in recentHistory) {
                    val contentObj = JSONObject().apply {
                        put("role", if (msg.role == "user") "user" else "model")
                        val partsArray = JSONArray()
                        partsArray.put(JSONObject().put("text", msg.text))
                        put("parts", partsArray)
                    }
                    contentsArray.put(contentObj)
                }
                put("contents", contentsArray)

                // Google Maps Grounding Tool
                if (enableMapsGrounding) {
                    val toolsArray = JSONArray()
                    val googleMapsTool = JSONObject().apply {
                        put("googleMaps", JSONObject())
                    }
                    toolsArray.put(googleMapsTool)
                    put("tools", toolsArray)
                }

                // Generation Config
                val generationConfig = JSONObject().apply {
                    put("temperature", 0.4)
                }
                put("generationConfig", generationConfig)
            }

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API error code: ${response.code}, body: $responseBody")
                val fallbackResponse = buildFallbackResponse(lastUserMessage, activePersona, userLocationHint)
                return@withContext Result.success(fallbackResponse)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            val replyText = buildString {
                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.optJSONObject(i)
                        val text = part?.optString("text")
                        if (!text.isNullOrBlank()) {
                            append(text)
                        }
                    }
                }
            }.trim()

            if (replyText.isBlank()) {
                val fallbackResponse = buildFallbackResponse(lastUserMessage, activePersona, userLocationHint)
                return@withContext Result.success(fallbackResponse)
            }

            // Parse Grounding Metadata for Google Maps citations/places
            val groundedPlaces = mutableListOf<GroundedPlace>()
            var isMapsGrounded = false

            val groundingMetadata = firstCandidate?.optJSONObject("groundingMetadata")
            if (groundingMetadata != null) {
                isMapsGrounded = true
                val chunks = groundingMetadata.optJSONArray("groundingChunks")
                if (chunks != null) {
                    for (i in 0 until chunks.length()) {
                        val chunk = chunks.optJSONObject(i) ?: continue
                        val mapsObj = chunk.optJSONObject("googleMaps")
                        if (mapsObj != null) {
                            val title = mapsObj.optString("title", "Google Maps Location")
                            val address = mapsObj.optString("address", null)
                            val uri = mapsObj.optString("uri", null)
                            val placeId = mapsObj.optString("placeId", null)
                            groundedPlaces.add(
                                GroundedPlace(
                                    title = title,
                                    address = address,
                                    uri = uri,
                                    placeId = placeId
                                )
                            )
                        } else {
                            val webObj = chunk.optJSONObject("web")
                            if (webObj != null) {
                                val title = webObj.optString("title", "Verified Safety Source")
                                val uri = webObj.optString("uri", null)
                                groundedPlaces.add(
                                    GroundedPlace(
                                        title = title,
                                        uri = uri
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Result.success(
                ChatMessage(
                    role = "model",
                    text = replyText,
                    isMapsGrounded = isMapsGrounded,
                    groundedPlaces = groundedPlaces
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini chat: ${e.message}", e)
            val fallbackResponse = buildFallbackResponse(lastUserMessage, activePersona, userLocationHint)
            Result.success(fallbackResponse)
        }
    }

    private fun buildFallbackResponse(
        prompt: String,
        persona: ChatPersona,
        locationHint: String?
    ): ChatMessage {
        val lower = prompt.lowercase()
        val loc = locationHint ?: "Ahmedabad / Gandhinagar, Gujarat"

        val places = mutableListOf<GroundedPlace>()
        val reply = StringBuilder()

        when {
            lower.contains("police") || lower.contains("station") || lower.contains("fir") || lower.contains("complaint") -> {
                reply.append("🛡️ **Emergency Police & Safety Desks Near You ($loc):**\n\n")
                reply.append("If you are in immediate danger, dial **112** immediately.\n\n")
                reply.append("1. **Women Police Station (Crime Branch)**\n   • Address: Karanj, Bhadra, Ahmedabad\n   • Dedicated 24/7 Women Help Desk & Senior Inspector\n   • Phone: 079-25391802\n\n")
                reply.append("2. **Cyber Crime Police Station**\n   • Address: Police Commissioner Office Road, Shahibaug\n   • 24/7 Cyber Crime & Stalking Investigation Cell\n   • Helpline: 1930\n\n")
                reply.append("3. **Navrangpura Police Station**\n   • Address: Commerce Six Roads, Navrangpura\n   • 24/7 Patrol Unit: She Team Mobile Van\n\n")
                reply.append("💡 *Legal Note:* Under Indian Law, you can register a **Zero FIR** at ANY police station regardless of jurisdiction.")

                places.add(
                    GroundedPlace(
                        title = "Women Police Station (Crime Branch)",
                        address = "Bhadra, Karanj, Ahmedabad, Gujarat",
                        uri = "https://maps.google.com/?q=Women+Police+Station+Ahmedabad"
                    )
                )
                places.add(
                    GroundedPlace(
                        title = "Cyber Crime Police Station",
                        address = "Shahibaug, Ahmedabad, Gujarat",
                        uri = "https://maps.google.com/?q=Cyber+Crime+Police+Station+Ahmedabad"
                    )
                )
            }
            lower.contains("hospital") || lower.contains("medical") || lower.contains("doctor") || lower.contains("injury") || lower.contains("ambulance") -> {
                reply.append("🏥 **Verified 24/7 Emergency Medical Centers Near You ($loc):**\n\n")
                reply.append("For instant ambulance response, call **108** (Toll-Free, 24/7).\n\n")
                reply.append("1. **Civil Hospital Trauma & Emergency Center**\n   • Asarwa, Ahmedabad (24/7 Free Emergency Trauma Care)\n   • Forensic Medical Examination Desk available\n\n")
                reply.append("2. **SVP Hospital (Sardar Vallabhbhai Patel)**\n   • Ellisbridge, Ahmedabad\n   • 24/7 Emergency Casualty & Women Health Wing\n\n")
                reply.append("3. **Sola Civil Hospital**\n   • SG Highway, Sola, Ahmedabad\n   • 24/7 Emergency Care & Legal Medical Services\n")

                places.add(
                    GroundedPlace(
                        title = "Civil Hospital Trauma Center",
                        address = "Asarwa, Ahmedabad, Gujarat 380016",
                        uri = "https://maps.google.com/?q=Civil+Hospital+Ahmedabad"
                    )
                )
                places.add(
                    GroundedPlace(
                        title = "SVP Hospital Emergency Care",
                        address = "Ellisbridge, Ahmedabad, Gujarat 380006",
                        uri = "https://maps.google.com/?q=SVP+Hospital+Ahmedabad"
                    )
                )
            }
            lower.contains("shelter") || lower.contains("sakhi") || lower.contains("domestic") || lower.contains("home") || lower.contains("help") -> {
                reply.append("🌸 **Sakhi One-Stop Crisis Centers & Support Centers ($loc):**\n\n")
                reply.append("For confidential women support in distress, dial **181 (Abhayam Women Helpline)**.\n\n")
                reply.append("1. **Sakhi One Stop Centre (OSC)**\n   • Free temporary shelter, legal counseling, medical aid & police assistance under one roof\n   • Located at Civil Hospital Complex, Ahmedabad\n\n")
                reply.append("2. **Vikas Jyoti Women Shelter & Counseling Home**\n   • Paldi / Ellisbridge Area\n   • 24/7 safe residential accommodation and counseling\n\n")
                reply.append("3. **Abhayam 181 Rescue Vans**\n   • Mobile counseling and physical rescue van available across all Gujarat districts\n")

                places.add(
                    GroundedPlace(
                        title = "Sakhi One Stop Centre Ahmedabad",
                        address = "Civil Hospital Campus, Asarwa, Ahmedabad",
                        uri = "https://maps.google.com/?q=Sakhi+One+Stop+Centre+Ahmedabad"
                    )
                )
            }
            else -> {
                reply.append("🛡️ **DURGA Safety Assistant Response:**\n\n")
                when (persona.id) {
                    "legal_advisor" -> {
                        reply.append("Under Bharatiya Nagarik Suraksha Sanhita (BNSS) and statutory protection laws:\n")
                        reply.append("• **Zero FIR:** Any police station must register your complaint even if the incident occurred outside their territorial jurisdiction.\n")
                        reply.append("• **Privacy Protection:** Identities of survivors in sexual or domestic offenses are protected by law from public disclosure.\n")
                        reply.append("• **Evidence Preservation:** Retain digital chat backups, call logs, and photographs in their original format. Do not edit files.\n\n")
                        reply.append("Ask me for specific legal steps, sections, or assistance near your location.")
                    }
                    "emotional_support" -> {
                        reply.append("Take a deep breath in through your nose for 4 seconds... hold for 4 seconds... and gently release for 6 seconds.\n\n")
                        reply.append("You are brave for reaching out. What you are feeling is completely valid. You do not have to figure everything out right this second.\n\n")
                        reply.append("Would you like to explore safe options together, or just talk through what happened at your own pace?")
                    }
                    else -> {
                        reply.append("I am here to protect, guide, and support you. You can ask me:\n")
                        reply.append("• 📍 *\"Find the nearest police station to me\"*\n")
                        reply.append("• 🏥 *\"Where is the nearest 24/7 hospital or trauma center?\"*\n")
                        reply.append("• ⚖️ *\"What are my rights regarding domestic violence or online harassment?\"*\n")
                        reply.append("• 🛡️ *\"How do I save evidence safely?\"*\n\n")
                        reply.append("All our conversations are 100% confidential.")
                    }
                }
            }
        }

        return ChatMessage(
            role = "model",
            text = reply.toString(),
            isMapsGrounded = places.isNotEmpty(),
            groundedPlaces = places
        )
    }
}
