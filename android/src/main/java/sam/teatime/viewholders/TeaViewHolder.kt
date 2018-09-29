package sam.teatime.viewholders

import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.tea_card.view.*
import org.ligi.kaxt.startActivityFromClass
import sam.teatime.activities.TimerActivity
import sam.teatime.db.entities.TeaWithInfusions
import sam.teatime.State
import sam.teatime.timer.Timer


class TeaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(tea: TeaWithInfusions) {
        itemView.tea_name.text = tea.tea.name

        if (itemView.infoIcon != null) {

            itemView.infoIcon.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                itemView.context.startActivity(intent)
            }

            itemView.setOnClickListener {
                Timer.getTimer().resetAndPause()

                val activity = itemView.context as FragmentActivity
                State.lastSelectedTeaId = tea.tea.id
                State.lastSelectedInfusionIndex = 0

                activity.startActivityFromClass(TimerActivity::class.java)

                itemView.postDelayed({
                    activity.finish()
                }, 1000)
            }
        }
    }
}