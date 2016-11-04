package com.me.chat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.me.chat.R;
import com.me.chat.tool.Tools;

public class SplashActivity extends Activity {

	private TextView splashText;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		splashText = (TextView) findViewById(R.id.splashText);

		// 进入程序时的小动画
		if (Tools.isNetworkConnected(this)) {
			AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
			aa.setDuration(4000);
			// splashText.setText("稍后进入...");
			splashText.startAnimation(aa);

			// 延时2秒执行r任务
			new Handler().postDelayed(new LoadMainTask(), 4000);
		} else {
			showSetNetworkDialog();
		}

	}

	private class LoadMainTask implements Runnable {

		@Override
		public void run() {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);

			// 关闭splash页面
			finish();
		}

	}

	private void showSetNetworkDialog() {
		// 打开对话框
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("设置网络");
		builder.setMessage("网络错误，请检查网络状态");
		builder.setPositiveButton("设置网络", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (android.os.Build.VERSION.SDK_INT > 10) {
					// 3.0以上打开设置界面
					startActivity(new Intent(
							android.provider.Settings.ACTION_SETTINGS));
				} else {
					startActivity(new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
				finish();
			}

		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}

		});
		builder.create().show();
	}
}
