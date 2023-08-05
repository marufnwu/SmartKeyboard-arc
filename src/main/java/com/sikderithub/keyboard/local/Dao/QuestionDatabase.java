package com.sikderithub.keyboard.local.Dao;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.android.inputmethod.utils.GkEngine;
import com.sikderithub.keyboard.Models.Config;
import com.sikderithub.keyboard.Models.Gk;
import com.sikderithub.keyboard.Models.NotificationData;
import com.sikderithub.keyboard.Models.Theme;
import com.sikderithub.keyboard.Models.Update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Gk.class, Config.class, Update.class, NotificationData.class, Theme.class}, version = 2, exportSchema = false)
public abstract class QuestionDatabase extends RoomDatabase {
    public abstract QuestionDAO questionDAO();
    private static volatile QuestionDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static QuestionDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuestionDatabase.class) {
                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    QuestionDatabase.class, "question_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                QuestionDAO dao = INSTANCE.questionDAO();

//                dao.deleteAll();
//
//                Word word = new Word("Hello");
//                dao.insert(word);
//                word = new Word("World");
//                dao.insert(word);
            });
        }
    };


    @Override
    public void clearAllTables() {

    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(@NonNull DatabaseConfiguration databaseConfiguration) {
        return null;
    }
}
