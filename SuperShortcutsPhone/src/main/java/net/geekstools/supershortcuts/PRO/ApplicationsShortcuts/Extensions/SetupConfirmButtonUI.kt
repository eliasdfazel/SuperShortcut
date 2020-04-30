/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 10:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions

import android.widget.RelativeLayout
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListXYZ
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButton

fun NormalAppShortcutsSelectionListXYZ.setupConfirmButtonUI(confirmButtonProcessInterface: ConfirmButtonProcessInterface) : AppsConfirmButton {

    val confirmButtonLayoutParams = RelativeLayout.LayoutParams(functionsClass.DpToInteger(63), functionsClass.DpToInteger(63))

    val appsConfirmButton = AppsConfirmButton(this@setupConfirmButtonUI, applicationContext,
            functionsClass,
            confirmButtonProcessInterface)

    appsConfirmButton.layoutParams = confirmButtonLayoutParams
    appsConfirmButton.bringToFront()

    normalAppSelectionBinding.confirmLayout.addView(appsConfirmButton)
    appsConfirmButton.setOnClickListener {

    }

    return appsConfirmButton
}