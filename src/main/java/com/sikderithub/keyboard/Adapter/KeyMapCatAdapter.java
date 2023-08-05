package com.sikderithub.keyboard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.inputmethod.latin.PhoneticBangla;
import com.sikderithub.keyboard.R;

import java.util.List;

public class KeyMapCatAdapter extends RecyclerView.Adapter<KeyMapCatAdapter.MyViewHodler> {

    private List<PhoneticBangla.KeyMap> keyMapList;
    private Context context;

    public KeyMapCatAdapter(List<PhoneticBangla.KeyMap> keyMapList, Context context) {
        this.keyMapList = keyMapList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHodler(LayoutInflater.from(context).inflate(R.layout.key_map_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHodler holder, int position) {

        PhoneticBangla.KeyMap keyMap = keyMapList.get(position);
        holder.bind(keyMap);
    }

    @Override
    public int getItemCount() {
        return keyMapList.size();
    }

    public class MyViewHodler extends RecyclerView.ViewHolder {

        RecyclerView rvMapItem;
        TextView tvTitle;

        public MyViewHodler(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            rvMapItem = itemView.findViewById(R.id.rvKeyCat);
        }

        public void bind(PhoneticBangla.KeyMap keyMap) {
            tvTitle.setText(keyMap.getTitle());
            KeyMapItemAdapter adapter = new KeyMapItemAdapter(keyMap.getCharList(), context);
            rvMapItem.setLayoutManager(new LinearLayoutManager(context));
            rvMapItem.setHasFixedSize(true);
            rvMapItem.setAdapter(adapter);

        }
    }
}
