package com.example.apicalls.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deltatlog.R
import com.google.android.material.card.MaterialCardView

class TaskAttrAdapter(

    private var dataset: List<String>
) : RecyclerView.Adapter<TaskAttrAdapter.ItemViewHolder>() {

    // parts of the item which need to be change by adapter
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tvAttr = view.findViewById<TextView>(R.id.list_text)
    }

    // create new viewholders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAttrAdapter.ItemViewHolder {

        // inflate item layout
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_task_attr, parent, false)

        // return viewholder
        return ItemViewHolder(adapterLayout)
    }

    // recyclingprocess
    // set parameters

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = dataset[position]
        holder.tvAttr.text = item
    }

    // get size of list for viewholder
    override fun getItemCount(): Int {
        return dataset.size
    }
}