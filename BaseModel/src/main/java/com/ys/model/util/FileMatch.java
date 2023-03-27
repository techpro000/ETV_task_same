package com.ys.model.util;

import com.ys.model.entity.FileEntity;

/**
 * 判断文件的格式
 */
public class FileMatch {

    public static int fileMatch(String name) {
        name = name.toLowerCase().trim();
        if (name.endsWith("mp3")
                || name.endsWith("m4a")
                || name.endsWith("amr")
                || name.endsWith("ogg")
                || name.endsWith("pcm")
                || name.endsWith("wma")
                || name.endsWith("wav")
                || name.endsWith("asf")
                || name.endsWith("aac")
                || name.endsWith("vqf")
                || name.endsWith("mp3pro")
                || name.endsWith("flac")
                || name.endsWith("ape")
                || name.endsWith("aac")) {
            return FileEntity.STYLE_FILE_MUSIC;
        } else if (name.endsWith("jpg")
                || name.endsWith("jpeg")
                || name.endsWith("png")
                || name.endsWith("bmp")
                || name.endsWith("gif")
                || name.endsWith("webp")) {
            return FileEntity.STYLE_FILE_IMAGE;
        } else if (name.endsWith("mp4")
                || name.endsWith("3gp")
                || name.endsWith("mov")
                || name.endsWith("mpg")
                || name.endsWith("wmv")
                || name.endsWith("flv")
                || name.endsWith("avi")
                || name.endsWith("ts")
                || name.endsWith("rmvb")
                || name.endsWith("mkv")) {
            return FileEntity.STYLE_FILE_VIDEO;
        } else if (name.endsWith("txt")
                || name.endsWith("log") ||
                name.endsWith("xml") ||
                name.endsWith("java") ||
                name.endsWith("html")) {
            return FileEntity.STYLE_FILE_TXT;
        } else if (name.endsWith("zip")
                || name.endsWith("rar")) {
            return FileEntity.STYLE_FILE_ZIP;
        } else if (name.endsWith("apk")) {
            return FileEntity.STYLE_FILE_APK;
        } else if (name.endsWith("ppt") || name.endsWith("pptx")) {
            return FileEntity.STYLE_FILE_PPT;
        } else if (name.endsWith("doc") || name.endsWith("docx")) {
            return FileEntity.STYLE_FILE_DOC;
        } else if (name.endsWith("xls") || name.endsWith("xlsx")) {
            return FileEntity.STYLE_FILE_EXCEL;
        } else if (name.endsWith("pdf")) {
            return FileEntity.STYLE_FILE_PDF;
        } else if (name.endsWith("wbd")) {
            return FileEntity.STYLE_FILE_WHITE_BROAD;
        }
        return FileEntity.STYLE_FILE_OTHER;
    }
}
