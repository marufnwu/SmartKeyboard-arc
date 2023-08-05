package com.android.inputmethod.utils;

import android.util.Log;

import com.sikderithub.keyboard.Models.Gk;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.local.Dao.QuestionDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GkEngine  {
    private static final String TAG = "GkEngine";
    public class CurrentGk{
        public Gk gk;
        public int position;

        public CurrentGk(Gk gk, int position) {
            this.gk = gk;
            this.position = position;
        }
    }

    public static List<Gk> cachedGk = new ArrayList<>();

    public static void getGkFromLocal(){
        QuestionDatabase.databaseWriteExecutor.execute(() -> {
            cachedGk = QuestionDatabase.getDatabase(MyApp.getApContext())
                    .questionDAO()
                    .getQuestions();

            Log.d(TAG, "getGkFromLocal: "+cachedGk.size());
        });
    }

    public static void addToSavedGk(CurrentGk currentGk){
        currentGk.gk.isSaved = true;
        cachedGk.add(currentGk.position, currentGk.gk);

        QuestionDatabase.databaseWriteExecutor.execute(() -> {
            QuestionDatabase.getDatabase(MyApp.getApContext())
                    .questionDAO()
                    .updateGk(currentGk.gk);
        });
    }



    public static void setGkAsShown(CurrentGk currentGk){
        cachedGk.remove(currentGk.position);
        currentGk.gk.isShown = true;

        QuestionDatabase.databaseWriteExecutor.execute(() -> {
            QuestionDatabase.getDatabase(MyApp.getApContext())
                    .questionDAO()
                    .updateGk(currentGk.gk);
        });
    }

    private Random random;

    public GkEngine() {
        random = new Random();

    }
    public CurrentGk getGk(){
        if(cachedGk.size()<1){
            return null;
        }

        int gkPosition = random.nextInt(cachedGk.size());
        return new CurrentGk(cachedGk.get(gkPosition), gkPosition);
    }
}
