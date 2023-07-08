package com.sikderithub.keyboard.local.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.android.inputmethod.utils.GkEngine;
import com.sikderithub.keyboard.Models.Config;
import com.sikderithub.keyboard.Models.Gk;
import com.sikderithub.keyboard.Models.NotificationData;

import java.util.List;

@Dao
public interface QuestionDAO {
    @Query("SELECT * FROM question_table  WHERE isShown = 0 ORDER BY id ASC LIMIT 100")
    List<Gk> getQuestions();
    @Query("SELECT * FROM question_table WHERE isSaved = 1 ORDER BY id ASC LIMIT 20")
    List<Gk> getSavedQuestions();

    @Query("SELECT id FROM question_table ORDER BY id DESC LIMIT 1")
    int getLastQuestionId();
    @Query("DELETE FROM question_table")
    void clearQuestionTable();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Gk> gkList);

    @Update
    void updateGk(Gk gk);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateConfig(Config config);
    @Query("SELECT * FROM config_table ORDER BY id ASC LIMIT 1")
    Config getConfig();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateUpdateInfo(com.sikderithub.keyboard.Models.Update update);

    @Query("SELECT * FROM update_info_table ORDER BY id DESC LIMIT 1")
    com.sikderithub.keyboard.Models.Update getUpdateInfo();

    @Query("DELETE FROM update_info_table")
    void clearUpdateInfo();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveNotification(NotificationData notificationData);

    @Query("SELECT * FROM notification_table ORDER BY id DESC LIMIT 1")
    NotificationData getLastNotification();

    @Query("DELETE FROM notification_table WHERE id= :id")
    void removeNotification(int id);
}
