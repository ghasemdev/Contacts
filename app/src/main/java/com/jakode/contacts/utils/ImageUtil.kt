package com.jakode.contacts.utils

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.random.Random

object ImageUtil {
    @Synchronized
    fun saveFilePrivate(context: Context, data: Uri): String? {
        return try {
            // Build a random name and save in memory
            val fileName = UUID.randomUUID().toString() + "." + uriToExtension(context, data)
            context.openFileOutput(fileName, Context.MODE_PRIVATE).write(uriToByte(context, data))
            fileName
        } catch (e: Exception) {
            null
        }
    }

    @Synchronized
    fun loadFilePrivate(context: Context, name: String): File {
        val file = File(context.cacheDir, name)

        return if (file.exists()) file else {
            val byteArray = context.openFileInput(name).readBytes()
            FileOutputStream(file).apply { write(byteArray) }
            file
        }
    }

    fun setDefaultImage(context: Context, target: ImageView, coverName: String? = null): String {
        var cover = coverName
        if (cover == null) {
            val random = Random.nextInt(1, 10).toString()
             cover = "cover_$random"
        }

        target.setImageResource(getResources(context, cover))
        return cover
    }

    fun setImage(imageUri: Uri, target: ImageView) {
        Picasso.get().load(imageUri).centerCrop().fit().into(target)
    }

    /**
     * Uri convert to byte array
     * @param context
     * @param data
     * @return ByteArray
     */
    private fun uriToByte(context: Context, data: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(data)!!
        val byteArrayOutput = ByteArrayOutputStream()

        byteArrayOutput.write(inputStream.readBytes())
        return byteArrayOutput.toByteArray()
    }

    /**
     * Uri to extension
     * @param context
     * @param data
     * @return String include type ot empty
     */
    private fun uriToExtension(context: Context, data: Uri): String {
        val contentResolver = context.contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(data)) ?: ""
    }

    /**
     * Get resource from name
     * @param context
     * @param coverName
     * @return Int resource
     */
    private fun getResources(context: Context, coverName: String): Int {
        val resources: Resources = context.resources
        return resources.getIdentifier(
            coverName, "drawable",
            context.packageName
        )
    }
}