package com.ls.bt.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.android.utils.log.JLog;
import com.bt.BTDefine;
import com.ls.bt.utils.Utils;

public class BTReceiver extends BroadcastReceiver{
	
	JLog LOG = new JLog("BTReceiver",BTReceiver.DEBUG,JLog.TYPE_DEBUG);
	public static final boolean DEBUG = false;
	
    private static final String TAG = "BTReceiver";
    private static final String INCOMING_CALL = BTDefine.ACTION_BT_INCOMING_CALL;
    private static final String OUTGOING_CALL_NUMBER = BTDefine.ACTION_BT_OUTGOING_NUMBER;
    private static final String CANCEL_CALL = BTDefine.ACTION_BT_END_CALL;
    private static final String BEGIN_CALL_ONLINE = BTDefine.ACTION_BT_BEGIN_CALL_ONLINE;
    private static final String INCOMMING_CALL_NUMBER = BTDefine.ACTION_BT_INCOMING_NUMBER;
    private static final String OUTGOING_CALL = BTDefine.ACTION_BT_OUTGOING_CALL;
    public static String BT_PHONE_STATUS = CANCEL_CALL;//防止突然挂掉多次弹出Incoming
    
    public static final String KEY_IS_APP_BE_STARTED_BY_OTHER_APP = "is_app_be_started_by_other_app";
    public static final String PHONE_ACTIVITY_NAME = "com.ls.android.phone.PhoneActivity";
    private String KEY_PIN_CODE = "key_pin_code";
    public static final String KEY_PHONE_OUTGOING = "key_phone_outgoing";
    public static final String KEY_OUT_GOING_CALL_NUMBER = "key_out_going_call_number";
    
    public final static String BT_ENABLE = "bt_enable";
    public final static String BT_DISABLE = "bt_disable";
    
    private Handler mHandler = new Handler();
    
    private String mOutGoingCallNumber;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LOG.print("action:"+ action);
        
        if (INCOMING_CALL.equals(action) /*|| OUTGOINGCALL.equals(action)*/) 
        {
            Log.d("BTReceiver","INCOMINGCALL");
            BT_PHONE_STATUS = INCOMING_CALL;
            
            //TODO 切换到来电界面------------------------------------------
//            Intent i = new Intent(context,InComing.class);   
//            Log.d(TAG,"top activity:" + Utils.getTopActivity(context));
//            
//            if(!PHONE_ACTIVITY_NAME.equals(Utils.getTopActivity(context))){
//                i.putExtra(KEY_IS_APP_BE_STARTED_BY_OTHER_APP, true);
//            }else{
//                i.putExtra(KEY_IS_APP_BE_STARTED_BY_OTHER_APP, false);
//            }
//            
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
//            context.startActivity(i);  
            //来电时也要切换声音通道
            Utils.switchBtSoundChannel(context,BT_ENABLE);
        }else if(CANCEL_CALL.equals(action)){
            BT_PHONE_STATUS = CANCEL_CALL;
            LOG.print("bt_disable");
            Utils.switchBtSoundChannel(context,BT_DISABLE);
        }
        else if(BEGIN_CALL_ONLINE.equals(action)){
            LOG.print("RECEIVEDCALLv");
            Utils.switchBtSoundChannel(context,BT_ENABLE);

        }else if(OUTGOING_CALL_NUMBER.equals(action)){
//            PhoneActivity.isRealCall = true;
//            Utils.switchBtSoundChannel(context,BT_ENABLE);
//            String phoneNumber = intent.getStringExtra(BTDefine.EXTRA_PHONE_NUMBER);
//            Log.d(TAG,"PHONE_INFO_RECEIVED mOutGoingCallNumber: " + phoneNumber);
//            if(phoneNumber != null){
//                mOutGoingCallNumber = phoneNumber;
//                ((BTApplication)context.getApplicationContext()).setOutGoingNumber(mOutGoingCallNumber);
//            }
                
        }else if(INCOMMING_CALL_NUMBER.equals(action)){
//            PhoneActivity.isRealCall = true;
//            String phoneNumber = intent.getStringExtra(BTDefine.EXTRA_PHONE_NUMBER);
//            Log.d(TAG,"PHONE_NUMBER_RECEIVED mOutGoingCallNumber: " + phoneNumber);
//            if(phoneNumber != null){
//                mOutGoingCallNumber = phoneNumber;
//            }
        }else if(OUTGOING_CALL.equals(action))
        {
          //电话打出去 跳到拨号界面
//            if(!PHONE_ACTIVITY_NAME.equals(Utils.getTopActivity(context))){
//                Intent i = new Intent();  
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );  
//                i.putExtra(KEY_PHONE_OUTGOING, true);
//                i.putExtra(BTReceiver.KEY_OUT_GOING_CALL_NUMBER, mOutGoingCallNumber);
//                i.putExtra(KEY_IS_APP_BE_STARTED_BY_OTHER_APP, true);
//                i.setComponent(new ComponentName("com.ls.android.phone", "com.ls.android.phone.PhoneActivity"));  
//                context.startActivity(i);  
//            }            
        }
    }
    
    class ConnectDeviceThread extends Thread{
        private String mAddress;
        private Context mContext;
        
        public ConnectDeviceThread(Context context,String address){
            mAddress = address;
            mContext = context;
        }
        
        public void run(){
            if(mAddress != null && !mAddress.equals("")){
                try{
                	 LOG.print("connecting device:" + mAddress);
                	 //IVTBluetoothSdk.getInstance(mContext).connect(mAddress);
                	 //BTController.getInstance(mContext).getFeature().connectDevice_BC(mAddress);
                }catch(Exception e){
                    LOG.print(e.toString());
                }
                
            }
        }
    }
    
    class PairRunnable implements Runnable {
        private String address;
        private String pincode;
        private Context mContext;
        
        public PairRunnable(Context context,String address, String pincode) {
            this.address = address;
            this.pincode = pincode;
            mContext = context;
        }

        public void run() {
            if (null != pincode && !pincode.equals("")) {
                try{
                    LOG.print("setPinCode start");
					//if(IVTBluetoothSdk.getInstance(mContext).setPinCode(address, pincode.getBytes())){
					//	Log.d(TAG,"setPinCode " + pincode + " sucess");
					//}
                }catch(Exception e){
                    e.printStackTrace();
                    LOG.print(e.toString());
                }
            }
        }
    }
}
