package net.north101.android.ghplayertracker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.cards_list_layout.view.*
import net.north101.android.ghplayertracker.data.Card
import net.north101.android.ghplayertracker.data.CardSpecial
import net.north101.android.ghplayertracker.data.Status
import net.north101.android.ghplayertracker.livedata.ModifierCard
import net.north101.android.ghplayertracker.livedata.TrackerLiveData
import java.util.*
import kotlin.collections.ArrayList


class TrackerDeckViewHolder(itemView: View) : BaseViewHolder<TrackerLiveData>(itemView) {
    var blessContainerView: View = itemView.findViewById(R.id.bless_container)
    var blessTextView: TextView = itemView.findViewById(R.id.bless_text)
    var blessPlusView: View = itemView.findViewById(R.id.bless_plus)
    var blessMinusView: View = itemView.findViewById(R.id.bless_minus)

    var curseContainerView: View = itemView.findViewById(R.id.curse_container)
    var curseTextView: TextView = itemView.findViewById(R.id.curse_text)
    var cursePlusView: View = itemView.findViewById(R.id.curse_plus)
    var curseMinusView: View = itemView.findViewById(R.id.curse_minus)

    var minus1ContainerView: View = itemView.findViewById(R.id.minus_1_container)
    var minus1TextView: TextView = itemView.findViewById(R.id.minus_1_text)
    var minus1PlusView: View = itemView.findViewById(R.id.minus_1_plus)
    var minus1MinusView: View = itemView.findViewById(R.id.minus_1_minus)

    val deckView: ImageView = itemView.findViewById(R.id.draw_deck)
    val discardView : ImageView = itemView.findViewById(R.id.discard_deck)
    val discardView2 : ImageView = itemView.findViewById(R.id.discard_deck_2)
    val discardView3 : ImageView = itemView.findViewById(R.id.discard_deck_3)
    val statusIconView : ImageView = itemView.findViewById(R.id.status_icon)

    var toast : Toast = Toast(itemView.context)

//    val advantageView: ImageView = itemView.findViewById(R.id.advantage)
//    val disadvantageView: ImageView = itemView.findViewById(R.id.disadvantage)
    val shuffleView: ImageView = itemView.findViewById(R.id.shuffle)

    var onNumberClickListener: ((String) -> Unit)? = null

    val blessCard: Card
        get() = Card["mod_extra_double_bless_remove"]
    val curseCard: Card
        get() = Card["mod_extra_null_curse_remove"]
    val minus1Card: Card
        get() = Card["mod_extra_minus_1"]

    var shuffle: Boolean
        get() = item!!.shuffle.value
        set(value) {
            item!!.shuffle.value = value
        }

    var shuffleCount: Int
        get() = item!!.shuffleCount.value
        set(value) {
            item!!.shuffleCount.value = value
        }

    var drawDeck: ArrayList<Card>
        get() = item!!.drawDeck.value
        set(value) {
            item!!.drawDeck.value = value
        }

    var discardDeck: ArrayList<Card>
        get() = item!!.discardDeck.value
        set(value) {
            item!!.discardDeck.value = value
        }

    init {
        blessContainerView.setOnClickListener {
            onNumberClickListener?.invoke("bless")
        }
        blessPlusView.setOnTouchListener(RepeatListener({ _, _ ->
            drawDeck.add(blessCard)
            updateBlessText()
        }))
        blessMinusView.setOnTouchListener(RepeatListener({ _, _ ->
            drawDeck.remove(blessCard)
            updateBlessText(false)
        }))

        curseContainerView.setOnClickListener {
            onNumberClickListener?.invoke("curse")
        }
        cursePlusView.setOnTouchListener(RepeatListener({ _, _ ->
            drawDeck.add(curseCard)
            updateCurseText()
        }))
        curseMinusView.setOnTouchListener(RepeatListener({ _, _ ->
            drawDeck.remove(curseCard)
            updateCurseText(false)
        }))

        minus1ContainerView.setOnClickListener {
            onNumberClickListener?.invoke("minus_1")
        }
        minus1PlusView.setOnTouchListener(RepeatListener({ _, _ ->
            drawDeck.add(minus1Card)
            updateMinus1Text()
        }))
        minus1MinusView.setOnTouchListener(RepeatListener({ _, _ ->
            if (drawDeck.contains(minus1Card)) {
                drawDeck.remove(minus1Card)
            } else if (discardDeck.contains(minus1Card)) {
                discardDeck.remove(minus1Card)
            }
            updateMinus1Text(false)
        }))

        deckView.setOnClickListener {
            draw()
        }
        shuffleView.setOnClickListener {
            shuffleDrawAndDiscardDeck()
        }
        deckView.setOnLongClickListener {
            if(drawDeck.isNotEmpty()) {
                showCards()
            }
            true
        }

        discardView.setOnClickListener {
            showDiscardedCards()
        }

    }


