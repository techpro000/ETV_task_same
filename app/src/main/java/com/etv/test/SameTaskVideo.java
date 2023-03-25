package com.etv.test;

public class SameTaskVideo {
    String filePath;
    int progress;
    String secenId;

    public SameTaskVideo(String filePath, int progress, String secenId) {
        this.filePath = filePath;
        this.progress = progress;
        this.secenId = secenId;
    }

    public String getSecenId() {
        return secenId;
    }

    public void setSecenId(String secenId) {
        this.secenId = secenId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "SameTaskVideo{" +
                "filePath='" + filePath + '\'' +
                ", progress=" + progress +
                '}';
    }
}
