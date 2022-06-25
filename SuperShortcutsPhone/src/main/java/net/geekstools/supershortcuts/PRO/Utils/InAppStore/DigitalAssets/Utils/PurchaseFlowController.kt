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
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

interface PurchaseFlowController {
    fun purchaseFlowInitial(billingResult: BillingResult?)
    fun purchaseFlowDisrupted(errorMessage: String?)
    fun purchaseFlowSucceeded(productDetails: ProductDetails)
    fun purchaseFlowPaid(billingClient: BillingClient, purchase: Purchase)
    fun purchaseFlowPaid(productDetails: ProductDetails)
}