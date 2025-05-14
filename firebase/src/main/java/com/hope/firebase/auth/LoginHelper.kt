package com.hope.firebase.auth

import android.content.Intent
import com.hope.firebase.auth.AuthProviderStrategy

class LoginHelper(
    private val authProviderStrategy: AuthProviderStrategy
) {
    fun startLogin() = authProviderStrategy.startLogin()
    fun handleLoginResult(requestCode: Int, data: Intent?) =
        authProviderStrategy.handleLoginResult(requestCode, data)

    fun signOut(onSignOut: () -> Unit = {}) = authProviderStrategy.signOut(onSignOut)
    fun getCurrentUserID() = authProviderStrategy.getCurrentUserID()
    fun getCurrentUserEmail() = authProviderStrategy.getCurrentUserEmail()
    fun getCurrentUserName() = authProviderStrategy.getCurrentUserName()
    fun getCurrentUserPhotoUrl() = authProviderStrategy.getCurrentUserPhotoUrl()
    fun getCurrentUserDisplayName() = authProviderStrategy.getCurrentUserDisplayName()
    fun getCurrentUserPhoneNumber() = authProviderStrategy.getCurrentUserPhoneNumber()
}
