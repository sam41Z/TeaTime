package sam.teatime.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_pots.*
import kotlinx.android.synthetic.main.activity_teas.*
import org.ligi.kaxt.startActivityFromClass
import sam.teatime.R
import sam.teatime.adapters.PotAdapter
import sam.teatime.viewmodels.PotViewModel

class PotActivity : AppCompatActivity() {

    private lateinit var potViewModel: PotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.postponeEnterTransition(this)
        setContentView(R.layout.activity_pots)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        pot_recycler.layoutManager = LinearLayoutManager(this)
        val adapter = PotAdapter()
        pot_recycler.adapter = adapter

        pot_recycler.postDelayed({
            ActivityCompat.startPostponedEnterTransition(this)
        }, 100)

        potViewModel = ViewModelProviders.of(this).get(PotViewModel::class.java)
        potViewModel.allPots.observe(this, Observer { teas ->
            teas?.let { adapter.setPots(it) }
        })

        fab.setOnClickListener {
            startActivityFromClass(TeaActivity::class.java)
        }
    }
}
