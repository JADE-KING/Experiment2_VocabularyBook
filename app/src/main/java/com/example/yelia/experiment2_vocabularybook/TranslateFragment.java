package com.example.yelia.experiment2_vocabularybook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.youdao.sdk.app.Language;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydonlinetranslate.TranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;

/**
 * Created by yelia on 2017/11/7.
 */

public class TranslateFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translate_page, container, false);

        final EditText translateEdit1 = (EditText)view.findViewById(R.id.translateEdit1);
        final EditText translateEdit2 = (EditText)view.findViewById(R.id.translateEdit2);
        TextView translateClearBtn = (TextView)view.findViewById(R.id.translateClearBtn);
        TextView translateBtn = (TextView)view.findViewById(R.id.translateBtn);

        translateClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateEdit1.setText("");
                translateEdit2.setText("");
            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = translateEdit1.getText().toString();

                Language from = Language.AUTO;
                Language to = Language.AUTO;

                TranslateParameters tps = new TranslateParameters.Builder()
                        .source("vb").from(from).to(to).timeout(3000).build();

                Translator translator = Translator.getInstance(tps);

                translator.lookup(query, new TranslateListener() {
                    @Override
                    public void onResult(Translate translate, String s) {
                        WordDetailData translateData = new WordDetailData(translate);
                        translateEdit2.setText(translateData.getTranslation());
                    }

                    @Override
                    public void onError(TranslateErrorCode translateErrorCode) {

                    }
                });
            }
        });

        return view;
    }
}
