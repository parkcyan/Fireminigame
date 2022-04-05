package com.cyan.fireminigame.view.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cyan.fireminigame.R
import com.cyan.fireminigame.adapter.NumBaseAdapter
import com.cyan.fireminigame.databinding.ActivityNbBinding
import com.cyan.fireminigame.model.NbNotify
import com.cyan.fireminigame.viewmodel.ViewModelFactory
import com.cyan.fireminigame.viewmodel.activity.NumBaseViewModel
import com.cyan.fireminigame.viewmodel.activity.NumBaseViewModel.Companion.NUM_REDUP
import com.cyan.fireminigame.viewmodel.activity.NumBaseViewModel.Companion.NUM_SHORT
import com.cyan.fireminigame.viewmodel.activity.NumBaseViewModel.Companion.SUBMIT_FIRST
import kotlinx.android.synthetic.main.activity_nb.*
import kotlinx.android.synthetic.main.fragment_chat.*

class NumBaseActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var numBaseVM: NumBaseViewModel
    private lateinit var binding: ActivityNbBinding
    private lateinit var numBaseAdapter: NumBaseAdapter
    private lateinit var vmFactory: ViewModelFactory.WithKey
    var turn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmFactory = ViewModelFactory.WithKey(
            intent.extras!!.getString("key")!!, intent.extras!!.getBoolean("host"))
        numBaseAdapter = NumBaseAdapter(this)
        numBaseVM = ViewModelProvider(this, vmFactory).get(NumBaseViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nb)
        binding.lifecycleOwner = this
        binding.nb = numBaseVM

        toolbar = findViewById(R.id.toolbar_nb)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        rv_numBase.adapter = numBaseAdapter

        numBaseVM.refreshNb.observe(this) {
            binding.invalidateAll()
        }

        numBaseVM.numCursor.observe(this) {
            setLineColor(it)
        }
        numBaseVM.numSubmit.observe(this) {
            when (it) {
                NUM_SHORT -> {
                    txt_nb_status.text = resources.getString(R.string.nbNumShort)
                    txt_nb_status.setTextColor(ContextCompat.getColor(this, R.color.red))
                }
                NUM_REDUP -> {
                    txt_nb_status.text = resources.getString(R.string.nbNumDup)
                    txt_nb_status.setTextColor(ContextCompat.getColor(this, R.color.red))
                }
                SUBMIT_FIRST -> {
                    txt_nb_status.text = resources.getString(R.string.nbSubmit)
                    txt_nb_status.setTextColor(ContextCompat.getColor(this, R.color.white))
                    numBaseVM.sendNotify(resources.getString(R.string.nbStatFirstSubmit), true)
                }
            }
        }
        numBaseVM.status.observe(this) {
            when (it) {
                "WAIT_SUBMIT" -> {
                    if (turn) {
                        txt_nb_status.text = resources.getString(R.string.nbMyTurn)
                        txt_nb_status.setTextColor(ContextCompat.getColor(this, R.color.white))
                    } else {
                        txt_nb_status.text = resources.getString(R.string.nbOpTurn)
                        txt_nb_status.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                }
                "HOST_OUT" -> {
                    if (!intent.extras!!.getBoolean("host")) {
                        gameOverDialog()
                        finish()
                    }
                }
                "GUEST_OUT" -> {
                    if (intent.extras!!.getBoolean("host")) {
                        gameOverDialog()
                        finish()
                    }
                }
            }
        }
        numBaseVM.notify.observe(this) {
            when (it) {
                "NUMBER_SUBMIT" -> numBaseVM.sendNotify(resources.getString(R.string.nbStatFirst))
            }
        }
        numBaseVM.notifyData.observe(this) {
            numBaseAdapter.datas.clear()
            numBaseAdapter.datas = it
            numBaseAdapter.notifyDataSetChanged()
            rv_numBase.scrollToPosition(numBaseAdapter.datas.size - 1)
        }
        numBaseVM.myTurn.observe(this) {
            turn = it
        }
        numBaseVM.isOut.observe(this) {
            when (it) {
                true -> txt_nb_out.setTextColor(ContextCompat.getColor(this, R.color.red))
                false -> txt_nb_out.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }
        numBaseVM.outGame.observe(this) {
            finish()
        }


    }

    private fun setLineColor(line: Int) {
        when (line) {
            1 -> {
                edt_nb_n1.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.cyan)
                edt_nb_n2.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
                edt_nb_n3.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
            }
            2 -> {
                edt_nb_n1.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
                edt_nb_n2.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.cyan)
                edt_nb_n3.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
            }
            3, 4 -> {
                edt_nb_n1.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
                edt_nb_n2.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
                edt_nb_n3.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.cyan)
            }
            0 -> {
                edt_nb_n1.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
                edt_nb_n2.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
                edt_nb_n3.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
            }
        }
    }

    private fun gameOverDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("게임 종료")
        builder.setMessage("상대방이 게임을 포기하였습니다.")
        builder.setPositiveButton("예") { _, _ ->
            finish()
        }.show()
    }

    override fun onBackPressed() {
        outGameDialog()
    }

    private fun outGameDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("나가기")
        builder.setMessage("게임을 포기하시겠습니까?")
        builder.setNegativeButton("아니오") { _, _ -> }
            .setPositiveButton("예") { _, _ ->
                numBaseVM.outGame()
            }
            .show()
    }
}