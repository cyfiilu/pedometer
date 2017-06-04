package com.iilu.fendou.modules.myself.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.iilu.fendou.R;

import org.apache.log4j.Logger;

public class MyselfFragment_1 extends PreferenceFragment {

    private Logger mlog = Logger.getLogger(MyselfFragment_1.class.getSimpleName());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.myself_fragment_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 获取手机PreferenceFragment默认布局 com.android.internal.R.layout.preference_list_fragment
        // View view = super.onCreateView(inflater, container, savedInstanceState);
        // init(view);

        // 自定义ListView，注意android:id="@android:id/list
        View view = inflater.inflate(R.layout.fragment_myself_1, container, false);

        return view;
    }

    private View init(View view) {
        // 其实是一个LinearLayout容器，因此强转
        LinearLayout layout = (LinearLayout) view;
        // 设置背景色
        layout.setBackgroundResource(R.color.white);
        // 容器中第一个item是listview(xml中的数据项，其实是展示在listview上)
        ListView listView = (ListView) layout.getChildAt(0);
        // 设置item颜色选择器，系统默认太难看
        listView.setSelector(R.drawable.selector_item_main);
        // 设置左右边距为0
        listView.setPadding(0, 0, 0, 0);
        // 通过PreferenceScreen获取ListAdapter
        PreferenceScreen ps = getPreferenceScreen();
        ListAdapter listAdapter = ps.getRootAdapter();
        // ScrollView中嵌套ListView，只展示一个item，需重新测量
        remeasure(listView, listAdapter);
        return view;
    }

    private void remeasure(ListView listView, ListAdapter listAdapter) {
        if (listAdapter == null) return;

        int len = listAdapter.getCount();

        int totalHeight = 0;
        for (int i = 0; i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);listItem.setMinimumHeight(50);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
