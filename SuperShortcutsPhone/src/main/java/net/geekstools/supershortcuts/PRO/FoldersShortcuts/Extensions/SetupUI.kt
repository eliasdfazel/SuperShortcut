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
import android.os.Build
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable

fun FolderShortcuts.setupUI() {

    folderShortcutsViewBinding.confirmLayout.bringToFront()

    recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    folderShortcutsViewBinding.recyclerViewList.layoutManager = recyclerViewLayoutManager

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = getColor(R.color.light)
    window.navigationBarColor = getColor(R.color.light)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    val typeface = resources.getFont(R.font.upcil)

    folderShortcutsViewBinding.loadingDescription.typeface = typeface
    folderShortcutsViewBinding.loadingProgress.indeterminateTintList = ColorStateList.valueOf(getColor(R.color.default_color))

    folderShortcutsViewBinding.selectedShortcutCounterView.typeface = typeface
    folderShortcutsViewBinding.selectedShortcutCounterView.bringToFront()
}

fun FolderShortcuts.evaluateShortcutsInfo() {

    if (functionsClass.mixShortcuts()) {

        PublicVariable.advanceShortcutsMaxAppShortcuts = functionsClass.systemMaxAppShortcut - functionsClass.countLine(".mixShortcuts")

        folderShortcutsViewBinding.estimatedShortcutCounterView.text = Html.fromHtml("<small><font color='" + getColor(R.color.default_color) + "'>"
                + getString(R.string.maximum) + "</font>"
                + "<b><font color='" + getColor(R.color.default_color_darker) + "'>" + PublicVariable.advanceShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

    } else {

        appShortcutLimitCounter = functionsClass.systemMaxAppShortcut - functionsClass.countLine(".categorySuperSelected")

        folderShortcutsViewBinding.estimatedShortcutCounterView.text = Html.fromHtml("<small><font color='" + getColor(R.color.default_color) + "'>"
                + getString(R.string.maximum) + "</font>"
                + "<b><font color='" + getColor(R.color.default_color_darker) + "'>" + appShortcutLimitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

        PublicVariable.advanceShortcutsMaxAppShortcuts = functionsClass.systemMaxAppShortcut

    }
}