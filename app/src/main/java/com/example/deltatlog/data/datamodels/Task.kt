
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.common.util.Hex
import com.squareup.moshi.Json
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.time.LocalDate
import java.time.LocalDateTime

//@Entity
data class Task(

//    @PrimaryKey(autoGenerate = true)
//    @Json(name = "idTask")
//    val id: Long,

    @Json(name = "customId")
    val customId: Map<String, Boolean> = mutableMapOf("1" to false, "customId" to true),

    @Json(name = "NameTask")
    val name: Map<String, Boolean> = mutableMapOf("Unnamed" to true, "name" to true),

    @Json(name = "ColorTask")
    val color: String = "#163036",

    @Json(name = "DateTask")
    val date: String = LocalDateTime.now().toLocalDate().toString(),

    @Json(name = "DurationTask")
    val duration: Map<String, Boolean> = mutableMapOf("0" to false, "duration" to true),

    @Json(name = "DescriptionTask")
    val description: Map<String, Boolean> = mutableMapOf("No Description" to true, "description" to true),

    @Json(name = "NotesTask")
    val notes: Map<String, Boolean> = mutableMapOf("No Notes" to true, "notes" to true)
)