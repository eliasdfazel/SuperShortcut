/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 12:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Extensions

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.WindowManager
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling

fun InitializeInAppBilling.setupInAppBillingUI() {

    inAppBillingViewBinding.root.setBackgroundColor(getColor(R.color.light))

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = getColor(R.color.light)
    window.navigationBarColor = getColor(R.color.light)
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

    inAppBillingViewBinding.shareFloatIt.setOnClickListener {

        val sharingText: String = getString(R.string.shareTitle) + "\n" +
                "" + getString(R.string.shareSummary) + "\n" +
                "" + "${getString(R.string.play_store_link)}${packageName}"

        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, sharingText)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(this@apply)
        }
    }

    inAppBillingViewBinding.rateFloatIt.setOnClickListener {

        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.play_store_link) + packageName)))
    }
}