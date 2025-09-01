/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 2:01 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts.Extensions

import android.content.res.ColorStateList
import android.text.Html
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable

fun FolderShortcuts.setupUI() {

    folderShortcutsViewBinding.confirmLayout.bringToFront()

    recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    folderShortcutsViewBinding.recyclerViewList.layoutManager = recyclerViewLayoutManager

    enableEdgeToEdge()

    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())

        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.hide(WindowInsetsCompat.Type.navigationBars())

        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    folderShortcutsViewBinding.loadingProgress.indeterminateTintList = ColorStateList.valueOf(getColor(R.color.default_color))

    folderShortcutsViewBinding.selectedShortcutCounterView.bringToFront()
}

fun FolderShortcuts.evaluateShortcutsInfo() {

    if (functionsClass.mixShortcuts()) {

        PublicVariable.advanceShortcutsMaxAppShortcuts = functionsClass.systemMaxAppShortcut - functionsClass.countLine(".mixShortcuts")

        folderShortcutsViewBinding.estimatedShortcutCounterView.text = Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>"
                + getString(R.string.maximum) + "</font>"
                + "<b><font color='" + getColor(R.color.white) + "'> " + PublicVariable.advanceShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

    } else {

        appShortcutLimitCounter = functionsClass.systemMaxAppShortcut - functionsClass.countLine(".categorySuperSelected")

        folderShortcutsViewBinding.estimatedShortcutCounterView.text = Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>"
                + getString(R.string.maximum) + "</font>"
                + "<b><font color='" + getColor(R.color.white) + "'> " + appShortcutLimitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

        PublicVariable.advanceShortcutsMaxAppShortcuts = functionsClass.systemMaxAppShortcut

    }
}