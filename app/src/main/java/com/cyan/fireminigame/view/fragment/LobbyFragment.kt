package com.cyan.fireminigame.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cyan.fireminigame.R
import com.cyan.fireminigame.adapter.LobbyAdapter
import com.cyan.fireminigame.callback.LobbyResultListener
import com.cyan.fireminigame.databinding.FragmentLobbyBinding
import com.cyan.fireminigame.model.FireBaseManager.Companion.CREATE_ROOM_SUCCESS
import com.cyan.fireminigame.model.FireBaseManager.Companion.NUMBER_BASEBALL
import com.cyan.fireminigame.model.FireBaseManager.Companion.TEST_GAME1
import com.cyan.fireminigame.model.FireBaseManager.Companion.TEST_GAME2
import com.cyan.fireminigame.model.FireBaseManager.Companion.TEST_GAME3
import com.cyan.fireminigame.view.NewRoomDialog
import com.cyan.fireminigame.view.activity.GameActivity
import com.cyan.fireminigame.viewmodel.ViewModelFactory
import com.cyan.fireminigame.viewmodel.fragment.LobbyViewModel
import com.cyan.fireminigame.viewmodel.fragment.LobbyViewModel.Companion.GAME_NOT_INPUT
import com.cyan.fireminigame.viewmodel.fragment.LobbyViewModel.Companion.GAME_NOT_READY
import com.cyan.fireminigame.viewmodel.fragment.LobbyViewModel.Companion.TITLE_NOT_INPUT
import kotlinx.android.synthetic.main.fragment_lobby.*

class LobbyFragment : Fragment(), LobbyResultListener.RvIntoRoom {

    private lateinit var lobbyVM: LobbyViewModel
    private lateinit var binding: FragmentLobbyBinding
    private lateinit var lobbyAdapter: LobbyAdapter
    private lateinit var newRoomDialog: NewRoomDialog
    private lateinit var vmFactory: ViewModelFactory.WithNick
    private lateinit var nick: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        nick = arguments?.getString("nick")!!
        vmFactory = ViewModelFactory.WithNick(nick)
        lobbyAdapter = LobbyAdapter(context, this)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lobby, container, false)
        lobbyVM = ViewModelProvider(this, vmFactory).get(LobbyViewModel::class.java)
        newRoomDialog = NewRoomDialog(context, lobbyVM)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.lb = lobbyVM

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_lobby.adapter = lobbyAdapter

        lobbyVM.roomData.observe(viewLifecycleOwner) {
            lobbyAdapter.datas.clear()
            lobbyAdapter.datas = it
            lobbyAdapter.notifyDataSetChanged()
            if (prg_lb.visibility == View.VISIBLE) prg_lb.visibility = View.INVISIBLE
        }

        lobbyVM.createRoomBtn.observe(viewLifecycleOwner) {
            newRoomDialog.show()
        }

        lobbyVM.gameChecked.observe(viewLifecycleOwner) {
            var gameName = ""
            when (it) {
                NUMBER_BASEBALL -> gameName = resources.getString(R.string.numberBaseball)
                TEST_GAME1 -> gameName = resources.getString(R.string.test1)
                TEST_GAME2 -> gameName = resources.getString(R.string.test2)
                TEST_GAME3 -> gameName = resources.getString(R.string.test3)
            }
            newRoomDialog.setTitle(String.format(resources.getString(R.string.newRoomTitle),
                gameName))
            newRoomDialog.updateDialog()
        }

        lobbyVM.rcResult.observe(viewLifecycleOwner) {
            when (it) {
                TITLE_NOT_INPUT -> Toast.makeText(context, "방 제목을 입력해주세요.",
                    Toast.LENGTH_SHORT).show()
                GAME_NOT_INPUT -> Toast.makeText(context, "게임을 선택해 주세요.",
                    Toast.LENGTH_SHORT).show()
                GAME_NOT_READY -> Toast.makeText(context, "개발중입니다 ^^;",
                    Toast.LENGTH_SHORT).show()
                CREATE_ROOM_SUCCESS -> {
                    newRoomDialog.dismiss()
                    val intent = Intent(activity, GameActivity::class.java)
                    intent.putExtra("nick", nick)
                    intent.putExtra("key", lobbyVM.key)
                    intent.putExtra("host", true)
                    startActivity(intent)
                }
            }
        }

        lobbyVM.inResult.observe(viewLifecycleOwner) {
            if (it == "INTO_ROOM_FAIL") {
                Toast.makeText(context, "방 정보를 불러오는데 실패했습니다.",
                    Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, GameActivity::class.java)
                intent.putExtra("nick", nick)
                intent.putExtra("key", it)
                intent.putExtra("host", false)
                startActivity(intent)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        lobbyVM.stopRefreshRoom()
    }

    override fun rvIntoRoom(key: String) {
        lobbyVM.intoRoom(key, nick)
    }

}