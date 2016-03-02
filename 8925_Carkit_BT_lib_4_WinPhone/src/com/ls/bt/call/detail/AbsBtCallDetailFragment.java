package com.ls.bt.call.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.utils.log.JLog;
import com.android.utils.string.FullNameStyle;
import com.android.utils.string.LocaleUtils;
import com.bt.BTController;
import com.bt.BTDefine;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.bt.contact.Contact;
import com.ls.bt.myview.MyProgress;
import com.ls.bt.utils.BtHelper;
import com.ls.android.phone.R;
/***
 * 蓝牙呼叫日志
 */
abstract public class AbsBtCallDetailFragment extends AbsBaseFragment implements OnItemClickListener{
	 
	public static final boolean DEBUG = true;
	Menu callMenu;
	int currentMenuItemPosition = 0;
    private final static int HIDE_ANIMATION = -1;
    private final static int SHOW_ANIMATION = 0;
	/**呼出list*/
	public static List<Contact> mCallOutList = new ArrayList<Contact>();
	/**未接听List*/
	public static List<Contact> mMissedcallsList = new ArrayList<Contact>();
	/**呼入list*/
	public static List<Contact> mReceivedList = new ArrayList<Contact>();
	/** */
	private static AbsCallDetailAdapter mCalloutAdapter = null;
	/** */
	private static AbsCallDetailAdapter mReceiverAdapter = null;
	/** */
	private static AbsCallDetailAdapter mMissedAdapter = null;
	 /**开始下载显示图片*/
	 private ImageView mIvCallDownLoad;
	 
	 /**开始下载背景*/
	 private ImageView mIvCallDownLoadBg;
	 /**开始下载动画*/
	 private Animation mAnimationDownLoad ;
	/** 呼出listView*/
	private ListView mCallOutListView;
	/** 呼入listView*/
	private ListView mReceivedListView;
	/** 未接listView*/
	private ListView mMissedListView;
	public final static String MESSAGE_NAME = "name",MESSAGE_NUMBER = "number";
	public static TextView mPgsBar;

	/**
	 * 接收数据
	 */
	int callout ;
	int missed;
	int received;
	
