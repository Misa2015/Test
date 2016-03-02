package com.ls.bt.utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.utils.log.JLog;
import com.bt.BTDefine;
import com.ls.bt.music.BTMusicHelper;

/***
 * 蓝牙广播接收
 * @author june
 */
public class BtBroadcastReceiver extends BroadcastReceiver
{
    private static final JLog LOG = new JLog("MusicBtBroadcastReceiver", BtBroadcastReceiver.DEBUG, JLog.TYPE_DEBUG);
    public static final boolean DEBUG = true;
    public static final byte BT_STATUS_IDEL = 0;
    public static final byte BT_STATUS_CONNECTING = 2;
    public static final byte BT_STATUS_CALL_IN = 3;
    public static final byte BT_STATUS_CALL_OUT = 4;
    public static final byte BT_STATUS_ONLINE = 5;

    /** 下载电话本标志位 */
    public final static String BT_ENABLE = "bt_enable";
    public final static String BT_DISABLE = "bt_disable";
    Context mcontext;
    public static String mTopAppClassName = "";
    
    //语音
  	private final String ACTION_LOCAL_NOTIFICATION_STOP = "action_local_notification_stop";
  	private final String ACTION_LOCAL_NOTIFICATION_START = "action_local_notification_start";
  	//系统声音
  	private final String ACTION_HXB_AUDIO_FOCUS_OPEN = "ilincar.audio.focus.open";
  	private final String ACTION_HXB_AUDIO_FOCUS_CLOSE = "ilincar.audio.focus.close";
  	
  	// 本机蓝牙连接，断开广播
 	private final String ACTION_BTHOST_CONNECTED = "ilincar.bthost.connected";
 	private final String ACTION_BTHOST_DISCONNECTED = "ilincar.bthost.disconnected";
 	
    /** 是否正在通话*/
    public static boolean isCalling = false;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String act = intent.getAction();
        
        Log.i("bt", "action="+act);
        
        if (BTDefine.ACTION_BT_INCOMING_CALL.equals(act) || BTDefine.ACTION_BT_OUTGOING_CALL.equals(act)
        		|| BTDefine.ACTION_BT_OUTGOING_CALL.equals(act) || BTDefine.ACTION_BT_BEGIN_CALL_ONLINE.equals(act)) {
        	isCalling = true;
        }else if(BTDefine.ACTION_BT_END_CALL.equals(act)){		//电话挂断
        	isCalling = false;
        	if(BTMusicHelper.mBlueToothPlayOrPasue == true){
        		Utils.openSpeakerMusic(context);
        	}else{
        		Utils.sysEnable();
        	}
        	Utils.sysEnable();
        	
        }
    }

    private String getTopClassName(Context context)
    {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
        String className = cn.getClassName();
        LOG.print("className=" + className);
        return className;
    }
}