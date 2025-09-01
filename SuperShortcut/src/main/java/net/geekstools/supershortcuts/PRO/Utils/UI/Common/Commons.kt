package net.geekstools.supershortcuts.PRO.Utils.UI.Common

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun setupUI(context: AppCompatActivity) {

    context.enableEdgeToEdge()

    WindowCompat.setDecorFitsSystemWindows(context.window, false)
    WindowInsetsControllerCompat(context.window, context.window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())

        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.hide(WindowInsetsCompat.Type.navigationBars())

        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

}