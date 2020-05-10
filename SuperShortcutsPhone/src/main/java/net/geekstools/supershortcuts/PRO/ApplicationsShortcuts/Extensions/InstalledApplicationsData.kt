/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 9:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.*
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SelectionListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.Factory.IndexedFastScrollerFactory
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.IndexedFastScroller
import java.util.*
import kotlin.collections.ArrayList

fun NormalAppShortcutsSelectionListPhone.loadInstalledAppsData() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
    installedAppsListItem.clear()

    val listOfNewCharOfItemsForIndex: ArrayList<String> = ArrayList<String>()

    val applicationInfoList = packageManager.queryIntentActivities(Intent().apply {
        this.action = Intent.ACTION_MAIN
        this.addCategory(Intent.CATEGORY_LAUNCHER)
    }, PackageManager.GET_RESOLVED_FILTER)
    val applicationInfoListSorted = applicationInfoList.sortedWith(ResolveInfo.DisplayNameComparator(packageManager))

    applicationInfoListSorted.forEach { resolveInfo ->

        if (applicationContext.packageManager.getLaunchIntentForPackage((resolveInfo).activityInfo.packageName) != null) {

            val packageName = resolveInfo.activityInfo.packageName
            val className = resolveInfo.activityInfo.name
            val appName = functionsClass.activityLabel(resolveInfo.activityInfo)
            val appIcon = if (functionsClass.customIconsEnable()) {
                loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.activityIcon(resolveInfo.activityInfo))
            } else {
                functionsClass.activityIcon(resolveInfo.activityInfo)
            }

            listOfNewCharOfItemsForIndex.add(appName.substring(0, 1).toUpperCase(Locale.getDefault()))

            installedAppsListItem.add(AdapterItemsData(
                    appName,
                    packageName,
                    className,
                    appIcon))
        }
    }

    withContext(Dispatchers.Main) {

        appSelectionListAdapter = SelectionListAdapter(applicationContext,
                functionsClass,
                normalAppSelectionBinding.temporaryFallingIcon,
                installedAppsListItem,
                appsConfirmButtonPhone!!,
                this@loadInstalledAppsData)

        normalAppSelectionBinding.recyclerViewList.adapter = appSelectionListAdapter

        normalAppSelectionBinding.loadingSplash.visibility = View.INVISIBLE

        if (!resetAdapter) {
            val animationFadeOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            normalAppSelectionBinding.loadingSplash.startAnimation(animationFadeOut)
        }

        appsConfirmButtonPhone?.makeItVisible()

        val animationFadeIn = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in)
        normalAppSelectionBinding.selectedShortcutCounterView.startAnimation(animationFadeIn)
        animationFadeIn.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

                normalAppSelectionBinding.selectedShortcutCounterView.text = functionsClass.countLineInnerFile(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile).toString()
            }

            override fun onAnimationEnd(animation: Animation) {

                normalAppSelectionBinding.selectedShortcutCounterView.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        PublicVariable.maxAppShortcutsCounter = functionsClass.countLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile)
        resetAdapter = false;
    }

    /*Indexed Popup Fast Scroller*/
    val indexedFastScroller: IndexedFastScroller = IndexedFastScroller(
            context = applicationContext,
            layoutInflater = layoutInflater,
            rootView = normalAppSelectionBinding.MainView,
            nestedScrollView = normalAppSelectionBinding.nestedScrollView,
            recyclerView = normalAppSelectionBinding.recyclerViewList,
            fastScrollerIndexViewBinding = normalAppSelectionBinding.fastScrollerIndexInclude,
            indexedFastScrollerFactory = IndexedFastScrollerFactory(
                    popupEnable = true,
                    popupTextColor = getColor(R.color.light),
                    indexItemTextColor = getColor(R.color.dark))
    )
    indexedFastScroller.initializeIndexView().await()
            .loadIndexData(listOfNewCharOfItemsForIndex = listOfNewCharOfItemsForIndex).await()
    /*Indexed Popup Fast Scroller*/
}