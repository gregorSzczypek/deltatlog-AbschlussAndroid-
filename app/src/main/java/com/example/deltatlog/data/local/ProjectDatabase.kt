package com.example.deltatlog.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.deltatlog.data.datamodels.Project

// RoomDatabase for storing Projects

@Database(entities = [Project::class], version = 2)
abstract class ProjectDatabase : RoomDatabase() {
    abstract val projectDatabaseDao: ProjectDatabaseDao
}

private lateinit var INSTANCE: ProjectDatabase

// if there's no Database a new one is built
fun getProjectDatabase(context: Context): ProjectDatabase {
    synchronized(ProjectDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ProjectDatabase::class.java,
                "project_database"
            )
//                .fallbackToDestructiveMigration() // TODO THIS CODE NEEDS TO BE DELETED OTHERWISE DATA COULD GET LOST
                .build()
        }
    }
    Log.i("DatabaseBuilder", "here bulding db")
    return INSTANCE
}