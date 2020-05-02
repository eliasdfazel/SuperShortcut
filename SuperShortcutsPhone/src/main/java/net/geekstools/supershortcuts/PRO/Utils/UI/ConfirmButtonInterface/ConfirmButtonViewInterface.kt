/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 11:42 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface

import android.view.animation.Animation

interface ConfirmButtonViewInterface {
    fun makeItVisible()
    fun startCustomAnimation(animation: Animation?)
    fun setDismissBackground()
}