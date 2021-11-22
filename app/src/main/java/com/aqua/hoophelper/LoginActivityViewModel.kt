package com.aqua.hoophelper

import android.animation.ObjectAnimator
import android.graphics.Path
import android.util.DisplayMetrics
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginActivityViewModel: ViewModel() {

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun signIn(googleSignInClient: GoogleSignInClient, activity: FragmentActivity) {
        val signInIntent = googleSignInClient.signInIntent
        ActivityCompat.startActivityForResult(activity, signInIntent, RC_SIGN_IN, null)
    }

    fun getUserInfo() {
        coroutineScope.launch {
            HoopRemoteDataSource.getUserInfo()
            HoopRemoteDataSource.getMatchMembers()
        }
    }
}