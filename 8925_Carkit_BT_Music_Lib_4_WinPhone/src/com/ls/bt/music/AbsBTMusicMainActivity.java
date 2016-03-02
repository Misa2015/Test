package com.ls.bt.music;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.android.utils.log.JLog;
import com.android.utils.utils.AbsServiceControllerHelper.OnServiceConnectSuccessListener;
import com.bt.BTController;
import com.bt.BTFeature;
import com.ls.bt.utils.BtBroadcastReceiver;
import com.ls.bt.utils.BtHelper;
import com.ls.bt.utils.BtMusicService;
import com.ls.bt.utils.Utils;
/***
 * 蓝牙音乐主界面：连接服务，初始化蓝牙功能控制模块
 */
abstract public class AbsBTMusicMainActivity extends Activity implements TabListener{
	 private static final JLog LOG = new JLog("BTMainActivity", AbsBTMusicMainActivity.DEBUG, JLog.TYPE_DEBUG);
	public static final boolean DEBUG = true;
 
	private FragmentManager mFragmentManager;
	public static final int INIT_FRAGMENT = 0;
	public static final int INIT_DEVICE_CONTROLLER = 11;
	
	/** 蓝牙音乐 */
	AbsBtMusicFragment btMusicFragment;
	
	/**APP和蓝牙设备通讯对象*/
    protected BTController btDeviceController;
    protected BTFeature btDeviceFeature;
    private byte btEvent = -1;
    
    Fragment oldFragment;
	private FrameLayout mMainFrameLayout = null;
	BtMusicService mService;
	private Context mContext;
	
	/**切换页面*/
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/**初始化Fragment*/
			case INIT_FRAGMENT:
				btMusicFragment = onCreateFragmentBtMusic();
				break;
			case INIT_DEVICE_CONTROLLER:
				LOG.print("INIT_DEVICE_CONTROLLER");
	        	btMusicFragment.completeDeviceConnectService(btDeviceController);
				switchToFragment(btMusicFragment, R.id.music_frame_layout,
						false,BtHelper.BtScreen.BT_DIAL_NUM);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler.sendEmptyMessage(INIT_FRAGMENT);
		btDeviceController = BTController.getInstance(getApplicationContext());
		//连接蓝牙服务
		btDeviceController.connectService(mConnectDeviceListener);
		View view=onCreateViewBtMusicLayout();
		setContentView(view);
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
        
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
	
    /**初始界面*/
	private void initView() {
		mFragmentManager = getFragmentManager();
		mMainFrameLayout = (FrameLayout)findViewById(R.id.music_frame_layout);
	}
	
	@Override
	protected void onResume() {
		LOG.print("main.onResume btDeviceController=="+(null==btDeviceController));
		super.onResume();
		/*if (!BtBroadcastReceiver.isCalling)
			Utils.openSpeakerMusic(this);*/
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        btEvent = -1;
       /* btDeviceController.disconnectService();
        if (!BtBroadcastReceiver.isCalling)
        	Utils.closeSpeaker(this);*/
    }
    
    private OnServiceConnectSuccessListener mConnectDeviceListener = new OnServiceConnectSuccessListener() {
        @Override
        public void onServiceConnectSuccess() {
        	LOG.print("BT设备连接成功");
        	mHandler.sendEmptyMessage(INIT_FRAGMENT);
        	mHandler.sendEmptyMessage(INIT_DEVICE_CONTROLLER);
        }
    };

	/**
	 * 将指定的Fragment替换成参数中的
	 * @param aFragment
	 * @param aFragmentRef
	 * @param isAddStack
	 *            是否添加到后台栈
	 */
	private void switchToFragment(Fragment aFragment, int aFragmentRef,
			boolean isAddStack,byte screenId) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		//主要防止Fragment重复添加
		if(null!=oldFragment&&oldFragment!=aFragment)
		{
			ft.remove(oldFragment);
		} 
		ft.add(aFragmentRef, aFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		if (isAddStack)
			ft.addToBackStack(null);
		ft.commit();
		oldFragment = aFragment;
	}

	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction arg1) {
		LOG.print("tab.getPosition()="+tab.getPosition());
		// TODO Auto-generated method stub
	}
	@Override
	public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
 
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        mFragmentManager = null;
        if(btDeviceController != null)
        {
        	btDeviceController.release();
        	btDeviceController = null;
        }
    }
    protected abstract View onCreateViewBtMusicLayout();
    protected abstract View onCreateViewActionbarMusicLayout();
    protected abstract AbsBtMusicFragment onCreateFragmentBtMusic();
}
