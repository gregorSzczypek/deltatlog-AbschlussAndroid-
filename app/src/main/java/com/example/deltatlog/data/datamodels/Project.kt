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
    var name: String = "No Project name",

    @Json(name = "strCustomerProject")
    var nameCustomer: String = "No customer name",

    @Json(name = "imageProject")
    var image: Int = R.drawable.applogo,

    @Json(name = "strDateProject")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "strDescriptionProject")
    var description: String = "No description",

    @Json(name = "ColorProject")
    var color: String = "#163036"

//    @Json(name = "intNumberOfTasks")
//    val numberOfTasks: Int = tasks.size,

)