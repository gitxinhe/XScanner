package com.kiloway.commonscanner.base;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.kiloway.commonscanner.R;

import java.util.HashMap;
import java.util.Map;

public enum BeepUtil {
    INS;
    SoundPool soundPool;
    private Context context;
    //初始化声音池
    public void initSound(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频的数量
            builder.setMaxStreams(3);
            //AudioAttributes是一个封装音频各种属性的类
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
            soundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 0);
        }
        initBeep();
    }

    boolean initComplete = false;
    Map<Integer, Integer> soundMap = new HashMap<>();

    //初始化资源文件
    private void initBeep() {
        //第一个参数Context,第二个参数资源Id，第三个参数优先级
        soundMap.put(1, soundPool.load(context, R.raw.tag_inventoried, 1));
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                initComplete = true;
                Log.e("tag","初始成功");
            }
        });
    }

    //播放
    public void speak() {
        Log.e("tag",initComplete+"");
        if (initComplete) {
            int soundID = soundMap.get(1);
            soundPool.play(soundID, 0.3f, 0.3f, 0, 0, 1);
        }
    }

    //销毁
    public void destroy() {
        if (null != soundPool) {
            soundPool.release();
        }
    }
}
