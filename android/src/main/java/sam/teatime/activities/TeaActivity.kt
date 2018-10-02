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

        teaRecycler.layoutManager = LinearLayoutManager(this)
        val adapter = TeaAdapter()
        teaRecycler.adapter = adapter

        teaRecycler.postDelayed({
            ActivityCompat.startPostponedEnterTransition(this)
        }, 100)

        teaViewModel = ViewModelProviders.of(this).get(TeaViewModel::class.java)
        teaViewModel.allTeas.observe(this, Observer { teas ->
            teas?.let { adapter.setTeas(it) }
        })

        addTea.setOnClickListener {
            startActivityFromClass(EditorActivity::class.java)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
