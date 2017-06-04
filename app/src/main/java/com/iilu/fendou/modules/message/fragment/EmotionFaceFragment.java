package com.iilu.fendou.modules.message.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.iilu.fendou.MainFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.modules.message.adapter.FaceGridViewAdapter;
import com.iilu.fendou.modules.message.adapter.FacePagerAdapter;
import com.iilu.fendou.modules.message.utils.FaceUtils;
import com.iilu.fendou.modules.message.utils.GlobalItemClickManager;
import com.iilu.fendou.utils.SystemUtil;
import com.iilu.fendou.views.MainViewPager;
import com.iilu.fendou.views.PointIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class EmotionFaceFragment extends MainFragment {

    private final int FACE_COUNT_ONE_PAGER = 20;

    private Context mContext;
    private FacePagerAdapter mFacePagerAdapter;
    private MainViewPager mViewPager;
    private PointIndicatorView mIndicatorView; // 表情面板对应的点列表
    private int mEmotionMapType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    /**
     * 创建与Fragment对象关联的View视图时调用
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_face, container, false);

        initView(rootView);

        initEmotion();

        initListener();

        List<View> list = mIndicatorView.getImageViews();
        for (int i = 0; i < list.size(); i++) {
            View view = list.get(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem((Integer) v.getTag());
                }
            });
        }

        return rootView;
    }

    /**
     * 初始化view控件
     */
    protected void initView(View rootView) {
        mViewPager = (MainViewPager) rootView.findViewById(R.id.view_pager);
        mIndicatorView = (PointIndicatorView) rootView.findViewById(R.id.point_indicator_view);
        // 获取map的类型
        mEmotionMapType = mArgs.getInt("EMOTION_MAP_TYPE");

        mViewPager.setScanScroll(true);
    }

    /**
     * 初始化监听器
     */
    protected void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPagerPos = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndicatorView.setCurrIndex(oldPagerPos, position);
                oldPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化表情面板
     * 思路：获取表情的总数，按每行存放7个表情，动态计算出每个表情所占的宽度大小（包含间距），
     * 而每个表情的高与宽应该是相等的，这里我们约定只存放3行
     * 每个面板最多存放7*3=21个表情，再减去一个删除键，即每个面板包含20个表情
     * 根据表情总数，循环创建多个容量为20的List，存放表情，对于大小不满20进行特殊
     * 处理即可。
     */
    private void initEmotion() {
        // 获取屏幕宽度
        int screenWidth = SystemUtil.getScreenWidth(mContext);
        // item的间距
        int spacing = SystemUtil.dip2px(mContext, 12);
        // 动态计算item的宽度和高度
        int itemWidth = (screenWidth - spacing * 8) / 7;
        //动态计算gridview的总高度
        int gvHeight = itemWidth * 3 + spacing * 6;

        List<GridView> emotionViews = new ArrayList<>();
        List<String> emotionNames = new ArrayList<>();
        // 遍历所有的表情的key
        for (String emojiName : FaceUtils.getEmojiMap(mEmotionMapType).keySet()) {
            emotionNames.add(emojiName);
            // 每20个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == FACE_COUNT_ONE_PAGER) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                emotionViews.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<>();
            }
        }

        // 判断最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
            emotionViews.add(gv);
        }

        //初始化指示器
        mIndicatorView.initIndicator(emotionViews.size());
        // 将多个GridView添加显示到ViewPager中
        mFacePagerAdapter = new FacePagerAdapter(emotionViews);
        mViewPager.setAdapter(mFacePagerAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        mViewPager.setLayoutParams(params);
    }

    /**
     * 创建显示表情的GridView
     *
     * @param emotionNames
     * @param gvWidth
     * @param padding
     * @param itemWidth
     * @param gvHeight
     * @return
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        // 创建GridView
        GridView gv = new GridView(getActivity());
        // 设置点击背景透明
        gv.setSelector(android.R.color.transparent);
        // 设置7列
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding * 2);
        // 设置GridView的宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        FaceGridViewAdapter adapter = new FaceGridViewAdapter(mContext, emotionNames, itemWidth, mEmotionMapType);
        gv.setAdapter(adapter);
        // 设置全局点击事件
        gv.setOnItemClickListener(GlobalItemClickManager.getInstance(mContext).getOnItemClickListener(mEmotionMapType));
        return gv;
    }
}
