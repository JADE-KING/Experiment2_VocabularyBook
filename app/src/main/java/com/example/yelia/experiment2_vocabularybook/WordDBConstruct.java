package com.example.yelia.experiment2_vocabularybook;

import java.util.List;
import java.util.Map;

/**
 * Created by yelia on 2017/10/24.
 */

public abstract class WordDBConstruct {
    final static String DB_NAME = "Words";
    final static int DB_VERSION = 4;
    final static String TABLE_NAME = "WordDetail";
    final static String COLUMN_NAME_ID = "id";
    final static String COLUMN_NAME_WORD = "word";
    final static String COLUMN_NAME_TRANSLATION = "translation";
    final static String COLUMN_NAME_PHONETIC = "phonetic";
    final static String COLUMN_NAME_UKPHONETIC = "ukPhonetic";
    final static String COLUMN_NAME_USPHONETIC = "usPhonetic";
    final static String COLUMN_NAME_EXPLAINS = "explains";
    final static String COLUMN_NAME_WEBEXPLAINS = "webExplains";
    final static String COLUMN_NAME_USERNOTE = "userNote";

    /**
     * 添加一个单词
     * @param data WordDetailData对象
     * @return 成功true, 失败false
     */
    public abstract boolean addWord(WordDetailData data);

    /**
     * 删除一个单词
     * @param id 要删除的单词id
     * @return 成功true, 失败false
     */
    public abstract boolean delWord(int id);

    /**
     * 更新一个单词
     * @param data 更新的资料
     * @return 成功true, 失败false
     */
    public abstract boolean updateWord(WordDetailData data);

    /**
     * 更新用户笔记
     * @param word 单词
     * @param note 笔记
     * @return 成功true, 失败false
     */
    public abstract boolean updateUserNote(String word, String note);

    /**
     * 获取一个单词
     * @param id 单词id
     * @return 返回WordDetailData对象
     */
    public abstract WordDetailData getWord(int id);

    /**
     * 获取列表
     * @param mode 查询模式
     * @param str 搜索用字段(模糊搜索用字符串)
     * @return 返回列表
     */
    public abstract List<Map<String, Object>> getWordList(String mode, String[] str);

    /**
     * 按时间顺序获取单词列表
     * @return 返回单词列表, 按时间顺序
     */
    public abstract List<Map<String, Object>> getWordListByTimeOrder();

    /**
     * 按时间倒序获取单词列表
     * @return 返回单词列表, 按时间倒序
     */
    public abstract List<Map<String, Object>> getWordListReserveTimeOrder();

    /**
     * 按字母顺序获取列表
     * @return 返回单词列表, 按字母顺序
     */
    public abstract List<Map<String, Object>> getWordListByAlphabetOrder();

    /**
     * 按字母倒序获取列表
     * @return 返回单词列表, 按字母倒序
     */
    public abstract List<Map<String, Object>> getWordListReserveAlphabetOrder();

    /**
     * 模糊搜索
     * @param str 字符串
     * @return 返回查询结果列表, 按时间顺序
     */
    public abstract List<Map<String, Object>> getWordListFuzzily(String str);

    /**
     * 获取所有单词详细内容
     * @return 返回单词详细内容列表
     */
    public abstract List<WordDetailData> getWordDetailList();
}
