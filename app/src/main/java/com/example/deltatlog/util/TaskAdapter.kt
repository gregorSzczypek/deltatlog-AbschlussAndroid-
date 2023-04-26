import Datasource
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
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
import com.example.deltatlog.databinding.FragmentHomeBinding
import com.example.deltatlog.databinding.FragmentTaskBinding
import com.example.deltatlog.databinding.ListItemTaskBinding
import com.google.android.material.card.MaterialCardView
import java.security.AccessController.getContext

class TaskAdapter(

    private val context: Context,
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
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = dataset[position]

        holder.textViewName.text = item.name
        holder.taskCardView.setCardBackgroundColor(Color.parseColor(item.color))

        val attrList = Datasource().loadTaskAttributes(item)
        val taskAttrLayoutManager = LinearLayoutManager(
            holder.rvTaskAttr.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        // Setup of second recyclerview in the item of the current recyclerview
        holder.rvTaskAttr.layoutManager = taskAttrLayoutManager
        val taskAttrAdapter = TaskAttrAdapter(attrList)
        holder.rvTaskAttr.adapter = taskAttrAdapter

        holder.rvTaskAttr.setHasFixedSize(true)

        val helperTaskAttr: SnapHelper = PagerSnapHelper()
        holder.rvTaskAttr.setOnFlingListener(null)
        helperTaskAttr.attachToRecyclerView(holder.rvTaskAttr)

    }

    // get size of list for viewholder
    override fun getItemCount(): Int {
        return dataset.size
    }
}