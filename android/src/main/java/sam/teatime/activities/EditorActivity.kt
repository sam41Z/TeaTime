package sam.teatime.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.edit_infusion.view.*
import sam.teatime.R
import sam.teatime.TimeUtil
import sam.teatime.db.entities.Infusion
import sam.teatime.db.entities.Tea
import sam.teatime.db.entities.TeaWithInfusions
import sam.teatime.random
import sam.teatime.viewmodels.TeaViewModel
import java.util.*

class EditorActivity : AppCompatActivity() {

    var teaId: Int? = null

    var infusionEditTexts: ArrayList<EditText> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        if (intent != null && intent.hasExtra("teaId")) {
            teaId = intent.getIntExtra("teaId", 0)
        }

        addInfusion.setOnClickListener {
            addInfusion()
        }
    }


    override fun onResume() {
        super.onResume()

        val teaViewModel = ViewModelProviders.of(this).get(TeaViewModel::class.java)
        teaViewModel.allTeas.observe(this, Observer { teas ->
            teas?.find { tea -> tea.tea.id == teaId }?.let {
                updateView(it)
            }
        })
    }

    private fun updateView(tea: TeaWithInfusions) {
        teaName.setText(tea.tea.name, TextView.BufferType.EDITABLE)
        teaDescription.setText(tea.tea.description, TextView.BufferType.EDITABLE)
        infusions.removeAllViews()
        infusionEditTexts.clear()
        tea.infusions.forEach { infusion -> addInfusion(infusion) }
    }

    private fun addInfusion(infusion: Infusion? = null) {
        val edit = LayoutInflater.from(this).inflate(R.layout.edit_infusion, infusions, false)
        infusions.addView(edit)
        infusionEditTexts.add(edit.infusionTime)
        edit.clearInfusion.setOnClickListener {
            val edit = it.parent as View
            infusions.removeView(edit)
            infusionEditTexts.remove(edit.infusionTime)
        }

        if (infusion != null) {
            edit.infusionTime.setText(TimeUtil.secondsToString(infusion.time), TextView.BufferType.EDITABLE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.editor, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menuDone -> {
            saveChanges()
            finish()
            true
        }
        R.id.menuDelete -> {
            deleteTea()
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun saveChanges() {
        val teaViewModel = ViewModelProviders.of(this).get(TeaViewModel::class.java)
        val teaId = teaId
        if (teaId != null) {
            teaViewModel.update(Tea(teaId, teaName.text.toString(), teaDescription.text.toString()))
            var i = 0
            val infusions = infusionEditTexts.asSequence().map { edit ->
                Infusion(teaId, i++, TimeUtil.parseString(edit.text.toString()))
            }.toList()
            teaViewModel.update(teaId, infusions)
        } else {
            val newTeaId = Math.abs(Random().nextInt())
            teaViewModel.insert(Tea(newTeaId, teaName.text.toString(), teaDescription.text.toString()))
            var i = 0
            infusionEditTexts.asSequence().forEach {
                teaViewModel.insert(Infusion(newTeaId, i++, TimeUtil.parseString(it.text.toString())))
            }
        }
    }

    private fun deleteTea() {
        val teaId = teaId
        if (teaId != null) {
            val teaViewModel = ViewModelProviders.of(this).get(TeaViewModel::class.java)
            teaViewModel.deleteInfusionsForTeaId(teaId)
            teaViewModel.deleteByTeaId(teaId)
        }
    }
}
