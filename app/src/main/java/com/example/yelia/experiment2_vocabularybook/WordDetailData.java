package com.example.yelia.experiment2_vocabularybook;

import android.database.Cursor;

import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.WebExplain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelia on 2017/10/23.
 */

public class WordDetailData {
    private int id; // 单词id, 数据库排序用
    private String language; // 确定语言
    private String word; // 单词
    private String translation; // 翻译

    private String phonetic; // 音标
    private String ukPhonetic; // 英音音标
    private String usPhonetic; // 美音音标
    private String speakUrl; // 音频
    private String ukSpeakUrl; // 英音音频
    private String usSpeakUrl; // 美音音频

    private List<String> explains = new ArrayList<String>(); // 释义
    private List<WebExplain> webExplains = new ArrayList<WebExplain>(); // 网络释义
    private String explainString;
    private String webExplainString;
    private String deepLink; // 详细释义
    private String userNote; // 用户笔记

    private final static String speakUrlHead = "http://dict.youdao.com/dictvoice?audio=";
    private final static String ukSpeakUrlHead = "http://dict.youdao.com/dictvoice?type=1&audio=";
    private final static String usSpeakUrlHead = "http://dict.youdao.com/dictvoice?type=2&audio=";
    private final static String deeplinkHead = "http://m.youdao.com/dict?le=eng&q=";

    /**
     * 初始化从网络获取的单词数据
     * @param translate 网络获取结果
     */
    public WordDetailData(Translate translate) {
        language = translate.getFrom();
        word = translate.getQuery();
        translation = "";
        for (int i = 0; i < translate.getTranslations().size(); i++) {
            if (i > 0) translation +="；";
            translation += translate.getTranslations().get(i);
        }

        phonetic = translate.getPhonetic();
        ukPhonetic = translate.getUkPhonetic();
        usPhonetic = translate.getUsPhonetic();
        if (language.equals("EN")) {
            this.initUrl();
        }

        explains = translate.getExplains();
        explainString = this.explainsToString();
        webExplains = translate.getWebExplains();
        webExplainString = this.webExplainsToString();
        deepLink = translate.getDeeplink();
    }

    /**
     * 初始化从数据库获取的单词数据
     * @param cursor 数据库获取结果
     */
    public WordDetailData(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_ID));
        language = "EN";
        word = cursor.getString(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_WORD));
        translation = cursor.getString(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_TRANSLATION));

        phonetic = cursor.getString(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_PHONETIC));
        ukPhonetic = cursor.getString(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_UKPHONETIC));
        usPhonetic = cursor.getString(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_USPHONETIC));
        this.initUrl();

        explainString = cursor.getString(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_EXPLAINS));
        webExplainString = cursor.getString(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_WEBEXPLAINS));
        userNote = cursor.getString(cursor.getColumnIndex(WordDBConstruct.COLUMN_NAME_USERNOTE));
    }

    /**
     * 初始化读音和deeplink网址
     */
    public void initUrl() {
        speakUrl = speakUrlHead + word;
        ukSpeakUrl = ukSpeakUrlHead + word;
        usSpeakUrl = usSpeakUrlHead + word;
        deepLink = deeplinkHead + word;
    }

    public String explainsToString() {
        String str = "";
        try {
            for (int i = 0; i < explains.size(); i++) {
                str += explains.get(i);
                if (i < explains.size() - 1) str += "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String webExplainsToString() {
        String str = "";
        try {
            for (int i = 0; i < webExplains.size(); i++) {
                str += webExplains.get(i).getKey() + "：";
                for (int j = 0; j < webExplains.get(i).getMeans().size(); j++) {
                    if (j > 0) str += "；";
                    str += webExplains.get(i).getMeans().get(j);
                }
                if (i < webExplains.size() - 1) str += "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public String toString() {
        String str = "";
        if (word != null) {
            str = word + " /" + phonetic + "/\n";
            str += translation + "\n";
            str += "英 /" + ukPhonetic + "/\n";
            str += "美 /" + usPhonetic + "/\n";
            str += "释义：\n";
            str += explainString + "\n";
            str += "网络释义：\n";
            str += webExplainString + "\n";
            str += "用户笔记：\n";
            str += userNote + "\n";
            str += "查看更多：\n";
            str += deepLink;
        }
        return str;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public String getUkPhonetic() {
        return ukPhonetic;
    }

    public String getUsPhonetic() {
        return usPhonetic;
    }

    public String getSpeakUrl() {
        return speakUrl;
    }

    public String getUkSpeakUrl() {
        return ukSpeakUrl;
    }

    public String getUsSpeakUrl() {
        return usSpeakUrl;
    }

    public String getExplainString() {
        return explainString;
    }

    public String getWebExplainString() {
        return webExplainString;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }
}
