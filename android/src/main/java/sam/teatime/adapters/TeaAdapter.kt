package sam.teatime.adapters

import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.tea_card.view.*
import sam.teatime.R
import sam.teatime.State
import sam.teatime.activities.EditorActivity
import sam.teatime.db.entities.TeaWithInfusions
import sam.teatime.timer.Timer

class TeaAdapter : RecyclerView.Adapter<TeaAdapter.TeaViewHolder>() {

    private var teas = emptyList<TeaWithInfusions>() // Cached copy of words

    internal fun setTeas(teas: List<TeaWithInfusions>) {
        this.teas = teas
        notifyDataSetChanged()
    }

    override fun getItemCount() = teas.size

    override fun onBindViewHolder(holder: TeaViewHolder, position: Int) = holder.bind(teas[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeaViewHolder = TeaViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tea_card, parent, false))

    class TeaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(tea: TeaWithInfusions) {
            val activity = itemView.context as FragmentActivity
            itemView.teaName.text = tea.tea.name
            itemView.tea_description.text = tea.tea.description
            itemView.tea_infusions_number.text = tea.infusions.size.toString()

            itemView.setOnClickListener {
                Timer.getTimer().resetAndPause()

                State.lastSelectedTeaId = tea.tea.id
                State.lastSelectedInfusionIndex = 0

                activity.finish()
            }
            itemView.tea_edit.setOnClickListener {
                val intent = Intent(activity, EditorActivity::class.java).apply {
                    putExtra("teaId", tea.tea.id)
                }
                activity.startActivity(intent)
            }
        }
    }
}