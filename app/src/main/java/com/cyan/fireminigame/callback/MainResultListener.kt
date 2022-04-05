package com.cyan.fireminigame.callback

interface MainResultListener {
    fun getNick(string: String)
    fun getSignInStatus(boolean: Boolean)
}