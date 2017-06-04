package com.iilu.fendou.modules.message.listener;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.modules.message.adapter.MessageAdapter;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.iilu.fendou.utils.ToastUtil;

import java.io.File;
import java.io.IOException;

public class VoiceImageViewClickListener implements View.OnClickListener{

    private Context mContext;
    private ImageView mImgPlayVoice;
    private EMVoiceMessageBody mVoiceBody;
    private MessageAdapter mMessageAdapter;
    private EMMessage mEmMessage;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private AnimationDrawable mVoiceAnimation;

    private boolean isPlaying = false;

    public VoiceImageViewClickListener(Context context, ImageView imgPlayVoice, EMMessage emMessage, MessageAdapter messageAdapter) {
        this.mContext = context;
        this.mImgPlayVoice = imgPlayVoice;
        this.mEmMessage = emMessage;
        this.mMessageAdapter = messageAdapter;
        this.mVoiceBody = (EMVoiceMessageBody) mEmMessage.getBody();
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onClick(View v) {
        if (isPlaying) {
            stopVoice();
            return;
        }

        playVoice(mVoiceBody.getLocalUrl());
    }

    private void playVoice(String filePath) {
        if (!new File(filePath).exists()) {
            ToastUtil.showCenter(mContext, "播放失败！");
        } else {
            mMediaPlayer = new MediaPlayer();

            boolean isSpeaker = SPrefUtil_2.get(mContext, PrefsConfig.MSG_CHAT, "is_speaker", true);
            if (isSpeaker) {
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mAudioManager.setSpeakerphoneOn(true);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            } else {
                mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                mAudioManager.setSpeakerphoneOn(false);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }

            try {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopVoice();
                    }
                });
                isPlaying = true;
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showAnimation();
        }
    }

    private void stopVoice() {
        mVoiceAnimation.stop();
        if (mEmMessage.direct() == EMMessage.Direct.RECEIVE) {
            mImgPlayVoice.setImageResource(R.mipmap.voice_from_playing);
        } else if (mEmMessage.direct() == EMMessage.Direct.SEND) {
            mImgPlayVoice.setImageResource(R.mipmap.voice_to_playing);
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        isPlaying = false;
    }

    private void showAnimation() {
        if (mEmMessage.direct() == EMMessage.Direct.RECEIVE) {
            mImgPlayVoice.setImageResource(R.drawable.anim_voice_playing_from);
        } else {
            mImgPlayVoice.setImageResource(R.drawable.anim_voice_playing_to);
        }
        mVoiceAnimation = (AnimationDrawable) mImgPlayVoice.getDrawable();
        mVoiceAnimation.start();
    }
}
