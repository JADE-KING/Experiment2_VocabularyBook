package com.example.yelia.experiment2_vocabularybook;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.youdao.sdk.app.Language;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydonlinetranslate.TranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yelia on 2017/10/18.
 */

public class HomePageFragment extends Fragment {
    private static WordDetailData temporaryData; // 临时数据

    private static GetDailyOnline daily;
    private static int _year;
    private static int _month;
    private static int _day;
    private static String date;
    private static ImageView dailyImage;
    private static TextView dailyDate;
    private static TextView dailyContent;
    private static TextView dailyNote;
    private Thread initThread = new Thread(new Runnable() {
        @Override
        public void run() {
            daily = new GetDailyOnline(date);
        }
    });
    private Thread resetThread = new Thread(new Runnable() {
        @Override
        public void run() {
            daily.resetDate(date);
        }
    });

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            _year = year;
            _month = month + 1;
            _day = dayOfMonth;
            date = year + "-" + (month + 1) + "-" + dayOfMonth;

            resetThread.start();
            try {
                resetThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            date = daily.getDate();
            refreshDaily();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_page, container, false);

        SearchView wordSearchOnline = (SearchView)view.findViewById(R.id.wordSearchOnline);
        wordSearchOnline.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Language from = Language.AUTO;
                Language to = Language.AUTO;

                TranslateParameters tps = new TranslateParameters.Builder()
                        .source("vb").from(from).to(to).timeout(3000).build();

                Translator translator = Translator.getInstance(tps);

                translator.lookup(query, new TranslateListener() {
                    @Override
                    public void onResult(Translate translate, String s) {
                        // 获取网络数据
                        temporaryData = new WordDetailData(translate);

                        // 查看wordList中是否有该word
                        int id = MainActivity.wordListIdOf(temporaryData.getWord());
                        if (id >= 0) {
                            // 如果有, 更新数据库中该word的数据
                            MainActivity.getDb().updateWord(temporaryData);
                            // 更新数据库后更新wordDetailList
                            MainActivity.setWordDetailList(MainActivity.getDb().getWordDetailList());
                        }

                        Fragment wordDetailFragment = new WordDetailFragment();

                        // 传输词汇id到WordDetailFragment
                        Bundle bundle = new Bundle();
                        bundle.putInt(WordDBConstruct.COLUMN_NAME_ID, id);
                        wordDetailFragment.setArguments(bundle);

                        // 加载WordDetailFragment
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.homePageLinear, wordDetailFragment);

                        // HomePage加入退回栈顶
                        fragmentTransaction.addToBackStack(null);

                        // 提交
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onError(TranslateErrorCode translateErrorCode) {
                        Toast.makeText(getContext(), "您没有链接网络哦~", Toast.LENGTH_LONG).show();
                    }
                });

                MainActivity.hideSoftInput(view);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        final ImageView dailySpeak = (ImageView)view.findViewById(R.id.dailySpeak);
        TextView dailyTitle = (TextView)view.findViewById(R.id.dailyTitle);

        dailyImage = (ImageView)view.findViewById(R.id.dailyImage);
        dailyDate = (TextView)view.findViewById(R.id.dailyDate);
        dailyContent = (TextView)view.findViewById(R.id.dailyContent);
        dailyNote = (TextView)view.findViewById(R.id.dailyNote);

        dailyTitle.setTextColor(Color.BLACK);
        dailyTitle.setTypeface(MainActivity.getTypeface());

        dailyDate.setTextColor(Color.BLACK);
        dailyDate.setTypeface(MainActivity.getTypeface());

        dailyContent.setTextColor(Color.BLACK);
        dailyContent.setTypeface(MainActivity.getTypeface());

        dailyNote.setTextColor(Color.BLACK);
        dailyNote.setTypeface(MainActivity.getTypeface());

        if (date == null) { // 初始化年月日
            Calendar calendar = Calendar.getInstance();
            _year = calendar.get(Calendar.YEAR);
            _month = calendar.get(Calendar.MONTH) + 1;
            _day = calendar.get(Calendar.DAY_OF_MONTH);

            initThread.start();
            try {
                initThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            date = daily.getDate();
        }

        refreshDaily();

        dailySpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlaySpeakOnline(getActivity(), getContext(), dailySpeak, daily.getSpeakUrl());
            }
        });

        dailyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getContext(), onDateSetListener, _year, _month - 1, _day);
                DatePicker dp = dpd.getDatePicker();
                dp.setMaxDate((new Date()).getTime());
                dpd.show();
            }
        });

        return view;
    }

    public static WordDetailData getTemporaryData() {
        return temporaryData;
    }

    private void refreshDaily() {
        dailyImage.setImageBitmap(daily.getBitPicture());
        dailyDate.setText(daily.getDate());
        dailyContent.setText(daily.getContent());
        dailyNote.setText(daily.getNote());
    }
}
