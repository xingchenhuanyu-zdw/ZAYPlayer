package com.zay.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zay.common.listeners.ZAYOnBufferedUpdateListener;
import com.zay.common.listeners.ZAYOnBufferingListener;
import com.zay.common.listeners.ZAYOnPlayerStatusChangeListener;
import com.zay.common.listeners.ZAYOnPlayingTimeChangeListener;
import com.zay.common.widget.ZAYPlayerView;

import java.util.Map;

/**
 * Created by Zdw on 2021/01/19 16:02
 */

public interface ZAYPlayer {

    void addOnPlayingTimeChangeListener(@NonNull ZAYOnPlayingTimeChangeListener listener);

    void removeOnPlayingTimeChangeListener(@NonNull ZAYOnPlayingTimeChangeListener listener);

    void removeAllOnPlayingTimeChangeListener();

    void addOnBufferedUpdateListener(@NonNull ZAYOnBufferedUpdateListener listener);

    void removeOnBufferedUpdateListener(@NonNull ZAYOnBufferedUpdateListener listener);

    void removeAllOnBufferedUpdateListener();

    void addOnBufferingListener(@NonNull ZAYOnBufferingListener listener);

    void removeOnBufferingListener(@NonNull ZAYOnBufferingListener listener);

    void removeAllOnBufferingListener();

    void addOnPlayerStatusChangeListener(@NonNull ZAYOnPlayerStatusChangeListener listener);

    void removeOnPlayerStatusChangeListener(@NonNull ZAYOnPlayerStatusChangeListener listener);

    void removeAllOnPlayerStatusChangeListener();

    void bindPlayerView(ZAYPlayerView playerView);

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
