package net.north101.android.ghplayertracker.data

import org.json.JSONException

enum class PerkAction {
    add,
    remove,
    ignore_object;

    companion object {
        @Throws(JSONException::class)
        fun parse(perkActionData: String): PerkAction {
            return when (perkActionData) {
                "add" -> PerkAction.add
                "remove" -> PerkAction.remove
                "ignore_object" -> PerkAction.ignore_object
                else -> throw JSONException(perkActionData)
            }
        }
    }
}
