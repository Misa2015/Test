package com.ls.bt.music;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bt.BTConstant.MusicCommand;
import com.bt.BTController;
import com.bt.BTDefine;
import com.bt.BTFeature;
import com.ls.bt.music.R;
import com.ls.bt.utils.BtMusicService;
import com.ls.bt.utils.Utils;
import com.nwd.kernel.utils.KernelConstant;

abstract public class AbsBTMusicActivity extends Activity implements OnClickListener{
    private static final JLog LOG = new JLog("MainActivity", true, JLog.TYPE_DEBUG);

    private final String TAG = "MainActivity";
    
    public static Context skinContext;
    public static LayoutInflater skinInflater;
    private final static int MESSAGE_BLUETOOTH_ON = 0;
    private final static int MESSAGE_BLUETOOTH_ALREADY_ON = 1; 
    private final static int MESSAGE_LAST = 2;
    private final static int MESSAGE_PALY_OR_PAUSE = 3; 
    private final static int MESSAGE_NEXT = 4;
    private final static int MESSAGE_BUTTON_SWITCH = 5;
    private final static int MESSAGE_REFRESH_PLAY_BUTTON = 6;
    private final static int MSG_INIT_STATE = 7;
    
    private final static String BLUESTATECHANGE = BTDefine.ACTION_BT_STATE_CHANGE;
    public final static String BLUE_STATE_CONNECTION_CHANGE = BTDefine.ACTION_BT_CONNECTION_CHANGE;    
    public final static String BLUE_STAT_PAUSE= BTDefine.ACTION_BT_MUSIC_PAUSE;
    public final static String BLUE_STAT_PLAY= BTDefine.ACTION_BT_MUSIC_PLAY;
    
    private final static int BLUETOOTH0N = BTDefine.VALUE_OPEN;
    private final static int BLUETOOTHOFF = BTDefine.VALUE_CLOSE;
    
    
    private ViewGroup mOpenPanel;
    private ImageButton mOpenBluetooth;
    private TextView mBluetoothStatus;
    
    private ViewGroup mBluetoothInfoPanel;
    
    private ViewGroup mPlayControl;
    private View layOutView;
    private boolean a2dp = true ;
    private boolean mIsPlaying = false;   
    
    private BTController mBTController;
    private ImageButton mBtnPlayPause,mBtnNext,mBtnPrevious;
    
    private static final int LEVEL_PLAY = 1;
    private static final int LEVEL_PAUSE = 0;
    
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action = " + action);
            
