package com.ls.bt.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.utils.log.JLog;
import com.android.utils.utils.AbsServiceControllerHelper.OnServiceConnectSuccessListener;
import com.android.utils.utils.SkinUtil;
import com.bt.BTConstant;
import com.bt.BTConstant.MusicCommand;
import com.bt.BTController;
import com.bt.BTDefine;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.bt.utils.BtHelper;
import com.ls.android.phone.R;
import com.nwd.kernel.utils.KernelConstant;
/**
 * 蓝牙音乐界面
 * 
 * @author user
 * 
 */
abstract public class AbsBtMusicFragment extends AbsBaseFragment implements OnClickListener
{
	private static final JLog LOG = new JLog("AbsBtMusicFragment", AbsBtMusicFragment.DEBUG, JLog.TYPE_DEBUG);
	public static final boolean DEBUG = true;

	private ImageButton play;
	protected TextView btStatus;
	private boolean isClick=false;
	private ImageButton preMusic;
	private ImageButton nextMusic;
	private final static int MESSAGE_BLUETOOTH_ON = 0;
	private final static int MESSAGE_BLUETOOTH_ALREADY_ON = 1; 
    private final static int MESSAGE_LAST = 2;
    protected final static int MESSAGE_PALY_OR_PAUSE = 3; 
    private final static int MESSAGE_NEXT = 4;
    private final static int MESSAGE_BUTTON_SWITCH = 5;
    private final static int MESSAGE_REFRESH_PLAY_BUTTON = 6;
	private final static int MSG_INIT_STATE = 7;
	private final static int MSG_CALIBRATION_PLAY_STATE = 8;
	private boolean a2dp = true ;
	protected boolean mIsPlaying = false;
	private final static int BLUETOOTH0N = BTDefine.VALUE_OPEN;
    private final static int BLUETOOTHOFF = BTDefine.VALUE_CLOSE;
	

	private final static String BLUESTATECHANGE = BTDefine.ACTION_BT_STATE_CHANGE;
    public final static String BLUE_STATE_CONNECTION_CHANGE = BTDefine.ACTION_BT_CONNECTION_CHANGE;    
    public final static String BLUE_STAT_PAUSE= BTDefine.ACTION_BT_MUSIC_PAUSE;
    public final static String BLUE_STAT_PLAY= BTDefine.ACTION_BT_MUSIC_PLAY;
    
    private BTController mBTController;
    
    /**接收广播中的播放状态*/
    private boolean calibrationPlayStatus = false;
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        
    	LOG.print("Music.onCreate");
    	Log.i("fragment", "蓝牙音乐界面");
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BLUE_STATE_CONNECTION_CHANGE);
        filter.addAction(BLUESTATECHANGE);
        filter.addAction(BLUE_STAT_PAUSE);
        filter.addAction(BLUE_STAT_PLAY);
        getActivity().registerReceiver(mReceiver, filter);
        mBTController = BTController.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
    	LOG.print("Music.onCreateView.BT_PHONE_STATUS="+BtBroadcastReceiver.BT_PHONE_STATUS);
    	View view=onCreateViewBtMusic();
    	//View view=SkinUtil.inflater(getActivity(), "bt_music_layout", null);
    	play=(ImageButton)view.findViewById(R.id.music_ibt_play);
    	btStatus=(TextView)view.findViewById(R.id.bt_status);
    	preMusic=(ImageButton)view.findViewById(R.id.music_ibt_pre);
    	nextMusic=(ImageButton)view.findViewById(R.id.music_ibt_next);
    	play.setOnClickListener(this);
    	preMusic.setOnClickListener(this);
    	nextMusic.setOnClickListener(this);
