package com.example.durga.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
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
}
