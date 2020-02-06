package net.north101.android.ghplayertracker

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import net.north101.android.ghplayertracker.livedata.ModifierCard
import kotlin.math.abs


class CardsAdapter(val context: Context, val cardList: ArrayList<ModifierCard>, val classColor : Int) : RecyclerView.Adapter<TrackerCardViewHolder>(),
        DragHelper.ActionCompletionContract
{
    lateinit var touchHelper : ItemTouchHelper

    var onItemClickListener: BaseViewHolder.ClickListener<ModifierCard> = object : BaseViewHolder.ClickListener<ModifierCard>() {
        override fun onItemClick(holder: BaseViewHolder<ModifierCard>) {
            when {
                !holder.item!!.revealed.value ->  {
                    (holder as TrackerCardViewHolder).startAnimationOut()
                    holder.item!!.revealed.value = true
                }
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
        var startX = 0f
        var startY = 0f
        holder.cardView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                startX = event.rawX
                startY = event.rawY
            } else if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                if(abs(startX-event.rawX) > 10){
//                    Log.d("START SWIPE", abs(startX-event.rawX).toString())
                    touchHelper!!.startSwipe(holder)
                } else if(abs(startY-event.rawY) > 2){
//                    Log.d("START DRAG", abs(startY-event.rawY).toString())
                    touchHelper!!.startDrag(holder)
                }

            }

//            Log.d("Motion Event", "$v $event")
            false
        }
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
//        Log.d("OnViewMoved", "$oldPosition $newPosition")
        val targetCard = cardList[oldPosition]
        val card = ModifierCard(targetCard)
        cardList.removeAt(oldPosition)
        cardList.add(newPosition, card)
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(position: Int) {
//        Log.d("onViewSwiped", "$position")
        val card = ModifierCard(cardList.removeAt(position))
        cardList.add(card)
        notifyItemRemoved(position)
    }

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        touchHelper = itemTouchHelper
    }
}