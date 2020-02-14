package net.north101.android.ghplayertracker.livedata

import net.north101.android.ghplayertracker.AttackStatus
import net.north101.android.ghplayertracker.data.*
import java.util.*
import kotlin.collections.ArrayList

class TrackerLiveData {
    var character: Character
    val health = BoundedIntLiveData(0, minValue = 0)
    val healthCompanion = BoundedIntLiveData(0, minValue = 0)
    val hasCompanion = InitLiveData(false)
    val xp = BoundedIntLiveData(0, minValue = 0)
    val loot = BoundedIntLiveData(0, minValue = 0)
    val status = HashMap(Status.values().map {
        it to InitLiveData(false)
    }.toMap())
    val statusCompanion = HashMap(Status.values().map {
        it to InitLiveData(false)
    }.toMap())
    val drawDeck = InitLiveData<ArrayList<Card>>(ArrayList())
    val discardDeck = InitLiveData<ArrayList<Card>>(ArrayList())
    val summons = InitLiveData<ArrayList<SummonLiveData>>(ArrayList())

    val shuffle = InitLiveData(false)
    val shuffleCount = InitLiveData(0)
    val attackStatus = InitLiveData(AttackStatus.None)
    val houseRule = InitLiveData(false)
    val turn = InitLiveData( 1)
    val shuffleNow = InitLiveData(false)

    constructor(character: Character) {
        this.character = character
        this.health.value = character.maxHealth
        this.health.maxValue = character.maxHealth
        this.hasCompanion.value = character.maxHealthCompanion != -1
        this.healthCompanion.value = character.maxHealthCompanion
        this.healthCompanion.maxValue = character.maxHealthCompanion
        this.xp.value = 0
        this.loot.value = 0
        this.status.forEach {
            it.value.value = false
        }
        this.statusCompanion.forEach {
            it.value.value = false
        }
        this.drawDeck.value = ArrayList()
        this.discardDeck.value = ArrayList()
        this.summons.value = ArrayList()

        this.shuffle.value = false
        this.shuffleCount.value = 0
        this.attackStatus.value = AttackStatus.None
        this.houseRule.value = false
        this.turn.value = 1
        this.shuffleNow.value = false
    }

    constructor(data: Tracker) {
        character = data.character
        health.value = data.health
        health.maxValue = data.character.maxHealth
        healthCompanion.value = data.healthCompanion
        healthCompanion.maxValue = data.character.maxHealthCompanion
        xp.value = data.xp
        loot.value = data.loot
        status.putAll(data.status.map {
            it.key to InitLiveData(it.value)
        }.toMap())
        status.putAll(data.statusCompanion.map {
            it.key to InitLiveData(it.value)
        }.toMap())
        drawDeck.value = data.drawDeck
        discardDeck.value = data.discardDeck
        summons.value = ArrayList(data.summons.map {
            SummonLiveData(it)
        })
        shuffle.value = data.shuffle
        shuffleCount.value = data.shuffleCount
        attackStatus.value = data.attackStatus
        houseRule.value = data.houseRule
        turn.value = data.turn
        shuffleNow.value = false
    }

    fun toParcel(): Tracker {
        return Tracker(
            character,
            health.value,
            healthCompanion.value,
            xp.value,
            loot.value,
            HashMap(status.entries.map {
                it.key to it.value.value
            }.toMap()),
            HashMap(status.entries.map {
                    it.key to it.value.value
            }.toMap()),
            drawDeck.value,
            discardDeck.value,
            ArrayList(summons.value.map {
                it.toParcel()
            }),
            shuffle.value,
            shuffleCount.value,
            attackStatus.value,
            houseRule.value,
            turn.value
        )
    }

    fun loadSavedData(trackerParseData: TrackerParseData) {
        this.health.value = trackerParseData.health
        this.healthCompanion.value = trackerParseData.healthCompanion
        this.xp.value = trackerParseData.xp
        this.loot.value = trackerParseData.loot

        for( enum in trackerParseData.status.keys) {
            this.status[enum]!!.value = trackerParseData.status[enum]!!
        }
        for( enum in trackerParseData.statusCompanion.keys) {
            this.statusCompanion[enum]!!.value = trackerParseData.statusCompanion[enum]!!
        }
        this.drawDeck.value.clear()
        this.discardDeck.value.clear()
        this.drawDeck.value = trackerParseData.drawDeck
        this.discardDeck.value = trackerParseData.discardDeck
        this.shuffle.value = trackerParseData.shuffle
    }
}