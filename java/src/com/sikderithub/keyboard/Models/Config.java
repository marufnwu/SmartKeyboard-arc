package com.sikderithub.keyboard.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "config_table")
public class Config {
    @PrimaryKey
    public int id;
    @ColumnInfo(defaultValue = "0")
    public int gk_view_ad_status = 0 ;
    @ColumnInfo(defaultValue = "0")
    public int emoji_view_ad_status=0;
    @ColumnInfo(defaultValue = "0")
    public int gk_ad_interval=0;
    @ColumnInfo(defaultValue = "0")
    public int show_gk=0;
    @ColumnInfo(defaultValue = "0")
    public int show_gk_view=0;
    @ColumnInfo(defaultValue = "1")
    public int gk_view_ad_type=1;

    @ColumnInfo(defaultValue = "2")
    public int content_interval=1;
    @Ignore
    public Update update;
    public String tutorial_link;




}
