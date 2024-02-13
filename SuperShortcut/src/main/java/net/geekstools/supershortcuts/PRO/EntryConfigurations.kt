/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/7/20 10:16 AM
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
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupDialogue.WaitingDialogue
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupDialogue.WaitingDialogueLiveData

class EntryConfigurations : AppCompatActivity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    val securityServicesProcess: SecurityServicesProcess by lazy {
        SecurityServicesProcess(this@EntryConfigurations)
    }

    private lateinit var waitingDialogue: Dialog

    private lateinit var waitingDialogueLiveData: WaitingDialogueLiveData

    private val googleSignInClient: SignInClient by lazy {
        Identity.getSignInClient(this@EntryConfigurations)
    }

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

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

        if (functionsClass.networkConnection()
                && functionsClass.readPreference(".UserInformation", "userEmail", null) == null
                && firebaseAuth.currentUser == null) {

            val googleIdTokenRequestOptions = BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(getString(R.string.webClientId))
                .setFilterByAuthorizedAccounts(false)
                .build()

            val beginSignInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(googleIdTokenRequestOptions)
                .setAutoSelectEnabled(true)
                .build()

            googleSignInClient.beginSignIn(beginSignInRequest)
                .addOnSuccessListener(this@EntryConfigurations) { result ->

                    try {

                        googleSignInResult.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())

                    } catch (e: Exception) {
                        e.printStackTrace()

                    }

                }.addOnFailureListener(this@EntryConfigurations) { e ->
                    e.printStackTrace()
                }

            waitingDialogueLiveData = ViewModelProvider(this@EntryConfigurations).get(WaitingDialogueLiveData::class.java)
            waitingDialogueLiveData.run {

                this.dialogueTitle.value = getString(R.string.signinTitle)
                this.dialogueMessage.value = getString(R.string.signinMessage)

                waitingDialogue = WaitingDialogue().initShow(this@EntryConfigurations)
                waitingDialogue.setOnDismissListener {

                    waitingDialogue.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                    shortcutModeEntryPoint()

                }

            }

        } else {

            CoroutineScope(Dispatchers.IO).launch {
                loadInstalledCustomIconPackages().await()
            }

            shortcutModeEntryPoint()
        }

    }

    private val googleSignInResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {

        when (it.resultCode) {
            Activity.RESULT_OK -> {

                val googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                val googleSignInAccount = googleSignInAccountTask.getResult(ApiException::class.java)

                val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount?.idToken, null)
                firebaseAuth.signInWithCredential(authCredential)
                    .addOnSuccessListener {

                        val firebaseUser = firebaseAuth.currentUser

                        if (firebaseUser != null) {

                            functionsClass.savePreference(".UserInformation", "userEmail", firebaseUser.email)

                            functionsClass.Toast(getString(R.string.signinFinished), Gravity.TOP)

                            shortcutModeEntryPoint()

                            waitingDialogue.dismiss()

                        }

                    }.addOnFailureListener { exception ->

                        waitingDialogueLiveData.run {
                            this.dialogueTitle.value = getString(R.string.error)
                            this.dialogueMessage.value = "Sign In Failed"
                        }

                    }

            }
            else -> {

                waitingDialogueLiveData.run {
                    this.dialogueTitle.value = getString(R.string.error)
                    this.dialogueMessage.value = "Google Account Result"
                }

            }
        }

    }

    private fun loadInstalledCustomIconPackages() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
        val packageManager = applicationContext.packageManager
        //ACTION: com.novalauncher.THEME
        //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
        val intentCustomIcons = Intent()
        intentCustomIcons.action = "com.novalauncher.THEME"
        intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER")
        val resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0)

        PublicVariable.customIconsPackages.clear()
        for (resolveInfo in resolveInfos) {
            PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName)
        }
    }
}