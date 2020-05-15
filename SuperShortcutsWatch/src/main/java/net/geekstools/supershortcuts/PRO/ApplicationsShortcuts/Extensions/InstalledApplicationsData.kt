/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/15/20 9:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions

import android.R
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SelectionListAdapter
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListWatch
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Factory.IndexedFastScrollerFactoryWatch
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.IndexedFastScrollerWatch
import java.util.*

fun NormalAppShortcutsSelectionListWatch.loadInstalledAppsData() = CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {

    val applicationInformationList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    Collections.sort(applicationInformationList, ApplicationInfo.DisplayNameComparator(packageManager))

    applicationInformationList.asFlow()
            .filter {

                (packageManager.getLaunchIntentForPackage(it.packageName) != null)
            }
            .map {

                it
            }
            .onCompletion {

                /*Indexed Popup Fast Scroller*/
                val indexedFastScrollerWatch: IndexedFastScrollerWatch = IndexedFastScrollerWatch(
                        context = applicationContext,
                        layoutInflater = layoutInflater,
                        rootView = applicationsSelectionListViewBinding.rootView,
                        recyclerView = applicationsSelectionListViewBinding.recyclerViewApplicationList,
                        fastScrollerIndexViewBinding = applicationsSelectionListViewBinding.fastScrollerIndexInclude,
                        indexedFastScrollerFactoryWatch = IndexedFastScrollerFactoryWatch(
                                popupEnable = true,
                                popupTextColor = getColor(net.geekstools.supershortcuts.PRO.R.color.light),
                                indexItemTextColor = getColor(net.geekstools.supershortcuts.PRO.R.color.dark))
                )
                indexedFastScrollerWatch.initializeIndexView().await()
                        .loadIndexData(listOfNewCharOfItemsForIndex = listOfNewCharOfItemsForIndex).await()
                /*Indexed Popup Fast Scroller*/

            }
            .collect { applicationInfo ->
                val packageName = applicationInfo.packageName
                val appName = functionsClass.appName(packageName)
                val appIcon = functionsClass.appIconDrawable(packageName)

                installedApplicationsItemsData.add(
                        AdapterItemsData(
                                appName,
                                packageName,
                                appIcon)
                )

                listOfNewCharOfItemsForIndex.add(appName[0].toUpperCase().toString())
            }

    selectionListAdapter = SelectionListAdapter(applicationContext,
            installedApplicationsItemsData,
            applicationsSelectionListViewBinding.temporaryIcon)

    functionsClass.savePreference("InstalledApps", "countApps", installedApplicationsItemsData.size)

    withContext(Dispatchers.Main) {

        applicationsSelectionListViewBinding.recyclerViewApplicationList.adapter = selectionListAdapter

        val fadeOutAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        applicationsSelectionListViewBinding.loadingSplash.visibility = View.INVISIBLE

        if (!resetAdapter) {
            applicationsSelectionListViewBinding.loadingSplash.startAnimation(fadeOutAnimation)
        }

        appsConfirmButtonWatch?.makeItVisible()

        val fadeInAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        applicationsSelectionListViewBinding.counterView.startAnimation(fadeInAnimation)
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

                applicationsSelectionListViewBinding.counterView.text = functionsClass.countLine(".autoSuper").toString()
            }

            override fun onAnimationEnd(animation: Animation) {

                applicationsSelectionListViewBinding.counterView.visibility = View.VISIBLE

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        PublicVariable.maxAppShortcutsCounter = functionsClass.countLine(".autoSuper")
        PublicVariable.maxAppShortcuts = installedApplicationsItemsData.size

        resetAdapter = false
    }
}