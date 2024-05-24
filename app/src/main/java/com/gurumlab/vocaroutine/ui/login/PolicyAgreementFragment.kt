package com.gurumlab.vocaroutine.ui.login

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentPolicyAgreementBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.Constants.PRIVACY_URL
import com.gurumlab.vocaroutine.ui.common.Constants.TERMS_URL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.gurumlab.vocaroutine.data.source.local.UserDataSource
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
import androidx.lifecycle.lifecycleScope
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts

private typealias SignInSuccessListener = (uid: String) -> Unit

@AndroidEntryPoint
class PolicyAgreementFragment : BaseFragment<FragmentPolicyAgreementBinding>() {

    private var isLoginSuccess = false
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
    ): FragmentPolicyAgreementBinding {
        return FragmentPolicyAgreementBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        setSignInRequest()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isLoginSuccess) {
            hideBottomNavigation()
            isLoginSuccess = false
        }
    }

    private fun setLayout() {
        setTopAppBar()
        setButtonClickListener()
        setCheckBoxClickListener()
    }

    private fun setCheckBoxClickListener() {
        binding.cbAllAgree.setOnClickListener {
            val isChecked = binding.cbAllAgree.isChecked
            binding.cbAge.isChecked = isChecked
            binding.cbPrivacy.isChecked = isChecked
            binding.cbTerms.isChecked = isChecked
            binding.btnAgree.isEnabled = isChecked
        }

        val checkBoxListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            updateAgreeButtonState()
        }

        binding.cbAge.setOnCheckedChangeListener(checkBoxListener)
        binding.cbPrivacy.setOnCheckedChangeListener(checkBoxListener)
        binding.cbTerms.setOnCheckedChangeListener(checkBoxListener)
    }

    private fun setButtonClickListener() {
        binding.btnTerms.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL))
            startActivity(intent)
        }

        binding.btnPrivacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL))
            startActivity(intent)
        }
    }

    private fun updateAgreeButtonState() {
        val allChecked =
            binding.cbAge.isChecked && binding.cbPrivacy.isChecked && binding.cbTerms.isChecked
        binding.cbAllAgree.isChecked = allChecked
        binding.btnAgree.isEnabled = allChecked
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            isLoginSuccess = true
            val action = PolicyAgreementFragmentDirections.actionPolicyAgreementToHome()
            findNavController().navigate(action)
        } else {
            setSnackbarMessage(R.string.message_sign_in_failure)
        }
    }

    private fun setSignInRequest() {
        signInClient = Identity.getSignInClient(requireActivity())

        val legacySignInLauncher = getLegacySignInResultLauncher(onSuccess)
        val oneTapSignInLauncher = getOneTapSignInResultLauncher(onSuccess, legacySignInLauncher)
        binding.btnAgree.setOnClickListener {
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
                    setSnackbarMessage(R.string.message_sign_in_failure)
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
                        setSnackbarMessage(R.string.message_sign_in_failure)
                    } else {
                        user.getIdToken(true)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val firebaseIdToken = task.result.token
                                    if (firebaseIdToken.isNullOrBlank() || user.uid.isBlank()) {
                                        setSnackbarMessage(R.string.message_sign_in_failure)
                                    } else {
                                        lifecycleScope.launch {
                                            userDataSource.setUid(user.uid)
                                            updateUI(user)
                                        }
                                    }
                                } else {
                                    setSnackbarMessage(R.string.message_sign_in_failure)
                                }
                            }
                    }
                } else {
                    crashlytics.log("Firebase authentication failed: ${it.exception}")
                    setSnackbarMessage(R.string.message_sign_in_failure)
                }
            }
    }

    private fun setSnackbarMessage(messageId: Int) {
        Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_LONG).show()
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun hideBottomNavigation() {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = true
    }
}