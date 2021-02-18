/*
 * *
 *  * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved. <BR>
 *  * <BR>
 *  * This software is the confidential and proprietary information of
 *  * This.ID. ("Confidential Information").<BR>
 *  * <BR>
 *  * Such Confidential Information shall not be disclosed and shall
 *  * use it only	 in accordance with the terms of the license agreement
 *  * entered into with This.ID; other than in accordance with the written
 *  * permission of This.ID. <BR>
 *  *
 *
 *
 */

package id.thork.app.pages.settings_language.element;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Reja on 08,June,2019
 * Jakarta, Indonesia.
 */
public class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
 
    private GestureDetector gestureDetector;
    private ClickListener clickListener;
 
    public RecyclerViewTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
 
            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }
 
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
 
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildPosition(child));
        }
        return false;
    }
 
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }
 
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
 
    }
 
    public interface ClickListener {
        void onClick(View view, int position);
 
        void onLongClick(View view, int position);
    }
}