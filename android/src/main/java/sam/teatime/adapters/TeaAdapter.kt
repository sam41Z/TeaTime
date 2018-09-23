package sam.teatime.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import sam.teatime.R
import sam.teatime.db.entities.Tea
import sam.teatime.viewholders.TeaViewHolder

class TeaAdapter : RecyclerView.Adapter<TeaViewHolder>() {

    private var teas = emptyList<Tea>() // Cached copy of words

    internal fun setTeas(teas: List<Tea>) {
        this.teas = teas
        notifyDataSetChanged()
    }

    override fun getItemCount() = teas.size

    override fun onBindViewHolder(holder: TeaViewHolder, position: Int) = holder.bind(teas[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeaViewHolder = TeaViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tea_card, parent, false))
}