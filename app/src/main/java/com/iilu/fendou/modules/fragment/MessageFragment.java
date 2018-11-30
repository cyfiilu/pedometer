package com.iilu.fendou.modules.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.ConstantConfig;
import com.iilu.fendou.dbs.MsgAddFriendDB;
import com.iilu.fendou.modules.message.activity.AddFriendActivity;
import com.iilu.fendou.modules.message.adapter.MsgRecyclerAdapter;
import com.iilu.fendou.modules.message.entity.EasemobAddFriend;
import com.iilu.fendou.modules.message.entity.EasemobMessage;
import com.iilu.fendou.modules.message.entity.MsgBase;
import com.iilu.fendou.utils.StatusBarUtil;
import com.iilu.fendou.views.RecycleViewDivider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MessageFragment extends MainFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private String mUserid = MainApplication.getCurrLoginUsername();

    private Context mContext;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MsgRecyclerAdapter mAdapter;

    private Map<String, EMConversation> mConversations = new HashMap<>();
    private List<MsgBase> mDatas = new ArrayList<>();

    private MsgAddFriendDB mMsgAddFriendDB;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter = new MsgRecyclerAdapter(mContext, mDatas);
            mRecyclerView.setAdapter(mAdapter);
            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        StatusBarUtil.compat(getActivity(), Color.TRANSPARENT);

        mMsgAddFriendDB = new MsgAddFriendDB(mContext);
        initViews(view);

        // 首次进入动画下拉刷新
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });

        return view;
    }

    private void initViews(View view) {
        RelativeLayout layoutTitle = (RelativeLayout) view.findViewById(R.id.main_title);
        TextView tvTitle = (TextView) layoutTitle.findViewById(R.id.tv_title);
        tvTitle.setText(getResources().getString(R.string.message));
        ImageView imgRight = (ImageView) layoutTitle.findViewById(R.id.iv_right);
        imgRight.setVisibility(View.VISIBLE);
        imgRight.setImageResource(R.drawable.selector_add);
        imgRight.setOnClickListener(this);

        // 1. 拿到控件对象
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);

        // 2. 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));

        // 3. 设置下拉刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        getEMAddFriendMessages();
        getEMMessages();
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void getEMAddFriendMessages() {
        Iterator<MsgBase> it = mDatas.iterator();
        while (it.hasNext()) {
            MsgBase msgBase = it.next();
            if (msgBase instanceof EasemobAddFriend) {
                it.remove();
            }
        }

        int unreadCount = mMsgAddFriendDB.queryUnreadCount(mUserid);
        List<EasemobAddFriend> list = mMsgAddFriendDB.queryFriends(mUserid);
        if (list.size() > 0) {
            EasemobAddFriend easemobAddFriend = list.get(0);
            easemobAddFriend.setUnreadCount(unreadCount);
            mDatas.add(easemobAddFriend);
        }
    }

    private void getEMMessages() {
        Iterator<MsgBase> it = mDatas.iterator();
        while (it.hasNext()) {
            MsgBase msgBase = it.next();
            if (msgBase instanceof EasemobMessage) {
                it.remove();
            }
        }

        mConversations = EMClient.getInstance().chatManager().getAllConversations();
        for (EMConversation emConversation : mConversations.values()) {
            EasemobMessage easemobMessage = new EasemobMessage();
            easemobMessage.setEmConversation(emConversation);
            mDatas.add(easemobMessage);
        }
    }

    public void onMessageReceived(EMMessage emMessage) {
        getEMMessages();

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addDatas(mDatas);
            }
        });
    }

    public void onContactInvited(EasemobAddFriend easemobAddFriend) {
        getEMAddFriendMessages();

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addDatas(mDatas);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantConfig.CHAT_CODE && resultCode == Activity.RESULT_OK) {
            getEMMessages();
        } else if (requestCode == ConstantConfig.REQUEST_ADD_FRIEND_CODE && resultCode == Activity.RESULT_OK) {
            getEMAddFriendMessages();
        }
        mHandler.sendEmptyMessageDelayed(0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_right:
                startActivity(new Intent(mContext, AddFriendActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
