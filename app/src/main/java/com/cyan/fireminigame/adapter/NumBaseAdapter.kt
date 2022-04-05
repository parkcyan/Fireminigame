package com.cyan.fireminigame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyan.fireminigame.R
import com.cyan.fireminigame.model.NbNotify

class NumBaseAdapter(private val context: Context?) : RecyclerView.Adapter<NumBaseAdapter.ViewHolder>() {

    var datas = mutableListOf<NbNotify>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recyclerview_nb, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val notify: TextView = itemView.findViewById(R.id.txt_nb_notify)

        fun bind(item: NbNotify) {
            notify.text = item.notify
        }
    }


}