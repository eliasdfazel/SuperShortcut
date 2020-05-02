/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 6:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionList
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import java.util.*

class SavedAppsListPopupAdapter(private val context: Context,
                                private val functionsClass: FunctionsClass,
                                private val selectedAppsListItem: ArrayList<AdapterItemsData>,
                                private val confirmButtonProcessInterface: ConfirmButtonProcessInterface) : BaseAdapter() {

    override fun getCount(): Int {

        return selectedAppsListItem.size
    }

    override fun getItem(position: Int): AdapterItemsData {

        return selectedAppsListItem[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, initialConvertView: View?, viewGroup: ViewGroup?): View? {

        val layoutInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = layoutInflater.inflate(R.layout.item_saved_app, null)

        val items = convertView.findViewById<View>(R.id.items) as RelativeLayout
        val appIconItem = convertView.findViewById<View>(R.id.appIconItem) as ImageView
        val textAppName = convertView.findViewById<View>(R.id.itemAppName) as TextView
        val deleteItem = convertView.findViewById<View>(R.id.deleteItem) as Button

        appIconItem.setImageDrawable(selectedAppsListItem[position].appIcon)
        textAppName.text = selectedAppsListItem[position].appName

        deleteItem.setOnClickListener {

            val appToDelete = functionsClass.appPackageNameClassName(selectedAppsListItem[position].packageName, selectedAppsListItem[position].className)

            context.deleteFile("$appToDelete.Super")

            functionsClass.removeLine(NormalAppShortcutsSelectionList.NormalApplicationsShortcutsFile, appToDelete)

            if (functionsClass.mixShortcuts()) {
                functionsClass.removeLine(".mixShortcuts", appToDelete)
            }

            confirmButtonProcessInterface.shortcutDeleted()
            confirmButtonProcessInterface.savedShortcutCounter()
            confirmButtonProcessInterface.reevaluateShortcutsInfo()

        }

        return convertView
    }
}