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
    fun exportToCSV(projects: List<Project>, tasks: List<Task>, context: Context) {
        if (projects.isNotEmpty()) {

            // Create a CSV file using Apache Commons CSV library
            val csvFile = File(context.cacheDir, "workload.csv")
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
            // show text that there are no projects saved
            Toast.makeText(context, "No projects found!.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(file: File, context: Context, naming: String) {

        // Create a new intent for sending an email
        val intent = Intent(Intent.ACTION_SEND)

        // Set the type of the email to "text/csv"
        intent.type = "text/csv"

        // Set the subject of the email to include the provided naming parameter
        intent.putExtra(Intent.EXTRA_SUBJECT, "$naming CSV")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Please find attached the $naming in CSV format."
        )

        // Get the URI for the file using a FileProvider
        val uri =
            FileProvider.getUriForFile(context, "com.example.deltatlog.fileprovider", file)

        // Attach the file to the email intent
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        // Check if there is an email app available to handle the intent
        if (intent.resolveActivity(context.packageManager) != null) {
            // Start the email intent with a chooser dialog
            context.startActivity(Intent.createChooser(intent, "Send Email"))
        } else {
            // Display a toast message if no email app is found
            Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
        }
    }
}
