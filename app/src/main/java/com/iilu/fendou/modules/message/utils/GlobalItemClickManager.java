package com.iilu.fendou.modules.message.utils;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.iilu.fendou.modules.message.adapter.FaceGridViewAdapter;

public class GlobalItemClickManager {

    private static GlobalItemClickManager mInstance;
    private EditText mEditText;//输入框
    private static Context mContext;

    public static GlobalItemClickManager getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            synchronized (GlobalItemClickManager.class) {
                if (mInstance == null) {
                    mInstance = new GlobalItemClickManager();
                }
            }
        }
        return mInstance;
    }

    public void attachToEditText(EditText editText) {
        mEditText = editText;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener(final int emotion_map_type) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAdapter = parent.getAdapter();

                if (itemAdapter instanceof FaceGridViewAdapter) {
                    // 点击的是表情
                    FaceGridViewAdapter emotionGvAdapter = (FaceGridViewAdapter) itemAdapter;

                    if (position == emotionGvAdapter.getCount() - 1) {
                        // 如果点击了最后一个回退按钮,则调用删除键事件
                        mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    } else {
                        // 如果点击了表情,则添加到输入框中
                        String emotionName = emotionGvAdapter.getItem(position);

                        // 获取当前光标位置,在指定位置上添加表情图片文本
                        int curPosition = mEditText.getSelectionStart();
                        StringBuilder sb = new StringBuilder(mEditText.getText().toString());
                        sb.insert(curPosition, emotionName);

                        // 特殊文字处理,将表情等转换一下
                        mEditText.setText(SpanStringUtils.getEmotionContent(emotion_map_type, mContext, mEditText, sb.toString()));

                        // 将光标设置到新增完表情的右侧
                        mEditText.setSelection(curPosition + emotionName.length());
                    }
                }
            }
        };
    }
}
