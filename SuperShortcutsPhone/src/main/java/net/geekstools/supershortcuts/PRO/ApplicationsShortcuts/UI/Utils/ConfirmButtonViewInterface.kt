/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/5/20 5:41 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.Utils

import android.view.animation.Animation

interface ConfirmButtonViewInterface {
    fun makeItVisible()
    fun startCustomAnimation(animation: Animation?)
    fun setDismissBackground()
}