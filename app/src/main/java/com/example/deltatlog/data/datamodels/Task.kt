package com.example.apicalls.data.datamodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.common.util.Hex
import com.squareup.moshi.Json
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class Task(

    @PrimaryKey
    @Json(name = "idTask")
    val id: Long,

    @Json(name = "strNameTask")
    val name: String = "Unnamed",

    @Json(name = "intColorTask")
    val color: String = "#ffffff",

    @Json(name = "strDateTask")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "strDurationTask")
    val duration: Int = 0,

    @Json(name = "strDescriptionTask")
    val description: String = "No Description",

    @Json(name = "strNotesTask")
    val notes: String = "No Notes"

)