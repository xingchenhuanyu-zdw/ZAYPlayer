package com.zay.common;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
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

    void addOnMPlayingTimeChangeListener(@NonNull OnMPlayingTimeChangeListener listener);

    void removeOnMPlayingTimeChangeListener(@NonNull OnMPlayingTimeChangeListener listener);

    void removeAllOnMPlayingTimeChangeListener();

    void addOnMBufferedUpdateListener(@NonNull OnMBufferedUpdateListener listener);

    void removeOnMBufferedUpdateListener(@NonNull OnMBufferedUpdateListener listener);

    void removeAllOnMBufferedUpdateListener();

    void addOnMBufferingListener(@NonNull OnMBufferingListener listener);

    void removeOnMBufferingListener(@NonNull OnMBufferingListener listener);

    void removeAllOnMBufferingListener();

    void addOnMPlayerStatusChangeListener(@NonNull OnMPlayerStatusChangeListener listener);

    void removeOnMPlayerStatusChangeListener(@NonNull OnMPlayerStatusChangeListener listener);

    void removeAllOnMPlayerStatusChangeListener();

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
