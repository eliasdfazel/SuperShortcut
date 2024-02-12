/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 5:30 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.MixShortcuts

import android.app.ActivityOptions
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.view.View
import com.google.android.material.button.MaterialButton
import net.geekstools.supershortcuts.PRO.EntryConfigurations
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData


class MixShortcutsProcess(private val context: Context,
                          private val mixShortcutsSwitchView: MaterialButton) {

    private val functionsClass: FunctionsClass = FunctionsClass(context)

    fun initialize() {

        if(functionsClass.UsageAccessEnabled()) {

            mixShortcutsSwitchView.visibility = View.GONE

        } else {

            if (functionsClass.mixShortcutsPurchased()) {
                if (functionsClass.mixShortcuts()) {

                    mixShortcutsSwitchView.iconTint = ColorStateList.valueOf(context.getColor(R.color.default_color_light))

                } else {

                    mixShortcutsSwitchView.iconTint = ColorStateList.valueOf(context.getColor(R.color.darker))

                }
            } else {

                mixShortcutsSwitchView.iconTint = ColorStateList.valueOf(context.getColor(R.color.red))

            }

            mixShortcutsSwitchView.setOnClickListener {

                trigger()
            }
        }
    }

    fun trigger() {

        if (functionsClass.mixShortcutsPurchased()) {

            functionsClass.deleteSelectedFiles()

            val sharedPreferences: SharedPreferences = context.getSharedPreferences("mix", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            if (sharedPreferences.getBoolean("mixShortcuts", false)) {
                editor.putBoolean("mixShortcuts", false)
                editor.apply()
            } else if (!sharedPreferences.getBoolean("mixShortcuts", false)) {
                editor.putBoolean("mixShortcuts", true)
                editor.apply()
            }

            Intent(context, EntryConfigurations::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this@apply, ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            }

        } else {

            context.startActivity(Intent(context, InitializeInAppBilling::class.java).apply {
                putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemMixShortcuts)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

        }
    }
}