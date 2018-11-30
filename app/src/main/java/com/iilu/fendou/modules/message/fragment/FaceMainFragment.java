package com.iilu.fendou.modules.message.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iilu.fendou.R;
import com.iilu.fendou.modules.message.activity.ChatFaceSettingActivity;
import com.iilu.fendou.modules.message.adapter.HorizontalRecyclerViewAdapter;
import com.iilu.fendou.modules.message.adapter.ViewPagerAdapter;
import com.iilu.fendou.modules.message.entity.FaceEntity;
import com.iilu.fendou.modules.message.utils.FaceUtils;
import com.iilu.fendou.modules.message.utils.GlobalItemClickManager;

import java.util.ArrayList;
import java.util.List;

public class FaceMainFragment extends ChatBottomFragmentBase {

    /** 表情面板Fragment */
    private List<Fragment> mFaceFragments = new ArrayList<>();
    /** 表情面板下边Tab数据 */
    private List<FaceEntity> mFaceDatas = new ArrayList<>();
    /** 更多面板Fragment */
    private List<Fragment> mMoreFragments = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initListener();
        // 表情面板对应的Fragment
        initFaceFragments();
        // 表情面板下边对应的Tab
        initFaceTabs();
        // 更多面板对应的Fragment
        initMoreFragments();

        // 创建全局监听
        GlobalItemClickManager globalItemClickManager = GlobalItemClickManager.getInstance(mChatActivity);
        // 绑定当前Bar的编辑框
        globalItemClickManager.attachToEditText(mInput);

        return view;
    }

    public void resetUI() {
        mInput.clearFocus();
        mImgFace.setImageResource(R.drawable.selector_msg_face);
    }

    private void initListener() {
        mInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentView instanceof ListView) {
                    ListView listView = (ListView) mContentView;
                    listView.setSelection(listView.getCount() - 1);
                }
                mInput.setBackground(getResources().getDrawable(R.mipmap.edittext_bg_focus));
            }
        });
        mInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mInput.setBackground(getResources().getDrawable(R.mipmap.edittext_bg_focus));
                } else {
                    mInput.setBackground(getResources().getDrawable(R.mipmap.edittext_bg_normal));
                }
            }
        });
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mImgMore.setVisibility(View.VISIBLE);
                    mSend.setVisibility(View.GONE);
                } else {
                    mImgMore.setVisibility(View.GONE);
                    mSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /** 表情面板Fragment */
    private void initFaceFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt("EMOTION_MAP_TYPE", FaceUtils.FACE_CLASSIC_TYPE);
        bundle.putString("fragment_num", "经典表情");
        EmotionFaceFragment emotionFaceFragment = EmotionFaceFragment.newInstance(EmotionFaceFragment.class, bundle);
        mFaceFragments.add(emotionFaceFragment);
        for (int i = 0; i < 7; i++) {
            bundle = new Bundle();
            if (i == 0) {
                bundle.putString("fragment_num", "收藏");
            } else {
                bundle.putString("fragment_num", "其他 - " + i);
            }
            Fragment_1 fragment_1 = Fragment_1.newInstance(Fragment_1.class, bundle);
            mFaceFragments.add(fragment_1);
        }

        mFaceViewPagerAdapter = new ViewPagerAdapter(getFragmentManager(), mFaceFragments);
        mFaceViewPager.setAdapter(mFaceViewPagerAdapter);
        mFaceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changePager(position, mFaceDatas);
                mRecyclerView.smoothScrollToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /** 表情面板下边对应Tab，与Fragment */
    private void initFaceTabs() {
        FaceEntity faceEntity = new FaceEntity();
        faceEntity.icon = getResources().getDrawable(R.mipmap.msg_face_normal);
        faceEntity.flag = "经典笑脸";
        faceEntity.isSelected = true;
        mFaceDatas.add(faceEntity);

        FaceEntity favoriteEntity = new FaceEntity();
        favoriteEntity.icon = getResources().getDrawable(R.mipmap.favorite);
        favoriteEntity.flag = "收藏";
        favoriteEntity.isSelected = false;
        mFaceDatas.add(favoriteEntity);

        for (int i = 2; i < mFaceFragments.size(); i++) {
            FaceEntity otherEntity = new FaceEntity();
            otherEntity.icon = getResources().getDrawable(R.mipmap.more_nomal);
            otherEntity.flag = "其他";
            otherEntity.isSelected = false;
            mFaceDatas.add(otherEntity);
        }

        FaceEntity settingEntity = new FaceEntity();
        settingEntity.icon = getResources().getDrawable(R.mipmap.setting);
        settingEntity.flag = "设置";
        settingEntity.isSelected = false;
        mFaceDatas.add(settingEntity);

        mRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(mChatActivity, mFaceDatas);
        mRecyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        mRecyclerViewAdapter.setOnClickItemListener(new HorizontalRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, List<FaceEntity> datas) {
                if (position == mFaceFragments.size()) {
                    startActivity(new Intent(mChatActivity, ChatFaceSettingActivity.class));
                } else {
                    changePager(position, datas);
                }
            }

            @Override
            public void onItemLongClick(View view, int position, List<FaceEntity> datas) {
            }
        });
    }

    private void changePager(int position, List<FaceEntity> datas) {
        for (int i = 0; i < datas.size(); i++) {
            if (position == i) {
                datas.get(i).isSelected = true;
            } else {
                datas.get(i).isSelected = false;
            }
        }
        mRecyclerViewAdapter.notifyDataSetChanged();
        // ViewPager页面切换
        mFaceViewPager.setCurrentItem(position, false);
    }

    /** 更多表情面板Fragment */
    private void initMoreFragments() {
        Bundle bundle;
        for (int i = 0; i < 2; i++) {
            bundle = new Bundle();
            bundle.putString("fragment_num", "更多面板 - " + i);
            Fragment_1 fragment_1 = Fragment_1.newInstance(Fragment_1.class, bundle);
            mMoreFragments.add(fragment_1);
        }

        mIndicatorView.initIndicator(mMoreFragments.size());

        mMoreViewPagerAdapter = new ViewPagerAdapter(getFragmentManager(), mMoreFragments);
        mMoreViewPager.setAdapter(mMoreViewPagerAdapter);
        mMoreViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        List<View> list = mIndicatorView.getImageViews();
        for (int i = 0; i < list.size(); i++) {
            View view = list.get(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoreViewPager.setCurrentItem((Integer) v.getTag());
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