            if(BLUE_STATE_CONNECTION_CHANGE.equals(action)) {
                int ev = intent.getIntExtra(BTDefine.EXTRA_BT_CONNECTION_EVENT, 0);
                if(ev == BTDefine.VALUE_CONNECTION_EVENT_CONNECT)
                {
                    Log.d(TAG, "****a2dpclass connect ....***");
                    a2dp = true;
                }
                else
                {
                    Log.d(TAG, "****a2dpclass disconnect ....***");
                    a2dp = false;
                }
                mHandler.sendEmptyMessage(MESSAGE_BUTTON_SWITCH); 
            } else if (BLUESTATECHANGE.equals(action)) {
                int state = intent.getIntExtra(BTDefine.EXTRA_BT_STATE, BLUETOOTH0N);
                if (state == BLUETOOTH0N) {
                    Log.d(TAG, "****bluetooth on ....***");
                    mHandler.sendEmptyMessage(MESSAGE_BLUETOOTH_ALREADY_ON);
                } else if (state == BLUETOOTHOFF) {
                    Log.d(TAG, "****bluetooth off ....***");
                    a2dp = false;
                    mHandler.sendEmptyMessage(MESSAGE_BUTTON_SWITCH);                   
                }
                
            } else if(BLUE_STAT_PLAY.equals(action)) {
            	Log.d(TAG, "****a2dp play ....***");
                // 播放
            	try {
            	    if(mBTController.getFeature().isConnectDevice())
            	    {
            	    	//((ImageButton)findViewById(R.id.btn_pause)).setImageResource(R.drawable.btn_pause_selector);
            	        mBtnPlayPause.setImageLevel(LEVEL_PAUSE);
                        mIsPlaying = true;
            	    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            	
            } else if(BLUE_STAT_PAUSE.equals(action)) {
            	Log.d(TAG, "****a2dp pause ....***");
                // 暂停
            	try {
            	    if(mBTController.getFeature().isConnectDevice())
            	    {
            	    	//((ImageButton)findViewById(R.id.btn_pause)).setImageResource(R.drawable.btn_play_selector);
            	        mBtnPlayPause.setImageLevel(LEVEL_PLAY);
                        mIsPlaying = false; 
            	    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
        }
    };
            
    private Handler mHandler = new Handler(){
        
        public void handleMessage(Message msg) 
        {
            try {
                BTFeature btFeature = mBTController.getFeature();
                switch(msg.what){
                case MESSAGE_BLUETOOTH_ON:
                    if(btFeature != null)
                        btFeature.startBlueTooth_BC();
                    break;
                case MESSAGE_BLUETOOTH_ALREADY_ON:
                    mOpenBluetooth.setImageResource(SkinUtil.getIdentifier(skinContext,"bluetooth_open" ,SkinUtil.TYPE_DRAWABLE));
                    mBluetoothStatus.setText(getString(R.string.bluetooth_connect_phone));   
                    if(btFeature != null //&& btFeature.isConnectDevice()
                    		&& btFeature.isConnectA2DP()
                    		) {                            
                        a2dp = true;                       
                    } else {
                        a2dp = false;
                    } 
                    mHandler.sendEmptyMessage(MESSAGE_BUTTON_SWITCH);
                    break;
                case MESSAGE_BUTTON_SWITCH:
                    if(a2dp) {
                        mPlayControl.setVisibility(View.VISIBLE);
                        mBluetoothInfoPanel.setVisibility(View.VISIBLE);
                        mOpenPanel.setVisibility(View.GONE);
                    } else {
                        mOpenPanel.setVisibility(View.VISIBLE); 
                        mPlayControl.setVisibility(View.GONE);
                        mBluetoothInfoPanel.setVisibility(View.GONE);
                    }                                    
                    break;
                case MESSAGE_PALY_OR_PAUSE:
                    if(btFeature != null && btFeature.isConnectA2DP()) {
                    	boolean bAvcrp = btFeature.isConnectAVRCP();
                    	LOG.print("===lsls===avcrp connected===" + bAvcrp);
                        if(!mIsPlaying) {
                            Intent intent =new Intent("PLAY");
            				mContext.sendBroadcast(intent);
                            mIsPlaying = true;
                        } else {
                            Intent intent =new Intent("PAUSE");
            				mContext.sendBroadcast(intent);
                            mIsPlaying = false;                       
                        }
                        mHandler.sendEmptyMessage(MESSAGE_REFRESH_PLAY_BUTTON);
                    } 
                    break;
                case MESSAGE_LAST:  
                    if(btFeature != null && btFeature.isConnectA2DP()) {
//                        mA2dp.BRDSDK_AvrcpControl(IVTBluetoothDef.BTSDK_AVRCP_OPID_AVC_PANEL_BACKWARD);
                       // btFeature.playControl(MusicCommand.PREVIOUS);
                    	Intent intent =new Intent("PREVIOUS");
        				mContext.sendBroadcast(intent);
                    }
                    break;
                case MESSAGE_NEXT:
                    if(btFeature != null && btFeature.isConnectA2DP()) {
//                        mA2dp.BRDSDK_AvrcpControl(IVTBluetoothDef.BTSDK_AVRCP_OPID_AVC_PANEL_FORWARD);
                        //btFeature.playControl(MusicCommand.NEXT);
                    	Intent intent =new Intent("NEXT");
        				mContext.sendBroadcast(intent);
                    }
                    break;
                case MESSAGE_REFRESH_PLAY_BUTTON:
                    if(mIsPlaying)
                    {
//                        ((ImageButton)findViewById(R.id.btn_pause)).setImageResource(R.drawable.btn_pause_selector);
                        mBtnPlayPause.setImageLevel(LEVEL_PAUSE);
                    }
                    else
                    {
//                        ((ImageButton)findViewById(R.id.btn_pause)).setImageResource(R.drawable.btn_play_selector);
                        mBtnPlayPause.setImageLevel(LEVEL_PLAY);
                    }
                    break;
                case MSG_INIT_STATE:
                    try {
                        if(btFeature != null)
                        {
                            if(btFeature.isBlueToothPowerOn())
                            {
                                mHandler.sendEmptyMessage(MESSAGE_BLUETOOTH_ALREADY_ON);
                            }
                            if(btFeature.isConnectDevice())
                            {
                            	Intent intent =new Intent("PLAY");
                				mContext.sendBroadcast(intent);
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
    protected void onStart() {
        super.onStart();
        // MusicHelper.appStart(getBaseContext());
        BTMusicHelper.appStart(getBaseContext());
        //获取音乐焦点
        BTMusicHelper.notifyIsBTMusicPlay(getBaseContext(), true);
        Utils.openSpeakerMusic(mContext);
        
        //初始化并播放音乐
        mBTController.connectService(new OnServiceConnectSuccessListener() {
            @Override
            public void onServiceConnectSuccess() {
                LOG.print("connect bt service success");
                mHandler.sendEmptyMessage(MSG_INIT_STATE);
            }
        });
    };
    
    @Override
    protected void onStop() {
        super.onStop();
        BTMusicHelper.notifyIsBTMusicPlay(getBaseContext(), false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinUtil.setSkin(this, "com.ls.bt.music", "com.ls.bt.music.skin.cool");
    	skinContext  =  SkinUtil. getSkinContext(this);  //皮肤上下文
    	skinInflater = LayoutInflater.from(skinContext);
        layOutView = onCreateBTMainView(skinContext);
        setContentView(layOutView); 
        //setContentView(R.layout.bluetooth_player);
        if( null != SkinUtil.findViewById(skinContext,layOutView,"btn_home" )) {
            SkinUtil.findViewById(skinContext,layOutView,"btn_home" ).setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View arg0) {
                	//KernelUtils.home(getBaseContext());
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
        mBTController = BTController.getInstance(getBaseContext());
        initViews();  
        IntentFilter filter = new IntentFilter();
        filter.addAction(BLUE_STATE_CONNECTION_CHANGE);
        filter.addAction(BLUESTATECHANGE);
        filter.addAction(BLUE_STAT_PAUSE);
        filter.addAction(BLUE_STAT_PLAY);
        filter.addAction(BTDefine.ACTION_BT_END_CALL);
        registerReceiver(mReceiver, filter);
    }

    private void initViews(){
    	mOpenPanel = (ViewGroup)SkinUtil.findViewById(skinContext,layOutView,"open_panel" );
    	mOpenBluetooth = (ImageButton)SkinUtil.findViewById(skinContext,layOutView,"btn_open_bluetooth" );
    	mOpenBluetooth.setId(R.id.btn_open_bluetooth);
    	mOpenBluetooth.setOnClickListener(this);
    	mBluetoothStatus = (TextView)SkinUtil.findViewById(skinContext,layOutView,"tv_bluetooth_status" );
    	mBluetoothInfoPanel = (ViewGroup)SkinUtil.findViewById(skinContext,layOutView,"info_panel" );
    	mPlayControl = (ViewGroup) SkinUtil.findViewById(skinContext,layOutView,"play_control_panel" );
    	mBtnPlayPause = (ImageButton)SkinUtil.findViewById(skinContext,layOutView,"btn_pause" );
    	mBtnPlayPause.setId(R.id.btn_pause);
    	mBtnPlayPause.setOnClickListener(this);
    	mBtnNext = (ImageButton)SkinUtil.findViewById(skinContext,layOutView,"btn_next" );
    	mBtnNext.setId(R.id.btn_next);
    	mBtnNext.setOnClickListener(this);
    	mBtnPrevious = (ImageButton)SkinUtil.findViewById(skinContext,layOutView,"btn_previous" );
    	mBtnPrevious.setId(R.id.btn_previous);
    	mBtnPrevious.setOnClickListener(this);
    }
    
    Context mContext = getApplicationContext();
    
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        //--if leave a2dp, stop a2dp music 为解决延迟发送广播， 退出暂停
        try {
            if(mBTController.getFeature().isConnectDevice())
            {
            	Intent intent =new Intent("PAUSE");
				mContext.sendBroadcast(intent);
                mIsPlaying = false;  
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mIsPlaying = BTMusicHelper.mBlueToothPlayOrPasue;
        mHandler.sendEmptyMessage(MESSAGE_REFRESH_PLAY_BUTTON);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open_bluetooth) {
            mHandler.sendEmptyMessage(MESSAGE_BLUETOOTH_ON);
        } else if (v.getId() == R.id.btn_previous) {
            mHandler.sendEmptyMessage(MESSAGE_LAST);
        } else if (v.getId() == R.id.btn_pause) {
            mHandler.sendEmptyMessage(MESSAGE_PALY_OR_PAUSE);
        } else if (v.getId() == R.id.btn_next) {
            mHandler.sendEmptyMessage(MESSAGE_NEXT);
        }       
    }  
    
    /**
     * 获取蓝牙音乐的界面
     * @return
     */
    abstract protected View onCreateBTMainView(Context skinContext);
}
