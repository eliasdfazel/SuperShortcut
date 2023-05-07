/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/22/21 10:06 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone
import net.geekstools.supershortcuts.PRO.BuildConfig
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts
import net.geekstools.supershortcuts.PRO.MixShortcuts.MixShortcutsProcess
import net.geekstools.supershortcuts.PRO.Preferences.PreferencesUI
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess
import net.geekstools.supershortcuts.PRO.SplitShortcuts.Adapters.SplitShortcutsAdapter
import net.geekstools.supershortcuts.PRO.SplitShortcuts.Extensions.evaluateShortcutsInfo
import net.geekstools.supershortcuts.PRO.SplitShortcuts.Extensions.loadCreatedSplitsData
import net.geekstools.supershortcuts.PRO.SplitShortcuts.Extensions.setupUI
import net.geekstools.supershortcuts.PRO.SplitShortcuts.Extensions.smartPickProcess
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
import net.geekstools.supershortcuts.PRO.databinding.SplitShortcutsViewBinding
import java.util.*

class SplitShortcuts : AppCompatActivity(),
        GestureListenerInterface {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val functionsClassDialogues: FunctionsClassDialogues by lazy {
        FunctionsClassDialogues(this@SplitShortcuts, functionsClass)
    }

    lateinit var recyclerViewLayoutManager: LinearLayoutManager
    lateinit var splitSelectionListAdapter: RecyclerView.Adapter<SplitShortcutsAdapter.ViewHolder>
    val createdSplitListItem: ArrayList<AdapterItemsData> = ArrayList<AdapterItemsData>()

    var appShortcutLimitCounter = 0

    var resetAdapter: Boolean = false
    var updateAvailable: Boolean = false

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@SplitShortcuts)
    }

    companion object {
        const val SplitShortcutsFile = ".SplitSuper"
        const val SplitShortcutsSelectedFile = ".SplitSuperSelected"
    }

    lateinit var splitShortcutsViewBinding: SplitShortcutsViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splitShortcutsViewBinding = SplitShortcutsViewBinding.inflate(layoutInflater)
        setContentView(splitShortcutsViewBinding.root)

        SecurityServicesProcess(this@SplitShortcuts).switchSecurityServices(splitShortcutsViewBinding.securityServicesSwitchView)

        /* Check Shortcuts Information */
        evaluateShortcutsInfo()
        /* Check Shortcuts Information */

        /* Setup UI*/
        setupUI()
        /* Setup UI*/

        initializeLoadingProcess()

        functionsClassDialogues.changeLog(!functionsClass.isFirstToCheckTutorial, false)

        //In-App Billing
        PurchasesCheckpoint(this@SplitShortcuts).trigger()
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
                        functionsClass.dialogueLicense(this@SplitShortcuts)

                        Handler().postDelayed({
                            stopService(Intent(applicationContext, LicenseValidator::class.java))
                        }, 1000)

                        unregisterReceiver(this)
                    }
                }
            }
            registerReceiver(broadcastReceiver, intentFilter)
        }

        splitShortcutsViewBinding.confirmButton.setOnClickListener {
            if (functionsClass.mixShortcuts()) {
                functionsClass.addMixAppShortcuts()
            } else {
                functionsClass.addAppsShortcutSplit()

                getSharedPreferences(".PopupShortcut", Context.MODE_PRIVATE).edit().apply {
                    putString("PopupShortcutMode", "SplitShortcuts")
                    apply()
                }
            }
        }

        splitShortcutsViewBinding.confirmButton.setOnLongClickListener {

            functionsClass.deleteSelectedFiles()

            shortcutDeleted()

            savedShortcutCounter()

            reevaluateShortcutsInfo()

            functionsClass.clearDynamicShortcuts()

            true
        }

        splitShortcutsViewBinding.autoApps.setOnClickListener {

            functionsClass.overrideBackPress(this@SplitShortcuts, NormalAppShortcutsSelectionListPhone::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))

        }

        splitShortcutsViewBinding.autoCategories.setOnClickListener {

            functionsClass.overrideBackPress(this@SplitShortcuts, FolderShortcuts::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
        }

        splitShortcutsViewBinding.preferencesView.setOnClickListener {

            if (updateAvailable) {

                FunctionsClassDialogues(this@SplitShortcuts, functionsClass).changeLogPreference(
                        firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()),
                        (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()).toString())
                )

            } else {

                startActivity(Intent(applicationContext, PreferencesUI::class.java),
                        ActivityOptions.makeCustomAnimation(applicationContext, R.anim.up_down, android.R.anim.fade_out).toBundle())

                this@SplitShortcuts.finish()
            }
        }

        MixShortcutsProcess(applicationContext, splitShortcutsViewBinding.mixShortcutsSwitchView).initialize()
    }

    override fun onResume() {
        super.onResume()

        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnSuccessListener {

                    firebaseRemoteConfig.activate().addOnSuccessListener {

                        if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > BuildConfig.VERSION_CODE) {

                            val layerDrawableNewUpdate = getDrawable(R.drawable.ic_update) as LayerDrawable?
                            val gradientDrawableNewUpdate = layerDrawableNewUpdate?.findDrawableByLayerId(R.id.temporaryBackground) as BitmapDrawable?
                            gradientDrawableNewUpdate?.setTint(getColor(R.color.default_color_game))

                            val temporaryBitmap = functionsClass.drawableToBitmap(layerDrawableNewUpdate)
                            val scaleBitmap = Bitmap.createScaledBitmap(temporaryBitmap, temporaryBitmap.width / 4, temporaryBitmap.height / 4, false)
                            val logoDrawable: Drawable = BitmapDrawable(resources, scaleBitmap)

                            splitShortcutsViewBinding.preferencesView.setImageDrawable(logoDrawable)

                            functionsClass.notificationCreator(
                                    getString(R.string.updateAvailable),
                                    firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                    firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()).toInt()
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
            putString("TabsView", SplitShortcuts::class.java.simpleName)
            apply()
        }

    }

    override fun onBackPressed() {
        if (functionsClass.UsageAccessEnabled()) {
            this@SplitShortcuts.finish()
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
                        functionsClass.overrideBackPress(this@SplitShortcuts, NormalAppShortcutsSelectionListPhone::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {
                        functionsClass.overrideBackPress(this@SplitShortcuts, FolderShortcuts::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
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
            loadCreatedSplitsData()
            /* Load Data */
        }
    }

    fun savedShortcutCounter() {

        splitShortcutsViewBinding.selectedShortcutCounterView.text = functionsClass.countLineInnerFile(SplitShortcuts.SplitShortcutsSelectedFile).toString()
    }

    fun reevaluateShortcutsInfo() {

        evaluateShortcutsInfo()
    }

    fun shortcutDeleted() {

        resetAdapter = true

        loadCreatedSplitsData()

    }
}