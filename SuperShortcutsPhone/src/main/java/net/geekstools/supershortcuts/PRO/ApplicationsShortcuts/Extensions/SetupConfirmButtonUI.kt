/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 11:42 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Extensions

import android.widget.RelativeLayout
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionList
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButton
import net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface.ConfirmButtonProcessInterface

fun NormalAppShortcutsSelectionList.setupConfirmButtonUI(confirmButtonProcessInterface: ConfirmButtonProcessInterface) : AppsConfirmButton {

    val confirmButtonLayoutParams = RelativeLayout.LayoutParams(
            functionsClass.DpToInteger(73),
            functionsClass.DpToInteger(73)
    )

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