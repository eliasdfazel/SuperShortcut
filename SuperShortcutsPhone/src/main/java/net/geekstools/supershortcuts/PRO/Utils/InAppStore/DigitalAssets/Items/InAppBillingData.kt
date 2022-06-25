/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 11:28 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items

import com.android.billingclient.api.BillingClient

class InAppBillingData {

    object SKU {
        val SKUS = HashMap<String, ArrayList<String>>()

        /**
         * One Time Purchase: Sku for Donation - Sku: donation
         **/
        const val InAppItemDonation = "donation"
        /**
         * One Time Purchase: Sku for Floating Widgets - Sku: floating.widgets
         **/
        const val InAppItemMixShortcuts = "mix.shortcuts"
    }

    init {
        SKU.SKUS[BillingClient.ProductType.INAPP] = arrayListOf(SKU.InAppItemDonation, SKU.InAppItemMixShortcuts)
    }

    /**
     * BillingClient.SkuType.INAPP
     * OR
     * BillingClient.SkuType.SUBS
     **/
    fun getAllSkusByType(@BillingClient.ProductType skuType: String) : ArrayList<String>? {

        return SKU.SKUS[skuType]
    }
}