package com.ls.android.phone;

import com.bt.BTDefine;
import com.ls.bt.inout.AbsBtCallOnLingFragment;
import com.ls.bt.utils.BtHelper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class BtCallOnLineService extends Service {

	private View view;
	private WindowManager mWindow;
	private WindowManager.LayoutParams wmParams;
	private int screenWidth;
	private int screenHeight;
	private TextView callTime;
    private static final int GET_DIALING_LONG_TIME = 0;
	
  	public int getCallTime;
  	private int mDialingTimeCount;
  	
	public Handler mHandler = new Handler(){

		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case GET_DIALING_LONG_TIME:
				Log.i("wuguisheng", "handleMessage");
				updateDailingTime();
				mHandler.sendEmptyMessageDelayed(GET_DIALING_LONG_TIME, 1000);
				break;
			default:
				break;
			}
		};
	};
  	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("wuguisheng", "BtCallOnLineService onCreate");
		
		view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bt_call_service, null);
		
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		float scaledDensity = dm.scaledDensity;
		float xdpi = dm.xdpi;
		float ydpi = dm.ydpi;
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		 
		createView();
	}

	private void createView() {
		mWindow = (WindowManager) getApplicationContext().getSystemService("window");
		wmParams = new WindowManager.LayoutParams();

		// 2002系统级窗口
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.BOTTOM; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置悬浮窗口长宽数据
		// wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.width = screenWidth;
		wmParams.height = 120;
		wmParams.format = 1;

		callTime = (TextView) view.findViewById(R.id.call_time);
		mWindow.addView(view, wmParams);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("wuguisheng", "BtCallOnLineService onStartCommand");
		 //获取到通话时间进度
	    mDialingTimeCount = intent.getIntExtra("DialingTimeCount", 0);
	    mHandler.sendEmptyMessage(GET_DIALING_LONG_TIME);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				BtHelper.startMaintActivity(getBaseContext(),BTDefine.ACTION_BT_BEGIN_CALL_ONLINE);
				return false;
			}
		});
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("wuguisheng", "BtCallOnLineService onDestroy");
		mWindow.removeView(view);
		mHandler.removeMessages(GET_DIALING_LONG_TIME);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void updateDailingTime()
    {
        int min = 0;
        int second = 0;
        if (mDialingTimeCount >= 60){
            min = mDialingTimeCount / 60;
        }
        second = mDialingTimeCount - 60 * min;
        callTime.setText((min > 9 ? min : "0" + min) + ":" + (second > 9 ? second : "0" + second));
        mDialingTimeCount++;
        Log.i("wuguisheng","Service:"+getCallTime);
    }
	
}
