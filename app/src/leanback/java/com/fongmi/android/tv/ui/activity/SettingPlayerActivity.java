package com.fongmi.android.tv.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;

import androidx.viewbinding.ViewBinding;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.Setting;
import com.fongmi.android.tv.databinding.ActivitySettingPlayerBinding;
import com.fongmi.android.tv.impl.BufferCallback;
import com.fongmi.android.tv.impl.SpeedCallback;
import com.fongmi.android.tv.impl.UaCallback;
import com.fongmi.android.tv.ui.base.BaseActivity;
import com.fongmi.android.tv.ui.dialog.BufferDialog;
import com.fongmi.android.tv.ui.dialog.SpeedDialog;
import com.fongmi.android.tv.ui.dialog.UaDialog;
import com.fongmi.android.tv.utils.ResUtil;

import java.text.DecimalFormat;

public class SettingPlayerActivity extends BaseActivity implements UaCallback, BufferCallback, SpeedCallback {

    private ActivitySettingPlayerBinding mBinding;
    private DecimalFormat format;
    private String[] caption;
    private String[] render;
    private String[] scale;
    private String[] rtspTransport;
    private String[] decode;
    private String[] player;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingPlayerActivity.class));
    }

    private String getSwitch(boolean value) {
        return getString(value ? R.string.setting_on : R.string.setting_off);
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivitySettingPlayerBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setVisible();
        format = new DecimalFormat("0.#");
        mBinding.render.requestFocus();
        mBinding.uaText.setText(Setting.getUa());
        mBinding.aacText.setText(getSwitch(Setting.isPreferAAC()));
        mBinding.tunnelText.setText(getSwitch(Setting.isTunnel()));
        mBinding.adblockText.setText(getSwitch(Setting.isAdblock()));
        mBinding.speedText.setText(format.format(Setting.getSpeed()));
        mBinding.bufferText.setText(String.valueOf(Setting.getBuffer()));
        mBinding.backgroundText.setText(getSwitch(Setting.isBackgroundOn()));
        mBinding.vodPlayerText.setText((player = ResUtil.getStringArray(R.array.select_player))[Setting.getVodPlayer()]);
        mBinding.livePlayerText.setText(player[Setting.getLivePlayer()]);
        mBinding.vodDecodeText.setText((decode = ResUtil.getStringArray(R.array.select_decode))[Setting.getVodDecode()]);
        mBinding.liveDecodeText.setText(decode[Setting.getLiveDecode()]);
        mBinding.epgUrlText.setText(Setting.getEpgUrl());
        mBinding.danmakuLoadText.setText(getSwitch(Setting.isDanmakuLoad()));
        mBinding.scaleText.setText((scale = ResUtil.getStringArray(R.array.select_scale))[Setting.getScale()]);
        mBinding.renderText.setText((render = ResUtil.getStringArray(R.array.select_render))[Setting.getRender()]);
        mBinding.rtspTransportText.setText((rtspTransport = ResUtil.getStringArray(R.array.select_rtsp_transport))[Setting.getRtspTransport()]);
        mBinding.captionText.setText((caption = ResUtil.getStringArray(R.array.select_caption))[Setting.isCaption() ? 1 : 0]);
    }

    @Override
    protected void initEvent() {
        mBinding.ua.setOnClickListener(this::onUa);
        mBinding.aac.setOnClickListener(this::setAAC);
        mBinding.scale.setOnClickListener(this::setScale);
        mBinding.speed.setOnClickListener(this::onSpeed);
        mBinding.buffer.setOnClickListener(this::onBuffer);
        mBinding.render.setOnClickListener(this::setRender);
        mBinding.tunnel.setOnClickListener(this::setTunnel);
        mBinding.caption.setOnClickListener(this::setCaption);
        mBinding.adblock.setOnClickListener(this::setAdblock);
        mBinding.caption.setOnLongClickListener(this::onCaption);
        mBinding.background.setOnClickListener(this::onBackground);
        mBinding.vodPlayer.setOnClickListener(this::setVodPlayer);
        mBinding.livePlayer.setOnClickListener(this::setLivePlayer);
        mBinding.vodDecode.setOnClickListener(this::setVodDecode);
        mBinding.liveDecode.setOnClickListener(this::setLiveDecode);
        mBinding.danmakuLoad.setOnClickListener(this::setDanmakuLoad);
        mBinding.rtspTransport.setOnClickListener(this::setRtspTransport);
        mBinding.epgUrl.setOnClickListener(this::setEpgUrl);
    }

    private void setVisible() {
        if (Setting.getBackground() == 2) Setting.putBackground(1);
        mBinding.caption.setVisibility(Setting.hasCaption() ? View.VISIBLE : View.GONE);
    }

    private void onUa(View view) {
        UaDialog.create(this).show();
    }

    @Override
    public void setUa(String ua) {
        mBinding.uaText.setText(ua);
        Setting.putUa(ua);
    }

    private void setAAC(View view) {
        Setting.putPreferAAC(!Setting.isPreferAAC());
        mBinding.aacText.setText(getSwitch(Setting.isPreferAAC()));
    }

    private void setScale(View view) {
        int index = (Setting.getScale() + 1) % scale.length;
        mBinding.scaleText.setText(scale[index]);
        Setting.putScale(index);
    }

    private void onSpeed(View view) {
        SpeedDialog.create(this).show();
    }

    @Override
    public void setSpeed(float speed) {
        mBinding.speedText.setText(format.format(speed));
        Setting.putSpeed(speed);
    }

    private void onBuffer(View view) {
        BufferDialog.create(this).show();
    }

    @Override
    public void setBuffer(int times) {
        mBinding.bufferText.setText(String.valueOf(times));
        Setting.putBuffer(times);
    }

    private void setRender(View view) {
        if (Setting.isTunnel() && Setting.getRender() == 0) setTunnel(view);
        int index = (Setting.getRender() + 1) % render.length;
        mBinding.renderText.setText(render[index]);
        Setting.putRender(index);
    }

    private void setTunnel(View view) {
        Setting.putTunnel(!Setting.isTunnel());
        mBinding.tunnelText.setText(getSwitch(Setting.isTunnel()));
        if (Setting.isTunnel() && Setting.getRender() == 1) setRender(view);
    }

    private void setCaption(View view) {
        Setting.putCaption(!Setting.isCaption());
        mBinding.captionText.setText(caption[Setting.isCaption() ? 1 : 0]);
    }

    private void setAdblock(View view) {
        Setting.putAdblock(!Setting.isAdblock());
        mBinding.adblockText.setText(getSwitch(Setting.isAdblock()));
    }

    private boolean onCaption(View view) {
        if (Setting.isCaption()) startActivity(new Intent(Settings.ACTION_CAPTIONING_SETTINGS));
        return Setting.isCaption();
    }

    private void setVodPlayer(View view) {
        int index = (Setting.getVodPlayer() + 1) % player.length;
        mBinding.vodPlayerText.setText(player[index]);
        Setting.putVodPlayer(index);
    }

    private void setLivePlayer(View view) {
        int index = (Setting.getLivePlayer() + 1) % player.length;
        mBinding.livePlayerText.setText(player[index]);
        Setting.putLivePlayer(index);
    }

    private void setVodDecode(View view) {
        int index = (Setting.getVodDecode() + 1) % decode.length;
        mBinding.vodDecodeText.setText(decode[index]);
        Setting.putVodDecode(index);
    }

    private void setLiveDecode(View view) {
        int index = (Setting.getLiveDecode() + 1) % decode.length;
        mBinding.liveDecodeText.setText(decode[index]);
        Setting.putLiveDecode(index);
    }

    private void setDanmakuLoad(View view) {
        Setting.putDanmakuLoad(!Setting.isDanmakuLoad());
        mBinding.danmakuLoadText.setText(getSwitch(Setting.isDanmakuLoad()));
    }

    private void setRtspTransport(View view) {
        int index = (Setting.getRtspTransport() + 1) % rtspTransport.length;
        mBinding.rtspTransportText.setText(rtspTransport[index]);
        Setting.putRtspTransport(index);
    }

    private void setEpgUrl(View view) {
        EditText input = new EditText(this);
        input.setText(Setting.getEpgUrl());
        input.setSingleLine();
        new AlertDialog.Builder(this).setTitle(R.string.player_epg_url).setView(input).setPositiveButton(R.string.dialog_positive, (d, w) -> {
            String url = input.getText().toString().trim();
            Setting.putEpgUrl(url);
            mBinding.epgUrlText.setText(url);
        }).setNegativeButton(R.string.dialog_negative, null).show();
    }

    private void onBackground(View view) {
        Setting.putBackground(Setting.isBackgroundOn() ? 0 : 1);
        mBinding.backgroundText.setText(getSwitch(Setting.isBackgroundOn()));
    }
}
