package com.sikderithub.keyboard.local.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.android.inputmethod.utils.GkEngine;
import com.sikderithub.keyboard.Models.Gk;

import java.util.List;

@Dao
public interface QuestionDAO {
    @Query("SELECT * FROM question_table  WHERE isShown = 0 ORDER BY id ASC LIMIT 100")
    List<Gk> getQuestions();
    @Query("SELECT * FROM question_table WHERE isSaved = 1 ORDER BY id ASC LIMIT 20")
    List<Gk> getSavedQuestions();

    @Query("SELECT id FROM question_table ORDER BY id DESC LIMIT 1")
    int getLastQuestionId();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Gk> gkList);

    @Update
    void updateGk(Gk gk);


}
