import com.example.apicalls.data.datamodels.Project
import com.example.apicalls.data.datamodels.Task
import com.example.deltatlog.R

class Datasource {
    fun loadProjects(): List<Project> {
        return listOf<Project>(

            Project(
                1,
                "Project 1",
                "Employer 1",
                R.color.btn_alert_low_sat,
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(
                        1,
                        "Task 1"
                    ),
                    Task(
                        2,
                        "Task 2"
                    )
                )
            ),
            Project(
                2,
                "Project 2",
                "Employer 2",
                R.color.btn_alert_low_sat,
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(
                        1,
                        "Task 1"
                    ),
                    Task(
                        2,
                        "Task 2"
                    )
                )
            ),
            Project(
                3,
                "Project 3",
                "Employer 3",
                R.color.btn_alert_low_sat,
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(
                        1,
                        "Task 1"
                    ),
                    Task(
                        2,
                        "Task 2"
                    )
                )
            ),
            Project(
                4,
                "Project 4",
                "Employer 4",
                R.color.btn_alert_low_sat,
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(
                        1,
                        "Task 1"
                    ),
                    Task(
                        2,
                        "Task 2"
                    )
                )
            ),
            Project(
                5,
                "Project 5",
                "Employer 5",
                R.color.btn_alert_low_sat,
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(
                        1,
                        "Task 1"
                    ),
                    Task(
                        2,
                        "Task 2"
                    )
                )
            ),
            Project(
                6,
                "Project 6",
                "Employer 6",
                R.color.btn_alert_low_sat,
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(
                        1,
                        "Task 1"
                    ),
                    Task(
                        2,
                        "Task 2"
                    )
                )
            ),
            Project(
                7,
                "Project 7",
                "Employer 7",
                R.color.btn_alert_low_sat,
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(
                        1,
                        "Task 1"
                    ),
                    Task(
                        2,
                        "Task 2"
                    )
                )
            ),
            Project(
                8,
                "Project 8",
                "Employer 8",
                R.color.btn_alert_low_sat,
                "01.01.2023",
                "no additional Info",
                listOf(
                    Task(
                        1,
                        "Task 1"
                    ),
                    Task(
                        2,
                        "Task 2"
                    )
                )
            ),
        )
    }
}
