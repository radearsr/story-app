package com.storyapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.Uri
import android.text.format.DateUtils
import android.util.Patterns
import androidx.exifinterface.media.ExifInterface
import com.storyapp.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
private const val MAXIMAL_SIZE = 1000000

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

fun validEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validPassword(password: String?): Boolean {
    return password != null && password.length >= 8
}

fun getTimeAgo(context: Context, timestamp: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    try {
        val time = sdf.parse(timestamp)?.time ?: return ""
        val now = System.currentTimeMillis()
        val diff = now - time

        return when {
            diff < DateUtils.MINUTE_IN_MILLIS -> context.getString(R.string.txt_just_now)
            diff < DateUtils.HOUR_IN_MILLIS -> {
                val minutes = (diff / DateUtils.MINUTE_IN_MILLIS).toInt()
                if (minutes == 1)
                    context.getString(R.string.txt_a_minutes)
                else
                    context.getString(R.string.txt_minutes_ago, minutes)
            }
            diff < DateUtils.DAY_IN_MILLIS -> {
                val hours = (diff / DateUtils.HOUR_IN_MILLIS).toInt()
                if (hours == 1)
                    context.getString(R.string.txt_an_hour_ago)
                else
                    context.getString(R.string.txt_hour_ago, hours)
            }
            diff < DateUtils.WEEK_IN_MILLIS -> {
                val days = (diff / DateUtils.DAY_IN_MILLIS).toInt()
                if (days == 1)
                    context.getString(R.string.txt_yesterday)
                else
                    context.getString(R.string.txt_hour_ago, days)
            }
            else -> DateUtils.getRelativeTimeSpanString(time, now, DateUtils.DAY_IN_MILLIS).toString()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return ""
}