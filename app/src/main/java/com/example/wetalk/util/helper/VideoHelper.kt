package com.example.wetalk.util.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.text.TextUtils
import com.example.wetalk.WeTalkApp
import com.example.wetalk.data.local.VideoBody
import com.example.wetalk.data.local.VideoLocal
import com.example.wetalk.util.helper.FileHelper.getDataFolder
import com.google.gson.Gson


class VideoHelper private constructor() :
    SQLiteOpenHelper(WeTalkApp.get(), DB_NAME, null, DB_VERSION) {
    private var database: SQLiteDatabase? = null

    object TableProvide : BaseColumns {
        const val TABLE_NAME = "provide"
        const val ID = "id"
        const val TIME = "time"
        const val TITLE = "title"
        const val BODY_JSON = "body_json"
        const val TAG_JSON = "tag_json"
        const val IS_ACTIVE = "isActive"
    }

    val dataPath: String
        get() = DB_PATH + DB_NAME

    init {
        DB_PATH =
            WeTalkApp.get().getDatabasePath(DB_NAME).getAbsolutePath() // Use DB_NAME directly
        openDataBase()
    }

    private fun openDataBase() {
        if (database == null || !database!!.isOpen) {
            database =
                this.writableDatabase // Use getWritableDatabase to ensure the database is created if not exists
        }
    }

    @Synchronized
    override fun close() {
        if (database != null) {
            database!!.close()
        }
        super.close()
    }

    fun stop() {
        close()
        dataHelper = null
        database = null
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Define your database schema and initial data if needed
        // For example, you can create your "provide" table here
        // You can use execSQL to execute SQL statements
        db.execSQL(
            "CREATE TABLE " + TableProvide.TABLE_NAME + " ("
                    + TableProvide.ID + " INTEGER PRIMARY KEY, "
                    + TableProvide.TIME + " TEXT, "
                    + TableProvide.TITLE + " TEXT, "
                    + TableProvide.BODY_JSON + " TEXT, "
                    + TableProvide.TAG_JSON + " TEXT, "
                    + TableProvide.IS_ACTIVE + " INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema upgrades here if needed
        // For example, you can add or modify tables and columns
    }

    fun deleteProvide(provide: VideoLocal) {
        // Xóa dữ liệu từ bảng
        database!!.execSQL("DELETE FROM " + TableProvide.TABLE_NAME + " WHERE " + TableProvide.ID + " = '" + provide.id + "'")
    }

    fun deleteActiveProvide(provide: VideoLocal) {
        // Cập nhật trạng thái isActive
        val contentValues = ContentValues()
        contentValues.put(TableProvide.IS_ACTIVE, 0)
        database!!.update(
            TableProvide.TABLE_NAME,
            contentValues,
            TableProvide.ID + " = " + provide.id,
            null
        )
    }

    fun restoreDiary(provide: VideoLocal) {
        // Cập nhật trạng thái isActive
        val contentValues = ContentValues()
        contentValues.put(TableProvide.IS_ACTIVE, 1)
        database!!.update(
            TableProvide.TABLE_NAME,
            contentValues,
            TableProvide.ID + " = " + provide.id,
            null
        )
    }

    fun clearDiaryNotActive() {
        database!!.execSQL("DELETE FROM " + TableProvide.TABLE_NAME + " WHERE " + TableProvide.IS_ACTIVE + " = 0 ")
    }

    fun addVideo(provide: VideoLocal): VideoLocal {
        val gson = Gson()
        val folder = getDataFolder()
        val jsonBody = gson.toJson(provide.videoBodies)
        val newJsonBody = jsonBody.replace(folder.toRegex(), "data_folder_h1x2hfv")
        val contentValues = ContentValues()
        contentValues.put(TableProvide.TIME, provide.time)
        contentValues.put(TableProvide.TITLE, provide.title)
        contentValues.put(TableProvide.BODY_JSON, newJsonBody)
        contentValues.put(TableProvide.TAG_JSON, provide.videoTag)
        contentValues.put(TableProvide.IS_ACTIVE, provide.isActive)
        val id = database!!.insert(TableProvide.TABLE_NAME, null, contentValues)
        provide.id = id
        return provide
    }

    fun updateProvide(provide: VideoLocal): Long {
        val gson = Gson()
        val folder = getDataFolder()
        val jsonBody = gson.toJson(provide.videoBodies)
        val newJsonBody = jsonBody.replace(folder.toRegex(), "data_folder_h1x2hfv")
        val contentValues = ContentValues()
        contentValues.put(TableProvide.TIME, provide.time)
        contentValues.put(TableProvide.TITLE, provide.title)
        contentValues.put(TableProvide.BODY_JSON, newJsonBody)
        contentValues.put(TableProvide.TAG_JSON, provide.videoTag)
        contentValues.put(TableProvide.IS_ACTIVE, provide.isActive)
        return database!!.update(
            TableProvide.TABLE_NAME,
            contentValues,
            TableProvide.ID + " = " + provide.id,
            null
        ).toLong()
    }

    fun getVideos(where: String, limit: String): ArrayList<VideoLocal> {
        // Truy vấn dữ liệu từ bảng
        val gson = Gson()
        val folder = getDataFolder()
        val diaries: ArrayList<VideoLocal> = ArrayList<VideoLocal>()
        val cursor = database!!.rawQuery(
            "SELECT * FROM " + TableProvide.TABLE_NAME + " " + where + " ORDER BY " + TableProvide.TIME + " DESC " + limit,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(0)
                val time = cursor.getLong(1)
                val title = cursor.getString(2)
                val bodyJson = cursor.getString(3)
                val tagJson = cursor.getString(4)
                val isActive = cursor.getInt(5)
                val newJsonBody = bodyJson.replace("data_folder_h1x2hfv".toRegex(), folder)
                val videoBody: VideoBody = gson.fromJson(newJsonBody, VideoBody::class.java)
                if (isActive == 1) {
                    diaries.add(VideoLocal(id, time, title, videoBody, tagJson, 1))
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return diaries
    }

    fun getVideo(id: Long): VideoLocal? {
        val gson = Gson()
        val folder = getDataFolder()
        val cursor = database!!.rawQuery(
            "SELECT * FROM " + TableProvide.TABLE_NAME + " WHERE " + TableProvide.ID + " = '" + id + "'",
            null
        )
        var diary: VideoLocal? = null
        if (cursor != null && cursor.moveToFirst()) {
            val time = cursor.getLong(1)
            val title = cursor.getString(2)
            val bodyJson = cursor.getString(3)
            val tagJson = cursor.getString(4)
            val isActive = cursor.getInt(5)
            val newJsonBody = bodyJson.replace("data_folder_h1x2hfv".toRegex(), folder)
            val videoBody: VideoBody = gson.fromJson(newJsonBody, VideoBody::class.java)
            diary = VideoLocal(id, time, title, videoBody, tagJson, isActive)
            cursor.close()
        }
        return diary
    }

    @get:SuppressLint("Range")
    val allProvideData: List<ContentValues>
        get() {
            val provideList: MutableList<ContentValues> = ArrayList()
            val db = this.readableDatabase
            val columns = arrayOf(
                TableProvide.ID,
                TableProvide.TIME,
                TableProvide.TITLE,
                TableProvide.BODY_JSON,
                TableProvide.TAG_JSON,
                TableProvide.IS_ACTIVE
            )
            val cursor = db.query(
                TableProvide.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val values = ContentValues()
                    values.put(
                        TableProvide.ID,
                        cursor.getLong(cursor.getColumnIndex(TableProvide.ID))
                    )
                    values.put(
                        TableProvide.TIME, cursor.getString(
                            cursor.getColumnIndex(
                                TableProvide.TIME
                            )
                        )
                    )
                    values.put(
                        TableProvide.TITLE, cursor.getString(
                            cursor.getColumnIndex(
                                TableProvide.TITLE
                            )
                        )
                    )
                    values.put(
                        TableProvide.BODY_JSON, cursor.getString(
                            cursor.getColumnIndex(
                                TableProvide.BODY_JSON
                            )
                        )
                    )
                    values.put(
                        TableProvide.TAG_JSON, cursor.getString(
                            cursor.getColumnIndex(
                                TableProvide.TAG_JSON
                            )
                        )
                    )
                    values.put(
                        TableProvide.IS_ACTIVE, cursor.getInt(
                            cursor.getColumnIndex(
                                TableProvide.IS_ACTIVE
                            )
                        )
                    )
                    provideList.add(values)
                } while (cursor.moveToNext())
                cursor.close()
            }
            return provideList
        }
    val homeProvides: ArrayList<VideoLocal>
        get() = provides
    val provides: ArrayList<VideoLocal>
        get() = getVideos("", "")

    fun search(toString: String): ArrayList<VideoLocal> {
        val replace = toString.replace("'".toRegex(), "")
        if (TextUtils.isEmpty(replace)) {
            return ArrayList<VideoLocal>()
        }
        val where =
            (" WHERE body_json LIKE '%" + replace + "%' OR " + " location_json LIKE '%" + replace + "%' OR " + " tag_json LIKE '%" + replace + "%' OR "
                    + " title LIKE '%" + replace + "%'")
        return getVideos(where, "")
    }

    companion object {
        private lateinit var DB_PATH: String

        // Database file name
        private const val DB_NAME = "video_local.db"
        private const val DB_VERSION = 1
        private var dataHelper: VideoHelper? = null
        fun get(): VideoHelper? {
            if (dataHelper == null) {
                dataHelper = VideoHelper()
            }
            return dataHelper
        }
    }
}