/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 1:53 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts

import android.app.ActivityOptions
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SavedAppsListPopupAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SelectionListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions.*
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButton
import net.geekstools.supershortcuts.PRO.BuildConfig
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts
import net.geekstools.supershortcuts.PRO.MixShortcuts.MixShortcutsProcess
import net.geekstools.supershortcuts.PRO.Preferences.PreferencesUI
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint
import net.geekstools.supershortcuts.PRO.Utils.InAppUpdate.InAppUpdateProcess
import net.geekstools.supershortcuts.PRO.Utils.RemoteProcess.LicenseValidator
import net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.SwipeGestureListener
import net.geekstools.supershortcuts.PRO.databinding.NormalAppSelectionBinding
import java.lang.String
import java.util.*

class NormalAppShortcutsSelectionList : AppCompatActivity(),
        GestureListenerInterface,
        ConfirmButtonProcessInterface {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val functionsClassDialogues: FunctionsClassDialogues by lazy {
        FunctionsClassDialogues(this@NormalAppShortcutsSelectionList, functionsClass)
    }

    private val listPopupWindow: ListPopupWindow by lazy {
        ListPopupWindow(applicationContext)
    }

    lateinit var recyclerViewLayoutManager: LinearLayoutManager
    lateinit var appSelectionListAdapter: RecyclerView.Adapter<SelectionListAdapter.ViewHolder>
    val installedAppsListItem: ArrayList<AdapterItemsData> = ArrayList<AdapterItemsData>()

    private val selectedAppsListItem: ArrayList<AdapterItemsData> = ArrayList<AdapterItemsData>()

    var appsConfirmButton: AppsConfirmButton? = null

    var appShortcutLimitCounter = 0

    var resetAdapter: Boolean = false
    var updateAvailable: Boolean = false

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@NormalAppShortcutsSelectionList)
    }

    companion object {
        const val NormalApplicationsShortcutsFile = ".autoSuper"
    }

    lateinit var normalAppSelectionBinding: NormalAppSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        normalAppSelectionBinding = NormalAppSelectionBinding.inflate(layoutInflater)
        setContentView(normalAppSelectionBinding.root)

        /* Check Shortcuts Information */
        evaluateShortcutsInfo()
        /* Check Shortcuts Information */

        appsConfirmButton = setupConfirmButtonUI(this@NormalAppShortcutsSelectionList)

        /* Setup UI*/
        setupUI()
        /* Setup UI*/

        initializeLoadingProcess()

        functionsClassDialogues.changeLog()

        //In-App Billing
        PurchasesCheckpoint(this@NormalAppShortcutsSelectionList).trigger()
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
                        functionsClass.dialogueLicense(this@NormalAppShortcutsSelectionList)

                        Handler().postDelayed({
                            stopService(Intent(applicationContext, LicenseValidator::class.java))
                        }, 1000)

                        unregisterReceiver(this)
                    }
                }
            }
            registerReceiver(broadcastReceiver, intentFilter)
        }

        normalAppSelectionBinding.autoCategories.setOnClickListener {

            functionsClass.overrideBackPress(this@NormalAppShortcutsSelectionList, FolderShortcuts::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
        }

        normalAppSelectionBinding.autoSplit.setOnClickListener {

            functionsClass.overrideBackPress(this@NormalAppShortcutsSelectionList, SplitShortcuts::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
        }

        normalAppSelectionBinding.preferencesView.setOnClickListener {

            if (updateAvailable) {

                FunctionsClassDialogues(this@NormalAppShortcutsSelectionList, functionsClass).changeLogPreference(
                        firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()),
                        String.valueOf(firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()))
                )

            } else {

                startActivity(Intent(applicationContext, PreferencesUI::class.java),
                        ActivityOptions.makeCustomAnimation(applicationContext, R.anim.up_down, android.R.anim.fade_out).toBundle())

                this@NormalAppShortcutsSelectionList.finish()
            }
        }

        MixShortcutsProcess(applicationContext, normalAppSelectionBinding.mixShortcutsSwitchView).initialize()
    }

    override fun onResume() {
        super.onResume()

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnSuccessListener {

                    firebaseRemoteConfig.activate().addOnSuccessListener {

                        if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > BuildConfig.VERSION_CODE) {

                            val layerDrawableNewUpdate = getDrawable(R.drawable.ic_update) as LayerDrawable?
                            val gradientDrawableNewUpdate = layerDrawableNewUpdate!!.findDrawableByLayerId(R.id.temporaryBackground) as BitmapDrawable
                            gradientDrawableNewUpdate.setTint(getColor(R.color.default_color_game))

                            val temporaryBitmap = functionsClass.drawableToBitmap(layerDrawableNewUpdate)
                            val scaleBitmap = Bitmap.createScaledBitmap(temporaryBitmap, temporaryBitmap.width / 4, temporaryBitmap.height / 4, false)
                            val logoDrawable: Drawable = BitmapDrawable(resources, scaleBitmap)

                            normalAppSelectionBinding.preferencesView.setImageDrawable(logoDrawable)

                            functionsClass.notificationCreator(
                                    getString(R.string.updateAvailable),
                                    firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                    firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) as Int
                            )

                            val inAppUpdateTriggeredTime =
                                    (Calendar.getInstance()[Calendar.YEAR].toString() + Calendar.getInstance()[Calendar.MONTH].toString() + Calendar.getInstance()[Calendar.DATE].toString())
                                            .toInt()

                            if (FirebaseAuth.getInstance().currentUser != null
                                    && functionsClass.readPreference("InAppUpdate", "TriggeredDate", 0) < inAppUpdateTriggeredTime) {

                                startActivity(Intent(applicationContext, InAppUpdateProcess::class.java)
                                        .putExtra("UPDATE_CHANGE_LOG", firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()))
                                        .putExtra("UPDATE_VERSION", firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()).toString())
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                        ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                            }

                            updateAvailable = true
                        }
                    }
                }
    }

    override fun onPause() {
        super.onPause()

        getSharedPreferences("ShortcutsModeView", Context.MODE_PRIVATE).edit().apply {
            putString("TabsView", NormalAppShortcutsSelectionList::class.java.simpleName)
            apply()
        }
    }

    override fun onBackPressed() {
        if (functionsClass.UsageAccessEnabled()) {
            this@NormalAppShortcutsSelectionList.finish()
        } else {
            val homeScreen = Intent(Intent.ACTION_MAIN).apply {
                this.addCategory(Intent.CATEGORY_HOME)
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(homeScreen,
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)

        when (gestureConstants) {
            is GestureConstants.SwipeHorizontal -> {
                when (gestureConstants.horizontalDirection) {
                    GestureListenerConstants.SWIPE_RIGHT -> {

                    }
                    GestureListenerConstants.SWIPE_LEFT -> {
                        functionsClass.navigateToClass(this@NormalAppShortcutsSelectionList, SplitShortcuts::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        swipeGestureListener.onTouchEvent(motionEvent)

        return super.dispatchTouchEvent(motionEvent)
    }

    /*ConfirmButtonProcess*/
    override fun savedShortcutCounter() {

        normalAppSelectionBinding.selectedShortcutCounterView.text = functionsClass.countLineInnerFile(NormalAppShortcutsSelectionList.NormalApplicationsShortcutsFile).toString()
    }

    override fun showSavedShortcutList() {

        if (getFileStreamPath(NormalAppShortcutsSelectionList.NormalApplicationsShortcutsFile).exists()
                && functionsClass.countLineInnerFile(NormalAppShortcutsSelectionList.NormalApplicationsShortcutsFile) > 0) {

            selectedAppsListItem.clear()

            val savedLine = functionsClass.readFileLine(NormalAppShortcutsSelectionList.NormalApplicationsShortcutsFile)
            for (aSavedLine in savedLine) {

                val aLineSplit = aSavedLine.split("|")

                val packageName = aLineSplit[0]
                val className = aLineSplit[1]

                val activityInfo = packageManager.getActivityInfo(ComponentName(packageName, className), 0)

                selectedAppsListItem.add(AdapterItemsData(
                        functionsClass.activityLabel(activityInfo),
                        packageName,
                        className,
                        if (functionsClass.customIconsEnable()) {
                            loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.activityIcon(activityInfo))
                        } else {
                            functionsClass.activityIcon(activityInfo)
                        }
                ))
            }

            val savedAppsListPopupAdapter = SavedAppsListPopupAdapter(
                    applicationContext,
                    functionsClass,
                    selectedAppsListItem,
                    this@NormalAppShortcutsSelectionList
            )

            listPopupWindow.apply {
                anchorView = normalAppSelectionBinding.confirmLayout
                width = functionsClass.DpToInteger(300)
                height = ListPopupWindow.WRAP_CONTENT
                promptPosition = ListPopupWindow.POSITION_PROMPT_ABOVE
                isModal = true
                setDropDownGravity(Gravity.CENTER)
                setBackgroundDrawable(null)
            }

            listPopupWindow.setOnDismissListener {

                appsConfirmButton?.makeItVisible()
            }

            listPopupWindow.setAdapter(savedAppsListPopupAdapter)
            listPopupWindow.show()
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

    override fun reevaluateShortcutsInfo() {

        evaluateShortcutsInfo()
    }
    /*ConfirmButtonProcess*/

    private fun initializeLoadingProcess() {

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons.load()
        }

        if (functionsClass.UsageAccessEnabled()) {

            smartPickProcess()

        } else {

            if (!functionsClass.mixShortcuts()) {

                if (applicationContext.getFileStreamPath(".mixShortcuts").exists()) {
                    val mixShortcutsContent = functionsClass.readFileLine(".mixShortcuts")

                    for (mixShortcutLine in mixShortcutsContent) {
                        when {
                            mixShortcutLine.contains(".CategorySelected") -> {
                                applicationContext.deleteFile(functionsClass.categoryNameSelected(mixShortcutLine))
                            }
                            mixShortcutLine.contains(".SplitSelected") -> {
                                applicationContext.deleteFile(functionsClass.splitNameSelected(mixShortcutLine))
                            }
                            else -> {
                                applicationContext.deleteFile(functionsClass.packageNameSelected(mixShortcutLine))
                            }
                        }

                    }

                    applicationContext.deleteFile(".mixShortcuts")
                }
            }

            if (applicationContext.getFileStreamPath(".superFreq").exists()) {
                applicationContext.deleteFile(".superFreq")
            }

            /* Load Installed Applications */
            loadInstalledAppsData()
            /* Load Installed Applications */
        }
    }
}