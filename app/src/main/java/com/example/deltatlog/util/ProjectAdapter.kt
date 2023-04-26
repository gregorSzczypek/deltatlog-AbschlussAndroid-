package com.example.apicalls.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.apicalls.data.datamodels.Project
import com.example.deltatlog.R
import com.example.deltatlog.ui.HomeFragmentDirections
import com.example.deltatlog.ui.ProjectDetailFragment
import com.example.deltatlog.ui.ProjectDetailFragmentDirections
import com.google.android.material.card.MaterialCardView

class ProjectAdapter(
    private var dataset: List<Project>
) : RecyclerView.Adapter<ProjectAdapter.ItemViewHolder>() {

//    @SuppressLint("NotifyDataSetChanged")
//    fun submitList(list: List<Project>) {
//        dataset = list
//        notifyDataSetChanged()
//    }

    // der ViewHolder weiß welche Teile des Layouts beim Recycling angepasst werden
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.list_image)
        val textView = view.findViewById<TextView>(R.id.list_text)
        val projectCardview = view.findViewById<MaterialCardView>(R.id.project_card_view)
    }

    // hier werden neue ViewHolder erstellt
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        // das itemLayout wird gebaut
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_project, parent, false)

        // und in einem ViewHolder zurückgegeben
        return ItemViewHolder(adapterLayout)
    }

    // hier findet der Recyclingprozess statt
    // die vom ViewHolder bereitgestellten Parameter werden verändert
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.textView.text = item.name
//        holder.imageView.background = item.severityColor

        holder.projectCardview.setOnClickListener {
            val navController = holder.projectCardview.findNavController()
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToProjectDetailFragment(item.id))
        }
    }

    // damit der LayoutManager weiß wie lang die Liste ist
    override fun getItemCount(): Int {
        return dataset.size
    }
}