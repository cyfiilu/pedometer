package com.iilu.fendou.refreshs.pulltorefresh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iilu.fendou.R;

/**
 * 这个类封装了下拉刷新的布局
 */
public class HeaderLoadingLayout extends LoadingLayout {
	/** 旋转动画时间 */
	private static final int ROTATE_ANIM_DURATION = 150;
	/** Header的容器 */
	private RelativeLayout mHeaderContainer;
	/** 箭头图片 */
	private ImageView mArrowImageView;
	/** 进度条 */
	private ProgressBar mProgressBar;
	/** 状态提示TextView */
	private TextView mHintTextView;
	/** 最后更新时间的TextView */
	private TextView mHeaderTimeView;
	/** 最后更新时间的标题 */
	private TextView mHeaderTimeViewTitle;
	/** 向上的动画 */
	private Animation mRotateUpAnim;
	/** 向下的动画 */
	private Animation mRotateDownAnim;
	/** 小脚丫动画 */
	private AnimationDrawable animationDrawable;

	public HeaderLoadingLayout(Context context) {
		super(context);
		init();
	}

	public HeaderLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
		mArrowImageView = (ImageView) findViewById(R.id.pull_to_refresh_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.pull_to_refresh_header_hint_textview);
		mProgressBar = (ProgressBar) findViewById(R.id.pull_to_refresh_header_progressbar);
		mHeaderTimeView = (TextView) findViewById(R.id.pull_to_refresh_header_time);
		mHeaderTimeViewTitle = (TextView) findViewById(R.id.pull_to_refresh_last_update_time_text);

		float pivotValue = 0.5f; // SUPPRESS CHECKSTYLE
		float toDegree = -180f; // SUPPRESS CHECKSTYLE
		// 初始化旋转动画
		mRotateUpAnim = new RotateAnimation(0.0f, toDegree,
				Animation.RELATIVE_TO_SELF, pivotValue, Animation.RELATIVE_TO_SELF, pivotValue);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);

		mRotateDownAnim = new RotateAnimation(toDegree, 0.0f,
				Animation.RELATIVE_TO_SELF, pivotValue, Animation.RELATIVE_TO_SELF, pivotValue);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);

		animationDrawable = (AnimationDrawable) mArrowImageView.getDrawable();
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		mHeaderTimeView.setText(label);
	}
	
	@Override
	public void setHeaderTextColor(String color) {
		mHintTextView.setTextColor(Color.parseColor(color));
	}
	
	@Override
	public void setLoadingDrawable(Drawable drawable) {
		mArrowImageView.setImageDrawable(drawable);
		animationDrawable = (AnimationDrawable) mArrowImageView.getDrawable();
	}


	@Override
	public int getContentSize() {
		if (null != mHeaderContainer) {
			return mHeaderContainer.getHeight();
		}
		return (int) (getResources().getDisplayMetrics().density * 60);
	}

	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View container = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, null);
		return container;
	}

	@Override
	protected void onStateChanged(State curState, State oldState) {
		mArrowImageView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		super.onStateChanged(curState, oldState);
	}

	@Override
	protected void onReset() {
		mArrowImageView.clearAnimation();
		animationDrawable.stop();
		mHintTextView.setText(R.string.pull_to_refresh_header_pull_down);
	}

	@Override
	protected void onPullToRefresh() {
		if (State.RELEASE_TO_REFRESH == getPreState()) {
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mRotateDownAnim);
		}
		mHintTextView.setText(R.string.pull_to_refresh_header_pull_down);
	}

	@Override
	protected void onReleaseToRefresh() {
		mHintTextView.setText(R.string.pull_to_refresh_header_release);
	}

	@Override
	protected void onRefreshing() {
		animationDrawable.start();
		mHintTextView.setText(R.string.pull_to_refresh_header_loading);
	}
}
