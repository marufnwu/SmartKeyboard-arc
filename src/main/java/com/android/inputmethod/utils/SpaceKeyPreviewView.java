package com.android.inputmethod.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SpaceKeyPreviewView extends RelativeLayout {
    public SpaceKeyPreviewView(Context context) {
        this(context, null);
    }

    public SpaceKeyPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextView t = new TextView(context);
        t.setText("I am here");
        addView(t);
    }
}
