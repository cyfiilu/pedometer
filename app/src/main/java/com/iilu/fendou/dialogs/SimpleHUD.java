package com.iilu.fendou.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.iilu.fendou.R;

public class SimpleHUD {

	private static SimpleHUDDialog mDialog;
	private static Context mContext;

	public static void showLoadingMessage(Context context, String msg, boolean cancelable) {
		dismiss();
		setDialog(context, msg, R.anim.anim_loading, R.layout.dialog_simplehud, cancelable);
		if (mDialog != null) mDialog.show();
	}

	public static void showLoadingMessage(Context context, int msg, boolean cancelable) {
		dismiss();
		setDialog(context, context.getResources().getString(msg), R.anim.anim_loading, R.layout.dialog_simplehud, cancelable);
		if (mDialog != null) mDialog.show();
	}

	public static void closeLoadingMessage() {
		mDialog.dismiss();
	}

	public static void showErrorMessage(Context context, int msg, long duration) {
		dismiss();
		setDialog(context, context.getResources().getString(msg), R.mipmap.simplehud_error, R.layout.dialog_simplehud, true);
		if (mDialog != null) {
			mDialog.show();
			dismissAfter(duration);
		}
	}

	public static void showErrorMessage(Context context, String msg, long duration) {
		dismiss();
		setDialog(context, msg, R.mipmap.simplehud_error, R.layout.dialog_simplehud, true);
		if (mDialog != null) {
			mDialog.show();
			dismissAfter(duration);
		}
	}

	public static void showSuccessMessage(Context context, int msg, long duration) {
		dismiss();
		setDialog(context, context.getResources().getString(msg), R.mipmap.simplehud_success, R.layout.dialog_simplehud, true);
		if (mDialog != null) {
			mDialog.show();
			dismissAfter(duration);
		}
	}

	public static void showSuccessMessage(Context context, String msg, long duration) {
		dismiss();
		setDialog(context, msg, R.mipmap.simplehud_success, R.layout.dialog_simplehud, true);
		if (mDialog != null) {
			mDialog.show();
			dismissAfter(duration);
		}
	}

	public static void showInfoMessage(Context context, String msg, long duration) {
		dismiss();
		setDialog(context, msg, R.mipmap.simplehud_info, R.layout.dialog_simplehud, true);
		if (mDialog != null) {
			mDialog.show();
			dismissAfter(duration);
		}
	}

	public static void showInfoMessage(Context context, int msg, long duration) {
		dismiss();
		setDialog(context, context.getResources().getString(msg), R.mipmap.simplehud_info, R.layout.dialog_simplehud, true);
		if (mDialog != null) {
			mDialog.show();
			dismissAfter(duration);
		}
	}

	public static void showInfoMessage(Context context, int drawable, String msg1, String msg2, long duration) {
		dismiss();
		setDialog(context, msg1, msg2, drawable, R.layout.dialog_simplehud, true);
		if (mDialog != null) {
			mDialog.show();
			dismissAfter(duration);
		}
	}

	private static void setDialog(Context context, String msg, int resId, int layoutId, boolean cancelable) {
		mContext = context;
		if (!isContextValid()) return;
		mDialog = SimpleHUDDialog.createDialog(context, layoutId);
		mDialog.setMessage(msg);
		mDialog.setImage(context, resId);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(cancelable); // back键是否可dimiss对话框
	}

	private static void setDialog(Context context, String msg1, String msg2, int resId, int layoutId, boolean cancelable) {
		mContext = context;
		if (!isContextValid()) return;
		mDialog = SimpleHUDDialog.createDialog(context, layoutId);
		mDialog.setMessage(msg1, msg2);
		mDialog.setImage(context, resId);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(cancelable); // back键是否可dimiss对话框
	}

	public static void dismiss() {
		if (isContextValid() && mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog = null;
	}

	/**
	 * 计时关闭对话框
	 * @param duration
     */
	private static void dismissAfter(final long duration) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(duration);
					mHanlder.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private static Handler mHanlder = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) dismiss();
		}
	};

	/**
	 * 判断parent view是否还存在，若不存在不能调用dismis，或setDialog等方法
	 * @return
	 */
	private static boolean isContextValid() {
		if (mContext == null) return false;
		if (mContext instanceof Activity) {
			Activity act = (Activity) mContext;
			if (act.isFinishing()) return false;
		}
		return true;
	}

	public static boolean isShowing() {
		if (mDialog != null) {
			return mDialog.isShowing();
		}
		return false;
	}

}
