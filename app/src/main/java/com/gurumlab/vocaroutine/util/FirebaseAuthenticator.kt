package com.gurumlab.vocaroutine.util

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await


object FirebaseAuthenticator {

    suspend fun getUserToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return try {
            user.getIdToken(true).await()?.token
        } catch (e: Exception) {
            null
        }
    }
}