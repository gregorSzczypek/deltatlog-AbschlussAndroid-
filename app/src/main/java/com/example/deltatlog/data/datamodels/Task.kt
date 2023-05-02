package com.example.deltatlog.data.datamodels
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.common.util.Hex
import com.squareup.moshi.Json
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "taskTable")
data class Task(

    @PrimaryKey(autoGenerate = true)
    @Json(name = "idTask")
    val id: Long = 0,

//    @Json(name = "projectId")
//    val projectId: Map<String, Boolean> = mutableMapOf("1" to false, "customId" to true),

    @Json(name = "NameTask")
    val name: String = "Unnamed",

    @Json(name = "ColorTask")
    val color: String = "#163036",

    @Json(name = "DateTask")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "DurationTask")
    val duration: Long = 0,

    @Json(name = "DescriptionTask")
    val description: String = "No Description",

    @Json(name = "NotesTask")
    val notes: String = "No Notes"
)