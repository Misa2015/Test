package com.ls.bt.inout;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.utils.log.JLog;
import com.bt.BTController;
import com.bt.BTDefine;
import com.bt.BTFeature;
import com.ls.android.bt.AbsBTMainActivity;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.bt.service.AbsBTContactService;
 
import com.ls.bt.utils.BtHelper;
import com.ls.bt.utils.ButtonGridLayout;
import com.ls.bt.utils.Utils;
import com.ls.bt.utils.VerticalSeekbar;
import com.ls.android.phone.R;
import com.nwd.kernel.source.DeviceState;
import com.nwd.kernel.source.SettingTableKey;
import com.nwd.kernel.source.SettingTableKey.PublicSettingTable;
import com.nwd.kernel.utils.KernelConstant;

 
abstract public class AbsBtCallOnLingFragment extends AbsBaseFragment implements OnClickListener
{
    private static final JLog LOG = new JLog("AbsBtCallOnLingFragment", AbsBtCallOnLingFragment.DEBUG, JLog.TYPE_DEBUG);
    public static final boolean DEBUG = false;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private static final int GET_LOCALDEVICE_KEY_LAYOUT = 0;
    private static final int GET_DIALING_LONG_TIME = 1;
    private static final int GET_CALL_NAME = 2;
    private static final int MSG_CHANGE_CHANNEL = 3;
    private static final int END_NO_RESP = MSG_CHANGE_CHANNEL + 1;
    private TextView mTextViewName;
    private TextView mTextViewCallingTime;
    private String mNumberString = "";
    private TextView mTextViewNumber;
    private ImageButton mImageButtonHangup, mImageButtonSwitch, mDigitSwitch;
	private SeekBar seekBar;
	private ImageView mVoiceShow;
	private ImageView mVoiceSet;
	private VerticalSeekbar  mVerticalSeekBar;
	private TextView mVolmeText;
	
    private int mDialingTimeCount;
    private boolean mCalling = false;
    private boolean mIsPhoneSound = false;
    private boolean isSoundAtCar = true;
    
    private boolean isVoiceShow = false;

    private Dialog keyDialog;

    public static final String INCOMING_STATE = "isInComingState", INCOMING_NUMBER = "inComingNumber";
    private static final String TAG = "BtIncomingFragment";
    String mInComingNumber = "";
    String mInComingName = "";
    private ButtonGridLayout mDialNumGridLayout = null;
    
    /**默认延时时间*/
    private static final int DEFAULT_DELAY_TIME = 1000;
    
    //语音控制蓝牙广播
  	private final String hangupCall = "com.bt.action_bt.hangupCall";
  	
  	public final static String BT_DISABLE = "bt_disable";
 
  	private final String SEND_CALLTIME = "com.bt.call.time";
  	//熄火广播
  	 private static final String car_signal = "carsignal";
     private static final String car_powerdown_suspend = "car_powerdown_suspend";
     private static final String car_mode = "carmode";
     
     public int vol;
     public int volume;
  	private SharedPreferences preferences;
  	private Editor edit;

