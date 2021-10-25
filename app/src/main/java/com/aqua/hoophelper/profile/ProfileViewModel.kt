package com.aqua.hoophelper.profile

import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

const val RC_SIGN_IN = 0

class ProfileViewModel : ViewModel() {

    fun signIn(googleSignInClient: GoogleSignInClient, activity: FragmentActivity) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(activity, signInIntent, RC_SIGN_IN, null)
    }



}