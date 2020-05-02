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