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

    @Json(name = "projectId")
    val projectId: Long,

    @Json(name = "NameTask")
    var name: String = "Unnamed",

    @Json(name = "ColorTask")
    val color: String = "#163036",

    @Json(name = "DateTask")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "DurationTask")
    var duration: Long = 0,

    @Json(name = "DescriptionTask")
    var description: String = "No Description",

    @Json(name = "NotesTask")
    var notes: String = "No Notes"
)