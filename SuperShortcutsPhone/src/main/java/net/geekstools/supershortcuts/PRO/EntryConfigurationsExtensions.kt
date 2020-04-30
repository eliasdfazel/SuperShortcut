/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 12:30 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListXYZ
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.AdvanceShortcuts
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts

fun EntryConfigurations.shortcutModeEntryPoint() {

    when (getSharedPreferences("ShortcutsModeView", Context.MODE_PRIVATE).getString("TabsView", NormalAppShortcutsSelectionListXYZ::class.java.simpleName)) {
        NormalAppShortcutsSelectionListXYZ::class.java.simpleName -> {

            startActivity(Intent(applicationContext, NormalAppShortcutsSelectionListXYZ::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
        SplitShortcuts::class.java.simpleName -> {

            startActivity(Intent(applicationContext, SplitShortcuts::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
        AdvanceShortcuts::class.java.simpleName -> {

            startActivity(Intent(applicationContext, AdvanceShortcuts::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
        else -> {

            startActivity(Intent(applicationContext, NormalAppShortcutsSelectionListXYZ::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
    }

    this@shortcutModeEntryPoint.finish()
}