package com.example.durga.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.EvidenceEntity
import java.io.File
import java.io.FileOutputStream

object CaseRecordPdfGenerator {

    /**
     * Generates a clean, official A4 PDF document for the case record.
     */
    fun generateCaseRecordPdf(
        context: Context,
        caseEntity: CaseEntity,
        caseUpdates: List<CaseUpdateEntity> = emptyList(),
        evidenceList: List<EvidenceEntity> = emptyList()
    ): File? {
        val pdfDocument = PdfDocument()

        // Standard A4 dimensions at 72 DPI: 595 x 842 points
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Background
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

        // Header Background Banner
        val navyPrimary = Color.parseColor("#0F2042")
        val textNavy = Color.parseColor("#1B254B")
        val mutedText = Color.parseColor("#64748B")
        val lightBorder = Color.parseColor("#E2E8F0")
        val badgeBg = Color.parseColor("#E8F5E9")
        val badgeGreen = Color.parseColor("#1E7E34")
        val softSectionBg = Color.parseColor("#F8FAFC")

        paint.color = navyPrimary
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, paint)

        // Header Titles
        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("DURGA — WOMEN SAFETY PLATFORM", 32f, 40f, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        paint.color = Color.parseColor("#CBD5E1")
        canvas.drawText("DIGITAL COMPLAINT CASE RECORD & OFFICIAL VERIFICATION DOSSIER", 32f, 62f, paint)

        // Embedded QR Code (Top Right)
        val qrBitmap: Bitmap? = CaseQrHelper.generateQrBitmap(
            content = CaseQrHelper.buildSecureQrPayload(caseEntity.secureToken),
            sizePixels = 120
        )
        if (qrBitmap != null) {
            val qrRect = RectF((pageWidth - 110).toFloat(), 12f, (pageWidth - 30).toFloat(), 92f)
            paint.color = Color.WHITE
            canvas.drawRoundRect(qrRect, 6f, 6f, paint)
            val scaledQr = Bitmap.createScaledBitmap(qrBitmap, 72, 72, true)
            canvas.drawBitmap(scaledQr, (pageWidth - 106).toFloat(), 16f, null)
        }

        var currentY = 115f

        // Case Reference & Status Bar
        paint.color = textNavy
        paint.textSize = 15f
        paint.isFakeBoldText = true
        canvas.drawText("Case ID: ${caseEntity.caseId}", 32f, currentY, paint)

        // Status badge
        val statusText = caseEntity.status.replace("_", " ")
        val badgeWidth = paint.measureText(statusText) + 20f
        val badgeRect = RectF((pageWidth - 32 - badgeWidth), currentY - 14f, (pageWidth - 32).toFloat(), currentY + 6f)
        paint.color = badgeBg
        canvas.drawRoundRect(badgeRect, 4f, 4f, paint)
        paint.color = badgeGreen
        paint.textSize = 10f
        paint.isFakeBoldText = true
        canvas.drawText(statusText, badgeRect.left + 10f, currentY - 1f, paint)

        currentY += 24f

        // Submission Date / Time in IST
        val submittedDt = DurgaDateTimeUtil.formatEpochMillis(caseEntity.createdAt)
        paint.color = mutedText
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Complaint Submitted: ${submittedDt.fullDisplay} (IST)", 32f, currentY, paint)

        currentY += 16f
        paint.color = lightBorder
        paint.strokeWidth = 1f
        canvas.drawLine(32f, currentY, (pageWidth - 32).toFloat(), currentY, paint)

        currentY += 20f

        // Helper function for drawing titled sections
        fun drawSectionBox(
            title: String,
            height: Float,
            contentDraw: (Float) -> Unit
        ) {
            val rect = RectF(32f, currentY, (pageWidth - 32).toFloat(), currentY + height)
            paint.color = softSectionBg
            canvas.drawRoundRect(rect, 8f, 8f, paint)
            paint.color = lightBorder
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(rect, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            // Section Header
            paint.color = navyPrimary
            paint.textSize = 11f
            paint.isFakeBoldText = true
            canvas.drawText(title.uppercase(), 46f, currentY + 20f, paint)

            paint.color = lightBorder
            canvas.drawLine(46f, currentY + 26f, (pageWidth - 46).toFloat(), currentY + 26f, paint)

            contentDraw(currentY + 42f)
            currentY += height + 14f
        }

        // 1. INCIDENT INFORMATION
        val incidentDt = DurgaDateTimeUtil.parseIncidentDateTime(caseEntity.incidentDate, caseEntity.incidentTime)
        drawSectionBox("1. Incident Information", 82f) { startY ->
            paint.textSize = 10f
            paint.isFakeBoldText = false

            // Left column: Date & Day
            paint.color = mutedText
            canvas.drawText("Incident Date & Day:", 46f, startY, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            canvas.drawText("${incidentDt.dayName}, ${incidentDt.dateString}", 170f, startY, paint)

            // Right column: Time
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Incident Time:", 340f, startY, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            canvas.drawText(incidentDt.timeString, 430f, startY, paint)

            // Location
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Incident Location:", 46f, startY + 18f, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            val loc = if (caseEntity.incidentLocation.isNotBlank()) caseEntity.incidentLocation else "Location not provided"
            canvas.drawText(loc.take(65), 170f, startY + 18f, paint)
        }

        // 2. COMPLAINT INFORMATION
        drawSectionBox("2. Complaint Information", 110f) { startY ->
            paint.textSize = 10f

            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Complaint Category:", 46f, startY, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            canvas.drawText(caseEntity.category, 170f, startY, paint)

            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Filed On:", 340f, startY, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            canvas.drawText("${submittedDt.dayName}, ${submittedDt.dateString}", 400f, startY, paint)

            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Complaint Summary:", 46f, startY + 20f, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            canvas.drawText(caseEntity.title.take(70), 170f, startY + 20f, paint)

            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Official Statement:", 46f, startY + 38f, paint)
            paint.color = textNavy
            val statement = caseEntity.structuredDescription.ifBlank { caseEntity.originalDescription }.ifBlank { "Statement filed on record." }
            val line1 = statement.take(75)
            val line2 = if (statement.length > 75) statement.substring(75).take(75) else ""
            canvas.drawText(line1, 170f, startY + 38f, paint)
            if (line2.isNotBlank()) {
                canvas.drawText(line2, 170f, startY + 52f, paint)
            }
        }

        // 3. POLICE / AUTHORITY INFORMATION (Strict: No fake names)
        drawSectionBox("3. Police Station & Official Assignment", 84f) { startY ->
            paint.textSize = 10f

            // Police station
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Police Station:", 46f, startY, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            val station = if (caseEntity.policeStation.isNotBlank()) caseEntity.policeStation else "Not assigned yet"
            canvas.drawText(station, 170f, startY, paint)

            // District
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("District:", 340f, startY, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            val district = if (caseEntity.policeDistrict.isNotBlank()) caseEntity.policeDistrict else "Not assigned yet"
            canvas.drawText(district, 400f, startY, paint)

            // Assigned Inspector
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Assigned Inspector:", 46f, startY + 20f, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            val inspector = if (caseEntity.assignedInspector.isNotBlank()) caseEntity.assignedInspector else "Not assigned yet"
            canvas.drawText(inspector, 170f, startY + 20f, paint)

            // Officer / Investigator
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Investigator:", 340f, startY + 20f, paint)
            paint.color = textNavy
            paint.isFakeBoldText = true
            val officer = if (caseEntity.investigatingOfficer.isNotBlank()) caseEntity.investigatingOfficer else "Not assigned yet"
            canvas.drawText(officer, 420f, startY + 20f, paint)
        }

        // 4. EVIDENCE INFORMATION (Safe Metadata Summary)
        val photoCount = evidenceList.count { it.type.contains("PHOTO", true) }
        val videoCount = evidenceList.count { it.type.contains("VIDEO", true) }
        val audioCount = evidenceList.count { it.type.contains("AUDIO", true) }
        val docCount = evidenceList.count { it.type.contains("DOC", true) || it.type.contains("SCREENSHOT", true) }

        drawSectionBox("4. Evidence Repository (Secure Metadata Only)", 54f) { startY ->
            paint.textSize = 10f
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Attached Evidence:", 46f, startY, paint)

            paint.color = textNavy
            paint.isFakeBoldText = true
            val summary = "$photoCount Photos • $videoCount Video • $audioCount Audio • $docCount Documents (Total ${evidenceList.size} files)"
            canvas.drawText(summary, 170f, startY, paint)

            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText("Note: Cryptographic hashes stored in secure local vault. Private bytes not exposed in QR.", 46f, startY + 16f, paint)
        }

        // 5. CASE TIMELINE (Chronological)
        drawSectionBox("5. Official Case Timeline", 130f) { startY ->
            paint.textSize = 9.5f

            var lineY = startY

            // 1. Created
            paint.color = navyPrimary
            paint.isFakeBoldText = true
            canvas.drawText("● Complaint Created & Stored in Local Vault", 46f, lineY, paint)
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText(submittedDt.fullDisplay, 340f, lineY, paint)
            lineY += 18f

            // 2. Submitted
            paint.color = navyPrimary
            paint.isFakeBoldText = true
            canvas.drawText("● Complaint Submitted to DURGA Central Gateway", 46f, lineY, paint)
            paint.color = mutedText
            paint.isFakeBoldText = false
            canvas.drawText(submittedDt.fullDisplay, 340f, lineY, paint)
            lineY += 18f

            // Real case updates
            val displayUpdates = caseUpdates.take(3)
            if (displayUpdates.isNotEmpty()) {
                for (up in displayUpdates) {
                    val updateDt = DurgaDateTimeUtil.formatEpochMillis(up.timestamp)
                    paint.color = navyPrimary
                    paint.isFakeBoldText = true
                    canvas.drawText("● ${up.status.replace("_", " ")} — ${up.updatedBy}", 46f, lineY, paint)
                    paint.color = mutedText
                    paint.isFakeBoldText = false
                    canvas.drawText(updateDt.fullDisplay, 340f, lineY, paint)
                    lineY += 18f
                }
            } else {
                paint.color = mutedText
                paint.isFakeBoldText = false
                canvas.drawText("● Authority triage pending. Updates will be recorded upon official assignment.", 46f, lineY, paint)
            }
        }

        // Footer / Verification Notice
        paint.color = mutedText
        paint.textSize = 8.5f
        paint.isFakeBoldText = false
        val footerText = "Generated securely via DURGA Safety App • Secure Reference: /case/secure/${caseEntity.secureToken.take(16)}... • Statutory Verification"
        canvas.drawText(footerText, 32f, (pageHeight - 20).toFloat(), paint)

        pdfDocument.finishPage(page)

        // Write to cache
        return try {
            val cacheDir = File(context.cacheDir, "case_records")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val file = File(cacheDir, "DURGA_Case_${caseEntity.caseId.replace('/', '_')}.pdf")
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    /**
     * Opens or shares the generated PDF using Android Intent with FileProvider.
     */
    fun openOrSharePdf(context: Context, pdfFile: File, action: String = Intent.ACTION_VIEW) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = if (action == Intent.ACTION_SEND) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "DURGA Digital Complaint Case Record")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

            val chooser = Intent.createChooser(intent, "Open or Share Case Record PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