//    	if(BTDefine.ACTION_BT_HFP_RELEASE.equals(BtBroadcastReceiver.BT_PHONE_STATUS))
    	

    	
    	return view;
    }
    
    /**接收蓝牙连接广播*/
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LOG.print("btmusic.action="+action);
            if(BLUE_STATE_CONNECTION_CHANGE.equals(action)) {
                int ev = intent.getIntExtra(BTDefine.EXTRA_BT_CONNECTION_EVENT, 0);
                if(ev == BTDefine.VALUE_CONNECTION_EVENT_CONNECT)
                {
                    a2dp = true;
                    btStatus.setText(R.string.bt_music_ok);
                }
                else
                {
                	btStatus.setText(R.string.disconnect_successful);
                    a2dp = false;
                }
                mHandler.sendEmptyMessage(MESSAGE_BUTTON_SWITCH); 
            } else if (BLUESTATECHANGE.equals(action)) {
                int state = intent.getIntExtra(
                        BTDefine.EXTRA_BT_STATE, BLUETOOTH0N);
                if (state == BLUETOOTH0N) {
                    mHandler.sendEmptyMessage(MESSAGE_BLUETOOTH_ALREADY_ON);
                } else if (state == BLUETOOTHOFF) {
                    a2dp = false;
                    mHandler.sendEmptyMessage(MESSAGE_BUTTON_SWITCH);                   
                }
                
            } else if(BLUE_STAT_PLAY.equals(action)) {
                // 播放
            	calibrationPlayStatus = true;
            	onChangePlayState(play,true);
            	
            } else if(BLUE_STAT_PAUSE.equals(action)) {
                // 暂停
            	calibrationPlayStatus = false;
            	onChangePlayState(play,false);
            }
        }
    };
    
    
    
    
    @Override
    public void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    }
    
    @Override
    public void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    }
    
    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	LOG.print("onPause="+mIsPlaying);
    	/**退出蓝牙音乐暂停*/
    	mHandler.sendEmptyMessage(MESSAGE_PALY_OR_PAUSE);
    	/**退出蓝牙音乐需要静音*/
    	if(null!=btDeviceFeature)
    	{
    		try {
				btDeviceFeature.setBtMusicMute(true);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	
    }
    
    
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    @Override
    public void onDestroy() {
    	if (mHandler != null)
    		mHandler.removeMessages(MSG_CALIBRATION_PLAY_STATE);
    	super.onDestroy();
    	getActivity().unregisterReceiver(mReceiver);
    }
    
    protected Handler mHandler = new Handler(){
        
        public void handleMessage(Message msg) 
        {
            try {
                switch(msg.what){
                
                case MESSAGE_BLUETOOTH_ON:
                    if(btDeviceFeature != null)
                    	btDeviceFeature.startBlueTooth_BC();
                    break;
                case MESSAGE_BLUETOOTH_ALREADY_ON:
                    if(btDeviceFeature != null && btDeviceFeature.isConnectDevice()) {                            
                        a2dp = true;                       
                    } else {
                        a2dp = false;
                    } 
                    mHandler.sendEmptyMessage(MESSAGE_BUTTON_SWITCH);
                    break;
                case MESSAGE_BUTTON_SWITCH:
                    if(a2dp) {
                    } else {
                    }                                    
                    break;
                case MESSAGE_PALY_OR_PAUSE:
                    if(btDeviceFeature != null && btDeviceFeature.isConnectDevice()) {
                        if(!mIsPlaying) {
                        	btDeviceFeature.playControl(BTConstant.MusicCommand.PLAY);
                            mIsPlaying = true;
                        	LOG.print("music.handler.play");
                        	onChangePlayState(play,true);
                        } else {
                        	btDeviceFeature.playControl(BTConstant.MusicCommand.PLAY);//PAUSE
                            mIsPlaying = false;
                        	LOG.print("music.handler.pause");
                        	onChangePlayState(play,false);
                        }
                        mHandler.removeMessages(MSG_CALIBRATION_PLAY_STATE);
                        mHandler.sendEmptyMessageDelayed(MSG_CALIBRATION_PLAY_STATE, 1500);
                    }
                    break;
                case MSG_CALIBRATION_PLAY_STATE: //频繁按播放、暂停时需要校准播放状态
                	 LOG.print("music.calbration.mIsPlaying="+mIsPlaying+" cal="+calibrationPlayStatus);
                       if(calibrationPlayStatus!=mIsPlaying)
                       {
                    	   mIsPlaying = calibrationPlayStatus;
                    	   onChangePlayState(play,mIsPlaying);
                       }
                break;
                case MESSAGE_LAST:  
                    if(btDeviceFeature != null && btDeviceFeature.isConnectDevice()) {
//                        mA2dp.BRDSDK_AvrcpControl(IVTBluetoothDef.BTSDK_AVRCP_OPID_AVC_PANEL_BACKWARD);
                    	btDeviceFeature.playControl(MusicCommand.PREVIOUS);
                    }
                    break;
                case MESSAGE_NEXT:
                	LOG.print("btDeviceFeature="+(btDeviceFeature != null)+" isConnectDevice="+btDeviceFeature.isConnectDevice());
                    if(btDeviceFeature != null && btDeviceFeature.isConnectDevice()) {
//                        mA2dp.BRDSDK_AvrcpControl(IVTBluetoothDef.BTSDK_AVRCP_OPID_AVC_PANEL_FORWARD);
                    	btDeviceFeature.playControl(MusicCommand.NEXT);
                    }
                    break;
                case MESSAGE_REFRESH_PLAY_BUTTON:
                	LOG.print("music.handler.frase"+mIsPlaying);
                	onChangePlayState(play,mIsPlaying);
                    break;
                case MSG_INIT_STATE:
                    try {
                        if(btDeviceFeature != null)
                        {
                            if(btDeviceFeature.isBlueToothPowerOn())
                            {
                                mHandler.sendEmptyMessage(MESSAGE_BLUETOOTH_ALREADY_ON);
                            }
                            if(btDeviceFeature.isConnectDevice())
                            {
                            	btDeviceFeature.playControl(MusicCommand.PLAY);
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }                    
                    break;
                }                
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    };
    
    
    
    
	@Override
	public void onClick(View v) {
		int id=v.getId();
		LOG.print("onClick="+id);
		if(id==R.id.music_ibt_play){
			mHandler.sendEmptyMessage(MESSAGE_PALY_OR_PAUSE);
		} else if(id==R.id.music_ibt_pre){
			mHandler.sendEmptyMessage(MESSAGE_LAST);
		} else if(id==R.id.music_ibt_next){
			LOG.print("onClick.Next");
			mHandler.sendEmptyMessage(MESSAGE_NEXT);
		}
	}
	protected abstract View onCreateViewBtMusic();
	protected abstract void onChangePlayState(View view,boolean isPlaying);
}
