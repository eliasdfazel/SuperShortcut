/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 10:19 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.UI.Gesture

import android.view.MotionEvent

interface GestureListenerInterface {
    fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {}

    fun onSingleTapUp(motionEvent: MotionEvent) {}
    fun onLongPress(motionEvent: MotionEvent) {}
}