/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 11:14 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

interface PurchaseFlowController {
    fun purchaseFlowInitial(billingResult: BillingResult?)
    fun purchaseFlowDisrupted(errorMessage: String?)
    fun purchaseFlowSucceeded(skuDetails: SkuDetails)
    fun purchaseFlowPaid(billingClient: BillingClient, purchase: Purchase)
    fun purchaseFlowPaid(skuDetails: SkuDetails)
}