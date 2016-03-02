package com.ls.android.bt;

import com.android.utils.log.JLog;
import com.android.utils.utils.AbsServiceControllerHelper.OnServiceConnectSuccessListener;
import com.bt.BTConstant;
import com.bt.BTController;
import com.bt.BTDefine;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class BTApplication extends Application {
    private static final JLog LOG = new JLog("BTApplication", false, JLog.TYPE_DEBUG);
	//	private static final boolean D = true;
	//	private static final String TAG = "BTApplication";
	public static final int PIM_REQUEST_TYPE_NULL = 0x01;
	public static final int PIM_REQUEST_TYPE_CONTACT = 0x02;
	public static final int PIM_REQUEST_TYPE_CALL_LOG = 0x03;
	
	Bundle mCalllogBundle = null ;
	Bundle mContactBundle = null ;
	Bundle mTelBundle = null ;
	boolean mPhoneBookDail = false;
	String phoneBookDailNumber = null; 
	boolean mCalllogDail =false;
	String CalllogDailNumber = null;
	boolean backgroundStatus = true;
	private boolean mSubToMain = false;
	
	public static BTApplication mInstance;
	private final int A2DP_SERVICE = BTConstant.DeviceService.AUDIO_SINK;
	private final int AVRCP_SERVICE = BTConstant.DeviceService.AVRCP_CT;
	private final int CONNECT = BTDefine.VALUE_CONNECTION_EVENT_CONNECT;
	private final int DISCONNECT = BTDefine.VALUE_CONNECTION_EVENT_DISCONNECT;
	private String mAddress = "";
	
	boolean mlock = false;
	
	private String mCallNumber;
	

	BroadcastReceiver mReceiver = new BroadcastReceiver() 
	{
		public void onReceive(Context context, Intent intent) 
		{
			LOG.print("Application Receive broadCast");
			String action = intent.getAction();
			if (BTDefine.ACTION_BT_STATE_CHANGE.equals(action)) {
				int state = intent.getIntExtra(BTDefine.EXTRA_BT_STATE, 0);
				if (state == BTDefine.VALUE_OPEN) {
					LOG.print("Bluetooth state changed: ON");
				} else {
				    LOG.print("Bluetooth state changed: OFF");
				}
			} else if (BTDefine.ACTION_BT_DEVICE_FOUND.equals(action)) {
				String address = intent.getStringExtra(BTDefine.EXTRA_BT_DEVICE_ADDRESS);
				String name = intent.getStringExtra(BTDefine.EXTRA_BT_DEVICE_NAME);
				int cls = intent.getIntExtra(BTDefine.EXTRA_BT_DEVICE_CLASS, 0);
				boolean paired = intent.getBooleanExtra(BTDefine.EXTRA_BT_DEVICE_PAIRED, false);
				LOG.print("new device found:" + name + "[" + address + "], device class:" + cls + ",isPaired:" 
						+ paired);
				
			} else if (BTDefine.ACTION_BT_DISCOVER_FINISH.equals(action)) {
			    LOG.print("searching devices finished.");
			} 
//			else if (IVTBluetoothDef.BRDSDK_PAIR_STATE_CHANGED.equals(action)) {
//				String address = intent.getStringExtra(IVTBluetoothDef.BRDSDK_BLUETOOTH_DEVICE_ADDRESS);
//				boolean paired = intent.getBooleanExtra(IVTBluetoothDef.BRDSDK_BLUETOOTH_PAIR_STATE, false);
//				log("device:[" + address + "] pair state changed:" + paired);
//			} 
			else if (BTDefine.ACTION_BT_CONNECTION_CHANGE.equals(action)) {
				int ev = intent.getIntExtra(BTDefine.EXTRA_BT_CONNECTION_EVENT, 0);
				LOG.print(" CON" + CONNECT+" DISCONNECT is"+ DISCONNECT + "status is "+ ev);
		        mAddress = intent.getStringExtra(BTDefine.EXTRA_BT_DEVICE_ADDRESS);
	            LOG.print(mAddress);

			} 
	
		}
	};
	public void onCreate()
	{ 
		super.onCreate();
		mCalllogBundle = new Bundle();
		mContactBundle = new Bundle();
		mTelBundle = new Bundle();
		mInstance = this;
/*		IntentFilter filter = new IntentFilter();
		filter.addAction(BTDefine.ACTION_BT_STATE_CHANGE);
		filter.addAction(BTDefine.ACTION_BT_DEVICE_FOUND);
		filter.addAction(BTDefine.ACTION_BT_DISCOVER_FINISH);
//		filter.addAction(IVTBluetoothDef.BRDSDK_PAIR_STATE_CHANGED);
		filter.addAction(BTDefine.ACTION_BT_CONNECTION_CHANGE);
		Log.v("BTApplication","Register Receive Phone");
		registerReceiver(mReceiver, filter);*/
	}
	
	
	
	/* (non-Javadoc)
     * @see android.app.Application#onTerminate()
     */
    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        BTController.getInstance(getApplicationContext()).disconnectService();
    }



    public void setCallNumber(String number){
    	mCallNumber = number;
	}
	
	public String getCallNumber(){
	    return mCallNumber;
	}
	
	public String getAddress(){
	    return mAddress;
	}
	
	public static BTApplication getInstance()
	{
		return mInstance;
	}
	
	public void saveTelInformation(Bundle telBundle)
	{
		mTelBundle = telBundle ;
	}
	
	
	public void saveContactInformation(Bundle contactBundle)
	{
		mContactBundle = contactBundle ;
	}
	
	public void saveCallLogInformation(Bundle calllog)
	{
		mCalllogBundle = calllog ;
	}
	
	public Bundle getTelInformation()
	{
		return mTelBundle ;
	}
	
	public Bundle getContactInformation()
	{
		return mContactBundle ;
	}
	
	public Bundle getCalllogInformation()
	{
		return mCalllogBundle;
	}

	public boolean isPhoneBookDail()
	{
		return mPhoneBookDail;
	}
	
	public void setPhoneBookDail(boolean value)
	{
		Log.v("BTApplication", "setPhoneBookDial(" + value + ")");
		mPhoneBookDail = value;
	}
	
	public void setPhoneBookDailNumber(String value)
	{
		phoneBookDailNumber = value;
	}
	
	public String getPhoneBookDailNumber()
	{
		return phoneBookDailNumber;
	}
	
	
	public boolean isCalllogDail()
	{
		return mCalllogDail;
	}
	
	public void setCalllogDail(boolean value)
	{
		mCalllogDail = value;
	}
	
	public void setCalllogDailNumber(String value)
	{
		CalllogDailNumber = value;
	}
	
	public String getCalllogDailNumber()
	{
		return CalllogDailNumber;
	}
	
	public void setSystemBackgroundOn()
	{
		backgroundStatus = true;
	}
	
	public void setSystemBackgroundOff()
	{
		backgroundStatus = false;
	}
	
	public void setSubToMain(boolean value)
	{
		mSubToMain = value;
	}
	
	public boolean getSubToMain()
	{
		return mSubToMain;
	}
	
	public void setPimLock(boolean value)
	{
		mlock = value;
	}
	
	public boolean getPimLock()
	{
		return mlock;
	}
}
