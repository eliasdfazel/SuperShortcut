/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/29/20 2:26 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.supershortcuts.PRO.databinding.NormalAppSelectionBinding

class NormalAppShortcutsSelectionListXYZ : AppCompatActivity() {

    lateinit var normalAppSelectionBinding: NormalAppSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        normalAppSelectionBinding = NormalAppSelectionBinding.inflate(layoutInflater)
        setContentView(normalAppSelectionBinding.root)


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {

    }


}