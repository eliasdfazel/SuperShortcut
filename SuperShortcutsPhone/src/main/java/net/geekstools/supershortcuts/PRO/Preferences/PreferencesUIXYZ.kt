/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 6/10/20 7:44 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Preferences

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import net.geekstools.supershortcuts.PRO.BuildConfig
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.supershortcuts.PRO.databinding.PreferenceViewBinding

class PreferencesUIXYZ : AppCompatActivity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    lateinit var preferenceViewBinding: PreferenceViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceViewBinding = PreferenceViewBinding.inflate(layoutInflater)
        setContentView(preferenceViewBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        FunctionsClassDialogues(this@PreferencesUIXYZ, functionsClass).changeLog(!functionsClass.isFirstToCheckTutorial)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.default_color_darker)))
        supportActionBar?.title = Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + getString(R.string.pref) + "</font>", Html.FROM_HTML_MODE_LEGACY)
        supportActionBar?.subtitle = Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + BuildConfig.VERSION_NAME + "</font></small>", Html.FROM_HTML_MODE_LEGACY)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.default_color_darker)
        window.navigationBarColor = getColor(R.color.light)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        if (!functionsClass.mixShortcutsPurchased()) {

            val billingClient = BillingClient.newBuilder(this@PreferencesUIXYZ).setListener { billingResult, purchases ->

            }.enablePendingPurchases().build()

            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        functionsClass.savePreference(".PurchasedItem", "mix.shortcuts", false)

                        val purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList
                        for (purchase in purchases) {
                            FunctionsClassDebug.PrintDebug("*** Purchased Item: $purchase ***")

                            functionsClass.savePreference(".PurchasedItem", purchase.sku, true)
                        }

                    }

                }

                override fun onBillingServiceDisconnected() {

                }

            })

        }
    }

    override fun onStart() {
        super.onStart()


        preferenceViewBinding.smartView.setOnClickListener {

            val sharedPreferences = getSharedPreferences("smart", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            if (sharedPreferences.getBoolean("smartPick", false)) {

                preferenceViewBinding.prefSwitch.isChecked = false

                editor.putBoolean("smartPick", false)
                editor.apply()

                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)

                this@PreferencesUIXYZ.finish()

            } else if (!sharedPreferences.getBoolean("smartPick", false)) {

                functionsClass.UsageAccess(this@PreferencesUIXYZ, preferenceViewBinding.prefSwitch)

            }
        }

        preferenceViewBinding.splitView.setOnClickListener {

            if (!functionsClass.AccessibilityServiceEnabled()) {

                functionsClass.AccessibilityService(this@PreferencesUIXYZ, false)

            } else {

                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
        }

        preferenceViewBinding.mixView.setOnClickListener {
            if (functionsClass.mixShortcutsPurchased()) {
                functionsClass.deleteSelectedFiles()

                val sharedPreferences = getSharedPreferences("mix", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                if (sharedPreferences.getBoolean("mixShortcuts", false)) {

                    preferenceViewBinding.mixSwitch.isChecked = false

                    editor.putBoolean("mixShortcuts", false)
                    editor.apply()

                } else if (!sharedPreferences.getBoolean("mixShortcuts", false)) {

                    preferenceViewBinding.mixSwitch.isChecked = true

                    editor.putBoolean("mixShortcuts", true)
                    editor.apply()

                }

            } else {

                startActivity(Intent(applicationContext, InitializeInAppBilling::class.java)
                        .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                        .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemMixShortcuts)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())

            }
        }
    }

    override fun onResume() {
        super.onResume()

        preferenceViewBinding.prefIconNews.setImageDrawable(getDrawable(R.drawable.ic_launcher))
        preferenceViewBinding.customIconDesc.text = if (functionsClass.customIconsEnable()) {
            functionsClass.appName(functionsClass.readDefaultPreference("customIcon", packageName))
        } else {
            getString(R.string.customIconDesc)
        }
    }

    override fun onBackPressed() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun shareSuperShortcuts() {

        val shareText = "${getString(R.string.invitation_title)}\n${getString(R.string.invitation_message)}\n${getString(R.string.play_store_link)}${packageName}"

        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"

            startActivity(this@apply)
        }

    }
}