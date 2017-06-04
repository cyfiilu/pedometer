package com.iilu.fendou.modules.message.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.iilu.fendou.R;
import com.iilu.fendou.modules.message.listener.VoiceImageViewClickListener;
import com.iilu.fendou.modules.message.utils.FaceUtils;
import com.iilu.fendou.views.CircleImageView;

import org.apache.log4j.Logger;

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private final Logger mlog = Logger.getLogger(MessageAdapter.class.getSimpleName());

    private final int MSG_TXT_RECV = 0;
    private final int MSG_TXT_SEND = 1;
    private final int MSG_VOICE_RECV = 2;
    private final int MSG_VOICE_SEND = 3;

    private Context mContext;
    private EMConversation mEMConversation;
    private List<EMMessage> mDatas;

    public MessageAdapter(Context context, EMConversation emConversation) {
        this.mContext = context;
        this.mEMConversation = emConversation;
        mDatas = mEMConversation.getAllMessages();
    }

    public void refreshUI() {
        mDatas = mEMConversation.getAllMessages();
        notifyDataSetChanged();
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
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage emMessage = (EMMessage) getItem(position);
        if (emMessage != null) {
            if (emMessage.getType() == EMMessage.Type.TXT) {
                return emMessage.direct() == EMMessage.Direct.RECEIVE ? MSG_TXT_RECV : MSG_TXT_SEND;
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                return emMessage.direct() == EMMessage.Direct.RECEIVE ? MSG_VOICE_RECV : MSG_VOICE_SEND;
            }
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage emMessage = (EMMessage) getItem(position);
        ViewHolder  viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = createViewByMessage(emMessage);
            viewHolder.imgHead = (CircleImageView) convertView.findViewById(R.id.img_head);
            if (emMessage.getType() == EMMessage.Type.TXT) {
                viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(viewHolder);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                viewHolder.imgPlayVoice = (ImageView) convertView.findViewById(R.id.iv_play_voice);
                viewHolder.voiceDuration = (TextView) convertView.findViewById(R.id.tv_record_voice_duration);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EMMessage.ChatType chatType = emMessage.getChatType();
        if (chatType == EMMessage.ChatType.Chat) {
            switch (emMessage.getType()) {
                case TXT:
                    EMTextMessageBody textBody = (EMTextMessageBody) emMessage.getBody();
                    String message = textBody.getMessage();
                    viewHolder.content.setText(FaceUtils.createFaceText(mContext, viewHolder.content, message));
                    break;
                case VOICE:
                    EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) emMessage.getBody();
                    String format = mContext.getString(R.string.record_voice_duraion);
                    viewHolder.voiceDuration.setText(String.format(format, voiceBody.getLength()));
                    viewHolder.imgPlayVoice.setOnClickListener(new VoiceImageViewClickListener(mContext, viewHolder.imgPlayVoice, emMessage, this));
                    break;
            }
        }
        return convertView;
    }

    private View createViewByMessage(EMMessage emMessage) {
        switch (emMessage.getType()) {
            case TXT:
                return emMessage.direct() == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.msg_txt_left, null)
                        : View.inflate(mContext, R.layout.msg_txt_right, null);
            case VOICE:
                return emMessage.direct() == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.msg_voice_left, null)
                        : View.inflate(mContext, R.layout.msg_voice_right, null);
        }
        return null;
    }

    class ViewHolder {
        CircleImageView imgHead;
        TextView content;
        ImageView imgPlayVoice;
        TextView voiceDuration;
    }

}
