package com.ls.bt.bc03.feature.atpound;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.utils.log.JLog;
import com.android.utils.uart.UartCommunication;
import com.android.utils.uart.UartWorker;
import com.android.utils.uart.UartWorker.OnProtocalDistributeListener;
import com.ls.bt.bc03.BC03BTConstant;
import com.ls.bt.bc03.BC03BTConstant.AutoFeature;
import com.ls.bt.bc03.BC03BTConstant.HFPStatus;
import com.ls.bt.bc03.BC03BTConstant.ListType;
import com.ls.bt.bc03.BC03BTConstant.PhoneBookType;
import com.ls.bt.bc03.BC03BTConstant.PowerStatus;
import com.ls.bt.bc03.BC03BTConstant.SoundChannel;
import com.ls.bt.bc03.feature.IBC03Callback;
import com.ls.bt.bc03.feature.IBC03Feature;
import com.nwd.SerialPort;
import com.nwd.kernel.utils.KernelConstant;

/**
 * 蓝牙模块功能
 * @author Administrator
 *
 */
public class AtPoundBC03Feature implements IBC03Feature , IAtPoundCallback
{
	
	private static final String TAG = "AtPoundBC03Feature";
    private static final JLog LOG = new JLog(TAG, true, JLog.TYPE_INFO);
    
    private UartCommunication mUartCommunication;
    private Context mContext;
    private IBC03Callback mBC03Callback;
    private UartWorker mUartWorker;
    
    /**当前要获取的电话本类型*/
    private int mCurrentRequestPhoneBookType;
    /**是否可以获取电话本信息*/
    private boolean isCanGetPhoneBookData;
    private int mRequestGetPhoneBookCount = 9999;
    private int mHaveGetPhoneBookCount;
    
    private static final String CONFIG_BT_UART = "key_bt_uart";
    private static final String CONFIG_BT_UART_BITRATE = "key_bt_uart_bitrate";
    /**client require this dir*/
    private final String PATH_CLIENT = "/system/UartConfig.int";
    
    /**蓝牙名称及pin码*/
    private String mBlueName = "CAR-", mPinCode = "0000";
    
    private SharedPreferences mSharePref = null;
    
    private final String XML_NAME = "bc03_at";
    /**蓝牙名称及pin码存放的key*/
    private final String BLU_NAME = "key_name", PIN_CODE = "key_pin";
    /**自动接听,自动连接**/
    private final String AUTO_ACCEPT = "auto_accept", AUTO_CONNECT = "auto_connect";
    /**是否自动接听，自动连接***/
    private boolean isAutoAccept = false, isAutoConnect = false;
    /**自动接听**/
    private final int MSG_AUTO_ACCEPT = 1000;
    /**自动连接**/
    private final int MSG_AUTO_CONNECT = 1001;
    /**重新连接**/
    private final int MSG_REPEAT = MSG_AUTO_CONNECT + 1;
    /**是否在通话中**/
    private boolean isCalling = false;
    
    public static final String CATION_CUSTOM_UPDATE ="cn.yunzhisheng.intent.action.custom.order.contact";
    
