/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/29/20 1:59 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.Factory

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable

/**
 * @param rootPaddingTop Set Padding For Root View Group
 *
 * @param rootPaddingBottom Set Padding For Root View Group
 *
 * @param rootPaddingStart Set Padding For Root View Group
 *
 * @param rootPaddingEnd Set Padding For Root View Group
 *
 *
 *
 * @param popupEnable Enable Popup View Of Index Text
 *
 * @param popupVerticalOffset Set Integer Number Of Offset For Popup View Of Index Text. It Will Automatically Convert It To DP. Default Value Is 7dp.
 *
 * @param popupHorizontalOffset Set Integer Number Of Offset For Popup View Of Index Text. It Will Automatically Convert It To DP. Default Value Is 7dp.
 *
 * @param popupTextColor Set Color Of Text For Popup View.
 *
 * @param popupBackgroundShape Set A Drawable As Background Of Popup View Of Index Text.
 *
 *
 *
 * @param indexItemTextColor Set Color Of Text For Popup View.
 **/
data class IndexedFastScrollerFactory(
        /**
         * Set Padding For Root View Group
         **/
        var rootPaddingTop: Int = 0,
        /**
         * Set Padding For Root View Group
         **/
        var rootPaddingBottom: Int = 0,
        /**
         * Set Padding For Root View Group
         **/
        var rootPaddingStart: Int = 0,
        /**
         * Set Padding For Root View Group
         **/
        var rootPaddingEnd: Int = 0,



        /**
         * Enable Popup View Of Index Text.
         **/
        var popupEnable: Boolean = true,
        /**
         * Set Integer Number Of Offset For Popup View Of Index Text.
         * It Will Automatically Convert It To DP.
         *
         * Default Value Is 7dp.
         **/
        var popupVerticalOffset: Float = 7F,
        /**
         * Set Integer Number Of Offset For Popup View Of Index Text.
         * It Will Automatically Convert It To DP.
         *
         * Default Value Is 1dp.
         **/
        var popupHorizontalOffset: Float = 1F,
        /**
         * Set Color Of Text For Popup View.
         **/
        var popupTextColor: Int = Color.WHITE,
        /**
         * Set Typeface (Font) Of Text For Popup View.
         **/
        var popupTextFont: Typeface = Typeface.MONOSPACE,
        /**
         * Set Size Of Text For Popup View.
         **/
        var popupTextSize: Float = 29F,
        /**
         * Set A Drawable As Background Of Popup View Of Index Text.
         **/
        var popupBackgroundShape: Drawable? = null,
        /**
         * Set Tint Of Background Of Popup View Of Index Text.
         **/
        var popupBackgroundTint: Int = Color.BLACK,



        /**
         * Set Color Of Text For Item In Index View.
         **/
        var indexItemTextColor: Int = Color.WHITE,
        /**
         * Set Typeface (Font) Of Text For Item In Index View.
         **/
        var indexItemFont: Typeface = Typeface.MONOSPACE,
        /**
         * Set Size Of Text For Item In Index View.
         **/
        var indexItemSize: Float = 13F
)