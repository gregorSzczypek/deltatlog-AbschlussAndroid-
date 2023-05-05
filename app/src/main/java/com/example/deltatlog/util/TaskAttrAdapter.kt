package com.example.apicalls.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.deltatlog.R
import com.example.deltatlog.SharedViewModel
import com.example.deltatlog.data.datamodels.Task
import com.google.android.material.card.MaterialCardView

class TaskAttrAdapter(

    private var taskList: List<Task>,
    private var taskId: Long,
    private var sharedViewModel: SharedViewModel,
    private var context: Context,
    private var dataset: List<String>
) : RecyclerView.Adapter<TaskAttrAdapter.ItemViewHolder>() {

    // parts of the item which need to be change by adapter
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tvAttr = view.findViewById<TextView>(R.id.list_text)
        val cardView = view.findViewById<CardView>(R.id.taskAttr_card_view)
        val prefixText = view.findViewById<TextView>(R.id.task_prefix)
    }

    // create new viewholders
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskAttrAdapter.ItemViewHolder {

        // inflate item layout
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_task_attr, parent, false)

        // return viewholder
        return ItemViewHolder(adapterLayout)
    }

    // recyclingprocess
    // set parameters

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = dataset[position]


        var prefix = ""


        when (position) {
            0 -> prefix = "Duration: "
            1 -> prefix = "Description: "
            2 -> prefix = "Notes: "
            3 -> prefix = "ID: "
        }

        holder.prefixText.text = prefix
        holder.tvAttr.text = item
        holder.tvAttr.isEnabled = false
//        holder.tvAttr.isFocusable = false
//        holder.tvAttr.isLongClickable = true

        // Set an OnTouchListener on the item view
        holder.cardView.setOnTouchListener { v, event ->
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



        holder.cardView.setOnLongClickListener{
                if (position == 1 || position == 2) {
                    val menuItems = arrayOf("Edit Data")

                    val popupMenu = PopupMenu(context, holder.cardView)
                    menuItems.forEach { popupMenu.menu.add(it) }

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.title) {
                            "Edit Data" -> {
                                // Handle edit task action
//                                holder.tvAttr.isFocusable = true
                                holder.cardView.isLongClickable = false
                                val currentTask = taskList.find { it.id == taskId }!!

                                holder.cardView.setOnClickListener {
                                    when (position) {
                                        // Change description
                                        1 -> {
                                            currentTask.description = holder.tvAttr.text.toString()
                                        }
                                        // Change Notes
                                        2 -> {
                                            currentTask.notes = holder.tvAttr.text.toString()
                                        }
                                    }

                                    sharedViewModel.updateTask(currentTask)
//                                    holder.tvAttr.isFocusable = false
                                    holder.tvAttr.isLongClickable = true
                                }
                                true
                            }

                            else -> false
                        }
                    }
                    popupMenu.show()
                }
            true // return true to indicate that the event has been consumed
        }
    }

    // get size of list for viewholder
    override fun getItemCount(): Int {
        return dataset.size
    }
}