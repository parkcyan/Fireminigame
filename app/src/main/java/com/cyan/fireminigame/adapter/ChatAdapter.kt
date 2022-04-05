package com.cyan.fireminigame.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cyan.fireminigame.R
import com.cyan.fireminigame.model.Chat

class ChatAdapter(private val context: Context?, var nick: String) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var datas = mutableListOf<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recyclerview_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val chat: TextView = itemView.findViewById(R.id.txt_ct_chat)

        fun bind(item: Chat) {
            if (context != null) {
                chat.text = String.format(
                    context.resources.getString(R.string.chatContext),
                    item.nick, item.chat
                ) // 채팅 이름 : 채팅 내용
            }
            if (nick == item.nick && context != null) {
                chat.setTextColor(
                    ContextCompat.getColor(context, R.color.cyan)
                )
            } else if (nick != item.nick && context != null) {
                chat.setTextColor(
                    ContextCompat.getColor(context, R.color.white)
                )
            } // 본인이 쓴 채팅은 다른 채팅과 색 구별
        }

    }

}