package com.sikderithub.keyboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;

import com.android.inputmethod.keyboard.KeyboardId;
import com.android.inputmethod.keyboard.KeyboardLayoutSet;
import com.android.inputmethod.keyboard.KeyboardSwitcher;
import com.android.inputmethod.keyboard.KeyboardTheme;
import com.android.inputmethod.keyboard.MainKeyboardView;
import com.android.inputmethod.latin.InputView;
import com.android.inputmethod.latin.RichInputMethodManager;
import com.android.inputmethod.latin.RichInputMethodSubtype;
import com.android.inputmethod.latin.define.ProductionFlags;
import com.android.inputmethod.latin.settings.Settings;
import com.android.inputmethod.latin.utils.ResourceUtils;
import com.android.inputmethod.latin.utils.StatsUtilsManager;
import com.sikderithub.keyboard.Adapter.ThemeAdapter;
import com.sikderithub.keyboard.R;

import java.util.ArrayList;
import java.util.List;

public class ThemeDemoActivity extends AppCompatActivity {
    private static final String TAG = "ThemeDemoActivity";


    private ContextThemeWrapper mThemeContext;
    private KeyboardTheme mKeyboardTheme;
    private RichInputMethodManager mRichImm;
    private Settings mSettings;
    private KeyboardLayoutSet mKeyboardLayoutSet;
    private View mCurrentInputView;
    private KeyboardTheme[] themes;
    private List<MainKeyboardView> mainKeyboardViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_demo);
        
        EditorInfo editorInfo = new EditorInfo();
        mRichImm = RichInputMethodManager.getInstance();
        mSettings = Settings.getInstance();

        themes = KeyboardTheme.KEYBOARD_THEMES;


        RecyclerView recyclerView = findViewById(R.id.recyThemes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mThemeContext = new ContextThemeWrapper(this, R.style.KeyboardTheme_ICS);
        ThemeAdapter adapter = new ThemeAdapter(mThemeContext, themes);
        recyclerView.setAdapter(adapter);







    }

    private boolean updateKeyboardThemeAndContextThemeWrapper(final Context context,
                                                              final KeyboardTheme keyboardTheme) {
        if (mThemeContext == null || !keyboardTheme.equals(mKeyboardTheme)
                || !mThemeContext.getResources().equals(context.getResources())) {
            mKeyboardTheme = keyboardTheme;
            mThemeContext = new ContextThemeWrapper(context, R.style.KeyboardTheme_ICS);
            return true;
        }
        return false;
    }

    private @NonNull Context getDisplayContext() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // createDisplayContext is not available.
            return this;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            // IME context sources is now managed by WindowProviderService from Android 12L.
            return this;
        }
        // An issue in Q that non-activity components Resources / DisplayMetrics in
        // Context doesn't well updated when the IME window moving to external display.
        // Currently we do a workaround is to create new display context directly and re-init
        // keyboard layout with this context.
        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        return createDisplayContext(wm.getDefaultDisplay());
    }


}