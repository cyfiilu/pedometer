package com.iilu.fendou.modules.myself.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.iilu.fendou.BuildConfig;
import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.UserInfoConfig;
import com.iilu.fendou.modules.myself.activity.PersonalInfoActivity;
import com.iilu.fendou.utils.BitmapUtil;
import com.iilu.fendou.utils.FileUtil;
import com.iilu.fendou.utils.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonalCoverFragment extends MainFragment implements View.OnClickListener {

    private final String mPath = Environment.getDataDirectory().getPath() + File.separator
            + "data" + File.separator + BuildConfig.APPLICATION_ID + File.separator
            + "pics" + File.separator;

    private int FROM_CAMERA = 220;
    private int FROM_GALLERY = 221;

    private static boolean isCover;

    private static String mUsernameStr = "cover_";
    private static String mHeadStr = "head_";

    private static Context mContext;
    // 不设置static，从相机 或 图库返回后，会为null
    private static ImageView mImgCover;
    private static TextView mUsername;
    private static ImageView mImgHead;
    private static TextView mPersonalInfo;
    private static ImageView mImgArrow;

    private static Uri mPhotoUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_cover, container, false);

        reset();

        initViews(view);

        return view;
    }

    private void reset() {
        isCover = false;
        mUsernameStr = "cover_";
        mHeadStr = "head_";

        mImgCover = null;
        mUsername = null;
        mImgHead = null;
        mPersonalInfo = null;
        mImgArrow = null;

        mPhotoUri = null;
    }

    private void initViews(View view) {
        mImgCover = (ImageView) view.findViewById(R.id.iv_personal_cover);
        mImgHead = (ImageView) view.findViewById(R.id.iv_head);
        mUsername = (TextView) view.findViewById(R.id.tv_username);
        mPersonalInfo = (TextView) view.findViewById(R.id.tv_personal_info);
        mImgArrow = (ImageView) view.findViewById(R.id.iv_arrow);

        mImgCover.setOnClickListener(this);
        mImgHead.setOnClickListener(this);
        mPersonalInfo.setOnClickListener(this);
        mImgArrow.setOnClickListener(this);

        String currLoginUsername = MainApplication.getCurrLoginUsername();
        mUsername.setText(currLoginUsername);

        mUsernameStr = mUsernameStr + currLoginUsername;
        if ("cover_".equals(mUsernameStr)) {
            mUsernameStr = mUsernameStr + UserInfoConfig.USER_ID;
        }

        mHeadStr = mHeadStr + currLoginUsername;
        if ("head_".equals(mHeadStr)) {
            mHeadStr = mHeadStr + UserInfoConfig.USER_ID;
        }

        String coverFileName = mUsernameStr + ".jpg";
        String headFileName = mHeadStr + ".jpg";

        String[] fileList = new File(mPath).list();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                String file = fileList[i];
                if ((coverFileName).equals(file)) {
                    Bitmap cover = BitmapUtil.toBitmapThumbnail(new File(mPath + coverFileName));
                    mImgCover.setImageBitmap(cover);
                }
                if (headFileName.equals(file)) {
                    Bitmap head = BitmapUtil.toBitmapThumbnail(new File(mPath + headFileName));
                    mImgHead.setImageBitmap(head);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_personal_cover:
                isCover = true;
                new BottomDialog(mContext, R.style.NewDialogSytle).show();
                break;
            case R.id.iv_head:
                isCover = false;
                new BottomDialog(mContext, R.style.NewDialogSytle).show();
                break;
            case R.id.tv_personal_info:
            case R.id.iv_arrow:
                startActivity(new Intent(mContext, PersonalInfoActivity.class));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_CAMERA && resultCode == Activity.RESULT_OK) { // 拍照
            Uri uri;
            if (data != null) {
                uri = data.getData();
            } else {
                uri = mPhotoUri;
            }
            String path = FileUtil.getPath(mContext, uri);
            File file = new File(path);
            Bitmap bitmap;
            if (isCover) {
                bitmap = BitmapUtil.toBitmapThumbnail(file); // 旋转的图片
                mImgCover.setImageBitmap(bitmap);
                BitmapUtil.saveImage(bitmap, mPath, mUsernameStr + ".jpg");
            } else {
                bitmap = BitmapUtil.compressBitmap(file);
                mImgHead.setImageBitmap(bitmap);
                BitmapUtil.saveImage(bitmap, mPath, mHeadStr + ".jpg");
            }
        } else if (requestCode == FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) { // 图库
            Uri uri = data.getData();
            String scheme = uri.getScheme();
            if ("file".equalsIgnoreCase(scheme)) {
                String path = uri.getPath();
                File file = new File(path);
                Bitmap bitmap;
                if (isCover) {
                    bitmap = BitmapUtil.toBitmapThumbnail(file);
                    mImgCover.setImageBitmap(bitmap);
                    BitmapUtil.saveImage(bitmap, mPath, mUsernameStr + ".jpg");
                } else {
                    bitmap = BitmapUtil.compressBitmap(file);
                    mImgHead.setImageBitmap(bitmap);
                    BitmapUtil.saveImage(bitmap, mPath, mHeadStr + ".jpg");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCover = false;
        mUsernameStr = "cover_";
        mHeadStr = "head_";
    }

    private class BottomDialog extends Dialog implements View.OnClickListener {

        public BottomDialog(Context context, int theme) {
            super(context, theme);
            View view = View.inflate(context, R.layout.dialog_bottom_cover, null);
            TextView tvCamera = (TextView) view.findViewById(R.id.tv_camera);
            TextView tvGallery = (TextView) view.findViewById(R.id.tv_gallery);
            TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);

            tvCamera.setOnClickListener(this);
            tvGallery.setOnClickListener(this);
            tvCancel.setOnClickListener(this);

            setContentView(view);

            Window window = this.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.BottomAnim);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_camera:
                    dismiss();
                    String mountedPath = Environment.getExternalStorageState();
                    final File cameraPath = new File(mountedPath + "/DCIM/Camera"); // 拍摄照片存储的文件夹路径
                    if (Environment.MEDIA_MOUNTED.equals(mountedPath)) { // 判断是否有SD卡
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, getPhotoFileName());
                        mPhotoUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        ((Activity) mContext).startActivityForResult(intent, FROM_CAMERA); // 用户点击了从照相机获取
                    } else {
                        ToastUtil.showCenter(mContext, R.string.no_sdcard);
                    }
                    break;
                case R.id.tv_gallery:
                    dismiss();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((Activity) mContext).startActivityForResult(intent, FROM_GALLERY);
                    break;
                case R.id.tv_cancel:
                    dismiss();
                    break;
            }
        }

        private String getPhotoFileName() {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
            return dateFormat.format(date) + ".jpg";
        }
    }

}
