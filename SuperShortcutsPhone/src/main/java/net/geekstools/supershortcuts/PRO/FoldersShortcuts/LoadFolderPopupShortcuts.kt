/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 12:26 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.FoldersShortcuts

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.supershortcuts.PRO.databinding.FolderPopupViewBinding

class LoadFolderPopupShortcuts : Activity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private lateinit var folderName: String

    private lateinit var folderPopupViewBinding: FolderPopupViewBinding

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        folderPopupViewBinding = FolderPopupViewBinding.inflate(layoutInflater)
        setContentView(folderPopupViewBinding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        if (intent != null) {
            if (intent.action != null) {

                if (intent.action == "load_category_action") {
                    folderName = intent.getStringExtra("categoryName")!!
                } else if (intent.action == "load_category_action_shortcut") {
                    folderName = intent.getStringExtra(Intent.EXTRA_TEXT)!!
                }

                if (functionsClass.customIconsEnable()) {
                    loadCustomIcons.load()
                }

                if (intent.action == "load_category_action") {

                    functionsClass.showPopupCategoryItem(this@LoadFolderPopupShortcuts,
                            folderPopupViewBinding.popupAnchorView,
                            folderName.replace(".CategorySelected", ""), loadCustomIcons)

                } else if (intent.action == "load_category_action_shortcut") {

                    functionsClass.showPopupCategoryItem(this@LoadFolderPopupShortcuts,
                            folderPopupViewBinding.popupAnchorView,
                            folderName, loadCustomIcons)

                }

            } else {
                this@LoadFolderPopupShortcuts.finish()

                return
            }
        } else {
            this@LoadFolderPopupShortcuts.finish()

            return
        }

        folderPopupViewBinding.root.setOnTouchListener { view, motionEvent ->

            this@LoadFolderPopupShortcuts.finish()

            true
        }
    }

    public override fun onPause() {
        super.onPause()

        this@LoadFolderPopupShortcuts.finish()
    }
}