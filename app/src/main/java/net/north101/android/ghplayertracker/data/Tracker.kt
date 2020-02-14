package net.north101.android.ghplayertracker.data

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.AttackStatus
import net.north101.android.ghplayertracker.Util
import net.north101.android.ghplayertracker.map
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException

@Parcelize
data class Tracker(
    var character: Character,
    var health: Int,
    var healthCompanion: Int,
    var xp: Int,
    var loot: Int,
    var status: HashMap<Status, Boolean>,
    var statusCompanion: HashMap<Status, Boolean>,
    var drawDeck: ArrayList<Card>,
    var discardDeck: ArrayList<Card>,
    var summons: ArrayList<Summon>,
    var shuffle: Boolean,
    var shuffleCount: Int,
    var attackStatus: AttackStatus,
    var houseRule: Boolean,
    var turn: Int
) : Parcelable {

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val data = JSONObject()
        data.put("health", health)
        data.put("healthCompanion", healthCompanion)
        data.put("xp", xp)
        data.put("loot", loot)

        val statusJSON = JSONObject()
        for(enum in Status.values()) {
            statusJSON.put(enum.toString(), status[enum])
        }
        data.put( "status", statusJSON)

        val statusCompanionJSON = JSONObject()
        for(enum in Status.values()) {
            statusCompanionJSON.put(enum.toString(), statusCompanion[enum])
        }
        data.put( "statusCompanion", statusCompanionJSON)

        data.put("drawDeck", JSONArray(drawDeck.map {
            it.toJSON()
        }))
        data.put("discardDeck", JSONArray(discardDeck.map {
            it.toJSON()
        }))
        data.put("shuffle", shuffle)

        return data
    }


    companion object {
        @Throws(JSONException::class, IOException::class, ParseException::class, kotlin.KotlinNullPointerException::class)
        fun parse(data: JSONObject): TrackerParseData {
            val health = data.optInt("health")
            val healthCompanion = data.optInt("healthCompanion")
            val xp = data.optInt("xp", 0)
            val loot = data.optInt("loot", 0)

            var statusData = data.optJSONObject("status")
            val status = HashMap(statusData.map {
                Status.fromString(it.key) to it.getBoolean()
            }.toMap())

            statusData = data.optJSONObject("statusCompanion")
            val statusCompanion = HashMap(statusData.map {
                Status.fromString(it.key) to it.getBoolean()
            }.toMap())

            val drawDeckData = data.optJSONArray("drawDeck")
            val drawDeck = ArrayList(drawDeckData?.map {
                Card.parse(it.getJSONObject())
            }.orEmpty())

            val discardDeckData = data.optJSONArray("discardDeck")
            val discardDeck = ArrayList(discardDeckData?.map {
                Card.parse(it.getJSONObject())
            }.orEmpty())

            val shuffle = data.optBoolean("shuffle", false)


            return TrackerParseData(
                    health,
                    healthCompanion,
                    xp,
                    loot,
                    status,
                    statusCompanion,
                    drawDeck,
                    discardDeck,
                    shuffle
            )
        }

    }


}

class TrackerParseData(
        var health: Int,
        var healthCompanion: Int,
        var xp: Int,
        var loot: Int,
        var status: HashMap<Status, Boolean>,
        var statusCompanion: HashMap<Status, Boolean>,
        var drawDeck: ArrayList<Card>,
        var discardDeck: ArrayList<Card>,
        var shuffle: Boolean
)


class TrackerData(val context: Context, var data: JSONObject) {
    companion object {
        fun load(context: Context): TrackerData {
            val data = try {
                loadJSON(context)
            } catch (e: Exception) {
                JSONObject()
            }

            return TrackerData(context, data)
        }

        private fun getFile(context: Context): File {
            return File(context.getExternalFilesDir(null), "tracker_save.json")
        }

        @Throws(IOException::class, JSONException::class, ParseException::class)
        private fun loadJSON(context: Context): JSONObject {
            val inputStream = FileInputStream(getFile(context))
            return JSONObject(Util.readInputString(inputStream))
        }

        fun saveExists(context: Context): Boolean {
            return getFile(context).exists()
        }
    }

    fun update(tracker: Tracker): TrackerData {
        data = tracker.toJSON()
        return this
    }

    fun delete(): TrackerData {
        data = JSONObject()
        return this
    }

    @Throws(IOException::class, JSONException::class)
    fun save() {
        FileOutputStream(getFile(context)).use { outputStream ->
            outputStream.write(data.toString().toByteArray())
        }
    }
}