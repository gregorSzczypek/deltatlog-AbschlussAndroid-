import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.deltatlog.R
import com.example.deltatlog.viewModel
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.ui.TaskFragmentDirections
import com.google.android.material.card.MaterialCardView


class TaskAdapter(
    private var viewModel: viewModel,
    private val lifeCycleOwner: LifecycleOwner,
    private var context: Context,
    private var dataset: List<Task>,
    private var navController: NavController,
    private var projectId: Long,
    private var color: String?

) : RecyclerView.Adapter<TaskAdapter.ItemViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Task>) {
        dataset = list
        notifyDataSetChanged()
    }

    // parts of the item which need to be change by adapter
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val taskCardView = view.findViewById<MaterialCardView>(R.id.task_card_view)
        val taskName = view.findViewById<TextView>(R.id.task_name)
        val taskDuration = view.findViewById<TextView>(R.id.duration)
        val taskDate = view.findViewById<TextView>(R.id.date)
        val taskDescription = view.findViewById<TextView>(R.id.task_description)
        val playButton = view.findViewById<ImageButton>(R.id.start_button)
    }

    // create new viewholders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ItemViewHolder {

        // inflate item layout
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_task, parent, false)
        // return viewholder
        return ItemViewHolder(adapterLayout)
    }

    // recyclingprocess
    // set parameters
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        for (i in dataset) {
            if (i.color != color && color != null) {
                i.color = color!!
                viewModel.updateTask(i)
            }
        }

        holder.taskName.text = item.name
        holder.taskDate.text = item.date
        holder.taskCardView.setCardBackgroundColor(Color.parseColor(item.color))
        holder.taskDuration.text = item.duration
        holder.taskDescription.text = item.notes

        // Set an OnTouchListener on the item view for animation
        holder.taskCardView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start the jump animation on the view
                    val animator = ObjectAnimator.ofPropertyValuesHolder(
                        v,
                        PropertyValuesHolder.ofFloat("scaleX", 0.975f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.975f),
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
            val menuItems = arrayOf("Edit Task", "Delete Task")
            val popupMenu = PopupMenu(context, holder.taskCardView)
            menuItems.forEach { popupMenu.menu.add(it) }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title) {
                        "Edit Task" -> {
                            val builder = AlertDialog.Builder(context)
                            val inflater = LayoutInflater.from(context)
                            val dialogLayout =
                                inflater.inflate(R.layout.edit_text_dialogue_task, null)
                            val newTaskName =
                                dialogLayout.findViewById<EditText>(R.id.input_task_name)
                            newTaskName.setText(item.name)
                            val newNote =
                                dialogLayout.findViewById<EditText>(R.id.input_task_description)
                            newNote.setText(item.notes)

                            with(builder) {
                                setTitle("Update Task")
                                setPositiveButton("Ok") { dialog, which ->
                                    val newProjectNameString = newTaskName.text.toString()
                                    val newDescriptionString = newNote.text.toString()

                                        item.name = newProjectNameString
                                        item.notes = newDescriptionString

                                    viewModel.updateTask(item)
                                    submitList(viewModel.taskList.value!!)
                                    Toast.makeText(
                                        context,
                                        "$newProjectNameString updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                setNegativeButton("Cancel") { dialog, which ->
                                    dialog.dismiss()
                                }
                                setView(dialogLayout)
                            }.show()
                            true
                        }

                    "Delete Task" -> {
                        // Handle delete task action
                        AlertDialog.Builder(context)
                            .setTitle("Confirm Task Deletion")
                            .setMessage("Are you sure you want to delete this Task?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                viewModel.deleteTask(item)

                                // Update number of tasks in the project in question
                                viewModel.taskList.observe(
                                    lifeCycleOwner
                                ) { it ->
                                    val filteredTaskList = it.filter { it.taskProjectId == projectId }
                                    val size = filteredTaskList.size.toLong()
                                    Log.d("TaskAdapter", "size: ${size.toString()}")
                                    Log.d("TaskAdapter", "projectid: ${projectId.toString()}")
                                    viewModel.taskObserverTriggered = 1
                                    viewModel.projectList.observe(
                                        lifeCycleOwner
                                    ) {
                                        if (viewModel.taskObserverTriggered == 1) {
                                            val project = it.find { it.id == projectId }
                                            Log.d("TaskAdapter", "inif nrtasks it: ${it.find { it.id == projectId }!!.numberOfTasks.toString()}")
                                            Log.d("TaskAdapter", "inif size: ${size}")
                                            project!!.numberOfTasks = size
                                            viewModel.updateProject(project)
                                            viewModel.taskObserverTriggered = 0
                                            Log.d("TaskAdapter", "in if")
                                        }
                                        Log.d("TaskAdapter", "projectid: triggered")
                                    }
                                }

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
            navController.navigate(TaskFragmentDirections.actionProjectDetailFragmentToTimerFragment(projectId, item.id, item.name, color))
        }
    }

    // get size of list for viewholder
    override fun getItemCount(): Int {
        return dataset.size
    }
}