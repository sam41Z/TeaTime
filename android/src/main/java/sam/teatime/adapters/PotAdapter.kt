package sam.teatime.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import sam.teatime.R
import sam.teatime.db.entities.Pot
import sam.teatime.viewholders.PotViewHolder

class PotAdapter : RecyclerView.Adapter<PotViewHolder>() {

    private var pots = emptyList<Pot>() // Cached copy of words

    internal fun setPots(pots: List<Pot>) {
        this.pots = pots
        notifyDataSetChanged()
    }

    override fun getItemCount() = pots.size

    override fun onBindViewHolder(holder: PotViewHolder, position: Int) = holder.bind(pots[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PotViewHolder = PotViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tea_card, parent, false))
}