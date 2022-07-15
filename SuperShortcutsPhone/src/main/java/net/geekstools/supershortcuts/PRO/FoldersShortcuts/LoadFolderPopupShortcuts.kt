/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/15/20 8:23 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.FoldersShortcuts

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SecurityServices.Protection
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.supershortcuts.PRO.databinding.FolderPopupViewBinding

class LoadFolderPopupShortcuts : AppCompatActivity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private var folderName: String? = null

    private lateinit var folderPopupViewBinding: FolderPopupViewBinding

    private val securityServicesProcess: SecurityServicesProcess by lazy {
        SecurityServicesProcess(this@LoadFolderPopupShortcuts)
    }

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

                folderName?.let { folderName ->

                    if (functionsClass.countLineInnerFile(folderName) > 0) {

                        if (securityServicesProcess.securityServiceEnabled()) {

                            securityServicesProcess.protectIt(folderName.replace(".CategorySelected", ""), object : Protection {

                                override fun processProtected() {

                                    openFolder()

                                }

                                override fun processNotProtected() {

                                    Toast.makeText(applicationContext, getString(R.string.notAuthorized), Toast.LENGTH_LONG).show()

                                    this@LoadFolderPopupShortcuts.finish()

                                }

                            })

                        } else {

                            openFolder()

                        }

                    } else {

                        this@LoadFolderPopupShortcuts.finish()

                    }

                }

            } else {

                this@LoadFolderPopupShortcuts.finish()

            }
        } else {

            this@LoadFolderPopupShortcuts.finish()

        }

        folderPopupViewBinding.root.setOnTouchListener { view, motionEvent ->

            this@LoadFolderPopupShortcuts.finish()

            true
        }
    }

    fun openFolder() {

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons.load()
        }

        folderName?.let { folderName ->

            folderPopupViewBinding.popupAnchorView.post {

                if (intent.action == "load_category_action") {

                    functionsClass.showPopupCategoryItem(this@LoadFolderPopupShortcuts,
                        folderPopupViewBinding.popupAnchorView,
                        folderName.replace(".CategorySelected", ""),
                        loadCustomIcons)

                } else if (intent.action == "load_category_action_shortcut") {

                    functionsClass.showPopupCategoryItem(this@LoadFolderPopupShortcuts,
                        folderPopupViewBinding.popupAnchorView,
                        folderName,
                        loadCustomIcons)

                }
            }

        }

    }

}