package com.sikderithub.keyboard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.inputmethod.latin.PhoneticBangla;
import com.sikderithub.keyboard.R;

import java.util.List;

public class KeyMapItemAdapter extends RecyclerView.Adapter<KeyMapItemAdapter.MyViewHodler> {

    private List<PhoneticBangla.Char> charList;
    private Context context;

    public KeyMapItemAdapter(List<PhoneticBangla.Char> charList, Context context) {
        this.charList = charList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHodler(LayoutInflater.from(context).inflate(R.layout.layout_key_map_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHodler holder, int position) {

        PhoneticBangla.Char keyMap = charList.get(position);
        holder.bind(keyMap);
    }

    @Override
    public int getItemCount() {
        return charList.size();
    }

    public class MyViewHodler extends RecyclerView.ViewHolder {

        TextView tvValue;
        TextView tvKey;
        public MyViewHodler(@NonNull View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.txtValue);
            tvKey = itemView.findViewById(R.id.txtKey);
        }

        public void bind(PhoneticBangla.Char keyMap){
            tvKey.setText(keyMap.getKey());
            tvValue.setText(keyMap.getValue());
        }
    }
}
