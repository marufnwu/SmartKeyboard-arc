package com.sikderithub.keyboard.Adapter;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sikderithub.keyboard.Models.Gk;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.Views.GkView;

import java.util.List;
import java.util.Objects;

public class SavedGkAdapter extends RecyclerView.Adapter<SavedGkAdapter.MyViewHolder> {
    private static final String TAG = "SavedGkAdapter";
    private List<Gk> gkList;
    private Context context;

    public SavedGkAdapter(List<Gk> gkList, Context context) {
        this.gkList = gkList;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedGkAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_saved_gk_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SavedGkAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Gk gk = gkList.get(position);
        if(gk.has_option==1){
            holder.mNonOptionalGkHolder.setVisibility(GONE);
            holder.mOptionalGkHolder.setVisibility(VISIBLE);

            holder.optionGkText.setText(gk.question);
            holder.option1.setText(gk.option1);
            holder.option2.setText(gk.option2);
            holder.option1.setEnabled(false);
            holder.option2.setEnabled(false);

            if(Objects.equals(gk.option1, gk.answer)){
                holder.option1.setChecked(true);
                holder.option2.setChecked(false);
            } else if (Objects.equals(gk.option2, gk.answer)) {
                holder.option1.setChecked(false);
                holder.option2.setChecked(true);
            }else {
                holder.option1.setChecked(false);
                holder.option2.setChecked(false);
            }

        }else {

            holder.mNonOptionalGkHolder.setVisibility(VISIBLE);
            holder.mOptionalGkHolder.setVisibility(GONE);
            holder.nonOptionGkText.setText(gk.question);

        }

        holder.imgDeleteGk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.removeSavedGk(gk.id);
                gkList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+gkList.size());
        return gkList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgDeleteGk;
        private TextView nonOptionGkText;
        private TextView optionGkText;
        private RadioButton option1;
        private RadioButton  option2;

        private final LinearLayout mOptionalGkHolder;
        private final LinearLayout mNonOptionalGkHolder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mNonOptionalGkHolder = itemView.findViewById(R.id.nonOptionalGkHolder);
            mOptionalGkHolder = itemView.findViewById(R.id.optionalGkHolder);

            nonOptionGkText = itemView.findViewById(R.id.nonOptionGkText);
            optionGkText = itemView.findViewById(R.id.optionGkText);
            imgDeleteGk = itemView.findViewById(R.id.imgDeleteGk);

            option1 = itemView.findViewById(R.id.txtOption1);
            option2 = itemView.findViewById(R.id.option2);
        }
    }
}
