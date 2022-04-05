package com.cyan.fireminigame.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cyan.fireminigame.R
import com.cyan.fireminigame.adapter.ChatAdapter
import com.cyan.fireminigame.databinding.FragmentChatBinding
import com.cyan.fireminigame.viewmodel.ViewModelFactory
import com.cyan.fireminigame.viewmodel.fragment.ChatViewModel
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {

    private lateinit var chatVM: ChatViewModel
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var vmFactory: ViewModelFactory.WithNick

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vmFactory = ViewModelFactory.WithNick(arguments?.getString("nick")!!)
        chatAdapter = ChatAdapter(context, arguments?.getString("nick")!!)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        chatVM = ViewModelProvider(this, vmFactory).get(ChatViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.ct = chatVM
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_chat.adapter = chatAdapter

        chatVM.chatData.observe(viewLifecycleOwner) {
            chatAdapter.datas.clear()
            chatAdapter.datas = it
            chatAdapter.notifyDataSetChanged()
            rv_chat.scrollToPosition(chatAdapter.datas.size - 1)
            if (prg_chat.visibility == View.VISIBLE) prg_chat.visibility = View.INVISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatVM.stopRefreshChat()
    }

}