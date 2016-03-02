package com.ls.bt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.android.utils.string.LocaleUtils;
import com.android.utils.utils.AbsServiceControllerHelper.OnServiceConnectSuccessListener;
import com.bt.BTController;
import com.bt.BTFeature;
import com.bt.BTPIMCallback;
import com.ls.bt.call.detail.AbsBtCallDetailFragment;
import com.ls.bt.call.detail.AbsCallDetailAdapter;
import com.ls.bt.contact.AbsBtContactNumFragment;
import com.ls.bt.contact.AbsContactAdapter;
import com.ls.bt.contact.Contact;
import com.ls.bt.inout.AbsBtDialNumFragment;
import com.ls.bt.utils.BtHelper;
import com.ls.bt.utils.Utils;
import com.ls.bt.utils.BtHelper.CallLog;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract.FullNameStyle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class AbsBTContactService extends Service {

	public static List<Contact> mContactList=new ArrayList<Contact>();
	private ArrayList<String> contactList;
	/**APP和蓝牙设备通讯对象*/
    protected BTController btDeviceController;
    protected BTFeature btDeviceFeature;
    
	/** 拨号 */
	AbsBtDialNumFragment btDialNumFragment;
	/** 联系人 */
	AbsBtContactNumFragment btContactFragment;
	/** 拨打明细 */
	AbsBtCallDetailFragment btCallDetailFragment;
	
	private static final String PHONE_CONTACT = "cn.yunzhisheng.intent.action.custom.order.contact";
	private final String ACTION_BT_PHONEBOOK_SYNC = "bt.action.sync.order.contact";
	
	//语音控制蓝牙广播
  	private final String makeCall = "com.bt.action_bt.makeCall";
  	private final String hangupCall = "com.bt.action_bt.hangupCall";
	
  	public final static String MESSAGE_NAME = "name",MESSAGE_NUMBER = "number";
  	
  	private ArrayList<String> callLog;
  	private final static int PHONE_COUNT = 3;
  	 public final static String CURRENT_COUNT = "count";
  	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("server", "service onCreate");
		IntentFilter filter=new IntentFilter();
		filter.addAction(PHONE_CONTACT);
		filter.addAction(makeCall);
		filter.addAction(hangupCall);
		filter.addAction(ACTION_BT_PHONEBOOK_SYNC);
		registerReceiver(contactReceiver, filter);
		
		btCallDetailFragment = new AbsBtCallDetailFragment() {
			@Override
			protected void onInflateMenuCallDetail(MenuInflater inflater, Menu menu) {
			}
			@Override
			protected View onCreateViewCallDetailLayout() {
				return null;
			}
			@Override
			protected AbsCallDetailAdapter onCreateAdapter(Context context,
					int textViewResourceId, List<Contact> items, int callTypeIcon) {
				return null;
			}
		};
		
		
		btContactFragment = new AbsBtContactNumFragment() {
			
			@Override
			protected void onInflateMenuContact(MenuInflater inflater, Menu menu) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected View onCreateViewBtContactLayout() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected AbsContactAdapter onCreateAdapter(Context context,
					int textViewResourceId, List<Contact> contactLists) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		
		btDeviceController = BTController.getInstance(getApplicationContext());
		btDeviceController.connectService(mConnectDeviceListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		btDeviceController.removePimDataCallback(mBTPIMCallback);
	}
	
    private OnServiceConnectSuccessListener mConnectDeviceListener = new OnServiceConnectSuccessListener() {

        @Override
        public void onServiceConnectSuccess() {
        	Log.i("book","服务绑定成功");
        	btDeviceFeature = btDeviceController.getFeature();
        	btDeviceController.addPimDataCallback(mBTPIMCallback);
        }
    };
	
	private BroadcastReceiver contactReceiver=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(PHONE_CONTACT.equals(action)){
				
				 /**
				  * 接收从PhoneBookManager发送过来的广播数据
				  */
				contactList = intent.getStringArrayListExtra("contactList");
				Log.i("book", "AbsBTContactService 接收到数据 ");
				if(contactList!=null&&contactList.size()>0){
					for (String str : contactList) {
						addContact(str);
					}
				}
				
	        }else if(makeCall.equals(action)){
            	String number=intent.getStringExtra("number");
            	if(number!=null||!"".equals(number)){
            		try {
						btDeviceFeature.dial_BC(number);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
            	}
            }else if(hangupCall.equals(action)){
            	try {
					btDeviceFeature.cancelCall();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }else if(ACTION_BT_PHONEBOOK_SYNC.equals(action)){
            	new Handler().postDelayed(new Runnable() {
					public void run() {
						try {
							Log.i("book","执行结果"+ btDeviceFeature.getContact_CB());
							
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}, 2000);
            }
		}
	};
	
	private void addContact(String str) {
		String[] strs=str.split("_");
		String name=strs[0];
		String number=strs[1];
		String sortKey = getFirstChar(LocaleUtils.getIntance().getSortKey(name, FullNameStyle.CHINESE));
		String[] nameInitialSet = Utils.getInitial(name);
		Contact contact = new Contact(name, number);
		contact.setmInitialArray(nameInitialSet);
        contact.setSortKey(sortKey);
		mContactList.add(contact);
	}
	
	/** 获取呼叫日志信息 */
	private BTPIMCallback.Stub mBTPIMCallback = new BTPIMCallback.Stub() {
		@Override
		public void onGetSMS(String number, String text, int time)
				throws RemoteException {
		}
		@Override
		public void onGetContact(String name, String number)
				throws RemoteException {
			Log.i("count", "AbsBTContactService发送通讯录       name="+name+"  number="+number);
			btContactFragment.mHandler.sendMessage(btContactFragment.createMessage(BtHelper.MESSAGE_ADD_CONTACT, name, number));
		}
		@Override
		public void onGetCallLog(int type, String name, String number,String time) throws RemoteException {
			if (type == BtHelper.CallLog.TYPE_OUTGOING_CALL) {  
				
				Log.i("call", "AbsBTContactService发送已拨电话消息       name="+name+"  number="+number);
				btCallDetailFragment.mHandler.sendMessage(btCallDetailFragment.createMessage(BtHelper.MESSAGE_ADD_CALLOUT, name,
						number));
			} else if (type == CallLog.TYPE_MISSING_CALL) { 
				Log.i("cmd", "AbsBTContactService发送未接电话消息       name="+name+"  number="+number);
				btCallDetailFragment.mHandler.sendMessage(btCallDetailFragment.createMessage(BtHelper.MESSAGE_ADD_MISSED, name,
						number));
				
			} else if (type == CallLog.TYPE_INCOMING_CALL) { //
				Log.i("cmd", "AbsBTContactService发送已接电话消息       name="+name+"  number="+number);
			    btCallDetailFragment.mHandler.sendMessage(btCallDetailFragment.createMessage(BtHelper.MESSAGE_ADD_RECEIVED, name,
						number));
			}
			
		}
	};
	
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
}
