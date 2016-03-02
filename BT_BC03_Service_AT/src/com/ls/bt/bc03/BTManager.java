package com.ls.bt.bc03;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.utils.log.JLog;
import com.bt.BTConstant;
import com.bt.BTConstant.AutoFeature;
import com.bt.BTDefine;
import com.bt.BTDefineSender;
import com.bt.BTDevice;
import com.bt.BTFeature;
import com.bt.BTPIMCallback;
import com.bt.hd.link.HIDKeyboardStatus;
import com.bt.hd.link.HIDMouseStatus;
import com.bt.hd.link.SppDataCallBack;
import com.ls.bt.bc03.BC03BTConstant.A2DPStatus;
import com.ls.bt.bc03.BC03BTConstant.HFPStatus;
import com.ls.bt.bc03.BC03BTConstant.ListType;
import com.ls.bt.bc03.BC03BTConstant.PowerStatus;
import com.ls.bt.bc03.BTService.DataChangeListener;
import com.ls.bt.bc03.feature.IBC03Callback;
import com.ls.bt.bc03.feature.IBC03Feature;
import com.ls.bt.bc03.feature.atplus.AtPlusBC03Feature;
import com.ls.bt.bc03.feature.atpound.AtPoundBC03Feature;
import com.nwd.kernel.utils.KernelUtils;


/**
 * 蓝牙功能实现，实现aidl中的方法
 */
public class BTManager extends BTFeature.Stub{
    private static final JLog LOG = new JLog("BTManager", true, JLog.TYPE_DEBUG);

    /**蓝牙功能对象*/
    private IBC03Feature mBtFeature;
    private Context mContext;
    
    /**已连接的蓝牙手提电话设备*/
    private BTDevice mConnectHFPRemoteDevice;
    /**已连接的A2DP设备*/
    private BTDevice mConnectA2DPRemoteDevice;
    /**本机设备*/
    private BTDevice mLocalDevice;
    /**配对用的pin码*/
    private String mPinCode;

    /**电话本管理器*/
    private PhoneBookManager mPhoneBookManager;
    /**蓝牙电话管理器*/
    private HFPManager mHFPManager;
    /**A2DP管理器*/
    private A2DPManager mA2DPManager;
    
    /**自动功能*/
    private int mAutoFeature;
    
    private DataChangeListener mDataChangeListener;
    
    /**是否连接设备*/
    private boolean isConnectDevice = false;
    
    /**文强服务启动消息*/
    private static final String ACTION_WENQIANG_SERVICE_START = "com.wenqiang.SERVICE_START";
    
    /**显示蓝牙模块升级成功提示*/
    private static final int MSG_SHOW_UPDATE_SUCCESS = 0;
    
    /**模块是否能用*/
    private boolean isModuleOk = false;
    
    public BTManager(Context context)
    {
        mContext = context;
        mBtFeature = new AtPoundBC03Feature(context, mBC03Callback); //AT#
        mPhoneBookManager = new PhoneBookManager(mContext, mBtFeature);
        mHFPManager = new HFPManager(mContext,mBtFeature);
        mA2DPManager = new A2DPManager(mContext, mBtFeature, mHFPManager);
        mLocalDevice = new BTDevice();
        
        try
        {
            IntentFilter filter = new IntentFilter(ACTION_WENQIANG_SERVICE_START);
            context.registerReceiver(mReceiver, filter);
            
            IntentFilter mountFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
            mountFilter.addDataScheme("file");
            context.registerReceiver(mMountReceiver, mountFilter);
        }
        catch (Exception e)
        {
        }
        initData();
        initOnce();
        reset();
    }

    /**
     * 只执行一次的初始化动作
     */
    private void initOnce()
    {
//        mBtFeature.setPower(true);//打开蓝牙
//        mBtFeature.querySoftwareVersion();//获取蓝牙版本号
//        mBtFeature.queryModuleVersion();
//        mBtFeature.queryPowerState();
//        mBtFeature.requestUpdate();//请求升级蓝牙模块
//        mBtFeature.setBtMusicMute(true);//设置蓝牙音乐静音
    }
    
