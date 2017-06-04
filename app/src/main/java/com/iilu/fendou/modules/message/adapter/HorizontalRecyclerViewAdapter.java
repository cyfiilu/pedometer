package com.iilu.fendou.modules.message.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iilu.fendou.R;
import com.iilu.fendou.modules.message.entity.FaceEntity;
import com.iilu.fendou.utils.SystemUtil;
import java.util.List;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder> {


    private Context mContext;
    private List<FaceEntity> mDatas;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;

    public HorizontalRecyclerViewAdapter(Context context, List<FaceEntity> datas) {
        this.mDatas = datas;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_face_horizontal_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FaceEntity model = mDatas.get(position);
        // 点击事件和长按事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //使用该方法获取position，防止点击事件时pos未刷新问题
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos, mDatas);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //使用该方法获取position，防止点击事件时pos未刷新问题
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos, mDatas);
                    return false;
                }
            });
        }

        // 动态计算底部tab的宽度
        int width = SystemUtil.getScreenWidth(mContext);
        float itemW = width / 6;
        ViewGroup.LayoutParams lp = holder.imageBtn.getLayoutParams();
        lp.width = (int) itemW;

        // 设置icon
        holder.imageBtn.setImageDrawable(model.icon);
        if (model.isSelected) {
            holder.imageBtn.setBackgroundColor(mContext.getResources().getColor(R.color.gray_6));
        } else {
            holder.imageBtn.setBackgroundColor(mContext.getResources().getColor(R.color.gray_light));
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            imageBtn = (ImageView) itemView.findViewById(R.id.image_btn);
        }
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnClickItemListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position, List<FaceEntity> datas);

        void onItemLongClick(View view, int position, List<FaceEntity> datas);

    }

}
