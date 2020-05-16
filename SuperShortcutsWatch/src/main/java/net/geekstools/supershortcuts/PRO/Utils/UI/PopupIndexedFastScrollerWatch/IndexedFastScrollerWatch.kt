/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/15/20 4:21 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import kotlinx.coroutines.*
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Adapter.IndexCurveItemAdapter
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Adapter.IndexCurveWearLayoutManager
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Factory.IndexedFastScrollerFactoryWatch
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Factory.convertToDp
import net.geekstools.supershortcuts.PRO.databinding.FastScrollerIndexViewBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

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

    init {
        Log.d(this@IndexedFastScrollerWatch.javaClass.simpleName, "*** Indexed Fast Scroller Initialized ***")
    }

    fun initializeIndexView() : Deferred<IndexedFastScrollerWatch> = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {

        //Root View
        val rootLayoutParams = fastScrollerIndexViewBinding.root.layoutParams as RelativeLayout.LayoutParams
        rootLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, rootView.id)

        fastScrollerIndexViewBinding.root.layoutParams = rootLayoutParams

        fastScrollerIndexViewBinding.root
                .setPadding(indexedFastScrollerFactoryWatch.rootPaddingStart, indexedFastScrollerFactoryWatch.rootPaddingTop,
                        indexedFastScrollerFactoryWatch.rootPaddingEnd, indexedFastScrollerFactoryWatch.rootPaddingBottom)

        //Popup Text
        val popupIndexBackground: Drawable? = indexedFastScrollerFactoryWatch.popupBackgroundShape?:context.getDrawable(R.drawable.ic_launcher_balloon)?.mutate()
        popupIndexBackground?.setTint(indexedFastScrollerFactoryWatch.popupBackgroundTint)

        fastScrollerIndexViewBinding.popupIndex.background = popupIndexBackground
        fastScrollerIndexViewBinding.popupIndex.typeface = indexedFastScrollerFactoryWatch.popupTextFont
        fastScrollerIndexViewBinding.popupIndex.setTextColor(indexedFastScrollerFactoryWatch.popupTextColor)
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



        //Curve Index View
        /*Repositioning Process*/
//        val nestedIndexScrollViewCurve = rootView.findViewById<WearableRecyclerView>(R.id.nestedIndexScrollViewCurve)

        val fastScrollerCurvedIndexView = layoutInflater.inflate(R.layout.fast_scroller_curved_index_view, null) as RelativeLayout
        val nestedIndexScrollViewCurve = fastScrollerCurvedIndexView.findViewById<WearableRecyclerView>(R.id.nestedIndexScrollViewCurve)
        rootView.addView(fastScrollerCurvedIndexView, 0)

        delay(1000)

        val wearableLinearLayoutManager = WearableLinearLayoutManager(context, IndexCurveWearLayoutManager())
        nestedIndexScrollViewCurve.layoutManager = wearableLinearLayoutManager

        nestedIndexScrollViewCurve.setOnTouchListener { view, motionEvent -> true }

        nestedIndexScrollViewCurve.isEdgeItemsCenteringEnabled = true
        nestedIndexScrollViewCurve.apply {
            isCircularScrollingGestureEnabled = true
            bezelFraction = 0.10f
            scrollDegreesPerScreen = 90f
        }

        val itemsIndex = ArrayList<String>()
        itemsIndex.addAll(mapIndexFirstItem.keys)

        val indexCurveItemAdapter: IndexCurveItemAdapter = IndexCurveItemAdapter(context,
                indexedFastScrollerFactoryWatch,
                itemsIndex)
        nestedIndexScrollViewCurve.adapter = indexCurveItemAdapter

        delay(500)
        nestedIndexScrollViewCurve.smoothScrollToPosition(itemsIndex.size/2)
        nestedIndexScrollViewCurve.visibility = View.VISIBLE
        ///


        var sideIndexItemLayout = layoutInflater.inflate(R.layout.fast_scroller_side_index_item, null) as ConstraintLayout

        mapIndexFirstItem.keys.forEach { indexText ->
            sideIndexItemLayout = layoutInflater.inflate(R.layout.fast_scroller_side_index_item, null) as ConstraintLayout

            val sideIndexItem = sideIndexItemLayout.findViewById<TextView>(R.id.itemIndexView)
            sideIndexItem.setTextColor(Color.TRANSPARENT)
            sideIndexItem.text = indexText.toUpperCase(Locale.getDefault())

            fastScrollerIndexViewBinding.indexView.addView(sideIndexItemLayout)
        }

        val finalTextViewHeight = 19F.convertToDp(context)

        delay(777)

        var upperRange = (fastScrollerIndexViewBinding.indexView.y - finalTextViewHeight).toInt()

        for (number in 0 until fastScrollerIndexViewBinding.indexView.childCount) {
            val indexItemRootView = fastScrollerIndexViewBinding.indexView.getChildAt(number) as ConstraintLayout

            val indexText = (indexItemRootView.findViewById<TextView>(R.id.itemIndexView)).text.toString()
            val indexRange = (fastScrollerIndexViewBinding.indexView.getChildAt(number).y + fastScrollerIndexViewBinding.indexView.y + finalTextViewHeight).toInt()

            for (jRange in upperRange..indexRange) {
                mapRangeIndex[jRange] = indexText
            }

            upperRange = indexRange
        }

        fastScrollerIndexViewBinding.indexView.visibility = View.GONE

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

        fastScrollerIndexViewBinding.nestedIndexScrollView.setOnTouchListener { view, motionEvent ->

            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (indexedFastScrollerFactoryWatch.popupEnable) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            fastScrollerIndexViewBinding.popupIndex.text = indexText

                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (indexedFastScrollerFactoryWatch.popupEnable) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            if (!fastScrollerIndexViewBinding.popupIndex.isShown) {
                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                                fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
                            }

                            fastScrollerIndexViewBinding.popupIndex.text = indexText

                            recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)

                        } else {
                            if (fastScrollerIndexViewBinding.popupIndex.isShown) {
                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                                fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (indexedFastScrollerFactoryWatch.popupEnable) {
                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {

                            recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)

                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    } else {

                        recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)

                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (indexedFastScrollerFactoryWatch.popupEnable) {
                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {

                            recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)

                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    } else {

                        recyclerView.smoothScrollToPosition(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!)

                    }
                }
            }

            true
        }



    }
}