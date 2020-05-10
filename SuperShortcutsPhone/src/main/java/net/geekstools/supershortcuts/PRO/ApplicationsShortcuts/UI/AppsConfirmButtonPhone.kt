/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 9:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatButton
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable
import net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface.ConfirmButtonViewInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.Gesture.SwipeGestureListener

class AppsConfirmButtonPhone : AppCompatButton, GestureListenerInterface,
        ConfirmButtonViewInterface {

    private lateinit var activity: Activity

    lateinit var functionsClass: FunctionsClass

    private lateinit var confirmButtonProcessInterface: ConfirmButtonProcessInterface

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(context, this@AppsConfirmButtonPhone)
    }

    private lateinit var dismissDrawable: LayerDrawable

    constructor(activity: Activity, context: Context,
                functionsClass: FunctionsClass,
                confirmButtonProcessInterface: ConfirmButtonProcessInterface) : super(context) {

        this.activity = activity


        this.functionsClass = functionsClass
        this.confirmButtonProcessInterface = confirmButtonProcessInterface

        initializeConfirmButton()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        initializeConfirmButton()
    }

    constructor(context: Context) : super(context) {

        initializeConfirmButton()
    }

    private fun initializeConfirmButton() {
        swipeGestureListener.swipeMinDistance = 100

        dismissDrawable = context.getDrawable(R.drawable.draw_saved_dismiss) as LayerDrawable
        val backgroundTemporary = dismissDrawable.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(context.getColor(R.color.default_color_darker))

        PublicVariable.confirmButtonX = this.x
        PublicVariable.confirmButtonY = this.y
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        swipeGestureListener.onTouchEvent(motionEvent)

        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)

        when (gestureConstants) {
            is GestureConstants.SwipeHorizontal -> {
                when (gestureConstants.horizontalDirection) {
                    GestureListenerConstants.SWIPE_RIGHT -> {
                        confirmButtonProcessInterface.showSavedShortcutList()

                        Handler().postDelayed({
                            if (functionsClass.countLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile) > 0) {
                                this@AppsConfirmButtonPhone.background = context.getDrawable(R.drawable.draw_saved_dismiss)
                            }
                        }, 200)
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {
                        confirmButtonProcessInterface.showSavedShortcutList()

                        Handler().postDelayed({
                            if (functionsClass.countLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile) > 0) {
                                this@AppsConfirmButtonPhone.background = context.getDrawable(R.drawable.draw_saved_dismiss)
                            }
                        }, 200)
                    }
                }
            }
            is GestureConstants.SwipeVertical -> {
                when (gestureConstants.verticallDirection) {
                    GestureListenerConstants.SWIPE_UP -> {
                        confirmButtonProcessInterface.showSavedShortcutList()

                        Handler().postDelayed({
                            if (functionsClass.countLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile) > 0) {
                                this@AppsConfirmButtonPhone.background = context.getDrawable(R.drawable.draw_saved_dismiss)
                            }
                        }, 200)
                    }
                    GestureListenerConstants.SWIPE_DOWN -> {

                    }
                }
            }
        }
    }

    override fun onSingleTapUp(motionEvent: MotionEvent) {
        super.onSingleTapUp(motionEvent)

        if (functionsClass.mixShortcuts()) {

            functionsClass.addMixAppShortcuts()

        } else {

            functionsClass.addAppShortcuts()

            context.getSharedPreferences(".PopupShortcut", Context.MODE_PRIVATE).edit().apply {
                putString("PopupShortcutMode", "AppShortcuts")
                apply()
            }
        }
    }

    override fun onLongPress(motionEvent: MotionEvent) {
        super.onLongPress(motionEvent)

        functionsClass.deleteSelectedFiles()

        confirmButtonProcessInterface.shortcutDeleted()

        confirmButtonProcessInterface.savedShortcutCounter()

        confirmButtonProcessInterface.reevaluateShortcutsInfo()

        functionsClass.clearDynamicShortcuts()
    }

    /*ConfirmButtonViewInterface*/
    override fun makeItVisible() {
        val confirmDrawable = context.getDrawable(R.drawable.ripple_effect_confirm) as LayerDrawable
        this@AppsConfirmButtonPhone.background = confirmDrawable

        if (!this@AppsConfirmButtonPhone.isShown) {
            this@AppsConfirmButtonPhone.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            this@AppsConfirmButtonPhone.visibility = View.VISIBLE
        }
    }

    override fun startCustomAnimation(animation: Animation?) {
        if (animation == null) {

            this@AppsConfirmButtonPhone.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_confirm_button))

        } else {

            this@AppsConfirmButtonPhone.startAnimation(animation)

        }
    }

    override fun setDismissBackground() {
        val drawDismiss = context.getDrawable(R.drawable.draw_saved_dismiss) as LayerDrawable
        val backgroundTemporary: Drawable = drawDismiss.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(context.getColor(R.color.default_color_darker))

        this@AppsConfirmButtonPhone.background = drawDismiss
    }
    /*ConfirmButtonViewInterface*/
}