package com.cyan.fireminigame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyan.fireminigame.R
import com.cyan.fireminigame.callback.LobbyResultListener
import com.cyan.fireminigame.model.FireBaseManager.Companion.NUMBER_BASEBALL
import com.cyan.fireminigame.model.GameRoom

class LobbyAdapter(private val context: Context?, private val rvIntoRoom: LobbyResultListener.RvIntoRoom) :
    RecyclerView.Adapter<LobbyAdapter.ViewHolder>() {

    var datas = mutableListOf<GameRoom>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recyclerview_lobby, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LobbyAdapter.ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        private val title: TextView = itemView.findViewById(R.id.txt_lb_title)
        private val game: TextView = itemView.findViewById(R.id.txt_lb_game)


        fun bind(item: GameRoom) {
            view.setOnClickListener{
                rvIntoRoom.rvIntoRoom(item.key)
            }
            title.text = item.title
            when (item.game) {
                NUMBER_BASEBALL -> game.text = context!!.resources.getString(R.string.numberBaseball)
            }
        }

    }
}