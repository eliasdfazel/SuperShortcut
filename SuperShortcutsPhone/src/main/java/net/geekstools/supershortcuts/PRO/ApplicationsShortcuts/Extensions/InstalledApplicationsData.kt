/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 1:01 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions

import android.content.pm.ApplicationInfo
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.*
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SelectionListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListXYZ
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.Factory.IndexedFastScrollerFactory
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.IndexedFastScroller
import java.util.*
import kotlin.collections.ArrayList

fun NormalAppShortcutsSelectionListXYZ.loadInstalledAppsData() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
    installedAppsListItem.clear()

    val listOfNewCharOfItemsForIndex: ArrayList<String> = ArrayList<String>()

    applicationInfoList = applicationContext.packageManager.getInstalledApplications(0) as ArrayList<ApplicationInfo>
    Collections.sort(applicationInfoList, ApplicationInfo.DisplayNameComparator(packageManager))

    applicationInfoList.forEach { applicationInfo ->

        if (applicationContext.packageManager.getLaunchIntentForPackage((applicationInfo).packageName) != null) {

            val packageName = applicationInfo.packageName
            val appName = functionsClass.appName(packageName)
            val appIcon = if (functionsClass.customIconsEnable()) {
                loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.appIconDrawable(packageName))
            } else {
                functionsClass.appIconDrawable(packageName)
            }

            listOfNewCharOfItemsForIndex.add(appName.substring(0, 1).toUpperCase(Locale.getDefault()))

            installedAppsListItem.add(AdapterItemsData(
                    appName,
                    packageName,
                    appIcon))
        }
    }

    withContext(Dispatchers.Main) {

        appSelectionListAdapter = SelectionListAdapter(applicationContext,
                functionsClass,
                normalAppSelectionBinding.temporaryFallingIcon,
                installedAppsListItem,
                appsConfirmButton!!,
                this@loadInstalledAppsData)

        normalAppSelectionBinding.recyclerViewApplicationsList.adapter = appSelectionListAdapter

        normalAppSelectionBinding.loadingSplash.visibility = View.INVISIBLE

        if (!resetAdapter) {
            val animationFadeOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            normalAppSelectionBinding.loadingSplash.startAnimation(animationFadeOut)
        }

        appsConfirmButton?.makeItVisible()

        val animationFadeIn = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in)
        normalAppSelectionBinding.appSelectedCounterView.startAnimation(animationFadeIn)
        animationFadeIn.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

                normalAppSelectionBinding.appSelectedCounterView.text = functionsClass.countLineInnerFile(".autoSuper").toString()
            }

            override fun onAnimationEnd(animation: Animation) {

                normalAppSelectionBinding.appSelectedCounterView.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        resetAdapter = false
    }

    /*Indexed Popup Fast Scroller*/
    val indexedFastScroller: IndexedFastScroller = IndexedFastScroller(
            context = applicationContext,
            layoutInflater = layoutInflater,
            rootView = normalAppSelectionBinding.MainView,
            nestedScrollView = normalAppSelectionBinding.nestedScrollView,
            recyclerView = normalAppSelectionBinding.recyclerViewApplicationsList,
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