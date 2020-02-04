package net.north101.android.ghplayertracker

import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import net.north101.android.ghplayertracker.livedata.ModifierCard

class TrackerCardViewHolder(itemView: View) : BaseViewHolder<ModifierCard>(itemView){
    var cardView: ImageView = itemView.findViewById(R.id.card)
    var layout: FrameLayout = itemView.findViewById(R.id.card_layout)

    override fun bind(item: ModifierCard) {
        super.bind(item)

        cardView.setOnClickListener(this)
        cardView.setOnLongClickListener(this)

        item.revealed.observeForever(revealedObserver)
        item.selected.observeForever(selectedObserver)
    }

    override fun unbind() {
        item?.let {
            item!!.revealed.removeObserver(revealedObserver)
            item!!.selected.removeObserver(selectedObserver)
        }

        super.unbind()
    }

    private val revealedObserver: (Boolean) -> Unit = {
        when(it){
            true -> {
                cardView.setImageResource(Util.getImageResource(itemView.context, item!!.card.id))
            }
            else -> {
                cardView.setImageResource(R.drawable.card_back)
            }
        }
    }

    private val selectedObserver: (Boolean) -> Unit = {
        when(it){
            true -> {
                layout.foreground =  ColorDrawable(ContextCompat.getColor(itemView.context, R.color.colorControlActivated))
//                layout.background =  ColorDrawable(ContextCompat.getColor(itemView.context, R.color.colorControlActivated))
            }
            else -> {
                layout.foreground = ColorDrawable(ContextCompat.getColor(itemView.context, android.R.color.transparent))
//                layout.background =  ColorDrawable(ContextCompat.getColor(itemView.context, android.R.color.transparent))
            }
        }
    }

    companion object {
        var layout = R.layout.card_item_view

        fun inflate(parent: ViewGroup): TrackerCardViewHolder {
            return TrackerCardViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
