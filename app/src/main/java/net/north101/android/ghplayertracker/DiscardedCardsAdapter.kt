package net.north101.android.ghplayertracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.north101.android.ghplayertracker.data.Card

class DiscardedCardsAdapter(val context: Context, val cardList: ArrayList<Card>) : RecyclerView.Adapter<TrackerDiscardedCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackerDiscardedCardViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.card_item_view, parent, false)
        return TrackerDiscardedCardViewHolder(itemView)
    }

    override fun getItemCount(): Int = cardList.count()

    override fun onBindViewHolder(holder: TrackerDiscardedCardViewHolder, index: Int) {
        holder.bind(cardList[index])
    }
}