/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/29/20 1:59 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.Factory.IndexedFastScrollerFactory
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.Factory.calculateNavigationBarHeight
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.Factory.calculateStatusBarHeight
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.Factory.convertToDp
import net.geekstools.supershortcuts.PRO.databinding.FastScrollerIndexViewBinding
import java.util.*

/**
 * You Must Enable ViewBinding.
 * Just Add The Below Configuration To Your App Level Gradle In Android Section
 * viewBinding { enabled = true }
 *
 *
 * @param rootView Instance Of Root (Base) View In Your Layout
 * @param nestedScrollView Follow This Hierarchy: ScrollView -> RelativeLayout -> RecyclerView
 * @param recyclerView Instance Of A RecyclerView That You Want To Populate With Items
 *
 *
 * Add Index Layout As <include /> In Your Layout. Be Careful With Layers. That's Why I Let You To Put It Manually In Layout.
 * @param fastScrollerIndexViewBinding Pass ViewBinding Instance Of Fast Scroller Layout That Added Using <include />
 *
 * @param indexedFastScrollerFactory Change Default Value Or Just Pass IndexedFastScrollerFactory()
 **/
class IndexedFastScroller(private val context: Context,
                          private val layoutInflater: LayoutInflater,
                          private val rootView: ViewGroup,
                          private val nestedScrollView: ScrollView,
                          private val recyclerView: RecyclerView,
                          private val fastScrollerIndexViewBinding: FastScrollerIndexViewBinding,
                          private val indexedFastScrollerFactory: IndexedFastScrollerFactory) {

    private val statusBarHeight = calculateStatusBarHeight(context.resources)
    private val navigationBarBarHeight = calculateNavigationBarHeight(context.resources)

    private val finalPopupVerticalOffset: Int = indexedFastScrollerFactory.popupVerticalOffset.convertToDp(context)

    private val finalPopupHorizontalOffset: Int = indexedFastScrollerFactory.popupHorizontalOffset.convertToDp(context)

    init {
        Log.d(this@IndexedFastScroller.javaClass.simpleName, "*** Indexed Fast Scroller Initialized ***")
    }

    fun initializeIndexView() : Deferred<IndexedFastScroller> = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {

        fastScrollerIndexViewBinding.indexView.removeAllViews()

        //Root View
        val rootLayoutParams = fastScrollerIndexViewBinding.root.layoutParams as RelativeLayout.LayoutParams
        rootLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, rootView.id)

        fastScrollerIndexViewBinding.root.layoutParams = rootLayoutParams

        fastScrollerIndexViewBinding.root
                .setPadding(indexedFastScrollerFactory.rootPaddingStart, indexedFastScrollerFactory.rootPaddingTop,
                        indexedFastScrollerFactory.rootPaddingEnd, indexedFastScrollerFactory.rootPaddingBottom)


        //Popup Text
        val popupIndexLayoutParams = fastScrollerIndexViewBinding.popupIndex.layoutParams as RelativeLayout.LayoutParams
        popupIndexLayoutParams.marginEnd = finalPopupHorizontalOffset

        fastScrollerIndexViewBinding.popupIndex.layoutParams = popupIndexLayoutParams

        val popupIndexBackground: Drawable? = indexedFastScrollerFactory.popupBackgroundShape?:context.getDrawable(R.drawable.ic_launcher_balloon)?.mutate()
        popupIndexBackground?.setTint(indexedFastScrollerFactory.popupBackgroundTint)
        fastScrollerIndexViewBinding.popupIndex.background = popupIndexBackground
        fastScrollerIndexViewBinding.popupIndex.setTextColor(indexedFastScrollerFactory.popupTextColor)
        fastScrollerIndexViewBinding.popupIndex.typeface = indexedFastScrollerFactory.popupTextFont
        fastScrollerIndexViewBinding.popupIndex.setTextSize(TypedValue.COMPLEX_UNIT_SP, indexedFastScrollerFactory.popupTextSize)

        this@IndexedFastScroller
    }

    /**
     * When Populating Your List Get First Char Of Each Item Title By itemTextTitle.substring(0, 1).toUpperCase(Locale.getDefault()).
     * & Add It To A ArrayList<String>.
     * Then Pass It As...
     *
     * @param listOfNewCharOfItemsForIndex ArrayList<String>
     **/
    fun loadIndexData(listOfNewCharOfItemsForIndex: ArrayList<String>) = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {

        val mapIndexFirstItem: LinkedHashMap<String, Int> = LinkedHashMap<String, Int>()
        val mapIndexLastItem: LinkedHashMap<String, Int> = LinkedHashMap<String, Int>()

        val mapRangeIndex: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>()

        withContext(Dispatchers.Default) {

            for (indexNumber in 0 until listOfNewCharOfItemsForIndex.size) {
                val indexText = listOfNewCharOfItemsForIndex[indexNumber]

                /*Avoid Duplication*/
                if (mapIndexFirstItem[indexText] == null) {
                    mapIndexFirstItem[indexText] = indexNumber
                }

                mapIndexLastItem[indexText] = indexNumber
            }
        }

        var sideIndexItem = layoutInflater.inflate(R.layout.fast_scroller_side_index_item, null) as TextView

        mapIndexFirstItem.keys.forEach { indexText ->
            sideIndexItem = layoutInflater.inflate(R.layout.fast_scroller_side_index_item, null) as TextView
            sideIndexItem.text = indexText.toUpperCase(Locale.getDefault())
            sideIndexItem.setTextColor(indexedFastScrollerFactory.indexItemTextColor)
            sideIndexItem.typeface = indexedFastScrollerFactory.indexItemFont
            sideIndexItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, indexedFastScrollerFactory.indexItemSize)

            fastScrollerIndexViewBinding.indexView.addView(sideIndexItem)
        }

        val finalTextView = sideIndexItem

        delay(777)

        var upperRange = (fastScrollerIndexViewBinding.indexView.y - finalTextView.height).toInt()

        for (number in 0 until fastScrollerIndexViewBinding.indexView.childCount) {
            val indexText = (fastScrollerIndexViewBinding.indexView.getChildAt(number) as TextView).text.toString()
            val indexRange = (fastScrollerIndexViewBinding.indexView.getChildAt(number).y + fastScrollerIndexViewBinding.indexView.y + finalTextView.height).toInt()

            for (jRange in upperRange..indexRange) {
                mapRangeIndex[jRange] = indexText
            }

            upperRange = indexRange
        }

        this@async.launch {

            setupFastScrollingIndexing(mapIndexFirstItem,
                    mapRangeIndex)
        }.join()
    }

    /**
     * Setup Popup View Of Index With Touch On List Of Index
     **/
    @SuppressLint("ClickableViewAccessibility")
    private fun setupFastScrollingIndexing(mapIndexFirstItem: LinkedHashMap<String, Int>,
                                           mapRangeIndex: LinkedHashMap<Int, String>) {

        fastScrollerIndexViewBinding.nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        fastScrollerIndexViewBinding.nestedIndexScrollView.visibility = View.VISIBLE

        val popupIndexOffsetY = (
                statusBarHeight
                        + navigationBarBarHeight
                        + finalPopupVerticalOffset).toFloat()

        fastScrollerIndexViewBinding.nestedIndexScrollView.setOnTouchListener { view, motionEvent ->

            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (indexedFastScrollerFactory.popupEnable) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            fastScrollerIndexViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            fastScrollerIndexViewBinding.popupIndex.text = indexText
                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (indexedFastScrollerFactory.popupEnable) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            if (!fastScrollerIndexViewBinding.popupIndex.isShown) {
                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                                fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
                            }

                            fastScrollerIndexViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            fastScrollerIndexViewBinding.popupIndex.text = indexText

                            nestedScrollView.smoothScrollTo(
                                    0,
                                    recyclerView.getChildAt(mapIndexFirstItem[mapRangeIndex[motionEvent.y.toInt()]]!!).y.toInt()
                            )
                        } else {
                            if (fastScrollerIndexViewBinding.popupIndex.isShown) {
                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                                fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (indexedFastScrollerFactory.popupEnable) {
                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {

                            nestedScrollView.smoothScrollTo(
                                    0,
                                    recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                            )

                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    } else {
                        nestedScrollView.smoothScrollTo(
                                0,
                                recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                        )
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (indexedFastScrollerFactory.popupEnable) {
                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {

                            nestedScrollView.smoothScrollTo(
                                    0,
                                    recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                            )

                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    } else {
                        nestedScrollView.smoothScrollTo(
                                0,
                                recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                        )
                    }
                }
            }

            true
        }
    }
}