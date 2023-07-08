package com.sikderithub.keyboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sikderithub.keyboard.BuildConfig;
import com.sikderithub.keyboard.CommonMethod;
import com.sikderithub.keyboard.Models.GenericResponse;
import com.sikderithub.keyboard.Models.Update;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity {

    LinearLayout updateView;
    LinearLayout checkingView;
    LinearLayout uptoDateView;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.english_ime_name)+" App update");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_update);

        updateView = findViewById(R.id.layoutUpdateView);
        checkingView = findViewById(R.id.layoutChecking);
        uptoDateView = findViewById(R.id.layoutUpToDate);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethod.INSTANCE.openAppLink(UpdateActivity.this);
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUpdate();
    }

    private void checkUpdate() {
        checkingView.setVisibility(View.VISIBLE);
        updateView.setVisibility(View.GONE);
        uptoDateView.setVisibility(View.GONE);

        MyApp .getMyApi()
                .getLatestUpdate()
                .enqueue(new Callback<GenericResponse<Update>>() {
                    @Override
                    public void onResponse(Call<GenericResponse<Update>> call, Response<GenericResponse<Update>> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            if(!response.body().error){
                                updateView(response.body().data);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse<Update>> call, Throwable t) {

                    }
                });

    }

    private void updateView(Update data) {
        if (data.version_code>BuildConfig.VERSION_CODE){
            updateView.setVisibility(View.VISIBLE);
            checkingView.setVisibility(View.GONE);
            uptoDateView.setVisibility(View.GONE);
        }else{
            updateView.setVisibility(View.GONE);
            checkingView.setVisibility(View.GONE);
            uptoDateView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}