package lesniewski.pawel.uwd_android_studio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import lesniewski.pawel.uwd_android_studio.models.CardModel

class CardModelAdapter(var models: ArrayList<CardModel>, var context: Context): PagerAdapter()
{

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view.equals(obj)
    }

    override fun getCount(): Int {
        return models.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item, container, false)

        val textOnCard = view.findViewById<TextView>(R.id.cardText)

        textOnCard.text = models[position].answer

        container.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}