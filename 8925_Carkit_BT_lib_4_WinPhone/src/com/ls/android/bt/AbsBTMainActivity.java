package com.ls.android.bt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.utils.log.JLog;
import com.android.utils.string.FullNameStyle;
import com.android.utils.string.LocaleUtils;
import com.android.utils.utils.AbsServiceControllerHelper.OnServiceConnectSuccessListener;
import com.bt.BTController;
import com.bt.BTDefine;
import com.bt.BTFeature;
import com.bt.BTPIMCallback;
import com.ls.bt.call.detail.AbsBtCallDetailFragment;
import com.ls.bt.contact.AbsBtContactNumFragment;
import com.ls.bt.contact.Contact;
import com.ls.bt.inout.AbsBtCallOnLingFragment;
import com.ls.bt.inout.AbsBtDialNumFragment;
import com.ls.bt.inout.AbsBtInComingCallFragment;
import com.ls.bt.match.AbsBtMatchFragment;
import com.ls.bt.match.AbsBtMatchRecordFragment;
import com.ls.bt.music.AbsBtMusicFragment;
import com.ls.bt.setting.AbsBtSettingFragment;
import com.ls.bt.utils.BtHelper;
import com.ls.bt.utils.Utils;
import com.ls.bt.utils.BtHelper.CallLog;
import com.ls.android.phone.R;
import com.nwd.kernel.utils.KernelConstant;
/***
 * 蓝牙主界面
 */
abstract public class AbsBTMainActivity extends Activity implements TabListener{
	private static final JLog LOG = new JLog("BTMainActivity", AbsBTMainActivity.DEBUG, JLog.TYPE_DEBUG);
	public static final boolean DEBUG = true;
	private InputMethodManager mInputMethodManager; 
	private FragmentManager mFragmentManager;
	public static final int INIT_FRAGMENT = 0;
	public static final int INIT_DEVICE_CONTROLLER = 11;
	
	public static final int TAB_INDEX_CALL_DETAIL = 0;
	public static final int TAB_INDEX_CONTACT = 1;
	public static final int TAB_INDEX_DIAL_NUM = 2;
	
	public static final int TAB_INDEX_MATCH = 3;
	public static final int TAB_INDEX_SETTINGS = 4;
	public static final int TAB_INDEX_COUNT = 5;
	
	/** 匹配 */
	AbsBtMatchFragment btMatchFragment;
	/** 拨号 */
	AbsBtDialNumFragment btDialNumFragment;
	/** 联系人 */
	AbsBtContactNumFragment btContactFragment;
	/** 拨打明细 */
	AbsBtCallDetailFragment btCallDetailFragment;
	/** 蓝牙设置 */
	AbsBtSettingFragment btSetfragment;
	
	/**来电*/
	AbsBtInComingCallFragment btInComingCallFragment;
	AbsBtCallOnLingFragment btCallOnLineFragment;
	public static ActionBar actionBar;
	
	private int currentPosition = 0;
	
	/**APP和蓝牙设备通讯对象*/
    protected BTController btDeviceController;
    protected BTFeature btDeviceFeature;
    String btStatus = "";
    Fragment oldFragment;
	private ViewPager mMainViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private FrameLayout mMainFrameLayout = null;
	public static Context mContext;
	/**当前页做位置*/
	private int currentPage = -1;
	
