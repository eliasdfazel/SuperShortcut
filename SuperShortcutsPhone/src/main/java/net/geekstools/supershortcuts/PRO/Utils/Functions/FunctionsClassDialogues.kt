/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/22/21 10:06 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.Functions

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.dialogue_message.*
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.InAppReview.InAppReviewProcess

class FunctionsClassDialogues (var activity: AppCompatActivity, var functionsClass: FunctionsClass) {

    fun changeLog(loadVideoTutorial: Boolean, forceShow: Boolean) {

        val layoutParams = WindowManager.LayoutParams()
        val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370f, activity.resources.displayMetrics).toInt()
        val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 430f, activity.resources.displayMetrics).toInt()

        layoutParams.width = dialogueWidth
        layoutParams.height = dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_message)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialog.window?.attributes = layoutParams

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(activity.getColor(R.color.light))

        if (loadVideoTutorial) {
            dialog.webViewTutorial.settings.setAppCacheEnabled(false)
            dialog.webViewTutorial.settings.javaScriptEnabled = true

            dialog.webViewTutorial.clearCache(true)
            dialog.webViewTutorial.loadUrl("https://geeksempire.net/Projects/Android/SuperShortcuts/OverviewVideo.html")

            dialog.webViewTutorial.visibility = View.VISIBLE
            dialog.dialogueTitle.visibility = View.INVISIBLE

            functionsClass.isFirstToCheckTutorial(true)
        }

        dialog.dialogueTitle.text = activity.getString(R.string.whatsnew)
        dialog.dialogueMessage.text = Html.fromHtml(activity.getString(R.string.changelog))

        dialog.rateIt.setBackgroundColor(activity.getColor(R.color.default_color))
        dialog.followIt.setBackgroundColor(activity.getColor(R.color.default_color_darker))

        dialog.dialogueTitle.setTextColor(activity.getColor(R.color.darker))
        dialog.dialogueMessage.setTextColor(activity.getColor(R.color.dark))

        dialog.rateIt.setTextColor(activity.getColor(R.color.light))
        dialog.followIt.setTextColor(activity.getColor(R.color.light))

        dialog.rateIt.setOnClickListener {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.play_store_link))))

            dialog.dismiss()
        }

        dialog.followIt.setOnClickListener {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.link_facebook_app))))

            dialog.dismiss()
        }

        dialog.setOnDismissListener {

            if (functionsClass.appVersionCode(activity.packageName) > functionsClass.readFile(".Updated")?.toInt()?:0) {

                if (!activity.isFinishing) {

                    InAppReviewProcess(activity as AppCompatActivity).start()

                }

            }

            functionsClass.saveFile(".Updated", functionsClass.appVersionCode(activity.packageName).toString())

        }

        if (!activity.getFileStreamPath(".Updated").exists()) {

            if (!activity.isFinishing) {
                dialog.show()
            }

        } else if (functionsClass.appVersionCode(activity.packageName) > functionsClass.readFile(".Updated")?.toInt()?:0) {

            if (!activity.isFinishing) {
                dialog.show()
            }

        } else if (forceShow) {

            if (!activity.isFinishing) {
                dialog.show()
            }
        }
    }

    fun changeLogPreference(betaChangeLog: String, betaVersionCode: String) {
        val layoutParams = WindowManager.LayoutParams()
        val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370f, activity.resources.displayMetrics).toInt()
        val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 430f, activity.resources.displayMetrics).toInt()

        layoutParams.width = dialogueWidth
        layoutParams.height = dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_message)
        dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(activity.getColor(R.color.light))

        dialog.dialogueTitle.text = activity.getString(R.string.whatsnew)
        dialog.dialogueMessage.text = Html.fromHtml(activity.getString(R.string.changelog))

        dialog.rateIt.setBackgroundColor(activity.getColor(R.color.default_color))
        dialog.followIt.setBackgroundColor(activity.getColor(R.color.default_color_darker))

        dialog.dialogueTitle.setTextColor(activity.getColor(R.color.darker))
        dialog.dialogueMessage.setTextColor(activity.getColor(R.color.dark))

        dialog.rateIt.setTextColor(activity.getColor(R.color.light))
        dialog.followIt.setTextColor(activity.getColor(R.color.light))

        dialog.rateIt.text = if (betaChangeLog.contains(activity.packageName)) {
            activity.getString(R.string.shareIt)
        } else {
            activity.getString(R.string.betaUpdate)
        }
        dialog.rateIt.setOnClickListener {
            dialog.dismiss()

            if (dialog.rateIt.text == activity.getString(R.string.shareIt)) {
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.play_store_link).toString() + activity.getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } else if (dialog.rateIt.text == activity.getString(R.string.betaUpdate)) {
                functionsClass.upcomingChangeLog(activity, betaChangeLog, betaVersionCode)
            }
        }

        dialog.followIt.setOnClickListener {
            dialog.dismiss()

            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.link_facebook_app))))
        }

        dialog.setOnDismissListener {
            functionsClass.saveFile(".Updated", functionsClass.appVersionCode(activity.packageName).toString())
        }

        if (!activity.isFinishing) {
            dialog.show()
        }
    }
}