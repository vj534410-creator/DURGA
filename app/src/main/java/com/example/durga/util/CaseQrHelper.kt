package com.example.durga.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CaseQrHelper {

    private const val SECURE_URI_PREFIX = "/case/secure/"
    private const val FULL_WEB_PREFIX = "https://durga.gov.in/case/secure/"
    private const val DEEP_LINK_PREFIX = "durga://case/secure/"

    /**
     * Builds the standard secure reference payload encoded into the QR code.
     * Note: Does NOT contain private complaint data, names, phone numbers, or photos.
     */
    fun buildSecureQrPayload(secureToken: String): String {
        val cleanToken = secureToken.trim()
        return "$SECURE_URI_PREFIX$cleanToken"
    }

    /**
     * Parses any scanned DURGA QR string to extract the secure token or case ID.
     */
    fun extractTokenFromQr(scannedContent: String?): String? {
        if (scannedContent.isNullOrBlank()) return null
        val trimmed = scannedContent.trim()

        return when {
            trimmed.startsWith(FULL_WEB_PREFIX) -> trimmed.removePrefix(FULL_WEB_PREFIX).trim()
            trimmed.startsWith(DEEP_LINK_PREFIX) -> trimmed.removePrefix(DEEP_LINK_PREFIX).trim()
            trimmed.startsWith(SECURE_URI_PREFIX) -> trimmed.removePrefix(SECURE_URI_PREFIX).trim()
            trimmed.contains(SECURE_URI_PREFIX) -> {
                val idx = trimmed.indexOf(SECURE_URI_PREFIX)
                trimmed.substring(idx + SECURE_URI_PREFIX.length).trim()
            }
            trimmed.matches(Regex("^[a-zA-Z0-9_-]{6,64}$")) -> trimmed
            trimmed.startsWith("DURGA-", ignoreCase = true) || trimmed.startsWith("DG-", ignoreCase = true) -> trimmed
            else -> null
        }
    }

    /**
     * Generates a high-quality Bitmap of the QR code using ZXing.
     */
    fun generateQrBitmap(
        content: String,
        sizePixels: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 1
            )
            val bitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                sizePixels,
                sizePixels,
                hints
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix[x, y]) foregroundColor else backgroundColor
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Decodes a QR code from a Bitmap (e.g. from gallery pick or camera capture).
     */
    fun decodeQrFromBitmap(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val intArray = IntArray(width * height)
            bitmap.getPixels(intArray, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            result.text
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Saves the QR code Bitmap to app cache and returns an Android Share Intent.
     */
    fun shareQrCode(context: Context, qrBitmap: Bitmap, caseId: String): Boolean {
        return try {
            val cacheDir = File(context.cacheDir, "shared_qr")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val file = File(cacheDir, "DURGA_Case_QR_${caseId.replace('/', '_')}.png")
            FileOutputStream(file).use { out ->
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "DURGA Secure Case QR — $caseId")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "DURGA Women Safety Digital Case Reference: $caseId\nScan using the DURGA App to view verified case records."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Save or Share DURGA Case QR"))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Generates a high-resolution, printable Field Officer QR Docket Pass.
     * Contains official header, secure QR code, Case ID, Category, Status, Officer,
     * and verification instructions. Launches system Print/Share chooser.
     */
    fun printOrShareFieldQrPass(
        context: Context,
        caseId: String,
        token: String,
        title: String,
        category: String,
        status: String,
        officerName: String = "Duty Officer"
    ): Boolean {
        return try {
            val qrBitmap = generateQrBitmap(buildSecureQrPayload(token.ifBlank { caseId }), sizePixels = 600)
                ?: return false

            val width = 800
            val height = 1100
            val passBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(passBitmap)
            canvas.drawColor(Color.WHITE)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // Header Banner
            val navyColor = Color.parseColor("#0F2042")
            paint.color = navyColor
            canvas.drawRect(0f, 0f, width.toFloat(), 130f, paint)

            // Header Text
            paint.color = Color.WHITE
            paint.textSize = 28f
            paint.isFakeBoldText = true
            canvas.drawText("DURGA • STATE POLICE & WOMEN SAFETY", 40f, 60f, paint)

            paint.textSize = 18f
            paint.isFakeBoldText = false
            paint.color = Color.parseColor("#93C5FD")
            canvas.drawText("OFFICIAL INVESTIGATION FIELD QR PASS • RAPID LOOKUP", 40f, 95f, paint)

            // Frame around QR
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#CBD5E1")
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }
            val bgPaint = Paint().apply {
                color = Color.parseColor("#F8FAFC")
                style = Paint.Style.FILL
            }
            val qrCardRect = RectF(150f, 160f, 650f, 660f)
            canvas.drawRoundRect(qrCardRect, 24f, 24f, bgPaint)
            canvas.drawRoundRect(qrCardRect, 24f, 24f, borderPaint)

            // Draw QR
            canvas.drawBitmap(qrBitmap, 180f, 190f, null)

            // Case Details
            paint.color = navyColor
            paint.textSize = 30f
            paint.isFakeBoldText = true
            val caseIdText = "CASE ID: $caseId"
            val caseIdWidth = paint.measureText(caseIdText)
            canvas.drawText(caseIdText, (width - caseIdWidth) / 2f, 720f, paint)

            paint.textSize = 20f
            paint.color = Color.parseColor("#334155")
            paint.isFakeBoldText = false
            val titleTrimmed = if (title.length > 40) title.take(37) + "..." else title
            val detailsText = "$category • $titleTrimmed"
            val detailsWidth = paint.measureText(detailsText)
            canvas.drawText(detailsText, (width - detailsWidth) / 2f, 760f, paint)

            // Status Badge
            val statusColor = if (status.contains("RESOLVED", true)) Color.parseColor("#15803D") else Color.parseColor("#B45309")
            val statusBgColor = if (status.contains("RESOLVED", true)) Color.parseColor("#DCFCE7") else Color.parseColor("#FEF3C7")
            val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = statusBgColor
                style = Paint.Style.FILL
            }
            val statusText = "STATUS: ${status.replace("_", " ")}"
            paint.textSize = 20f
            paint.isFakeBoldText = true
            val statusTextWidth = paint.measureText(statusText)
            val badgeRect = RectF((width - statusTextWidth) / 2f - 24f, 790f, (width + statusTextWidth) / 2f + 24f, 835f)
            canvas.drawRoundRect(badgeRect, 12f, 12f, badgePaint)
            paint.color = statusColor
            canvas.drawText(statusText, (width - statusTextWidth) / 2f, 822f, paint)

            // Divider
            canvas.drawLine(50f, 870f, (width - 50f), 870f, borderPaint)

            // Officer & Guidance Info
            paint.color = Color.parseColor("#64748B")
            paint.textSize = 16f
            paint.isFakeBoldText = false
            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy • hh:mm a (IST)", Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            }
            val timestampStr = "Generated: ${sdf.format(Date())}"
            canvas.drawText(timestampStr, 50f, 910f, paint)
            canvas.drawText("Issued To: $officerName • Law Enforcement Jurisdiction", 50f, 940f, paint)

            paint.color = navyColor
            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText("AUTHORITY INSTRUCTIONS FOR FIELD UNITS:", 50f, 985f, paint)

            paint.color = Color.parseColor("#475569")
            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText("1. Scan with DURGA App 'Scan Case QR' for real-time verified record.", 50f, 1015f, paint)
            canvas.drawText("2. Provides cryptographic token verification without exposing private data.", 50f, 1040f, paint)
            canvas.drawText("3. Authorized field officers can update status & timeline milestones.", 50f, 1065f, paint)

            // Save to cache
            val cacheDir = File(context.cacheDir, "field_passes")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val file = File(cacheDir, "DURGA_Field_Pass_${caseId.replace('/', '_')}.png")
            FileOutputStream(file).use { out ->
                passBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "DURGA Officer Field QR Pass — $caseId")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "OFFICIAL FIELD QR PASS\nCase ID: $caseId\nStatus: $status\nScan with DURGA app to view real-time verified investigation records."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Print or Share Field QR Pass")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