    private final Handler mHandle = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
	    		case MSG_AUTO_ACCEPT:
	    			if (!isCalling)
	    				//接听来电
	    				AtPoundProtocalUtil.requestAcceptCall(mUartCommunication);
	    			break;
	    		case MSG_AUTO_CONNECT:
	    				//连接HPF
	    			    AtPoundProtocalUtil.requestConnectHFP(mUartCommunication, 0);
	    			break;
	    		case MSG_REPEAT:
	    			if (isPAFail) {
	    				//读取SIM卡联系人
	    				AtPoundProtocalUtil.requestGetContactFromSim(mUartCommunication);
	    			}
	    			break;
    		}
    	}
    };
    
    public AtPoundBC03Feature(Context context,IBC03Callback callback)
    {
        Properties properties = new Properties();
        try
        {
             properties.load(new FileInputStream(KernelConstant.UART_CONFIG));// default
        }
        catch (Exception e)
        {
        	LOG.print("===properties.load exception");
        }
        String uart = properties.getProperty(CONFIG_BT_UART);
        int bitrate = 460800;
        if(properties.containsKey(CONFIG_BT_UART_BITRATE))
        {
            bitrate = Integer.parseInt(properties.getProperty(CONFIG_BT_UART_BITRATE));
        }
        if(uart == null)
        {
            //uart = "/dev/ttyMT1";
            uart = "/dev/goc_serial";
        }
        Log.i(TAG, "===uart==" + uart + ";bitrate==" + bitrate);
    	mUartWorker = new UartWorker(uart, bitrate,SerialPort.FLAG_BLOCK, new AtPoundProtocal()); //For AT# 文强BC06
    	//mUartWorker = new UartWorker("/dev/ttyS0", 9600, new AtPoundProtocal()); //FOR AT# BC03
    	//mUartWorker = new UartWorker("/dev/tcc-uart1", 9600, new AtPoundProtocal());
    	
        mUartWorker.setProtocalDistributeListener(mOnProtocalDistributeListener);
        
        mUartCommunication = mUartWorker.getWriter();
        
        mContext = context;
        mBC03Callback = callback;
        mUartWorker.startCommunicate();
        
        mSharePref = mContext.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        String name = mSharePref.getString(BLU_NAME, null);
        if (null == name) {
        	Random ran=new Random();
        	int i=ran.nextInt(99999);
        	String str="";
        	if(i<10){
        		str="0000"+i;
        	}
        	else if(i<100){
        		str="000"+i;
        	}
        	else if(i<1000){
        		str="00"+i;
        	}
        	else if(i<10000){
        		str="0"+i;
        	}
        	else 
        		str=""+i;
        	mBlueName=mBlueName+str;
        	setDeviceName(mBlueName);
        	setPinCode(mPinCode);
        }
        else
        {
        	mBlueName = name;
        	mPinCode = mSharePref.getString(PIN_CODE, null);
        }
        
        isAutoAccept = mSharePref.getBoolean(AUTO_ACCEPT, false);
        mBC03Callback.onAutoFeatureChange(BC03BTConstant.AutoFeature.AUTO_ACCEPT_CALL, isAutoAccept);
        
        isAutoConnect = mSharePref.getBoolean(AUTO_CONNECT, false);
        mBC03Callback.onAutoFeatureChange(BC03BTConstant.AutoFeature.AUTO_CONNECT_DEVICE, isAutoConnect);
        resetPhoneBookStatus();
    }
    
    /**
     * 重置下载电话本状态
     */
    private void resetPhoneBookStatus()
    {
        mCurrentRequestPhoneBookType = -1;
        isCanGetPhoneBookData = true;
    }
    
    @Override
    public void receive(byte[] protocal)
    {
        AtPoundProtocalUtil.receive(protocal, mBC03Callback, this);
    }

    @Override
    public void release()
    {
        mContext = null;
        mUartCommunication = null;
        mBC03Callback = null;
        if(mUartWorker != null)
        {
            mUartWorker.release();
            mUartWorker = null;
        } 
    }

    @Override
    public void sendATCommand(String command)
    {
        AtPoundProtocalUtil.requestSendAtCommand(mUartCommunication, command);
    }

    @Override
    public void querySoftwareVersion()
    {
        AtPoundProtocalUtil.requestQuerySoftwareVersion(mUartCommunication);
    }

    @Override
    public void queryDeviceName()
    {
    	mBC03Callback.onGetDeviceName(mBlueName);
    }

    @Override
    public void setDeviceName(String name)
    {
    	if (!TextUtils.isEmpty(name)) {
	    	mBlueName = name;
	        AtPoundProtocalUtil.requestSetDeviceName(mUartCommunication, name);
	        Editor e = mSharePref.edit();
	    	e.putString(BLU_NAME, name);
	    	e.commit();
	    	e = null;
    	}
    }

    @Override
    public void queryPinCode()
    {
    	mBC03Callback.onGetPinCode(mPinCode);
    }

    @Override
    public void setPinCode(String pinCode)
    {
    	if (!TextUtils.isEmpty(pinCode)) {
	    	mPinCode = pinCode;
	    	AtPoundProtocalUtil.requestSetPinCode(mUartCommunication, pinCode);
	    	Editor e = mSharePref.edit();
	    	e.putString(PIN_CODE, pinCode);
	    	e.commit();
	    	e = null;
    	}
    }

    @Override
    public void startDiscover()
    {
        // AtPoundProtocalUtil.requestStartDiscover(mUartCommunication);
    }

    @Override
    public void stopDiscover()
    {
        AtPoundProtocalUtil.requestStopDiscover(mUartCommunication);
    }

    @Override
    public void queryPairDeviceList()
    {
        AtPoundProtocalUtil.requestQueryPairDeviceList(mUartCommunication);
    }

    @Override
    public void queryConnectDeviceList()
    {
        AtPoundProtocalUtil.requestQueryConnectDeivce(mUartCommunication);
    }

    @Override
    public void queryPowerState()
    {
        AtPoundProtocalUtil.requestQueryPowerStatus(mUartCommunication);
        mBC03Callback.onGetPowerState(BC03BTConstant.PowerStatus.ON);
    }

    @Override
    public void queryPairState()
    {

    }

    @Override
    public void queryA2dpState()
    {
        // TODO Auto-generated method stub
        //AtPoundProtocalUtil.requestQueryA2DPStatus(mUartCommunication);
    }

    @Override
    public void queryAVRCPState()
    {
        // TODO Auto-generated method stub
        //AtPoundProtocalUtil.requestQueryAVRCPStatus(mUartCommunication);
    }

    @Override
    public void queryHFPState()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestQueryHFPStatus(mUartCommunication);
    }

    @Override
    public void queryConnectProfileState()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void querySCOState()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void queryMuteState()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void connectLastA2dpDevice()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectA2dpDeviceFromList(int index, int listType)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disconnectA2dpDevice()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestDisconnectA2DP(mUartCommunication);
    }

    @Override
    public void connectLastHFPDevice()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectHFPDeviceFromList(int index, int listType)
    {
        // TODO Auto-generated method stub
        switch(listType)
        {
            case ListType.PAIR_LIST:
                AtPoundProtocalUtil.requestConnectHFP(mUartCommunication,index);
                break;
        }
    }

    @Override
    public void disconnectHFPDevice()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestDisconnectHFP(mUartCommunication);
    }

    @Override
    public void setSpkVolume(int volume)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void querySpkVolume()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void playControl(int command)
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestPlayControl(mUartCommunication, command);
    }
    
   

    @Override
    public void dial(String number)
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestDial(mUartCommunication, number);
    }

    @Override
    public void acceptCall()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestAcceptCall(mUartCommunication);
    }

    @Override
    public void rejectCall()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestRejectCall(mUartCommunication);
    }

    @Override
    public void handUpCall()
    {
        // TODO Auto-generated method stub
//    	if (isCalling)
    		AtPoundProtocalUtil.requestCancelCall(mUartCommunication);
//    	else
    		AtPoundProtocalUtil.requestRejectCall(mUartCommunication);
    }

    @Override
    public void sendDTMF(String number)
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestSendDTMF(mUartCommunication, number);
    }

    @Override
    public void changeSoundChannel(int channel)
    {
        // TODO Auto-generated method stub
        switch(channel)
        {
            case SoundChannel.CHANNEL_ANOTHER:
            case SoundChannel.CHANNEL_AG:
            	AtPoundProtocalUtil.requestChangeSoundChannelToBT(mUartCommunication);
            case SoundChannel.CHANNEL_HFP:
                AtPoundProtocalUtil.requestChangeSoundChannel(mUartCommunication);
                break;
        }
    }

    @Override
    public void rejectWaittingCall()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestRejectWaittingCall(mUartCommunication);
    }

    @Override
    public void releaseActiveCallAndAcceptOtherCall()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestReleaseActiveCallAndAcceptWaittingCall(mUartCommunication);
    }

    @Override
    public void holdActiveCallAndAcceptOtherCall()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestHoldActiveCallAndAcceptWaittingCall(mUartCommunication);
    }

    @Override
    public void addHoldCall2Conversation()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestThirdCommunicate(mUartCommunication);
    }

    @Override
    public void setMute(boolean isMute)
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestSpkMuteOrUnmute(mUartCommunication);
    }

    /**若PA指令发了超了4秒还没有返回则重发*/
    private boolean isPAFail = false;
    /**
     * 设置要获取的电话本类型
     * @param phoneBookType
     */
    private void setPhoneBookType(int phoneBookType)
    {
    	Log.i("cmd", "获取电话本的类型："+phoneBookType);
    	
        if(mCurrentRequestPhoneBookType != phoneBookType)
        {
            mCurrentRequestPhoneBookType = phoneBookType;
            isCanGetPhoneBookData = false;
            mHaveGetPhoneBookCount = 0;
            switch(phoneBookType)
            {
                case PhoneBookType.DIAL_CALL: //2
                    AtPoundProtocalUtil.requestGetDialCallLog(mUartCommunication);
                    break;
                case PhoneBookType.MISS_CALL: //0
                    AtPoundProtocalUtil.requestGetMissCallLog(mUartCommunication);
                    break;
                case PhoneBookType.RECEIVE_CALL: //1
                    AtPoundProtocalUtil.requestGetReceiveCallLog(mUartCommunication);
                    break;
                case PhoneBookType.PHONE_CONTACT:   //3            	
                	AtPoundProtocalUtil.requestGetContactFromPhone(mUartCommunication);
                	isPAFail = true;
                	Message msg = mHandle.obtainMessage(MSG_REPEAT);
                    mHandle.sendMessageDelayed(msg, 4200);
                    break;
                case PhoneBookType.SIM_CONTACT:  //4
                    AtPoundProtocalUtil.requestGetContactFromSim(mUartCommunication);
                    isPAFail = true;
                    msg = mHandle.obtainMessage(MSG_REPEAT);
                    mHandle.sendMessageDelayed(msg, 4200);
                    break;
            }
        }        
    }
    
    
    /**
     * 下载电话本数据
     * @param index
     * @param count
     */
    private void downloadPhoneBookData(int index,int count)
    {
        if(isCanGetPhoneBookData)
        {
            AtPoundProtocalUtil.requestGetNextRecord(mUartCommunication, count);
        }
    }
    
    /**
     * 下载所有的电话本数据
     * @param index
     * @param count
     */
    private void downloadPhoneBookDataAll()
    {
        if(isCanGetPhoneBookData)
        {
            AtPoundProtocalUtil.requestGetAllPb(mUartCommunication);
        }
    }
    
    @Override
    public void getPhoneBook(int phoneBookType, int index, int count)
    {
    	Log.d("book", "AtPoundBC03Feature 实现接口 ————> IBC03Feature getPhoneBook ");
    	mHaveGetPhoneBookCount = 0;
    	mRequestGetPhoneBookCount = 9999;
        setPhoneBookType(phoneBookType);
    }

    @Override
    public void setPower(boolean isOn)
    {
        AtPoundProtocalUtil.requestSetPowerStatus(mUartCommunication, isOn ? PowerStatus.ON : PowerStatus.OFF);
    }

    @Override
    public void reset()
    {
        AtPoundProtocalUtil.requestReset(mUartCommunication);
    }

    @Override
    public void setDiscoverable(int timeout)
    {
        if(timeout > 0)
        {
            // AtPoundProtocalUtil.requestStartDiscover(mUartCommunication);
        }
        else
        {
            // AtPoundProtocalUtil.requestStopDiscover(mUartCommunication);
        }
    }

    @Override
    public void setCallEffect(int level)
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestSetCallEffect(mUartCommunication, level);
    }

    @Override
    public void queryCallEffect()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestQueryCallEffect(mUartCommunication);
    }

    @Override
    public void setPIO4Level(int level)
    {
    }

    @Override
    public void queryPIO4Level()
    {

    }

    @Override
    public void queryModuleVersion()
    {

    }

    @Override
    public void setAutoFeature(int featureType, boolean isOn)
    {
        // TODO Auto-generated method stub
        switch(featureType)
        {
            case AutoFeature.AUTO_ACCEPT_CALL:
                // AtPoundProtocalUtil.requestSetAutoAcceptCall(mUartCommunication, isOn);
                Editor e = mSharePref.edit();
                isAutoAccept = isOn;
    	    	e.putBoolean(AUTO_ACCEPT, isOn);
    	    	e.commit();
    	    	e = null;
                break;
            case AutoFeature.AUTO_CONNECT_DEVICE:
                // AtPoundProtocalUtil.requestSetAutoConnectDevice(mUartCommunication, isOn);
            	isAutoConnect = isOn;
                e = mSharePref.edit();
    	    	e.putBoolean(AUTO_CONNECT, isOn);
    	    	e.commit();
    	    	e = null;
                break;
        }
        
    }

    @Override
    public void onSetPhonebookStatus(boolean isOk)
    {
        // TODO Auto-generated method stub
        isCanGetPhoneBookData = isOk;
        if(isCanGetPhoneBookData)
        {
        	isPAFail = false;
            //设置电话本成功,则去获取数据 @fanjc {
            // downloadPhoneBookData(0, mRequestGetPhoneBookCount);
        	/// }
        	// added by fanjc 2015-05-01
            downloadPhoneBookDataAll();
        }
        else
        {
            //设置不成功,则通知获取结束
            mBC03Callback.onGetPhoneBookFinish();
            isPAFail = false;
        }
    }

    private OnProtocalDistributeListener mOnProtocalDistributeListener = new OnProtocalDistributeListener()
    {
        
        @Override
        public void onDistribution(byte[] protocal)
        {
            // TODO Auto-generated method stub
            receive(protocal);
        }
    };

    @Override
    public void onGetPhoneBook(String name, String number)
    {
        mHaveGetPhoneBookCount++;
        LOG.print("mHaveGetPhoneBookCount = " + mHaveGetPhoneBookCount + ",mRequestGetPhoneBookCount = " + mRequestGetPhoneBookCount);
        //获取满需要的数据量,则通知callback
        if(mHaveGetPhoneBookCount % mRequestGetPhoneBookCount == 0)
        {
            mBC03Callback.onGetPhoneBookFinish();
            isPAFail = false;
        }
    }

    @Override
    public void queryAutoFeature()
    {
        // TODO Auto-generated method stub
    	mBC03Callback.onAutoFeatureChange(BC03BTConstant.AutoFeature.AUTO_ACCEPT_CALL, isAutoAccept);
        mBC03Callback.onAutoFeatureChange(BC03BTConstant.AutoFeature.AUTO_CONNECT_DEVICE, isAutoConnect);
        // AtPoundProtocalUtil.requestQueryAutoFeature(mUartCommunication);
    }

    @Override
    public void setBtMusicMute(boolean isMute)
    {
        // TODO Auto-generated method stub
        if(isMute)
        {
            AtPoundProtocalUtil.requestBtMusicMute(mUartCommunication);
        }
        else
        {
            AtPoundProtocalUtil.requestBtMusicUnMute(mUartCommunication);
        }
    }

    @Override
    public void onHfpRelease()
    {
        // TODO Auto-generated method stub
//    	Intent intent=new Intent();
//    	intent.setAction(CATION_CUSTOM_UPDATE);
//    	intent.putExtra("flag", 3);
//    	mContext.sendBroadcast(intent);
//    	Log.d("mg", "发送了删除广播");
        resetPhoneBookStatus();
    }

    @Override
    public void requestUpdate()
    {
        // TODO Auto-generated method stub
        AtPoundProtocalUtil.requestUpdate(mUartCommunication);
    }

	@Override
	public void onHfpConnected(String profile) {
		// TODO Auto-generated method stub
		mBC03Callback.onGetConnectedDevice(profile, mBlueName, "");
	}

	@Override
	public void onCallEnd() {
		// TODO Auto-generated method stub
		isCalling = false;
		hasCallIn = false;
	}

	@Override
	public void onCalling() {
		// TODO Auto-generated method stub
		isCalling = true;
		hasCallIn = false;
	}

	private boolean hasCallIn = false;
	@Override
	public void onCallIn() {
		// TODO Auto-generated method stub
		if (!hasCallIn && isAutoAccept)
			mHandle.sendEmptyMessageDelayed(MSG_AUTO_ACCEPT, 6000);
		hasCallIn = true;
	}
	@Override
	public void onCallNum(String num) {
		if (!hasCallIn) {
			mBC03Callback.onGetHFPState(HFPStatus.CALL_INCOMING);
			mBC03Callback.onGetIncommingOrOutGoingCallNumber(num);
		}
		if (!hasCallIn && isAutoAccept)
			mHandle.sendEmptyMessageDelayed(MSG_AUTO_ACCEPT, 6000);
	}

	 /**
     * 请求蓝牙音乐声音值
     */
    @Override
	public void requestBtMusicVolume() {
		// TODO Auto-generated method stub
    	AtPoundProtocalUtil.requestBtMusicVolume(mUartCommunication);
    	Log.i("dp", "AtPoundBC03Feature  requestBtMusicVolume");
	}
    
	@Override
	public void setBtMusicVolume(int vol) {
		// TODO Auto-generated method stub
		Log.i("dp", "AtPoundBC03Feature  setBtMusicVolume");
		AtPoundProtocalUtil.setMusicVolume(mUartCommunication, vol);
	}

	
}
