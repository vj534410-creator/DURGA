package com.example.durga.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.durga.model.AuditAccessLogEntity
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseNotificationEntity
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.model.EvidenceEntity
import com.example.durga.model.OfficialServiceEntity
import com.example.durga.model.SafetyCardEntity

@Database(
    entities = [
        CaseEntity::class,
        EvidenceEntity::class,
        DraftComplaintEntity::class,
        CaseUpdateEntity::class,
        OfficialServiceEntity::class,
        SafetyCardEntity::class,
        AuditAccessLogEntity::class,
        CaseNotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class DurgaDatabase : RoomDatabase() {
    abstract fun caseDao(): CaseDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun draftDao(): DraftDao
    abstract fun caseUpdateDao(): CaseUpdateDao
    abstract fun officialServiceDao(): OfficialServiceDao
    abstract fun safetyCardDao(): SafetyCardDao
    abstract fun auditDao(): AuditDao
    abstract fun notificationDao(): CaseNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: DurgaDatabase? = null

        fun getInstance(context: Context): DurgaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DurgaDatabase::class.java,
                    "durga_secure_vault.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
