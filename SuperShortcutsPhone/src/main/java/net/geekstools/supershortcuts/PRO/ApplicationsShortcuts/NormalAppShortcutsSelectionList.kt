/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/1/20 12:15 PM
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
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SavedAppsListPopupAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SelectionListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions.*
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButton
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.AdvanceShortcuts
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint
import net.geekstools.supershortcuts.PRO.Utils.RemoteProcess.LicenseValidator
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.SwipeGestureListener
import net.geekstools.supershortcuts.PRO.databinding.NormalAppSelectionBinding
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

            functionsClass.overrideBackPress(this@NormalAppShortcutsSelectionList, AdvanceShortcuts::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
        }

        normalAppSelectionBinding.autoSplit.setOnClickListener {

            functionsClass.overrideBackPress(this@NormalAppShortcutsSelectionList, SplitShortcuts::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
        }


    }

    override fun onResume() {
        super.onResume()

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnSuccessListener {

                    firebaseRemoteConfig.activate().addOnSuccessListener {

                        if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(packageName)) {

                            val layerDrawableNewUpdate = getDrawable(R.drawable.ic_update) as LayerDrawable?
                            val gradientDrawableNewUpdate = layerDrawableNewUpdate!!.findDrawableByLayerId(R.id.temporaryBackground) as BitmapDrawable
                            gradientDrawableNewUpdate.setTint(getColor(R.color.default_color_game))

                            val temporaryBitmap = functionsClass.drawableToBitmap(layerDrawableNewUpdate)
                            val scaleBitmap = Bitmap.createScaledBitmap(temporaryBitmap, temporaryBitmap.width / 4, temporaryBitmap.height / 4, false)
                            val logoDrawable: Drawable = BitmapDrawable(resources, scaleBitmap)

                            //Edit UI Here

                            functionsClass.notificationCreator(
                                    getString(R.string.updateAvailable),
                                    firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                    firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) as Int
                            )
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

        normalAppSelectionBinding.appSelectedCounterView.text = functionsClass.countLineInnerFile(NormalAppShortcutsSelectionList.NormalApplicationsShortcutsFile).toString()
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

            listPopupWindow.setAdapter(savedAppsListPopupAdapter)
            listPopupWindow.anchorView = normalAppSelectionBinding.popupAnchorView
            listPopupWindow.width = ListPopupWindow.WRAP_CONTENT
            listPopupWindow.height = ListPopupWindow.WRAP_CONTENT
            listPopupWindow.isModal = true
            listPopupWindow.setBackgroundDrawable(null)
            listPopupWindow.setOnDismissListener {

                appsConfirmButton?.makeItVisible()
            }
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