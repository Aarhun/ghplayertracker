package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.north101.android.ghplayertracker.livedata.TrackerLiveData

class TrackerStatsViewHolder(itemView: View) : BaseViewHolder<TrackerLiveData>(itemView) {
    var healthContainerView: View = itemView.findViewById(R.id.health_container)
    var healthTextView: TextView = itemView.findViewById(R.id.health_text)
    var healthPlusView: View = itemView.findViewById(R.id.health_plus)
    var healthMinusView: View = itemView.findViewById(R.id.health_minus)

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
    }
    val xpObserver: (Int) -> Unit = {
        xpTextView.text = it.toString()
    }

    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        item.health.observeForever(healthObserver)
        item.xp.observeForever(xpObserver)
    }

    override fun unbind() {
        item?.let {
            item!!.health.removeObserver(healthObserver)
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


