package com.sikderithub.keyboard.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.inputmethod.keyboard.KeyboardTheme;
import com.sikderithub.keyboard.Models.Theme;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.Utils.CustomThemeHelper;

import java.util.List;


public class CustomThemeAdapter extends RecyclerView.Adapter<CustomThemeAdapter.ViewHolder> {


    private Context context;
    private List<Theme> themeList;
    private ItemClickListener itemClickListener;
    private int selectedPosition = 0;



    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CustomThemeAdapter(Context context, List<Theme> themeList) {
        this.context = context;
        this.themeList = themeList;
    }

    public interface ItemClickListener{
        void onThemeItemClick(Theme theme);
        void onCreateThemeClick();
        void onDeleteThemeClick(Theme theme, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_custom_theme_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position==0){
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.ivThemeBackground.setImageDrawable(null);
            holder.ivThemeBackground.setEnabled(false);
            holder.ivSelected.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
        }else{
            holder.ivAdd.setVisibility(View.GONE);
            holder.ivThemeBackground.setEnabled(true);
            Drawable bgDrawable = CustomThemeHelper.getKeyboardBackgroundDrawable(context,themeList.get(position));
            holder.ivThemeBackground.setImageDrawable(bgDrawable);
            holder.ivDelete.setVisibility(View.VISIBLE);


            if(CustomThemeHelper.isCustomThemeApplicable(context) && themeList.get(position).id==CustomThemeHelper.selectedCustomTheme.id){
                selectedPosition = position;
                holder.ivSelected.setVisibility(View.VISIBLE);
            }else{
                holder.ivSelected.setVisibility(View.GONE);
            }

            holder.ivDelete.setOnClickListener(v -> {
                if(itemClickListener!=null){
                    itemClickListener.onDeleteThemeClick(themeList.get(position), position);
                }
            });

        }

        holder.itemView.setOnClickListener(v -> {
            if(itemClickListener!=null){

                if (position==0){
                    itemClickListener.onCreateThemeClick();
                }else{
                    Theme theme = themeList.get(position);
                    itemClickListener.onThemeItemClick(theme);
                    CustomThemeHelper.selectedCustomTheme = theme;
                    notifyDataSetChanged();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThemeBackground;
        ImageView ivAdd;
        ImageView ivSelected;
        ImageView ivDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivThemeBackground = itemView.findViewById(R.id.ivThemeBg);
            ivAdd = itemView.findViewById(R.id.ivAdd);
            ivSelected = itemView.findViewById(R.id.ivSelected);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
