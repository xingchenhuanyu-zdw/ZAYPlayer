package com.zay.common;

import android.widget.FrameLayout;

import com.zay.common.listeners.OnMBufferedUpdateListener;
import com.zay.common.listeners.OnMBufferingListener;
import com.zay.common.listeners.OnMPlayerStatusChangeListener;
import com.zay.common.listeners.OnMPlayingTimeChangeListener;

import java.util.List;

/**
 * Created by Zdw on 2021/01/19 16:02
 */

public interface MPlayer {
    void setOnMPlayingTimeChangeListener(OnMPlayingTimeChangeListener listener);

    void setOnMBufferedUpdateListener(OnMBufferedUpdateListener listener);

    void setOnMBufferingListener(OnMBufferingListener listener);

    void setOnMPlayerStatusChangeListener(OnMPlayerStatusChangeListener listener);

    void bindPlayerView(FrameLayout playerView);

    void setAutoPlay(boolean autoPlay);

    void setupOnlineVideoWithId(String videoId, String token);

    boolean isPlaying();

    void play();

    void pause();

    void stop();

    void release();

    void seek(int seekTime);

    int getCurrentTime();

    int getDuration();

    int getDefinition();

    List<Integer> getSupportedDefinitionList();

    boolean changeDefinition(int definition);

    void setPreferredDefinition(int definition);
}
