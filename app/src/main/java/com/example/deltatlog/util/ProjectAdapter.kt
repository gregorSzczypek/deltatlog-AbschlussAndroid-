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

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Project>) {
        dataset = list
        notifyDataSetChanged()
    }

    // Define the request code as a constant

    // parts of the item which need to be change by adapter
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val textView = view.findViewById<TextView>(R.id.Project_name)
        val projectCardview = view.findViewById<MaterialCardView>(R.id.project_card_view)
        val dateView = view.findViewById<TextView>(R.id.date)
        val customerView = view.findViewById<TextView>(R.id.duration)
        val descriptionView = view.findViewById<TextView>(R.id.task_description)

        val icons = listOf(
            R.drawable.ellipse_dunkel,
            R.drawable.ellipse_blau,
            R.drawable.ellipse_gelb,
            R.drawable.ellipse_gruen,
            R.drawable.ellipse_rot,
            R.drawable.ellipse_orange,
            R.drawable.ellipse_pink,
            R.drawable.ellipse_t_rkis,
            R.drawable.ellipse_schwarz
        )
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
        holder.imageView.setImageResource(item.image)


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

            // Show dialog when a button is clicked
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_item_list, null)

            val iconListView = dialogView.findViewById<ListView>(R.id.icon_list)
            val iconAdapter = IconAdapter(holder.icons, context) { icon ->
                item.image = icon
                sharedViewModel.updateProject(item)
//                    Toast.makeText(context, "Icon clicked: ${context.resources.getResourceEntryName(icon)}", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(context, "Item image: ${item.image.toString()}", Toast.LENGTH_SHORT).show()
            }
            iconListView.adapter = iconAdapter

            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()
            dialog.show()

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
                        val builder = AlertDialog.Builder(context)
                        val inflater = LayoutInflater.from(context)
                        val dialogLayout =
                            inflater.inflate(R.layout.edit_text_dialogue_project, null)
                        val newProjectName =
                            dialogLayout.findViewById<EditText>(R.id.input_project_name)
                        val newCustomerName =
                            dialogLayout.findViewById<EditText>(R.id.input_project_customer_name)
                        val newDescription =
                            dialogLayout.findViewById<EditText>(R.id.input_project_description)

                        with(builder) {
                            setTitle("Update Project")
                            setPositiveButton("Ok") { dialog, which ->
                                val newProjectNameString = newProjectName.text.toString()
                                val newCustomerNameString = newCustomerName.text.toString()
                                val newDescriptionString = newDescription.text.toString()

                                if (newProjectNameString != "") {
                                    item.name = newProjectNameString
                                }
                                if (newCustomerNameString != "") {
                                    item.nameCustomer = newCustomerNameString
                                }
                                if (newDescriptionString != "") {
                                    item.description = newDescriptionString
                                }

                                sharedViewModel.updateProject(item)
                                submitList(sharedViewModel.projectList.value!!)
                                Toast.makeText(
                                    context,
                                    "$newProjectNameString updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            setView(dialogLayout)
                        }.show()

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