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
import android.text.Html
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable

fun SplitShortcuts.setupUI() {

    splitShortcutsViewBinding.confirmLayout.bringToFront()

    recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    splitShortcutsViewBinding.recyclerViewList.layoutManager = recyclerViewLayoutManager

    enableEdgeToEdge()

    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())

        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.hide(WindowInsetsCompat.Type.navigationBars())

        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    splitShortcutsViewBinding.loadingProgress.indeterminateTintList = ColorStateList.valueOf(getColor(R.color.default_color))

    splitShortcutsViewBinding.selectedShortcutCounterView.bringToFront()
}

fun SplitShortcuts.evaluateShortcutsInfo() {

    if (functionsClass.mixShortcuts()) {

        PublicVariable.SplitShortcutsMaxAppShortcuts= functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");

        splitShortcutsViewBinding.estimatedShortcutCounterView.text = Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>"
                + getString(R.string.maximum) + "</font>"
                + "<b><font color='" + getColor(R.color.white) + "'> " + PublicVariable.SplitShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

    } else {

        appShortcutLimitCounter = functionsClass.systemMaxAppShortcut - functionsClass.countLine(".SplitSuperSelected");

        splitShortcutsViewBinding.estimatedShortcutCounterView.text = Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>"
                + getString(R.string.maximum) + "</font>"
                + "<b><font color='" + getColor(R.color.white) + "'> " + appShortcutLimitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

        PublicVariable.SplitShortcutsMaxAppShortcuts = functionsClass.systemMaxAppShortcut
    }
}