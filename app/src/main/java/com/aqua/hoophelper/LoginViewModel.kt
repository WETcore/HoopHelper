package com.aqua.hoophelper

import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

const val RC_SIGN_IN = 0

class LoginViewModel : ViewModel() {

    fun signIn(googleSignInClient: GoogleSignInClient, activity: FragmentActivity) {
        val signInIntent = googleSignInClient.signInIntent
        ActivityCompat.startActivityForResult(activity, signInIntent, RC_SIGN_IN, null)
    }
}