     public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case GET_LOCALDEVICE_KEY_LAYOUT:
                    View passview = onCreateViewBtCallOnLineDialog();
                    mDialNumGridLayout = (ButtonGridLayout) passview.findViewById(R.id.dial_num_gridlayout);
                    mDialNumGridLayout.registerDialNumberListener(ditigsListener);
                    keyDialog = onCreateDialogFromDialogStyle();
                    keyDialog.setContentView(passview);
                    break;
                case GET_DIALING_LONG_TIME:
                    updateDailingTime();
                    mHandler.sendEmptyMessageDelayed(GET_DIALING_LONG_TIME, 1000);
                    break;
                case GET_CALL_NAME:
                   // mInComingNumber = BtBroadcastReceiver.mInCommingCallNumber;
                    //mTextViewNumber.setText(mInComingNumber);
                    //mInComingName = "";
                    //获取正在通话中的name
                    //mInComingName = Utils.getCallingName(mInComingNumber, AbsBTContactService.mContactList);
                    //mTextViewName.setText(mInComingName);
                    break;
                case MSG_CHANGE_CHANNEL:
                    checkIsNeedToSwitchSoundToPhone();
                    break;
                case END_NO_RESP : // 挂电话时无响应
                	Log.i("end", "挂断电话");
                	Intent it = new Intent(BTDefine.ACTION_BT_END_CALL);
                	getActivity().sendBroadcast(it);
                	it = null;
                	break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("setVolume", 0);
    	edit = preferences.edit();
    }
    
    @Override
    public void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = onCreateViewCallOnLineLayout();
        //View view=SkinUtil.inflater(mContext, "bt_call_on_line_layout", null);
        mDigitSwitch = (ImageButton) view.findViewById(R.id.switch_digit_key);
        mDigitSwitch.setOnClickListener(this);

        mTextViewName = (TextView) view.findViewById(R.id.incoming_name);
        mTextViewNumber = (TextView) view.findViewById(R.id.incoming_number);
        mTextViewCallingTime = (TextView) view.findViewById(R.id.tv_phone_calling);
        mImageButtonHangup = (ImageButton) view.findViewById(R.id.ibt_off_phone);
        mImageButtonHangup.setOnClickListener(this);

        mImageButtonSwitch = (ImageButton) view.findViewById(R.id.ibt_bt_phone);
        mImageButtonSwitch.setOnClickListener(this);

        mVoiceShow = (ImageView)view.findViewById(R.id.bt_voice_show);
        mVoiceShow.setOnClickListener(this);
        
        mVoiceSet = (ImageView)view.findViewById(R.id.bt_voice_set);
        mVerticalSeekBar= (VerticalSeekbar)view.findViewById(R.id.vertical_seekbar);
        mVerticalSeekBar.setOnSeekBarChangeListener(seekListener);
        
        mVolmeText = (TextView)view.findViewById(R.id.volme_text);
        
        mHandler.sendEmptyMessage(GET_LOCALDEVICE_KEY_LAYOUT);
        mHandler.sendEmptyMessageDelayed(GET_CALL_NAME, 3000);
        LOG.print("BtIncomingFragment.onCreateView.mInComingNumber=" + mInComingNumber + " mInComingName" + " BT_PHONE_STATUS=" + BtBroadcastReceiver.BT_PHONE_STATUS);
        return view;
    }
    
    OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			try {
				vol = progress/5;
				if(btDeviceFeature != null){
					btDeviceFeature.setBtMusicVolume(vol);
				}
				mVolmeText.setText(progress+"%");
            	edit.putInt("volume", vol);
            	edit.commit();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}};
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                LOG.print("KEYCODE_BACK");
                return false;
        }
        return false;
    }

    @Override
    public void onClick(View v)
    {
//        KernelUtils.requestBee(getActivity());
        super.onClick(v);
        int id = v.getId();
        
       /* *//**请求当前蓝牙音量值**//*
        try {
        	btDeviceFeature.getBtMusicVolume();
		} catch (RemoteException e) {
			e.printStackTrace();
		}*/
        
        if (id == R.id.ibt_off_phone)
        {
        	getActivity().finish();
            cancel_answer();
            mHandler.removeMessages(GET_DIALING_LONG_TIME);
        }
        else if (id == R.id.ibt_bt_phone)
        {
            switchSound();
        }
        else if (id == R.id.switch_digit_key)
        {
        	//获取屏幕的宽高
            DisplayMetrics dm = new DisplayMetrics();
    		dm = getResources().getDisplayMetrics();
    		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
    		int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
    		float scaledDensity=dm.scaledDensity;
    		float xdpi = dm.xdpi;
    		float ydpi = dm.ydpi;
    		int screenWidth = dm.widthPixels;
    		int screenHeight = dm.heightPixels;
            keyDialog.show();
            LayoutParams param = keyDialog.getWindow().getAttributes();
            param.width = screenWidth;
            //param.height = 500;
            param.y=-200;
            
            keyDialog.getWindow().setAttributes(param);
        }else if(id == R.id.bt_voice_show){

        	if(isVoiceShow){
        		mVoiceSet.setVisibility(View.VISIBLE);
        		mVerticalSeekBar.setVisibility(View.VISIBLE);
        		mVolmeText.setVisibility(View.VISIBLE);
        		isVoiceShow = false;
        	}else{
        		mVoiceSet.setVisibility(View.GONE);
        		mVerticalSeekBar.setVisibility(View.GONE);
        		mVolmeText.setVisibility(View.GONE);
        		isVoiceShow = true;
        	}	
        }
    }

    private void switchSound()
    {
        Log.d(TAG, "switchSound");
        // mSdk.answerCall();
        try
        {
            if (!mIsPhoneSound)
            {
                // if (mSdk.transfer()) {
                btDeviceFeature.changeSoundChannel();
                mIsPhoneSound = true;
                Log.i("sound", "true");
            }
            else
            {
                // if (mSdk.transfer()) {
                btDeviceFeature.changeSoundChannel();
                mIsPhoneSound = false;
                Log.i("sound", "false");
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        LOG.print("ONLINE.onResume" + " status=" + BtBroadcastReceiver.BT_PHONE_STATUS);
        
		Intent intent = new Intent("com.bt.CallOnLingService");
		getActivity().stopService(intent);
		
        if (!BTDefine.ACTION_BT_END_CALL.equals(BtBroadcastReceiver.BT_PHONE_STATUS))
        {
            getActivity().getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        }
        if (null != mHandler) mHandler.removeMessages(GET_DIALING_LONG_TIME);
        if (BTDefine.ACTION_BT_BEGIN_CALL_ONLINE.equals(BtBroadcastReceiver.BT_PHONE_STATUS))
        {
            mCalling=true;
            mHandler.sendEmptyMessage(GET_DIALING_LONG_TIME);
            if(Utils.isInBackCarState(getActivity()))
            {
                Log.d(TAG, "isInBackCarState");
                mHandler.removeMessages(MSG_CHANGE_CHANNEL);
                mHandler.sendEmptyMessageDelayed(MSG_CHANGE_CHANNEL, DEFAULT_DELAY_TIME);
            }
        }
        else
        {
            mCalling=false;
            mTextViewCallingTime.setText(R.string.calling_waiting);
        }
        
        SharedPreferences preferences = getActivity().getSharedPreferences("setVolume", 0);
        int prefer = preferences.getInt("volume", 19);
        mVolmeText.setText(prefer*5+"%");
        mVerticalSeekBar.setProgress(prefer*5);
        
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTDefine.ACTION_BT_INCOMING_NUMBER);
        intentFilter.addAction(BTDefine.ACTION_BT_OUTGOING_NUMBER);
        intentFilter.addAction(BTDefine.ACTION_BT_BEGIN_CALL_ONLINE);
        intentFilter.addAction(BTDefine.ACTION_BT_END_CALL);
        intentFilter.addAction(BTDefine.ACTION_BT_CONNECTION_CHANGE);
        intentFilter.addAction(hangupCall);
        intentFilter.addAction(car_signal);
        intentFilter.addAction(BTDefine.ACTION_A2DP_VOLUME);
        getActivity().registerReceiver(mReceiver, intentFilter);
 
        IntentFilter changeIntent = new IntentFilter();
        changeIntent.addAction(KernelConstant.ACTION_MCU_STATE_CHANGE);
        changeIntent.addAction(KernelConstant.ACTION_BACKCAR_STATE_CHANGE);
        getActivity().registerReceiver(mNeedToChangeSoundChannelReceiver, changeIntent);

        IntentFilter mMcuKeyIntentFilter = new IntentFilter(KernelConstant.ACTION_KEY_VALUE);
        getActivity().registerReceiver(McuKeyValueReciever, mMcuKeyIntentFilter);
        
    }

    private void cancel_answer()
    {
        // mSdk.cancelCall();
        try
        {
        	//挂断电话
            btDeviceFeature.cancelCall();
            mHandler.sendEmptyMessageDelayed(END_NO_RESP, 6000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LOG.print("ONLINE.onPause=" + " status=" + BtBroadcastReceiver.BT_PHONE_STATUS);
        getActivity().getWindow().clearFlags(FLAG_HOMEKEY_DISPATCHED);
        //mHandler.removeMessages(GET_DIALING_LONG_TIME);
        if (null != keyDialog)
        {
            keyDialog.hide();
        }
        mHandler.removeMessages(END_NO_RESP);
    }

    @Override
    public void onStop() {
    	super.onStop();
    	Log.i("wuguisheng", "AbsBtCallOnLingFragment onStop");
    	
    	Intent filter = new Intent("com.bt.CallOnLingService");
    	filter.putExtra("DialingTimeCount", mDialingTimeCount);
		getActivity().startService(filter);
     
        Log.i("wuguisheng", "AbsBtCallOnLingFragment onStop callTime="+mDialingTimeCount);
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        
        Intent intent = new Intent("com.bt.CallOnLingService");
		getActivity().stopService(intent);
		
        mHandler.removeMessages(END_NO_RESP);
        if (mReceiver != null)
        {
            getActivity().unregisterReceiver(mReceiver);
        }
        if (mNeedToChangeSoundChannelReceiver != null)
        {
            getActivity().unregisterReceiver(mNeedToChangeSoundChannelReceiver);
        }
        if (McuKeyValueReciever != null)
        {
            getActivity().unregisterReceiver(McuKeyValueReciever);
        }
        mHandler.removeMessages(GET_DIALING_LONG_TIME);
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
		public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            LOG.print("action=" + action);
            if (BTDefine.ACTION_BT_INCOMING_NUMBER.equals(action) || BTDefine.ACTION_BT_OUTGOING_NUMBER.equals(action))
            {
                mNumberString = intent.getStringExtra(BTDefine.EXTRA_PHONE_NUMBER);
                if(mNumberString != null){
                	if(TextUtils.isEmpty(mNumberString)){
                    	mNumberString = BtBroadcastReceiver.mInCommingCallNumber;
                    }
                    mTextViewNumber.setText(mNumberString);
                    mInComingName = "";
                    mInComingName = Utils.getCallingName(mNumberString, AbsBTContactService.mContactList);
                    mTextViewName.setText(mInComingName);
                } 
            }
            else if (BTDefine.ACTION_BT_BEGIN_CALL_ONLINE.equals(action))
            {
                mCalling = true;
                if(Utils.isInBackCarState(getActivity()))
                {
                    mHandler.removeMessages(MSG_CHANGE_CHANNEL);
                    mHandler.sendEmptyMessageDelayed(MSG_CHANGE_CHANNEL, DEFAULT_DELAY_TIME);
                }
                mHandler.sendEmptyMessage(GET_DIALING_LONG_TIME);
            }
            else if (BTDefine.ACTION_BT_END_CALL.equals(action))
            {
                mCalling = false;
                mHandler.removeMessages(GET_DIALING_LONG_TIME);
                mDialingTimeCount = 0;
                Log.i("wuguisheng", "BtBroadcastReceiver.mTopAppClassName="+BtBroadcastReceiver.mTopAppClassName);
                /*if (!BtHelper.APP_CLASS_NAME.equals(BtBroadcastReceiver.mTopAppClassName))
                {*/
                	getActivity().finish();
                //}
            }
            else if(hangupCall.equals(action)){
            	cancel_answer();
            }else if(BTDefine.ACTION_BT_CONNECTION_CHANGE.equals(action)){
            	
            	byte status = (byte) intent.getIntExtra(BTDefine.EXTRA_BT_CONNECTION_EVENT, 0);
            	
            	if (BTDefine.VALUE_CONNECTION_EVENT_DISCONNECT == status){
            		Intent it = new Intent(BTDefine.ACTION_BT_END_CALL);
                 	context.sendBroadcast(it);
                 	it = null;
                    if (!BtHelper.APP_CLASS_NAME.equals(BtBroadcastReceiver.mTopAppClassName))
                    {
                    	getActivity().finish();
                    }
            	}
            }else if(car_signal.equals(action)){
            	
            	Log.i("call", "收到熄火广播");
            	String extra = intent.getStringExtra(car_mode);
            	if(car_powerdown_suspend.equals(extra)){
                    getActivity().finish();
                    Utils.switchBtSoundChannel(context, BT_DISABLE);
            	}
            }/*else if(BTDefine.ACTION_A2DP_VOLUME.equals(action)){
            	//获取当前蓝牙的音量值
            	volume = intent.getIntExtra(BTDefine.EXTRA_A2DP_VOLUME, 0);
            	
            	SharedPreferences preferences = getActivity().getSharedPreferences("setVolume", 0);
            	Editor edit = preferences.edit();
            	edit.putInt("volume", volume);
            	edit.commit();
            	
            	if(volume <= 20){
            		mVerticalSeekBar.setProgress(volume*5);
            		mVolmeText.setText(volume*5+"%");
            	}
            }*/
        }
    };
    
    private ButtonGridLayout.DialNumberListener ditigsListener = new ButtonGridLayout.DialNumberListener()
    {
        @Override
        public void onNumberDialed(char number)
        {
            try{
                btDeviceFeature.sendDTMF(number);
                //mTextViewNumber.setText(mTextViewNumber.getText()+(String.valueOf(number)));
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }
    };

    private void updateDailingTime()
    {
        int min = 0;
        int second = 0;
        mDialingTimeCount++;
        if (mDialingTimeCount >= 60)
        {
            min = mDialingTimeCount / 60;
        }
        second = mDialingTimeCount - 60 * min;
        mTextViewCallingTime.setText(this.getResources().getString(R.string.dailing) + "    " + (min > 9 ? min : "0" + min) + ":" + (second > 9 ? second : "0" + second));
    }

    /** 是否需要切换声道的侦听器 */
    private BroadcastReceiver mNeedToChangeSoundChannelReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // 假如当前在通话中 
            if (mCalling)
            {
                if (KernelConstant.ACTION_MCU_STATE_CHANGE.equals(action))
                {
                    byte mcuState = intent.getByteExtra(KernelConstant.EXTRA_MCU_STATE, DeviceState.MCU_STATE_START);
                    switch (mcuState)
                    {
                    // MCU关机的话,假如当前声道在车机,则切换去手机 
                        case DeviceState.MCU_STATE_FALSE_SHUTDOWN:
                        case DeviceState.MCU_STATE_SHUTDOWN:
                            checkIsNeedToSwitchSoundToPhone();
                            break;
                        // MCU开机的话,假如当前声道在手机,并且之前是由车机切换去手机的,则切换去车机 
                        case DeviceState.MCU_STATE_START:
                            checkIsNeedToSwitchSoundToCar();
                            break;
                    }
                }
                else if (KernelConstant.ACTION_BACKCAR_STATE_CHANGE.equals(action))
                {
                    int backCarState =
                            SettingTableKey
                                    .getIntValue(getActivity().getContentResolver(), PublicSettingTable.BACKCAR_STATE);
                    // 开始倒车的时候,假如当前声道在车机,则切换去手机 
                    if (backCarState == DeviceState.BACKCAR_STATE_ON)
                    {
                        checkIsNeedToSwitchSoundToPhone();
                    }
                    else
                    {
                        // 倒车结束的时候,假如当前声道在手机,并且之前是由车机切换去手机的,则切换去车机 
                        checkIsNeedToSwitchSoundToCar();
                    }
                }
            }
        }
    };

    private BroadcastReceiver McuKeyValueReciever = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (KernelConstant.ACTION_KEY_VALUE.equals(action))
            {
                byte keyValue = intent.getByteExtra(KernelConstant.EXTRA_KEY_VALUE, (byte) 0x00);
                switch (keyValue)
                {
                    case KernelConstant.McuKeyValue.CANCEL_CALL:
                        mImageButtonHangup.performClick();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    /**
     * 检查是否需要把声道切换去手机
     */
    private void checkIsNeedToSwitchSoundToPhone()
    {
        if (!mIsPhoneSound)
        {
            mImageButtonSwitch.performClick();
            isSoundAtCar = false;
            LOG.print("need to switch sound to phone");
        }
        else
        {
            LOG.print("current sound channel not at car.so not to change sound channel");
        }
    }

    /**
     * 检查是否需要把声道切换去车机
     */
    private void checkIsNeedToSwitchSoundToCar()
    {
        if (!isSoundAtCar && mIsPhoneSound)
        {
            mImageButtonSwitch.performClick();
            isSoundAtCar = true;
            LOG.print("need to switch sound to car");
        }
        else
        {
            LOG.print("current sound channel not at phone.so not to change sound channel");
        }
    }
    
    protected abstract View onCreateViewBtCallOnLineDialog();
    protected abstract Dialog onCreateDialogFromDialogStyle();
    protected abstract View onCreateViewCallOnLineLayout();
}
