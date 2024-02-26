/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/3/20 10:26 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.Extensions

import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable

fun SplitShortcuts.setupUI() {

    splitShortcutsViewBinding.confirmLayout.bringToFront()

    recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    splitShortcutsViewBinding.recyclerViewList.layoutManager = recyclerViewLayoutManager

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = getColor(R.color.light)
    window.navigationBarColor = getColor(R.color.light)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    val typeface = resources.getFont(R.font.upcil)

    splitShortcutsViewBinding.loadingDescription.typeface = typeface
    splitShortcutsViewBinding.loadingProgress.indeterminateTintList = ColorStateList.valueOf(getColor(R.color.default_color))

    splitShortcutsViewBinding.selectedShortcutCounterView.typeface = typeface
    splitShortcutsViewBinding.selectedShortcutCounterView.bringToFront()
}

fun SplitShortcuts.evaluateShortcutsInfo() {

    if (functionsClass.mixShortcuts()) {

        PublicVariable.SplitShortcutsMaxAppShortcuts= functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");

        splitShortcutsViewBinding.estimatedShortcutCounterView.text = Html.fromHtml("<small><font color='" + getColor(R.color.default_color) + "'>"
                + getString(R.string.maximum) + "</font>"
                + "<b><font color='" + getColor(R.color.default_color_darker) + "'> " + PublicVariable.SplitShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

    } else {

        appShortcutLimitCounter = functionsClass.systemMaxAppShortcut - functionsClass.countLine(".SplitSuperSelected");

        splitShortcutsViewBinding.estimatedShortcutCounterView.text = Html.fromHtml("<small><font color='" + getColor(R.color.default_color) + "'>"
                + getString(R.string.maximum) + "</font>"
                + "<b><font color='" + getColor(R.color.default_color_darker) + "'> " + appShortcutLimitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

        PublicVariable.SplitShortcutsMaxAppShortcuts = functionsClass.systemMaxAppShortcut
    }
}