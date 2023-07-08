package com.sikderithub.keyboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.android.inputmethod.keyboard.KeyboardLayoutSet;
import com.android.inputmethod.keyboard.KeyboardTheme;
import com.android.inputmethod.keyboard.MainKeyboardView;
import com.sikderithub.keyboard.Adapter.ThemeAdapter;
import com.sikderithub.keyboard.ThemeMainKeyboardView;
import com.android.inputmethod.latin.RichInputMethodManager;
import com.android.inputmethod.latin.settings.Settings;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.Views.MyRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ThemeActivity extends AppCompatActivity {
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
        getSupportActionBar().setTitle("Themes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_theme_demo);
        
        EditorInfo editorInfo = new EditorInfo();
        mRichImm = RichInputMethodManager.getInstance();
        mSettings = Settings.getInstance();

        themes = KeyboardTheme.KEYBOARD_THEMES;


        MyRecyclerView recyclerView = findViewById(R.id.recyThemes);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}