package net.north101.android.ghplayertracker

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import net.north101.android.ghplayertracker.data.SelectableCharacter

class CharacterViewHolder(view: View) : BaseViewHolder<SelectableCharacter>(view) {
    private val iconView: ImageView = view.findViewById(R.id.icon)
    private val nameView: TextView = view.findViewById(R.id.name)
    private val levelView: TextView = view.findViewById(R.id.level)

    override fun bind(item: SelectableCharacter) {
        super.bind(item)

        val iconResId = itemView.context.resources.getIdentifier("ic_"+item.character.characterClass.id, "drawable", itemView.context.packageName)
        this.iconView.setImageResource(iconResId)
        this.nameView.text = item.character.name
        this.levelView.text = item.character.currentLevel.level.toString()
        if (item.selected) {
            itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    companion object {
        var layout = R.layout.character_item

        fun inflate(parent: ViewGroup): CharacterViewHolder {
            return CharacterViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
