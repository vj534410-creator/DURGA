package com.example.durga.network

import com.example.durga.model.AuthorityRole
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseStatus
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.OfficialServiceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

object DurgaBackendClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Configurable backend URL (defaults to production/local endpoint)
    private const val DEFAULT_BACKEND_URL = "https://durga-api.safety.gov.in/api"

    suspend fun submitCaseToBackend(
        caseEntity: CaseEntity,
        evidenceCount: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("caseId", caseEntity.caseId)
                put("anonymousDeviceId", caseEntity.anonymousDeviceId)
                put("category", caseEntity.category)
                put("title", caseEntity.title)
                put("description", caseEntity.structuredDescription)
                put("originalLanguage", caseEntity.originalLanguage)
                put("incidentDate", caseEntity.incidentDate)
                put("incidentTime", caseEntity.incidentTime)
                put("incidentLocation", caseEntity.incidentLocation)
                put("latitude", caseEntity.latitude)
                put("longitude", caseEntity.longitude)
                put("isWitnessReport", caseEntity.isWitnessReport)
                put("sharedItemsSummary", caseEntity.sharedItemsSummary)
                put("evidenceCount", evidenceCount)
                put("secureToken", caseEntity.secureToken)

                val complainant = JSONObject().apply {
                    put("name", caseEntity.complainantName)
                    put("age", caseEntity.complainantAge)
                    put("gender", caseEntity.complainantGender)
                    put("state", caseEntity.complainantState)
                    put("district", caseEntity.complainantDistrict)
                    put("mobile", caseEntity.complainantMobile)
                }
                put("complainant", complainant)
            }

            val requestBody = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$DEFAULT_BACKEND_URL/cases")
                .post(requestBody)
                .build()

            // In production, execute against server. In local / offline mode, succeed gracefully.
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseJson = JSONObject(response.body?.string() ?: "{}")
                    val serverCaseId = responseJson.optString("caseId", caseEntity.caseId)
                    return@withContext Result.success(serverCaseId)
                } else {
                    return@withContext Result.failure(Exception("Server returned HTTP ${response.code}"))
                }
            } catch (netEx: Exception) {
                // Return explicit failure so repository accurately preserves offline status
                return@withContext Result.failure(netEx)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchOfficialServices(): List<OfficialServiceEntity> = withContext(Dispatchers.IO) {
        // Returns verified official emergency directories for India
        getVerifiedIndianEmergencyServices()
    }

    fun getVerifiedIndianEmergencyServices(): List<OfficialServiceEntity> {
        return listOf(
            OfficialServiceEntity(
                id = "112_nat",
                serviceName = "National Emergency Response Support System (ERSS)",
                category = "Police",
                region = "National (All India)",
                phone = "112",
                website = "https://112.gov.in",
                description = "Unified single emergency response for Police, Fire, Ambulance, and Disaster in any state.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "1091_women",
                serviceName = "Women in Distress Helpline",
                category = "Women",
                region = "National",
                phone = "1091",
                website = "https://ncw.nic.in",
                description = "24x7 toll-free emergency response helpline for women facing violence, stalking, or harassment.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "181_domestic",
                serviceName = "Women Domestic Violence Helpline (Abhayam / WHL)",
                category = "Women",
                region = "State / National",
                phone = "181",
                website = "https://wcd.nic.in",
                description = "Immediate rescue, counseling, legal aid, and shelter assistance for women facing domestic abuse.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "1098_child",
                serviceName = "CHILDLINE India (National Child Helpline)",
                category = "Child",
                region = "National (All India)",
                phone = "1098",
                website = "https://childlineindia.org",
                description = "24x7 emergency helpline for children in need of care, protection from abuse, or distress.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "1930_cyber",
                serviceName = "National Cybercrime Reporting Helpline",
                category = "Cyber",
                region = "National (MHA)",
                phone = "1930",
                website = "https://cybercrime.gov.in",
                description = "Immediate reporting of financial fraud, UPI scams, fake profiles, and cyber harassment.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "108_med",
                serviceName = "Emergency Medical & Ambulance Service",
                category = "Medical",
                region = "National / State",
                phone = "108",
                website = "https://nhm.gov.in",
                description = "Free 24x7 emergency medical transport and immediate trauma hospital response.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "102_maternal",
                serviceName = "Maternal & Child Health Ambulance",
                category = "Medical",
                region = "National",
                phone = "102",
                website = "https://nhm.gov.in",
                description = "Specialized free ambulance for pregnant women, infants, and pediatric emergencies.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "100_police",
                serviceName = "Police Emergency Control Room",
                category = "Police",
                region = "National",
                phone = "100",
                website = "https://mha.gov.in",
                description = "Direct police dispatch and control room patrol vehicle assistance.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "101_fire",
                serviceName = "Fire & Rescue Disaster Services",
                category = "Fire",
                region = "National",
                phone = "101",
                website = "https://dgcdf.gov.in",
                description = "Fire rescue, entrapment rescue, and building safety response.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "1512_railway",
                serviceName = "Railway Protection Force (RPF) Helpline",
                category = "Helpline",
                region = "Indian Railways",
                phone = "1512",
                website = "https://indianrailways.gov.in",
                description = "Security assistance on trains and railway station platforms across India.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "1090_wpl",
                serviceName = "Women Power Line (Anti-Stalking & Cyber Harassment)",
                category = "Women",
                region = "Regional Support",
                phone = "1090",
                website = "https://1090up.in",
                description = "Confidential intervention against phone harassment, online bullying, and roadside stalking.",
                verifiedDate = "2026-01-01"
            ),
            OfficialServiceEntity(
                id = "15100_legal",
                serviceName = "National Legal Services Authority (NALSA)",
                category = "Helpline",
                region = "National",
                phone = "15100",
                website = "https://nalsa.gov.in",
                description = "Free legal aid, counsel, and victim compensation support for women and children.",
                verifiedDate = "2026-01-01"
            )
        )
    }
}
