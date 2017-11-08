package com.example.yelia.experiment2_vocabularybook;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by yelia on 2017/11/3.
 */

public class PlaySpeakOnline {
    public PlaySpeakOnline(FragmentActivity activity,Context context, final ImageView speak, String url) {
        try {
            speak.setImageResource(R.drawable.systemprompt_fill);
            Uri speakUri = Uri.parse(url);
            final MediaPlayer speakPlayer = MediaPlayer.create(activity, speakUri);
            speakPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    speakPlayer.release();
                    speak.setImageResource(R.drawable.systemprompt);
                }
            });
            speakPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            speak.setImageResource(R.drawable.systemprompt);
            Toast.makeText(context, "您没有链接网络哦~", Toast.LENGTH_LONG).show();
        }
    }
}
