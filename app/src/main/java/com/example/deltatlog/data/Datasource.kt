import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Project

class Datasource {
    fun loadProjects(): List<Project> {
        return listOf<Project>(

            Project(
                1,
                "Project",
                "Employer",
                "#ffffff",
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(1),
                    Task(2),
                    Task(3),
                    Task(4),
                    Task(5),
                    Task(6),
                    Task(7),
                    Task(8)
                )
            ),
            Project(
                1,
                "Project",
                "Employer",
                "#ffffff",
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(1),
                    Task(2),
                    Task(3),
                    Task(4),
                    Task(5),
                    Task(6),
                    Task(7),
                    Task(8)
                )
            ),
            Project(
                1,
                "Project",
                "Employer",
                "#ffffff",
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(1),
                    Task(2),
                    Task(3),
                    Task(4),
                    Task(5),
                    Task(6),
                    Task(7),
                    Task(8)
                )
            )
        )
    }

    fun loadTasks(project: Project): List<Task> {
        val tasks = project.tasks
        return tasks
    }

    fun loadTaskAttributes(task: Task): List<Map<String, Boolean>> {
        val taskAttr = listOf<Map<String, Boolean>>(
            task.duration,
            task.description,
            task.notes,
            task.customId
        )
        return taskAttr
    }
}
