package com.example.apicalls.data.datamodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class Task(

    @PrimaryKey
    @Json(name = "idTask")
    val id: Long,

    @Json(name = "strNameTask")
    val name: String,

//    @Json(name = "intColorTask")
//    val color: Int,

//    @Json(name = "imageTask")
//    val image: String,

//    @Json(name = "strDateTask")
//    val date: String,

//    @Json(name = "strDurationTask")
//    val addInfo: String

)