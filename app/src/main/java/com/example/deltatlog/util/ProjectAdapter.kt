package com.example.apicalls.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.deltatlog.MainActivity
import com.example.deltatlog.R
import com.example.deltatlog.SharedViewModel
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.ui.HomeFragment
import com.example.deltatlog.ui.HomeFragmentDirections
import com.example.deltatlog.ui.TaskFragmentArgs
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.withContext
import java.security.AccessController.getContext


class ProjectAdapter(
    private var sharedViewModel: SharedViewModel,
    private var context: Context,
    private var dataset: List<Project>
) : RecyclerView.Adapter<ProjectAdapter.ItemViewHolder>() {

//    @SuppressLint("NotifyDataSetChanged")
//    fun submitList(list: List<Project>) {
//        dataset = list
//        notifyDataSetChanged()
//    }

    // parts of the item which need to be change by adapter
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.list_image)
        val textView = view.findViewById<TextView>(R.id.list_text)
        val projectCardview = view.findViewById<MaterialCardView>(R.id.project_card_view)
    }

    // create new viewholders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        // inflate item layout
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_project, parent, false)

        // return viewholder
        return ItemViewHolder(adapterLayout)
    }

    // recyclingprocess
    // set parameters
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.textView.text = item.name
//        holder.imageView.background = item.severityColor

        holder.projectCardview.setOnClickListener {
            val navController = holder.projectCardview.findNavController()
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToProjectDetailFragment(
                    item.id
                )
            )
        }

        holder.projectCardview.setOnLongClickListener {

            val menuItems = arrayOf("Edit Project", "Delete Project")

            val popupMenu = PopupMenu(context, holder.projectCardview)
            menuItems.forEach { popupMenu.menu.add(it) }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Edit Project" -> {
                        // Handle edit project action
                        true
                    }
                    "Delete Project" -> {
                        // Handle delete project action
                        AlertDialog.Builder(context)
                            .setTitle("Confirm Project Deletion")
                            .setMessage("Are you sure you want to delete this project?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                // Handle delete project action
                                sharedViewModel.deleteProject(item)
                                Toast.makeText(context, "Project ${item.name} deleted", Toast.LENGTH_LONG).show()
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