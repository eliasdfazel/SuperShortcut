/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/15/20 11:18 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScrollerWatch.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass

class CurveIndexItemAdapter(private val context: Context,
                            private val itemsIndex: ArrayList<String>) : RecyclerView.Adapter<CurveIndexItemAdapter.ViewHolder>() {

    var functionsClass: FunctionsClass = FunctionsClass(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.fast_scroller_side_index_item, parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolderBinder: ViewHolder, position: Int) {

        viewHolderBinder.itemIndexView.text = itemsIndex[position]

        viewHolderBinder.itemIndexView.setOnClickListener {

            println(itemsIndex[position])

        }

//
//        viewHolderBinder.itemIndexView.setOnTouchListener { view, motionEvent ->
//
//            when (motionEvent.action) {
//                MotionEvent.ACTION_DOWN -> {
//
//                }
//                MotionEvent.ACTION_MOVE -> {
//
//                }
//                MotionEvent.ACTION_UP -> {
//
//                }
//            }
//            true
//        }
    }

    override fun getItemCount(): Int {

        return itemsIndex.size
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        var itemIndexView: TextView = view.findViewById<View>(R.id.itemIndexView) as TextView
    }
}