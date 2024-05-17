package com.gurumlab.vocaroutine.ui.login

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.BuildConfig
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.source.local.UserDataSource
import com.gurumlab.vocaroutine.databinding.FragmentLoginBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private typealias SignInSuccessListener = (uid: String) -> Unit

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private var signInRequest: BeginSignInRequest = getBeginSignInRequest()
    private lateinit var signInClient: SignInClient

    private val onSuccess: SignInSuccessListener = { idToken ->
        getFirebaseCredential(idToken)
    }

    @Inject
    lateinit var userDataSource: UserDataSource

    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
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

        val legacySignInLauncher = getLegacySignInResultLauncher(onSuccess)
        val oneTapSignInLauncher = getOneTapSignInResultLauncher(onSuccess, legacySignInLauncher)
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
                    crashlytics.log("sign-in error: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                crashlytics.log("sign-in fail: ${e.localizedMessage}")
                requestLegacySignIn(legacySignInLauncher)
            }
            .addOnCanceledListener {
                crashlytics.log("sign-in cancelled")
            }
    }

    private fun getOneTapSignInResultLauncher(
        onSuccess: SignInSuccessListener,
        legacySignInLauncher: ActivityResultLauncher<IntentSenderRequest>
    ): ActivityResultLauncher<IntentSenderRequest> {
        return registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            try {
                signInWithGoogle(activityResult.data, onSuccess)
            } catch (e: ApiException) {
                crashlytics.log("One Tap sign-in failed: ${e.message}")
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
                    crashlytics.log("Legacy sign-in fail: ${e.localizedMessage}")
                    Snackbar.make(
                        binding.root,
                        getString(R.string.message_sign_in_failure),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                .addOnCanceledListener {
                    crashlytics.log("Legacy sign-in cancelled")
                }
        } else {
            onSuccess(lastToken)
        }
    }

    private fun getLegacySignInResultLauncher(onSuccess: SignInSuccessListener): ActivityResultLauncher<IntentSenderRequest> {
        return registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            try {
                signInWithGoogle(activityResult.data, onSuccess)
            } catch (e: Exception) {
                crashlytics.log("Legacy launcher error: ${e.localizedMessage}")
            }
        }
    }

    private fun signInWithGoogle(data: Intent?, onSuccess: SignInSuccessListener) {
        val credential = signInClient.getSignInCredentialFromIntent(data)
        val idToken = credential.googleIdToken
        if (idToken != null) {
            onSuccess(idToken)
        }
    }

    private fun getFirebaseCredential(idToken: String?) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user == null) {
                        showLoginErrorSnackBar()
                    } else {
                        user.getIdToken(true)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val firebaseIdToken = task.result.token
                                    if (firebaseIdToken.isNullOrBlank() || user.uid.isBlank()) {
                                        showLoginErrorSnackBar()
                                    } else {
                                        lifecycleScope.launch {
                                            userDataSource.setUid(user.uid)
                                            userDataSource.setUserToken(firebaseIdToken)
                                            updateUI(user)
                                        }
                                    }
                                } else {
                                    showLoginErrorSnackBar()
                                }
                            }
                    }
                } else {
                    crashlytics.log("Firebase authentication failed: ${it.exception}")
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

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }
}