/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 9:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.UI.AppsConfirmButtonPhone
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable
import net.geekstools.supershortcuts.PRO.Utils.UI.ConfirmButtonInterface.ConfirmButtonProcessInterface
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import java.util.*

class SelectionListAdapter(private val context: Context,
                           private var functionsClass: FunctionsClass,
                           private var temporaryFallingIcon: ImageView,
                           private val adapterItemData: ArrayList<AdapterItemsData>,
                           private val appsConfirmButtonPhone: AppsConfirmButtonPhone,
                           private val confirmButtonProcessInterface: ConfirmButtonProcessInterface) : RecyclerView.Adapter<SelectionListAdapter.ViewHolder>() {

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(context, functionsClass.customIconPackageName())
    }

    object PickedAttribute {
        var fromX: Float = 0f
        var fromY: Float = 0f

        var toX: Float = 0f
        var toY: Float = 0f

        var dpHeight: Float = 0f
        var dpWidth: Float = 0f

        var animationType: Int = 0
    }

    init {
        val displayMetrics = context.resources.displayMetrics

        PickedAttribute.dpHeight = displayMetrics.heightPixels.toFloat()
        PickedAttribute.dpWidth = displayMetrics.widthPixels.toFloat()

        PickedAttribute.toX = PublicVariable.confirmButtonX
        PickedAttribute.fromX = PickedAttribute.toX

        PickedAttribute.toY = PublicVariable.confirmButtonY

        PickedAttribute.animationType = Animation.ABSOLUTE

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons.load()
        }
    }

    override fun getItemCount(): Int {

        return adapterItemData.size
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.selection_item_card_list, viewGroup, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolderBinder: ViewHolder, position: Int) {

        viewHolderBinder.appIconView.setImageDrawable(adapterItemData[position].appIcon)
        viewHolderBinder.appNameView.text = adapterItemData[position].appName

        viewHolderBinder.autoChoice = viewHolderBinder.autoChoice

        viewHolderBinder.autoChoice.isChecked = false
        viewHolderBinder.autoChoice.isChecked = context.getFileStreamPath(functionsClass.appPackageNameClassName(adapterItemData[position].packageName, adapterItemData[position].className) + ".Super").exists()
        viewHolderBinder.autoChoice.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                if (PublicVariable.maxAppShortcutsCounter < PublicVariable.maxAppShortcuts) {
                    PublicVariable.maxAppShortcutsCounter++
                }
            } else if (!isChecked) {
                PublicVariable.maxAppShortcutsCounter = PublicVariable.maxAppShortcutsCounter - 1
            }
        }

        viewHolderBinder.fullItemView.setOnTouchListener { view, motionEvent ->

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                    PickedAttribute.fromY = -(PickedAttribute.dpHeight - motionEvent.rawY)

                }
                MotionEvent.ACTION_UP -> {

                    val packageName = adapterItemData[position].packageName
                    val className = adapterItemData[position].className

                    val autoFile = context.getFileStreamPath(functionsClass.appPackageNameClassName(packageName, className) + ".Super")

                    if (autoFile.exists()) {
                        context.deleteFile(functionsClass.appPackageNameClassName(packageName, className) + ".Super")

                        functionsClass.removeLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile, functionsClass.appPackageNameClassName(packageName, className))

                        if (functionsClass.mixShortcuts()) {
                            functionsClass.removeLine(".mixShortcuts", functionsClass.appPackageNameClassName(packageName, className))
                        }

                        viewHolderBinder.autoChoice.isChecked = false

                        confirmButtonProcessInterface.savedShortcutCounter()
                        confirmButtonProcessInterface.reevaluateShortcutsInfo()
                        confirmButtonProcessInterface.hideSavedShortcutList()
                        appsConfirmButtonPhone.makeItVisible()

                    } else {
                        if (functionsClass.mixShortcuts()) {
                            if (functionsClass.countLine(".mixShortcuts") < functionsClass.systemMaxAppShortcut) {
                                functionsClass.saveFile(
                                        functionsClass.appPackageNameClassName(packageName, className) + ".Super",
                                        functionsClass.appPackageNameClassName(packageName, className))

                                functionsClass.saveFileAppendLine(
                                        NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile,
                                        functionsClass.appPackageNameClassName(packageName, className))

                                functionsClass.saveFileAppendLine(
                                        ".mixShortcuts",
                                        functionsClass.appPackageNameClassName(packageName, className))

                                viewHolderBinder.autoChoice.isChecked = true

                                val translateAnimation = TranslateAnimation(PickedAttribute.animationType, PickedAttribute.fromX,
                                        PickedAttribute.animationType, PickedAttribute.toX,
                                        PickedAttribute.animationType, PickedAttribute.fromY,
                                        PickedAttribute.animationType, PickedAttribute.toY)
                                translateAnimation.duration = Math.abs(PickedAttribute.fromY).toLong()

                                temporaryFallingIcon.setImageDrawable(if (functionsClass.customIconsEnable()) {
                                    loadCustomIcons.getDrawableIconForPackage(packageName, adapterItemData[position].appIcon)
                                } else {
                                    adapterItemData[position].appIcon
                                })
                                temporaryFallingIcon.startAnimation(translateAnimation)

                                translateAnimation.setAnimationListener(object : Animation.AnimationListener {

                                    override fun onAnimationStart(animation: Animation) {

                                        confirmButtonProcessInterface.hideSavedShortcutList()
                                        appsConfirmButtonPhone.makeItVisible()

                                        temporaryFallingIcon.visibility = View.VISIBLE
                                    }

                                    override fun onAnimationEnd(animation: Animation) {
                                        temporaryFallingIcon.visibility = View.INVISIBLE

                                        appsConfirmButtonPhone.startCustomAnimation(null)
                                        confirmButtonProcessInterface.savedShortcutCounter()
                                        confirmButtonProcessInterface.reevaluateShortcutsInfo()
                                    }

                                    override fun onAnimationRepeat(animation: Animation) {}
                                })
                            }
                        } else {
                            if (PublicVariable.maxAppShortcutsCounter < PublicVariable.maxAppShortcuts) {

                                functionsClass.saveFile(
                                        functionsClass.appPackageNameClassName(packageName, className) + ".Super",
                                        packageName)

                                functionsClass.saveFileAppendLine(
                                        NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile,
                                        functionsClass.appPackageNameClassName(packageName, className))

                                viewHolderBinder.autoChoice.isChecked = true

                                val translateAnimation = TranslateAnimation(PickedAttribute.animationType, PickedAttribute.fromX,
                                        PickedAttribute.animationType, PickedAttribute.toX,
                                        PickedAttribute.animationType, PickedAttribute.fromY,
                                        PickedAttribute.animationType, PickedAttribute.toY)
                                translateAnimation.duration = Math.abs(PickedAttribute.fromY).toLong()

                                temporaryFallingIcon.setImageDrawable(if (functionsClass.customIconsEnable()) loadCustomIcons!!.getDrawableIconForPackage(packageName, adapterItemData[position].appIcon) else adapterItemData[position].appIcon)
                                temporaryFallingIcon.startAnimation(translateAnimation)

                                translateAnimation.setAnimationListener(object : Animation.AnimationListener {

                                    override fun onAnimationStart(animation: Animation) {

                                        confirmButtonProcessInterface.hideSavedShortcutList()
                                        appsConfirmButtonPhone.makeItVisible()

                                        temporaryFallingIcon.visibility = View.VISIBLE
                                    }

                                    override fun onAnimationEnd(animation: Animation) {
                                        temporaryFallingIcon.visibility = View.INVISIBLE

                                        appsConfirmButtonPhone.startCustomAnimation(null)
                                        confirmButtonProcessInterface.savedShortcutCounter()
                                        confirmButtonProcessInterface.reevaluateShortcutsInfo()
                                    }

                                    override fun onAnimationRepeat(animation: Animation) {}
                                })
                            }
                        }
                    }

                }
            }
            true
        }
        PublicVariable.maxAppShortcutsCounter = functionsClass.countLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var fullItemView: RelativeLayout = view.findViewById<RelativeLayout>(R.id.fullItemView) as RelativeLayout
        var appIconView: ImageView = view.findViewById<ImageView>(R.id.appIconView) as ImageView
        var appNameView: TextView = view.findViewById<TextView>(R.id.appNameView) as TextView
        var autoChoice: CheckBox = view.findViewById<CheckBox>(R.id.autoChoice) as CheckBox
    }
}