package io.github.xialincn.fakenokiaclock.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lin on 2016/1/18.
 */
public class Size {
    public int width;
    public int height;

    public Size(int w, int h) {
        if (w != h) {
            throw new RuntimeException("WidthNotEqualsHeight");
        }

        width = w;
        height = h;
    }

    public static Size extract(Context context, AttributeSet attrs) {
        int[] attrsArray = new int[]{
                android.R.attr.id, // 0
                android.R.attr.layout_width, // 1
                android.R.attr.layout_height // 2
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        int id = ta.getResourceId(0 /* index of attribute in attrsArray */, View.NO_ID);
        int width = ta.getDimensionPixelSize(1, ViewGroup.LayoutParams.MATCH_PARENT);
        int height = ta.getDimensionPixelSize(2, ViewGroup.LayoutParams.MATCH_PARENT);
        ta.recycle();
        return new Size(width, height);
    }
}
