/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 3:29 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SavedListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SelectionListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions.loadInstalledAppsData
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButtonWatch
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout
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
        applicationsSelectionListViewBinding.temporaryIcon.bringToFront()

        applicationsSelectionListViewBinding.rootView.setBackgroundColor(getColor(R.color.light))

        val recyclerViewLayoutManager: LinearLayoutManager = RecycleViewSmoothLayout(applicationContext, OrientationHelper.VERTICAL, false)
        applicationsSelectionListViewBinding.recyclerViewApplicationList.layoutManager = recyclerViewLayoutManager

        applicationsSelectionListViewBinding.nestedScrollView.isSmoothScrollingEnabled = true

        val typeface = Typeface.createFromAsset(assets, "upcil.ttf")
        applicationsSelectionListViewBinding.counterView.typeface = typeface
        applicationsSelectionListViewBinding.counterView.bringToFront()

        applicationsSelectionListViewBinding.loadingProgress.indeterminateTintList = ColorStateList.valueOf(getColor(R.color.default_color))

        initializeLoadingProcess()

        setAmbientEnabled()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
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