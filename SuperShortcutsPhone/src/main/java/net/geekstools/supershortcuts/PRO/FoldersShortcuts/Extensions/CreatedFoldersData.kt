/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 2:03 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts.Extensions

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.*
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Adapters.FolderShortcutsAdapter
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData

fun FolderShortcuts.loadCreatedFoldersData()  = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
    createdFolderListItem.clear()

    if (!getFileStreamPath(".categorySuper").exists()) {

        createdFolderListItem.add(AdapterItemsData(packageName, arrayOf(packageName)))

    } else {

        functionsClass.readFileLine(".categorySuper").forEachIndexed { index, folderName ->

            createdFolderListItem.add(AdapterItemsData(
                    folderName,
                    functionsClass.readFileLine(folderName)
            ))
        }

        createdFolderListItem.add(AdapterItemsData(packageName, arrayOf(packageName)))
    }

    withContext(Dispatchers.Main) {

        folderSelectionListAdapter = FolderShortcutsAdapter(this@loadCreatedFoldersData,
                createdFolderListItem)

        folderShortcutsViewBinding.recyclerViewList.adapter = folderSelectionListAdapter

        folderShortcutsViewBinding.loadingSplash.visibility = View.INVISIBLE

        if (!resetAdapter) {
            val animationFadeOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            folderShortcutsViewBinding.loadingSplash.startAnimation(animationFadeOut)
        }

        folderShortcutsViewBinding.confirmButton.visibility = View.VISIBLE

        val animationFadeIn = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in)
        folderShortcutsViewBinding.selectedShortcutCounterView.startAnimation(animationFadeIn)
        animationFadeIn.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

                folderShortcutsViewBinding.selectedShortcutCounterView.text = functionsClass.countLineInnerFile(FolderShortcuts.FolderShortcutsSelectedFile).toString()
            }

            override fun onAnimationEnd(animation: Animation) {

                folderShortcutsViewBinding.selectedShortcutCounterView.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        resetAdapter = false
    }
}