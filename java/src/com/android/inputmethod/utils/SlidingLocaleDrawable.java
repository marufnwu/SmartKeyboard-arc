package com.android.inputmethod.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.view.ViewConfiguration;

import com.sikderithub.keyboard.R;

/**
 * Animation to be displayed on the spacebar preview popup when switching
 * languages by swiping the spacebar. It draws the current, previous and
 * next languages and moves them by the delta of touch movement on the spacebar.
 */
public class SlidingLocaleDrawable extends Drawable {
    private static final String TAG = "SlidingLocaleDrawable";

    private static final int OPACITY_FULLY_OPAQUE = 255;


    private final int mWidth;
    private final int mHeight;
    private final Drawable mBackground;
    private final TextPaint mTextPaint;
    private final int mMiddleX;
    private final Drawable mLeftDrawable;
    private final Drawable mRightDrawable;
    private final int mThreshold;
    private int mDiff;
    private boolean mHitThreshold;
    private String mCurrentLanguage;
    private String mNextLanguage;
    private String mPrevLanguage;
    private static final float SPACEBAR_LANGUAGE_BASELINE = 0.6f;
    private Context context;
    private LanguageSwitcher mLanguageSwitcher;
    private int spaceSlideTextColor;



    public SlidingLocaleDrawable(Drawable background, int width, int height, Context context, int spaceSlideTextColor) {
        this.context = context;
        this.spaceSlideTextColor = spaceSlideTextColor;
        mBackground = background;
        setDefaultBounds(mBackground);
        mWidth = width;
        mHeight = height;
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(getTextSizeFromTheme(android.R.style.TextAppearance_Medium, 18));
        mTextPaint.setColor(context.getResources().getColor(R.color.highlight_color_lxx_light));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAlpha(OPACITY_FULLY_OPAQUE);
        mTextPaint.setAntiAlias(true);
        mMiddleX = (mWidth - mBackground.getIntrinsicWidth()) / 2;
        mLeftDrawable =
                context.getDrawable(R.drawable.btn_keyboard_key_functional_ics);
        mRightDrawable =
                context.getDrawable(R.drawable.btn_keyboard_key_functional_ics);
        mThreshold = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    public void setDiff(int diff) {
        if (diff == Integer.MAX_VALUE) {
            mHitThreshold = false;
            mCurrentLanguage = null;
            return;
        }


        mDiff = diff;
        if (mDiff > mWidth) mDiff = mWidth;
        if (mDiff < -mWidth) mDiff = -mWidth;
        if (Math.abs(mDiff) > mThreshold)
            mHitThreshold = true;
        invalidateSelf();
    }




    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        if (mHitThreshold) {
            Paint paint = mTextPaint;
            final int width = mWidth;
            final int height = mHeight;
            final int diff = mDiff;
            final Drawable lArrow = mLeftDrawable;
            final Drawable rArrow = mRightDrawable;
            canvas.clipRect(0, 0, width, height);

            if (mCurrentLanguage == null) {
                final LanguageSwitcher languageSwitcher = LanguageSwitcher.instance;
                    mCurrentLanguage = Constant.getLanguageName(languageSwitcher.getInputLocale());
                    mNextLanguage =  Constant.getLanguageName(languageSwitcher.getNextInputLocale());
                    mPrevLanguage =  Constant.getLanguageName(languageSwitcher.getPrevInputLocale());
            }

            Log.d(TAG, "mCurrentLanguage: "+mCurrentLanguage);
            Log.d(TAG, "mNextLanguage: "+mNextLanguage);
            Log.d(TAG, "mPrevLanguage: "+mPrevLanguage);


            // Draw language text with shadow
            final float baseline = mHeight * SPACEBAR_LANGUAGE_BASELINE - paint.descent();
            paint.setColor(spaceSlideTextColor);
            canvas.drawText(mCurrentLanguage, width / 2 + diff, baseline, paint);
            canvas.drawText(mNextLanguage, diff - width / 2, baseline, paint);
            canvas.drawText(mPrevLanguage, diff + width + width / 2, baseline, paint);

            setDefaultBounds(lArrow);
            rArrow.setBounds(width - rArrow.getIntrinsicWidth(), 0, width,
                    rArrow.getIntrinsicHeight());
//            lArrow.draw(canvas);
//            rArrow.draw(canvas);
        }
        if (mBackground != null) {
            canvas.translate(mMiddleX, 0);
            mBackground.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        // Ignore
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Ignore
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    private void setDefaultBounds(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    private int getTextSizeFromTheme(int style, int defValue) {
        TypedArray array = context.getTheme().obtainStyledAttributes(
                style, new int[] { android.R.attr.textSize });
        int resId = array.getResourceId(0, 0);
        if (resId >= array.length()) {
            Log.i(TAG, "getTextSizeFromTheme error: resId " + resId + " > " + array.length());
            return defValue;
        }
        int textSize = array.getDimensionPixelSize(resId, defValue);
        return textSize;
    }



}