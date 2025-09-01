/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 11:12 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.Extensions

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.SubscriptionPurchase

fun SubscriptionPurchase.setupOneTimePurchaseUI() {

    inAppBillingSubscriptionPurchaseViewBinding.itemTitleView.setTextColor(requireContext().getColor(R.color.light))
    inAppBillingSubscriptionPurchaseViewBinding.itemDescriptionView.setTextColor(requireContext().getColor(R.color.light))
}

fun SubscriptionPurchase.setScreenshots() = CoroutineScope(Dispatchers.Main).launch {

    for (i in 1..screenshotsNumber) {
        val inAppBillingScreenshots = requireActivity().layoutInflater.inflate(R.layout.in_app_billing_screenshots, null) as RelativeLayout
        val screenshotItemView = inAppBillingScreenshots.findViewById<View>(R.id.screenshotItemView) as ImageView

        screenshotItemView.setImageDrawable(mapIndexDrawable[i])
        screenshotItemView.setOnClickListener(this@setScreenshots)
        screenshotItemView.tag = mapIndexURI[i]

        inAppBillingSubscriptionPurchaseViewBinding.itemScreenshotsListView.addView(inAppBillingScreenshots)
    }

    inAppBillingSubscriptionPurchaseViewBinding.waitingView.visibility = View.GONE
}