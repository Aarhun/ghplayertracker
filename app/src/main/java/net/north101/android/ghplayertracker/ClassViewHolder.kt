package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import net.north101.android.ghplayertracker.data.CharacterClass


class ClassViewHolder(view: View) : BaseViewHolder<CharacterClass>(view) {
    private val imageView: ImageView = view.findViewById(R.id.class_icon)

    override fun bind(item: CharacterClass) {
        super.bind(item)

        val iconResId = imageView.context.resources.getIdentifier("ic_"+item.id, "drawable", imageView.context.packageName)
        this.imageView.setImageResource(iconResId)
    }

    companion object {
        var layout = R.layout.class_item_view

        fun inflate(parent: ViewGroup): ClassViewHolder {
            return ClassViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
