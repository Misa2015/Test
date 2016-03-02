/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ls.bt.utils;

import java.util.HashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ls.android.phone.R;

/**
 * Create a 4x3 grid of dial buttons.
 *
 * It was easier and more efficient to do it this way than use
 * standard layouts. It's perfectly fine (and actually encouraged) to
 * use custom layouts rather than piling up standard layouts.
 *
 * The horizontal and vertical spacings between buttons are controlled
 * by the amount of padding (attributes on the ButtonGridLayout element):
 *   - horizontal = left + right padding and
 *   - vertical = top + bottom padding.
 *
 * This class assumes that all the buttons have the same size.
 * The buttons will be bottom aligned in their view on layout.
 *
 * Invocation: onMeasure is called first by the framework to know our
 * size. Then onLayout is invoked to layout the buttons.
 */
// TODO: Blindly layout the buttons w/o checking if we overrun the
// bottom-right corner.

public class ButtonGridLayout extends ViewGroup implements View.OnTouchListener{
    private int COLUMNS = 4;
    private int ROWS = 3;
    private int NUM_CHILDREN;

    private View[] mButtons;
    private static final HashMap<Integer, Character> mDisplayMap =
        new HashMap<Integer, Character>();
    /** Set up the static maps*/
    static {
        // Map the buttons to the display characters
        mDisplayMap.put(R.id.one, '1');
        mDisplayMap.put(R.id.two, '2');
        mDisplayMap.put(R.id.three, '3');
        mDisplayMap.put(R.id.star, '*');
        mDisplayMap.put(R.id.four, '4');
        mDisplayMap.put(R.id.five, '5');
        mDisplayMap.put(R.id.six, '6');
        mDisplayMap.put(R.id.zero, '0');
        mDisplayMap.put(R.id.seven, '7');
        mDisplayMap.put(R.id.eight, '8');
        mDisplayMap.put(R.id.nine, '9');
        mDisplayMap.put(R.id.pound, '#');
    }

    public interface DialNumberListener {
    	void onNumberDialed(char number);
    }
    
    private DialNumberListener mDialNumberListener;
    public void registerDialNumberListener(DialNumberListener l) {
    	mDialNumberListener = l;
    }
    public void unregisterDialNumberListener() {
    	mDialNumberListener = null;
    }
    
    // This what the fields represent (height is similar):
    // PL: mPaddingLeft
    // BW: mButtonWidth
    // PR: mPaddingRight
    //
    //        mWidthInc
    // <-------------------->
    //   PL      BW      PR
    // <----><--------><---->
    //        --------
    //       |        |
    //       | button |
    //       |        |
    //        --------
    //
    // We assume mPaddingLeft == mPaddingRight == 1/2 padding between
    // buttons.
    //
    // mWidth == COLUMNS x mWidthInc

    // Width and height of a button
    private int mButtonWidth;
    private int mButtonHeight;

    // Width and height of a button + padding.
    private int mWidthInc;
    private int mHeightInc;

    // Height of the dialpad. Used to align it at the bottom of the
    // view.
    private int mWidth;
    private int mHeight;


    public ButtonGridLayout(Context context) {
        super(context);
    }

    public ButtonGridLayout(Context context, AttributeSet attrs) {
//        super(context, attrs , 0);
        this(context,attrs,0);
    }

    public ButtonGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray type = context.obtainStyledAttributes(attrs,R.styleable.ButtonGridLayout);
        COLUMNS = type.getInt(R.styleable.ButtonGridLayout_col, 4);
        ROWS = type.getInt(R.styleable.ButtonGridLayout_row, 3);
        type.recycle();
        NUM_CHILDREN = ROWS * COLUMNS;
        mButtons = new View[NUM_CHILDREN];
       
    }

    /**
     * Cache the buttons in a member array for faster access.  Compute
     * the measurements for the width/height of buttons.  The inflate
     * sequence is called right after the constructor and before the
     * measure/layout phase.
     */
    @Override
    protected void onFinishInflate () {
        super.onFinishInflate();
        final View[] buttons = mButtons;
        for (int i = 0; i < NUM_CHILDREN; i++) {
            buttons[i] = getChildAt(i);
            // Measure the button to get initialized.
            buttons[i].measure(MeasureSpec.UNSPECIFIED , MeasureSpec.UNSPECIFIED);
            //buttons[i].measure(MeasureSpec.makeMeasureSpec(120, MeasureSpec.AT_MOST) , MeasureSpec.makeMeasureSpec(60, MeasureSpec.AT_MOST));
            buttons[i].setOnTouchListener(this);
            buttons[i].setClickable(true);

        }

        // Cache the measurements.
        final View child = buttons[0];
       // mButtonWidth = child.getMeasuredWidth();
       // mButtonHeight = child.getMeasuredHeight();
        mButtonWidth = 200;
        mButtonHeight = 120;
        mWidthInc = mButtonWidth + getPaddingLeft() + getPaddingRight();
        mHeightInc = mButtonHeight + getPaddingTop() + getPaddingBottom();
        mWidth = COLUMNS * mWidthInc;
        mHeight = ROWS * mHeightInc;
    }

    /**
     * Set the background of all the children. Typically a selector to
     * change the background based on some combination of the button's
     * attributes (e.g pressed, enabled...)
     * @param resid Is a resource id to be used for each button's background.
     */
    public void setChildrenBackgroundResource(int resid) {
        final View[] buttons = mButtons;
        for (int i = 0; i < NUM_CHILDREN; i++) {
            buttons[i].setBackgroundResource(resid);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final View[] buttons = mButtons;
        final int paddingLeft = getPaddingLeft();
        final int buttonWidth = mButtonWidth;
        final int buttonHeight = mButtonHeight;
        final int widthInc = mWidthInc;
        final int heightInc = mHeightInc;

        int i = 0;
        // The last row is bottom aligned.
        int y = (bottom - top) - mHeight + getPaddingTop();
        for (int row = 0; row < ROWS; row++) {
            int x = paddingLeft;
            for (int col = 0; col < COLUMNS; col++) {
                buttons[i].layout(x, y, x + buttonWidth, y + buttonHeight);
                x += widthInc;
                i++;
            }
            y += heightInc;
        }
    }

    /**
     * This method is called twice in practice. The first time both
     * with and height are constraint by AT_MOST. The second time, the
     * width is still AT_MOST and the height is EXACTLY. Either way
     * the full width/height should be in mWidth and mHeight and we
     * use 'resolveSize' to do the right thing.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = resolveSize(mWidth, widthMeasureSpec);
        final int height = resolveSize(mHeight, heightMeasureSpec);
        Log.i("button", "width="+width+"  height="+height);
        setMeasuredDimension(width, height);
    }

	public boolean onTouch(View v, MotionEvent event) {
		int viewId = v.getId();

        // if the button is recognized
        if (mDisplayMap.containsKey(viewId)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                	if (null != mDialNumberListener) {
                		mDialNumberListener.onNumberDialed(mDisplayMap.get(viewId));
                	}
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        }
        return false;
	}
}
