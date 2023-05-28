package com.example.deltatlog.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import org.apache.commons.csv.CSVFormat
import java.io.File

class ExportManager {

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
                "Project ID",
                "Task Name",
                "Date",
                "Duration",
                "Notes",
            ).print(csvFile.writer())

            // Write each task to the CSV file
            for (task in tasks) {
                csvWriter.printRecord(
                    task.taskProjectId,
                    task.name,
                    task.date,
                    task.duration,
                    task.notes,
                )
            }

            // Close the CSV writer
            csvWriter.close()
            sendEmail(csvFile, context, "task database")
        } else {
            Toast.makeText(context, "No tasks found!.", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportAllToCSV(projects: List<Project>, tasks: List<Task>, context: Context) {
        if (projects.isNotEmpty()) {
            // Create a CSV file using Apache Commons CSV library
            val csvFile = File(context.cacheDir, "project_database.csv")
            val csvWriter = CSVFormat.DEFAULT.withHeader(
                "ID",
                "Name",
                "NameCustomer",
                "CompanyName",
                "Date",
                "Description",
                "NumberOfTasks",
                "TotalTime"
            ).print(csvFile.writer())

            // Write each project and its associated tasks to the CSV file
            for (project in projects) {
                // Find tasks associated with the current project
                val associatedTasks = tasks.filter { it.taskProjectId == project.id }

                // Write project details to the CSV file
                csvWriter.printRecord(
                    project.id,
                    project.name,
                    project.nameCustomer,
                    project.companyName,
                    project.date,
                    project.description,
                    associatedTasks.size, // Number of associated tasks
                    project.totalTime
                )

                // Write each associated task to the CSV file
                for (task in associatedTasks) {
                    csvWriter.printRecord(
                        task.taskProjectId,
                        task.name,
                        project.nameCustomer,
                        project.companyName,
                        task.date,
                        task.notes,
                        "",
                        task.duration
                    )
                }
            }

            // Close the CSV writer
            csvWriter.close()

            // Send the project database CSV file via email
            sendEmail(csvFile, context, "Database")
        } else {
            Toast.makeText(context, "No projects found!.", Toast.LENGTH_SHORT).show()
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
