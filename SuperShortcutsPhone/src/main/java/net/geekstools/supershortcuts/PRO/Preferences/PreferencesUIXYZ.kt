/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 6/10/20 12:26 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Preferences

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.*
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
import java.util.*

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

        preferenceViewBinding.newsView.setOnClickListener {

            FunctionsClassDialogues(this@PreferencesUIXYZ, functionsClass).changeLog(false)

        }

        preferenceViewBinding.newsView.setOnLongClickListener {

            FunctionsClassDialogues(this@PreferencesUIXYZ, functionsClass).changeLog(true)

            true
        }






        preferenceViewBinding.supportView.setOnClickListener {

            val contactOption = arrayOf<String>(
                    "Send an Email",
                    "Send a Message",
                    "Contact via Forum",
                    "Join Beta Program",
                    "Rate & Write Review")

            val alertBuilder = AlertDialog.Builder(this@PreferencesUIXYZ, R.style.GeeksEmpire_Dialogue_Light)
            alertBuilder.setTitle(getString(R.string.supportTitle))
            alertBuilder.setSingleChoiceItems(contactOption, 0, null)
            alertBuilder.setPositiveButton(android.R.string.ok) { dialog, whichButton ->

                val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition

                if (selectedPosition == 0) {

                    val emailEssentialContent = "[Essential Information] ${functionsClass.deviceName} | API ${Build.VERSION.SDK_INT} | ${functionsClass.countryIso.toUpperCase(Locale.ROOT)}"

                    val email = Intent(Intent.ACTION_SEND)
                    email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                    email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_tag) + " [" + functionsClass.appVersionName(packageName) + "] ")
                    email.putExtra(Intent.EXTRA_TEXT, emailEssentialContent)
                    email.type = "message/*"
                    email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(Intent.createChooser(email, getString(R.string.feedback_tag)))

                } else if (selectedPosition == 1) {

                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app))))

                } else if (selectedPosition == 2) {

                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_xda))))

                } else if (selectedPosition == 3) {

                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_alpha))))

                    functionsClass.Toast(getString(R.string.alphaTitle), Gravity.BOTTOM)

                } else if (selectedPosition == 4) {

                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_link) + packageName)))

                }
            }
            alertBuilder.setNegativeButton(android.R.string.cancel) { dialog, whichButton ->

                dialog.dismiss()
            }
            alertBuilder.show()
        }

        preferenceViewBinding.translatorView.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_xda_translator))))

        }

        preferenceViewBinding.floatingView.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_floating_shortcuts))))

        }

        preferenceViewBinding.share.setOnClickListener {
            shareSuperShortcuts()
        }

        preferenceViewBinding.rate.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_link) + packageName)))

        }

        preferenceViewBinding.twitter.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_twitter))))

        }

        preferenceViewBinding.facebook.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook))))

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