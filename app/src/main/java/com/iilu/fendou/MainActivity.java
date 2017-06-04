package com.iilu.fendou;

import android.os.Bundle;

import com.iilu.fendou.utils.SystemUtil;
import com.lib.swipeback.SwipeBackLayout;
import com.lib.swipeback.app.SwipeBackActivity;

import org.apache.log4j.Logger;

public class MainActivity extends SwipeBackActivity {

    private Logger mLog = Logger.getLogger(MainActivity.class);

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSwipeBackLayout = getSwipeBackLayout();

        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {

            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                SystemUtil.vibrate(MainActivity.this, 20);
            }

            @Override
            public void onScrollOverThreshold() {
                SystemUtil.vibrate(MainActivity.this, 20);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
