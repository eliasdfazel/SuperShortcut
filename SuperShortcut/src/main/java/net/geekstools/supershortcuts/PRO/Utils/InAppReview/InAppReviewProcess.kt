/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/16/20 2:26 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppReview

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import net.geekstools.supershortcuts.PRO.BuildConfig

class InAppReviewProcess (private val context: AppCompatActivity) {

    private val reviewManager = ReviewManagerFactory.create(context)

    private val fakeReviewManager = FakeReviewManager(context)

    fun start() {

        val requestReviewFlow = if (BuildConfig.DEBUG) {
            fakeReviewManager.requestReviewFlow()
        } else {
            reviewManager.requestReviewFlow()
        }

        requestReviewFlow.addOnCompleteListener { request ->

            if (request.isSuccessful) {

                val reviewInfo = request.result

                val reviewFlow = if (BuildConfig.DEBUG) {
                    fakeReviewManager.launchReviewFlow(context, reviewInfo)
                } else {
                    reviewManager.launchReviewFlow(context, reviewInfo)
                }

                reviewFlow.addOnCompleteListener {

                    if (it.isSuccessful) {

                    } else {

                    }

                }

            } else {
                Log.d(this@InAppReviewProcess.javaClass.simpleName, "In Application Review Process Error")



            }

        }

    }

}