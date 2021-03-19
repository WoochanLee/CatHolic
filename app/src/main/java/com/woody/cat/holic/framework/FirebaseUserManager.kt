package com.woody.cat.holic.framework

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.base.CatHolicLogger

object FirebaseUserManager {

    private const val USER_PROVIDER = "firebase"

    private lateinit var gso: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth

    fun init(context: Context) {
        firebaseAuth = FirebaseAuth.getInstance()
        gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .build()
    }

    fun startGoogleSignInForResult(
        fragment: Fragment,
        onSuccess: (AuthResult) -> Unit,
        onError: (Exception) -> Unit
    ): ActivityResultLauncher<Intent> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            handleGoogleSignInResult(task, onSuccess, onError)
        }
    }

    fun signIn(
        activityResultLauncher: ActivityResultLauncher<Intent>,
        activity: Activity
    ) {

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        activityResultLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun handleGoogleSignInResult(
        task: Task<GoogleSignInAccount>,
        onSuccess: (AuthResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    CatHolicLogger.log("success to firebase google sign in")
                    onSuccess(it)
                }
                .addOnFailureListener {
                    CatHolicLogger.log("fail to firebase google sign in")
                    onError(it)
                }
        } catch (e: ApiException) {
            CatHolicLogger.log("fail to firebase google sign in")
            onError(e)
        }
    }

    fun isSignedIn(): Boolean {
        return getCurrentUserId() != null
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.providerData?.find { it.providerId == USER_PROVIDER }?.uid
    }

    fun signOut(activity: Activity) {
        GoogleSignIn.getClient(activity, gso).signOut()
        firebaseAuth.signOut()
    }
}