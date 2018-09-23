package sam.teatime.viewholders

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.tea_card.view.*
import sam.teatime.activities.MainActivity
import sam.teatime.db.entities.Tea
import sam.teatime.model.State
import sam.teatime.timer.Timer


class TeaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(tea: Tea) {
        itemView.tea_name.text = tea.name

        if (itemView.infoIcon != null) {

            itemView.infoIcon.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                itemView.context.startActivity(intent)
            }

            itemView.setOnClickListener {
                Timer.resetAndPause()
                State.lastSelectedTeaId = tea.id

                val activity = itemView.context as Activity
                val intent = Intent(activity, MainActivity::class.java)

                activity.startActivity(intent)

                itemView.postDelayed({
                    activity.finish()
                }, 1000)
            }
        }
    }
}