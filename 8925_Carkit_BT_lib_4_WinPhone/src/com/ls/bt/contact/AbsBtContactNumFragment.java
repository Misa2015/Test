package com.ls.bt.contact;

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
import android.provider.ContactsContract.FullNameStyle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.utils.log.JLog;
import com.android.utils.string.LocaleUtils;
import com.bt.BTController;
import com.bt.BTDefine;
import com.ls.android.bt.AbsBTMainActivity;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.bt.myview.MyProgress;
import com.ls.bt.service.AbsBTContactService;
import com.ls.bt.utils.BtHelper;
import com.ls.bt.utils.Utils;
import com.ls.android.phone.R;
/***
 * 蓝牙联系人界面
 */
abstract public class AbsBtContactNumFragment extends AbsBaseFragment implements OnItemClickListener,TextWatcher
{
	 private static final JLog LOG = new JLog("AbsBtContactNumFragment", AbsBtContactNumFragment.DEBUG, JLog.TYPE_DEBUG);
     
	 public static final boolean DEBUG = true;
     private final static int HIDE_ANIMATION = -1;
     private final static int SHOW_ANIMATION = 0;
     public final static int MESSAGE_ADD_CONTACT = 1;
     private final static int MESSAGE_SEARCH_CONTACT = 2;
     public final static String MESSAGE_NAME = "name", MESSAGE_NUMBER = "number";
     protected static final String TAG = "BtContactNumFragment";
	 private ListView mContactsListView;
	 public final static int PHONE_COUNT = 3;
	 
	 
	 /**开始下载显示图片*/
	 private ImageView mIvDownLoad;
	 /**开始下载背景*/
	 private ImageView mIvDownLoadBg;
	 /**开始下载动画*/
	 private Animation mAnimationDownLoad ;
	 
	 public static  TextView mPgsBar;
	 
	 private static final int MSG_PROGRESS_UPDATE = 0x110;
	 
	 public static EditText editTextSearch;
	 private String rightString = "A"+"\n"+"B"+"\n"+"C"+"\n"+"D"+"\n"+"E"+"\n"+"F"+"\n"+"G"+"\n"+"H"+"\n"+"I"+"\n"
	 +"J"+"\n"+"K"+"\n"+"L"+"\n"+"M"+"\n"+"N"+"\n"+"O"+"\n"+"P"+"\n"+"Q"+"\n"+"R"+"\n"+"S"+"\n"+"T"+"\n"
	 +"U"+"\n"+"V"+"\n"+"+W"+"\n"+"X"+"\n"+"Y"+"\n"+"Z";
	 private static AbsContactAdapter mContactAdapter = null;
	 public static List<Contact> mContactsList = new ArrayList<Contact>();
	 public static List<Contact> mSearchContactsList = new ArrayList<Contact>();
	 public final static String CURRENT_COUNT = "count";
	 
