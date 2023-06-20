package com.sikderithub.keyboard;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sikderithub.keyboard.Models.GenericResponse;
import com.sikderithub.keyboard.Models.Gk;
import com.sikderithub.keyboard.internet.MyApi;
import com.sikderithub.keyboard.local.Dao.QuestionDAO;
import com.sikderithub.keyboard.local.Dao.QuestionDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApp extends Application {
    private static final String TAG = "MyApp";
    private static MyApp INSTANCE;
    public static MyApi myApi;
    private int lastQuestionId;

    public static MyApi getMyApi() {
        if (myApi == null) {
            myApi = MyApi.Companion.invoke();
        }
        return myApi;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        MobileAds.initialize(this, initializationStatus -> {

        });
        getLatestQuestion();
    }

   public static synchronized MyApp getInstance(){
        return INSTANCE;
   }

   public  static Context getApContext(){
        return INSTANCE.getApplicationContext();
   }


   private void getLatestQuestion(){
        QuestionDatabase.databaseWriteExecutor.execute(() -> {
            lastQuestionId = QuestionDatabase.getDatabase(getInstance())
                    .questionDAO()
                    .getLastQuestionId();

            Log.d(TAG, "getLatestQuestion: "+lastQuestionId);

            MyApp.getMyApi()
                    .getLatestQuestion(String.valueOf(lastQuestionId))
                    .enqueue(new Callback<GenericResponse<List<Gk>>>() {
                        @Override
                        public void onResponse(@NonNull Call<GenericResponse<List<Gk>>> call, Response<GenericResponse<List<Gk>>> response) {
                            if(response.isSuccessful() && response.body()!=null){
                                if (!response.body().error){
                                    QuestionDatabase.databaseWriteExecutor.execute(() -> {
                                        QuestionDatabase.getDatabase(getInstance())
                                                .questionDAO()
                                                .insert(response.body().data);
                                    });

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GenericResponse<List<Gk>>> call, Throwable t) {

                        }
                    });

        });
   }


}
