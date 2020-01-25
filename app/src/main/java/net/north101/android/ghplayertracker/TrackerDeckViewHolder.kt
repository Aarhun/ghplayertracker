package net.north101.android.ghplayertracker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.discarded_cards_list_layout.view.*
import net.north101.android.ghplayertracker.data.Card
import net.north101.android.ghplayertracker.data.CardSpecial
import net.north101.android.ghplayertracker.data.PlayedCards
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

    var toast : Toast = Toast(itemView.context)
    var displayWidth = 0f
    var density = 0f

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

    var cleanedDiscardDeck: ArrayList<Card>
        get() = item!!.cleanedDiscardDeck.value
        set(value) {
            item!!.cleanedDiscardDeck.value = value
        }

    var playedCards: ArrayList<PlayedCards>
        get() = item!!.playedCards.value
        set(value) {
            item!!.playedCards.value = value
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
            updateBlessText()
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
            updateCurseText()
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
            updateMinus1Text()
        }))

        deckView.setOnClickListener {
            draw()
        }
        shuffleView.setOnClickListener {
            shuffle()
        }
        discardView.setOnClickListener {
            showDiscardedCards()
        }

        var wm = itemView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        displayWidth = point.x.toFloat()
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        density = metrics.density
    }


    private fun showDiscardedCards()
    {
        //Inflate the dialog with custom view
        val dialog = LayoutInflater.from(itemView.context!!).inflate(R.layout.discarded_cards_list_layout, null)
        var cardList: ArrayList<Card> = discardDeck
        if(cardList.count() > 1)
        {
            cardList.reverse()
        }

        val adapter = CardsAdapter(itemView.context!!, cardList)
        dialog.card_list.setAdapter(adapter)
        //AlertDialogBuilder
        val builder = AlertDialog.Builder(itemView.context!!)
                .setView(dialog)
        //show dialog
        val  mAlertDialog = builder.show()
    }

    private val shuffleObserver: (Boolean) -> Unit = {
        updateShuffle()
    }

//    val attackStatusObserver: (AttackStatus) -> Unit = {
//        updateAttackStatus()
//    }

    private val deckObserver: (ArrayList<Card>) -> Unit = {
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

    fun onStartAnimation()
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

        anim2X.interpolator = OvershootInterpolator(1.5f)
        anim2Y.interpolator = OvershootInterpolator(1.5f)
        anim3X.interpolator = OvershootInterpolator(1.5f)
        anim3Y.interpolator = OvershootInterpolator(1.5f)
        anim1X.interpolator = OvershootInterpolator(1.5f)
        anim2Scale.interpolator = OvershootInterpolator(1.5f)
        anim3Scale.interpolator = OvershootInterpolator(1.5f)
        animatorSet.duration = 500

        animatorSet.start()
    }

    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        item.shuffle.observeForever(shuffleObserver)
//        item.attackStatus.observeForever(attackStatusObserver)
        item.drawDeck.observeForever(deckObserver)
        item.discardDeck.observeForever(discardDeckObserver)
    }

    override fun unbind() {
        item?.let {
            item!!.shuffle.removeObserver(shuffleObserver)
//            item!!.attackStatus.removeObserver(attackStatusObserver)
            item!!.drawDeck.removeObserver(deckObserver)
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

    fun updateBlessText() {
        val count = (drawDeck + discardDeck).count { it == blessCard }
        blessTextView.text = count.toString()
    }

    fun updateCurseText() {
        val count = (drawDeck + discardDeck).count { it == curseCard }
        curseTextView.text = count.toString()
    }

    fun updateMinus1Text() {
        val count = (drawDeck + discardDeck).count { it == minus1Card }
        minus1TextView.text = count.toString()
    }

//    fun updateAttackStatus() {
//        setImageViewGreyscale(advantageView, attackStatus != AttackStatus.Advantage)
//        setImageViewGreyscale(disadvantageView, attackStatus != AttackStatus.Disadvantage)
//    }

    fun updateShuffle() {
        shuffleView.isEnabled = shuffle
        setImageViewGreyscale(shuffleView, !shuffle)
    }

    fun shuffle() {
        shuffle = false

        shuffleCount += 1
        for (playedCards in playedCards) {
            playedCards.shuffledIndex = shuffleCount
        }

        drawDeck.addAll(cleanedDiscardDeck)
        cleanedDiscardDeck.clear()
        discardDeck.clear()

        drawDeck = drawDeck
        discardDeck = discardDeck
        playedCards = playedCards

        toast.cancel()
        toast = Toast.makeText(itemView.context, itemView.context.getString(R.string.shuffled), Toast.LENGTH_SHORT)
        toast.show()
    }

    fun draw() {
        val playedCards =  drawNormal()
        if (playedCards.hasShuffle()) {
            shuffle = true
        }

        for (card in playedCards.pile1) {
            if (card.special != CardSpecial.Remove) {
                cleanedDiscardDeck.add(card)
            }
            discardDeck.add(card)
        }

        this.playedCards.add(playedCards)
        this.playedCards = this.playedCards
        drawDeck = drawDeck
        discardDeck = discardDeck

//        toast.cancel()
//        toast = Toast.makeText(itemView.context, String.format(itemView.context.getString(R.string.remaining), drawDeck.count()), Toast.LENGTH_SHORT)
//        toast.show()

    }

    fun drawNormal(): PlayedCards {
        return PlayedCards(drawCards(), null, null)
    }

    fun drawAdvantage(): PlayedCards {
        val item1 = drawCards()
        val item2 = ArrayList<Card>()
        if (item1.count() < 2) {
            val card = drawCard()
            if (card.special == CardSpecial.Rolling) {
                item1.add(card)
            } else {
                item2.add(card)
            }
        }
        return PlayedCards(item1, item2, null)
    }

    fun drawDisadvantage(): PlayedCards {
        val item1 = ArrayList<Card>()
        val item2 = ArrayList<Card>()
        val card1 = drawCard()
        val card2 = drawCard()

        item1.add(card1)
        if (card1.special == CardSpecial.Rolling && card2.special == CardSpecial.Rolling) {
            item1.add(card2)
            while (true) {
                val card = drawCard()
                item1.add(card)
                if (card.special != CardSpecial.Rolling)
                    break
            }
        } else {
            item2.add(card2)
        }
        return PlayedCards(item1, item2, null)
    }

    fun drawHouse(): PlayedCards {
        return PlayedCards(drawCards(), drawCards(), null)
    }

    fun drawCards(): ArrayList<Card> {
        val cards = ArrayList<Card>()

        while (true) {
            val card = drawCard()

            cards.add(card)
//            if (card.special != CardSpecial.Rolling)
                break
        }

        return cards
    }

    var random = Random()
    fun drawCard(): Card {
        if (drawDeck.isEmpty()) {
            shuffle()
        }

        val index = random.nextInt(drawDeck.size)
        return drawDeck.removeAt(index)
    }
}
