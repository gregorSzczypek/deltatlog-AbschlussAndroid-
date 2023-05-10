package com.example.deltatlog.data.datamodels

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.deltatlog.R
import com.squareup.moshi.Json
import java.time.LocalDateTime

@Entity(tableName = "projectTable")

data class Project(

    @PrimaryKey(autoGenerate = true)
    @Json(name = "idProject")
    val id: Long = 0,

    @Json(name = "strNameProject")
    var name: String,

    @Json(name = "strCustomerProject")
    var nameCustomer: String = "No customer name",

//    @Json(name = "intColorProject")
//    val color: String = "#ffffff",

    @Json(name = "imageProject")
    var image: Int = R.drawable.applogo,

    @Json(name = "strDateProject")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "strDescriptionProject")
    var description: String = "No description",

//    @Json(name = "intNumberOfTasks")
//    val numberOfTasks: Int = tasks.size,

//    @Json(name = "intNumberOfOpenTasks")
//    val numberOfOpenTasks: Int = 0

)