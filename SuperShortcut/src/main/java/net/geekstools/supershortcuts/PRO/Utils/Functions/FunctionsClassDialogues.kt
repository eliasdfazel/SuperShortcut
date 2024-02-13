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
import android.webkit.WebSettings
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.InAppReview.InAppReviewProcess
import net.geekstools.supershortcuts.PRO.databinding.DialogueMessageBinding

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

        val dialogueMessageBinding = DialogueMessageBinding.inflate(activity.layoutInflater)

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogueMessageBinding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialog.window?.attributes = layoutParams

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(activity.getColor(R.color.light))

        if (loadVideoTutorial) {
            dialogueMessageBinding.webViewTutorial.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            dialogueMessageBinding.webViewTutorial.settings.javaScriptEnabled = true

            dialogueMessageBinding.webViewTutorial.clearCache(true)
            dialogueMessageBinding.webViewTutorial.loadUrl("https://www.youtube.com/watch?v=84NgoS2ccs4&list=PLTs5v2BrWyWmEpqaArzs43ZRsMOleBNvw&index=1")

            dialogueMessageBinding.webViewTutorial.visibility = View.VISIBLE
            dialogueMessageBinding.dialogueTitle.visibility = View.INVISIBLE

            functionsClass.isFirstToCheckTutorial(true)
        }

        dialogueMessageBinding.dialogueTitle.text = activity.getString(R.string.whatsnew)
        dialogueMessageBinding.dialogueMessage.text = Html.fromHtml(activity.getString(R.string.changelog))

        dialogueMessageBinding.rateIt.setBackgroundColor(activity.getColor(R.color.default_color))
        dialogueMessageBinding.followIt.setBackgroundColor(activity.getColor(R.color.default_color_darker))

        dialogueMessageBinding.dialogueTitle.setTextColor(activity.getColor(R.color.darker))
        dialogueMessageBinding.dialogueMessage.setTextColor(activity.getColor(R.color.dark))

        dialogueMessageBinding.rateIt.setTextColor(activity.getColor(R.color.light))
        dialogueMessageBinding.followIt.setTextColor(activity.getColor(R.color.light))

        dialogueMessageBinding.rateIt.setOnClickListener {

            val shareText = activity.getString(R.string.shareTitle) +
                    "\n" + activity.getString(R.string.shareSummary) +
                    "\n" + activity.getString(R.string.play_store_link) + activity.packageName
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.putExtra(Intent.EXTRA_TEXT, shareText)
                this.type = "text/plain"
            }
            activity.startActivity(sharingIntent)

        }

        dialogueMessageBinding.followIt.setOnClickListener {
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

        val dialogueMessageBinding = DialogueMessageBinding.inflate(activity.layoutInflater)

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogueMessageBinding.root)
        dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(activity.getColor(R.color.light))

        dialogueMessageBinding.dialogueTitle.text = activity.getString(R.string.whatsnew)
        dialogueMessageBinding.dialogueMessage.text = Html.fromHtml(activity.getString(R.string.changelog))

        dialogueMessageBinding.rateIt.setBackgroundColor(activity.getColor(R.color.default_color))
        dialogueMessageBinding.followIt.setBackgroundColor(activity.getColor(R.color.default_color_darker))

        dialogueMessageBinding.dialogueTitle.setTextColor(activity.getColor(R.color.darker))
        dialogueMessageBinding.dialogueMessage.setTextColor(activity.getColor(R.color.dark))

        dialogueMessageBinding.rateIt.setTextColor(activity.getColor(R.color.light))
        dialogueMessageBinding.followIt.setTextColor(activity.getColor(R.color.light))

        dialogueMessageBinding.rateIt.setOnClickListener {

            val shareText = activity.getString(R.string.shareTitle) +
                    "\n" + activity.getString(R.string.shareSummary) +
                    "\n" + activity.getString(R.string.play_store_link) + activity.packageName
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.putExtra(Intent.EXTRA_TEXT, shareText)
                this.type = "text/plain"
            }
            activity.startActivity(sharingIntent)

        }

        dialogueMessageBinding.followIt.setOnClickListener {
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