package com.iilu.fendou.refreshs.reload;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.iilu.fendou.R;

public class ReloadListView extends ListView implements OnScrollListener {

    private static final int RELEASE_TO_REFRESH = 0;
    private static final int PULL_TO_REFRESH = 1;
    private static final int REFRESHING = 2;
    private static final int DONE = 3;
    private static final int LOADING = 4;
    private static final int RATIO = 3;

    private int headContentHeight;
    private int headContentWidth;
    private int state;
    private int firstItemIndex;
    private int startY;

    private boolean isRefreshable;
    private boolean isRecored;
    private boolean isBack;

    private LayoutInflater inflater;
    private LinearLayout headView;
    private ImageView progressBar;
    private RelativeLayout backgroundImage;
    private AnimationDrawable animationDrawable;

    private OnRefreshListener refreshListener;
    private OnMyScrollListener onmyscrollListener;

    public ReloadListView(Context context) {
        super(context);
        init(context);
    }

    public ReloadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.listview_reload_header, null);
        progressBar = (ImageView) headView.findViewById(R.id.animationiamge);
        animationDrawable = (AnimationDrawable) progressBar.getBackground();
        backgroundImage = (RelativeLayout) headView.findViewById(R.id.image);
        measureView(headView);
        headContentHeight = progressBar.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();

        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        headView.invalidate();

        addHeaderView(headView);
        setOnScrollListener(this);

        state = DONE;
        isRefreshable = false;
    }

    /**
     * 是否有头部背景
     * @param flag
     */
    public void isvisibleImage(boolean flag) {
        if (!flag) backgroundImage.setVisibility(View.GONE);
    }

    /**
     * @param child
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstItemIndex = firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 隐藏输入框
        hideInput();
        if (onmyscrollListener != null) {
            onmyscrollListener.onScroll(view, scrollState);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isRefreshable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (firstItemIndex == 0 && !isRecored) {
                        isRecored = true;
                        startY = (int) ev.getY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (state != REFRESHING && state != LOADING) {
                        if (state == DONE) {
                            // do nothing
                        }
                        if (state == PULL_TO_REFRESH) {
                            state = DONE;
                            changeHeaderViewByState();
                        }
                        if (state == RELEASE_TO_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                        }
                    }
                    isRecored = false;
                    isBack = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) ev.getY();
                    if (!isRecored && firstItemIndex == 0) {
                        isRecored = true;
                        startY = tempY;
                    }
                    if (state != REFRESHING && isRecored && state != LOADING) {
                        // 保证在设置padding的过程中，当前的位置一直是在head，
                        // 否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                        // 可以松手去刷新了
                        if (state == RELEASE_TO_REFRESH) {
                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0) {
                                state = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                            // 一下子推到顶了
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                            } else { // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                                // 不用进行特别的操作，只用更新paddingTop的值就行了
                            }
                        }
                        // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
                        if (state == PULL_TO_REFRESH) {
                            // 下拉到可以进入RELEASE_TO_REFRESH的状态
                            if ((tempY - startY) / RATIO >= headContentHeight) {
                                state = RELEASE_TO_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();
                            }
                            // 上推到顶了
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                            }
                        }
                        // done状态下
                        if (state == DONE) {
                            if (tempY - startY > 0) {
                                state = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                        }
                        // 更新headView的size
                        if (state == PULL_TO_REFRESH) {
                            headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);
                        }
                        // 更新headView的paddingTop
                        if (state == RELEASE_TO_REFRESH) {
                            headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_TO_REFRESH:

                break;
            case PULL_TO_REFRESH:
                if (isBack) {
                    isBack = false;
                } else {

                }
                break;
            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                progressBar.setVisibility(View.VISIBLE);
                animationDrawable.start();
                break;
            case DONE:
                headView.setPadding(0, -1 * headContentHeight, 0, 0);
                animationDrawable.stop();
                break;
        }
    }

    public void setonRefreshListener(OnRefreshListener onRefreshListener) {
        this.refreshListener = onRefreshListener;
        isRefreshable = true;
    }

    public interface OnRefreshListener {
        void onRefresh();

        /**
         * 隐藏输入框，只有说说列表实现改监听方法
         */
        void hide();
    }

    /**
     * 刷新完成，改变状态
     */
    public void onRefreshComplete() {
        state = DONE;
        changeHeaderViewByState();
    }

    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    private void hideInput() {
        if (refreshListener != null) {
            refreshListener.hide();
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        super.setAdapter(adapter);
    }

    /**
     * 上拉分页接口
     */
    public interface OnMyScrollListener {
        void onScroll(AbsListView view, int scrollState);
    }

    public void setOnMyScrollListener(OnMyScrollListener onmyscrollListener) {
        this.onmyscrollListener = onmyscrollListener;
    }

    public LinearLayout getHeadView() {
        return headView;
    }

    public void setisRefreshable(boolean isRefresh) {
        this.isRefreshable = isRefresh;
    }
}

