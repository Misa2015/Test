package com.ls.bt.utils;

import com.android.utils.log.JLog;
import com.bt.BTConstant.MusicCommand;
import com.bt.BTConstant;
import com.bt.BTController;
import com.bt.BTDefine;
import com.bt.BTFeature;
import com.ls.bt.music.BTMusicHelper;
import com.nwd.kernel.utils.KernelConstant;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class BtMusicService extends Service{

	JLog LOG = new JLog("BtMusicService",BtMusicService.DEBUG,JLog.TYPE_DEBUG);
	public static final boolean DEBUG = true;
	BTController mBTController;
	private BTFeature btFeature;
	 
	protected static final int PLAY = 0;
	protected static final int PREVIOUS = 1;
	protected static final int NEXT = 2;
	protected static final int PAUSE = 3;
	
	 //蓝牙开关状态发生改变
    private final static String BLUESTATECHANGE = BTDefine.ACTION_BT_STATE_CHANGE;
    //蓝牙连接状态发生改变
    public final static String BLUE_STATE_CONNECTION_CHANGE = BTDefine.ACTION_BT_CONNECTION_CHANGE;    
    //蓝牙音乐结束播放
    public final static String BLUE_STAT_PAUSE= BTDefine.ACTION_BT_MUSIC_PAUSE;
    //播放蓝牙音乐
    public final static String BLUE_STAT_PLAY= BTDefine.ACTION_BT_MUSIC_PLAY;
    //蓝牙开启
    private final static int BLUETOOTH0N = BTDefine.VALUE_OPEN;
    //蓝牙关闭
    private final static int BLUETOOTHOFF = BTDefine.VALUE_CLOSE;
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "MyService onStart", Toast.LENGTH_LONG).show();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
            registerReceiver(mMcuKeyReceiver, new IntentFilter(KernelConstant.ACTION_KEY_VALUE));
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		/*IntentFilter filter1 = new IntentFilter();
        filter1.addAction(BLUE_STAT_PAUSE);
        filter1.addAction(BLUE_STAT_PLAY);
        registerReceiver(mReceiver, filter1);*/
        
	}
	
/*	BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			 if(BLUE_STAT_PLAY.equals(action)) {
				 Log.i("dp", "接收到音乐播放的广播>>>>>>>>>>>>>>>>>>>>>>>");
	             Utils.openSpeakerMusic(getBaseContext());
	             Utils.setAudioMode(context, AudioManager.MODE_IN_CALL);
	             Utils.openSpeaker1(context);
	             BTMusicHelper.mBlueToothPlayOrPasue = true;
	         } else if(BLUE_STAT_PAUSE.equals(action)) {
	        	 Utils.sysEnable();
	        	 Utils.closeSpeaker(context);
	        	 Utils.setAudioMode(context, AudioManager.MODE_NORMAL);
	        	 BTMusicHelper.notifyIsBTMusicPlay(context, false);
	             BTMusicHelper.mBlueToothPlayOrPasue = false;
	         } 
		}
	};*/
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		 mBTController = BTController.getInstance(getBaseContext());
		 btFeature = mBTController.getFeature();
		try {
			btFeature.playControl(BTConstant.MusicCommand.PAUSE);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
            unregisterReceiver(mMcuKeyReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			 mBTController = BTController.getInstance(getBaseContext());
			 btFeature = mBTController.getFeature();
			Log.i("music", "BtMusicService接收消息  ");
			try {
				switch (msg.what) {
				case PLAY:
					btFeature.playControl(BTConstant.MusicCommand.PLAY);
					break;
				case PREVIOUS:
					btFeature.playControl(MusicCommand.PREVIOUS);
					break;
				case NEXT:
					btFeature.playControl(MusicCommand.NEXT);
					break;
				case PAUSE:
					btFeature.playControl(MusicCommand.PAUSE);
					break;
				default:
					break;
				}
			} catch (Exception e) {
			}
		};
		
	};
	
	  private BroadcastReceiver mMcuKeyReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if(KernelConstant.ACTION_KEY_VALUE.equals(action))
	            {
	                byte key = intent.getByteExtra(KernelConstant.EXTRA_KEY_VALUE, (byte) 0);
	                switch(key)
	                {
	                case KernelConstant.McuKeyValue.NEXT:
	                    mHandler.sendEmptyMessage(NEXT);
	                    break;
	                case KernelConstant.McuKeyValue.PREVIOUS:
	                    mHandler.sendEmptyMessage(PREVIOUS);
	                    break;
	                case KernelConstant.McuKeyValue.PLAY:
	                    mHandler.sendEmptyMessage(PLAY);
	                    break;
	                }
	            }
	        }
	  };
}
