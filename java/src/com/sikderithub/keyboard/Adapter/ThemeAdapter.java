package com.sikderithub.keyboard.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.inputmethod.keyboard.Keyboard;
import com.android.inputmethod.keyboard.KeyboardId;
import com.android.inputmethod.keyboard.KeyboardLayoutSet;
import com.android.inputmethod.keyboard.KeyboardTheme;
import com.android.inputmethod.keyboard.MainKeyboardView;
import com.android.inputmethod.latin.RichInputMethodSubtype;
import com.android.inputmethod.latin.settings.Settings;
import com.android.inputmethod.latin.utils.ResourceUtils;
import com.sikderithub.keyboard.R;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHodler> {
    private Context context;
    private KeyboardTheme[] keyboardThemes;
    private Settings mSettings;
    private ContextThemeWrapper mThemeContext;

    public ThemeAdapter(Context context, KeyboardTheme[] keyboardThemes) {
        this.context = context;
        this.keyboardThemes = keyboardThemes;
        mSettings = Settings.getInstance();
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ThemeAdapter.MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //mThemeContext = new ContextThemeWrapper(context, viewType);
        return new MyViewHodler(LayoutInflater.from(context).inflate(R.layout.demo_keyboard_frame, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeAdapter.MyViewHodler holder, int position) {
        //View frame = mCurrentInputView.findViewById(R.id.main_keyboard_frame);

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        ContextThemeWrapper mThemeContext = new ContextThemeWrapper(context, KeyboardTheme.THEME_ID_LXX_LIGHT);

        final KeyboardLayoutSet.Builder builder = new KeyboardLayoutSet.Builder(
                mThemeContext, null);
        final Resources res = mThemeContext.getResources();
        final int keyboardWidth = ResourceUtils.getDefaultKeyboardWidth(res)-20;
        final int keyboardHeight = ResourceUtils.getKeyboardHeight(res, mSettings.getCurrent())-20;
        builder.setKeyboardGeometry(keyboardWidth, keyboardHeight);
        builder.setSubtype(RichInputMethodSubtype.getNoLanguageSubtype());

        builder.disableTouchPositionCorrectionData();


        KeyboardLayoutSet mKeyboardLayoutSet = builder.build();
        holder.mainKeyboardView.setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardTheme.THEME_ID_KLP));


    }

    @Override
    public int getItemViewType(int position) {
        return keyboardThemes[position].mStyleId;
    }

    @Override
    public int getItemCount() {
        return keyboardThemes.length;
    }

    public class MyViewHodler extends RecyclerView.ViewHolder{
      MainKeyboardView mainKeyboardView;
        public MyViewHodler(@NonNull View itemView) {
            super(itemView);

            mainKeyboardView = itemView.findViewById(R.id.keyboard_view);

        }
    }
}
