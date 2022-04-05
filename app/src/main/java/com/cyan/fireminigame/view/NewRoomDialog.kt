package com.cyan.fireminigame.view

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.cyan.fireminigame.R
import com.cyan.fireminigame.databinding.DialogNewroomBinding
import com.cyan.fireminigame.viewmodel.fragment.LobbyViewModel

class NewRoomDialog(var context: Context?, gameVM: LobbyViewModel) {
    private val dialog = Dialog(context!!)
    private var binding: DialogNewroomBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_newroom, null, false
    )

    init {
        binding.gm = gameVM
        dialog.setContentView(binding.root)
    }

    private fun dialogResize(context: Context, dialog: Dialog) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = dialog.window
            val x = (rect.width() * 0.8f).toInt()
            val y = (rect.height() * 0.5f).toInt()
            window?.setLayout(x, y)
        } else {

        }

    }

    fun setTitle(title :String) {
        dialog.findViewById<EditText>(R.id.edt_nr_title).setText(title)
    }

    fun updateDialog() {
        binding.invalidateAll()
    }

    fun show() {
        dialogResize(context!!, dialog)
        dialog.show()
    }

    fun dismiss(){
        dialog.dismiss()
    }


}