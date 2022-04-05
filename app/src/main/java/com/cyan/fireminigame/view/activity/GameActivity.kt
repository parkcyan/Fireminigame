package com.cyan.fireminigame.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cyan.fireminigame.R
import com.cyan.fireminigame.databinding.ActivityGameBinding
import com.cyan.fireminigame.model.FireBaseManager.Companion.NOT_READY
import com.cyan.fireminigame.model.FireBaseManager.Companion.NUMBER_BASEBALL
import com.cyan.fireminigame.model.FireBaseManager.Companion.READY
import com.cyan.fireminigame.viewmodel.ViewModelFactory
import com.cyan.fireminigame.viewmodel.activity.GameViewModel
import com.cyan.fireminigame.viewmodel.activity.GameViewModel.Companion.HOST_LEAVES
import com.cyan.fireminigame.viewmodel.activity.GameViewModel.Companion.NOT_HOST
import com.cyan.fireminigame.viewmodel.activity.GameViewModel.Companion.NO_OPPONENT
import com.cyan.fireminigame.viewmodel.activity.MainViewModel
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var gameVM: GameViewModel
    private lateinit var binding: ActivityGameBinding
    private lateinit var vmFactory: ViewModelFactory.WithKey
    private var game = 0
    private var gameStart = 0
    private var outActivity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmFactory = ViewModelFactory.WithKey(
            intent.extras!!.getString("key")!!, intent.extras!!.getBoolean("host"))
        gameVM = ViewModelProvider(this, vmFactory).get(GameViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        binding.lifecycleOwner = this
        binding.gm = gameVM

        toolbar = findViewById(R.id.toolbar_game)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        gameVM.game.observe(this) {
            when (it) {
                NUMBER_BASEBALL -> {
                    game = NUMBER_BASEBALL
                    img_gm_gm.setImageResource(R.drawable.ic_baseball_white_24dp)
                }
            }
            binding.invalidateAll()
        }

        gameVM.m1Ready.observe(this) {
            when (it) {
                READY -> btn_gm_m1.setBackgroundResource(R.drawable.round_border_white_2dp_ready)
                NOT_READY -> btn_gm_m1.setBackgroundResource(R.drawable.round_border_white_2dp)
            }
        }

        gameVM.m2Ready.observe(this) {
            when (it) {
                READY -> btn_gm_m2.setBackgroundResource(R.drawable.round_border_white_2dp_ready)
                NOT_READY -> btn_gm_m2.setBackgroundResource(R.drawable.round_border_white_2dp)
            }
        }

        gameVM.outRoom.observe(this) {
            finish()
        }

        gameVM.kickedOutRoom.observe(this) {
            when (it) {
                HOST_LEAVES -> Toast.makeText(this, "방장이 방을 나갔습니다.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        gameVM.gameStart.observe(this) {
            when (it) {
                NOT_HOST -> Toast.makeText(this, "게임의 방장이 아닙니다.",
                    Toast.LENGTH_SHORT).show()
                NO_OPPONENT -> Toast.makeText(this, "상대방이 없습니다.",
                    Toast.LENGTH_SHORT).show()
                NOT_READY -> Toast.makeText(this, "레디하지 않은 플레이어가 있습니다.",
                    Toast.LENGTH_SHORT).show()
                NUMBER_BASEBALL -> {
                    gameStart = 1
                    val intent = Intent(this, NumBaseActivity::class.java)
                    intent.putExtra("key", this.intent.extras!!.getString("key")!!)
                    intent.putExtra("host", this.intent.extras!!.getBoolean("host"))
                    startActivity(intent)
                }
            }
        }


    }

    override fun onBackPressed() {
        outRoomDialog()
    }

    private fun outRoomDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("나가기")
        builder.setMessage("대기 방을 나가시겠습니까?")
        builder.setNegativeButton("아니오") { _, _ -> }
            .setPositiveButton("예") { _, _ ->
                gameVM.outRoom()
            }
            .show()
    }
    /**
    override fun onStop() {
        super.onStop()
        if (gameStart != 1) {
            gameVM.outRoom()
            outActivity = 1
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (outActivity == 1) {
            Toast.makeText(this, "어플리케이션이 비활성화되어 방에서 나갔습니다.",
                Toast.LENGTH_SHORT).show()
            outActivity = 0
        }
    }
    **/
}