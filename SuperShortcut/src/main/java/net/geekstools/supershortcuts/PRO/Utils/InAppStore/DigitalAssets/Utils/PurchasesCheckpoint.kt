/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 10/5/21, 6:05 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData

class PurchasesCheckpoint(var appCompatActivity: AppCompatActivity) : PurchasesUpdatedListener {

    val functionsClass: FunctionsClass = FunctionsClass(appCompatActivity)

    fun trigger() : BillingClient {

        val billingClient = BillingClient.newBuilder(appCompatActivity)
                .setListener(this@PurchasesCheckpoint)
                .enablePendingPurchases().build()

        //In-App Billing
        if (functionsClass.networkConnection()) {

            //Restore Purchased Item
            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    billingResult?.let {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            functionsClass.savePreference(".PurchasedItem", InAppBillingData.SKU.InAppItemMixShortcuts, false)

                            appCompatActivity.lifecycleScope.async {

                                val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build()

                                billingClient.queryPurchasesAsync(queryPurchasesParams).purchasesList.let { purchases ->

                                    for (purchase in purchases) {

                                        functionsClass.savePreference(".PurchasedItem", purchase.products.first(), true)

                                        //Consume Donation
                                        if (purchase.products.first() == InAppBillingData.SKU.InAppItemDonation
                                            && functionsClass.alreadyDonated()) {

                                            val consumeResponseListener = ConsumeResponseListener { billingResult, purchaseToken ->
                                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                                                    functionsClass.savePreference(".PurchasedItem", purchase.products.first(), false)
                                                }
                                            }
                                            val consumeParams = ConsumeParams.newBuilder()
                                            consumeParams.setPurchaseToken(purchase.purchaseToken)
                                            billingClient.consumeAsync(consumeParams.build(), consumeResponseListener)
                                        }

                                        PurchasesCheckpoint.purchaseAcknowledgeProcess(billingClient, purchase, BillingClient.ProductType.INAPP)
                                    }

                                }

                            }

                        }
                    }
                }

                override fun onBillingServiceDisconnected() {

                }
            })

            //Restore Subscribed Item
            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    billingResult.let {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            functionsClass.savePreference(".SubscribedItem", InAppBillingData.SKU.InAppItemSecurityServices, false)

                            appCompatActivity.lifecycleScope.async {

                                val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()

                                billingClient.queryPurchasesAsync(queryPurchasesParams).purchasesList.let { purchases ->

                                    for (purchase in purchases) {

                                        functionsClass.savePreference(".SubscribedItem", purchase.products.first(), true)

                                        PurchasesCheckpoint.purchaseAcknowledgeProcess(billingClient, purchase, BillingClient.ProductType.SUBS)
                                    }
                                }

                            }

                        }
                    }
                }

                override fun onBillingServiceDisconnected() {

                }
            })
        }

        return billingClient
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchasesList: List<Purchase>?) {

        billingResult.let {
            if (!purchasesList.isNullOrEmpty()) {

                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                    }
                    BillingClient.BillingResponseCode.OK -> {

                    }
                    else -> {

                    }
                }
            }
        }
    }

    companion object {

        fun purchaseAcknowledgeProcess(billingClient: BillingClient, purchase: Purchase, purchaseType: String) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

                if (!purchase.isAcknowledged) {

                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)

                    val aPurchaseResult: BillingResult = billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())

                }
            }
        }
    }
}