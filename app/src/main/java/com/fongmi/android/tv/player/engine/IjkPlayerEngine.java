package com.fongmi.android.tv.player.engine;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.Tracks;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.bean.Track;
import com.fongmi.android.tv.utils.ResUtil;

import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkPlayerEngine implements PlayerEngine {

    private IjkMediaPlayer player;
    private PlaySpec spec;
    private Surface surface;
    private Player.Listener listener;
    private boolean prepared;

    public IjkPlayerEngine() {
        createPlayer();
    }

    private void createPlayer() {
        player = new IjkMediaPlayer();
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 64);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 1024 * 15);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 25);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        player.setOnPreparedListener(mp -> {
            prepared = true;
            if (surface != null) player.setSurface(surface);
        });
        player.setOnErrorListener((mp, what, extra) -> true);
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
        if (prepared && surface != null) player.setSurface(surface);
    }

    @Override
    public int getType() {
        return IJK;
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
        prepared = false;
    }

    @Override
    public Player rebuild(Player.Listener listener) {
        this.listener = listener;
        if (player != null) player.release();
        createPlayer();
        return null;
    }

    @Override
    public int getDecode() {
        return HARD;
    }

    @Override
    public void setDecode(int decode) {
    }

    @Override
    public boolean isHard() {
        return true;
    }

    @Override
    public String getDecodeText() {
        return ResUtil.getStringArray(R.array.select_player)[IJK];
    }

    @Override
    public void start(PlaySpec spec) {
        this.spec = spec;
        this.prepared = false;
        try {
            if (spec.getHeaders() != null && !spec.getHeaders().isEmpty()) {
                for (Map.Entry<String, String> entry : spec.getHeaders().entrySet()) {
                    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "headers", entry.getKey() + ": " + entry.getValue());
                }
            }
            player.setDataSource(App.get(), Uri.parse(spec.getUrl()));
            player.prepareAsync();
        } catch (Exception e) {
            if (listener != null) listener.onPlayerError(new PlaybackException(e.getMessage(), e, PlaybackException.ERROR_CODE_REMOTE_ERROR));
        }
    }

    @Override
    public void setMetadata(MediaMetadata data) {
    }

    @Override
    public boolean isLive() {
        return spec != null && spec.getUrl() != null;
    }

    @Override
    public boolean isVod() {
        return true;
    }

    @Override
    public void setTrack(List<Track> tracks) {
    }

    @Override
    public void resetTrack() {
    }

    @Override
    public boolean haveTrack(int type) {
        return false;
    }

    @Override
    public Tracks getCurrentTracks() {
        return null;
    }

    @Override
    public String getErrorMessage(PlaybackException e) {
        return e != null ? e.getMessage() : "";
    }

    @Override
    public ErrorAction handleError(PlaybackException e) {
        return ErrorAction.FATAL;
    }

    @Override
    public void play() {
        if (player != null) player.start();
    }

    @Override
    public void pause() {
        if (player != null) player.pause();
    }

    @Override
    public void stopPlayback() {
        if (player != null) player.stop();
    }

    @Override
    public void seekTo(long time) {
        if (player != null) player.seekTo(time);
    }

    @Override
    public long getCurrentPosition() {
        return player != null ? player.getCurrentPosition() : 0;
    }

    @Override
    public long getDuration() {
        return player != null ? player.getDuration() : 0;
    }

    @Override
    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    @Override
    public void setPlaybackSpeed(float speed) {
        if (player != null) player.setSpeed(speed);
    }

    @Override
    public float getPlaybackSpeed() {
        return player != null ? player.getSpeed(1.0f) : 1.0f;
    }
}
