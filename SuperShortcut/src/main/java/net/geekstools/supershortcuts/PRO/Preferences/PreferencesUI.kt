/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 10/5/21, 6:40 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Preferences

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryPurchasesAsync
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.async
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts
import net.geekstools.supershortcuts.PRO.Preferences.Adapter.CustomIconsThemeAdapter
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.CustomIconInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout
import net.geekstools.supershortcuts.PRO.databinding.PreferenceViewBinding

class PreferencesUI : AppCompatActivity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    lateinit var preferenceViewBinding: PreferenceViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceViewBinding = PreferenceViewBinding.inflate(layoutInflater)
        setContentView(preferenceViewBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        FunctionsClassDialogues(this@PreferencesUI, functionsClass).changeLog(false)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.default_color_darker)))
        supportActionBar?.title = Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + getString(R.string.pref) + "</font>", Html.FROM_HTML_MODE_LEGACY)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())

            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.hide(WindowInsetsCompat.Type.navigationBars())

            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        if (!functionsClass.mixShortcutsPurchased()) {

            val billingClient = BillingClient.newBuilder(this@PreferencesUI).setListener { billingResult, purchases ->

            }.enablePendingPurchases(PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()).build()

            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        functionsClass.savePreference(".PurchasedItem", "mix.shortcuts", false)

                        lifecycleScope.async {

                            val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build()

                            billingClient.queryPurchasesAsync(queryPurchasesParams).purchasesList.let { purchases ->

                                for (purchase in purchases) {

                                    functionsClass.savePreference(".PurchasedItem", purchase.products.first(), true)

                                }

                            }

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

                this@PreferencesUI.finish()

            } else if (!sharedPreferences.getBoolean("smartPick", false)) {

                functionsClass.UsageAccess(this@PreferencesUI, preferenceViewBinding.prefSwitch)

            }
        }

        preferenceViewBinding.splitView.setOnClickListener {

            if (!functionsClass.AccessibilityServiceEnabled()) {

                functionsClass.AccessibilityService(this@PreferencesUI, false)

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

            FunctionsClassDialogues(this@PreferencesUI, functionsClass).changeLog(true)

        }

        preferenceViewBinding.newsView.setOnLongClickListener {

            FunctionsClassDialogues(this@PreferencesUI, functionsClass).changeLog(true)

            true
        }

        preferenceViewBinding.supportView.setOnClickListener {

            val a = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_contacts)))
            a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(a)
        }

        preferenceViewBinding.share.setOnClickListener {
            shareSuperShortcuts()
        }

        preferenceViewBinding.twitter.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_twitter))))

        }

        preferenceViewBinding.facebook.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook))))

        }

        preferenceViewBinding.customIconView.setOnClickListener {

            val layoutParams = WindowManager.LayoutParams()

            layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 313f, resources.displayMetrics).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.windowAnimations = android.R.style.Animation_Dialog

            val dialog = Dialog(this@PreferencesUI)
            dialog.setContentView(R.layout.custom_icons)
            dialog.setTitle(Html.fromHtml("<font color='" + getColor(R.color.dark) + "'>" + getString(R.string.customIconTitle) + "</font>", Html.FROM_HTML_MODE_LEGACY))
            dialog.window!!.attributes = layoutParams
            dialog.window!!.decorView.setBackgroundColor(getColor(R.color.light))
            dialog.setCancelable(true)

            val defaultTheme = dialog.findViewById<View>(R.id.setDefault) as TextView
            val customIconList = dialog.findViewById<View>(R.id.customIconList) as RecyclerView

            val recyclerViewLayoutManager = RecycleViewSmoothLayout(applicationContext, OrientationHelper.VERTICAL, false)
            customIconList.layoutManager = recyclerViewLayoutManager
            customIconList.removeAllViews()

            val adapterItemsData = ArrayList<AdapterItemsData>()
            adapterItemsData.clear()

            PublicVariable.customIconsPackages.forEach { customIconsPackage ->

                adapterItemsData.add(AdapterItemsData(
                        functionsClass.appName(customIconsPackage),
                        customIconsPackage,
                        functionsClass.appIconDrawable(customIconsPackage)
                ))

            }

            val customIconsThemeAdapter = CustomIconsThemeAdapter(applicationContext, adapterItemsData, functionsClass, object : CustomIconInterface {

                override fun customIconPackageSelected(selectedPackageName: String) {

                    preferenceViewBinding.customIconIcon.setImageDrawable(if (functionsClass.customIconsEnable()) functionsClass.appIconDrawable(functionsClass.readDefaultPreference("customIcon", packageName)) else getDrawable(R.drawable.draw_pref_custom_icon))
                    preferenceViewBinding.customIconDesc.text = if (functionsClass.customIconsEnable()) functionsClass.appName(functionsClass.readDefaultPreference("customIcon", packageName)) else getString(R.string.customIconDesc)

                    if (functionsClass.customIconsEnable()) {
                        if (functionsClass.mixShortcuts()) {
                            functionsClass.addMixAppShortcutsCustomIconsPref()
                        } else if (functionsClass.AppShortcutsMode() == "AppShortcuts") {
                            functionsClass.addAppShortcutsCustomIconsPref()
                        } else if (functionsClass.AppShortcutsMode() == "SplitShortcuts") {
                            functionsClass.addAppsShortcutSplitCustomIconsPref()
                        } else if (functionsClass.AppShortcutsMode() == "CategoryShortcuts") {
                            functionsClass.addAppsShortcutCategoryCustomIconsPref()
                        }
                    }

                    dialog.dismiss()

                }
            })
            customIconList.adapter = customIconsThemeAdapter

            defaultTheme.setOnClickListener {
                functionsClass.saveDefaultPreference("customIcon", packageName)
                preferenceViewBinding.customIconIcon.setImageDrawable(getDrawable(R.drawable.draw_pref_custom_icon))
                dialog.dismiss()
            }

            dialog.setOnDismissListener {

                adapterItemsData.clear()

            }
            dialog.show()

        }

    }

    override fun onResume() {
        super.onResume()

        preferenceViewBinding.prefTitlefloating.text = Html.fromHtml(getString(R.string.adApp), Html.FROM_HTML_MODE_LEGACY)
        preferenceViewBinding.prefDescfloating.text = Html.fromHtml(getString(R.string.adAppSummary), Html.FROM_HTML_MODE_LEGACY)

        Glide.with(applicationContext)
            .load(getDrawable(R.drawable.arwen_ai_icon))
            .transform(RoundedCorners(99))
            .into(preferenceViewBinding.prefIconfloating)

        preferenceViewBinding.floatingView.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_ad_app)))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        }

        preferenceViewBinding.prefIconNews.setImageDrawable(getDrawable(R.drawable.ic_launcher))
        preferenceViewBinding.customIconDesc.text = if (functionsClass.customIconsEnable()) {
            functionsClass.appName(functionsClass.readDefaultPreference("customIcon", packageName))
        } else {
            getString(R.string.customIconDesc)
        }

        val editor = getSharedPreferences("smart", Context.MODE_PRIVATE).edit()

        if (functionsClass.UsageAccessEnabled()) {
            preferenceViewBinding.prefSwitch.isChecked = true
            editor.putBoolean("smartPick", true)
            editor.apply()
        } else {
            preferenceViewBinding.prefSwitch.isChecked = false
            editor.putBoolean("smartPick", false)
            editor.apply()
        }

        preferenceViewBinding.splitSwitch.isChecked = functionsClass.AccessibilityServiceEnabled()

        if (getSharedPreferences("mix", Context.MODE_PRIVATE).getBoolean("mixShortcuts", false)) {
            preferenceViewBinding.mixSwitch.isChecked = true
        } else if (!getSharedPreferences("mix", Context.MODE_PRIVATE).getBoolean("mixShortcuts", false)) {
            preferenceViewBinding.mixSwitch.isChecked = false
        }

        preferenceViewBinding.customIconIcon.setImageDrawable(if (functionsClass.customIconsEnable()) functionsClass.appIconDrawable(functionsClass.readDefaultPreference("customIcon", packageName)) else getDrawable(R.drawable.draw_pref_custom_icon))
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val tabView = getSharedPreferences("ShortcutsModeView", Context.MODE_PRIVATE).getString("TabsView", NormalAppShortcutsSelectionListPhone::class.java.simpleName)
        if (tabView == NormalAppShortcutsSelectionListPhone::class.java.simpleName) {
            startActivity(Intent(applicationContext, NormalAppShortcutsSelectionListPhone::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, R.anim.go_up).toBundle())
        } else if (tabView == SplitShortcuts::class.java.simpleName) {
            startActivity(Intent(applicationContext, SplitShortcuts::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, R.anim.go_up).toBundle())
        } else if (tabView == FolderShortcuts::class.java.simpleName) {
            startActivity(Intent(applicationContext, FolderShortcuts::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, R.anim.go_up).toBundle())
        } else {
            startActivity(Intent(applicationContext, NormalAppShortcutsSelectionListPhone::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, R.anim.go_up).toBundle())
        }

        this@PreferencesUI.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menu?.let {

            val inflater = menuInflater
            inflater.inflate(R.menu.preferences_menu, menu)

        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.facebook -> {

                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app))))

            }
            R.id.donate -> {

                startActivity(Intent(applicationContext, InitializeInAppBilling::class.java)
                        .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                        .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemDonation)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        , ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())

            }
            android.R.id.home -> {

                val tabView = getSharedPreferences("ShortcutsModeView", Context.MODE_PRIVATE).getString("TabsView", NormalAppShortcutsSelectionListPhone::class.java.simpleName)

                if (tabView == NormalAppShortcutsSelectionListPhone::class.java.simpleName) {
                    startActivity(Intent(applicationContext, NormalAppShortcutsSelectionListPhone::class.java),
                            ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, R.anim.go_up).toBundle())
                } else if (tabView == SplitShortcuts::class.java.simpleName) {
                    startActivity(Intent(applicationContext, SplitShortcuts::class.java),
                            ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, R.anim.go_up).toBundle())
                } else if (tabView == FolderShortcuts::class.java.simpleName) {
                    startActivity(Intent(applicationContext, FolderShortcuts::class.java),
                            ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, R.anim.go_up).toBundle())
                } else {
                    startActivity(Intent(applicationContext, NormalAppShortcutsSelectionListPhone::class.java),
                            ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, R.anim.go_up).toBundle())
                }

                this@PreferencesUI.finish()
            }
        }

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