    /**
     * 每次服务被连接时都会被调用的初始化动作 
     */
    protected void initData()
    {
        mBtFeature.queryAutoFeature();//查询自动功能
        mBtFeature.queryDeviceName();//获取设备名字
    	/// @fanjc {
//        mBtFeature.setDeviceName("fanjc");
//        mBtFeature.setPinCode("0000");
    	/// @fanjc }
        mBtFeature.queryPinCode();//获取pincode
        mBtFeature.queryHFPState();//查询HFP连接状态  CY
        mBtFeature.queryConnectDeviceList();//查询已连接设备
//        mBtFeature.queryA2dpState();//查询A2DP的连接状态
//        mBtFeature.queryAVRCPState();//查询AVRCP的连接状态
    }
    
    public void release()
    {
        try
        {
            mContext.unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        
        try
        {
            mContext.unregisterReceiver(mMountReceiver);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        
        mBtFeature = null;
        mContext = null;
        mConnectHFPRemoteDevice = null;
        mLocalDevice = null;
        reset();
        if(mPhoneBookManager != null)
        {
            mPhoneBookManager.release();
            mPhoneBookManager = null;
        }
        if(mHFPManager != null)
        {
            mHFPManager.release();
            mHFPManager = null;
        }
        if(mA2DPManager != null)
        {
            mA2DPManager.release();
            mA2DPManager = null;
        }
    }
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            switch(msg.what)
            {
                case MSG_SHOW_UPDATE_SUCCESS:
                    try
                    {
                        Toast.makeText(mContext, "蓝牙模块升级成功", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }                    
                    break;
            }
        }
        
    };
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            LOG.print("action = " + action);
            //文强服务启动后,再发送蓝牙音乐静音指令.
            if(ACTION_WENQIANG_SERVICE_START.equals(action))
            {
                mBtFeature.setBtMusicMute(true);//设置蓝牙音乐静音
            }
        }
    };
    
    private BroadcastReceiver mMountReceiver = new BroadcastReceiver()
    {
        private static final String SDCARD = "/mnt/card";
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            LOG.print("action = " + action);
            if(Intent.ACTION_MEDIA_MOUNTED.equals(action))
            {
                String path = intent.getData().getPath();
                if(path.equals(SDCARD))
                {
                    mBtFeature.requestUpdate();
                }
            }
        }
    };
    
    /**
     * 检查连接状态 
     */
    private void checkConnectState()
    {
        int connectEvent = BTDefine.VALUE_CONNECTION_EVENT_DISCONNECT;
        boolean lastIsConnectDevice = isConnectDevice;
//        if(mA2DPManager.isConnected() || mHFPManager.isConnected())
        if(mHFPManager.isConnected())//只有蓝牙电话服务连接
        {
            connectEvent = BTDefine.VALUE_CONNECTION_EVENT_CONNECT;
            isConnectDevice = true;
        }
        else
        {
            isConnectDevice = false;
            
        }
        LOG.print("lastIsConnectDevice = " + lastIsConnectDevice + ",isConnectDevice = " + isConnectDevice);
        if(lastIsConnectDevice != isConnectDevice)
        {
            if(mConnectHFPRemoteDevice == null)
            {
                BTDefineSender.sendBTConnectionChange(mContext, connectEvent, null , null);
            }
            else
            {
                BTDefineSender.sendBTConnectionChange(mContext, connectEvent, mConnectHFPRemoteDevice.getAddress() , mConnectHFPRemoteDevice.getName());                
            }
        }
        if(!isConnectDevice)
        {
        	reset();
        }
    }
    
    public void setDataChangeListener(DataChangeListener listener)
    {
        mDataChangeListener = listener;
        mPhoneBookManager.setDataChangeListener(mDataChangeListener);
    }
    
    public void receive(byte[] protocal)
    {
        mBtFeature.receive(protocal);
    }
    
    /**
     * 断开连接后的恢复操作
     */
    private void reset() {
        LOG.print("reset ****");
        if(mPhoneBookManager != null)
        {
            mPhoneBookManager.reset();
        }
        mConnectHFPRemoteDevice = null;
    }    
    
    /**
     * 设置自动开关功能
     * @param feature
     * @param isOn
     */
    private void setAutoFeature(int feature,boolean isOn,boolean isNotify)
    {
        if(isOn)
        {
            mAutoFeature |= feature;
        }
        else
        {
            mAutoFeature &= ~feature;
        }
        if(isNotify)
        {
            BTDefineSender.sendBTAutoFeatureChange(mContext, mAutoFeature);
        }
    }

    private IBC03Callback mBC03Callback = new IBC03Callback()
    {
        
        @Override
        public void onSendDTMF(String number)
        {
            // TODO Auto-generated method stub
            mHFPManager.onSendDTMF(number);
        }
        
        @Override
        public void onMultipulCall_NewOutGoingCall(String number)
        {
            // TODO Auto-generated method stub
            mHFPManager.onMultipulCall_NewOutGoingCall(number);
        }
        
        @Override
        public void onMultipulCall_NewIncommingCall(String number)
        {
            // TODO Auto-generated method stub
            mHFPManager.onMultipulCall_NewIncommingCall(number);
        }
        
        @Override
        public void onMultipulCall_HoldCall(String number)
        {
            // TODO Auto-generated method stub
            mHFPManager.onMultipulCall_HoldCall(number);
        }
        
        @Override
        public void onMultipulCall_EnterThirdCalling()
        {
            // TODO Auto-generated method stub
            mHFPManager.onMultipulCall_EnterThirdCalling();   
        }
        
        @Override
        public void onMultipulCall_CancelHoldCall()
        {
            // TODO Auto-generated method stub
            mHFPManager.onMultipulCall_CancelHoldCall();
        }
        
        @Override
        public void onMultipulCall_CancelCall()
        {
            // TODO Auto-generated method stub
            mHFPManager.onMultipulCall_CancelCall();
        }
        
        @Override
        public void onMultipulCall_AcceptWaittingCallAndCancelCurrentCall()
        {
            // TODO Auto-generated method stub
            mHFPManager.onMultipulCall_AcceptWaittingCallAndCancelCurrentCall();
        }
        
        @Override
        public void onRing()
        {
            // TODO Auto-generated method stub
            mHFPManager.onRing();
        }
        
        @Override
        public void onHTPRelease()
        {
            // TODO Auto-generated method stub
            mHFPManager.onHTPRelease();
            KernelUtils.setBtVolumeState(mContext, 0);//蓝牙断开的时候.通知蓝牙没声音 - modify by yewei
            reset();
        }
        
        @Override
        public void onGetWaittingCallNumber(String number)
        {
            // TODO Auto-generated method stub
            mHFPManager.onGetWaittingCallNumber(number);
        }
        
        @Override
        public void onGetSoftwareVersion(String version)
        {
            // TODO Auto-generated method stub
        }
        
        @Override
        public void onGetSearchDevice(int index, String address, String name)
        {
            // TODO Auto-generated method stub
            BTDefineSender.sendBTDeviceFound(mContext, address, name, 0, false);
        }
        
        @Override
        public void onGetSearchDeivceFinish()
        {
            // TODO Auto-generated method stub
            BTDefineSender.sendBTDiscoverFinish(mContext);
        }
        
        @Override
        public void onGetSCOState(int state)
        {
            // TODO Auto-generated method stub
        }
        
        @Override
        public void onGetPowerState(int state)
        {
            switch(state)
            {
                case PowerStatus.ON:
                    state = BTDefine.VALUE_OPEN;
                    break;
                case PowerStatus.OFF:
                    state = BTDefine.VALUE_CLOSE;
                    break;
                case PowerStatus.POWER_DOWN:
                    state = BTDefine.VALUE_CLOSEING;
                    break;
            }
            BTDefineSender.sendBTStateChange(mContext, state);
        }
        
        @Override
        public void onGetPinCode(String pinCode)
        {
            // TODO Auto-generated method stub
            mPinCode = pinCode;
        }
        
        @Override
        public void onGetPhoneBookFinish()
        {
            // TODO Auto-generated method stub
            mPhoneBookManager.onGetPhoneBookFinish();
        }
        
        @Override
        public void onGetPhoneBook(int index, String number, String name, String time)
        {
        	LOG.print("BTManager onGetPhoneBook >>>>>>>>>>number"+number);
            // TODO Auto-generated method stub
            mPhoneBookManager.onGetPhoneBook(index, number, name, time);
        }
        
        @Override
        public void onGetPairState(int state)
        {
            // TODO Auto-generated method stub
        }
        
        @Override
        public void onGetPairDeviceFinish()
        {
            // TODO Auto-generated method stub
        }
        
        @Override
        public void onGetPairDevice(int index, String address, String name)
        {
            // TODO Auto-generated method stub
            BTDevice recordDevice = new BTDevice(address, name);
            recordDevice.setPairRecordIndex(index);
            BTDefineSender.sendBTPairDeviceRecord(mContext, recordDevice);            
        }
        
        @Override
        public void onGetPIO4State(int state)
        {
            // TODO Auto-generated method stub
        }
        
        @Override
        public void onGetMuteState(int state)
        {
            // TODO Auto-generated method stub
        }
        
        @Override
        public void onGetModuleVersion(String version)
        {
            // TODO Auto-generated method stub
        }
        
        @Override
        public void onGetIncommingOrOutGoingCallNumber(String number)
        {
            // TODO Auto-generated method stub
            mHFPManager.onGetIncommingOrOutGoingCallNumber(number);
        }
        
        @Override
        public void onGetIncommingCallName(String name)
        {
            // TODO Auto-generated method stub
            mHFPManager.onGetIncommingCallName(name);
        }
        
        @Override
        public void onGetHFPVolume(int volume)
        {
            // TODO Auto-generated method stub
            mHFPManager.onGetHFPVolume(volume);
        }
        
        @Override
        public void onGetHFPState(int state)
        {
            // TODO Auto-generated method stub
            mHFPManager.onGetHFPState(state);
            if(state == HFPStatus.READY || state == HFPStatus.CONNECTED)
            {
                checkConnectState();
            }
        }
        
        @Override
        public void onGetDeviceName(String name)
        {
            // TODO Auto-generated method stub
            mLocalDevice.setName(name);
        }
        
        @Override
        public void onGetConnectedDeviceFinish()
        {
            // TODO Auto-generated method stub
            checkConnectState();
        }
        
        @Override
        public void onGetConnectedDevice(String profile, String name, String address)
        {
            // TODO Auto-generated method stub
            if("A2DP".equalsIgnoreCase(profile))
            {
                if(mConnectA2DPRemoteDevice == null)
                {
                    mConnectA2DPRemoteDevice = new BTDevice(address, name);
                }
            }
            else if("HFP".equalsIgnoreCase(profile))
            {
                if(mConnectHFPRemoteDevice == null)
                {
                    mConnectHFPRemoteDevice = new BTDevice(address, name);
                }
            }
        }
        
        @Override
        public void onGetConnectProfile(int profile)
        {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onGetCallEffectLevel(int level)
        {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onGetAVRCPState(int state)
        {
            // TODO Auto-generated method stub
            mA2DPManager.onGetAVRCPState(state);
        }
        
        @Override
        public void onGetA2dpVolume(int volume)
        {
            // TODO Auto-generated method stub
        	Log.i("dp", "BTManager  onGetA2dpVolume");
            mA2DPManager.onGetA2dpVolume(volume);
        }
        
        @Override
        public void onGetA2dpState(int state)
        {
            // TODO Auto-generated method stub
        	Log.i("dp", "BTManager  onGetA2dpState");
            mA2DPManager.onGetA2dpState(state);
            if(state == A2DPStatus.READY)
            {
                //A2DP断开了.这里检查一下连接情况
                checkConnectState();
            }
        }
        
        @Override
        public void onCancelCall()
        {
            // TODO Auto-generated method stub
            mHFPManager.onCancelCall();
        }
        
        @Override
        public void onCallOnline()
        {
            // TODO Auto-generated method stub
            mHFPManager.onCallOnline();
        }
        
        @Override
        public void onA2dpRelease()
        {
            // TODO Auto-generated method stub
            mA2DPManager.onA2dpRelease();
            mConnectA2DPRemoteDevice = null;
        }

        @Override
        public void onAutoFeatureChange(int featureType, boolean isOn)
        {
            // TODO Auto-generated method stub
            boolean isNotify = false;
            switch(featureType)
            {
                case BC03BTConstant.AutoFeature.AUTO_ACCEPT_CALL:
                    featureType = BTConstant.AutoFeature.FEATURE_AUTO_ACCEPT_CALL;
                    break;
                case BC03BTConstant.AutoFeature.AUTO_CONNECT_DEVICE:
                    featureType = BTConstant.AutoFeature.FEATURE_AUTO_CONNECT_DEVICE;
                    isNotify = true;
                    break;
            }
            setAutoFeature(featureType, isOn, isNotify);
        }

        @Override
        public void onA2dpEstablish()
        {
            // TODO Auto-generated method stub
            mA2DPManager.onA2dpEstablish();
        }

        @Override
        public void onHTPEstablish()
        {
            // TODO Auto-generated method stub
            mHFPManager.onHTPEstablish();
        }

		@Override
		public void onError() {
			// TODO Auto-generated method stub
			mPhoneBookManager.onError();
		}

        @Override
        public void onInitSuccess()
        {
            // TODO Auto-generated method stub
            isModuleOk = true;
        }

        @Override
        public void onUpdateSuccess()
        {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessage(MSG_SHOW_UPDATE_SUCCESS);
        }

		@Override
		public void requestBtMusicVolume() {
			// TODO Auto-generated method stub
			
		}
    };
    
    @Override
    public boolean startBlueTooth_BC() throws RemoteException {
        // TODO Auto-generated method stub
        mBtFeature.setPower(true);
        return true;
    }

    @Override
    public boolean stopBlueTooth_BC() throws RemoteException {
        // TODO Auto-generated method stub
        mBtFeature.setPower(false);
        return true;
    }

    @Override
    public boolean isBlueToothPowerOn() throws RemoteException {
        // TODO Auto-generated method stub
        return isModuleOk;
    }

    @Override
    public boolean connectDevice_BC(BTDevice device) throws RemoteException {
        // TODO Auto-generated method stub
        if(device == null)
        {
            //连接最后连接的设备
            mHFPManager.connectDevice(ListType.LAST_USED, 0);
            mA2DPManager.connectDevice(ListType.LAST_USED, 0);
        }
        else
        {
            //连接指定配对索引的设备
            int index = device.getPairRecordIndex();
            mHFPManager.connectDevice(ListType.PAIR_LIST, index);
            mA2DPManager.connectDevice(ListType.PAIR_LIST, index);
        }
        return true;
    }

    @Override
    public boolean disConnectDevice_BC() throws RemoteException {
        // TODO Auto-generated method stub
        mHFPManager.disconnect();
        mA2DPManager.disconnect();
        return true;
    }

    @Override
    public boolean isConnectDevice() throws RemoteException {
        // TODO Auto-generated method stub
        return mConnectHFPRemoteDevice != null;
    }

    @Override
    public boolean startSearchDevice_BC() throws RemoteException {
        // TODO Auto-generated method stub
        mBtFeature.startDiscover();
        return true;
    }

    @Override
    public boolean stopSearchDevice_BC() throws RemoteException {
        // TODO Auto-generated method stub
        mBtFeature.stopDiscover();
        return true;
    }

    @Override
    public boolean setLocalDeviceName(String name) throws RemoteException {
        // TODO Auto-generated method stub
        mLocalDevice.setName(name);
        mBtFeature.setDeviceName(name);
        // fanjc
        // mBtFeature.queryDeviceName();
        return true;
    }

    @Override
    public String getLocalDeviceName() throws RemoteException {
        // TODO Auto-generated method stub
        return mLocalDevice.getName();
    }

    @Override
    public void setPinCode(String pinCode) throws RemoteException {
        // TODO Auto-generated method stub
        mPinCode = pinCode;
        mBtFeature.setPinCode(mPinCode);
        // fanjc
        // mBtFeature.queryPinCode();
    }

    @Override
    public String getPinCode() throws RemoteException {
        // TODO Auto-generated method stub
        return mPinCode;
    }

    @Override
    public boolean dial_BC(String phoneNumber) throws RemoteException {
        // TODO Auto-generated method stub
        mHFPManager.dial(phoneNumber);
        return true;
    }

    @Override
    public boolean answerCall() throws RemoteException {
        // TODO Auto-generated method stub
        mHFPManager.acceptCall();
        return true;
    }

    @Override
    public boolean cancelCall() throws RemoteException {
        // TODO Auto-generated method stub
        mHFPManager.cancelCall();
        return true;
    }

    @Override
    public String getRemoteDeviceName(String address) throws RemoteException {
        // TODO Auto-generated method stub
        if(mConnectHFPRemoteDevice != null && mConnectHFPRemoteDevice.getAddress().equals(address))
        {
            return mConnectHFPRemoteDevice.getName();
        }
        return null;
    }

    @Override
    public int getRemoteDeviceClass(String address) throws RemoteException {
        // TODO Auto-generated method stub
        if(mConnectHFPRemoteDevice != null && mConnectHFPRemoteDevice.getAddress().equals(address))
        {
            return mConnectHFPRemoteDevice.getDeviceClass();
        }
        return 0;
    }

    @Override
    public boolean setDiscoverable(boolean isCanDiscover) throws RemoteException {
        // TODO Auto-generated method stub
        if(isCanDiscover)
        {
            //进入配对模式
            mBtFeature.setDiscoverable(65535);
        }
        else
        {
            //取消配对模式
            mBtFeature.setDiscoverable(0);
        }
        return true;
    }

    @Override
    public boolean setPairable(boolean isPairable) throws RemoteException {
        // TODO Auto-generated method stub
        if(isPairable)
        {
            //进入配对模式
            mBtFeature.setDiscoverable(65535);
        }
        else
        {
            //取消配对模式
            mBtFeature.setDiscoverable(0);
        }
        return false;
    }

    @Override
    public String getLocalDeviceAddress() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLocalDeviceFeature() throws RemoteException {
        // TODO Auto-generated method stub
        //BC03仅支持被动连接
        return BTConstant.DeviceFeature.PASSIVITY;
    }

    @Override
    public boolean sendDTMF(char code) throws RemoteException {
        // TODO Auto-generated method stub
        mHFPManager.sendDTMF(code);
        return true;
    }

    @Override
    public boolean playControl(int command) throws RemoteException {
        // TODO Auto-generated method stub
        mA2DPManager.playControl(command);
        return true;
    }

    @Override
    public boolean getContact_CB() throws RemoteException {
        
    	Log.i("book", "调用BTManager中的  getContact_CB 请求电话本");
 
    	if(isConnectDevice())
    	{
    		Log.i("book", "调用PhoneBookManager中的  getContact 请求电话本");
    		
    	    return mPhoneBookManager.getContact();
    	}
    	else
    	{
    	    return false;
    	}
    }

    @Override
    public boolean getCallLog_CB() throws RemoteException {
        
        if(isConnectDevice())
        {
            return mPhoneBookManager.getCallLog();
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean getSMS_CB() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int changeSoundChannel() throws RemoteException {
        // TODO Auto-generated method stub
        return mHFPManager.changeSoundChannel();
    }

    @Override
    public void registPIMCallback(BTPIMCallback callback) throws RemoteException {
        // TODO Auto-generated method stub
        if(mDataChangeListener != null)
        {
            mDataChangeListener.registPIMCallback(callback);
        }
    }

    @Override
    public void unregistPIMCallback(BTPIMCallback callback) throws RemoteException {
        // TODO Auto-generated method stub
        if(mDataChangeListener != null)
        {
            mDataChangeListener.unregistPIMCallback(callback);
        }
    }

    @Override
    public boolean isBTMusicPlaying() throws RemoteException {
        // TODO Auto-generated method stub
        return mA2DPManager.isMusicPlaying();
    }

    @Override
    public boolean setAutoFeature_BC(int feature,boolean isOn) throws RemoteException
    {
        // TODO Auto-generated method stub
//        setAutoFeature(feature, isOn);
        switch(feature)
        {
            case BTConstant.AutoFeature.FEATURE_AUTO_ACCEPT_CALL:
                feature = BC03BTConstant.AutoFeature.AUTO_ACCEPT_CALL;
                break;
            case BTConstant.AutoFeature.FEATURE_AUTO_CONNECT_DEVICE:
                feature = BC03BTConstant.AutoFeature.AUTO_CONNECT_DEVICE;
                break;
        }
        mBtFeature.setAutoFeature(feature, isOn);
        return true;
    }

    @Override
    public int getAutoFeature() throws RemoteException
    {
        // TODO Auto-generated method stub
        return mAutoFeature;
    }

    @Override
    public boolean getPairDeviceRecord_BC() throws RemoteException
    {
        // TODO Auto-generated method stub
        mBtFeature.queryPairDeviceList();
        return true;
    }

    @Override
    public boolean deletePairDeviceRecord(int index) throws RemoteException
    {
        // TODO Auto-generated method stub
        mBtFeature.reset();
        return false;
    }

    @Override
    public boolean isConnectHFP() throws RemoteException
    {
        // TODO Auto-generated method stub
        return mHFPManager.isConnected();
    }

    @Override
    public boolean isConnectA2DP() throws RemoteException
    {
        // TODO Auto-generated method stub
        return mA2DPManager.isConnected();
    }

    @Override
    public boolean isConnectAVRCP() throws RemoteException
    {
        // TODO Auto-generated method stub
        return mA2DPManager.isConnectAVRCP();
    }

    @Override
    public BTDevice getConnectDevice() throws RemoteException
    {
        // TODO Auto-generated method stub
        return mConnectHFPRemoteDevice;
    }

    @Override
    public void setBtMusicMute(boolean isMute) throws RemoteException
    {
        // TODO Auto-generated method stub
        mBtFeature.setBtMusicMute(isMute);
    }


    @Override
    public void HIDConnect(String btAddress) throws RemoteException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void HIDDisConnect() throws RemoteException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean SppConnect(String address) throws RemoteException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void SppDisConnect() throws RemoteException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void SppSendData(byte[] data) throws RemoteException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isHIDConnect() throws RemoteException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSppConnect() throws RemoteException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void registSppDataCallback(SppDataCallBack callback) throws RemoteException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void unregistSppDataCallback(SppDataCallBack callback) throws RemoteException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void HIDKeyboardInput(HIDKeyboardStatus key) throws RemoteException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void HIDMouseInput(HIDMouseStatus key) throws RemoteException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getCallState() throws RemoteException
    {
        // TODO Auto-generated method stub
        if(mHFPManager != null)
        {
            if(mHFPManager.getCallState() == HFPManager.CallState.CALL_OUT)
            {
                return BTConstant.CallState.CALL_OUT;
            }
            else if(mHFPManager.getCallState() == HFPManager.CallState.CALL_IN)
            {
                return BTConstant.CallState.CALL_IN;
            }
            else if(mHFPManager.getCallState() == HFPManager.CallState.CALL_ONLINE)
            {
                return BTConstant.CallState.CALL_ONLINE;
            }
        }
        return BTConstant.CallState.END_CALL;
    }

	@Override
	public void connectA2dp(String arg0) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnectA2dp() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxSpeakerVolume() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSpeakerVolume() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSpeakerVolume(int arg0) throws RemoteException {
		// TODO Auto-generated method stub
	}

	@Override
	public void installMhlSoft() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMute(boolean isMute) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 获取蓝牙音乐的声音值
	 */
	@Override
	public void getBtMusicVolume() throws RemoteException {
		// TODO Auto-generated method stub
		mA2DPManager.requestBtMusicVolume();
		
		Log.i("dp", "BTManager getBtMusicVolume");
	}

	/**
	 * 设置蓝牙音乐的声音值
	 */
	@Override
	public void setBtMusicVolume(int vol) throws RemoteException {
		// TODO Auto-generated method stub
		Log.i("dp", "BTManager  setBtMusicVolume");
		mA2DPManager.setBtMusicVolume(vol);
	}


}
