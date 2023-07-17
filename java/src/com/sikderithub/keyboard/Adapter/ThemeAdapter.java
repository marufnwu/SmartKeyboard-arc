package com.sikderithub.keyboard.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.inputmethod.keyboard.Keyboard;
import com.android.inputmethod.keyboard.KeyboardActionListener;
import com.android.inputmethod.keyboard.KeyboardLayoutSet;
import com.android.inputmethod.keyboard.KeyboardTheme;
import com.android.inputmethod.keyboard.MainKeyboardView;
import com.android.inputmethod.latin.RichInputMethodSubtype;
import com.android.inputmethod.latin.common.InputPointers;
import com.android.inputmethod.latin.settings.Settings;
import com.android.inputmethod.latin.settings.SettingsValues;
import com.android.inputmethod.latin.utils.ResourceUtils;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.ThemeMainKeyboardView;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHodler> {
    private Context context;
    private KeyboardTheme[] keyboardThemes;
    private SettingsValues currentSettingsValues;
    private ContextThemeWrapper mThemeContext;
    private int selectedThemeId;
    private int columnWidth = 0;
    private Keyboard keybaord;
    private DefaultThemeSelectListener defaultThemeSelectListener;


    public interface DefaultThemeSelectListener{
        void onSelect(int themeId);
    }

    public void setDefaultThemeSelectListener(DefaultThemeSelectListener defaultThemeSelectListener) {
        this.defaultThemeSelectListener = defaultThemeSelectListener;
    }

    public ThemeAdapter(Context context, KeyboardTheme[] keyboardThemes) {
        this.context = context;
        this.keyboardThemes = keyboardThemes;
        currentSettingsValues = Settings.getInstance().getCurrent();
        selectedThemeId = KeyboardTheme.getKeyboardTheme(context).mThemeId;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mThemeContext = new ContextThemeWrapper(context, viewType);
        return new MyViewHodler(LayoutInflater.from(mThemeContext).inflate(R.layout.demo_keyboard_frame, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHodler holder, int position) {
        //View frame = mCurrentInputView.findViewById(R.id.main_keyboard_frame);


        ContextThemeWrapper mThemeContext = new ContextThemeWrapper(context, KeyboardTheme.THEME_ID_LXX_LIGHT);

        final KeyboardLayoutSet.Builder builder = new KeyboardLayoutSet.Builder(
                mThemeContext, null);
        final Resources res = mThemeContext.getResources();

        float itemMarginDP = res.getDimension(R.dimen.theme_keyboard_margin);
        float pixelValue = itemMarginDP * (res.getDisplayMetrics().densityDpi / 160f);

        int pixelItemMarginValue = (int) pixelValue;





        final int keyboardWidth = ResourceUtils.getDefaultKeyboardWidth(res)/2;
        final int keyboardHeight = ResourceUtils.getKeyboardHeight(res, currentSettingsValues)/2;
        builder.setKeyboardGeometry(columnWidth, keyboardHeight-pixelItemMarginValue);

        builder.setSubtype(RichInputMethodSubtype.getNoLanguageSubtype());


        KeyboardLayoutSet mKeyboardLayoutSet = builder.build();

        keybaord = mKeyboardLayoutSet.getKeyboard(KeyboardTheme.THEME_ID_KLP);
        holder.mainKeyboardView.setKeyboard(keybaord);



        if(keyboardThemes[position].mThemeId==selectedThemeId){
            holder.imgSelected.setVisibility(View.VISIBLE);
        }else{
            holder.imgSelected.setVisibility(View.GONE);
        }

        holder.txtThemeName.setText(keyboardThemes[position].mThemeName);


    }

    @Override
    public int getItemViewType(int position) {
        return keyboardThemes[position].mStyleId;
    }

    @Override
    public int getItemCount() {
        return keyboardThemes.length;
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth-10;
        notifyDataSetChanged();
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public class MyViewHodler extends RecyclerView.ViewHolder{
        ThemeMainKeyboardView mainKeyboardView;
      ImageView imgSelected;
      TextView txtThemeName;
      LinearLayout clickParent;
        public MyViewHodler(@NonNull View itemView) {
            super(itemView);

            mainKeyboardView = itemView.findViewById(R.id.keyboardView);
            imgSelected  = itemView.findViewById(R.id.imgSelected);
            txtThemeName  = itemView.findViewById(R.id.txtThemeName);
            clickParent  = itemView.findViewById(R.id.clickParent);

            clickParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedThemeId = keyboardThemes[getAdapterPosition()].mThemeId;

                    if(defaultThemeSelectListener!=null){
                        defaultThemeSelectListener.onSelect(selectedThemeId);
                    }

                    notifyDataSetChanged();
                }
            });


        }
    }



}
