package com.iilu.fendou.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.iilu.fendou.interfaces.OnNotificationListener;

public class NoScrollListView extends ListView {
	
	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
