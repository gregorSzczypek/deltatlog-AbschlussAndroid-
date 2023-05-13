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

// IconAdapter.kt
class ColorAdapter(private val colors: List<Int>, private val context: Context, private val onIconClickListener: ((String) -> Unit)? = null
) : BaseAdapter() {

    override fun getCount(): Int {
        return colors.size
    }

    override fun getItem(position: Int): Any {
        return colors[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_color, parent, false)
        }

        val iconNameTextView = view?.findViewById<TextView>(R.id.icon_name_text_view)
        val colorString = "#" + Integer.toHexString(ContextCompat.getColor(context, colors[position])).substring(2).toUpperCase()

        iconNameTextView?.text = colorString
        iconNameTextView?.background = ColorDrawable(Color.parseColor(colorString))

        iconNameTextView?.setOnClickListener {
            onIconClickListener?.invoke(colorString)
        }
        return view!!
    }
}
