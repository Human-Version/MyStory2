package com.dicoding.android.mystory2.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

var BASE_URL = "https://story-api.dicoding.dev/v1/"
const val RETROFIT_TAG = "Retrofit"
const val EXTRA_IMAGE = "extra_image"
const val STORY_NAME = "story_name"
const val EXTRA_DESCRIPTION = "extra_description"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    val ei = ExifInterface(file.path)
    val orientation: Int = ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )

    var rotatedBitmap: Bitmap? = null
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap =
            TransformationUtils.rotateImage(bitmap, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
            TransformationUtils.rotateImage(bitmap, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
            TransformationUtils.rotateImage(bitmap, 270)
        ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
        else -> rotatedBitmap = bitmap
    }

    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)

    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}
