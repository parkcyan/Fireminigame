package com.cyan.fireminigame.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cyan.fireminigame.R
import com.cyan.fireminigame.databinding.ActivitySigninBinding
import com.cyan.fireminigame.model.FireBaseAuthentication.Companion.ERROR_PASSWORD_NOT_MATCH
import com.cyan.fireminigame.model.FireBaseAuthentication.Companion.ERROR_USER_NOT_FOUND
import com.cyan.fireminigame.model.FireBaseAuthentication.Companion.SIGN_IN_SUCCESS
import com.cyan.fireminigame.viewmodel.ConnectionCheck
import com.cyan.fireminigame.viewmodel.ViewModelFactory
import com.cyan.fireminigame.viewmodel.activity.SignInViewModel
import com.cyan.fireminigame.viewmodel.activity.SignInViewModel.Companion.EMAIL_NOT_INPUT
import com.cyan.fireminigame.viewmodel.activity.SignInViewModel.Companion.PWD_NOT_INPUT
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {

    private lateinit var signInVM: SignInViewModel
    private lateinit var binding: ActivitySigninBinding
    private lateinit var vmFactory: ViewModelFactory.WithActivity
    private lateinit var connectionCheck: ConnectionCheck

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionCheck = ConnectionCheck(this)
        vmFactory = ViewModelFactory.WithActivity(this)
        signInVM = ViewModelProvider(this, vmFactory).get(SignInViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
        binding.lifecycleOwner = this
        binding.si = signInVM

        connectionCheck.observe(this) {
            when (it) {
                false -> connectionLostDialog()
            }
        }

        signInVM.siResult.observe(this) {
            when (it) {
                EMAIL_NOT_INPUT -> Toast.makeText(
                    this, resources.getString(R.string.emailNotInput),
                    Toast.LENGTH_SHORT
                ).show()
                PWD_NOT_INPUT -> Toast.makeText(
                    this, resources.getString(R.string.pwdNotInput),
                    Toast.LENGTH_SHORT
                ).show()
                SIGN_IN_SUCCESS -> {
                    Toast.makeText(
                        this, resources.getString(R.string.signInSuccess),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                ERROR_PASSWORD_NOT_MATCH -> {
                    txt_si_checkEP.visibility = View.VISIBLE
                    Toast.makeText(
                        this, resources.getString(R.string.pwdNotMatch),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ERROR_USER_NOT_FOUND -> {
                    txt_si_checkEP.visibility = View.VISIBLE
                    Toast.makeText(
                        this, resources.getString(R.string.userNotFound),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> Toast.makeText(this, resources.getString(R.string.failSignIn), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        signInVM.suEm.observe(this) {
            if (it) {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }

        signInVM.siStatus.observe(this) {
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
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

    // 회원가입 완료 후 바로 그 계정으로 로그인 후 메인 액티비티로 넘어가기 위함
    override fun onStart() {
        super.onStart()
    }

}