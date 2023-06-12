package com.example.deltatlog.data.datamodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.deltatlog.R
import com.squareup.moshi.Json
import java.time.LocalDateTime

// This dataclass represents the different properties of a project
@Entity(tableName = "projectTable")

data class Project(

    @PrimaryKey(autoGenerate = true)
    @Json(name = "idProject")
    val id: Long = 0,

    @Json(name = "strNameProject")
    var name: String = "No Project name",

    @Json(name = "strCustomerProject")
    var nameCustomer: String = "No customer name",

    @Json(name = "strCompanyName")
    var companyName: String = "",

    @Json(name = "strHomepage")
    var homepage: String = "",

    @Json(name = "logoUrl")
    var logoUrl: String = "",

    @Json(name = "imageProject")
    var image: Int = R.drawable.applogo,

    @Json(name = "strDateProject")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "strDescriptionProject")
    var description: String = "No description",

    @Json(name = "ColorProject")
    var color: String = "#54948B",

    @Json(name = "longNumberOfTasks")
    var numberOfTasks: Long = 0,

    @Json(name = "strTotalTime")
    var totalTime: String = "00:00:00",
)