package com.sikderithub.keyboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.android.inputmethod.latin.PhoneticBangla;
import com.sikderithub.keyboard.Adapter.KeyMapCatAdapter;
import com.sikderithub.keyboard.Adapter.KeyMapItemAdapter;
import com.sikderithub.keyboard.R;

public class KeyMapActivity extends AppCompatActivity {

    private RecyclerView recyKeyCat;
    private KeyMapCatAdapter adapter;
    private PhoneticBangla phoneticBangla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Bangla Phonetic Keymap");

        setContentView(R.layout.activity_key_map);



        recyKeyCat  =findViewById(R.id.recyKeyCat);
        recyKeyCat.setHasFixedSize(true);
        recyKeyCat.setLayoutManager(new GridLayoutManager(this, 2));

        phoneticBangla = new PhoneticBangla();

        adapter = new KeyMapCatAdapter(phoneticBangla.getPhoneticKeyMapInList(), this);
        recyKeyCat.setAdapter(adapter);



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}