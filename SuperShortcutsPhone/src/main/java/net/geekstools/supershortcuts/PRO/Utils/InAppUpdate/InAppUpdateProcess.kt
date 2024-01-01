/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/20/20 8:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppUpdate

import android.app.Activity
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import java.util.Calendar

class InAppUpdateProcess (private val context: AppCompatActivity, private val anchorView: View) {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(context)
    }

    private var appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    private var installStateUpdatedListener: InstallStateUpdatedListener

    companion object {
        private const val IN_APP_UPDATE_REQUEST = 333
    }

    init {

        installStateUpdatedListener = InstallStateUpdatedListener {
            when (it.installStatus()) {
                InstallStatus.DOWNLOADING -> {
                }
                InstallStatus.DOWNLOADED -> {
                    showCompleteConfirmation(anchorView)
                }
                InstallStatus.INSTALLING -> {

                }
                InstallStatus.INSTALLED -> {

                }
                InstallStatus.CANCELED -> {
                }
                InstallStatus.FAILED -> {

                    val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                    functionsClass.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)

                }
                InstallStatus.PENDING -> {
                }
                InstallStatus.UNKNOWN -> {
                }
            }
        }

        appUpdateManager.registerListener(installStateUpdatedListener)

    }

    fun onCreate() {

        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->

            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                appUpdateManager.startUpdateFlowForResult(
                    updateInfo,
                    context,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).setAppUpdateType(AppUpdateType.FLEXIBLE).build(),
                    IN_APP_UPDATE_REQUEST
                )

            } else {
                val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                functionsClass.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)
            }

        }.addOnFailureListener {

            val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
            functionsClass.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)
        }

        appUpdateManager.unregisterListener {
        }
    }

    fun onResume() {

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {

                val updateIntentSend = IntentSenderForResultStarter { intentSender, i, intent, flagsValues, flagsMask, i4, bundle ->

                    val request = IntentSenderRequest.Builder(intentSender)
                        .setFillInIntent(intent)
                        .setFlags(flagsValues, flagsMask)
                        .build()

                    applicationUpdateResult.launch(request)
                }

                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    updateIntentSend,
                    IN_APP_UPDATE_REQUEST
                )

            }

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteConfirmation(anchorView)
            }
        }
    }

    private val applicationUpdateResult = context.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {

        when (it.resultCode) {
            Activity.RESULT_CANCELED -> {

                val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                functionsClass.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)

                appUpdateManager.unregisterListener(installStateUpdatedListener)

            }
            Activity.RESULT_OK -> {

            }
            ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {

            }
        }

    }

    private fun showCompleteConfirmation(anchorView: View) {

        val snackbar = Snackbar.make(anchorView,
                context.getString(R.string.inAppUpdateDescription),
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setBackgroundTint(context.getColor(R.color.dark))
        snackbar.setTextColor(context.getColor(R.color.light))
        snackbar.setActionTextColor(context.getColor(R.color.default_color_light))
        snackbar.setAction(Html.fromHtml(context.getString(R.string.inAppUpdateAction), Html.FROM_HTML_MODE_COMPACT)) { view ->
            appUpdateManager.completeUpdate().addOnSuccessListener {

            }.addOnFailureListener {

                val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                functionsClass.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)
            }
        }

        val view = snackbar.view
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.BOTTOM
        view.layoutParams = layoutParams

        snackbar.show()
    }
}