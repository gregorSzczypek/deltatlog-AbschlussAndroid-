import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.deltatlog.R
import com.example.deltatlog.SharedViewModel
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.ui.HomeFragmentDirections
import com.google.android.material.card.MaterialCardView


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

    // Define the request code as a constant
    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 123456789
        const val REQUEST_CODE = 123
    }

    // parts of the item which need to be change by adapter
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val textView = view.findViewById<TextView>(R.id.Project_name)
        val projectCardview = view.findViewById<MaterialCardView>(R.id.project_card_view)
        val dateView = view.findViewById<TextView>(R.id.date)
        val customerView = view.findViewById<TextView>(R.id.customer)
        val descriptionView = view.findViewById<TextView>(R.id.description)

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
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.textView.text = item.name
        holder.customerView.text = item.nameCustomer
        holder.dateView.text = item.date
        holder.descriptionView.text = item.description


        holder.projectCardview.setOnClickListener {

            val navController = holder.projectCardview.findNavController()
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToProjectDetailFragment(
                    item.id
                )
            )
        }

        // Set an OnTouchListener on the item view
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start the animation on the view
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

        holder.imageView.setOnLongClickListener {
            Toast.makeText(context, "Image clicked long", Toast.LENGTH_SHORT).show()
            false
        }

        holder.projectCardview.setOnLongClickListener {

            val menuItems = arrayOf("Edit Project", "Delete Project")

            val popupMenu = PopupMenu(context, holder.projectCardview)
            menuItems.forEach { popupMenu.menu.add(it) }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Edit Project" -> {
                        // Handle edit project action
                        // TODO edit actions should be implemented here
                        true
                    }

                    "Delete Project" -> {
                        // Handle delete project action
                        AlertDialog.Builder(context)
                            .setTitle("Confirm Project Deletion")
                            .setMessage("Are you sure you want to delete this project and all of the related tasks?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                sharedViewModel.deleteAllTasks(item.id)
                                sharedViewModel.deleteProject(item)
                                Toast.makeText(
                                    context,
                                    "Project ${item.name} deleted",
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
    }

    // get size of list for viewholder
    override fun getItemCount(): Int {
        return dataset.size
    }
}