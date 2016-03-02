package com.ls.android.bt;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;

import com.android.utils.log.JLog;
import com.bt.BTController;
import com.bt.BTDefine;
import com.bt.BTFeature;
import com.ls.bt.call.detail.AbsBtCallDetailFragment;
import com.ls.bt.contact.AbsBtContactNumFragment;
import com.ls.bt.inout.AbsBtCallOnLingFragment;
import com.ls.bt.service.AbsBTContactService;
import com.ls.bt.utils.BtHelper;
import com.ls.bt.utils.Utils;

/***
 * 蓝牙广播接收
 */
public class BtBroadcastReceiver extends BroadcastReceiver
{
    private static final JLog LOG = new JLog("BtBroadcastReceiver", BtBroadcastReceiver.DEBUG, JLog.TYPE_DEBUG);
    public static final boolean DEBUG = false;
    public static final byte BT_STATUS_IDEL = 0;
    public static final byte BT_STATUS_CONNECTING = 2;
    public static final byte BT_STATUS_CALL_IN = 3;
    public static final byte BT_STATUS_CALL_OUT = 4;
    public static final byte BT_STATUS_ONLINE = 5;
    private static final int END_CALL = 8;
    private static final String CANCEL_CALL = BTDefine.ACTION_BT_END_CALL;
    public static String BT_PHONE_STATUS = BTDefine.ACTION_BT_HFP_RELEASE;

    /** 下载呼叫日志标志位 */
    public static int BT_CALL_DOWNLOAD_RESULT = -1;

    /** 下载电话本标志位 */
    public static int BT_CONTACT_DOWNLOAD_RESULT = -1;
    public final static String BT_ENABLE = "bt_enable";
    public final static String BT_DISABLE = "bt_disable";
    public static String mOutGoingCallNumber;
    public static String mInCommingCallNumber;
    static final int MISSED_CALL_NOTIFICATION = 1;
    //public static String mbtCurrentDeviceName = "";
    Context mcontext;
    public static String mTopAppClassName = "";
    private byte btWorkStatus = 0;
//    private static String PHONE_CONTACT = "cn.yunzhisheng.intent.action.custom.order.contact";
	private static Intent service;

	private boolean BT_VOLUME_SET = true;
	
