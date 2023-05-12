import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.deltatlog.R
import com.example.deltatlog.viewModel
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.ui.HomeFragmentDirections
import com.example.deltatlog.ui.TaskFragmentDirections
import com.google.android.material.card.MaterialCardView


class TaskAdapter(
    private var viewModel: viewModel,
    private var context: Context,
    private var dataset: List<Task>,
    private var navController: NavController,
    private var projectId: Long

) : RecyclerView.Adapter<TaskAdapter.ItemViewHolder>() {

    // parts of the item which need to be change by adapter
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val taskCardView = view.findViewById<MaterialCardView>(R.id.task_card_view)
        val taskName = view.findViewById<TextView>(R.id.task_name)
        val taskDuration = view.findViewById<TextView>(R.id.duration)
        val taskDate = view.findViewById<TextView>(R.id.date)
        val taskDescription = view.findViewById<TextView>(R.id.task_description)
        val playButton = view.findViewById<ImageButton>(R.id.imageButton)
    }

    // create new viewholders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ItemViewHolder {


        // inflate item layout
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_task, parent, false)

        // return viewholder
        return ItemViewHolder(adapterLayout)
    }

//    private val timers = mutableMapOf<Long, java.util.Timer>()

    // recyclingprocess
    // set parameters
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.taskName.text = item.name
        holder.taskDate.text = item.date
//        holder.taskCardView.setCardBackgroundColor(Color.parseColor(item.color))
        holder.taskDuration.text = item.duration.toString()
        holder.taskDescription.text = item.notes

//        if (item.isTimerRunning) {
//            viewModel.startTimer(holder, item, position, item.id, timers)
//        }

        // Set an OnTouchListener on the item view
        holder.taskCardView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start the jump animation on the view
                    val animator = ObjectAnimator.ofPropertyValuesHolder(
                        v,
                        PropertyValuesHolder.ofFloat("scaleX", 0.9f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.9f),
                    )
                    animator.duration = 100
                    animator.start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val animator = ObjectAnimator.ofPropertyValuesHolder(
                        v,
                        PropertyValuesHolder.ofFloat("scaleX", 1f),
                        PropertyValuesHolder.ofFloat("scaleY", 1f)
                    )
                    animator.duration = 500
                    animator.start()
                }
            }
            false
        }

        // set an OnLongClickListener on the item view
        holder.taskCardView.setOnLongClickListener {

            val menuItems = arrayOf("Rename Task", "Delete Task")

            val popupMenu = PopupMenu(context, holder.taskCardView)
            menuItems.forEach { popupMenu.menu.add(it) }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Rename Task" -> {
                        // Handle Rename task action
                        // TODO Rename tasks actions should be implemented here
                        holder.taskName.isEnabled = true

                        holder.taskCardView.setOnClickListener {
                            item.name = holder.taskName.text.toString()
                            holder.taskName.isEnabled = false
                            viewModel.updateTask(item)
                        }
                        true
                    }

                    "Delete Task" -> {
                        // Handle delete task action
                        AlertDialog.Builder(context)
                            .setTitle("Confirm Task Deletion")
                            .setMessage("Are you sure you want to delete this Task?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                viewModel.deleteTask(item)
                                Toast.makeText(
                                    context,
                                    "Task ${item.name} deleted",
                                    Toast.LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                Toast.makeText(context, "Deletion cancelled", Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss()
                            }
                            .show()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
            true // return true to indicate that the event has been consumed
        }

        // Set an OnCLickListener on the image Button
        holder.playButton.setOnClickListener {
            navController.navigate(TaskFragmentDirections.actionProjectDetailFragmentToTimerFragment(projectId, item.id))
        }
    }

    // get size of list for viewholder
    override fun getItemCount(): Int {
        return dataset.size
    }
}