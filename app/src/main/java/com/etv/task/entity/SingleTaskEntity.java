package com.etv.task.entity;

import java.util.List;

/**
 * 单机模式封装的实体类
 */
public class SingleTaskEntity {

    List<MediAddEntity> list_image;   //包含所有的图片
    List<MediAddEntity> list_video;   //包含所有的视频
    List<MediAddEntity> list_doc;     //文本信息
    List<MediAddEntity> list_music;    //音频信息
    List<MediAddEntity> listsEntity;  //包含所有的信息

    List<MediAddEntity> list_image_double;   //副屏包含所有的图片
    List<MediAddEntity> list_video_double;   //副屏包含所有的视频
    List<MediAddEntity> list_doc_double;     //文本信息
    List<MediAddEntity> list_music_double;    //音频信息
    List<MediAddEntity> listsEntity_double;  //副屏包含所有的信息

    public List<MediAddEntity> getList_music() {
        return list_music;
    }

    public void setList_music(List<MediAddEntity> list_music) {
        this.list_music = list_music;
    }

    public List<MediAddEntity> getList_music_double() {
        return list_music_double;
    }

    public void setList_music_double(List<MediAddEntity> list_music_double) {
        this.list_music_double = list_music_double;
    }

    public List<MediAddEntity> getList_doc() {
        return list_doc;
    }

    public void setList_doc(List<MediAddEntity> list_doc) {
        this.list_doc = list_doc;
    }

    public List<MediAddEntity> getList_doc_double() {
        return list_doc_double;
    }

    public void setList_doc_double(List<MediAddEntity> list_doc_double) {
        this.list_doc_double = list_doc_double;
    }

    public List<MediAddEntity> getList_image_double() {
        return list_image_double;
    }

    public void setList_image_double(List<MediAddEntity> list_image_double) {
        this.list_image_double = list_image_double;
    }

    public List<MediAddEntity> getList_video_double() {
        return list_video_double;
    }

    public void setList_video_double(List<MediAddEntity> list_video_double) {
        this.list_video_double = list_video_double;
    }

    public List<MediAddEntity> getListsEntity_double() {
        return listsEntity_double;
    }

    public void setListsEntity_double(List<MediAddEntity> listsEntity_double) {
        this.listsEntity_double = listsEntity_double;
    }

    public List<MediAddEntity> getListsEntity() {
        return listsEntity;
    }

    public void setListsEntity(List<MediAddEntity> listsEntity) {
        this.listsEntity = listsEntity;
    }


    public List<MediAddEntity> getList_image() {
        return list_image;
    }

    public void setList_image(List<MediAddEntity> list_image) {
        this.list_image = list_image;
    }

    public List<MediAddEntity> getList_video() {
        return list_video;
    }

    public void setList_video(List<MediAddEntity> list_video) {
        this.list_video = list_video;
    }


}
