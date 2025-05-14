package com.hope.firebase.database

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginHelper_backup(
    private val fragment: Fragment,
    private val clientId: String,
    private val onLoginSuccess: (String) -> Unit,
    private val onLoginError: (Exception) -> Unit
) {

    private val auth: FirebaseAuth = Firebase.auth
    private val googleSignInClient: GoogleSignInClient

    companion object {
        const val RC_SIGN_IN = 1001
    }

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(fragment.requireContext(), gso)
    }

    fun startGoogleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleLoginResult(requestCode: Int, data: Intent?) {
        if (requestCode != RC_SIGN_IN) return

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    onLoginSuccess(it.user?.displayName!!)
                }
                .addOnFailureListener {
                    onLoginError(it)
                }
        } catch (e: ApiException) {
            onLoginError(e)
        }
    }

    fun signOut(onSignOut: () -> Unit = {}) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            // Also revoke access to force account chooser next time
            googleSignInClient.revokeAccess().addOnCompleteListener {
                onSignOut()
            }
        }
    }

    fun getCurrentUser(): String? {
        return auth.currentUser?.uid
    }
}
