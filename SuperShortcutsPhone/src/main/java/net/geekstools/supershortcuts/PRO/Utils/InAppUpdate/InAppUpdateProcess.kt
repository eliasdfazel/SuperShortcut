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

import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import net.geekstools.supershortcuts.PRO.R

class InAppUpdateProcess (private val context: AppCompatActivity, private val anchorView: View) {

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

            }

        }.addOnFailureListener {

        }

        appUpdateManager.unregisterListener {
        }
    }

    fun onResume() {

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {

                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    context,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).setAppUpdateType(AppUpdateType.FLEXIBLE).build(),
                    IN_APP_UPDATE_REQUEST
                )

            }

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteConfirmation(anchorView)
            }
        }
    }

    private fun showCompleteConfirmation(anchorView: View) {

        val snackBar = Snackbar.make(anchorView,
                context.getString(R.string.inAppUpdateDescription),
                Snackbar.LENGTH_INDEFINITE)
        snackBar.setBackgroundTint(context.getColor(R.color.dark))
        snackBar.setTextColor(context.getColor(R.color.light))
        snackBar.setActionTextColor(context.getColor(R.color.default_color_light))
        snackBar.setAction(Html.fromHtml(context.getString(R.string.inAppUpdateAction), Html.FROM_HTML_MODE_COMPACT)) { view ->
            appUpdateManager.completeUpdate().addOnSuccessListener {

            }.addOnFailureListener {

            }
        }

        val view = snackBar.view
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.BOTTOM
        view.layoutParams = layoutParams

        snackBar.show()
    }
}