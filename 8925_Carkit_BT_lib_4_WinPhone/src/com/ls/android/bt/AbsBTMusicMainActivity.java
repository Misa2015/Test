package com.ls.android.bt;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import com.android.utils.log.JLog;
import com.android.utils.utils.AbsServiceControllerHelper.OnServiceConnectSuccessListener;
import com.bt.BTController;
import com.bt.BTFeature;
import com.ls.bt.contact.Contact;
import com.ls.bt.music.AbsBtMusicFragment;
import com.ls.bt.utils.BtHelper;
import com.ls.bt.utils.Utils;
import com.ls.android.phone.R;
/***
 * 蓝牙主界面
 * @author yangmaolin 
 * @date 2013.09.25
 *
 */
abstract public class AbsBTMusicMainActivity extends Activity implements TabListener{
	 private static final JLog LOG = new JLog("BTMainActivity", AbsBTMusicMainActivity.DEBUG, JLog.TYPE_DEBUG);
	public static final boolean DEBUG = false;
	public static List<Contact> mContactList=new ArrayList<Contact>();
 
	private FragmentManager mFragmentManager;
	public static final int INIT_FRAGMENT = 0;
	public static final int INIT_DEVICE_CONTROLLER = 11;
	/** 蓝牙音乐 */
	AbsBtMusicFragment btMusicFragment;
	//public static ActionBar actionBar;
	
	/**APP和蓝牙设备通讯对象*/
    protected BTController btDeviceController;
    protected BTFeature btDeviceFeature;
    private byte btEvent = -1;
    
    Fragment oldFragment;
	private FrameLayout mMainFrameLayout = null;
	
	/**切换页面*/
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
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
		LOG.print("main.onCreate");
		mHandler.sendEmptyMessage(INIT_FRAGMENT);
		btDeviceController = BTController.getInstance(getApplicationContext());
		btDeviceController.connectService(mConnectDeviceListener);
		View view=onCreateViewBtMusicLayout();
		setContentView(view);
	//	setContentView(R.layout.bt_music_main_layout);
		initView();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
//		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//    	int ret = audioManager.requestAudioFocus(null,
//                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);  
//        if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {  
//            android.util.Log.i("BtMusic", "====request audio focus fail. " + ret);  
//        }  
//		audioManager.setMode(AudioManager.Str);
	}
	

	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		LOG.print("main.onNewIntent");
		super.onNewIntent(intent);
	}
	


    /**初始界面*/
	private void initView() {
		mFragmentManager = getFragmentManager();
		mMainFrameLayout = (FrameLayout)findViewById(R.id.music_frame_layout);
/*		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
    	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	actionBar.setDisplayShowTitleEnabled(false);
    	actionBar.setDisplayShowHomeEnabled(false);
    	actionBar.setDisplayHomeAsUpEnabled(false);
		mContactList.clear();
		actionBar.addTab(actionBar.newTab().setCustomView(onCreateViewActionbarMusicLayout()).setTabListener(this)); 
        LOG.print("actionBar.SIZE="+actionBar.getTabCount());*/
	}
	@Override
	protected void onResume() {
		LOG.print("main.onResume btDeviceController=="+(null==btDeviceController));
		// TODO Auto-generated method stub
		super.onResume();
		Utils.openSpeakerMusic(this);
    }
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LOG.print("main.onPause");
	}
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        LOG.print("main.onStop");
        btEvent = -1;
        btDeviceController.disconnectService();
//        AudioManager audioManager = (AudioManager) this
//				.getSystemService(Context.AUDIO_SERVICE);
//    	
//    	audioManager.abandonAudioFocus(null); 
//    	audioManager = null;
        Utils.closeSpeaker(this);
    }
    private OnServiceConnectSuccessListener mConnectDeviceListener = new OnServiceConnectSuccessListener() {

        @Override
        public void onServiceConnectSuccess() {
        	LOG.print("BT设备连接成功");
            // TODO Auto-generated method stub
        	mHandler.sendEmptyMessage(INIT_FRAGMENT);
        	mHandler.sendEmptyMessage(INIT_DEVICE_CONTROLLER);
        }
    };
    
    
    
	/**
	 * 将指定的Fragment替换成参数中的
	 * 
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
		}/*else if(oldFragment==aFragment)
		{
			return;
		}*/
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
		LOG.print("onConfigurationChanged");
/*		if (actionBar != null) {
			// icon点击使能
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			}*/
	}

	
	/*   @Override
public boolean onCreateOptionsMenu(Menu menu) {
	// TODO Auto-generated method stub
	return super.onCreateOptionsMenu(menu);
}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	LOG.print("main onOptionsItemSelected");
		switch (item.getItemId()) {
		case android.R.id.home:
			initView();
			Toast.makeText(this, "home", 1).show();
			break;

		case android.R.id.title:
			Toast.makeText(this, "title", 1).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
		
	}*/
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        mFragmentManager = null;
        if(btDeviceController != null)
        {
        	btDeviceController.release();
        	btDeviceController = null;
        }
        System.exit(0);//强制结束进程.不然重复进入会有bug
    
    }
    protected abstract View onCreateViewBtMusicLayout();
    protected abstract View onCreateViewActionbarMusicLayout();
    protected abstract AbsBtMusicFragment onCreateFragmentBtMusic();
}
