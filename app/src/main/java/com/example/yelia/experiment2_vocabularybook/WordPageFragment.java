package com.example.yelia.experiment2_vocabularybook;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * Created by yelia on 2017/10/22.
 */

public class WordPageFragment extends Fragment {
    private Context context;
    private static ListView wordListView;

    private static ImageView sort;
    private static ImageView order;

    private static int sortFlag = 0; // 0为时间, 1为字母
    private static int orderFlag = 1; // 0为顺序, 1为倒序

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.word_page, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();

        wordListView = (ListView)getActivity().findViewById(R.id.wordListView);
        sort = (ImageView)getActivity().findViewById(R.id.sort);
        order = (ImageView)getActivity().findViewById(R.id.order);

        refreshListView(context, sortFlag, orderFlag);

        wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment wordDetailFragment = new WordDetailFragment();

                // 传输选中词汇到WordDetailFragment
                Bundle bundle = new Bundle();
                bundle.putInt(WordDBConstruct.COLUMN_NAME_ID, MainActivity.getID(position));
                wordDetailFragment.setArguments(bundle);

                // 设置WordDetailFragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.wordDetailLinear, wordDetailFragment);

                // 判断手机屏幕方向
                Configuration configuration = getActivity().getResources().getConfiguration();
                int orientation = configuration.orientation;
                if (orientation == configuration.ORIENTATION_PORTRAIT) {
                    // 竖屏WordPage加入退回栈顶
                    fragmentTransaction.addToBackStack(null);
                }

                fragmentTransaction.commit();
            }
        });

        // 模糊搜索
        SearchView wordSearchOffline = (SearchView)getActivity().findViewById(R.id.wordSearchOffline);
        wordSearchOffline.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    refreshListView(context, sortFlag, orderFlag);
                } else {
                    MainActivity.setWordList(MainActivity.getDb().getWordListFuzzily(newText));
                    refreshListView(context, wordListView);
                }
                return true;
            }
        });

        // 排序
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortFlag == 0) { // 点击时间图标切换为字母
                    sortFlag = 1;
                    sort.setImageResource(R.drawable.sort_alphabet);
                }
                else { // 点击字母图标切换为时间
                    sortFlag = 0;
                    sort.setImageResource(R.drawable.sort_time);
                }
                refreshListView(context, sortFlag, orderFlag);
                refreshListToast(context, sortFlag, orderFlag);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderFlag == 0) { // 点击升序图标切换为降序
                    orderFlag = 1;
                }
                else { // 点击降序图标切换为升序
                    orderFlag = 0;
                }
                refreshListView(context, sortFlag, orderFlag);
                refreshListToast(context, sortFlag, orderFlag);
            }
        });
    }

    public static int getSortFlag() {
        return sortFlag;
    }

    public static int getOrderFlag() {
        return orderFlag;
    }

    private static void refreshListView(Context context, ListView wordListView) {
        SimpleAdapter wordListAdapter = new SimpleAdapter(
                context, MainActivity.getWordList(), R.layout.word_list_item,
                new String[] { WordDBConstruct.COLUMN_NAME_WORD, WordDBConstruct.COLUMN_NAME_TRANSLATION },
                new int[] { R.id.wordText, R.id.meanText });
        wordListView.setAdapter(wordListAdapter);
    }

    public static void refreshListView(Context context, int sortFlag, int orderFlag) {
        if (sortFlag == 0) {
            sort.setImageResource(R.drawable.sort_time);
            if (orderFlag == 0) { // 时间顺序
                order.setImageResource(R.drawable.asc);
                MainActivity.setWordList(MainActivity.getDb().getWordListByTimeOrder());
            }
            else { // 时间倒序
                order.setImageResource(R.drawable.desc);
                MainActivity.setWordList(MainActivity.getDb().getWordListReserveTimeOrder());
            }
        }
        else { // 字母顺序
            sort.setImageResource(R.drawable.sort_alphabet);
            if (orderFlag == 0) {
                order.setImageResource(R.drawable.asc);
                MainActivity.setWordList(MainActivity.getDb().getWordListByAlphabetOrder());
            }
            else { // 字母倒序
                order.setImageResource(R.drawable.desc);
                MainActivity.setWordList(MainActivity.getDb().getWordListReserveAlphabetOrder());
            }
        }

        refreshListView(context, wordListView);
    }

    // 排序切换提示
    private static void refreshListToast(Context context, int sortFlag, int orderFlag) {
        if (sortFlag == 0) {
            if (orderFlag == 0) { // 时间顺序
                Toast.makeText(context, "时间顺序", Toast.LENGTH_SHORT).show();
            }
            else { // 时间倒序
                Toast.makeText(context, "时间倒序", Toast.LENGTH_SHORT).show();
            }
        }
        else { // 字母顺序
            if (orderFlag == 0) {
                Toast.makeText(context, "字母顺序", Toast.LENGTH_SHORT).show();
            }
            else { // 字母倒序
                Toast.makeText(context, "字母倒序", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
