package com.gurumlab.vocaroutine

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.gurumlab.vocaroutine.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val oneTapResultLauncher = getResultLauncher()
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            if (isLogin()) {
                val action = LoginFragmentDirections.actionLoginToHome()
                findNavController().navigate(action)
            }
        }

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.btnGoogleSignIn.setOnClickListener {
            setSignInRequest()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val action = LoginFragmentDirections.actionLoginToHome()
            findNavController().navigate(action)
        } else {
            Toast.makeText(context, "로그아웃됨", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        updateUI(null)
    }

    private fun setSignInRequest() {
        oneTapClient = Identity.getSignInClient(requireActivity())

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    oneTapResultLauncher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("LoginFragment", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                Log.d("LoginFragment", "Begin sign-in request failed", e)
            }
    }

    private fun getResultLauncher() =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                try {
                    signInWithOneTap(data)
                } catch (e: ApiException) {
                    Log.e("LoginFragment", "One Tap sign-in failed", e)
                }
            }
        }

    private fun signInWithOneTap(data: Intent?) {
        val credential = oneTapClient.getSignInCredentialFromIntent(data)
        val idToken = credential.googleIdToken
        if (idToken != null) {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        saveUserToken(user)
                        updateUI(user)
                    } else {
                        Log.e(
                            "LoginFragment",
                            "Firebase authentication failed",
                            it.exception
                        )
                        updateUI(null)
                    }
                }
        }
    }

    private fun saveUserToken(user: FirebaseUser?) {

        lifecycleScope.launch {
            VocaRoutineApplication.getInstance().getDataStore().setUserToken(user.toString())
        }
    }

    private suspend fun isLogin(): Boolean {
        val userToken = VocaRoutineApplication.getInstance().getDataStore().savedUserToken.first()

        return userToken.isNotEmpty()
    }
}