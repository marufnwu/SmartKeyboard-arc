package com.sikderithub.keyboard.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "theme_table")
public class Theme {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String backgroundImage;
    @ColumnInfo(defaultValue = "1")
    public Boolean showKeyBorder = true;
    public int keyOpacity = 255;
    public int dominateColor;
    public int bodyTextColor;
    public String imageOverlay;

    public Theme(String backgroundImage, Boolean showKeyBorder, int keyOpacity, int dominateColor, int bodyTextColor, String imageOverlay) {
        this.backgroundImage = backgroundImage;
        this.showKeyBorder = showKeyBorder;
        this.keyOpacity = keyOpacity;
        this.dominateColor = dominateColor;
        this.bodyTextColor = bodyTextColor;
        this.imageOverlay = imageOverlay;
    }

    public Theme() {
    }
}
