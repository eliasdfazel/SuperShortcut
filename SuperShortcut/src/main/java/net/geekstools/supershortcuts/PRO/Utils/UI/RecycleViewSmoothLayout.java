/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 10:44 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.UI;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewSmoothLayout extends LinearLayoutManager {

    private static final float MILLISECONDS_PER_INCH = 27f;
    private Context context;

    public RecycleViewSmoothLayout(Context context, int Orientation, boolean reverseLayout) {
        super(context, Orientation, reverseLayout);
        this.context = context;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return RecycleViewSmoothLayout.this.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
