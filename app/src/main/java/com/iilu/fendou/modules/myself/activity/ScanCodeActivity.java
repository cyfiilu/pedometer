package com.iilu.fendou.modules.myself.activity;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.iilu.fendou.MainActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.scancode.camera.CameraManager;
import com.iilu.fendou.scancode.decoding.CaptureActivityHandler;
import com.iilu.fendou.scancode.views.ViewfinderView;
import com.iilu.fendou.utils.ToastUtil;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Vector;

public class ScanCodeActivity extends MainActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private Logger mlog = Logger.getLogger(ScanCodeActivity.class.getSimpleName());

    private final float BEEP_VOLUME = 1.00f;
    private final long VIBRATE_DURATION = 200L;

    private boolean hasSurfaceCreated;
    private boolean isPlayBeep;

    private String mCharacterSet;

    private Vector<BarcodeFormat> mDecodeFormats;

    private CaptureActivityHandler mCaptureActivityHandler;

    private ViewfinderView mViewFinder;
    private SurfaceView mSurfaceView;

    private TextView mScanResult;

    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);

        initViews();

        initBeepSound();
    }

    private void initViews() {
        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.scan_code_title);
        TextView title = (TextView) titleLayout.findViewById(R.id.tv_title);
        title.setText(getString(R.string.scan_code));
        ImageView imgBack = (ImageView) titleLayout.findViewById(R.id.iv_left);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);

        mViewFinder = (ViewfinderView) findViewById(R.id.viewfinder_view);
        mViewFinder.setShowInfoType(ViewfinderView.TEXTCONTENTTYPE_SCAN);
        CameraManager.frameSize = 2;
        CameraManager.init(this);

        hasSurfaceCreated = false;
    }

    private void initBeepSound() {
        isPlayBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            isPlayBeep = false;
        }

        if (isPlayBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            AssetFileDescriptor assetfile = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(assetfile.getFileDescriptor(), assetfile.getStartOffset(), assetfile.getLength());
                assetfile.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (hasSurfaceCreated) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mDecodeFormats = null;
        mCharacterSet = null;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        String permissionRefusedTip = "应用已被禁止权限：调用摄像头。请授予相应权限，再使用扫一扫！";
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            ToastUtil.showCenter(this, permissionRefusedTip);
            finish(); // 获取系统相机事件被拒绝后，直接finish()掉本activity
        } catch (RuntimeException e) {
            ToastUtil.showCenter(this, permissionRefusedTip);
            finish();
        }
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = new CaptureActivityHandler(this, mDecodeFormats, mCharacterSet);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurfaceCreated) {
            hasSurfaceCreated = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurfaceCreated = false;
    }

    public Handler getHandler() {
        return mCaptureActivityHandler;
    }

    public ViewfinderView getViewfinderView() {
        return mViewFinder;
    }

    public void drawViewfinder() {
        mViewFinder.drawViewfinder();
    }

    public void handleDecode(final String resultString) {
        mlog.info("resultString = " + resultString);
        playBeepSoundAndVibrate();

        mSurfaceView.setVisibility(View.GONE);
        mViewFinder.setVisibility(View.GONE);

        mScanResult = (TextView) findViewById(R.id.tv_scan_result);
        mScanResult.setVisibility(View.VISIBLE);
        mScanResult.setText("扫描结果：" + resultString);
    }

    private void playBeepSoundAndVibrate() {
        if (isPlayBeep && mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCaptureActivityHandler != null) {
            mCaptureActivityHandler.quitSynchronously();
            mCaptureActivityHandler = null;
        }
        CameraManager.get().closeDriver();
    }
}