    private fun showDiscardedCards()
    {
        //Inflate the dialog with custom view
        val dialog = LayoutInflater.from(itemView.context!!).inflate(R.layout.cards_list_layout, null)
        dialog.card_list.layoutManager = LinearLayoutManager(itemView.context!!)


        var cardList = discardDeck.toMutableList() as ArrayList<Card>
        cardList.reverse()

        val adapter = DiscardedCardsAdapter(itemView.context!!, cardList)
        dialog.card_list.adapter = adapter
        //AlertDialogBuilder
        val builder = AlertDialog.Builder(itemView.context!!)
                .setView(dialog)
        //show dialog
        val  alertDialog = builder.show()
        alertDialog.window.setBackgroundDrawable(ColorDrawable(ColorUtils.setAlphaComponent(Color.BLACK, 100)))
    }

    private fun showCards()
    {
        //Inflate the dialog with custom view
        val dialog = LayoutInflater.from(itemView.context!!).inflate(R.layout.cards_list_layout, null)
        dialog.card_list.layoutManager = LinearLayoutManager(itemView.context!!)

        var cardList = ArrayList<ModifierCard>()
//        var count = 0

        for(card in drawDeck){
            cardList.add(ModifierCard(card))
        }

        val cardsAdapter = CardsAdapter(itemView.context!!, cardList, item!!.character.characterClass.color)

        val dragHelper = DragHelper(cardsAdapter)
        val touchHelper = ItemTouchHelper(dragHelper)
        cardsAdapter.setItemTouchHelper(touchHelper)
        dialog.card_list.adapter = cardsAdapter
        touchHelper.attachToRecyclerView(dialog.card_list)

        //AlertDialogBuilder
        val builder = AlertDialog.Builder(itemView.context!!)
                .setView(dialog)
        //show dialog
        val  alertDialog = builder.show()
        alertDialog.window.setBackgroundDrawable(ColorDrawable(ColorUtils.setAlphaComponent(Color.BLACK, 100)))
        alertDialog.setOnCancelListener {
            drawDeck.clear()
            for(modifierCard in cardList) {
                drawDeck.add(modifierCard.card)
            }
        }
    }

    private val shuffleObserver: (Boolean) -> Unit = {
        updateShuffle()
    }

//    val attackStatusObserver: (AttackStatus) -> Unit = {
//        updateAttackStatus()
//    }

    private val drawDeckObserver: (ArrayList<Card>) -> Unit = {
        updateBlessText()
        updateCurseText()
        updateMinus1Text()
        setImageViewGreyscale(deckView, it.isEmpty())
    }

    private val discardDeckObserver: (ArrayList<Card>) -> Unit = {
        if(discardDeck.isEmpty())
        {
            discardView.setImageResource(0)
            discardView2.setImageResource(0)
            discardView3.setImageResource(0)
        }
        else {

            var i = discardDeck.lastIndex
            shuffle = shuffle || discardDeck[i].special == CardSpecial.Shuffle
            discardView.setImageResource(Util.getImageResource(itemView.context, discardDeck[i--].id))

            if (i >= 0) {
                discardView2.setImageResource(Util.getImageResource(itemView.context, discardDeck[i--].id))
            }
            if (i >= 0) {
                discardView3.setImageResource(Util.getImageResource(itemView.context, discardDeck[i].id))
            }

            onStartAnimation()
        }
    }

