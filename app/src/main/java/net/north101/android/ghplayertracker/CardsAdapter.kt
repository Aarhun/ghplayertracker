package net.north101.android.ghplayertracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import net.north101.android.ghplayertracker.data.Card

class CardsAdapter(context: Context, private val discarded: ArrayList<Card>)
    : ArrayAdapter<Card>(context, R.layout.card_item_view, discarded) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.card_item_view, null, true)

        val imageView = rowView.findViewById(R.id.card) as ImageView

        imageView.setImageResource(Util.getImageResource(context, discarded[position].id))
        return rowView

    }
}