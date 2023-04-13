package com.etv.entity;

import org.litepal.crud.LitePalSupport;

/***
 * 节目字体实体类
 *
 */
public class FontEntity extends LitePalSupport {

    int fontId;
    String fontName;
    long fontSize;
    String fontDownUrl;
    String creatTime;
    String downName;

    public FontEntity(int fontId, String fontName, long fontSize, String fontDownUrl, String downName, String creatTime) {
        this.fontId = fontId;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.fontDownUrl = fontDownUrl;
        this.downName = downName;
        this.creatTime = creatTime;
    }

    public String getDownName() {
        return downName;
    }

    public void setDownName(String downName) {
        this.downName = downName;
    }

    public int getFontId() {
        return fontId;
    }

    public void setFontId(int fontId) {
        this.fontId = fontId;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public long getFontSize() {
        return fontSize;
    }

    public void setFontSize(long fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontDownUrl() {
        return fontDownUrl;
    }

    public void setFontDownUrl(String fontDownUrl) {
        this.fontDownUrl = fontDownUrl;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    @Override
    public String toString() {
        return "FontEntity{" +
                "fontId=" + fontId +
                ", fontName='" + fontName + '\'' +
                ", fontSize=" + fontSize +
                ", fontDownUrl='" + fontDownUrl + '\'' +
                ", downName='" + downName + '\'' +
                '}';
    }
}
