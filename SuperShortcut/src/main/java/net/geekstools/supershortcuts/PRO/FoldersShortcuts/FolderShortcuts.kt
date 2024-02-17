/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/22/21 10:06 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Adapters.FolderShortcutsAdapter
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Extensions.evaluateShortcutsInfo
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Extensions.loadCreatedFoldersData
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Extensions.setupUI
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Extensions.smartPickProcess
import net.geekstools.supershortcuts.PRO.MixShortcuts.MixShortcutsProcess
import net.geekstools.supershortcuts.PRO.Preferences.PreferencesUI
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint
import net.geekstools.supershortcuts.PRO.Utils.InAppUpdate.InAppUpdateProcess
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.SwipeGestureListener
import net.geekstools.supershortcuts.PRO.databinding.FolderShortcutsViewBinding

class FolderShortcuts : AppCompatActivity(),
        GestureListenerInterface {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val functionsClassDialogues: FunctionsClassDialogues by lazy {
        FunctionsClassDialogues(this@FolderShortcuts, functionsClass)
    }

    lateinit var recyclerViewLayoutManager: LinearLayoutManager
    lateinit var folderSelectionListAdapter: RecyclerView.Adapter<FolderShortcutsAdapter.ViewHolder>
    val createdFolderListItem: ArrayList<AdapterItemsData> = ArrayList<AdapterItemsData>()

    var appShortcutLimitCounter = 0

    var resetAdapter: Boolean = false
    var updateAvailable: Boolean = false

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@FolderShortcuts)
    }

    val inAppUpdateProcess: InAppUpdateProcess by lazy {
        InAppUpdateProcess(this@FolderShortcuts, folderShortcutsViewBinding.root)
    }

    companion object {
        const val FolderShortcutsFile = ".categorySuper"
        const val FolderShortcutsSelectedFile = ".categorySuperSelected"
    }

    lateinit var folderShortcutsViewBinding: FolderShortcutsViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folderShortcutsViewBinding = FolderShortcutsViewBinding.inflate(layoutInflater)
        setContentView(folderShortcutsViewBinding.root)

        SecurityServicesProcess(this@FolderShortcuts).switchSecurityServices(folderShortcutsViewBinding.securityServicesSwitchView)

        /* Check Shortcuts Information */
        evaluateShortcutsInfo()
        /* Check Shortcuts Information */

        /* Setup UI*/
        setupUI()
        /* Setup UI*/

        initializeLoadingProcess()

        functionsClassDialogues.changeLog(false)

        //In-App Billing
        PurchasesCheckpoint(this@FolderShortcuts).trigger()

        inAppUpdateProcess.onCreate()

    }

    override fun onStart() {
        super.onStart()

        folderShortcutsViewBinding.confirmButton.setOnClickListener {
            if (functionsClass.mixShortcuts()) {
                functionsClass.addMixAppShortcuts()
            } else {
                functionsClass.addAppsShortcutCategory()

                getSharedPreferences(".PopupShortcut", Context.MODE_PRIVATE).edit().apply {
                    putString("PopupShortcutMode", "CategoryShortcuts")
                    apply()
                }
            }
        }

        folderShortcutsViewBinding.confirmButton.setOnLongClickListener {

            functionsClass.deleteSelectedFiles()

            shortcutDeleted()

            savedShortcutCounter()

            reevaluateShortcutsInfo()

            functionsClass.clearDynamicShortcuts()

            true
        }

        folderShortcutsViewBinding.autoApps.setOnClickListener {

            functionsClass.overrideBackPress(this@FolderShortcuts, NormalAppShortcutsSelectionListPhone::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
        }

        folderShortcutsViewBinding.autoSplit.setOnClickListener {

            functionsClass.overrideBackPress(this@FolderShortcuts, SplitShortcuts::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
        }

        folderShortcutsViewBinding.preferencesView.setOnClickListener {

            startActivity(Intent(applicationContext, PreferencesUI::class.java),
                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.up_down, android.R.anim.fade_out).toBundle())

        }

        MixShortcutsProcess(applicationContext, folderShortcutsViewBinding.mixShortcutsSwitchView).initialize()
    }

    override fun onResume() {
        super.onResume()

        inAppUpdateProcess.onResume()

    }

    override fun onPause() {
        super.onPause()

        getSharedPreferences("ShortcutsModeView", Context.MODE_PRIVATE).edit().apply {
            putString("TabsView", FolderShortcuts::class.java.simpleName)
            apply()
        }

    }

    override fun onBackPressed() {
        if (functionsClass.UsageAccessEnabled()) {
            this@FolderShortcuts.finish()
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
                        functionsClass.overrideBackPress(this@FolderShortcuts, SplitShortcuts::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {

                    }
                }
            }
            else -> {}
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        motionEvent?.let { swipeGestureListener.onTouchEvent(it) }

        return super.dispatchTouchEvent(motionEvent)
    }

    private fun initializeLoadingProcess() {

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons.load()
        }

        if (functionsClass.UsageAccessEnabled()) {

            smartPickProcess()

        } else {

            if (!functionsClass.mixShortcuts()) {

                if (applicationContext.getFileStreamPath(".mixShortcuts").exists()) {

                    functionsClass.readFileLine(".mixShortcuts")?.let {

                        for (mixShortcutLine in it) {
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
                    }

                    applicationContext.deleteFile(".mixShortcuts")
                }
            }

            if (applicationContext.getFileStreamPath(".superFreq").exists()) {
                applicationContext.deleteFile(".superFreq")
            }

            /* Load Data */
            loadCreatedFoldersData()
            /* Load Data */
        }
    }

    fun savedShortcutCounter() {

        folderShortcutsViewBinding.selectedShortcutCounterView.text = functionsClass.countLineInnerFile(FolderShortcuts.FolderShortcutsSelectedFile).toString()
    }

    fun reevaluateShortcutsInfo() {

        evaluateShortcutsInfo()
    }

    fun shortcutDeleted() {

        resetAdapter = true

        loadCreatedFoldersData()

    }
}