	 int current;
	 public Handler mHandler = new Handler(){
	        @SuppressWarnings("unchecked")
	        public void handleMessage(Message msg) {
				//添加联系人
	            if (BtHelper.MESSAGE_ADD_CONTACT == msg.what) {
	            	/**
	            	 * 从AbsBTContactService中传递过来的单个联系人信息
	            	 */
	                String name = msg.getData().getString(MESSAGE_NAME);
	                String sortKey = getFirstChar(LocaleUtils.getIntance().getSortKey(name, FullNameStyle.CHINESE));
	                String[] nameInitialSet = Utils.getInitial(name);
	                Contact contact = new Contact(name, msg.getData().getString(MESSAGE_NUMBER));
	                contact.setmInitialArray(nameInitialSet);
	                contact.setSortKey(sortKey);
	                mContactsList.add(contact);
	                current++;
	                try {
						mPgsBar.setText("已下载: "+current+" 个");
					} catch (Exception e) {
						e.printStackTrace();
					}
	            //查找联系人    
	            }else if(MESSAGE_SEARCH_CONTACT == msg.what)
	            {
	            	String[] searchKey  = msg.getData().getStringArray("searchKey");
	            	int searchKeyLen  = 0;
	            	if(null!=searchKey)
	            	{		
	            		searchKeyLen = searchKey.length;
	            	}
	        		if (searchKey == null || searchKeyLen == 0){
	        			mContactAdapter =onCreateAdapter(getActivity(), 0, mContactsList);
	        			mContactsListView.setAdapter(mContactAdapter);
	        			mContactAdapter.notifyDataSetChanged();
	        			return;
	        		}
	        		mSearchContactsList.clear();
	        		for (int i = 0; i < mContactsList.size(); i++) {
	        			int nameInitialLen = mContactsList.get(i).getmInitialArray().length;
	        			for (int j = 0; j < nameInitialLen; j++) {
	        				for (int j2 = 0; j2 < searchKeyLen; j2++) {
			        			LOG.print("name="+mContactsList.get(i).getName()+"nameInitialArray="+mContactsList.get(i).getmInitialArray()[j]+searchKey[j2]);
	        					if (mContactsList.get(i).getmInitialArray()[j].indexOf(searchKey[j2])!=-1) {//任意匹配 
	        						mSearchContactsList.add(mContactsList.get(i));
	        						break;
	        					} 
							}
						}
					}
	        		if (mContactsList.size() == 0)return;
	        		Collections.sort(mContactsList, mListComparator);
	        		mContactAdapter = onCreateAdapter(getActivity(), 0, mSearchContactsList);
        			mContactsListView.setAdapter(mContactAdapter);
        			mContactAdapter.notifyDataSetChanged();
	            }else if (5 == msg.what) {
	            	getContactNum();
	            }else if(msg.what == MSG_PROGRESS_UPDATE){
	                mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);  
	            }
	        }
	    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTDefine.ACTION_BT_PIM_SYNC_FINISH);
        intentFilter.addAction(CURRENT_COUNT);
        getActivity().registerReceiver(mReceiver, intentFilter);
        //AbsBTContactService.mContactList = mContactsList;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	view=onCreateViewBtContactLayout();
    	mContactsListView=(ListView)view.findViewById(R.id.bt_contact_listview);
    	mContactsListView.setOnItemClickListener(this);
    	mContactAdapter = onCreateAdapter(getActivity(), 0, mContactsList); 
    	mContactsListView.setAdapter(mContactAdapter);
        mContactAdapter.notifyDataSetChanged();
        
        mIvDownLoad = (ImageView)view.findViewById(R.id.imageview_loading);
        mAnimationDownLoad = AnimationUtils.loadAnimation(getActivity(),R.anim.ablum_download_contact);
        mIvDownLoadBg = (ImageView)view.findViewById(R.id.imageView_loading_bg);
        
       //下载进度条
        mPgsBar = (TextView)view.findViewById(R.id.pgsBar);

        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        view.requestFocus();
        //发送请求联系人数据的消息
        mHandler.sendEmptyMessageDelayed(5, 500);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	    String num=mContactAdapter.getSelectPhoneNum(arg2);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /**显示和隐藏下载动画*/
    	if(0 == mContactsList.size())
    	{
        	getContactNum();
    	}else if (BtBroadcastReceiver.BT_CONTACT_DOWNLOAD_RESULT ==-1)
    	{
    		//显示下载动画
    		setShowHideDownAnimation(SHOW_ANIMATION);
    		
    	}else if(BtBroadcastReceiver.BT_CONTACT_DOWNLOAD_RESULT ==BTDefine.VALUE_SYNC_CONTACT_SUCCESS
    			||BtBroadcastReceiver.BT_CONTACT_DOWNLOAD_RESULT ==BTDefine.VALUE_SYNC_CONTACT_FAILD)
    	{
    		setShowHideDownAnimation(HIDE_ANIMATION);
    	}
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    	onInflateMenuContact(inflater,menu);
		editTextSearch = (EditText)menu.findItem(R.id.menu_search).getActionView().findViewById(R.id.et_search);
		editTextSearch.addTextChangedListener(this);
		mHandler.sendEmptyMessage(MESSAGE_SEARCH_CONTACT);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
    	}
		return false;
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

    public Message createMessage(int what,String name,String phoneNumber){
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        Bundle data = new Bundle();
        data.putString(MESSAGE_NAME, name);
        data.putString(MESSAGE_NUMBER, phoneNumber);
        msg.setData(data);
        return msg;
    }
    
    static String getCallingName(String number) {
        if (mContactsList.size() > 0) {
            for (Contact contact : mContactsList) {
                if (contact.getNumber().equalsIgnoreCase(number))
                    return contact.getName();
            }
        }
        return number;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver != null){
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BTDefine.ACTION_BT_PIM_SYNC_FINISH)) {
                int errCode = intent.getIntExtra(BTDefine.EXTRA_PIM_SYNC_RESULT, -1);
                
                if (errCode == BTDefine.VALUE_SYNC_CONTACT_SUCCESS) {
                    Collections.sort(mContactsList, mListComparator);
                    mContactAdapter.notifyDataSetChanged();
                    BtBroadcastReceiver.BT_CONTACT_DOWNLOAD_RESULT = BTDefine.VALUE_SYNC_CONTACT_SUCCESS;
                    setShowHideDownAnimation(HIDE_ANIMATION);
                    current = 0;
                } else if (errCode == BTDefine.VALUE_SYNC_CONTACT_FAILD) {
                    Collections.sort(mContactsList, mListComparator);
                    mContactAdapter.notifyDataSetChanged();
                    BtBroadcastReceiver.BT_CONTACT_DOWNLOAD_RESULT = BTDefine.VALUE_SYNC_CONTACT_FAILD;
                    setShowHideDownAnimation(HIDE_ANIMATION);
                }
            }
        }

    };
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}
	@Override
	public void onTextChanged(CharSequence value, int arg1, int arg2, int arg3) {
		Message msg = new Message();
		msg.what = MESSAGE_SEARCH_CONTACT;
		Bundle data = new Bundle();
		String[] nameInitialSet = Utils.getInitial(value.toString());
		data.putStringArray("searchKey", nameInitialSet);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
	@Override
	public void completeDeviceConnectService(BTController cbtController) {
		super.completeDeviceConnectService(cbtController);
	}
	/**
	 * 得到联系人号码
	 */
	private void getContactNum()
	{
		if(mContactsList.size() < 1 && null!=btDeviceControler)
    	{
			mContactsList.clear();
			if (mContactAdapter != null)
	    		mContactAdapter.notifyDataSetChanged();
           try {
        	    Log.i("book", "Fragment中请求电话本   getContact_CB");
        	    btDeviceFeature.getContact_CB();
        	    setShowHideDownAnimation(SHOW_ANIMATION);
        	    BtBroadcastReceiver.BT_CONTACT_DOWNLOAD_RESULT = -1;
            } catch (Exception e) {
                e.printStackTrace();
            }
    	}
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
	/**显示和隐藏下载动画*/
	private void setShowHideDownAnimation(int id)
	{
		if(HIDE_ANIMATION==id)
		{
			mIvDownLoad.setVisibility(View.GONE);
			mIvDownLoadBg.setVisibility(View.GONE);
			mPgsBar.setVisibility(View.GONE);
		    mIvDownLoad.clearAnimation();
		}else{
			mIvDownLoad.setVisibility(View.VISIBLE);
			mIvDownLoadBg.setVisibility(View.VISIBLE);
			mPgsBar.setVisibility(View.VISIBLE);
		    mIvDownLoad.startAnimation(mAnimationDownLoad);		    
		}
	}
	
	/**清除数据*/
    public static void clearData() {
    	Log.i("clear", "AbsBtContactNumFragment clearData()");
    	mContactsList.clear();
    	if (mContactAdapter != null)
    		mContactAdapter.notifyDataSetChanged();
    }
    
	protected abstract View onCreateViewBtContactLayout();
	protected abstract void onInflateMenuContact(MenuInflater inflater,Menu menu);
	protected abstract AbsContactAdapter onCreateAdapter(Context context, int textViewResourceId, List<Contact> contactLists);
}
