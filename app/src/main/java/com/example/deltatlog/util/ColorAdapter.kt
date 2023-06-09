import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.deltatlog.R

class ColorAdapter(

    // List of color resources
    private val colors: List<Int>,
    // Context reference
    private val context: Context,
    // click listener for items in the list
    private val onIconClickListener: ((String) -> Unit)? = null
) : BaseAdapter() {

    // Return the number of colors in the list
    override fun getCount(): Int {
        return colors.size
    }

    // Get the color at the specified position
    override fun getItem(position: Int): Any {
        return colors[position]
    }

    // Return the item ID for the specified position
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {

            // If the view is null, inflate it from the specified layout
            view = LayoutInflater.from(context).inflate(R.layout.list_item_color, parent, false)
        }

        val iconNameTextView = view?.findViewById<TextView>(R.id.icon_name_text_view)

        // Convert the color resource to a hex string
        val colorString = "#" + Integer.toHexString(ContextCompat.getColor(context, colors[position])).substring(2).toUpperCase()

        iconNameTextView?.background = ColorDrawable(Color.parseColor(colorString))

        iconNameTextView?.setOnClickListener {
            // Invoke the click listener if available, passing the color string as an argument
            onIconClickListener?.invoke(colorString)
        }
        // Return the populated view
        return view!!
    }
}