	/**切换页面*/
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/**初始化Fragment*/
			case INIT_FRAGMENT:
				LOG.print("INIT_FRAGMENT");
				btMatchFragment = onCreateFragmentBtMatch();
				btDialNumFragment = onCreateFragmentBtDialNum();
				btContactFragment = onCreateFragmentBtContactNum();
				btCallDetailFragment = onCreateFragmentBtCallDetail();
				btSetfragment = onCreateFragmentBtSetting();
				btCallOnLineFragment = onCreateFragmentBtCallOnLine();
				btInComingCallFragment = onCreateFragmentBtInComingCall();
				btStatus = BtBroadcastReceiver.BT_PHONE_STATUS;
				break;
			case INIT_DEVICE_CONTROLLER:
	        	btMatchFragment.completeDeviceConnectService(btDeviceController);
	        	btDialNumFragment.completeDeviceConnectService(btDeviceController);
	        	btContactFragment.completeDeviceConnectService(btDeviceController);
	        	btCallDetailFragment.completeDeviceConnectService(btDeviceController);
	        	btSetfragment.completeDeviceConnectService(btDeviceController);
	        	btCallOnLineFragment.completeDeviceConnectService(btDeviceController);
	        	btInComingCallFragment.completeDeviceConnectService(btDeviceController);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btMatchFragment = onCreateFragmentBtMatch();
		btDialNumFragment = onCreateFragmentBtDialNum();
		btContactFragment = onCreateFragmentBtContactNum();
		btCallDetailFragment = onCreateFragmentBtCallDetail();
		btSetfragment = onCreateFragmentBtSetting();
		btCallOnLineFragment = onCreateFragmentBtCallOnLine();
		btInComingCallFragment = onCreateFragmentBtInComingCall();
		
		/** 蓝牙电话服务断开 */
		btStatus = BtBroadcastReceiver.BT_PHONE_STATUS;
		 
