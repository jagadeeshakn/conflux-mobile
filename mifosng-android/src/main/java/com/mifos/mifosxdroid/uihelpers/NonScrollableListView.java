package com.mifos.mifosxdroid.uihelpers;

import android.app.LauncherActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.OnItemClick;

/**
 * Created by conflux37 on 11/18/2015.
 */
public class NonScrollableListView extends ListView {

    private OnItemClick itemClickListener;
    public NonScrollableListView(Context context) {
        super(context);
    }
    public NonScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NonScrollableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = View.MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }




}
