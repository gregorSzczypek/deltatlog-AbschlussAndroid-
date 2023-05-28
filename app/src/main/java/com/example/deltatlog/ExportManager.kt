package com.example.deltatlog

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import org.apache.commons.csv.CSVFormat
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ExportManager {

    private fun convertProjectsToCSV(projects: List<Project>): String {
        val header =
            "ID,Name,NameCustomer,CompanyName,Homepage,LogoUrl,Image,Date,Description,Color,NumberOfTasks,TotalTime\n"
        val rows = projects.joinToString("\n") { project ->
            "${project.id},${project.name},${project.nameCustomer},${project.companyName},${project.homepage}," +
                    "${project.logoUrl},${project.image},${project.date},${project.description},${project.color}," +
                    "${project.numberOfTasks},${project.totalTime}"
        }
        return header + rows
    }

    fun exportProjectsToCSV(projects: List<Project>, context: Context) {
        val csvData = convertProjectsToCSV(projects)

        val filename = "project_database.csv"
        val file = File(context.externalCacheDir, filename)

        try {
            FileWriter(file).use { writer ->
                writer.append(csvData)
            }
            Toast.makeText(context, "CSV file exported", Toast.LENGTH_SHORT).show()
            sendEmail(file, context, "project database")
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to export CSV file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun exportTasksToCSV(tasks: List<Task>, context: Context) {

        if (tasks != null && tasks.isNotEmpty()) {
            // Create a CSV file using Apache Commons CSV library
            val csvFile = File(context.cacheDir, "task_database.csv")
            val csvWriter = CSVFormat.DEFAULT.withHeader(
                "Task ID",
                "Task Project ID",
                "Task Name",
                "Color",
                "Date",
                "Duration",
                "Description",
                "Notes",
                "Elapsed Time"
            ).print(csvFile.writer())

            // Write each task to the CSV file
            for (task in tasks) {
                csvWriter.printRecord(
                    task.id,
                    task.taskProjectId,
                    task.name,
                    task.color,
                    task.date,
                    task.duration,
                    task.description,
                    task.notes,
                    task.elapsedTime
                )
            }

            // Close the CSV writer
            csvWriter.close()
            sendEmail(csvFile, context, "task database")
        }
        else {
            Toast.makeText(context, "No tasks found!.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(file: File, context: Context, naming: String) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/csv"
            intent.putExtra(Intent.EXTRA_SUBJECT, "$naming CSV")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Please find attached the $naming in CSV format."
            )
            val uri =
                FileProvider.getUriForFile(context, "com.example.deltatlog.fileprovider", file)
            intent.putExtra(Intent.EXTRA_STREAM, uri)

            // Check if there is an email app available to handle the intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(intent, "Send Email"))
            } else {
                Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
            }
        }
    }