	public Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			Log.i("cmd", "呼叫日志中 Handler 接收到消息   msg.what="+msg.what);
		   if (BtHelper.MESSAGE_ADD_CALLOUT == msg.what) {
				 String name = msg.getData().getString(MESSAGE_NAME);
				 String number = msg.getData().getString(MESSAGE_NUMBER);
				 String sortKey = getFirstChar(LocaleUtils.getIntance().getSortKey(name, FullNameStyle.CHINESE));
				 Contact contact = new Contact(name, number.trim());
				 contact.setSortKey(sortKey);
				 mCallOutList.add(contact);
				 
				 callout++;
				 AbsBtCallDetailFragment.mPgsBar.setText("呼叫: "+callout+" 个");
				 
			} else if (BtHelper.MESSAGE_ADD_MISSED == msg.what) {
				 String name = msg.getData().getString(MESSAGE_NAME);
				 String number = msg.getData().getString(MESSAGE_NUMBER);
				 Contact contact = new Contact(name, number.trim());
				 String sortKey = getFirstChar(LocaleUtils.getIntance().getSortKey(name, FullNameStyle.CHINESE));
		         contact.setSortKey(sortKey);
		         mMissedcallsList.add(contact);
		         
		         missed++;
				 AbsBtCallDetailFragment.mPgsBar.setText("未接: "+missed+" 个");
		         
			} else if (BtHelper.MESSAGE_ADD_RECEIVED == msg.what) {
				
				 String name = msg.getData().getString(MESSAGE_NAME);
				 String number = msg.getData().getString(MESSAGE_NUMBER);
				 String sortKey = getFirstChar(LocaleUtils.getIntance().getSortKey(name, FullNameStyle.CHINESE));
				 Contact contact = new Contact(name, number.trim());
				 contact.setSortKey(sortKey); 
				 mReceivedList.add(contact);
				 
				 received++;
				 AbsBtCallDetailFragment.mPgsBar.setText("已接: "+received+" 个");
			}
		}
	};
	
    /**
     * 注册一个广播接收者，接收PIM数据的初始化情况
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 IntentFilter intentFilter = new IntentFilter();
	     intentFilter.addAction(BTDefine.ACTION_BT_PIM_SYNC_FINISH);
	     getActivity().registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view=onCreateViewCallDetailLayout();
        //呼出
		mCallOutListView = (ListView) view.findViewById(R.id.listview_call_out);
		mCallOutListView.setOnItemClickListener(this);
	    mCalloutAdapter = onCreateAdapter(getActivity(), 0, mCallOutList,R.drawable.icon_bochu);
		mCallOutListView.setAdapter(mCalloutAdapter);
		//来电
		Log.i("cmd", "mReceivedList="+mReceivedList.size());
		mReceivedListView = (ListView) view.findViewById(R.id.listview_received);
		mReceivedListView.setOnItemClickListener(this);	
		mReceiverAdapter=onCreateAdapter(getActivity(), 0, mReceivedList,R.drawable.icon_laidian);
		mReceivedListView.setAdapter(mReceiverAdapter);
		//未接
		mMissedListView = (ListView) view.findViewById(R.id.listview_missed);
		mMissedListView.setOnItemClickListener(this);
		mMissedAdapter=onCreateAdapter(getActivity(), 0, mMissedcallsList,R.drawable.icon_weijie);
		mMissedListView.setAdapter(mMissedAdapter);
		//下载动画
        mIvCallDownLoad = (ImageView)view.findViewById(R.id.iv_call_loading);
        mAnimationDownLoad = AnimationUtils.loadAnimation(getActivity(),R.anim.ablum_download_contact);
        mIvCallDownLoadBg = (ImageView)view.findViewById(R.id.iv_call_loading_bg);
        
        mPgsBar = (TextView)view.findViewById(R.id.pgsBar);
        
		return view;
	}

	/**
	 * 获取焦点请求数据
	 */
	@Override
	public void onResume() {
		super.onResume();
		view.requestFocus();
		//请求记录信息
		//getCallLog();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}
	
    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
     	menu.clear();
    	callMenu = menu;
    	//菜单项
		onInflateMenuCallDetail(inflater,menu);
		MenuItem menuCalling = menu.findItem(R.id.action_calling);
		MenuItem menuCalled = menu.findItem(R.id.action_called);
		MenuItem menuMissed = menu.findItem(R.id.atcion_call_missed);
		
		if(currentMenuItemPosition==R.id.action_calling){ 
			onOptionsItemSelected(menuCalling);
			menu.getItem(0).setTitle(R.string.calling);
		}else if(currentMenuItemPosition==R.id.action_called){
			onOptionsItemSelected(menuCalled);
			menu.getItem(0).setTitle(R.string.called);
		}else if(currentMenuItemPosition==R.id.atcion_call_missed){
			onOptionsItemSelected(menuMissed);
			menu.getItem(0).setTitle(R.string.missed_call);
		}else{
			onOptionsItemSelected(menuMissed);
			menu.getItem(0).setTitle(R.string.missed_call);
		}
		 
		if (BtBroadcastReceiver.BT_CALL_DOWNLOAD_RESULT ==-1)
    	{
    		setShowHideDownAnimation(SHOW_ANIMATION);
    	}else if(BtBroadcastReceiver.BT_CALL_DOWNLOAD_RESULT ==BTDefine.VALUE_SYNC_CALL_LOG_SUCCESS
    			||BtBroadcastReceiver.BT_CALL_DOWNLOAD_RESULT ==BTDefine.VALUE_SYNC_CALL_LOG_FAILD)
    	{
    		setShowHideDownAnimation(HIDE_ANIMATION);
    	}
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id=item.getItemId();
    	if(id==R.id.action_calling){
    		currentMenuItemPosition = R.id.action_calling;
    		callMenu.getItem(0).setTitle(R.string.calling);
    		Log.i("cmd", "去电");
    		callOutClick();
    	}else if(id==R.id.action_called){
    		currentMenuItemPosition = R.id.action_called;
    		callMenu.getItem(0).setTitle(R.string.called);
    		Log.i("cmd", "来电");
    		receivedClick();
    	}else if(id==R.id.atcion_call_missed){
    		currentMenuItemPosition = R.id.atcion_call_missed;
    		callMenu.getItem(0).setTitle(R.string.missed_call);
    		Log.i("cmd", "未接");
    		missedCallClick();
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onOptionsMenuClosed(Menu menu) {
    	super.onOptionsMenuClosed(menu);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver != null){
            getActivity().unregisterReceiver(mReceiver);
        }
    }
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String num= "";
		if(currentMenuItemPosition==R.id.action_called){//来电记录
			num = mReceiverAdapter.getSelectPhoneNum(arg2);
		}else if(currentMenuItemPosition==R.id.action_calling){//去电记录
			num = mCalloutAdapter.getSelectPhoneNum(arg2);
		}else if(currentMenuItemPosition==R.id.atcion_call_missed){//未接记录
			num = mMissedAdapter.getSelectPhoneNum(arg2);
		}
	    if(!num.equals("")){
	        //通知蓝牙模块拨号
	        try {
	        	btDeviceFeature.dial_BC(num);
            } catch (Exception e) {
                e.printStackTrace();
            }
	    }
	}
	@Override
	public void completeDeviceConnectService(BTController cbtController) {
		super.completeDeviceConnectService(cbtController);
	}
	
    /**
     * 消息创建
     */
    public Message createMessage(int what, String name, String phoneNumber) {
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        Bundle data = new Bundle();
        data.putString(MESSAGE_NAME, name);
        data.putString(MESSAGE_NUMBER, phoneNumber);
        msg.setData(data);
        return msg;
    }


	/** 呼出事件 */
	private void callOutClick() {
		mCallOutListView.setVisibility(View.VISIBLE); 
		mReceivedListView.setVisibility(View.GONE); 
		mMissedListView.setVisibility(View.GONE);
	    getCallLog();
		mCalloutAdapter.notifyDataSetChanged();
	}
	
     /**未接事件*/
	private void missedCallClick() {
		mMissedListView.setVisibility(View.VISIBLE);  
		mReceivedListView.setVisibility(View.GONE); 
		mCallOutListView.setVisibility(View.GONE);
	    getCallLog();
		mMissedAdapter.notifyDataSetChanged();
	}

	/** 呼入事件 */
	private void receivedClick() {
         mReceivedListView.setVisibility(View.VISIBLE);
         mCallOutListView.setVisibility(View.GONE);
         mMissedListView.setVisibility(View.GONE);
		 getCallLog();
		 mReceiverAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 注册的广播接收者，进行数据交互
	 */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();
            if (action.equals(BTDefine.ACTION_BT_PIM_SYNC_FINISH)) {
                int errCode = intent.getIntExtra(BTDefine.EXTRA_PIM_SYNC_RESULT, -1);
               
                if (errCode == BTDefine.VALUE_SYNC_CALL_LOG_SUCCESS) {
                	BtBroadcastReceiver.BT_CALL_DOWNLOAD_RESULT = BTDefine.VALUE_SYNC_CONTACT_SUCCESS;
                	setShowHideDownAnimation(HIDE_ANIMATION);
                	
                	if(currentMenuItemPosition==R.id.action_called){//来电记录
                		 Collections.sort(mReceivedList, mListComparator);  
                		 mReceiverAdapter.notifyDataSetChanged();
                		 
                	}else if(currentMenuItemPosition==R.id.action_calling){//去电记录
                		Collections.sort(mCallOutList, mListComparator);
                		mCalloutAdapter.notifyDataSetChanged();
                		
                	}else if(currentMenuItemPosition==R.id.atcion_call_missed){//未接记录
                		Collections.sort(mMissedcallsList, mListComparator);
                		mMissedAdapter.notifyDataSetChanged();
                	}
                } else if (errCode == BTDefine.VALUE_SYNC_CALL_LOG_FAILD) {
                    BtBroadcastReceiver.BT_CALL_DOWNLOAD_RESULT = BTDefine.VALUE_SYNC_CALL_LOG_FAILD;
                	setShowHideDownAnimation(HIDE_ANIMATION);
                }
            } 
        }
    };
    
     public void clearAllListInfo()
     {
    	 mMissedcallsList.clear();
    	 mReceivedList.clear();
    	 mCallOutList.clear();
     }
	
	/**
	 * 请求电话记录信息
	 */
	private void getCallLog()
	{
		//清楚所有的数据，然后再请求
		if(null!=btDeviceFeature&&0==mCallOutList.size()&&0==mMissedcallsList.size()&&0==mReceivedList.size())
		{
			clearAllListInfo();
			try {
				btDeviceFeature.getCallLog_CB();
				setShowHideDownAnimation(SHOW_ANIMATION);
				BtBroadcastReceiver.BT_CALL_DOWNLOAD_RESULT = -1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			return;
		}
	}
	
	/**显示和隐藏下载动画*/
	private void setShowHideDownAnimation(int id)
	{
		if(HIDE_ANIMATION==id)
		{
			mIvCallDownLoad.setVisibility(View.GONE);
			mIvCallDownLoadBg.setVisibility(View.GONE);
			mPgsBar.setVisibility(View.GONE);
			mIvCallDownLoad.clearAnimation();
		}else{
			mIvCallDownLoad.setVisibility(View.VISIBLE);
			mIvCallDownLoadBg.setVisibility(View.VISIBLE);
			mPgsBar.setVisibility(View.VISIBLE);
			mIvCallDownLoad.startAnimation(mAnimationDownLoad);
		}
	}
	
	/**清除数据*/
    public static void clearData() {
    	mCallOutList.clear();
    	mMissedcallsList.clear();
    	mReceivedList.clear();
    	if (mCalloutAdapter != null)
    		mCalloutAdapter.notifyDataSetChanged();
    	if (mReceiverAdapter != null)
    		mReceiverAdapter.notifyDataSetChanged();
    	if (mMissedAdapter != null)
    		mMissedAdapter.notifyDataSetChanged();
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
    
	
	private Comparator mListComparator = new Comparator<Contact>() {
        @Override
        public int compare(Contact contact1, Contact contact2) {
            final char sortKey1 = contact1.getSortKey().charAt(0);
            final char sortKey2 = contact2.getSortKey().charAt(0);
            if (sortKey1 > sortKey2) {
                return 1;
            } else if (sortKey1 == sortKey2) {
                return 0;
            } else {
                return -1;
            }
        }
    };
	
	protected abstract View onCreateViewCallDetailLayout();
	protected abstract void onInflateMenuCallDetail(MenuInflater inflater,Menu menu);
	protected abstract AbsCallDetailAdapter onCreateAdapter(Context context, int textViewResourceId, List<Contact> items,int callTypeIcon);
}
