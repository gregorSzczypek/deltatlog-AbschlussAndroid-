package com.example.deltatlog.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.deltatlog.data.datamodels.Project

/**
 * Data Access Object for projectdatabase
 */

@Dao
interface ProjectDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(projects: List<Project>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: Project)

    //ToDo: Update
    @Update
    suspend fun update(project: Project)

    @Query("SELECT * FROM projectTable")
    fun getAll(): LiveData<List<Project>>

    //Todo: Delete by id
    @Query("DELETE FROM projectTable WHERE id = :id")
    suspend fun deleteById(id: Long)
}