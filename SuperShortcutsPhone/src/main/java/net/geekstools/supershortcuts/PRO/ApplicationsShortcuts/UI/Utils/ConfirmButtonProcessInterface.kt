/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 11:21 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.Utils

interface ConfirmButtonProcessInterface {
    /**
     * Check Increase/Decrease In Counter Of Saved Shortcuts
     **/
    fun savedShortcutCounter()
    /**
     * Show Popup List Of Saved Shortcut
     **/
    fun showSavedShortcutList()
    /**
     * Hide Popup List Of Saved Shortcut
     **/
    fun hideSavedShortcutList()
    /**
     * Reload Apps List After Shortcut Deleted
     **/
    fun shortcutDeleted()

    /**
     * Reevaluate Popup Dynamic Shortcuts Information
     **/
    fun reevaluateShortcutsInfo()
}