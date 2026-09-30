package com.example.durga.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.durga.model.CaseNotificationEntity

object DurgaNotificationHelper {

    const val CHANNEL_ID = "durga_case_updates_channel"
    const val CHANNEL_NAME = "DURGA Case Progress & Police Actions"

    const val APP_UPDATES_CHANNEL_ID = "durga_app_updates_channel"
    const val APP_UPDATES_CHANNEL_NAME = "DURGA App Updates & New Features"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // Channel 1: Case Progress & Police Updates
            val caseChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Live notifications for complaint progress, investigation updates, and police actions"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
            }
            manager.createNotificationChannel(caseChannel)

            // Channel 2: App Feature Releases & System Updates
            val updateChannel = NotificationChannel(
                APP_UPDATES_CHANNEL_ID,
                APP_UPDATES_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new features, security updates, and enhancements in DURGA app"
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
            }
            manager.createNotificationChannel(updateChannel)
        }
    }

    fun postCaseProgressNotification(context: Context, notification: CaseNotificationEntity) {
        initNotificationChannels(context)

        val prefs = context.getSharedPreferences("durga_app_settings", Context.MODE_PRIVATE)
        val isDiscreetMode = prefs.getBoolean("discreet_notifications_enabled", true)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("NAVIGATE_TO", "case_notifications")
            putExtra("TARGET_CASE_ID", notification.caseId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notification.caseId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, summaryLine, bigText) = if (isDiscreetMode) {
            Triple(
                "System Security & Sync",
                "Device verification status updated",
                "DURGA Security Engine: Routine system status verified successfully."
            )
        } else {
            val actionEmoji = if (notification.isActionTaken) "✅" else "⏳"
            val actionHeader = if (notification.isActionTaken) "OFFICIAL ACTION RECORDED" else "REVIEW IN PROGRESS"
            val fullDetails = buildString {
                append("📌 Reference: ${notification.caseId}\n")
                append("📊 Progress: ${notification.progressPercent}%\n")
                append("🛡️ Status: ${notification.stageTitle}\n")
                append("🚨 Action: $actionEmoji $actionHeader\n")
                append("📝 Details: ${notification.actionDetails}\n")
                if (notification.officerName.isNotBlank()) append("👮 Officer: ${notification.officerName}\n")
                if (notification.policeStation.isNotBlank()) append("🏢 Station: ${notification.policeStation}")
            }
            Triple(
                "📋 Case ${notification.caseId} (${notification.progressPercent}%)",
                "${notification.stageTitle} • $actionEmoji ${notification.actionStatusText}",
                fullDetails
            )
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(summaryLine)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setProgress(100, notification.progressPercent, false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notification.caseId.hashCode(), builder.build())
        } catch (_: SecurityException) {
            // Android 13+ permission not granted yet - fallback gracefully
        }
    }

    fun postAppUpdateNotification(context: Context, notification: CaseNotificationEntity) {
        initNotificationChannels(context)

        val targetRoute = notification.targetRoute.ifBlank { "case_notifications" }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("NAVIGATE_TO", targetRoute)
            putExtra("NOTIFICATION_ID", notification.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bigText = buildString {
            append("✨ ${notification.title}\n")
            if (notification.stageTitle.isNotBlank()) append("🏷️ Version / Tag: ${notification.stageTitle}\n")
            append("📝 What's New:\n${notification.actionDetails}\n\n")
            append("👉 Tap to explore the new feature inside DURGA.")
        }

        val title = "🚀 નવું ફીચર / New App Feature: ${notification.title}"
        val summaryLine = "${notification.stageTitle.ifBlank { "DURGA Update" }} • New features ready"

        val builder = NotificationCompat.Builder(context, APP_UPDATES_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(summaryLine)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notification.id.hashCode(), builder.build())
        } catch (_: SecurityException) {
            // Android 13+ permission not granted yet
        }
    }
}
