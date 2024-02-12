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

class InAppBillingData {

    object SKU {
        val SKUS = HashMap<String, ArrayList<String>>()

        /**
         * One Time Purchase: Sku for Donation - Sku: donation
         **/
        const val InAppItemDonation = "donation"

        /**
         * One Time Purchase: Sku for Mix Shortcuts - Sku: mix.shortcuts
         **/
        const val InAppItemMixShortcuts = "mix.shortcuts"

        /**
         * Subscription Purchase: Sku for Security Services - Sku: security.services
         **/
        const val InAppItemSecurityServices = "security.services"
    }

}