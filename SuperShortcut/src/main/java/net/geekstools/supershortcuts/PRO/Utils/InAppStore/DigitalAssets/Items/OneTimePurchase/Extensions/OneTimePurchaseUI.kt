/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 11:08 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.Extensions

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.OneTimePurchase

fun OneTimePurchase.setupOneTimePurchaseUI() {

    inAppBillingOneTimePurchaseViewBinding.itemTitleView.setTextColor(requireContext().getColor(R.color.light))
    inAppBillingOneTimePurchaseViewBinding.itemDescriptionView.setTextColor(requireContext().getColor(R.color.light))
}

fun OneTimePurchase.setScreenshots() = CoroutineScope(Dispatchers.Main).launch {

    for (i in 1..screenshotsNumber) {
        val inAppBillingScreenshots = requireActivity().layoutInflater.inflate(R.layout.in_app_billing_screenshots, null) as RelativeLayout
        val screenshotItemView = inAppBillingScreenshots.findViewById<View>(R.id.screenshotItemView) as ImageView

        screenshotItemView.setImageDrawable(mapIndexDrawable[i])
        screenshotItemView.setOnClickListener(this@setScreenshots)
        screenshotItemView.tag = mapIndexURI[i]

        inAppBillingOneTimePurchaseViewBinding.itemScreenshotsListView.addView(inAppBillingScreenshots)
    }

    inAppBillingOneTimePurchaseViewBinding.waitingView.visibility = View.GONE
}