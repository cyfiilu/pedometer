package com.iilu.fendou.modules.message.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.iilu.fendou.MainActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.utils.ToastUtil;

import org.apache.log4j.Logger;

import java.util.List;

public class AddFriendActivity extends MainActivity implements View.OnClickListener {

    private Logger mlog = Logger.getLogger(AddFriendActivity.class.getSimpleName());

    private final int MSG_DELETE_SUCCESS = 0x00006;

    private List<String> mUserNameList;
    private ListView mListView;
    private ArrayAdapter mAdapter;
    private EditText mInput;
    private TextView mEmpty;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DELETE_SUCCESS:
                    ToastUtil.showCenter(AddFriendActivity.this, "删除成功！");
                    initDatas();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initViews();

        initDatas();

    }

    private void initViews() {
        RelativeLayout layoutTitle = (RelativeLayout) findViewById(R.id.add_friend_title);
        ((TextView)layoutTitle.findViewById(R.id.tv_title)).setText(getString(R.string.add_friend));
        ImageView imgBack = (ImageView) layoutTitle.findViewById(R.id.iv_left);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);

        mEmpty = (TextView) findViewById(R.id.tv_empty);
        mInput = (EditText) findViewById(R.id.et_input);
        final Button complete = (Button) findViewById(R.id.btn_complete);
        complete.setOnClickListener(this);
        complete.setEnabled(false);

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    complete.setEnabled(false);
                } else {
                    complete.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mUserNameList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initListView();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initListView() {
        if (mUserNameList != null) {
            mListView = (ListView) findViewById(R.id.list_view);
            mAdapter = new ArrayAdapter(this, R.layout.simple_list_item, mUserNameList);
            mListView.setAdapter(mAdapter);
            if (mUserNameList.size() > 0) {
                mEmpty.setVisibility(View.GONE);
            } else {
                mEmpty.setVisibility(View.VISIBLE);
            }
        } else {
            mEmpty.setVisibility(View.VISIBLE);
        }

        if (mListView != null) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String userName = (String) parent.getItemAtPosition(position);
                    Intent intent = new Intent(AddFriendActivity.this, ChatActivity.class);
                    intent.putExtra("msgFrom", userName);
                    intent.putExtra("fromActivity", AddFriendActivity.class.getSimpleName());
                    startActivity(intent);
                }
            });
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final String userName = (String) parent.getItemAtPosition(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendActivity.this);
                    builder.setMessage("确定删除该好友？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        EMClient.getInstance().contactManager().deleteContact(userName);
                                        mHandler.sendEmptyMessage(MSG_DELETE_SUCCESS);
                                    } catch (HyphenateException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
                    return true;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_complete:
                final String text = mInput.getText().toString();
                if (TextUtils.isEmpty(text)) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<String> friendNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                            for (String s : friendNames) {
                                mlog.info("s = " + s);
                                if (text.equals(s)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showCenter(AddFriendActivity.this, "你们已经是好友啦！~");
                                        }
                                    });
                                    return;
                                }
                            }

                            String reason = "注意你很久了，加个好友吧~";
                            EMClient.getInstance().contactManager().addContact(text, reason);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
