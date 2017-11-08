package com.example.yelia.experiment2_vocabularybook;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youdao.sdk.app.YouDaoApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static Typeface typeface; // 字体
    private static WordDBImplement db; // 数据库
    private static List<Map<String, Object>> wordList = new ArrayList<Map<String, Object>>(); // 单词列表
    private static List<WordDetailData> wordDetailList = new ArrayList<WordDetailData>(); // 单词详细列表

    private ViewPager viewPager;
    private TextView homePageBtn;
    private TextView translatePageBtn;
    private TextView wordPageBtn;
    private ImageView cursorImg;

    private TextView[] btns; // 按钮
    // private int[] btnsWidth; // 按钮宽度
    private float cursorX;
    private static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 有道SDK注册应用id
        YouDaoApplication.init(this, "1c3ee0b43294db24");

        // 初始化字体
        typeface = Typeface.createFromAsset(getAssets(), "fonts/YaheiConsolasHybrid.ttf");

        // 初始化数据库
        db = new WordDBImplement(this);

        // 更新数据库结构, 调试用
        // db.dbUpgrade(3, WordDBConstruct.DB_VERSION);

        // 初始化单词列表
        wordList = db.getWordListReserveTimeOrder();
        wordDetailList = db.getWordDetailList();

        // viewPager加载Fragments
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new HomePageFragment());
        fragments.add(new TranslateFragment());
        fragments.add(new WordPageFragment());

        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setOnPageChangeListener(onPageChangeListener);

        // 导航和滑动条
        homePageBtn = (TextView)findViewById(R.id.homePageBtn);
        translatePageBtn = (TextView)findViewById(R.id.translatePageBtn);
        wordPageBtn = (TextView)findViewById(R.id.wordPageBtn);
        cursorImg = (ImageView)findViewById(R.id.cursorImg);

        homePageBtn.setOnClickListener(onClickListener);
        translatePageBtn.setOnClickListener(onClickListener);
        wordPageBtn.setOnClickListener(onClickListener);

        btns = new TextView[] { homePageBtn, translatePageBtn, wordPageBtn };

        if (savedInstanceState == null) {
            position = 0;
        }
        else {
            position = savedInstanceState.getInt("position");
        }

        // 设置初始宽度
        homePageBtn.post(new Runnable() {
            @Override
            public void run() {
                cursorAnimation(position);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("position", position);
    }

    public static Typeface getTypeface() {
        return typeface;
    }

    public static WordDBImplement getDb() {
        return db;
    }

    public static List<Map<String, Object>> getWordList() {
        return wordList;
    }

    public static List<WordDetailData> getWordDetailList() {
        return wordDetailList;
    }

    public static void setWordList(List<Map<String, Object>> wordList) {
        MainActivity.wordList = wordList;
    }

    public static void setWordDetailList(List<WordDetailData> wordDetailList) {
        MainActivity.wordDetailList = wordDetailList;
    }

    /**
     * wordList 通过位置获取id
     * @param position 位置
     * @return id
     */
    public static int getID(int position) {
        return (int)wordList.get(position).get(WordDBConstruct.COLUMN_NAME_ID);
    }

    /**
     * wordDetailList 通过id获取位置
     * @param id id
     * @return 位置
     */
    public static int getPosition(int id) {
        int position = -1;
        if (id >= 0) {
            for (int i = 0; i < wordDetailList.size(); i++) {
                if (wordDetailList.get(i).getId() == id) {
                    position = i;
                    return position;
                }
            }
        }
        return position;
    }

    /**
     * wordList 通过word获取id (列表中是否有该word)
     * @param word 词
     * @return 若有返回id, 无返回-1
     */
    public static int wordListIdOf(String word) {
        int id = -1;
        for (int i = 0; i < wordList.size(); i++) {
            if (wordList.get(i).get(WordDBConstruct.COLUMN_NAME_WORD).toString().equals(word)) {
                id = (int)wordList.get(i).get(WordDBConstruct.COLUMN_NAME_ID);
                return id;
            }
        }
        return id;
    }

    // 隐藏软键盘, 无状态判断, 不好用
    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.toggleSoftInput(0, 0);
        }
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            MainActivity.position = position;
            cursorAnimation(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.homePageBtn:
                    position = 0;
                    break;
                case R.id.translatePageBtn:
                    position = 1;
                    break;
                case R.id.wordPageBtn:
                    position = 2;
                    break;
            }
            viewPager.setCurrentItem(position);
            cursorAnimation(position);
        }
    };

    // 滑动条实现
    private void cursorAnimation(int position) {
        // 初始化位置
        cursorX = 0f;
        // 设置宽度
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)cursorImg.getLayoutParams();
        layoutParams.width = btns[position].getWidth();
        cursorImg.setLayoutParams(layoutParams);
        // 获取位置
        for (int i = 0; i < position; i++) {
            cursorX += btns[position].getWidth();
        }
        cursorImg.setX(cursorX);
    }
}
