/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 7:18 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupDialogue.WaitingDialogue
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupDialogue.WaitingDialogueLiveData

class EntryConfigurations : AppCompatActivity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private lateinit var waitingDialogue: Dialog

    lateinit var firebaseAuth: FirebaseAuth

    private object Google {
        const val SignInRequest: Int = 666
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        FirebaseApp.initializeApp(applicationContext)

        functionsClass.savePreference(".UserInformation", "isBetaTester", functionsClass.appVersionName(packageName).contains("[BETA]"))
        functionsClass.savePreference(".UserInformation", "installedVersionCode", functionsClass.appVersionCode(packageName))
        functionsClass.savePreference(".UserInformation", "installedVersionName", functionsClass.appVersionName(packageName))
        functionsClass.savePreference(".UserInformation", "deviceModel", functionsClass.deviceName)
        functionsClass.savePreference(".UserInformation", "userRegion", functionsClass.countryIso)
        if (functionsClass.appVersionName(packageName).contains("[BETA]")) {
            functionsClass.saveDefaultPreference("JoinedBetaProgrammer", true)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        if (functionsClass.networkConnection()
                && functionsClass.readPreference(".UserInformation", "userEmail", null) == null
                && firebaseAuth.currentUser == null) {

            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.webClientId))
                    .requestEmail()
                    .build()

            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            googleSignInClient.signInIntent.run {
                startActivityForResult(this, Google.SignInRequest)
            }

            ViewModelProvider(this@EntryConfigurations).get(WaitingDialogueLiveData::class.java).run {
                this.dialogueTitle.value = getString(R.string.signinTitle)
                this.dialogueMessage.value = getString(R.string.signinMessage)

                waitingDialogue = WaitingDialogue().initShow(this@EntryConfigurations)
                waitingDialogue.setOnDismissListener {
                    waitingDialogue.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                    shortcutModeEntryPoint()
                }
            }
        } else {

            shortcutModeEntryPoint()
        }

    }

    override fun onBackPressed() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Google.SignInRequest -> {
                    val googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val googleSignInAccount = googleSignInAccountTask.getResult(ApiException::class.java)

                    val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount?.idToken, null)
                    firebaseAuth.signInWithCredential(authCredential)
                            .addOnSuccessListener {
                                val firebaseUser = firebaseAuth.currentUser
                                if (firebaseUser != null) {
                                    FunctionsClassDebug.PrintDebug("Firebase Activities Done Successfully")

                                    functionsClass.savePreference(".UserInformation", "userEmail", firebaseUser.email)

                                    functionsClass.Toast(getString(R.string.signinFinished), Gravity.TOP)

                                    shortcutModeEntryPoint()

                                    waitingDialogue.dismiss()
                                }
                            }.addOnFailureListener { exception ->

                                ViewModelProvider(this@EntryConfigurations).get(WaitingDialogueLiveData::class.java).run {
                                    this.dialogueTitle.value = getString(R.string.error)
                                    this.dialogueMessage.value = exception.message
                                }
                            }
                }
            }
        } else {
            ViewModelProvider(this@EntryConfigurations).get(WaitingDialogueLiveData::class.java).run {
                this.dialogueTitle.value = getString(R.string.error)
                this.dialogueMessage.value = Activity.RESULT_CANCELED.toString()
            }
        }
    }
}