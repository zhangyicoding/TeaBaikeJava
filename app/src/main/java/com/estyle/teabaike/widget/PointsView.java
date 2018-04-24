package com.estyle.teabaike.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.estyle.teabaike.R;

public class PointsView extends LinearLayout {

    private int mPointCount;
    private Drawable mPointDrawable;
    private int mPointMargin;
    private int mPointSize;

    public PointsView(Context context) {
        super(context);
    }

    public PointsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PointsView);
        mPointDrawable = typedArray.getDrawable(R.styleable.PointsView_pointDrawable);
        mPointMargin = typedArray.getDimensionPixelSize(R.styleable.PointsView_pointMargin, 0);
        mPointSize = typedArray.getDimensionPixelSize(R.styleable.PointsView_pointSize, 0);
        typedArray.recycle();
    }

    public void setPointCount(int pointCount) {
        setPoint(pointCount, mPointMargin, mPointSize, mPointDrawable);
    }

    public void setPoint(int pointCount, int pointMargin, int pointSize, Drawable pointDrawable) {
        if (pointCount > 0) {
            this.mPointCount = pointCount;
            LayoutParams params = new LayoutParams(pointSize, pointSize);
            params.setMargins(0, 0, pointMargin, 0);
            for (int i = 0; i < pointCount; i++) {
                View point = new View(getContext());
                if (i == pointCount - 1) {
                    params = new LayoutParams(pointSize, pointSize);
                    params.setMargins(0, 0, 0, 0);
                }
                point.setLayoutParams(params);
                point.setBackgroundDrawable(pointDrawable);
                addView(point);
            }
            getChildAt(0).setSelected(true);
        }
    }

    public void setSelectedPosition(int position) {
        for (int i = 0; i < mPointCount; i++) {
            getChildAt(i).setSelected(i == position);
        }
    }

}
