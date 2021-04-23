package com.zay.common;

import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.zay.common.listeners.OnMBufferedUpdateListener;
import com.zay.common.listeners.OnMBufferingListener;
import com.zay.common.listeners.OnMPlayerStatusChangeListener;
import com.zay.common.listeners.OnMPlayingTimeChangeListener;

import java.util.Map;

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

    void start();

    void pause();

    void stop();

    void release();

    void seekTo(int seekTime);

    int getCurrentPosition();

    int getDuration();

    int getDefinitionCode();

    @Nullable
    String getDefinitionName();

    @Nullable
    String getDefinitionName(int definition);

    @Nullable
    Map<String, Integer> getSupportedDefinitions();

    boolean changeDefinition(int definition);

    void setPreferredDefinition(int definition);
}
