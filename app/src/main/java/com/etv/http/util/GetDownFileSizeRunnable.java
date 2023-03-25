package com.etv.http.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetDownFileSizeRunnable implements Runnable {

    @Override
    public void run() {
        getFileSize();
    }

    public void getFileSize() {
        URL url = null;
        String fileUrl = "http://XXXXX";
        try {
            url = new URL(fileUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlcon = null;
        try {
            urlcon = (HttpURLConnection) url.openConnection();
            int fileLength = urlcon.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
