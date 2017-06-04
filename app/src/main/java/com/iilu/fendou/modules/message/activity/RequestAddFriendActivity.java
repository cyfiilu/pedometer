package com.iilu.fendou.modules.message.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.iilu.fendou.MainActivity;
import com.iilu.fendou.MainApplication;
import com.iilu.fendou.R;
import com.iilu.fendou.dbs.MsgAddFriendDB;
import com.iilu.fendou.modules.message.entity.EasemobAddFriend;

import java.util.List;

public class RequestAddFriendActivity extends MainActivity implements View.OnClickListener {

    private String mUserid = MainApplication.getCurrLoginUsername();

    private List<EasemobAddFriend> mDatas;
    private ListView mListView;

    private MsgAddFriendDB mMsgAddFriendDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_add_friend);

        initDatas();

        initViews();

    }

    private void initDatas() {
        mMsgAddFriendDB = new MsgAddFriendDB(this);
        mDatas = mMsgAddFriendDB.queryFriends(mUserid);
    }

    private void initViews() {
        RelativeLayout layoutTitle = (RelativeLayout) findViewById(R.id.request_add_friend_title);
        ImageView imgBack = (ImageView) layoutTitle.findViewById(R.id.iv_left);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);

        ((TextView)layoutTitle.findViewById(R.id.tv_title)).setText("新朋友");

        mListView = (ListView) findViewById(R.id.list_view);
        MyAdapter adapter = new MyAdapter(this, mDatas);
        mListView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                backPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backPressed();
        super.onBackPressed();
    }

    private void backPressed() {
        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyAdapter extends BaseAdapter {

        private Context mContext;
        private List<EasemobAddFriend> mDatas;

        public MyAdapter(Context context, List<EasemobAddFriend> datas) {
            this.mContext = context;
            this.mDatas = datas;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(mContext, R.layout.item_request_add_friend, null);

            TextView friendName = (TextView) convertView.findViewById(R.id.tv_friend_name);
            Button btnAgree = (Button) convertView.findViewById(R.id.btn_agree);
            TextView tvAgreed = (TextView) convertView.findViewById(R.id.tv_agreed);
            TextView tvDescription = (TextView) convertView.findViewById(R.id.tv_description);

            final EasemobAddFriend easemobAddFriend = (EasemobAddFriend) getItem(position);
            friendName.setText(easemobAddFriend.getFriendName());
            tvDescription.setText(easemobAddFriend.getReason());
            if ("1".equals(easemobAddFriend.getAccepte())) {
                btnAgree.setVisibility(View.VISIBLE);
                tvAgreed.setVisibility(View.GONE);
            } else {
                btnAgree.setVisibility(View.GONE);
                tvAgreed.setVisibility(View.VISIBLE);
            }

            btnAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(easemobAddFriend.getFriendName());
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    easemobAddFriend.setAccepte("0");
                    mMsgAddFriendDB.updateFriend(mUserid, easemobAddFriend);
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("msgFrom", easemobAddFriend.getFriendName());
                    mContext.startActivity(intent);
                }
            });

            return convertView;
        }
    }

}
