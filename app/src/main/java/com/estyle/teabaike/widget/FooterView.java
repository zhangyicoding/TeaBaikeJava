package com.estyle.teabaike.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.estyle.teabaike.R;

public class FooterView extends FrameLayout {

    public FooterView(Context context) {
        super(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        setLayoutParams(params);
        int margin = context.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        setPadding(0, margin, 0, margin);

        ProgressBar progressBar = new ProgressBar(context,
                null,
                android.R.attr.progressBarStyleSmall);
        addView(progressBar);

        setVisibility(View.GONE);
    }

}
