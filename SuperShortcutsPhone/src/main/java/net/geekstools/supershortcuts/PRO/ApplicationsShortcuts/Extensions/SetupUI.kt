/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 2:23 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions

import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListXYZ
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable

fun NormalAppShortcutsSelectionListXYZ.setupUI() {

    normalAppSelectionBinding.autoApps.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color))
    normalAppSelectionBinding.autoSplit.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_darker))
    normalAppSelectionBinding.autoCategories.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_darker))

    normalAppSelectionBinding.temporaryFallingIcon.bringToFront()
    normalAppSelectionBinding.confirmLayout.bringToFront()

    recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    normalAppSelectionBinding.recyclerViewApplicationsList.layoutManager = recyclerViewLayoutManager

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = getColor(R.color.default_color)
    window.navigationBarColor = getColor(R.color.light)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }

    val typeface = resources.getFont(R.font.upcil)

    normalAppSelectionBinding.loadingDescription.typeface = typeface

    normalAppSelectionBinding.appSelectedCounterView.typeface = typeface
    normalAppSelectionBinding.appSelectedCounterView.bringToFront()
}

fun NormalAppShortcutsSelectionListXYZ.evaluateShortcutsInfo() {

    if (functionsClass.mixShortcuts()) {

        PublicVariable.maxAppShortcuts = functionsClass.systemMaxAppShortcut - functionsClass.countLine(".mixShortcuts")

//        supportActionBar!!.subtitle = Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.maxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

    } else {

        appShortcutLimitCounter = functionsClass.systemMaxAppShortcut - functionsClass.countLine(NormalAppShortcutsSelectionListXYZ.NormalApplicationsShortcutsFile)

//        supportActionBar!!.subtitle = Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + appShortcutLimitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY)

        PublicVariable.maxAppShortcuts = functionsClass.systemMaxAppShortcut

    }

}