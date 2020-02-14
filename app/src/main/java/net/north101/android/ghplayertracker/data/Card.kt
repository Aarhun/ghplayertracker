package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Parcelize
class Card(
    val id: String,
    val deckType: DeckType,
    val special: CardSpecial
) : Parcelable {
    init {
        Card.cache[id] = this
    }

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val data = JSONObject()
        data.put("id", id)
        data.put("deckType", deckType.toString())
        data.put("special",special.toString())
        return data
    }

    companion object {
        private val cache = HashMap<String, Card>()

        @Throws(JSONException::class)
        fun parse(cardId: String, data: JSONObject): Card {
            val card = cache[cardId]
            if (card != null)
                return card

            val deckType = DeckType.parse(data.getString("deck"))
            val special = CardSpecial.parse(data.optString("special", null))

            return Card(cardId, deckType, special)
        }

        @Throws(JSONException::class)
        fun parse(data: JSONObject): Card {
            val cardId = data.getString("id")
            val card = cache[cardId]
            if (card != null)
                return card

            val deckType = DeckType.parse(data.getString("deck"))
            val special = CardSpecial.parse(data.optString("special", null))

            return Card(cardId, deckType, special)
        }

        operator fun get(cardId: String): Card {
            return cache[cardId] ?: throw RuntimeException(cardId)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is Card && other.id == this.id)
    }
}
