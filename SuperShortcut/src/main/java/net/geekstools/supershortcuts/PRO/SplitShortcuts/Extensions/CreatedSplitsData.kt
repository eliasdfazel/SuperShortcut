/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/15/20 8:16 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.Extensions

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SplitShortcuts.Adapters.SplitShortcutsAdapter
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Factory.IndexedFastScrollerFactory
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.IndexedFastScroller
import java.util.Locale

fun SplitShortcuts.loadCreatedSplitsData()  = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
    createdSplitListItem.clear()

    val listOfNewCharOfItemsForIndex: ArrayList<String> = ArrayList<String>()

    if (!getFileStreamPath(SplitShortcuts.SplitShortcutsFile).exists()) {

        createdSplitListItem.add(AdapterItemsData(packageName, arrayOf(packageName)))

    } else {

        functionsClass.readFileLine(SplitShortcuts.SplitShortcutsFile)?.let {
            it.sort()
            it.forEachIndexed { index, folderName ->

                if (!folderName.isNullOrBlank()) {

                    createdSplitListItem.add(AdapterItemsData(
                            folderName,
                            functionsClass.readFileLine(folderName)
                    ))

                    listOfNewCharOfItemsForIndex.add(folderName.substring(0, 1).toUpperCase(Locale.getDefault()))

                }

            }
        }

        createdSplitListItem.add(AdapterItemsData(packageName, arrayOf(packageName)))
    }

    withContext(Dispatchers.Main) {

        splitSelectionListAdapter = SplitShortcutsAdapter(this@loadCreatedSplitsData,
                createdSplitListItem)

        splitShortcutsViewBinding.recyclerViewList.adapter = splitSelectionListAdapter

        splitShortcutsViewBinding.loadingSplash.visibility = View.INVISIBLE

        if (!resetAdapter) {
            val animationFadeOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            splitShortcutsViewBinding.loadingSplash.startAnimation(animationFadeOut)
        }

        splitShortcutsViewBinding.confirmButton.visibility = View.VISIBLE

        val animationFadeIn = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in)
        splitShortcutsViewBinding.selectedShortcutCounterView.startAnimation(animationFadeIn)
        animationFadeIn.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

                splitShortcutsViewBinding.selectedShortcutCounterView.text = functionsClass.countLineInnerFile(SplitShortcuts.SplitShortcutsSelectedFile).toString()
            }

            override fun onAnimationEnd(animation: Animation) {

                splitShortcutsViewBinding.selectedShortcutCounterView.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        PublicVariable.SplitShortcutsMaxAppShortcutsCounter = functionsClass.countLine(".SplitSuperSelected")
        resetAdapter = false
    }

    /*Indexed Popup Fast Scroller*/
    val indexedFastScroller: IndexedFastScroller = IndexedFastScroller(
            context = applicationContext,
            layoutInflater = layoutInflater,
            rootView = splitShortcutsViewBinding.MainView,
            nestedScrollView = splitShortcutsViewBinding.nestedScrollView,
            recyclerView = splitShortcutsViewBinding.recyclerViewList,
            fastScrollerIndexViewBinding = splitShortcutsViewBinding.fastScrollerIndexInclude,
            indexedFastScrollerFactory = IndexedFastScrollerFactory(
                popupEnable = true,
                popupTextColor = getColor(R.color.light),
                indexItemTextColor = getColor(R.color.dark),
                popupVerticalOffset = (77/3).toFloat()
            )
    )
    indexedFastScroller.initializeIndexView().await()
            .loadIndexData(listOfNewCharOfItemsForIndex = listOfNewCharOfItemsForIndex).await()
    /*Indexed Popup Fast Scroller*/
}