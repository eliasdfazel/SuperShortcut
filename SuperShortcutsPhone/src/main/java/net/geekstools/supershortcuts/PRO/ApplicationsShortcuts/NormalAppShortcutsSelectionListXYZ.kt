/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 1:36 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts

import android.app.ActivityOptions
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SavedApplicationsListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SelectionListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions.evaluateShortcutsInfo
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions.loadInstalledAppsData
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions.setupConfirmButtonUI
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions.setupUI
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButton
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint
import net.geekstools.supershortcuts.PRO.Utils.RemoteProcess.LicenseValidator
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.SwipeGestureListener
import net.geekstools.supershortcuts.PRO.databinding.NormalAppSelectionBinding
import java.util.*

class NormalAppShortcutsSelectionListXYZ : AppCompatActivity(),
        GestureListenerInterface,
        ConfirmButtonProcessInterface {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val listPopupWindow: ListPopupWindow by lazy {
        ListPopupWindow(applicationContext)
    }

    lateinit var recyclerViewLayoutManager: LinearLayoutManager
    lateinit var appSelectionListAdapter: RecyclerView.Adapter<SelectionListAdapter.ViewHolder>
    val installedAppsListItem: ArrayList<AdapterItemsData> = ArrayList<AdapterItemsData>()

    var appsConfirmButton: AppsConfirmButton? = null

    var appShortcutLimitCounter = 0

    var resetAdapter: Boolean = false

    val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@NormalAppShortcutsSelectionListXYZ)
    }

    lateinit var normalAppSelectionBinding: NormalAppSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        normalAppSelectionBinding = NormalAppSelectionBinding.inflate(layoutInflater)
        setContentView(normalAppSelectionBinding.root)

        /* Check Shortcuts Information */
        evaluateShortcutsInfo()
        /* Check Shortcuts Information */

        appsConfirmButton = setupConfirmButtonUI(this@NormalAppShortcutsSelectionListXYZ)

        /* Setup UI*/
        setupUI()
        /* Setup UI*/

        /* Load Installed Applications */
        loadInstalledAppsData()
        /* Load Installed Applications */

        functionsClass.ChangeLog(this@NormalAppShortcutsSelectionListXYZ, false)

        //In-App Billing
        PurchasesCheckpoint(this@NormalAppShortcutsSelectionListXYZ).trigger()
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
                        functionsClass.dialogueLicense(this@NormalAppShortcutsSelectionListXYZ)

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
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (functionsClass.UsageAccessEnabled()) {
            this@NormalAppShortcutsSelectionListXYZ.finish()
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
                        functionsClass.navigateToClass(this@NormalAppShortcutsSelectionListXYZ, SplitShortcuts::class.java,
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

        normalAppSelectionBinding.appSelectedCounterView.text = functionsClass.countLineInnerFile(".autoSuper").toString()
    }

    override fun showSavedShortcutList() {

        if (getFileStreamPath(".autoSuper").exists()
                && functionsClass.countLine(".autoSuper") > 0) {

            val savedApplicationsList: ArrayList<AdapterItemsData> = ArrayList<AdapterItemsData>()
            savedApplicationsList.clear()

            val savedLine = functionsClass.readFileLine(".autoSuper")
            for (aSavedLine in savedLine) {

                val packageName = aSavedLine.split("\\|").toTypedArray()[0]
                val className = aSavedLine.split("\\|").toTypedArray()[1]
                val activityInfo = packageManager.getActivityInfo(ComponentName(packageName, className), 0)

                savedApplicationsList.add(AdapterItemsData(
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

            val savedListAdapter = SavedApplicationsListAdapter(this@NormalAppShortcutsSelectionListXYZ,
                    applicationContext,
                    savedApplicationsList)

            listPopupWindow.setAdapter(savedListAdapter)
            listPopupWindow.setBackgroundDrawable(null)
            listPopupWindow.anchorView = normalAppSelectionBinding.popupAnchorView
            listPopupWindow.width = android.widget.ListPopupWindow.WRAP_CONTENT
            listPopupWindow.height = android.widget.ListPopupWindow.WRAP_CONTENT
            listPopupWindow.isModal = true
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
}