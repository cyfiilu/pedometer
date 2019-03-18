package com.iilu.fendou.modules.myself.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.iilu.fendou.MainPreferenceFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.modules.myself.activity.ScanCodeActivity;
import com.iilu.fendou.preference.MainPreference;
import com.iilu.fendou.utils.PermissionUtil;
import com.iilu.fendou.utils.SystemUtil;

import org.apache.log4j.Logger;

public class MyselfFragment_1 extends MainPreferenceFragment {

    private Logger mlog = Logger.getLogger(MyselfFragment_1.class.getSimpleName());

    private Activity mActivity;
    private MainPreference mPreScanCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.myself_fragment_1);
        mActivity = getActivity();
        mPreScanCode = (MainPreference) findPreference("scan_code");
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        ListView list = (ListView) rootView.findViewById(android.R.id.list);
        list.setDivider(new ColorDrawable(getResources().getColor(R.color.gray_D5)));
        list.setDividerHeight(SystemUtil.dip2px(mActivity, 0.5f));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            list.setSelector(R.drawable.selector_ripple_white);
        }
        list.setVerticalScrollBarEnabled(false);
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
            View listItem = listAdapter.getView(i, null, listView);
            listItem.setMinimumHeight(50);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (mPreScanCode == preference) {
            if (!PermissionUtil.checkPermission(getActivity(), new String[] {Manifest.permission.CAMERA})) {
                PermissionUtil.requestPermission(getActivity(),
                        new String[]{Manifest.permission.CAMERA}, 0x002);
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x002 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(getActivity(), ScanCodeActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
