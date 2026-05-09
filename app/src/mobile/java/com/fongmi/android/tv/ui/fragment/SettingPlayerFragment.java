package com.fongmi.android.tv.ui.fragment;

import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.Setting;
import com.fongmi.android.tv.databinding.FragmentSettingPlayerBinding;
import com.fongmi.android.tv.impl.BufferCallback;
import com.fongmi.android.tv.impl.SpeedCallback;
import com.fongmi.android.tv.impl.UaCallback;
import com.fongmi.android.tv.ui.base.BaseFragment;
import com.fongmi.android.tv.ui.dialog.BufferDialog;
import com.fongmi.android.tv.ui.dialog.SpeedDialog;
import com.fongmi.android.tv.ui.dialog.UaDialog;
import com.fongmi.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;

public class SettingPlayerFragment extends BaseFragment implements UaCallback, BufferCallback, SpeedCallback {

    private FragmentSettingPlayerBinding mBinding;
    private DecimalFormat format;
    private String[] background;
    private String[] caption;
    private String[] render;
    private String[] scale;
    private String[] rtspTransport;
    private String[] decode;
    private String[] player;

    public static SettingPlayerFragment newInstance() {
        return new SettingPlayerFragment();
    }

    private String getSwitch(boolean value) {
        return getString(value ? R.string.setting_on : R.string.setting_off);
    }

    @Override
    protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return mBinding = FragmentSettingPlayerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        format = new DecimalFormat("0.#");
        mBinding.uaText.setText(Setting.getUa());
        mBinding.aacText.setText(getSwitch(Setting.isPreferAAC()));
        mBinding.tunnelText.setText(getSwitch(Setting.isTunnel()));
        mBinding.adblockText.setText(getSwitch(Setting.isAdblock()));
        mBinding.speedText.setText(format.format(Setting.getSpeed()));
        mBinding.bufferText.setText(String.valueOf(Setting.getBuffer()));
        mBinding.vodPlayerText.setText((player = ResUtil.getStringArray(R.array.select_player))[Setting.getVodPlayer()]);
        mBinding.livePlayerText.setText(player[Setting.getLivePlayer()]);
        mBinding.vodDecodeText.setText((decode = ResUtil.getStringArray(R.array.select_decode))[Setting.getVodDecode()]);
        mBinding.liveDecodeText.setText(decode[Setting.getLiveDecode()]);
        mBinding.epgUrlText.setText(Setting.getEpgUrl());
        mBinding.danmakuLoadText.setText(getSwitch(Setting.isDanmakuLoad()));
        mBinding.caption.setVisibility(Setting.hasCaption() ? View.VISIBLE : View.GONE);
        mBinding.scaleText.setText((scale = ResUtil.getStringArray(R.array.select_scale))[Setting.getScale()]);
        mBinding.renderText.setText((render = ResUtil.getStringArray(R.array.select_render))[Setting.getRender()]);
        mBinding.rtspTransportText.setText((rtspTransport = ResUtil.getStringArray(R.array.select_rtsp_transport))[Setting.getRtspTransport()]);
        mBinding.captionText.setText((caption = ResUtil.getStringArray(R.array.select_caption))[Setting.isCaption() ? 1 : 0]);
        mBinding.backgroundText.setText((background = ResUtil.getStringArray(R.array.select_background))[Setting.getBackground()]);
    }

    @Override
    protected void initEvent() {
        mBinding.ua.setOnClickListener(this::onUa);
        mBinding.aac.setOnClickListener(this::setAAC);
        mBinding.scale.setOnClickListener(this::onScale);
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
        mBinding.rtspTransport.setOnClickListener(this::onRtspTransport);
        mBinding.epgUrl.setOnClickListener(this::setEpgUrl);
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

    private void onScale(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.player_scale).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(scale, Setting.getScale(), (dialog, which) -> {
            mBinding.scaleText.setText(scale[which]);
            Setting.putScale(which);
            dialog.dismiss();
        }).show();
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

    private boolean onCaption(View view) {
        if (Setting.isCaption()) startActivity(new Intent(Settings.ACTION_CAPTIONING_SETTINGS));
        return Setting.isCaption();
    }

    private void setAdblock(View view) {
        Setting.putAdblock(!Setting.isAdblock());
        mBinding.adblockText.setText(getSwitch(Setting.isAdblock()));
    }

    private void onBackground(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.player_background).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(background, Setting.getBackground(), (dialog, which) -> {
            mBinding.backgroundText.setText(background[which]);
            Setting.putBackground(which);
            dialog.dismiss();
        }).show();
    }

    private void setVodPlayer(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.player_vod_player).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(player, Setting.getVodPlayer(), (dialog, which) -> {
            mBinding.vodPlayerText.setText(player[which]);
            Setting.putVodPlayer(which);
            dialog.dismiss();
        }).show();
    }

    private void setLivePlayer(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.player_live_player).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(player, Setting.getLivePlayer(), (dialog, which) -> {
            mBinding.livePlayerText.setText(player[which]);
            Setting.putLivePlayer(which);
            dialog.dismiss();
        }).show();
    }

    private void setVodDecode(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.player_vod_decode).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(decode, Setting.getVodDecode(), (dialog, which) -> {
            mBinding.vodDecodeText.setText(decode[which]);
            Setting.putVodDecode(which);
            dialog.dismiss();
        }).show();
    }

    private void setLiveDecode(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.player_live_decode).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(decode, Setting.getLiveDecode(), (dialog, which) -> {
            mBinding.liveDecodeText.setText(decode[which]);
            Setting.putLiveDecode(which);
            dialog.dismiss();
        }).show();
    }

    private void setDanmakuLoad(View view) {
        Setting.putDanmakuLoad(!Setting.isDanmakuLoad());
        mBinding.danmakuLoadText.setText(getSwitch(Setting.isDanmakuLoad()));
    }

    private void onRtspTransport(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.player_rtsp_transport).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(rtspTransport, Setting.getRtspTransport(), (dialog, which) -> {
            mBinding.rtspTransportText.setText(rtspTransport[which]);
            Setting.putRtspTransport(which);
            dialog.dismiss();
        }).show();
    }

    private void setEpgUrl(View view) {
        EditText input = new EditText(requireActivity());
        input.setText(Setting.getEpgUrl());
        input.setSingleLine();
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.player_epg_url).setView(input).setPositiveButton(R.string.dialog_positive, (d, w) -> {
            String url = input.getText().toString().trim();
            Setting.putEpgUrl(url);
            mBinding.epgUrlText.setText(url);
        }).setNegativeButton(R.string.dialog_negative, null).show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) initView();
    }
}
