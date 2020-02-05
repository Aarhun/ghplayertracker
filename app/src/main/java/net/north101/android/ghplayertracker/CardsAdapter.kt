package net.north101.android.ghplayertracker

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import net.north101.android.ghplayertracker.livedata.ModifierCard


class CardsAdapter(val context: Context, val cardList: ArrayList<ModifierCard>, val classColor : Int) : RecyclerView.Adapter<TrackerCardViewHolder>(),
        DragHelper.ActionCompletionContract
{
    lateinit var touchHelper : ItemTouchHelper

    var onItemClickListener: BaseViewHolder.ClickListener<ModifierCard> = object : BaseViewHolder.ClickListener<ModifierCard>() {
        override fun onItemClick(holder: BaseViewHolder<ModifierCard>) {
            when {
                holder.item!!.revealed.value -> holder.item!!.selected.value = !holder.item!!.selected.value
                !holder.item!!.revealed.value ->  holder.item!!.revealed.value = true
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackerCardViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.card_item_view, parent, false)
        return TrackerCardViewHolder(itemView)
    }

    override fun getItemCount(): Int = cardList.count()

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TrackerCardViewHolder, index: Int) {
        holder.bind(cardList[index], classColor)
        holder.setOnItemClickListener(this.onItemClickListener)
        holder.cardView.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper!!.startDrag(holder)
            }
            false
        }
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        val targetCard = cardList.get(oldPosition)
        val card = ModifierCard(targetCard)
        cardList.removeAt(oldPosition)
        cardList.add(newPosition, card)
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        touchHelper = itemTouchHelper
    }
}