    @Override
    public void onReceive(Context context, Intent intent)
    {
        mcontext = context.getApplicationContext();
        String action = intent.getAction();

        service = new Intent("com.bt.BTContactService");
		context.startService(service);
        if (BTDefine.ACTION_BT_CONNECTION_CHANGE.equals(action))
        {
            mTopAppClassName = getTopClassName(context);
            byte status = (byte) intent.getIntExtra(BTDefine.EXTRA_BT_CONNECTION_EVENT, 0);
            if (BTDefine.VALUE_CONNECTION_EVENT_CONNECT == status)
            {
            	/**
            	 * 获取正在通话中设置的蓝牙音量值，重启蓝牙之后重新恢复之前设置的音量
            	 */
            	Log.i("bt", "BT_VOLUME_SET="+BT_VOLUME_SET);
            	SharedPreferences preferences = context.getSharedPreferences("setVolume", 0);
                int volume = preferences.getInt("volume", 19);
                BTController btController = BTController.getInstance(context);
                BTFeature btFeature = btController.getFeature();
                if(btFeature != null){
                	try {
        				btFeature.setBtMusicVolume(volume);
        			} catch (RemoteException e) {
        				e.printStackTrace();
        			}
                }
            	
                BT_PHONE_STATUS = BTDefine.ACTION_BT_HFP_ESTABLISHED;
                if (BtHelper.APP_CLASS_NAME.equals(mTopAppClassName))
                {
                    BtHelper.startMaintActivity(context, BT_PHONE_STATUS);
                }
                service = new Intent("com.bt.BTContactService");
        		context.startService(service);
            }
            else if (BTDefine.VALUE_CONNECTION_EVENT_DISCONNECT == status)
            {
                clearBtPhoneData();
                
                BT_PHONE_STATUS = BTDefine.ACTION_BT_HFP_RELEASE;
                BT_CALL_DOWNLOAD_RESULT = -1;
                BT_CONTACT_DOWNLOAD_RESULT = -1;
                
                if (BtHelper.APP_CLASS_NAME.equals(mTopAppClassName))
                {
                    //BtHelper.startMaintActivity(context, BT_PHONE_STATUS);
                }
                Log.i("wjz", "DISCONNECT_service="+service);
                if(service!=null){
                	context.stopService(service);
                	service=null;
                }
            }
            status = (1 == status ? BT_STATUS_CONNECTING : status);
            /**取消状态栏上提示信息*/
            NotificationManager mNotificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(MISSED_CALL_NOTIFICATION);
        }
        else if (BTDefine.ACTION_BT_INCOMING_CALL.equals(action))	//来电
        {
         
            if (BT_PHONE_STATUS == BTDefine.ACTION_BT_INCOMING_CALL) 
            	return;
            mTopAppClassName = getTopClassName(context);
            BT_PHONE_STATUS = BTDefine.ACTION_BT_INCOMING_CALL;
            //Utils.switchBtSoundChannel(context, BT_ENABLE);
        }
        else if (BTDefine.ACTION_BT_OUTGOING_CALL.equals(action))	//去电
        {
        	Log.i("call", "去电");  
            if (BT_PHONE_STATUS == BTDefine.ACTION_BT_OUTGOING_CALL) return;
            mTopAppClassName = getTopClassName(context);
            BT_PHONE_STATUS = BTDefine.ACTION_BT_OUTGOING_CALL;
            Utils.switchBtSoundChannel(context, BT_ENABLE);
            String phoneNumber = intent.getStringExtra(BTDefine.EXTRA_PHONE_NUMBER);
            BtHelper.startMaintActivity(context, BT_PHONE_STATUS);
            LOG.print("mOutGoingCallNumber: " + phoneNumber + " mTopAppClassName=" + mTopAppClassName);
        }
        else if (BTDefine.ACTION_BT_BEGIN_CALL_ONLINE.equals(action))
        {
            LOG.print("RECEIVEDCALLv");
            Utils.switchBtSoundChannel(context, BT_ENABLE);
            BT_PHONE_STATUS = BTDefine.ACTION_BT_BEGIN_CALL_ONLINE;
            BtHelper.startMaintActivity(context, BT_PHONE_STATUS);
        }
        else if (BTDefine.ACTION_BT_END_CALL.equals(action))
        {
        	Log.i("end", "接收到挂断电话的广播");
            BT_PHONE_STATUS = CANCEL_CALL;
            ((BTApplication) context.getApplicationContext()).setCallNumber("");
            Utils.switchBtSoundChannel(context, BT_DISABLE);
            if (BtHelper.APP_CLASS_NAME.equals(mTopAppClassName))
            {
                //BtHelper.startMaintActivity(context, BT_PHONE_STATUS);
            }
        }
        else if (BTDefine.ACTION_BT_OUTGOING_NUMBER.equals(action))
        {
            Utils.switchBtSoundChannel(context, BT_ENABLE);
            String phoneNumber = intent.getStringExtra(BTDefine.EXTRA_PHONE_NUMBER);
            if (phoneNumber != null)
            {
                mInCommingCallNumber = phoneNumber;
                ((BTApplication) context.getApplicationContext()).setCallNumber(mOutGoingCallNumber);
                LOG.print("PHONE_INFO_RECEIVED mOutGoingCallNumber: " + phoneNumber);
                BtHelper.startMaintActivity(context, BT_PHONE_STATUS);
            }
        }
        else if (BTDefine.ACTION_BT_INCOMING_NUMBER.equals(action))
        {
            String phoneNumber = intent.getStringExtra(BTDefine.EXTRA_PHONE_NUMBER);
            if (phoneNumber != null)
            {
                mInCommingCallNumber = phoneNumber;
                BtHelper.startMaintActivity(context, BT_PHONE_STATUS);
                LOG.print("PHONE_NUMBER_RECEIVED phoneNumber: " + phoneNumber);
            }
        }
        else if (BTDefine.ACTION_BT_PIM_SYNC_FINISH.equals(action))
        {
        	if(BT_PHONE_STATUS == BTDefine.ACTION_BT_HFP_RELEASE)return;
            int resultCode = intent.getIntExtra(BTDefine.EXTRA_PIM_SYNC_RESULT, -1);
            LOG.print("finish.resultcode=" + resultCode);
            if (resultCode == BTDefine.VALUE_SYNC_CONTACT_SUCCESS) //电话本下载成功
            {
                BT_CONTACT_DOWNLOAD_RESULT = BTDefine.VALUE_SYNC_CONTACT_SUCCESS;
            }
            else if (resultCode == BTDefine.VALUE_SYNC_CALL_LOG_SUCCESS)//呼叫日志下载成功
            {
                BT_CALL_DOWNLOAD_RESULT = BTDefine.VALUE_SYNC_CONTACT_SUCCESS;
            }
            else if (resultCode == BTDefine.VALUE_SYNC_CONTACT_FAILD)
            {

                BT_CONTACT_DOWNLOAD_RESULT = BTDefine.VALUE_SYNC_CONTACT_FAILD;
                //Toast.makeText(mcontext, R.string.phone_book_down_failed, 1500).show();
            }
            else if (resultCode == BTDefine.VALUE_SYNC_CALL_LOG_FAILD)
            {
                BT_CALL_DOWNLOAD_RESULT = BTDefine.VALUE_SYNC_CALL_LOG_FAILD;
                //Toast.makeText(mcontext, R.string.call_log_down_failed, 1500).show();
            }
            LOG.print("downLoad=" + resultCode);
        } 
        LOG.print("action.end=" + action + " status" + intent.getIntExtra(BTDefine.EXTRA_BT_CONNECTION_EVENT, 9) + BT_PHONE_STATUS);
    }

    private String getTopClassName(Context context)
    {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
        String className = cn.getClassName();
        LOG.print("className=" + className);
        return className;
    }

    private void clearBtPhoneData()
    {
        Log.i("clear","清除数据");
        AbsBtContactNumFragment.clearData();
        AbsBtCallDetailFragment.clearData();
    }
}
