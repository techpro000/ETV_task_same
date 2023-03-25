package com.etv.entity;

/**
 * 通用List grid view的简单展示entity
 */
public class BeanEntity {

    String title;
    int imageId;
    int tagId;


    public BeanEntity(int imageId, String title) {
        this.title = title;
        this.imageId = imageId;
    }


    public BeanEntity(int imageId, String title, int tagId) {
        this.title = title;
        this.imageId = imageId;
        this.tagId = tagId;
    }


    public BeanEntity(String title, int imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public String toString() {
        return "BeanEntity{" +
                "title='" + title + '\'' +
                ", imageId=" + imageId +
                ", tagId=" + tagId +
                '}';
    }
}
