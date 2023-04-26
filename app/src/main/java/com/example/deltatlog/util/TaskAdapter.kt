package com.example.apicalls.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apicalls.data.datamodels.Project
import com.example.apicalls.data.datamodels.Task
import com.example.deltatlog.R
import com.example.deltatlog.ui.ProjectDetailFragment

class TaskAdapter(

    private var dataset: List<Task>
) : RecyclerView.Adapter<TaskAdapter.ItemViewHolder>() {

//    @SuppressLint("NotifyDataSetChanged")
//    fun submitList(list: List<Project>) {
//        dataset = list
//        notifyDataSetChanged()
//    }

    // der ViewHolder weiß welche Teile des Layouts beim Recycling angepasst werden
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.list_image)
        val textView = view.findViewById<TextView>(R.id.list_text)
    }

    // hier werden neue ViewHolder erstellt
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ItemViewHolder {

        // das itemLayout wird gebaut
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_task, parent, false)

        // und in einem ViewHolder zurückgegeben
        return ItemViewHolder(adapterLayout)
    }

    // hier findet der Recyclingprozess statt
    // die vom ViewHolder bereitgestellten Parameter werden verändert

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = dataset[position]
        holder.textView.text = item.name
//        holder.imageView.background = item.severityColor
    }

    // damit der LayoutManager weiß wie lang die Liste ist
    override fun getItemCount(): Int {
        return dataset.size
    }
}