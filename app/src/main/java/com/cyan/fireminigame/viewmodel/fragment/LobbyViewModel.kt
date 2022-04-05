package com.cyan.fireminigame.viewmodel.fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyan.fireminigame.R
import com.cyan.fireminigame.callback.LobbyResultListener
import com.cyan.fireminigame.model.FireBaseManager
import com.cyan.fireminigame.model.FireBaseManager.Companion.CREATE_ROOM_SUCCESS
import com.cyan.fireminigame.model.FireBaseManager.Companion.NUMBER_BASEBALL
import com.cyan.fireminigame.model.FireBaseManager.Companion.TEST_GAME1
import com.cyan.fireminigame.model.FireBaseManager.Companion.TEST_GAME2
import com.cyan.fireminigame.model.FireBaseManager.Companion.TEST_GAME3
import com.cyan.fireminigame.model.GameRoom
import com.google.firebase.database.DataSnapshot

class LobbyViewModel(var nick: String) : ViewModel(), LobbyResultListener {

    companion object {
        const val TITLE_NOT_INPUT = 3000
        const val GAME_NOT_INPUT = 3001
        const val GAME_NOT_READY = 3002
    }

    var title = ""
    var key = ""
    var intoRoomKey = ""
    var nbChecked: Boolean? = false
    var t1Checked: Boolean? = false
    var t2Checked: Boolean? = false
    var t3Checked: Boolean? = false
    var roomData = MutableLiveData<ArrayList<GameRoom>>()
    var createRoomBtn = MutableLiveData<Boolean>()
    var gameChecked = MutableLiveData<Int>()
    var rcResult = MutableLiveData<Int>()
    var inResult = MutableLiveData<String>()
    var gameCheckMap = HashMap<Int, Boolean>()

    private val fs = FireBaseManager()

    init {
        gameCheckMap[NUMBER_BASEBALL] = false
        gameCheckMap[TEST_GAME1] = false
        gameCheckMap[TEST_GAME2] = false
        gameCheckMap[TEST_GAME3] = false
        fs.getRoom(this)
    }

    fun btnNewRoomDialog() {
        createRoomBtn.value = true
    }

    fun radioCheckChange(view: View) {
        when (view.id) {
            R.id.rb_nr_nb -> radioCheck(NUMBER_BASEBALL)
            R.id.rb_nr_test1 -> radioCheck(TEST_GAME1)
            R.id.rb_nr_test2 -> radioCheck(TEST_GAME2)
            R.id.rb_nr_test3 -> radioCheck(TEST_GAME3)
        }
    }

    fun btnNewRoom() {
        when {
            title == "" -> rcResult.value = TITLE_NOT_INPUT
            gameChecked.value == null -> rcResult.value = GAME_NOT_INPUT
            gameChecked.value == NUMBER_BASEBALL -> {
                key = nick + NUMBER_BASEBALL.toString()
                fs.createRoom(GameRoom(key, title, gameChecked.value, nick, ""), this)
            }

            else -> rcResult.value = GAME_NOT_READY
        }
    }

    private fun radioCheck(game: Int) {
        for (d in gameCheckMap) {
            if (d.key == game) d.setValue(true)
            else d.setValue(false)
        }
        gameChecked.value = game
        refreshRadioCheck()
    }

    private fun refreshRadioCheck() {
        nbChecked = gameCheckMap[NUMBER_BASEBALL]
        t1Checked = gameCheckMap[TEST_GAME1]
        t2Checked = gameCheckMap[TEST_GAME2]
        t3Checked = gameCheckMap[TEST_GAME3]

    }

    fun stopRefreshRoom() {
        fs.removeGetRoom()
    }

    fun intoRoom(key: String, nick: String){
        intoRoomKey = key
        fs.intoRoom(key, nick, this)
    }

    override fun roomCreated() {
        rcResult.value = CREATE_ROOM_SUCCESS
    }

    override fun refreshRoom(data: MutableIterable<DataSnapshot>) {
        val db = ArrayList<GameRoom>()
        for (r in data) {
            if (r.child("title").value.toString() == "null"){
                break
            }
            db.add(
                GameRoom(
                    r.key!!,
                    r.child("title").value.toString(),
                    r.child("game").value.toString().toInt(),
                    r.child("member1").toString(),
                    r.child("member2").toString()
                )
            )
        }
        roomData.value = db
    }

    override fun intoRoomResult(res: Boolean) {
        when (res){
            true -> inResult.value = intoRoomKey
            false -> inResult.value = "INTO_ROOM_FAIL"
        }
    }


}
