package com.example.apicalls.data.datamodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class Project(

    @PrimaryKey
    @Json(name = "idProject")
    val id: Long,

    @Json(name = "strNameProject")
    val name: String,

    @Json(name = "strNameEmployerProject")
    val nameEmployer: String,

    @Json(name = "intColorProject")
    val severityColor: Int,

//    @Json(name = "imageProject")
//    val image: String,

    @Json(name = "strDateProject")
    val date: String,

    @Json(name = "strAdditionalInfoProject")
    val addInfo: String,

    @Json(name = "listTasksProject")
    val tasks: List<Task>

)