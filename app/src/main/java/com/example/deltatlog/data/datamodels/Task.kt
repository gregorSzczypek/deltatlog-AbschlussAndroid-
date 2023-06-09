package com.example.deltatlog.data.datamodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.time.LocalDateTime

// This dataclass represents the different properties of a task
@Entity(tableName = "taskTable")

data class Task(

    @PrimaryKey(autoGenerate = true)
    @Json(name = "idTask")
    val id: Long = 0,

    @Json(name = "taskProjectId")
    val taskProjectId: Long,

    @Json(name = "NameTask")
    var name: String = "No Task name",

    @Json(name = "ColorTask")
    var color: String = "#163036",

    @Json(name = "DateTask")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "DurationTask")
    var duration: String = "00:00:00",

    @Json(name = "DescriptionTask")
    var description: String = "No Description",

    @Json(name = "NotesTask")
    var notes: String = "No Notes",

    @Json(name = "ElapsedTime")
    var elapsedTime: Long = 0
)