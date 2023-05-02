package com.example.apicalls.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.ui.HomeFragmentDirections
import com.google.android.material.card.MaterialCardView

class ProjectAdapter(
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
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToProjectDetailFragment(item.id))
        }
    }

    // get size of list for viewholder
    override fun getItemCount(): Int {
        return dataset.size
    }
}