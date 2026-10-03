package com.example.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import com.example.model.SavedDrawing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DrawingRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("kids_drawings_meta", Context.MODE_PRIVATE)
    private val drawingsDir: File by lazy {
        File(context.filesDir, "drawings").apply {
            if (!exists()) mkdirs()
        }
    }

    suspend fun saveDrawing(bitmap: Bitmap, customTitle: String? = null): SavedDrawing =
        withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString().take(8)
            val timestamp = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
            val dateText = dateFormat.format(Date(timestamp))
            val title = customTitle?.ifBlank { null } ?: "Masterpiece #$id"

            val file = File(drawingsDir, "drawing_$id.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val savedItem = SavedDrawing(
                id = id,
                title = title,
                filePath = file.absolutePath,
                dateText = dateText,
                timestamp = timestamp
            )

            // Save to metadata list
            val currentList = loadDrawingsInternal().toMutableList()
            currentList.add(0, savedItem)
            saveDrawingsInternal(currentList)

            savedItem
        }

    suspend fun getSavedDrawings(): List<SavedDrawing> = withContext(Dispatchers.IO) {
        loadDrawingsInternal().filter { File(it.filePath).exists() }
    }

    suspend fun deleteDrawing(id: String): Boolean = withContext(Dispatchers.IO) {
        val list = loadDrawingsInternal().toMutableList()
        val item = list.find { it.id == id }
        if (item != null) {
            val file = File(item.filePath)
            if (file.exists()) {
                file.delete()
            }
            list.remove(item)
            saveDrawingsInternal(list)
            true
        } else {
            false
        }
    }

    fun getShareIntent(drawing: SavedDrawing): Intent? {
        val file = File(drawing.filePath)
        if (!file.exists()) return null

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, drawing.title)
            putExtra(Intent.EXTRA_TEXT, "Look at my drawing made with Kids Draw! 🎨✨")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun loadDrawingsInternal(): List<SavedDrawing> {
        val jsonString = prefs.getString("drawings_list", "[]") ?: "[]"
        val list = mutableListOf<SavedDrawing>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SavedDrawing(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        filePath = obj.getString("filePath"),
                        dateText = obj.getString("dateText"),
                        timestamp = obj.optLong("timestamp", 0L)
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }

    private fun saveDrawingsInternal(list: List<SavedDrawing>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("filePath", item.filePath)
                put("dateText", item.dateText)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("drawings_list", array.toString()).apply()
    }
}
