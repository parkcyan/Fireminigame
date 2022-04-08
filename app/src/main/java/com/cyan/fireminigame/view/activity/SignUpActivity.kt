package com.cyan.fireminigame.view.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cyan.fireminigame.R
import com.cyan.fireminigame.databinding.ActivitySignupBinding
import com.cyan.fireminigame.viewmodel.activity.SignUpViewModel
import kotlinx.android.synthetic.main.activity_signup.*
import com.cyan.fireminigame.model.FireBaseAuthentication.Companion.ERROR_EMAIL_ALREADY_IN_USE
import com.cyan.fireminigame.model.FireBaseAuthentication.Companion.ERROR_NICK_ALREADY_IN_USE
import com.cyan.fireminigame.model.FireBaseAuthentication.Companion.SIGN_UP_SUCCESS
import com.cyan.fireminigame.viewmodel.ConnectionCheck
import com.cyan.fireminigame.viewmodel.ViewModelFactory
import com.cyan.fireminigame.viewmodel.activity.SignUpViewModel.Companion.NICK_CHAR_WRONG
import com.cyan.fireminigame.viewmodel.activity.SignUpViewModel.Companion.NICK_LENGTH_WRONG
import com.cyan.fireminigame.viewmodel.activity.SignUpViewModel.Companion.NICK_OK

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpVM: SignUpViewModel
    private lateinit var binding: ActivitySignupBinding
    private lateinit var vmFactory: ViewModelFactory.WithActivity
    private lateinit var connectionCheck: ConnectionCheck

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionCheck = ConnectionCheck(this)
        vmFactory = ViewModelFactory.WithActivity(this)
        signUpVM = ViewModelProvider(this, vmFactory).get(SignUpViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.lifecycleOwner = this
        binding.su = signUpVM

        connectionCheck.observe(this) {
            when (it) {
                false -> connectionLostDialog()
            }
        }

        signUpVM.suReady.observe(this) {
            if (it) btn_su.setBackgroundResource(R.color.purple_500)
            else btn_su.setBackgroundResource(R.color.dark)
        }
        signUpVM.suResult.observe(this) {
            when (it) {
                SIGN_UP_SUCCESS -> {
                    Toast.makeText(this, resources.getString(R.string.signUpSuccess), Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                ERROR_NICK_ALREADY_IN_USE -> {
                    Toast.makeText(this, resources.getString(R.string.nickAlreadyInUse), Toast.LENGTH_SHORT)
                        .show()
                }
                ERROR_EMAIL_ALREADY_IN_USE -> {
                    txt_su_emAlready.visibility = View.VISIBLE
                    Toast.makeText(this, resources.getString(R.string.emailAlreadyInUse), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> Toast.makeText(this, resources.getString(R.string.failSignUp), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        signUpVM.pwdMatch.observe(this) {
            if (it) txt_su_pwdMatch.visibility = View.INVISIBLE
            else txt_su_pwdMatch.visibility = View.VISIBLE
        }
        signUpVM.nicCheck.observe(this) {
            when (it) {
                NICK_OK -> txt_su_nicCheck.visibility = View.INVISIBLE
                NICK_LENGTH_WRONG -> {
                    txt_su_nicCheck.text = resources.getString(R.string.nicLen)
                    txt_su_nicCheck.visibility = View.VISIBLE
                }
                NICK_CHAR_WRONG -> {
                    txt_su_nicCheck.text = resources.getString(R.string.nicSc)
                    txt_su_nicCheck.visibility = View.VISIBLE
                }
            }
        }
        signUpVM.emVer.observe(this) {
            if (it) txt_su_emVer.visibility = View.INVISIBLE
            else txt_su_emVer.visibility = View.VISIBLE
        }
        signUpVM.pwd6.observe(this) {
            if (it) txt_su_pwd6.visibility = View.INVISIBLE
            else txt_su_pwd6.visibility = View.VISIBLE
        }

    }

    private fun connectionLostDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle(resources.getString(R.string.connectionLost))
        builder.setMessage(resources.getString(R.string.connectionLostDialog))
        builder.setCancelable(false)
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ -> }
        builder.setOnDismissListener {
            finish()
        }
        builder.show()
    }

}


