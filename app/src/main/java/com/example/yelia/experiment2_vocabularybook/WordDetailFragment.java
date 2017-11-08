package com.example.yelia.experiment2_vocabularybook;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by yelia on 2017/10/22.
 */

public class WordDetailFragment extends Fragment {
    private WordDetailData data;
    private int id;
    private int position;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.word_detail, container, false);

        // 获取从WordPageFragment传来的参数
        Bundle bundle = getArguments();
        id = bundle.getInt(WordDBConstruct.COLUMN_NAME_ID);
        position = MainActivity.getPosition(id);
        if (position >= 0) { // 在列表中, 从详情列表获取
            data = MainActivity.getWordDetailList().get(position);
        } else { // 不在列表中, 从HomePage获取临时数据
            data = HomePageFragment.getTemporaryData();
        }

        ScrollView wordDetailScroll = (ScrollView)view.findViewById(R.id.wordDetailScroll);
        wordDetailScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FrameLayout替换之后还能点击列表, 设置监听器覆盖
            }
        });

        TextView word = (TextView)view.findViewById(R.id.word);
        word.setText(data.getWord());

        TextView phonetic = (TextView)view.findViewById(R.id.phonetic);
        phonetic.setText("/" + data.getPhonetic() + "/");

        TextView ukPhonetic = (TextView)view.findViewById(R.id.ukPhonetic);
        ukPhonetic.setText("英 /" + data.getUkPhonetic() + "/");

        TextView usPhonetic = (TextView)view.findViewById(R.id.usPhonetic);
        usPhonetic.setText("美 /" + data.getUsPhonetic() + "/");

        TextView explains = (TextView)view.findViewById(R.id.explains);
        explains.setText("释义：\n" + data.getExplainString());

        TextView webExplains = (TextView)view.findViewById(R.id.webExplains);
        webExplains.setText("网络释义：\n" + data.getWebExplainString());

        final TextView userNote = (TextView)view.findViewById(R.id.userNote);
        userNote.setText(data.getUserNote());

        TextView deeplink = (TextView)view.findViewById(R.id.deeplink);
        deeplink.setText("了解更多：\n" + data.getDeepLink());

        // 添加到单词本 OR 从单词本删除
        final ImageView addToVB = (ImageView)view.findViewById(R.id.addToVB);
        if (position >= 0) {
            addToVB.setImageResource(R.drawable.offline);
        }
        addToVB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != -1) { // 单词本中存在, 点击删除, 图标改为添加
                    // 删除单词并更新列表
                    MainActivity.getDb().delWord(id);
                    MainActivity.setWordDetailList(MainActivity.getDb().getWordDetailList());
                    WordPageFragment.refreshListView(getActivity(), WordPageFragment.getSortFlag(), WordPageFragment.getOrderFlag());

                    id = -1;
                    position = -1;
                    addToVB.setImageResource(R.drawable.addition);
                }
                else { // 单词本中不存在, 点击添加, 图标改为删除
                    // 添加单词并更新列表
                    MainActivity.getDb().addWord(data);
                    MainActivity.setWordDetailList(MainActivity.getDb().getWordDetailList());
                    WordPageFragment.refreshListView(getActivity(), WordPageFragment.getSortFlag(), WordPageFragment.getOrderFlag());

                    id = MainActivity.wordListIdOf(data.getWord());
                    position = MainActivity.getPosition(id);
                    addToVB.setImageResource(R.drawable.offline);
                }
            }
        });

        // 播放读音
        final ImageView speak = (ImageView)view.findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlaySpeakOnline(getActivity(), getContext(), speak, data.getSpeakUrl());
            }
        });

        final ImageView ukSpeak = (ImageView)view.findViewById(R.id.ukSpeak);
        ukSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlaySpeakOnline(getActivity(), getContext(), ukSpeak, data.getUkSpeakUrl());
            }
        });

        final ImageView usSpeak = (ImageView)view.findViewById(R.id.usSpeak);
        usSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlaySpeakOnline(getActivity(), getContext(), usSpeak, data.getUsSpeakUrl());
            }
        });

        // 编辑用户笔记
        ImageView userNoteEditor = (ImageView)view.findViewById(R.id.userNoteEditor);
        userNoteEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View editUserNoteView = inflater.inflate(R.layout.user_note_edit_dialog, null);
                final EditText editUserNote = (EditText)editUserNoteView.findViewById(R.id.editUserNote);
                editUserNote.setText(data.getUserNote());
                editUserNote.setHeight(view.getWidth() / 3);
                builder.setView(editUserNoteView);
                builder.setTitle("编辑用户笔记");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.setUserNote(editUserNote.getText().toString());
                        MainActivity.getDb().updateUserNote(data.getWord(), data.getUserNote());
                        userNote.setText(data.getUserNote());
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        return view;
    }
}
