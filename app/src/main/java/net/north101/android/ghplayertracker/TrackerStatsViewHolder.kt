package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import net.north101.android.ghplayertracker.livedata.TrackerLiveData

class TrackerStatsViewHolder(itemView: View) : BaseViewHolder<TrackerLiveData>(itemView) {
    var healthContainerView: View = itemView.findViewById(R.id.health_container)
    var healthTextView: TextView = itemView.findViewById(R.id.health_text)
    var healthPlusView: View = itemView.findViewById(R.id.health_plus)
    var healthMinusView: View = itemView.findViewById(R.id.health_minus)

    var healthCompanionLayout : View = itemView.findViewById(R.id.companion_health_container)
    var healthCompanionCardView : View = itemView.findViewById(R.id.companion_health_card_view)
    var healthCompanionContainerView: View = itemView.findViewById(R.id.companion_health_container)
    var healthCompanionTextView: TextView = itemView.findViewById(R.id.companion_health_text)
    var healthCompanionPlusView: View = itemView.findViewById(R.id.companion_health_plus)
    var healthCompanionMinusView: View = itemView.findViewById(R.id.companion_health_minus)

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
            item!!.health.value += 1
        }))
        healthMinusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.health.value -= 1
        }))

        healthCompanionContainerView.setOnClickListener {
            onNumberClickListener?.invoke("health")
        }
        healthCompanionPlusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.healthCompanion.value += 1
        }))
        healthCompanionMinusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.healthCompanion.value -= 1
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


    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        item.health.observeForever(healthObserver)
        item.healthCompanion.observeForever(healthCompanionObserver)
        item.hasCompanion.observeForever(hasCompaniorObserver)
        item.xp.observeForever(xpObserver)
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


