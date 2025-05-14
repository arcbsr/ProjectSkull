package com.hope.firebase.auth

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GoogleAuthProviderStrategy(
    private val fragment: Fragment,
    private val clientId: String,
    private val onLoginSuccess: (String) -> Unit,
    private val onLoginError: (Exception) -> Unit
) : AuthProviderStrategy {

    private val auth: FirebaseAuth = Firebase.auth
    private val googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(fragment.requireContext(), gso)
    }

    override fun startLogin() {
        val signInIntent = googleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun handleLoginResult(requestCode: Int, data: Intent?) {
        if (requestCode != RC_SIGN_IN) return

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    onLoginSuccess(it.user?.displayName ?: "Unknown")
                }
                .addOnFailureListener {
                    onLoginError(it)
                }
        } catch (e: ApiException) {
            onLoginError(e)
        }
    }

    override fun signOut(onSignOut: () -> Unit) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess().addOnCompleteListener {
                onSignOut()
            }
        }
    }

    override fun getCurrentUserID(): String? {
        return auth.currentUser?.uid
    }
    override fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
    override fun getCurrentUserName(): String? {
        return auth.currentUser?.displayName
    }
    override fun getCurrentUserPhotoUrl(): String? {
        return auth.currentUser?.photoUrl?.toString()
    }
    override fun getCurrentUserPhoneNumber(): String? {
        return auth.currentUser?.phoneNumber
    }
    override fun getCurrentUserDisplayName(): String? {
        return auth.currentUser?.displayName
    }

}
