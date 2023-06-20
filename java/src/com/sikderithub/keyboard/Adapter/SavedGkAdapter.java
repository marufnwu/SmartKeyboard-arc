package com.sikderithub.keyboard.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sikderithub.keyboard.Models.Gk;
import com.sikderithub.keyboard.R;

import java.util.List;

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
    public void onBindViewHolder(@NonNull SavedGkAdapter.MyViewHolder holder, int position) {
        holder.txtGkQuestion.setText(gkList.get(position).question);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+gkList.size());
        return gkList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtGkQuestion;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGkQuestion = itemView.findViewById(R.id.txtQuestion);
        }
    }
}
