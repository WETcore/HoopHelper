package com.aqua.hoophelper

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class LoginActivityViewModel: ViewModel() {

    fun signIn(googleSignInClient: GoogleSignInClient, activity: FragmentActivity) {
        val signInIntent = googleSignInClient.signInIntent
        ActivityCompat.startActivityForResult(activity, signInIntent, RC_SIGN_IN, null)
    }

    fun getStatus(context: Context, intent: Intent) {
        Log.d("login","${User.account}")
        if(User.account != null) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            context.startActivity(intent)
        }
    }

}