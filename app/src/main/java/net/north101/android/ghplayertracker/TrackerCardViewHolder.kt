package net.north101.android.ghplayertracker

import android.animation.*
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import net.north101.android.ghplayertracker.livedata.ModifierCard

class TrackerCardViewHolder(itemView: View) : BaseViewHolder<ModifierCard>(itemView), Animator.AnimatorListener{
    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
        cardView.setImageResource(Util.getImageResource(itemView.context, item!!.card.id))
        startAnimationIn()
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {

    }

    var cardView: ImageView = itemView.findViewById(R.id.card)
    var layout: FrameLayout = itemView.findViewById(R.id.card_layout)
    var classColor : Int = R.color.colorControlActivated

    fun bind(item: ModifierCard, classColor: Int) {
        super.bind(item)
        this.classColor = classColor

        cardView.setOnClickListener(this)
        cardView.setOnLongClickListener(this)

        when(item.revealed.value){
            true -> cardView.setImageResource(Util.getImageResource(itemView.context, item!!.card.id))
            else -> cardView.setImageResource(R.drawable.card_back)
        }

        item.selected.observeForever(selectedObserver)
    }

    override fun unbind() {
        item?.let {
            item!!.selected.removeObserver(selectedObserver)
        }

        super.unbind()
    }


    fun startAnimationOut() {

        val left_out: Animator = AnimatorInflater.loadAnimator(itemView.context, R.animator.card_flip_left_out)

        left_out.setTarget(cardView)
        left_out.addListener(this)
        left_out.start()
    }


    private fun startAnimationIn() {

        val left_in: Animator = AnimatorInflater.loadAnimator(itemView.context, R.animator.card_flip_left_in)

        left_in.setTarget(cardView)
        left_in.start()
    }

    private val selectedObserver: (Boolean) -> Unit = {
        when(it){
            true -> {
//                layout.foreground =  ColorDrawable(ColorUtils.setAlphaComponent(classColor, 50))
                layout.background =  ColorDrawable(classColor)
            }
            else -> {
//                layout.foreground = ColorDrawable(ContextCompat.getColor(itemView.context, android.R.color.transparent))
                layout.background = ColorDrawable(ContextCompat.getColor(itemView.context, android.R.color.transparent))
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