    private fun onStartAnimation()
    {
        val startingX = deckView.x
        val endingX = deckView.x+discardView.getMeasuredWidth() + 5f
        val heightOffsetFactor = discardView.getMeasuredHeight() / 4f
        val widthOffsetFactor = discardView.getMeasuredWidth() / 2f * 1.10f
        val scale2Factor = 0.8f
        val scale3Factor = 0.6f

        val anim3X : ObjectAnimator = ObjectAnimator.ofFloat(discardView3, "x", endingX+widthOffsetFactor, endingX+widthOffsetFactor+widthOffsetFactor*scale2Factor)
        val anim3Y : ObjectAnimator = ObjectAnimator.ofFloat(discardView3, "y", deckView.y+heightOffsetFactor, deckView.y+heightOffsetFactor+heightOffsetFactor*scale2Factor)
        val anim3Scale = ValueAnimator.ofFloat(scale2Factor, scale3Factor)

        anim3Scale.addUpdateListener {
            val value = it.animatedValue as Float
            discardView3.scaleX = value
            discardView3.scaleY = value
        }
        val anim2X : ObjectAnimator = ObjectAnimator.ofFloat(discardView2, "x", endingX, endingX+widthOffsetFactor)
        val anim2Y : ObjectAnimator = ObjectAnimator.ofFloat(discardView2, "y", deckView.y, deckView.y+heightOffsetFactor)
        val anim1X : ObjectAnimator = ObjectAnimator.ofFloat(discardView, "x", startingX, endingX)
        val anim2Scale = ValueAnimator.ofFloat(1f, scale2Factor)

        anim2Scale.addUpdateListener {
            val value = it.animatedValue as Float
            discardView2.scaleX = value
            discardView2.scaleY = value
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(anim2Scale).with(anim2X).with(anim2Y).with(anim1X).with(anim3X).with(anim3Y).with(anim3Scale)

        animatorSet.interpolator = OvershootInterpolator(1.5f)
        animatorSet.duration = 500

        animatorSet.start()
    }

    private fun onStartAnimationStatus()
    {
        val animAlpha = ValueAnimator.ofFloat(1f, 0f)
        val animScale = ValueAnimator.ofFloat(1f, 2f)

        animAlpha.addUpdateListener {
            val value = it.animatedValue as Float
            statusIconView.alpha = value
        }

        animScale.addUpdateListener {
            val value = it.animatedValue as Float
            statusIconView.scaleX = value
            statusIconView.scaleY = value
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(animScale).with(animAlpha)
        animatorSet.interpolator = DecelerateInterpolator(0.5f)
        animatorSet.duration = 500

        animatorSet.start()


    }

    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        item.shuffle.observeForever(shuffleObserver)
//        item.attackStatus.observeForever(attackStatusObserver)
        item.drawDeck.observeForever(drawDeckObserver)
        item.discardDeck.observeForever(discardDeckObserver)

        shuffleDrawDeck()
    }

    override fun unbind() {
        item?.let {
            item!!.shuffle.removeObserver(shuffleObserver)
//            item!!.attackStatus.removeObserver(attackStatusObserver)
            item!!.drawDeck.removeObserver(drawDeckObserver)
            item!!.discardDeck.removeObserver(discardDeckObserver)
        }

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_tracker_deck_item

        fun inflate(parent: ViewGroup): TrackerDeckViewHolder {
            return TrackerDeckViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }

    fun updateBlessText(doShuffle : Boolean = true) {
        val count = (drawDeck).count { it == blessCard }
        var current =  0
        try {current = blessTextView.text.toString().toInt()} catch ( e :  NumberFormatException){}
        if(count > current && doShuffle){
            shuffleDrawDeck(true)
        }
        blessTextView.text = count.toString()
    }

    fun updateCurseText(doShuffle : Boolean = true) {
        val count = (drawDeck).count { it == curseCard }
        var current =  0
        try {current = curseTextView.text.toString().toInt()} catch ( e :  NumberFormatException){}
        if(count > current && doShuffle){
            shuffleDrawDeck(true)
        }
        curseTextView.text = count.toString()
    }

    fun updateMinus1Text(doShuffle : Boolean = true) {
        val count = (drawDeck + discardDeck).count { it == minus1Card }
        var current =  0
        try {current = minus1TextView.text.toString().toInt()} catch ( e :  NumberFormatException){}
        if(count > current && doShuffle){
            shuffleDrawDeck(true)
        }
        minus1TextView.text = count.toString()
    }

//    fun updateAttackStatus() {
//        setImageViewGreyscale(advantageView, attackStatus != AttackStatus.Advantage)
//        setImageViewGreyscale(disadvantageView, attackStatus != AttackStatus.Disadvantage)
//    }

    private fun updateShuffle() {
        shuffleView.isEnabled = shuffle
        setImageViewGreyscale(shuffleView, !shuffle)
    }

    private fun shuffleDrawAndDiscardDeck() {
        shuffle = false
        for(card in discardDeck) {
            if (card.special != CardSpecial.Remove) {
                drawDeck.add(card)
            }
        }
        discardDeck.clear()

        drawDeck = drawDeck
        discardDeck = discardDeck

        shuffleDrawDeck(true)
    }

    private fun shuffleDrawDeck(showToast: Boolean = false) {
        drawDeck.shuffle(Random(System.currentTimeMillis()))

        if(showToast) {
            toast.cancel()
            toast = Toast.makeText(itemView.context, itemView.context.getString(R.string.shuffled), Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    fun draw() {
        val card = drawCard()

        discardDeck.add(card)

        drawDeck = drawDeck
        discardDeck = discardDeck

        when{
            (item!!.status[Status.strengthen]!!.value && !item!!.status[Status.muddle]!!.value) -> {
                statusIconView.setImageResource(Util.getImageResource(itemView.context, "icon_status_strengthen"))
                onStartAnimationStatus()
            }
            (item!!.status[Status.muddle]!!.value && !item!!.status[Status.strengthen]!!.value) -> {
                statusIconView.setImageResource(Util.getImageResource(itemView.context, "icon_status_muddle"))
                onStartAnimationStatus()
            }
        }
    }

    fun drawCard(): Card {
        if (drawDeck.isEmpty()) {
            shuffleDrawAndDiscardDeck()
        }

        return drawDeck.removeAt(0)
    }
}
