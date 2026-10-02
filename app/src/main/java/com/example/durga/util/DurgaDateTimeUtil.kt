package com.example.durga.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DurgaDateTimeUtil {

    val IST_TIMEZONE: TimeZone = TimeZone.getTimeZone("Asia/Kolkata")

    data class FormattedDateTime(
        val fullDisplay: String, // e.g. "Friday, 2 October 2026 • 6:15 PM"
        val dayName: String,     // e.g. "Friday"
        val dateString: String,  // e.g. "2 October 2026"
        val timeString: String   // e.g. "6:15 PM"
    )

    fun formatEpochMillis(epochMillis: Long): FormattedDateTime {
        val date = Date(if (epochMillis <= 0L) System.currentTimeMillis() else epochMillis)
        return formatFromDate(date)
    }

    fun formatFromDate(date: Date): FormattedDateTime {
        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH).apply { timeZone = IST_TIMEZONE }
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).apply { timeZone = IST_TIMEZONE }
        val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = IST_TIMEZONE }

        val day = dayFormat.format(date)
        val dateStr = dateFormat.format(date)
        val timeStr = timeFormat.format(date)
        val full = "$day, $dateStr • $timeStr"

        return FormattedDateTime(
            fullDisplay = full,
            dayName = day,
            dateString = dateStr,
            timeString = timeStr
        )
    }

    /**
     * Parses stored incident date and time (e.g. "2026-10-02" and "18:15" or "8:35 PM")
     * and derives the exact day of week, clean date, and clean time in IST.
     */
    fun parseIncidentDateTime(dateInput: String, timeInput: String): FormattedDateTime {
        val cleanDate = dateInput.trim()
        val cleanTime = timeInput.trim()

        if (cleanDate.isBlank()) {
            return FormattedDateTime(
                fullDisplay = "Date & time not specified",
                dayName = "Not specified",
                dateString = "Not specified",
                timeString = cleanTime.ifBlank { "Not specified" }
            )
        }

        // Try standard ISO formats: yyyy-MM-dd, dd/MM/yyyy, d MMM yyyy, etc.
        val supportedDateFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH),
            SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH),
            SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH),
            SimpleDateFormat("d MMM yyyy", Locale.ENGLISH),
            SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
        )

        var parsedCalendar: Calendar? = null
        for (format in supportedDateFormats) {
            format.timeZone = IST_TIMEZONE
            try {
                val d = format.parse(cleanDate)
                if (d != null) {
                    val cal = Calendar.getInstance(IST_TIMEZONE)
                    cal.time = d
                    parsedCalendar = cal
                    break
                }
            } catch (_: Exception) {}
        }

        val dayName: String
        val formattedDate: String
        if (parsedCalendar != null) {
            val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH).apply { timeZone = IST_TIMEZONE }
            val df = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).apply { timeZone = IST_TIMEZONE }
            dayName = dayFormat.format(parsedCalendar.time)
            formattedDate = df.format(parsedCalendar.time)
        } else {
            dayName = "Recorded Date"
            formattedDate = cleanDate
        }

        val formattedTime = if (cleanTime.isNotBlank()) {
            cleanTime
        } else {
            "Time not recorded"
        }

        val full = "$dayName, $formattedDate • $formattedTime"
        return FormattedDateTime(
            fullDisplay = full,
            dayName = dayName,
            dateString = formattedDate,
            timeString = formattedTime
        )
    }
}
