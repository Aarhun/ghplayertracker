package net.north101.android.ghplayertracker.data

enum class Status() {
    disarm,
    stun,
    immobilize,
    poison,
    wound,
    muddle,
    invisible,
    strengthen,
    regenerate;

    companion object {
        fun fromString(key: String): Status {
            when{
                key == "disarm" -> return disarm
                key == "stun" -> return stun
                key == "immobilize" -> return immobilize
                key == "poison" -> return poison
                key == "wound" -> return wound
                key == "muddle" -> return muddle
                key == "invisible" -> return invisible
                key == "strengthen" -> return strengthen
                key == "regenerate" -> return regenerate
            }
            return disarm
        }
    }
}
