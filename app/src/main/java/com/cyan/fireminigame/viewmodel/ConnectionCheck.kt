package com.cyan.fireminigame.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import kotlin.concurrent.timer

class ConnectionCheck(val context: Context) : LiveData<Boolean>() {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    init {
        if (!connectivityManager.isDefaultNetworkActive) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        connectivityManager.registerDefaultNetworkCallback(callBack())
    }

    override fun onInactive() {
        super.onInactive()
        try {
            connectivityManager.unregisterNetworkCallback(callBack())
        } catch (e: Exception) {
        }
    }

    fun callBack(): ConnectivityManager.NetworkCallback {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                timer(period = 1000, initialDelay = 1000) {
                    if (!connectivityManager.isDefaultNetworkActive) {
                        postValue(false)
                        cancel()
                    } else cancel()
                }
            }
        }
        return networkCallback
    }

}