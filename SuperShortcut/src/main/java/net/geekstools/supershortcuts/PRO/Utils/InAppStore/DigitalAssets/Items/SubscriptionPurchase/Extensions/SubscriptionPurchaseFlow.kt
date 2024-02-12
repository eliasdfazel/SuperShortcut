/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 11:10 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.Extensions

import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.SubscriptionPurchase

fun SubscriptionPurchase.subscriptionPurchaseFlow(productDetails: ProductDetails) {

    inAppBillingSubscriptionPurchaseViewBinding.centerPurchaseButton.root.setOnClickListener {

        purchaseFlowCommand(productDetails)
    }

    inAppBillingSubscriptionPurchaseViewBinding.bottomPurchaseButton.root.setOnClickListener {

        purchaseFlowCommand(productDetails)
    }
}

private fun SubscriptionPurchase.purchaseFlowCommand(productDetails: ProductDetails) {

    val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken ?: ""

    val billingFlowParams = BillingFlowParams.newBuilder()
        .setProductDetailsParamsList(listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        ))
        .build()

    val billingResult = billingClient.launchBillingFlow(requireActivity() as InitializeInAppBilling, billingFlowParams)
    purchaseFlowController?.purchaseFlowInitial(billingResult)
}