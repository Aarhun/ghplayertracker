package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import net.north101.android.ghplayertracker.data.Card

class TrackerDiscardedCardViewHolder(itemView: View) : BaseViewHolder<Card>(itemView){
    var cardView: ImageView = itemView.findViewById(R.id.card)
    var layout: FrameLayout = itemView.findViewById(R.id.card_layout)

    override fun bind(item: Card) {
        super.bind(item)
        cardView.setImageResource(Util.getImageResource(itemView.context, item.id))
    }

    companion object {
        var layout = R.layout.card_item_view

        fun inflate(parent: ViewGroup): TrackerDiscardedCardViewHolder {
            return TrackerDiscardedCardViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
