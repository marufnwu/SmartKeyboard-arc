package com.sikderithub.keyboard.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "update_info_table")
public class Update {
    @PrimaryKey
    public int id;
    @ColumnInfo(defaultValue = "0")
    public int version_code=0;
    public String image_url;
    public String referLink;
    @ColumnInfo(defaultValue = "0")
    public int status=0;
    @ColumnInfo(defaultValue = "0")
    public int update_type=0;



}
