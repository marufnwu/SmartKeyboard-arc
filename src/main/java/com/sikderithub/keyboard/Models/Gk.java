package com.sikderithub.keyboard.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "question_table")
public class Gk{
    @PrimaryKey
    @NonNull
    public int id;
    public String question;
    public String option1;
    public String option2;
    public String option3;
    public String option4;
    public int has_option;
    public String answer;
    public String time;
    @ColumnInfo(defaultValue = "0")
    public boolean isShown;
    @ColumnInfo(defaultValue = "0")
    public boolean isSaved;


}