		btDeviceController = BTController.getInstance(getApplicationContext());
		btDeviceController.connectService(mConnectDeviceListener);
		View view = onCreateViewMainLayout();
		mContext= this;
		setContentView(view);
		initView();
	}
 
	@Override
	protected void onStart()
	{
	    super.onStart();
	    BtHelper.appStart(mContext);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		LOG.print("main.onNewIntent");
		Log.i("wjz", "main.onNewIntent="+intent);
		super.onNewIntent(intent);
		btStatus = intent.getStringExtra(KernelConstant.EXTRA_EVENT);
		if(null!=btStatus && !"".equals(btStatus))
		{
//			showAndHideIncoming(btStatus);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		initConnectStatus();
		showAndHideIncoming(BtBroadcastReceiver.BT_PHONE_STATUS);
    }
	
	private void showAndHideIncoming(String btStatus)
	{
		LOG.print("SHOWbtStatus="+btStatus);
		if(BTDefine.ACTION_BT_INCOMING_CALL.equals(btStatus))
		{
				mMainFrameLayout.setVisibility(View.VISIBLE);
				mMainViewPager.setVisibility(View.GONE);
				actionBar.hide();
				LOG.print("隐藏VIEWPAGE 来电了currentPosition="+mMainViewPager.getCurrentItem());
				currentPosition = +mMainViewPager.getCurrentItem();
				mMainViewPager.setCurrentItem(2);
				switchToFragment(btInComingCallFragment, R.id.right_frame_layout,false,BtHelper.BtScreen.BT_DIAL_NUM);
		}else  if(BTDefine.ACTION_BT_BEGIN_CALL_ONLINE.equals(btStatus)||BTDefine.ACTION_BT_OUTGOING_CALL.equals(btStatus)){
				mMainFrameLayout.setVisibility(View.VISIBLE);
				mMainViewPager.setVisibility(View.GONE);
				actionBar.hide();
				currentPosition = +mMainViewPager.getCurrentItem();
				LOG.print("隐藏VIEWPAGE 打出去了currentPosition="+mMainViewPager.getCurrentItem());
				switchToFragment(btCallOnLineFragment, R.id.right_frame_layout,false,BtHelper.BtScreen.BT_DIAL_NUM);
		}else if(null==btStatus||btStatus.equals("")||BTDefine.ACTION_BT_HFP_RELEASE.equals(btStatus)){
				mMainViewPager.setCurrentItem(3);
			    actionBar.setSelectedNavigationItem(3);
				mMainFrameLayout.setVisibility(View.GONE);
				mMainViewPager.setVisibility(View.VISIBLE);
				actionBar.show();
				LOG.print("显示VIEWPAGE11");
		}else  if(BTDefine.ACTION_BT_HFP_ESTABLISHED.equals(btStatus)||BTDefine.ACTION_BT_END_CALL.equals(btStatus)){
				mMainViewPager.setCurrentItem(2);
			    actionBar.setSelectedNavigationItem(2);
				mMainFrameLayout.setVisibility(View.GONE);
				mMainViewPager.setVisibility(View.VISIBLE);
				actionBar.show();
				LOG.print("显示VIEWPAGE22");
		}else{
				mMainFrameLayout.setVisibility(View.GONE);
				mMainViewPager.setVisibility(View.VISIBLE);
				LOG.print("显示VIEWPAGE currentPosition="+currentPosition);
				actionBar.setSelectedNavigationItem(currentPosition);
				//mMainViewPager.setCurrentItem(2);
				mMainViewPager.setCurrentItem(currentPosition);
				actionBar.show();
			}
	}

    /**初始界面*/
	private void initView() {
		mFragmentManager = getFragmentManager();
		mMainViewPager = (ViewPager)findViewById(R.id.main_view_pager);
		mMainFrameLayout = (FrameLayout)findViewById(R.id.right_frame_layout);
		mViewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
		mMainViewPager.setAdapter(mViewPagerAdapter);
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
    	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	actionBar.setDisplayShowTitleEnabled(false);
    	actionBar.setDisplayShowHomeEnabled(false);
    	actionBar.setDisplayHomeAsUpEnabled(false);
		//mContactList.clear();
    	
    	//修改ActionBar的背景色
    	actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.highlight_red));
		
		for (int i = 0; i < mViewPagerAdapter.getCount(); ++i) {
    		actionBar.addTab(actionBar.newTab().setCustomView(mViewPagerAdapter.getLayoutView(i)).setTabListener(this)); 
		}

		mMainViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
    		@Override
    		public void onPageSelected(int position) {
    		    LOG.print("position="+position+" status="+BtBroadcastReceiver.BT_PHONE_STATUS);
    		    
    		    if(BtBroadcastReceiver.BT_PHONE_STATUS == BTDefine.ACTION_BT_HFP_RELEASE
    		    		&&position<=3)
    		    {
    		    	mMainViewPager.setCurrentItem(3);
    		    	position = 3;
    		    }
    		    actionBar.setSelectedNavigationItem(position);
    		}
    		
    		@Override
    		public void onPageScrollStateChanged(int state) {
    			switch(state) {
    				case ViewPager.SCROLL_STATE_IDLE:
    					//TODO
    					break;
    				case ViewPager.SCROLL_STATE_DRAGGING:
    					//TODO
    					break;
    				case ViewPager.SCROLL_STATE_SETTLING:
    					//TODO
    					break;
    				default:
    					//TODO
    					break;
    			}
    		}
    	});
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LOG.print("main.onPause");
		//隐藏键盘
		if(null != AbsBtContactNumFragment.editTextSearch)
		{
			mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			mInputMethodManager.hideSoftInputFromWindow(AbsBtContactNumFragment.editTextSearch.getWindowToken(), 0);
		}
	}
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        LOG.print("main.onStop");
        //btDeviceController.disconnectService();
    }
    private OnServiceConnectSuccessListener mConnectDeviceListener = new OnServiceConnectSuccessListener() {
        @Override
        public void onServiceConnectSuccess() {
        	Log.i("cmd", "设备连接成功");
        	btDeviceFeature = btDeviceController.getFeature();
        	initConnectStatus();
        	mHandler.sendEmptyMessage(INIT_DEVICE_CONTROLLER);
//        	btDeviceController.addPimDataCallback(mBTPIMCallback);
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
		LOG.print("MAING 1111 return"+"oldFragment="+(null==oldFragment)+" aFragment="+(null==aFragment));
		if(null!=oldFragment&&oldFragment!=aFragment)
		{
			LOG.print("MAING 1111 Move");
			ft.remove(oldFragment);
		}else if(oldFragment==aFragment)
		{
			LOG.print("MAING 1111 return");
			return;
		}
		LOG.print("normal");
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
		//mMainViewPager.setCurrentItem(tab.getPosition());
		int position  = tab.getPosition();
		
		//如果蓝牙未连接，那么就设置显示第3个Fragment
		if(BtBroadcastReceiver.BT_PHONE_STATUS == BTDefine.ACTION_BT_HFP_RELEASE
	    		&&position<3)
	    {
			position = 3;
	    	//actionBar.setSelectedNavigationItem(position);
	    }
		LOG.print("设置当前位置="+position);
		mMainViewPager.setCurrentItem(position);
		
	}
	@Override
	public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LOG.print("onConfigurationChanged");
		if (actionBar != null) {
			// icon点击使能
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			}
	}
	public class ViewPagerAdapter extends FragmentPagerMyAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			LOG.print("getItem");
			switch (position) {
				
				case TAB_INDEX_CALL_DETAIL:
					return btCallDetailFragment;
				case TAB_INDEX_CONTACT:
					return btContactFragment;
				case TAB_INDEX_DIAL_NUM:
					return btDialNumFragment;
				case TAB_INDEX_MATCH:
					return btMatchFragment;
				/*case TAB_INDEX_MATCH_RECORD:
					return btMatchRecordFragment;*/
				case TAB_INDEX_SETTINGS:
					return btSetfragment;
			}
			throw new IllegalStateException("No fragment at position " + position);
		}

		@Override
		public int getCount() {
			return TAB_INDEX_COUNT;
		}
    	public View getLayoutView(int position)
    	{
    		View view=onCreateViewActionbarLayout(position);
    		return view;
    	}
    }
	
		   @Override
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
		
	}
 
	private String getFirstChar(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }
	 
	private void initConnectStatus()
	{
		LOG.print("btDeviceController="+(null!=btDeviceController)+" feature="+(null != btDeviceController.getFeature()));
		if(null != btDeviceFeature)
		{
			try {
				if(btDeviceFeature.isConnectHFP())
				{
					LOG.print("onResume.connect");
					if(BtBroadcastReceiver.BT_PHONE_STATUS==BTDefine.ACTION_BT_HFP_RELEASE)
					{
						BtBroadcastReceiver.BT_PHONE_STATUS = BTDefine.ACTION_BT_HFP_ESTABLISHED;
					}
				} else
				{
					LOG.print("onResume.disconnect");
					BtBroadcastReceiver.BT_PHONE_STATUS = BTDefine.ACTION_BT_HFP_RELEASE;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			LOG.print("onKeyDown.back.status.start="+BtBroadcastReceiver.BT_PHONE_STATUS);
			if(!BTDefine.ACTION_BT_BEGIN_CALL_ONLINE.equals(BtBroadcastReceiver.BT_PHONE_STATUS))
            {
				this.finish();
            }
			LOG.print("onKeyDown.back.status.end="+BtBroadcastReceiver.BT_PHONE_STATUS);
			return false;
		}
		return false;
    }
	
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	LOG.print("onDestroy");
        mFragmentManager = null;
        if(btDeviceController != null)
        {
//        	btDeviceController.removePimDataCallback(mBTPIMCallback);
        	btDeviceController.release();
        	btDeviceController = null;
        }
      //  System.exit(0);//强制结束进程.不然重复进入会有bug
    }
    protected abstract View onCreateViewMainLayout();
    /**
     * 根据index生成相应layout的view
     * @param position
     * @return
     */
    protected abstract View onCreateViewActionbarLayout(int position);
    protected abstract AbsBtMatchFragment onCreateFragmentBtMatch();
    protected abstract AbsBtDialNumFragment onCreateFragmentBtDialNum();
    protected abstract AbsBtContactNumFragment onCreateFragmentBtContactNum();
    protected abstract AbsBtCallDetailFragment onCreateFragmentBtCallDetail();
    protected abstract AbsBtMusicFragment onCreateFragmentBtMusic();
    protected abstract AbsBtSettingFragment onCreateFragmentBtSetting();
    protected abstract AbsBtCallOnLingFragment onCreateFragmentBtCallOnLine();
    protected abstract AbsBtInComingCallFragment onCreateFragmentBtInComingCall();
}
