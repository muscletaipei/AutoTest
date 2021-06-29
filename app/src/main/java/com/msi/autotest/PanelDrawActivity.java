package com.android.msi.diagnostic_trimble_g10;

import com.android.msi.diagnostic_trimble_g10.DrawView.finishListener;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class PanelDrawActivity extends Activity {

	protected static final String TAG = "PanelDrawActivity";
	private LinearLayout m_touchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
				                                          View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
				                                          View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
				                                          View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | // hide nav bar
				                                          View.SYSTEM_UI_FLAG_FULLSCREEN | // hide status bar
						                                  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		setContentView(R.layout.activity_panel_draw);
		
		init();
	}

	private void init() {
		Resources resources = getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int w = dm.widthPixels;
		int h = dm.heightPixels;
		Log.v("width:", String.valueOf(w));  //1280   // 1280//1024
		Log.v("height:", String.valueOf(h)); //736    // 752//552

		m_touchView = (LinearLayout) findViewById(R.id.touchView1);
		m_touchView.setVisibility(View.VISIBLE);

		Message msg = mHandler1.obtainMessage(0);
		mHandler1.removeMessages(0);
		mHandler1.sendMessageDelayed(msg, 60 * 1000);

		DrawView drawView = new DrawView(this);
		drawView.setOnFinishListener(new finishListener() {
			public void onRefreshType(String type) {
				if (type.equals("finish")) {
					Log.d(TAG, "OnFinishListener:Pass!");
					mHandler1.removeMessages(0);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("PanelDrawPass", 1);
					intent.putExtras(bundle);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});
		drawView.invalidate();
		m_touchView.addView(drawView);
		drawView.requestFocus();
	}

	private Handler mHandler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Log.d(TAG,"@@@@@");
				writeLog("PanelDraw", "timeout!");
				callFail();
				break;
			default:
				break;
			}
		}
	};

	private void callFail() {
		mHandler1.removeMessages(0);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("PanelDrawPass", 0);
		intent.putExtras(bundle);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed------------");
		super.onBackPressed();
		writeLog("PanelDraw", "onBackPressed");
		callFail();
	}

	private void writeLog(String funtion, String reason) {
		RecordFailReason re = new RecordFailReason(funtion, reason);
		try {
			re.Write(funtion + "_fail.log", reason);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
