package com.example.deltatlog.data.datamodels
import Task
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.time.LocalDateTime

@Entity(tableName = "projectTable")
data class Project(

    @PrimaryKey(autoGenerate = true)
    @Json(name = "idProject")
    val id: Long = 0,

    @Json(name = "strNameProject")
    val name: String,

    @Json(name = "strCustomerProject")
    val nameCustomer: String,

    @Json(name = "intColorProject")
    val color: String = "#ffffff",

//    @Json(name = "imageProject") (??)
//    val image: String,

    @Json(name = "strDateProject")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "strDescriptionProject")
    val description: String

//    @Json(name = "listTasksProject")
//    val tasks: List<Task>,

//    @Json(name = "intNumberOfTasks")
//    val numberOfTasks: Int = tasks.size,

//    @Json(name = "intNumberOfOpenTasks")
//    val numberOfOpenTasks: Int = 0

)