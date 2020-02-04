package net.north101.android.ghplayertracker.livedata

import net.north101.android.ghplayertracker.data.Card

class ModifierCard {
    var card: Card
    var revealed = InitLiveData(false)
    var selected = InitLiveData(false)

    constructor(card: Card) {
        this.card = card
        this.revealed.value = false
        this.selected.value = false
    }

    constructor(modifierCard: ModifierCard) {
        this.card = modifierCard.card
        this.revealed.value = modifierCard.revealed.value
        this.selected.value = modifierCard.selected.value
    }

}