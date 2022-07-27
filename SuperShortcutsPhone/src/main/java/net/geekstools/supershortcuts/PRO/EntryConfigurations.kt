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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug
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

            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.webClientId))
                    .requestEmail()
                    .build()

            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            googleSignInResult.launch(googleSignInClient.signInIntent)

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

    override fun onPause() {
        super.onPause()

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.reload()?.addOnCompleteListener {
            firebaseAuth.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user == null) {
                    functionsClass.savePreference(".UserInformation", "userEmail", null)

                    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.webClientId))
                            .requestEmail()
                            .build()

                    val googleSignInClient = GoogleSignIn.getClient(this@EntryConfigurations, googleSignInOptions)
                    try {
                        googleSignInClient.signOut()
                        googleSignInClient.revokeAccess()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {

                }
            }
        }
    }

    override fun onBackPressed() {

    }

    private val googleSignInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == Activity.RESULT_OK) {

            val googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(it.data)
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

                    waitingDialogueLiveData.run {
                        this.dialogueTitle.value = getString(R.string.error)
                        this.dialogueMessage.value = exception.message
                    }
                }

        } else {

            waitingDialogueLiveData.run {
                this.dialogueTitle.value = getString(R.string.error)
                this.dialogueMessage.value = Activity.RESULT_CANCELED.toString()
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
            FunctionsClassDebug.PrintDebug("CustomIconPackages ::: " + resolveInfo.activityInfo.packageName)

            PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName)
        }
    }
}