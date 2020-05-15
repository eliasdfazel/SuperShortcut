/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/15/20 11:18 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import kotlinx.coroutines.*
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.UI.CurveUtils.CurveWearLayoutManager
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Adapter.CurveIndexItemAdapter
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Factory.IndexedFastScrollerFactoryWatch
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Factory.calculateNavigationBarHeight
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Factory.calculateStatusBarHeight
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Factory.convertToDp
import net.geekstools.supershortcuts.PRO.databinding.FastScrollerIndexViewBinding

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
 * @param indexedFastScrollerFactoryWatch Change Default Value Or Just Pass IndexedFastScrollerFactory()
 **/
class IndexedFastScrollerWatch(private val context: Context,
                               private val layoutInflater: LayoutInflater,
                               private val rootView: ViewGroup,
                               private val recyclerView: RecyclerView,
                               private val fastScrollerIndexViewBinding: FastScrollerIndexViewBinding,
                               private val indexedFastScrollerFactoryWatch: IndexedFastScrollerFactoryWatch) {

    private val statusBarHeight = calculateStatusBarHeight(context.resources)
    private val navigationBarBarHeight = calculateNavigationBarHeight(context.resources)

    private val finalPopupVerticalOffset: Int = indexedFastScrollerFactoryWatch.popupVerticalOffset.convertToDp(context)

    private val finalPopupHorizontalOffset: Int = indexedFastScrollerFactoryWatch.popupHorizontalOffset.convertToDp(context)

    init {
        Log.d(this@IndexedFastScrollerWatch.javaClass.simpleName, "*** Indexed Fast Scroller Initialized ***")
    }

    fun initializeIndexView() : Deferred<IndexedFastScrollerWatch> = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {

        fastScrollerIndexViewBinding.nestedIndexScrollView.layoutManager = WearableLinearLayoutManager(context, CurveWearLayoutManager())
        fastScrollerIndexViewBinding.nestedIndexScrollView.isEdgeItemsCenteringEnabled = true
        fastScrollerIndexViewBinding.nestedIndexScrollView.apply {
            this.isCircularScrollingGestureEnabled = true
            this.bezelFraction = 0.10f
            this.scrollDegreesPerScreen = 90f
        }

        //Root View
//        val rootLayoutParams = fastScrollerIndexViewBinding.root.layoutParams as RelativeLayout.LayoutParams
//        rootLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, rootView.id)
//
//        fastScrollerIndexViewBinding.root.layoutParams = rootLayoutParams
//
//        fastScrollerIndexViewBinding.root
//                .setPadding(indexedFastScrollerFactoryWatch.rootPaddingStart, indexedFastScrollerFactoryWatch.rootPaddingTop,
//                        indexedFastScrollerFactoryWatch.rootPaddingEnd, indexedFastScrollerFactoryWatch.rootPaddingBottom)

        //Popup Text
        val popupIndexLayoutParams = fastScrollerIndexViewBinding.popupIndex.layoutParams as RelativeLayout.LayoutParams
        popupIndexLayoutParams.marginEnd = finalPopupHorizontalOffset

        fastScrollerIndexViewBinding.popupIndex.layoutParams = popupIndexLayoutParams

        val popupIndexBackground: Drawable? = indexedFastScrollerFactoryWatch.popupBackgroundShape?:context.getDrawable(R.drawable.ic_launcher_balloon)?.mutate()
        popupIndexBackground?.setTint(indexedFastScrollerFactoryWatch.popupBackgroundTint)
        fastScrollerIndexViewBinding.popupIndex.background = popupIndexBackground
        fastScrollerIndexViewBinding.popupIndex.setTextColor(indexedFastScrollerFactoryWatch.popupTextColor)
        fastScrollerIndexViewBinding.popupIndex.typeface = indexedFastScrollerFactoryWatch.popupTextFont
        fastScrollerIndexViewBinding.popupIndex.setTextSize(TypedValue.COMPLEX_UNIT_SP, indexedFastScrollerFactoryWatch.popupTextSize)

        this@IndexedFastScrollerWatch
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

        val t = ArrayList<String>()
        mapIndexFirstItem.keys.forEach {
            t.add(it)
        }

        val curveIndexItemAdapter: CurveIndexItemAdapter = CurveIndexItemAdapter(context, t)
        fastScrollerIndexViewBinding.nestedIndexScrollView.adapter = curveIndexItemAdapter

        delay(500)
        fastScrollerIndexViewBinding.nestedIndexScrollView.scrollToPosition(20)

//        var sideIndexItem = layoutInflater.inflate(R.layout.fast_scroller_side_index_item, null) as TextView

//        mapIndexFirstItem.keys.forEach { indexText ->
//            sideIndexItem = layoutInflater.inflate(R.layout.fast_scroller_side_index_item, null) as TextView
//            sideIndexItem.text = indexText.toUpperCase(Locale.getDefault())
//            sideIndexItem.setTextColor(indexedFastScrollerFactoryWatch.indexItemTextColor)
//            sideIndexItem.typeface = indexedFastScrollerFactoryWatch.indexItemFont
//            sideIndexItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, indexedFastScrollerFactoryWatch.indexItemSize)

//            fastScrollerIndexViewBinding.indexView.addView(sideIndexItem)
//        }

//        val finalTextView = sideIndexItem

//        delay(777)

//        var upperRange = (fastScrollerIndexViewBinding.indexView.y - finalTextView.height).toInt()

//        for (number in 0 until fastScrollerIndexViewBinding.indexView.childCount) {
//            val indexText = (fastScrollerIndexViewBinding.indexView.getChildAt(number) as TextView).text.toString()
//            val indexRange = (fastScrollerIndexViewBinding.indexView.getChildAt(number).y + fastScrollerIndexViewBinding.indexView.y + finalTextView.height).toInt()

//            for (jRange in upperRange..indexRange) {
//                mapRangeIndex[jRange] = indexText
//            }

//            upperRange = indexRange
//        }

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

//        fastScrollerIndexViewBinding.nestedIndexScrollView.setOnTouchListener { view, motionEvent ->
//
//            when(motionEvent.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    if (indexedFastScrollerFactoryWatch.popupEnable) {
//                        val indexText = mapRangeIndex[motionEvent.y.toInt()]
//
//                        if (indexText != null) {
//                            fastScrollerIndexViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
//                            fastScrollerIndexViewBinding.popupIndex.text = indexText
//                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
//                            fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
//                        }
//                    }
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    if (indexedFastScrollerFactoryWatch.popupEnable) {
//                        val indexText = mapRangeIndex[motionEvent.y.toInt()]
//
//                        if (indexText != null) {
//                            if (!fastScrollerIndexViewBinding.popupIndex.isShown) {
//                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
//                                fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
//                            }
//
//                            fastScrollerIndexViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
//                            fastScrollerIndexViewBinding.popupIndex.text = indexText
//
//                            recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)
//
//                        } else {
//                            if (fastScrollerIndexViewBinding.popupIndex.isShown) {
//                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
//                                fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
//                            }
//                        }
//                    }
//                }
//                MotionEvent.ACTION_UP -> {
//                    if (indexedFastScrollerFactoryWatch.popupEnable) {
//                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {
//
//                            recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)
//
//                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
//                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
//                        }
//                    } else {
//
//                        recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)
//
//                    }
//                }
//                MotionEvent.ACTION_CANCEL -> {
//                    if (indexedFastScrollerFactoryWatch.popupEnable) {
//                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {
//
//                            recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)
//
//                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
//                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
//                        }
//                    } else {
//
//                        recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)
//
//                    }
//                }
//            }
//
//            true
//        }
    }
}