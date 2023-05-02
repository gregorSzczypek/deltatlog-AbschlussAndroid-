package com.example.deltatlog.data.local

import android.content.Context
import android.nfc.Tag
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.deltatlog.data.datamodels.Project

/**
 *  RoomDatabase for storing Projects
 */

@Database(entities = [Project::class], version = 1)
abstract class ProjectDatabase : RoomDatabase() {

    abstract val projectDatabaseDao: ProjectDatabaseDao
}

private lateinit var INSTANCE: ProjectDatabase

// if there's no Database a new one is built
fun getDatabase(context: Context): ProjectDatabase {
    synchronized(ProjectDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ProjectDatabase::class.java,
                "project_database"
            )
                .build()
        }
    }
    Log.i("DatabaseBuilder", "HERE BUILDING DB")
    return INSTANCE
}