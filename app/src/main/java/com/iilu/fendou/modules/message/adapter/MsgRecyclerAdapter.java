package com.iilu.fendou.modules.message.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.ConstantConfig;
import com.iilu.fendou.dbs.MsgAddFriendDB;
import com.iilu.fendou.modules.message.activity.ChatActivity;
import com.iilu.fendou.modules.message.activity.RequestAddFriendActivity;
import com.iilu.fendou.modules.message.entity.EasemobAddFriend;
import com.iilu.fendou.modules.message.entity.EasemobMessage;
import com.iilu.fendou.modules.message.entity.MsgBase;
import com.iilu.fendou.modules.message.utils.FaceUtils;
import com.iilu.fendou.utils.DateUtil;
import com.iilu.fendou.views.CircleImageView;
import com.jauker.widget.BadgeView;

import java.util.List;

public class MsgRecyclerAdapter extends RecyclerView.Adapter<MsgRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<MsgBase> mDatas;

    public MsgRecyclerAdapter(Context context, List<MsgBase> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_msg, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(i);
        MsgBase msgBase = mDatas.get(i);
        if (msgBase instanceof EasemobMessage) {
            EasemobMessage easemobMessage = (EasemobMessage) msgBase;
            EMConversation emConversation = easemobMessage.getEmConversation();
            EMMessage emMessage = emConversation.getLastMessage();
            viewHolder.mImgHead.setImageResource(R.mipmap.head_default);
            viewHolder.mFriend.setText(emMessage.getUserName());
            viewHolder.mTime.setText(DateUtil.convertToFormatDate(emMessage.getMsgTime()));
            EMMessageBody emMessageBody = emMessage.getBody();
            if (emMessageBody instanceof EMTextMessageBody) {
                viewHolder.mDescription.setText(FaceUtils.createFaceText(mContext, viewHolder.mDescription, ((EMTextMessageBody)emMessageBody).getMessage()));
            } else if (emMessageBody instanceof EMVoiceMessageBody) {
                viewHolder.mDescription.setText("[语音]");
            }
            int unReadCount = emConversation.getUnreadMsgCount();
            if (unReadCount != 0) {
                BadgeView badgeView = new BadgeView(mContext);
                badgeView.setTargetView(viewHolder.mImgHead);
                badgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
                badgeView.setBadgeCount(unReadCount);
            }
        } else if (msgBase instanceof EasemobAddFriend) {
            EasemobAddFriend easemobAddFriend = (EasemobAddFriend) msgBase;
            viewHolder.mImgHead.setImageResource(R.mipmap.head_default);
            viewHolder.mFriend.setText(easemobAddFriend.getFriendName());
            viewHolder.mTime.setText(DateUtil.convertToFormatDate(easemobAddFriend.getReceiveTime()));
            viewHolder.mDescription.setText(FaceUtils.createFaceText(mContext, viewHolder.mDescription, easemobAddFriend.getReason()));
            int unReadCount = easemobAddFriend.getUnreadCount();
            if (unReadCount != 0) {
                BadgeView badgeView = new BadgeView(mContext);
                badgeView.setTargetView(viewHolder.mImgHead);
                badgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
                badgeView.setBadgeCount(unReadCount);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        MsgBase msgBase = mDatas.get(position);
        if (msgBase instanceof EasemobAddFriend) {
            return ViewType.AddFriend.ordinal();
        } else if (msgBase instanceof EasemobMessage) {
            return ViewType.Chat.ordinal();
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onClick(View v) {
        String msgFrom;
        Intent intent;
        MsgBase msgBase = mDatas.get((int) v.getTag());
        if (msgBase instanceof EasemobMessage) {
            EMConversation emConversation = ((EasemobMessage) msgBase).getEmConversation();
            msgFrom = emConversation.getLastMessage().getUserName();
            intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("msgFrom", msgFrom);
            intent.putExtra("fromActivity", ((Activity) mContext).getClass().getSimpleName());
            //启动前将所有消息标为已读
            emConversation.markAllMessagesAsRead();
            ((Activity) mContext).startActivityForResult(intent, ConstantConfig.CHAT_CODE);
        } else if (msgBase instanceof EasemobAddFriend) {
            EasemobAddFriend easemobAddFriend = ((EasemobAddFriend) msgBase);
            //将所有消息标为已读
            new MsgAddFriendDB(mContext).markMessageAsRead(easemobAddFriend.getUserid());
            intent = new Intent(mContext, RequestAddFriendActivity.class);
            ((Activity) mContext).startActivityForResult(intent, ConstantConfig.REQUEST_ADD_FRIEND_CODE);
        }
    }

    public enum ViewType {
        AddFriend,
        Chat,
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mImgHead;
        private TextView mFriend;
        private TextView mTime;
        private TextView mDescription;

        public ViewHolder(View view) {
            super(view);
            mImgHead = (CircleImageView) view.findViewById(R.id.img_head);
            mFriend = (TextView) view.findViewById(R.id.tv_from);
            mTime = (TextView) view.findViewById(R.id.tv_time);
            mDescription = (TextView) view.findViewById(R.id.tv_description);
        }
    }

    /**
     * 下拉刷新添加数据
     *
     * @param newDatas
     */
    public void addDatas(List<MsgBase> newDatas) {
        mDatas = newDatas;
        notifyDataSetChanged();
    }

    /**
     * 上拉刷新添加数据
     *
     * @param newDatas
     */
    public void addMoreDatas(List<MsgBase> newDatas) {
        mDatas.addAll(newDatas);
        notifyDataSetChanged();
    }

}
