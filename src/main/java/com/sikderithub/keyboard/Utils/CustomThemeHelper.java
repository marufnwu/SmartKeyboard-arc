package com.sikderithub.keyboard.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.android.inputmethod.keyboard.KeyboardTheme;
import com.sikderithub.keyboard.Models.Theme;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.local.Dao.QuestionDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CustomThemeHelper {
    private static final String TAG = "CustomThemeHelper";

    public static Theme selectedCustomTheme = null;
    public static int themeId=0;

    public static boolean isCustomThemeApplicable(Context context){
        if(CustomThemeHelper.selectedCustomTheme==null){
            return false;
        }
        return KeyboardTheme.getKeyboardTheme(context).mThemeId==KeyboardTheme.THEME_ID_CUSTOM;
    }

    public static void loadSelectedCustomTheme(){

        Log.d(TAG, "loadSelectedCustomTheme: called");

        themeId = KeyboardTheme.getCustomSelectedThemeId(PreferenceManager.getDefaultSharedPreferences(MyApp.getInstance()));


        if(themeId==0){
            Log.d(TAG, "loadSelectedCustomTheme: themeId==0");
            return;
        }

        QuestionDatabase.databaseWriteExecutor.execute(() -> {
            selectedCustomTheme = QuestionDatabase.getDatabase(MyApp.getInstance())
                    .questionDAO()
                    .getSelectedCustomTheme(themeId);
            
            if(selectedCustomTheme==null){
                Log.d(TAG, "loadSelectedCustomTheme: database retrurn null");
            }else {
                Log.d(TAG, "loadSelectedCustomTheme: theme id "+selectedCustomTheme.id);

            }
        });
    }


    public Drawable getKeyBackground(Context context){
        Drawable drawable = context.getResources().getDrawable(R.drawable.btn_keyboard_key_custom);
        drawable.setAlpha(255);
        return drawable;
    }

    public int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        int dp = Math.round(px / density);
        return dp;
    }


    public static Drawable getKeyboardBackgroundDrawable(Context context, Theme theme){
        if(theme==null){
            Log.d(TAG, "getKeyboardBackgroundDrawable: selectedCustomTheme==null");

            return null;
        }

        if(theme.backgroundImage==null){
            Log.d(TAG, "getKeyboardBackgroundDrawable: selectedCustomTheme.backgroundImage==null");

            return null;
        }

        File file = new File(context.getFilesDir().getPath()+"/"+theme.backgroundImage);


        Log.d(TAG, "getKeyboardBackgroundDrawable: "+file.getName());
        
        
        Drawable drawable  = fileToDrawable(file, context);

        if(theme.imageOverlay!=null){
            drawable.setColorFilter(theme.dominateColor, PorterDuff.Mode.valueOf(theme.imageOverlay));

        }

        return drawable;
    }

    public static Drawable fileToDrawable(File file, Context context){
        byte[] fileBytes = new byte[(int) file.length()];

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Drawable drawable = null;
        if (fileBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length);
            drawable = new BitmapDrawable(context.getResources(), bitmap);
        }

        return drawable;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
