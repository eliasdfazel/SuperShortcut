/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/1/20 12:03 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionList
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.AdvanceShortcuts
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts

fun EntryConfigurations.shortcutModeEntryPoint() {

    when (getSharedPreferences("ShortcutsModeView", Context.MODE_PRIVATE).getString("TabsView", NormalAppShortcutsSelectionList::class.java.simpleName)) {
        NormalAppShortcutsSelectionList::class.java.simpleName -> {

            startActivity(Intent(applicationContext, NormalAppShortcutsSelectionList::class.java),
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

            startActivity(Intent(applicationContext, NormalAppShortcutsSelectionList::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
    }

    this@shortcutModeEntryPoint.finish()
}