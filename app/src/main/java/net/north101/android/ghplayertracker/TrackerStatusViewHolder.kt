package net.north101.android.ghplayertracker

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.CycleInterpolator
import android.widget.ImageView
import net.north101.android.ghplayertracker.data.Card
import net.north101.android.ghplayertracker.data.Status
import net.north101.android.ghplayertracker.livedata.TrackerLiveData

class TrackerStatusViewHolder(itemView: View) : BaseViewHolder<TrackerLiveData>(itemView) {
    val mainStatusTracker: View = itemView.findViewById(R.id.character_tracker_status_main)
    val secondaryStatusTracker: View = itemView.findViewById(R.id.character_tracker_status_secondary)
    val secondarySpacer: View = itemView.findViewById(R.id.spacer_secondary)

    init {
        for (status in Status.values()) {
            statusToView(status, mainStatusTracker).setOnClickListener {
                item!!.status[status]!!.value = !(item!!.status[status]!!.value)
            }
        }

        for (status in Status.values()) {
            statusToView(status, secondaryStatusTracker).setOnClickListener {
                item!!.statusCompanion[status]!!.value = !(item!!.statusCompanion[status]!!.value)
            }
        }
    }

    val hasCompaniorObserver: (Boolean) -> Unit = {
        when {
            it -> {
                secondaryStatusTracker.visibility = View.VISIBLE
                secondarySpacer.visibility = View.VISIBLE
                mainStatusTracker.setPadding(0,0, 0, 0)
            }
            else -> {
                mainStatusTracker.setPadding(100,0, 100, 0)
                secondarySpacer.visibility = View.GONE
                secondaryStatusTracker.visibility = View.GONE
            }
        }
    }

    private val statusObservers: Map<Status, (Boolean) -> Unit> = Status.values().map { status ->
        val observer: ((Boolean) -> Unit) = {
            setImageViewGreyscale(statusToView(status,mainStatusTracker), !it)
        }
        status to observer
    }.toMap()

    private val statusCompanionObservers: Map<Status, (Boolean) -> Unit> = Status.values().map { status ->
        val observer: ((Boolean) -> Unit) = {
            setImageViewGreyscale(statusToView(status,secondaryStatusTracker), !it)
        }
        status to observer
    }.toMap()

    private val discardDeckObserver: (ArrayList<Card>) -> Unit = {
        if(!it.isEmpty()) {
                if(item!!.status[Status.strengthen]?.value!! && !item!!.status[Status.muddle]?.value!!) {onStartAnimation(mainStatusTracker.findViewById(R.id.status_strengthen))}
                if(item!!.status[Status.muddle]?.value!! && !item!!.status[Status.strengthen]?.value!!) {onStartAnimation(mainStatusTracker.findViewById(R.id.status_muddle))}
                if(item!!.statusCompanion[Status.strengthen]?.value!! && !item!!.statusCompanion[Status.muddle]?.value!!) {onStartAnimation(secondaryStatusTracker.findViewById(R.id.status_strengthen))}
                if(item!!.statusCompanion[Status.muddle]?.value!! && !item!!.statusCompanion[Status.strengthen]?.value!!) {onStartAnimation(secondaryStatusTracker.findViewById(R.id.status_muddle))}
            }
    }

    private val healthObserver: (Int) -> Unit = {
        if(item!!.status[Status.poison]?.value!!) {onStartAnimation(mainStatusTracker.findViewById(R.id.status_poison))}
    }

    private val healthCompanionObserver: (Int) -> Unit = {
        if(item!!.statusCompanion[Status.poison]?.value!!) {onStartAnimation(secondaryStatusTracker.findViewById(R.id.status_poison))}
    }

    fun onStartAnimation(view: View)
    {
        val animScale = ValueAnimator.ofFloat(1f, 1.2f)

        animScale.addUpdateListener {
            val value = it.animatedValue as Float
            view.scaleX = value
            view.scaleY = value
        }

        animScale.interpolator = AnticipateOvershootInterpolator( 1.5f)
        animScale.interpolator = CycleInterpolator(2f)
        animScale.duration = 500

        animScale.start()
    }


    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        for (o in statusObservers.entries) {
            item.status[o.key]!!.observeForever(o.value)
        }

        for (o in statusCompanionObservers.entries) {
            item.statusCompanion[o.key]!!.observeForever(o.value)
        }
        item.hasCompanion.observeForever(hasCompaniorObserver)
        item.discardDeck.observeForever(discardDeckObserver)
        item.health.observeForever(healthObserver)
        item.healthCompanion.observeForever(healthCompanionObserver)
    }

    override fun unbind() {
        item?.let {
            for (o in statusObservers.entries) {
                item!!.status[o.key]!!.removeObserver(o.value)
            }
            for (o in statusCompanionObservers.entries) {
                item!!.statusCompanion[o.key]!!.removeObserver(o.value)
            }
            item!!.hasCompanion.removeObserver(hasCompaniorObserver)
            item!!.discardDeck.removeObserver(discardDeckObserver)
            item!!.health.removeObserver(healthObserver)
            item!!.healthCompanion.removeObserver(healthCompanionObserver)
        }

        super.unbind()
    }

    fun statusToView(status: Status, view: View): ImageView {
        return when (status) {
            Status.disarm -> view.findViewById(itemView.context.resources.getIdentifier("status_disarm", "id", itemView.context.packageName))
            Status.stun -> view.findViewById(itemView.context.resources.getIdentifier("status_stun", "id", itemView.context.packageName))
            Status.immobilize -> view.findViewById(itemView.context.resources.getIdentifier("status_immobilize", "id", itemView.context.packageName))
            Status.strengthen -> view.findViewById(itemView.context.resources.getIdentifier("status_strengthen", "id", itemView.context.packageName))
            Status.poison -> view.findViewById(itemView.context.resources.getIdentifier("status_poison", "id", itemView.context.packageName))
            Status.wound -> view.findViewById(itemView.context.resources.getIdentifier("status_wound", "id", itemView.context.packageName))
            Status.muddle -> view.findViewById(itemView.context.resources.getIdentifier("status_muddle", "id", itemView.context.packageName))
            Status.invisible -> view.findViewById(itemView.context.resources.getIdentifier("status_invisible", "id", itemView.context.packageName))
        }
    }

    companion object {
        const val layout = R.layout.character_tracker_status_item_layout

        fun inflate(parent: ViewGroup): TrackerStatusViewHolder {
            return TrackerStatusViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
