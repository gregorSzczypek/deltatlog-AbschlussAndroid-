package com.example.deltatlog

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import org.apache.commons.csv.CSVFormat
import java.io.File

class ExportManager {

    fun exportProjectsToCSV(projects: List<Project>, context: Context) {

        // Export the list of projects to a CSV file
        // - Create a CSV file using Apache Commons CSV library
        // - Write each task to the CSV file
        // - Show a toast message indicating the export status
        // - Send an email with the CSV file as an attachment

        if (projects.isNotEmpty()) {
            // Create a CSV file using Apache Commons CSV library
            val csvFile = File(context.cacheDir, "project_database.csv")
            val csvWriter = CSVFormat.DEFAULT.withHeader(
                "ID",
                "Name",
                "NameCustomer",
                "CompanyName",
                "Homepage",
                "LogoUrl",
                "Image",
                "Date",
                "Description",
                "Color",
                "NumberOfTasks",
                "TotalTime"
            ).print(csvFile.writer())

            // Write each task to the CSV file
            for (project in projects) {
                csvWriter.printRecord(
                    project.id,
                    project.name,
                    project.nameCustomer,
                    project.companyName,
                    project.homepage,
                    project.logoUrl,
                    project.image,
                    project.date,
                    project.description,
                    project.color,
                    project.numberOfTasks,
                    project.totalTime
                )
            }

            // Close the CSV writer
            csvWriter.close()
            sendEmail(csvFile, context, "project database")
        }
        else {
            Toast.makeText(context, "No projects found!.", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportTasksToCSV(tasks: List<Task>, context: Context) {

        // Export the list of tasks to a CSV file
        // - Create a CSV file using Apache Commons CSV library
        // - Write each task to the CSV file
        // - Show a toast message indicating the export status
        // - Send an email with the CSV file as an attachment

        if (tasks.isNotEmpty()) {
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

        // Create an email intent with the necessary data
        // - Set the email type as "text/csv"
        // - Set the subject and body of the email
        // - Attach the CSV file using a FileProvider
        // - Start an activity to choose an email app and send the email

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
