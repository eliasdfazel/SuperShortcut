package net.geekstools.supershortcuts.PRO.SecurityServices

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData

interface Protection {
    fun processProtected()
    fun processNotProtected()
}

class SecurityServicesProcess (val context: AppCompatActivity) {

    val functionsClass = FunctionsClass(context)

    fun protectIt(protection: Protection) {

        if (securityServicePurchased()) {

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(context, executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)

                        protection.processNotProtected()

                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)

                        protection.processProtected()

                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()

                        protection.processNotProtected()

                    }

                })

            biometricPrompt.authenticate(promptInfo)

        } else {

            context.startActivity(
                Intent(context, InitializeInAppBilling::class.java)
                .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.SubscriptionPurchase)
                .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemSecurityServices)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle())

        }

    }

    fun securityServiceEnabled() : Boolean {

        return functionsClass.readPreference("SecurityServices", "ProtectionEnabled", false)
    }

    fun securityServicePurchased() : Boolean {

        return functionsClass.readPreference("SecurityServices", "ProtectionPurchased", false)
    }

    fun switchSecurityServices(securityServicesSwitch: MaterialButton) {

        securityServicesSwitch.setOnClickListener {

            if (securityServicePurchased()) {

                securityServicesSwitch.backgroundTintList = if (securityServiceEnabled()) {

                    FunctionsClass(context).savePreference("SecurityServices", "ProtectionEnabled", false)

                    ColorStateList.valueOf(context.getColor(R.color.red))

                } else {

                    FunctionsClass(context).savePreference("SecurityServices", "ProtectionEnabled", true)

                    ColorStateList.valueOf(context.getColor(R.color.default_color_light))

                }

            } else {

                context.startActivity(
                    Intent(context, InitializeInAppBilling::class.java)
                        .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.SubscriptionPurchase)
                        .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemSecurityServices)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle())

            }

        }

    }

}