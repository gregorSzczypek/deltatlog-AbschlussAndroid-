import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.deltatlog.R

// IconAdapter.kt
class IconAdapter(private val icons: List<Int>, private val context: Context, private val onIconClickListener: ((Int) -> Unit)? = null
) : BaseAdapter() {

    override fun getCount(): Int {
        return icons.size
    }

    override fun getItem(position: Int): Any {
        return icons[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_icon, parent, false)
        }

        val iconImageView = view?.findViewById<ImageView>(R.id.icon_image_view)
        val iconNameTextView = view?.findViewById<TextView>(R.id.icon_name_text_view)

        iconImageView?.setImageResource(icons[position])
        iconNameTextView?.text = context.resources.getResourceEntryName(icons[position])

        iconImageView?.setOnClickListener {
            onIconClickListener?.invoke(icons[position])

        }

        return view!!
    }
}
