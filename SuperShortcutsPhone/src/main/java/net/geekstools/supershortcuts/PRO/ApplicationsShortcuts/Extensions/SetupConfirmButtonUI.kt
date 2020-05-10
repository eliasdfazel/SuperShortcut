/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 9:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions

import android.widget.RelativeLayout
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButtonPhone
import net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface.ConfirmButtonProcessInterface

fun NormalAppShortcutsSelectionListPhone.setupConfirmButtonUI(confirmButtonProcessInterface: ConfirmButtonProcessInterface) : AppsConfirmButtonPhone {

    val confirmButtonLayoutParams = RelativeLayout.LayoutParams(
            functionsClass.DpToInteger(73),
            functionsClass.DpToInteger(73)
    )

    val appsConfirmButton = AppsConfirmButtonPhone(this@setupConfirmButtonUI, applicationContext,
            functionsClass,
            confirmButtonProcessInterface)

    appsConfirmButton.layoutParams = confirmButtonLayoutParams
    appsConfirmButton.bringToFront()

    normalAppSelectionBinding.confirmLayout.addView(appsConfirmButton)
    appsConfirmButton.setOnClickListener {

    }

    return appsConfirmButton
}