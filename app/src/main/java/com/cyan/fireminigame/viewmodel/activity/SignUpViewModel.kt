package com.cyan.fireminigame.viewmodel.activity

import android.app.Activity
import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyan.fireminigame.callback.SuResultListener
import com.cyan.fireminigame.model.FireBaseManager
import com.cyan.fireminigame.model.FireBaseAuthentication
import com.cyan.fireminigame.model.FireBaseAuthentication.Companion.ERROR_NICK_ALREADY_IN_USE
import java.util.regex.Pattern

class SignUpViewModel(activity: Activity) : ViewModel(), SuResultListener {

    companion object {
        const val MIN_CLICK_INTERVAL: Long = 1500
        const val NICK_OK: Int = 0
        const val NICK_LENGTH_WRONG: Int = 1
        const val NICK_CHAR_WRONG: Int = 2 // 닉네임은 알파벳, 한글, 숫자만 사용가능
    }

    var email = ""
    var pwd = ""
    var nick = ""
    var pwdCheck = ""
    private var currentClickTime: Long = 0
    private var lastClickTime: Long = 0
    var suReady = MutableLiveData<Boolean>() // 회원가입 준비 완료 여부
    var emVer = MutableLiveData<Boolean>() // 올바른 이메일 형식 여부
    var pwd6 = MutableLiveData<Boolean>() // 비밀번호 6자리 이상 여부
    var pwdMatch = MutableLiveData<Boolean>() // 비밀번호 확인 일치 여부
    var nicCheck = MutableLiveData<Int>() // 닉네임 중복 체크 결과값
    var suResult = MutableLiveData<Int>() // 회원가입 수행 결과값

    private val regexEm: String = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"
    private val regexSc: String = "^[0-9a-zA-Z가-힣]*$"
    private val auth = FireBaseAuthentication(activity)
    private val fs = FireBaseManager()

    // 이메일 형식 체크
    fun emailVerify(e: CharSequence) {
        val p = Pattern.compile(regexEm)
        val m = p.matcher(e)
        emVer.value = m.matches()
        email = e.toString()
        setSignUpReady(emVer.value, pwd6.value, pwdMatch.value, nicCheck.value)
    }

    // 패스워드 6자리 이상 체크
    fun pwd6(p: CharSequence) {
        pwd6.value = p.length > 5
        pwd = p.toString()
        if (pwdCheck != "") {
            pwdMatch(pwdCheck)
        }
        setSignUpReady(emVer.value, pwd6.value, pwdMatch.value, nicCheck.value)
    }

    // 비밀번호 일치 체크
    fun pwdMatch(c: CharSequence) {
        pwdMatch.value = pwd == c.toString()
        setSignUpReady(emVer.value, pwd6.value, pwdMatch.value, nicCheck.value)
    }

    // 닉네임 형식 체크
    fun nicCheck(c: CharSequence) {
        when {
            c.length < 2 -> nicCheck.value = NICK_LENGTH_WRONG
            !nicScCheck(c) -> nicCheck.value = NICK_CHAR_WRONG
            else -> {
                nicCheck.value = NICK_OK
                nick = c.toString()
            }
        }
        setSignUpReady(emVer.value, pwd6.value, pwdMatch.value, nicCheck.value)
    }

    private fun nicScCheck(n: CharSequence): Boolean {
        val p = Pattern.compile(regexSc)
        val m = p.matcher(n)
        return m.matches()
    }

    // 회원가입 준비 상태 확인
    private fun setSignUpReady(
        emVer: Boolean?,
        pwd6: Boolean?,
        pwdMatch: Boolean?,
        nicCheck: Int?,
    ) {
        suReady.postValue(emVer == true && pwd6 == true && pwdMatch == true && nicCheck == NICK_OK)
    }

    // 회원가입버튼 클릭
    fun btnSuOnclick() {
        if (timeCheck() && suReady.value == true) {
            fs.userNickAlready(nick, this)
        }
    }

    // fs.userNickAlready()
    override fun getNickAlreadyResult(suRes: Boolean) {
        if (suRes) suResult.value = ERROR_NICK_ALREADY_IN_USE
        else auth.signUp(email, pwd, nick, this)
    }

    // auth.signUp
    override fun getSuResult(suRes: Int) {
        suResult.value = suRes
    }

    // MIN_CLICK_INTERVAL 내에 버튼 중복 클릭 방지
    private fun timeCheck(): Boolean {
        currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime: Long = currentClickTime - lastClickTime
        lastClickTime = currentClickTime
        return elapsedTime > MIN_CLICK_INTERVAL
    }

}

