import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.apicalls.adapter.TaskAttrAdapter
import com.example.deltatlog.R
import com.example.deltatlog.SharedViewModel
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.databinding.FragmentHomeBinding
import com.example.deltatlog.databinding.FragmentTaskBinding
import com.example.deltatlog.databinding.ListItemTaskBinding
import com.example.deltatlog.util.Timer
import com.google.android.material.card.MaterialCardView
import java.security.AccessController.getContext

class TaskAdapter(
    private var sharedViewModel: SharedViewModel,
    private var context: Context,
    private var dataset: List<Task>

) : RecyclerView.Adapter<TaskAdapter.ItemViewHolder>() {

//    @SuppressLint("NotifyDataSetChanged")
//    fun submitList(list: List<Project>) {
//        dataset = list
//        notifyDataSetChanged()
//    }

    // parts of the item which need to be change by adapter
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val taskCardView = view.findViewById<MaterialCardView>(R.id.task_card_view)
        val textViewName = view.findViewById<TextView>(R.id.list_text)
        val textDate = view.findViewById<EditText>(R.id.edit_text_date)
        val rvTaskAttr = view.findViewById<RecyclerView>(R.id.list_scroll_view)
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

        holder.textViewName.text = item.name
        holder.textViewName.isEnabled = false
        holder.textDate.setText(item.date)

        holder.taskCardView.setCardBackgroundColor(Color.parseColor(item.color))

        // create list of actual task attributes
        val attrList = listOf<String>(
            item.duration.toString(),
            item.description,
            item.notes,
            item.taskProjectId.toString(),
        )

        val taskAttrLayoutManager = LinearLayoutManager(
            holder.rvTaskAttr.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
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

        // Setup of second recyclerview in the item of the current recyclerview
        val timers = mutableMapOf<Int, Timer>()
        for ( i in 0 until dataset.size) {
            timers.put(i, Timer())
        }
        holder.rvTaskAttr.layoutManager = taskAttrLayoutManager
        val taskAttrAdapter = TaskAttrAdapter(
            dataset,
            item.id,
            sharedViewModel,
            context,
            attrList,
            timers,
            holder.rvTaskAttr
        )

        holder.rvTaskAttr.adapter = taskAttrAdapter

        holder.rvTaskAttr.setHasFixedSize(true)

        val helperTaskAttr: SnapHelper = PagerSnapHelper()
        holder.rvTaskAttr.setOnFlingListener(null)
        helperTaskAttr.attachToRecyclerView(holder.rvTaskAttr)

        holder.taskCardView.setOnLongClickListener {

            val menuItems = arrayOf("Rename Task", "Delete Task")

            val popupMenu = PopupMenu(context, holder.taskCardView)
            menuItems.forEach { popupMenu.menu.add(it) }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Rename Task" -> {
                        // Handle Rename task action
                        // TODO Rename tasks actions should be implemented here
                        holder.textViewName.isEnabled = true

                        holder.taskCardView.setOnClickListener{
                            item.name = holder.textViewName.text.toString()
                            holder.textViewName.isEnabled = false
                            sharedViewModel.updateTask(item)
                        }
                        true
                    }
                    "Delete Task" -> {
                        // Handle delete task action
                        AlertDialog.Builder(context)
                            .setTitle("Confirm Task Deletion")
                            .setMessage("Are you sure you want to delete this Task?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                sharedViewModel.deleteTask(item)
                                Toast.makeText(context, "Task ${item.name} deleted", Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                Toast.makeText(context, "Deletion cancelled", Toast.LENGTH_SHORT).show()
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

    }

    // get size of list for viewholder
    override fun getItemCount(): Int {
        return dataset.size
    }
}