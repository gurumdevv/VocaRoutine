package com.gurumlab.vocaroutine.ui.login

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.BuildConfig
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.source.local.UserDataSource
import com.gurumlab.vocaroutine.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private var signInRequest: BeginSignInRequest = getBeginSignInRequest()
    private lateinit var signInClient: SignInClient

    @Inject
    lateinit var userDataSource: UserDataSource

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSignInRequest()
        hideBottomNavigation(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val action = LoginFragmentDirections.actionLoginToHome()
            findNavController().navigate(action)
        } else {
            Snackbar.make(requireView(), getString(R.string.logout_success), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setSignInRequest() {
        signInClient = Identity.getSignInClient(requireActivity())

        val legacySignInLauncher = getLegacySignInResultLauncher()
        val oneTapSignInLauncher = getOneTapSignInResultLauncher(legacySignInLauncher)
        binding.btnGoogleSignIn.setOnClickListener {
            startGoogleSignIn(oneTapSignInLauncher, legacySignInLauncher)
        }
    }

    private fun getBeginSignInRequest() = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()
        ).build()

    private fun startGoogleSignIn(
        oneTapSignInLauncher: ActivityResultLauncher<IntentSenderRequest>,
        legacySignInLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    oneTapSignInLauncher.launch(
                        IntentSenderRequest
                            .Builder(result.pendingIntent.intentSender)
                            .build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(tag, "sign-in error: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                Log.e(tag, "sign-in fail: ${e.localizedMessage}")
                requestLegacySignIn(legacySignInLauncher)
            }
            .addOnCanceledListener {
                Log.e(tag, "sign-in cancelled")
            }
    }

    private fun getOneTapSignInResultLauncher(
        legacySignInLauncher: ActivityResultLauncher<IntentSenderRequest>
    ): ActivityResultLauncher<IntentSenderRequest> {
        return registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            try {
                signInWithGoogle(activityResult.data)
            } catch (e: ApiException) {
                Log.e("LoginFragment", "One Tap sign-in failed", e)
                requestLegacySignIn(legacySignInLauncher)
            }
        }
    }

    private fun requestLegacySignIn(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        val lastToken = GoogleSignIn.getLastSignedInAccount(requireContext())?.idToken
        if (lastToken == null) {
            val request = GetSignInIntentRequest.builder()
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .build()

            signInClient
                .getSignInIntent(request)
                .addOnSuccessListener { pendingIntent ->
                    launcher.launch(
                        IntentSenderRequest
                            .Builder(pendingIntent.intentSender)
                            .build()
                    )
                }
                .addOnFailureListener { e ->
                    Log.e(tag, "Legacy sign-in fail: ${e.localizedMessage}")
                    Snackbar.make(
                        binding.root,
                        getString(R.string.message_sign_in_failure),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                .addOnCanceledListener {
                    Log.e(tag, "Legacy sign-in cancelled")
                }
        } else {
            getFirebaseCredential(lastToken)
        }
    }

    private fun getLegacySignInResultLauncher(): ActivityResultLauncher<IntentSenderRequest> {
        return registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            try {
                signInWithGoogle(activityResult.data)
            } catch (e: Exception) {
                Log.e(tag, "Legacy launcher error: ${e.localizedMessage}", e)
            }
        }
    }

    private fun signInWithGoogle(data: Intent?) {
        val credential = signInClient.getSignInCredentialFromIntent(data)
        val idToken = credential.googleIdToken
        if (idToken != null) {
            getFirebaseCredential(idToken)
        }
    }

    private fun getFirebaseCredential(idToken: String?) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val uid = user?.uid
                    if (uid.isNullOrBlank()) {
                        showLoginErrorSnackBar()
                    } else {
                        lifecycleScope.launch {
                            userDataSource.setUid(uid)
                            updateUI(user)
                        }
                    }
                } else {
                    Log.e(
                        "LoginFragment",
                        "Firebase authentication failed",
                        it.exception
                    )
                    showLoginErrorSnackBar()
                }
            }
    }

    private fun showLoginErrorSnackBar() {
        Snackbar.make(
            requireView(),
            R.string.message_sign_in_failure,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private suspend fun isLogin(): Boolean {
        val userToken = userDataSource.getUid()

        return userToken.isNotEmpty()
    }

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }
}