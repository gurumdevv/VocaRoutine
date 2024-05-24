package com.gurumlab.vocaroutine.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import com.gurumlab.vocaroutine.ui.common.Event

class NetworkConnection(context: Context) : LiveData<Event<Boolean>>() {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private lateinit var networkCallback: NetworkCallback

    override fun onActive() {
        super.onActive()
        postValue(Event(updateConnection()))
        connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun connectivityManagerCallback(): NetworkCallback {
        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(Event(true))
            }

            override fun onUnavailable() {
                super.onUnavailable()
                postValue(Event(false))
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(Event(false))
            }
        }
        return networkCallback
    }

    private fun updateConnection(): Boolean {
        val currentNetwork = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return false
        val result = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
            else -> false
        }
        return result
    }
}