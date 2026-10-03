package com.example.durga.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.durga.model.AuditAccessLogEntity
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseNotificationEntity
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.model.EvidenceEntity
import com.example.durga.model.OfficialServiceEntity
import com.example.durga.model.SafetyCardEntity
import com.example.durga.model.TrustedContactEntity
import com.example.durga.model.CaseTimelineEntity
import com.example.durga.model.CaseQrEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY createdAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    suspend fun getCaseById(caseId: String): CaseEntity?

    @Query("SELECT * FROM cases WHERE secureToken = :token LIMIT 1")
    suspend fun getCaseBySecureToken(token: String): CaseEntity?

    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    fun observeCaseById(caseId: String): Flow<CaseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(caseEntity: CaseEntity)

    @Update
    suspend fun updateCase(caseEntity: CaseEntity)

    @Query("UPDATE cases SET status = :newStatus, updatedAt = :timestamp WHERE caseId = :caseId")
    suspend fun updateCaseStatus(caseId: String, newStatus: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE cases SET policeStation = :station, policeDistrict = :district, assignedInspector = :inspector, investigatingOfficer = :officer, status = :newStatus, latestAuthorityNote = :note, assignedAt = :assignedAt, updatedAt = :timestamp WHERE caseId = :caseId")
    suspend fun assignAuthorityToCase(
        caseId: String,
        station: String,
        district: String,
        inspector: String,
        officer: String,
        newStatus: String,
        note: String,
        assignedAt: Long = System.currentTimeMillis(),
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM cases WHERE caseId = :caseId")
    suspend fun deleteCase(caseId: String)

    @Query("DELETE FROM cases")
    suspend fun deleteAllCases()

    @Query("SELECT COUNT(*) FROM cases")
    fun getCasesCount(): Flow<Int>
}

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence WHERE caseId = :caseId ORDER BY createdAt ASC")
    fun getEvidenceForCase(caseId: String): Flow<List<EvidenceEntity>>

    @Query("SELECT * FROM evidence ORDER BY createdAt DESC")
    fun getAllEvidence(): Flow<List<EvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: EvidenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidenceList(evidenceList: List<EvidenceEntity>)

    @Query("DELETE FROM evidence WHERE id = :id")
    suspend fun deleteEvidence(id: String)

    @Query("DELETE FROM evidence WHERE caseId = :caseId")
    suspend fun deleteEvidenceForCase(caseId: String)

    @Query("DELETE FROM evidence")
    suspend fun deleteAllEvidence()
}

@Dao
interface DraftDao {
    @Query("SELECT * FROM draft_complaints ORDER BY lastModified DESC")
    fun getAllDrafts(): Flow<List<DraftComplaintEntity>>

    @Query("SELECT * FROM draft_complaints WHERE draftId = :draftId")
    suspend fun getDraftById(draftId: String): DraftComplaintEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: DraftComplaintEntity)

    @Query("DELETE FROM draft_complaints WHERE draftId = :draftId")
    suspend fun deleteDraft(draftId: String)

    @Query("DELETE FROM draft_complaints")
    suspend fun deleteAllDrafts()

    @Query("SELECT COUNT(*) FROM draft_complaints")
    fun getDraftsCount(): Flow<Int>
}

@Dao
interface CaseUpdateDao {
    @Query("SELECT * FROM case_updates WHERE caseId = :caseId ORDER BY timestamp ASC")
    fun getUpdatesForCase(caseId: String): Flow<List<CaseUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: CaseUpdateEntity)

    @Query("DELETE FROM case_updates WHERE caseId = :caseId")
    suspend fun deleteUpdatesForCase(caseId: String)
}

@Dao
interface OfficialServiceDao {
    @Query("SELECT * FROM official_services WHERE isActive = 1 ORDER BY category ASC")
    fun getAllActiveServices(): Flow<List<OfficialServiceEntity>>

    @Query("SELECT * FROM official_services WHERE category = :category AND isActive = 1")
    fun getServicesByCategory(category: String): Flow<List<OfficialServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<OfficialServiceEntity>)

    @Query("SELECT COUNT(*) FROM official_services")
    suspend fun getServicesCount(): Int
}

@Dao
interface SafetyCardDao {
    @Query("SELECT * FROM safety_card WHERE id = 1")
    fun getSafetyCard(): Flow<SafetyCardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSafetyCard(card: SafetyCardEntity)

    @Query("DELETE FROM safety_card")
    suspend fun deleteSafetyCard()
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_access_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditAccessLogEntity>>

    @Query("SELECT * FROM audit_access_logs WHERE caseId = :caseId ORDER BY timestamp DESC")
    fun getLogsForCase(caseId: String): Flow<List<AuditAccessLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditAccessLogEntity)

    @Query("DELETE FROM audit_access_logs")
    suspend fun deleteAllLogs()
}

@Dao
interface CaseNotificationDao {
    @Query("SELECT * FROM case_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<CaseNotificationEntity>>

    @Query("SELECT * FROM case_notifications WHERE caseId = :caseId ORDER BY timestamp DESC")
    fun getNotificationsForCase(caseId: String): Flow<List<CaseNotificationEntity>>

    @Query("SELECT COUNT(*) FROM case_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: CaseNotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<CaseNotificationEntity>)

    @Query("UPDATE case_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE case_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM case_notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    @Query("DELETE FROM case_notifications")
    suspend fun deleteAllNotifications()
}

@Dao
interface TrustedContactDao {
    @Query("SELECT * FROM trusted_contacts ORDER BY createdAt ASC")
    fun getAllContacts(): Flow<List<TrustedContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: TrustedContactEntity)

    @Update
    suspend fun updateContact(contact: TrustedContactEntity)

    @Query("DELETE FROM trusted_contacts WHERE id = :id")
    suspend fun deleteContact(id: String)

    @Query("DELETE FROM trusted_contacts")
    suspend fun deleteAllContacts()
}

@Dao
interface CaseTimelineDao {
    @Query("SELECT * FROM case_timeline WHERE caseId = :caseId ORDER BY timestamp ASC")
    fun getTimelineForCase(caseId: String): Flow<List<CaseTimelineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CaseTimelineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<CaseTimelineEntity>)

    @Query("DELETE FROM case_timeline WHERE caseId = :caseId")
    suspend fun deleteTimelineForCase(caseId: String)
}

@Dao
interface CaseQrDao {
    @Query("SELECT * FROM case_qr WHERE caseId = :caseId ORDER BY createdAt DESC LIMIT 1")
    fun observeQrForCase(caseId: String): Flow<CaseQrEntity?>

    @Query("SELECT * FROM case_qr WHERE caseId = :caseId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getQrForCase(caseId: String): CaseQrEntity?

    @Query("SELECT * FROM case_qr WHERE token = :token LIMIT 1")
    suspend fun getQrByToken(token: String): CaseQrEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQr(qr: CaseQrEntity)

    @Query("UPDATE case_qr SET qrStatus = :status, revokedAt = :timestamp, revokedBy = :revokedBy, revocationReason = :reason WHERE caseId = :caseId")
    suspend fun updateQrStatus(caseId: String, status: String, timestamp: Long = System.currentTimeMillis(), revokedBy: String? = null, reason: String? = null)

    @Query("DELETE FROM case_qr WHERE caseId = :caseId")
    suspend fun deleteQrForCase(caseId: String)
}


