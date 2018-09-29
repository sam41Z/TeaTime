package sam.teatime.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_teas.*
import org.ligi.kaxt.startActivityFromClass
import sam.teatime.R
import sam.teatime.adapters.TeaAdapter
import sam.teatime.viewmodels.TeaViewModel

class TeaActivity : AppCompatActivity() {

    private lateinit var teaViewModel: TeaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.postponeEnterTransition(this)
        setContentView(R.layout.activity_teas)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tea_recycler.layoutManager = LinearLayoutManager(this)
        val adapter = TeaAdapter()
        tea_recycler.adapter = adapter

        tea_recycler.postDelayed({
            ActivityCompat.startPostponedEnterTransition(this)
        },100)

        teaViewModel = ViewModelProviders.of(this).get(TeaViewModel::class.java)
        teaViewModel.allTeas.observe(this, Observer { teas ->
            teas?.let { adapter.setTeas(it) }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        android.R.id.home -> {
            startActivityFromClass(TimerActivity::class.java)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
