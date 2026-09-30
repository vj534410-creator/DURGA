package com.example.durga.network

import com.example.BuildConfig
import com.example.durga.model.AiGeneratedDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiComplaintWriter {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val MODEL_NAME = "gemini-2.0-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    suspend fun generateComplaintDraft(
        rawText: String,
        category: String,
        languageName: String
    ): Result<AiGeneratedDraft> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // If API key is missing or blank or placeholder, provide structured deterministic safe drafting
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(fallbackComplaintDraft(rawText, category))
        }

        val prompt = """
You are DURGA AI Complaint-Writing Assistant.
PRIMARY ROLE: Convert the complainant's statement into a formal, clear, chronologically structured official incident report for police/support authorities.

CRITICAL PRODUCT RULES (STRICTLY ENFORCED):
1. You MUST NEVER invent facts, names, dates, times, locations, witnesses, evidence, legal accusations, or penal code sections not mentioned by the user.
2. You MUST NOT judge guilt, predict danger level, make legal judgments, or decide emergency actions.
3. If important information is missing (such as exact date, specific location, identity of perpetrator, or witness existence), list gentle clarifying questions in "missingQuestions".
4. You must output ONLY valid JSON matching this exact structure:
{
  "title": "Clear concise formal title of incident",
  "incidentSummary": "2-3 sentence factual summary of the user's statement",
  "detailedDescription": "Clean, structured, formal complaint narrative based solely on user facts",
  "missingQuestions": ["Clarifying question 1 if details missing", "Clarifying question 2"],
  "suggestedLegalSafeguards": "Official note reminding complainant to keep evidence safe and report to official authorities."
}

User's Incident Category: $category
User's Language Context: $languageName
User's Raw Statement:
\"\"\"$rawText\"\"\"
        """.trimIndent()

        try {
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray()
                    partsArray.put(JSONObject().put("text", prompt))
                    put("parts", partsArray)
                }
                contentsArray.put(contentObj)
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
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
            if (!response.isSuccessful) {
                // Graceful fallback to local structured template so user's complaint is never stalled
                return@withContext Result.success(fallbackComplaintDraft(rawText, category))
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (text.isBlank()) {
                return@withContext Result.success(fallbackComplaintDraft(rawText, category))
            }

            // Parse returned JSON from model
            val draftJson = JSONObject(text)
            val title = draftJson.optString("title", "Official Incident Complaint: $category")
            val incidentSummary = draftJson.optString("incidentSummary", rawText)
            val detailedDescription = draftJson.optString("detailedDescription", rawText)
            val questionsList = mutableListOf<String>()
            draftJson.optJSONArray("missingQuestions")?.let { qArray ->
                for (i in 0 until qArray.length()) {
                    val q = qArray.optString(i)
                    if (q.isNotBlank()) questionsList.add(q)
                }
            }
            val safeguards = draftJson.optString("suggestedLegalSafeguards", "Preserve all original evidence.")

            Result.success(
                AiGeneratedDraft(
                    title = title,
                    incidentSummary = incidentSummary,
                    detailedDescription = detailedDescription,
                    missingQuestions = questionsList,
                    suggestedLegalSafeguards = safeguards
                )
            )
        } catch (e: Exception) {
            // Absolute reliability: Fallback to structured local draft without data loss
            Result.success(fallbackComplaintDraft(rawText, category))
        }
    }

    private fun fallbackComplaintDraft(rawText: String, category: String): AiGeneratedDraft {
        val cleanSummary = if (rawText.length > 150) {
            rawText.substring(0, 147).trim() + "..."
        } else {
            rawText
        }

        val questions = mutableListOf<String>()
        val lower = rawText.lowercase()
        if (!lower.contains("date") && !lower.contains("yesterday") && !lower.contains("today") && !lower.contains("on ") && !lower.contains("તારીખ") && !lower.contains("दिन") && !lower.contains("दिनांक")) {
            questions.add("On what specific date and time did this incident take place?")
        }
        if (!lower.contains("at ") && !lower.contains("in ") && !lower.contains("near") && !lower.contains("સ્થળ") && !lower.contains("जगह") && !lower.contains("स्थान")) {
            questions.add("Where exactly did this happen (street, landmark, institution, online handle)?")
        }
        if (!lower.contains("message") && !lower.contains("photo") && !lower.contains("proof") && !lower.contains("screenshot") && !lower.contains("પુરાવા") && !lower.contains("सबूत")) {
            questions.add("Do you have any screenshot, message, photo, or audio evidence saved?")
        }

        val formalNarrative = StringBuilder()
        formalNarrative.append("COMPLAINT REGARDING: ").append(category.replace("_", " ")).append("\n\n")
        formalNarrative.append("NARRATIVE STATEMENT:\n")
        formalNarrative.append(rawText.trim()).append("\n\n")
        formalNarrative.append("REQUESTED ACTION:\n")
        formalNarrative.append("Kindly register this complaint for necessary enquiry and appropriate safety/protection measures in accordance with law.")

        return AiGeneratedDraft(
            title = "Report of ${category.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }}",
            incidentSummary = cleanSummary,
            detailedDescription = formalNarrative.toString(),
            missingQuestions = questions,
            suggestedLegalSafeguards = "DURGA safety note: Preserve all digital and physical evidence in their original unmodified state."
        )
    }
}
