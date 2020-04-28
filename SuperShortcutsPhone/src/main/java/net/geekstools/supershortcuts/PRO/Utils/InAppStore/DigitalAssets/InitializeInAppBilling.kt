/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 12:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Extensions.setupInAppBillingUI
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.OneTimePurchase
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.SubscriptionPurchase
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchaseFlowController
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint
import net.geekstools.supershortcuts.PRO.databinding.InAppBillingViewBinding

class InitializeInAppBilling : AppCompatActivity(), PurchaseFlowController {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val inAppBillingData: InAppBillingData by lazy {
        InAppBillingData()
    }

    private var oneTimePurchase: OneTimePurchase? = null
    private var subscriptionPurchase: SubscriptionPurchase? = null

    object Entry {
        const val PurchaseType = "PurchaseType"
        const val ItemToPurchase = "ItemToPurchase"

        const val OneTimePurchase = "OneTimePurchase"
        const val SubscriptionPurchase = "SubscriptionPurchase"
    }

    lateinit var inAppBillingViewBinding: InAppBillingViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inAppBillingViewBinding = InAppBillingViewBinding.inflate(layoutInflater)
        setContentView(inAppBillingViewBinding.root)

        setupInAppBillingUI()

        if (functionsClass.networkConnection()
                && intent.hasExtra(Entry.PurchaseType) && intent.hasExtra(Entry.ItemToPurchase)) {

            when(intent.getStringExtra(Entry.PurchaseType)) {
                Entry.OneTimePurchase -> {
                    oneTimePurchase = OneTimePurchase().apply {
                        purchaseFlowController = this@InitializeInAppBilling
                        inAppBillingData = this@InitializeInAppBilling.inAppBillingData
                    }
                    oneTimePurchase!!.arguments = Bundle().apply {
                        putString(Entry.ItemToPurchase, intent.getStringExtra(Entry.ItemToPurchase))
                    }

                    supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, 0)
                            .replace(R.id.fragmentPlaceHolder, oneTimePurchase!!, "One Time Purchase")
                            .commit()
                }
                Entry.SubscriptionPurchase -> {
                    subscriptionPurchase = SubscriptionPurchase().apply {
                        purchaseFlowController = this@InitializeInAppBilling
                        inAppBillingData = this@InitializeInAppBilling.inAppBillingData
                    }
                    subscriptionPurchase!!.arguments = Bundle().apply {
                        putString(Entry.ItemToPurchase, intent.getStringExtra(Entry.ItemToPurchase))
                    }

                    supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, 0)
                            .replace(R.id.fragmentPlaceHolder, subscriptionPurchase!!, "One Time Purchase")
                            .commit()
                }
            }
        } else {

            this@InitializeInAppBilling.finish()
        }
    }

    override fun onBackPressed() {

        this@InitializeInAppBilling.finish()
    }

    override fun purchaseFlowInitial(billingResult: BillingResult?) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "${billingResult?.debugMessage}")

        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                functionsClass
                        .savePreference(".PurchasedItem",
                                intent.getStringExtra(Entry.ItemToPurchase),
                                true)

                this@InitializeInAppBilling.finish()
            }
        }
    }

    override fun purchaseFlowDisrupted(errorMessage: String?) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "Purchase Flow Disrupted: ${errorMessage}")

        oneTimePurchase?.let {
            supportFragmentManager
                    .beginTransaction()
                    .remove(it)
        }

        subscriptionPurchase?.let {
            supportFragmentManager
                    .beginTransaction()
                    .remove(it)
        }

        val snackbar = Snackbar.make(inAppBillingViewBinding.root,
                getString(R.string.purchaseFlowDisrupted),
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setBackgroundTint(getColor(R.color.default_color))
        snackbar.setTextColor(getColor(R.color.light))
        snackbar.setActionTextColor(getColor(R.color.default_color_game_light))
        snackbar.setAction(Html.fromHtml(getString(R.string.retry))) {

        }
        snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {

            override fun onDismissed(transientBottomBar: Snackbar?, transitionEvent: Int) {
                super.onDismissed(transientBottomBar, transitionEvent)

                startActivity(Intent(applicationContext, InitializeInAppBilling::class.java).apply {
                    putExtra(InitializeInAppBilling.Entry.PurchaseType, intent.getStringExtra(Entry.PurchaseType))
                    putExtra(InitializeInAppBilling.Entry.ItemToPurchase, intent.getStringExtra(Entry.ItemToPurchase))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())
            }
        })

        val snackbarView = snackbar.view
        val frameLayoutLayoutParams = snackbarView.layoutParams as FrameLayout.LayoutParams
        frameLayoutLayoutParams.gravity = Gravity.BOTTOM
        snackbarView.layoutParams = frameLayoutLayoutParams

        snackbar.show()
    }

    override fun purchaseFlowSucceeded(skuDetails: SkuDetails) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "Purchase Flow Succeeded: ${skuDetails}")

    }

    override fun purchaseFlowPaid(billingClient: BillingClient, purchase: Purchase) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "Purchase Flow Paid: ${purchase}")

        PurchasesCheckpoint.purchaseAcknowledgeProcess(billingClient, purchase, BillingClient.SkuType.INAPP)

        functionsClass
                .savePreference(".PurchasedItem",
                        purchase.sku,
                        true)

        this@InitializeInAppBilling.finish()
    }

    override fun purchaseFlowPaid(skuDetails: SkuDetails) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "Purchase Flow Paid: ${skuDetails}")

        functionsClass
                .savePreference(".PurchasedItem",
                        skuDetails.sku,
                        true)

        this@InitializeInAppBilling.finish()
    }
}