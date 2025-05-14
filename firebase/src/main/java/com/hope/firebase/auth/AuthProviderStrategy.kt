package com.hope.firebase.auth

import android.content.Intent

interface AuthProviderStrategy {
    fun startLogin()
    fun handleLoginResult(requestCode: Int, data: Intent?)
    fun signOut(onSignOut: () -> Unit = {})
    fun getCurrentUserID(): String?
    fun getCurrentUserEmail(): String?
    fun getCurrentUserName(): String?
    fun getCurrentUserPhotoUrl(): String?
    fun getCurrentUserDisplayName(): String?
    fun getCurrentUserPhoneNumber(): String?


}
