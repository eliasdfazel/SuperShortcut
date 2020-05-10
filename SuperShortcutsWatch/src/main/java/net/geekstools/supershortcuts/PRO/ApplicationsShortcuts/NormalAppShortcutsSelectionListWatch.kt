/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 3:57 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.wearable.activity.WearableActivity
import android.widget.ListPopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.CurveWearLayoutManager
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SavedListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SelectionListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions.loadInstalledAppsData
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButtonWatch
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.RemoteTask.LicenseValidator
import net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.databinding.ApplicationsSelectionListViewBinding
import java.util.*


class NormalAppShortcutsSelectionListWatch : WearableActivity(),
        ConfirmButtonProcessInterface {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    lateinit var selectionListAdapter: RecyclerView.Adapter<SelectionListAdapter.ViewHolder>

    private val listPopupWindow: ListPopupWindow by lazy {
        ListPopupWindow(this@NormalAppShortcutsSelectionListWatch)
    }

    val installedApplicationsItemsData: ArrayList<AdapterItemsData> = ArrayList<AdapterItemsData>()
    private val selectedApplicationsDataItem: ArrayList<AdapterItemsData> = ArrayList<AdapterItemsData>()

    var appsConfirmButtonWatch: AppsConfirmButtonWatch? = null

    var resetAdapter = false

    companion object {
        const val NormalApplicationsShortcutsFile = ".autoSuper"
    }

    lateinit var applicationsSelectionListViewBinding: ApplicationsSelectionListViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationsSelectionListViewBinding = ApplicationsSelectionListViewBinding.inflate(layoutInflater)
        setContentView(applicationsSelectionListViewBinding.root)

        applicationsSelectionListViewBinding.confirmLayout.bringToFront()

        val confirmButtonLayoutParams = RelativeLayout.LayoutParams(
                functionsClass.DpToInteger(73),
                functionsClass.DpToInteger(73)
        )

        appsConfirmButtonWatch = AppsConfirmButtonWatch(this@NormalAppShortcutsSelectionListWatch, applicationContext,
                functionsClass,
                this@NormalAppShortcutsSelectionListWatch)

        appsConfirmButtonWatch!!.layoutParams = confirmButtonLayoutParams
        appsConfirmButtonWatch!!.bringToFront()

        applicationsSelectionListViewBinding.confirmLayout.addView(appsConfirmButtonWatch)
        appsConfirmButtonWatch!!.setOnClickListener {

        }

        applicationsSelectionListViewBinding.recyclerViewApplicationList.layoutManager = WearableLinearLayoutManager(applicationContext, CurveWearLayoutManager())
        applicationsSelectionListViewBinding.recyclerViewApplicationList.isEdgeItemsCenteringEnabled = true
        applicationsSelectionListViewBinding.recyclerViewApplicationList.apply {
            this.isCircularScrollingGestureEnabled = true
            this.bezelFraction = 0.10f
            this.scrollDegreesPerScreen = 90f
        }

        val typeface = Typeface.createFromAsset(assets, "upcil.ttf")
        applicationsSelectionListViewBinding.counterView.typeface = typeface
        applicationsSelectionListViewBinding.counterView.bringToFront()

        applicationsSelectionListViewBinding.loadingProgress.indeterminateTintList = ColorStateList.valueOf(getColor(R.color.default_color))

        initializeLoadingProcess()

        setAmbientEnabled()
    }

    override fun onStart() {
        super.onStart()

        if (!getFileStreamPath(".License").exists() && functionsClass.networkConnection()) {
            startService(Intent(applicationContext, LicenseValidator::class.java))

            val intentFilter = IntentFilter()
            intentFilter.addAction(getString(R.string.license))
            val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == getString(R.string.license)) {
                        functionsClass.dialogueLicense(this@NormalAppShortcutsSelectionListWatch)

                        Handler().postDelayed({
                            stopService(Intent(applicationContext, LicenseValidator::class.java))
                        }, 1000)

                        unregisterReceiver(this)
                    }
                }
            }
            registerReceiver(broadcastReceiver, intentFilter)
        }
    }

    override fun onResume() {
        super.onResume()

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(0)
                .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {

                            if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(packageName)) {

                                Toast.makeText(applicationContext,
                                        getString(R.string.updateAvailable), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
    }


    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
    }


    /*ConfirmButtonProcess*/
    override fun savedShortcutCounter() {

        applicationsSelectionListViewBinding.counterView.text = functionsClass.countLine(NormalAppShortcutsSelectionListWatch.NormalApplicationsShortcutsFile).toString()
    }

    override fun showSavedShortcutList() {

        if (functionsClass.countLine(NormalAppShortcutsSelectionListWatch.NormalApplicationsShortcutsFile) > 0) {

            selectedApplicationsDataItem.clear()

            val savedLine = functionsClass.readFileLine(NormalAppShortcutsSelectionListWatch.NormalApplicationsShortcutsFile)

            for (aSavedLine in savedLine) {

                selectedApplicationsDataItem.add(AdapterItemsData(
                        functionsClass.appName(aSavedLine),
                        aSavedLine,
                        functionsClass.appIconDrawable(aSavedLine)))
            }

            val savedListAdapter = SavedListAdapter(applicationContext, selectedApplicationsDataItem)

            listPopupWindow.setAdapter(savedListAdapter)
            listPopupWindow.anchorView = applicationsSelectionListViewBinding.popupAnchorView
            listPopupWindow.width = ListPopupWindow.WRAP_CONTENT
            listPopupWindow.height = ListPopupWindow.WRAP_CONTENT
            listPopupWindow.isModal = true
            listPopupWindow.setBackgroundDrawable(null)
            listPopupWindow.show()

            listPopupWindow.setOnDismissListener {

                appsConfirmButtonWatch?.makeItVisible()
            }
        }
    }

    override fun hideSavedShortcutList() {

        listPopupWindow.dismiss()
    }

    override fun shortcutDeleted() {

        resetAdapter = true

        loadInstalledAppsData()

        listPopupWindow.dismiss()
    }
    /*ConfirmButtonProcess*/

    private fun initializeLoadingProcess() {
        installedApplicationsItemsData.clear()

        loadInstalledAppsData()
    }
}