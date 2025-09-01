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
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.supershortcuts.PRO.Utils.UI.Common.setupUI

fun InitializeInAppBilling.setupInAppBillingUI() {

    inAppBillingViewBinding.root.setBackgroundColor(getColor(R.color.dark))

    setupUI(this@setupInAppBillingUI)

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