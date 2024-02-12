/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 9:31 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions

import android.app.Activity
import android.app.usage.UsageStatsManager
import android.content.Context
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass

fun NormalAppShortcutsSelectionListPhone.smartPickProcess() {

    normalAppSelectionBinding.loadingProgress.visibility = View.INVISIBLE
    normalAppSelectionBinding.loadingLogo.setImageDrawable(getDrawable(R.drawable.draw_smart))
    normalAppSelectionBinding.loadingDescription.text = Html.fromHtml(getString(R.string.smartInfo), Html.FROM_HTML_MODE_LEGACY)

    normalAppSelectionBinding.autoSelect.visibility = View.INVISIBLE
    normalAppSelectionBinding.estimatedShortcutCounterView.visibility = View.INVISIBLE

    functionsClass.deleteSelectedFiles()

    extractSaveFrequentlyUsedApplicationsData(this@smartPickProcess,
            functionsClass)
}

/**
 * 86400000 = 24h
 **/
private fun retrieveFrequentlyUsedApplications(activity: Activity,
                                               functionsClass: FunctionsClass,
                                               initialMaxValue: Int,
                                               /**86400000‬ = 1 days**/startTime: Long, endTime: Long /*System.currentTimeMillis()*/): List<String> {

    val frequentlyUsedApplications: MutableList<String> = ArrayList()

    val usageStatsManager = activity.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
            System.currentTimeMillis() - startTime,
            endTime)

    queryUsageStats.sortWith(Comparator { left, right ->

        right.totalTimeInForeground.compareTo(left.totalTimeInForeground)
    })

    var maxValue = initialMaxValue
    if (queryUsageStats.size < maxValue) {
        maxValue = queryUsageStats.size
    }

    for (i in 0 until maxValue) {
        val aPackageName = queryUsageStats[i].packageName

        if (aPackageName != activity.packageName) {
            if (functionsClass.isAppInstalled(aPackageName)) {
                if (!functionsClass.ifSystem(aPackageName)) {
                    if (!functionsClass.ifDefaultLauncher(aPackageName)) {
                        if (functionsClass.canLaunch(aPackageName)) {

                            frequentlyUsedApplications.add(aPackageName)

                        }
                    }
                }
            }
        }
    }

    val stringHashSet: Set<String> = LinkedHashSet(frequentlyUsedApplications)
    frequentlyUsedApplications.clear()
    frequentlyUsedApplications.addAll(stringHashSet)

    return frequentlyUsedApplications
}

private fun extractSaveFrequentlyUsedApplicationsData(activity: AppCompatActivity, functionsClass: FunctionsClass) = CoroutineScope(Dispatchers.Default).launch {

    functionsClass.deleteSelectedFiles()

    val frequentlyUsedApplications = retrieveFrequentlyUsedApplications(activity, functionsClass,
            functionsClass.systemMaxAppShortcut, (86400000 * 7).toLong(), System.currentTimeMillis())

    frequentlyUsedApplications.forEach {
        functionsClass.saveFileAppendLine(
                ".superFreq",
                it)
    }

    functionsClass.addAppShortcutsFreqApps()
}