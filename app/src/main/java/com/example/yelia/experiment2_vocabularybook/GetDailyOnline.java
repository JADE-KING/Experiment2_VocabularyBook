package com.example.yelia.experiment2_vocabularybook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by yelia on 2017/11/3.
 */

public class GetDailyOnline {
    private final static String dailyUrl = "http://open.iciba.com/dsapi/?date=";
    private String date;
    private String content;
    private String note;
    private String pictureUrl;
    private String speakUrl;
    private Bitmap bitPicture;

    public GetDailyOnline(String date) {
        resetDate(date);
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getNote() {
        return note;
    }

    public String getSpeakUrl() {
        return speakUrl;
    }

    public Bitmap getBitPicture() {
        return bitPicture;
    }

    public void resetDate(String date) {
        try {
            // 获取每日一句
            URL url = new URL(dailyUrl + date);
            URLConnection connection = url.openConnection();
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String dataStr = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                dataStr += line;
            }

            JSONObject jsonStr = new JSONObject(dataStr);
            this.date = jsonStr.getString("dateline");
            content = jsonStr.getString("content");
            note = jsonStr.getString("note");
            pictureUrl = jsonStr.getString("picture2");
            speakUrl = jsonStr.getString("tts");

            bufferedReader.close();

            // 获取图片
            url = new URL(pictureUrl);
            connection = url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            bitPicture = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
