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
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.deltatlog.util.FirebaseManager
import com.example.deltatlog.R
import com.example.deltatlog.viewModel
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.ui.ProjectFragmentDirections
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectAdapter(
    private var viewModel: viewModel,
    private var context: Context,
    private var dataset: List<Project>,
    private val coroutineScope: CoroutineScope

) : RecyclerView.Adapter<ProjectAdapter.ItemViewHolder>() {

    // parts of the item which need to be changed by adapter
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.start_button)!!
        val textView = view.findViewById<TextView>(R.id.Project_name)!!
        val projectCardview = view.findViewById<MaterialCardView>(R.id.project_card_view)!!
        val dateView = view.findViewById<TextView>(R.id.date)!!
        val customerView = view.findViewById<TextView>(R.id.duration)!!
        val descriptionView = view.findViewById<TextView>(R.id.task_description)!!
        val numberOfTasks = view.findViewById<TextView>(R.id.tv_total_task_amount)!!
        val totalTime = view.findViewById<TextView>(R.id.tv_total_task_time)!!
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
        holder.imageView.load(item.logoUrl) {
            error(R.drawable.applogo)
            transformations(RoundedCornersTransformation(30f))
        }
        holder.projectCardview.setCardBackgroundColor(Color.parseColor(item.color))
        holder.numberOfTasks.text = item.numberOfTasks.toString()
        holder.totalTime.text = item.totalTime


        // Set an OnTouchListener on the card view
        holder.projectCardview.setOnClickListener {
            val navController = holder.projectCardview.findNavController()
            navController.navigate(
                ProjectFragmentDirections.actionHomeFragmentToProjectDetailFragment(
                    item.id, item.color
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
                        PropertyValuesHolder.ofFloat("scaleX", 0.975f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.975f),
                    )
                    animator.duration = 100
                    animator.start()
                }
                // Reverse the animation on the view when released or canceled
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
        // open menu at longClick
        holder.projectCardview.setOnLongClickListener {
            val menuItems = arrayOf("Edit Project", "Change Color", "Delete Project")
            val popupMenu = PopupMenu(context, holder.projectCardview)

            menuItems.forEach { popupMenu.menu.add(it) }

            // Handle edit project action
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Edit Project" -> {
                        val builder = AlertDialog.Builder(context)
                        val inflater = LayoutInflater.from(context)
                        val dialogLayout =
                            inflater.inflate(R.layout.edit_text_dialogue_project, null)
                        val newProjectName =
                            dialogLayout.findViewById<EditText>(R.id.input_project_name)
                        newProjectName.setText(item.name)
                        val newCustomerName =
                            dialogLayout.findViewById<EditText>(R.id.input_project_customer_name)
                        newCustomerName.setText(item.nameCustomer)
                        val newDescription =
                            dialogLayout.findViewById<EditText>(R.id.input_project_description)
                        newDescription.setText(item.description)
                        val newCompanyName =
                            dialogLayout.findViewById<EditText>(R.id.input_company_name)
                        newCompanyName.setText(item.companyName)
                        // Update changes into databases local and remote (firestore)
                        with(builder) {
                            setTitle("Update Project")
                            setPositiveButton("Ok") { dialog, which ->
                                val newProjectNameString = newProjectName.text.toString()
                                val newCustomerNameString = newCustomerName.text.toString()
                                val newDescriptionString = newDescription.text.toString()
                                val newCompanyNameString = newCompanyName.text.toString()

                                viewModel.loadLogo(newCompanyNameString) {
                                    if (newCompanyNameString != "") {
                                        Log.d("ProjectFragment", "(5) Here updating logourl")
                                        Log.d(
                                            "ProjectFragment",
                                            viewModel.logoLiveData.value!!.logo
                                        )

                                        item.logoUrl = viewModel.logoLiveData.value!!.logo
                                    }

                                    if (newProjectNameString != "") {
                                        item.name = newProjectNameString
                                    }
                                    if (newCustomerNameString != "") {
                                        item.nameCustomer = newCustomerNameString
                                    }
                                    if (newDescriptionString != "") {
                                        item.description = newDescriptionString
                                    }
                                    if (newCompanyNameString != "") {
                                        item.companyName = newCompanyNameString
                                    }
                                    Log.d("ProjectFragment", item.logoUrl)

                                    viewModel.updateProject(item)

                                    val updates = mutableMapOf<String, Any>(
                                        "name" to item.name,
                                        "nameCustomer" to item.nameCustomer,
                                        "description" to item.description,
                                        "companyName" to item.companyName,
                                        "logoUrl" to item.logoUrl
                                    )
                                    val firebaseManager = FirebaseManager()
                                    firebaseManager.updateProjectChanges(
                                        item.id.toString(),
                                        updates
                                    )

                                    Toast.makeText(
                                        context,
                                        "$newProjectNameString updated",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                            setNegativeButton("Cancel") { dialog, which ->
                                dialog.dismiss()
                            }
                            setView(dialogLayout)
                        }.show()
                        true
                    }

                    // Handle delete Project action
                    "Delete Project" -> {
                        // Handle delete project action
                        AlertDialog.Builder(context)
                            .setTitle("Confirm Project Deletion")
                            .setMessage("Are you sure you want to delete this project and all of the related tasks?")
                            .setPositiveButton("Yes") { dialog, _ ->

                                val taskDataBase = getTaskDatabase(context)

                                coroutineScope.launch {
                                    viewModel.deleteAllTasks(item.id)
                                    val allTasks = withContext(Dispatchers.IO) {
                                        taskDataBase.taskDatabaseDao.getAllNLD()
                                    }
                                    val tasks2Delete =
                                        allTasks.filter { it.taskProjectId == item.id }
                                    val firebaseManager = FirebaseManager()

                                    for (i in tasks2Delete) {
                                        firebaseManager.deleteTask(i)
                                    }
                                }
                                // delete project from local datbase
                                viewModel.deleteProject(item)

                                val firebaseManager = FirebaseManager()
                                firebaseManager.deleteProject(item)
                                notifyItemChanged(position)

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

                    // Handle change of project color
                    "Change Color" -> {
                        // Show dialog when a button is clicked
                        val dialogView =
                            LayoutInflater.from(context).inflate(R.layout.dialog_color_list, null)
                        val dialog = AlertDialog.Builder(context)
                            .setView(dialogView)
                            .create()

                        val colorListView = dialogView.findViewById<ListView>(R.id.color_list)
                        val colorAdapter = ColorAdapter(viewModel.colors, context) { color ->
                            item.color = color
                            viewModel.updateProject(item)


                            // Update changes into firebase storage
                            val updates = mapOf<String, Any>("color" to item.color)
                            val firebaseManager = FirebaseManager()

                            firebaseManager.updateProjectChanges(item.id.toString(), updates)

                            this.notifyItemChanged(position)
                            dialog.dismiss()
                        }
                        colorListView.adapter = colorAdapter

                        // Set OnItemClickListener for the icon list
                        colorListView.setOnItemClickListener { _, _, position, _ ->
                            dialog.dismiss() // Dismiss the dialog when an icon is clicked
                        }
                        dialog.show()
                        false
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