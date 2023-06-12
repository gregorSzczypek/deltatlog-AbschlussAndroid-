package com.example.deltatlog.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.deltatlog.data.datamodels.Task

//RoomDatabase for storing Tasks

@Database(entities = [Task::class], version = 2)
abstract class TaskDatabase : RoomDatabase() {

    abstract val taskDatabaseDao: TaskDatabaseDao
}

private lateinit var INSTANCE: TaskDatabase

// if there's no Database a new one is built
fun getTaskDatabase(context: Context): TaskDatabase {
    synchronized(TaskDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TaskDatabase::class.java,
                "task_database"
            )
                .fallbackToDestructiveMigration() // TODO THIS CODE NEEDS TO BE DELETED OTHERWISE DATA COULD GET LOST
                .build()
        }
    }
    Log.i("DatabaseBuilder", "here building database")
    return INSTANCE
}