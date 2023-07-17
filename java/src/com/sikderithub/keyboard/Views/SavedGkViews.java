package com.sikderithub.keyboard.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sikderithub.keyboard.Adapter.SavedGkAdapter;
import com.sikderithub.keyboard.Models.Gk;
import com.sikderithub.keyboard.Models.Theme;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.Utils.CustomThemeHelper;
import com.sikderithub.keyboard.local.Dao.QuestionDatabase;

import java.util.ArrayList;
import java.util.List;

public class SavedGkViews extends LinearLayout {
    private static final String TAG = "SavedGkViews";
    private RecyclerView recyclerSavedGk;
    private SavedGkAdapter adapter;

    private static List<Gk> cachedGk  =new ArrayList<>();

    public SavedGkViews(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.mainKeyboardViewStyle);
    }

    public SavedGkViews(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.saved_gk_layout, this);

        if(CustomThemeHelper.isCustomThemeApplicable(getContext()) && CustomThemeHelper.selectedCustomTheme!=null){
            Drawable bgDrawable = CustomThemeHelper.getKeyboardBackgroundDrawable(context,CustomThemeHelper.selectedCustomTheme);
            Theme theme = CustomThemeHelper.selectedCustomTheme;
            if (bgDrawable!=null && theme!=null){
                this.setBackground(bgDrawable);
            }
        }

        recyclerSavedGk = findViewById(R.id.recyclerSavedGk);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerSavedGk.setLayoutManager(layoutManager);


        adapter = new SavedGkAdapter(cachedGk, getContext());
        recyclerSavedGk.setAdapter(adapter);

        if(cachedGk.isEmpty()){
            getSavedGkList();
            updateList();
        }

    }




    @SuppressLint("NotifyDataSetChanged")
    public void updateList(){
        adapter.notifyDataSetChanged();
    }


    public void removeFromList(){

    }

    public void addNewToList(Gk gk){
        cachedGk.add(0, gk);
        adapter.notifyItemChanged(0);
    }


    public void getSavedGkList(){
        QuestionDatabase.databaseWriteExecutor.execute(() -> {
            cachedGk.addAll( QuestionDatabase.getDatabase(MyApp.getApContext())
                    .questionDAO()
                    .getSavedQuestions());

            Log.d(TAG, "getSavedGkList: "+cachedGk.size());
        });
    }
}
