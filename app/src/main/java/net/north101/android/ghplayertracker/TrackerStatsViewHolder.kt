package net.north101.android.ghplayertracker

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import net.north101.android.ghplayertracker.data.Status
import net.north101.android.ghplayertracker.livedata.TrackerLiveData

class TrackerStatsViewHolder(itemView: View) : BaseViewHolder<TrackerLiveData>(itemView) {
    var healthContainerView: View = itemView.findViewById(R.id.health_container)
    var healthTextView: TextView = itemView.findViewById(R.id.health_text)
    var maxHealthTextView : TextView = itemView.findViewById(R.id.max_health_text)
    var healthPlusView: View = itemView.findViewById(R.id.health_plus)
    var healthMinusView: View = itemView.findViewById(R.id.health_minus)
    var statusIconView : ImageView = itemView.findViewById(R.id.status_icon)

    var healthCompanionLayout : View = itemView.findViewById(R.id.companion_health_container)
    var healthCompanionCardView : View = itemView.findViewById(R.id.companion_health_card_view)
    var healthCompanionContainerView: View = itemView.findViewById(R.id.companion_health_container)
    var healthCompanionTextView: TextView = itemView.findViewById(R.id.companion_health_text)
    var maxHealthCompanionTextView: TextView = itemView.findViewById(R.id.max_companion_health_text)
    var healthCompanionPlusView: View = itemView.findViewById(R.id.companion_health_plus)
    var healthCompanionMinusView: View = itemView.findViewById(R.id.companion_health_minus)
    var statusCompanionIconView : ImageView = itemView.findViewById(R.id.status_companion_icon)

    var xpContainerView: View = itemView.findViewById(R.id.xp_container)
    var xpTextView: TextView = itemView.findViewById(R.id.xp_text)
    var xpPlusView: View = itemView.findViewById(R.id.xp_plus)
    var xpMinusView: View = itemView.findViewById(R.id.xp_minus)

    var onNumberClickListener: ((String) -> Unit)? = null

    init {
        healthContainerView.setOnClickListener {
            onNumberClickListener?.invoke("health")
        }
        healthPlusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.status[Status.wound]!!.value = false
            if(item!!.status[Status.poison]?.value!!) {
                item!!.status[Status.poison]!!.value = false
            }
            else {
                item!!.health.value += 1
            }
        }))
        healthMinusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.health.value -= 1
            if(item!!.status[Status.poison]?.value!!) {
                onStartAnimationStatus(statusIconView)
            }
        }))

        healthCompanionContainerView.setOnClickListener {
            onNumberClickListener?.invoke("health")
        }
        healthCompanionPlusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.statusCompanion[Status.wound]!!.value = false
            if(item!!.statusCompanion[Status.poison]?.value!!) {
                item!!.statusCompanion[Status.poison]!!.value = false
            }
            else {
                item!!.healthCompanion.value += 1
            }
        }))
        healthCompanionMinusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.healthCompanion.value -= 1
            if(item!!.statusCompanion[Status.poison]?.value!!) {
                onStartAnimationStatus(statusCompanionIconView)
            }
        }))

        xpContainerView.setOnClickListener {
            onNumberClickListener?.invoke("xp")
        }
        xpPlusView.setOnTouchListener(RepeatListener({ _, count ->
            if (count >= 10) {
                item!!.xp.value = ((5 * Math.floor(item!!.xp.value / 5.0)) + 5).toInt()
            } else {
                item!!.xp.value += 1
            }
        }))
        xpMinusView.setOnTouchListener(RepeatListener({ _, count ->
            if (count >= 10) {
                item!!.xp.value = ((5 * Math.ceil(item!!.xp.value / 5.0)) - 5).toInt()
            } else {
                item!!.xp.value -= 1
            }
        }))
    }

    val healthObserver: (Int) -> Unit = {
        healthTextView.text = it.toString()
        val levelInfo = item!!.character.characterClass.levels.find { levelInfo -> levelInfo.level == item!!.character.level }!!
        setImageViewGreyscale(healthPlusView.findViewById(R.id.health_plus_button), it == levelInfo.health)
        setImageViewGreyscale(healthMinusView.findViewById(R.id.health_minus_button), it == 0)
    }

    val healthCompanionObserver: (Int) -> Unit = {
        healthCompanionTextView.text = it.toString()
        if(item!!.hasCompanion.value) {
            val levelInfo = item!!.character.characterClass.levelsCompanion.find { levelInfo -> levelInfo.level == item!!.character.level }!!
            setImageViewGreyscale(healthCompanionPlusView.findViewById(R.id.companion_health_plus_button), it == levelInfo.health)
            setImageViewGreyscale(healthCompanionMinusView.findViewById(R.id.companion_health_minus_button), it == 0)
        }
    }
    
    
    val xpObserver: (Int) -> Unit = {
        xpTextView.text = it.toString()
        setImageViewGreyscale(xpMinusView.findViewById(R.id.xp_minus_button), it == 0)
    }

    val hasCompaniorObserver: (Boolean) -> Unit = {
        when {
            it -> {
                healthCompanionLayout.visibility = View.VISIBLE
                healthCompanionCardView.visibility = View.VISIBLE
            }
            else -> {
                healthCompanionLayout.visibility = View.GONE
                healthCompanionCardView.visibility = View.GONE
            }
        }
    }


    private fun onStartAnimationStatus(view: View)
    {
        val animAlpha = ValueAnimator.ofFloat(1f, 0f)
        val animScale = ValueAnimator.ofFloat(1f, 1.5f)

        animAlpha.addUpdateListener {
            val value = it.animatedValue as Float
            view.alpha = value
        }

        animScale.addUpdateListener {
            val value = it.animatedValue as Float
            view.scaleX = value
            view.scaleY = value
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(animScale).with(animAlpha)
        animatorSet.interpolator = DecelerateInterpolator(0.5f)
        animatorSet.duration = 500

        animatorSet.start()


    }


    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        item.health.observeForever(healthObserver)
        item.healthCompanion.observeForever(healthCompanionObserver)
        item.hasCompanion.observeForever(hasCompaniorObserver)
        item.xp.observeForever(xpObserver)

        maxHealthTextView.text = item!!.health.maxValue.toString()
        maxHealthCompanionTextView.text = item!!.healthCompanion.maxValue.toString()
    }

    override fun unbind() {
        item?.let {
            item!!.health.removeObserver(healthObserver)
            item!!.healthCompanion.removeObserver(healthCompanionObserver)
            item!!.hasCompanion.removeObserver(hasCompaniorObserver)
            item!!.xp.removeObserver(xpObserver)
        }

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_tracker_stats_item

        fun inflate(parent: ViewGroup): TrackerStatsViewHolder {
            return TrackerStatsViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}


