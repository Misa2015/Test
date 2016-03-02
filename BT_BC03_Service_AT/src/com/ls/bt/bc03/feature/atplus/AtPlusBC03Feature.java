package com.ls.bt.bc03.feature.atplus;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.utils.log.JLog;
import com.android.utils.uart.UartCommunication;
import com.android.utils.uart.UartWorker;
import com.android.utils.uart.UartWorker.OnProtocalDistributeListener;
import com.bt.BTConstant;
import com.ls.bt.bc03.BC03BTConstant;
import com.ls.bt.bc03.BTManager;
import com.ls.bt.bc03.BC03BTConstant.ListType;
import com.ls.bt.bc03.BC03BTConstant.MuteStatus;
import com.ls.bt.bc03.BC03BTConstant.SoundChannel;
import com.ls.bt.bc03.feature.IBC03Callback;
import com.ls.bt.bc03.feature.IBC03Feature;
import com.nwd.SerialPort;

public class AtPlusBC03Feature implements IBC03Feature
{
	private static final JLog LOG = new JLog("AtPlusBC03Feature", true, JLog.TYPE_DEBUG);
    private UartCommunication mUartCommunication;
    private Context mContext;
    private IBC03Callback mBC03Callback;
    private UartWorker mUartWorker;
    /**自动特性*/
    private SharedPreferences autoFeaturePreference = null;
    private static boolean isAutoAccept = false;
    private static boolean isAutoConnect = false;
    
    public AtPlusBC03Feature(Context context,IBC03Callback callback)
    {
        mUartWorker = new UartWorker("/dev/ttyS0", 115200,SerialPort.FLAG_BLOCK, new AtPlusProtocal());
        mUartWorker.setProtocalDistributeListener(mOnProtocalDistributeListener);
        mUartCommunication = mUartWorker.getWriter();
        mContext = context;
        mBC03Callback = callback;
        mUartWorker.startCommunicate();
        autoFeaturePreference = mContext.getSharedPreferences("auto_feature", 0);
        isAutoAccept = autoFeaturePreference.getBoolean("accpet_feature", false);
        isAutoConnect = autoFeaturePreference.getBoolean("connect_feature", false);
        LOG.print("auto feature accept="+isAutoAccept+" connect_feature="+isAutoConnect);
        mBC03Callback.onAutoFeatureChange(BC03BTConstant.AutoFeature.AUTO_ACCEPT_CALL, isAutoAccept);
        mBC03Callback.onAutoFeatureChange(BC03BTConstant.AutoFeature.AUTO_CONNECT_DEVICE, isAutoConnect);
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
    public void receive(byte[] protocal)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.receive(protocal, mBC03Callback);
    }    
    
    @Override
    public void querySoftwareVersion()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestQuerySofewareVersion(mUartCommunication);
    }

    @Override
    public void queryDeviceName()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetOrQueryDeviceName(mUartCommunication, null);
    }

    @Override
    public void setDeviceName(String name)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetOrQueryDeviceName(mUartCommunication, name);
    }

    @Override
    public void queryPinCode()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetOrQueryPinCode(mUartCommunication, null);
    }

    @Override
    public void setPinCode(String pinCode)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetOrQueryPinCode(mUartCommunication, pinCode);
    }

    @Override
    public void startDiscover()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSearchDevice(mUartCommunication, true);
    }

    @Override
    public void stopDiscover()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSearchDevice(mUartCommunication, false);
    }

    @Override
    public void queryPairDeviceList()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestListPairDevice(mUartCommunication);
    }

    @Override
    public void queryConnectDeviceList()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestListConnectDevice(mUartCommunication);
    }

    @Override
    public void queryPowerState()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestQueryPowerStatus(mUartCommunication);
    }

    @Override
    public void queryPairState()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetBlueToothStatus(mUartCommunication);
    }

    @Override
    public void queryA2dpState()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetBlueToothStatus(mUartCommunication);
    }

    @Override
    public void queryAVRCPState()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetBlueToothStatus(mUartCommunication);
    }

    @Override
    public void queryHFPState()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetBlueToothStatus(mUartCommunication);
    }

    @Override
    public void queryConnectProfileState()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetBlueToothStatus(mUartCommunication);
    }

    @Override
    public void querySCOState()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetBlueToothStatus(mUartCommunication);
    }

    @Override
    public void queryMuteState()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetBlueToothStatus(mUartCommunication);
    }

    @Override
    public void connectLastA2dpDevice()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestConnectA2DPDeviceFromLastUsed(mUartCommunication);
    }

    @Override
    public void connectA2dpDeviceFromList(int index, int listType)
    {
        // TODO Auto-generated method stub
        switch(listType)
        {
            case ListType.PAIR_LIST:
                AtPlusProtocalUtil.requestConnectA2DPFromPairList(mUartCommunication, index);
                break;
            case ListType.SEARCH_LIST:
                AtPlusProtocalUtil.requestConnectA2DPFromSearchList(mUartCommunication, index);
                break;
        }
    }

    @Override
    public void disconnectA2dpDevice()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestDisconnectA2DP(mUartCommunication);
    }

    @Override
    public void connectLastHFPDevice()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestConnectHFPDeviceFromLastUsed(mUartCommunication);
    }

    @Override
    public void connectHFPDeviceFromList(int index, int listType)
    {
        // TODO Auto-generated method stub
        switch(listType)
        {
            case ListType.PAIR_LIST:
                AtPlusProtocalUtil.requestConnectHFPDeviceFromPairList(mUartCommunication, index);
                break;
            case ListType.SEARCH_LIST:
                AtPlusProtocalUtil.requestConenctHFPDeviceFromSearchList(mUartCommunication, index);
                break;
        }
    }

    @Override
    public void disconnectHFPDevice()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestDisconnectHFPDevice(mUartCommunication);
    }

    @Override
    public void setSpkVolume(int volume)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetSpeakerVolume(mUartCommunication, volume);
    }

    @Override
    public void playControl(int command)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestPlayControl(mUartCommunication, command);
    }

    @Override
    public void dial(String number)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestDial(mUartCommunication, number);
    }

    @Override
    public void acceptCall()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestAnswerCall(mUartCommunication);
    }

    @Override
    public void rejectCall()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestRejectCall(mUartCommunication);
    }

    @Override
    public void handUpCall()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestHandUpCall(mUartCommunication);
    }

    @Override
    public void sendDTMF(String number)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSendDTMF(mUartCommunication, number);
    }

    @Override
    public void changeSoundChannel(int channel)
    {
        // TODO Auto-generated method stub
        switch(channel)
        {
            case SoundChannel.CHANNEL_AG:
                AtPlusProtocalUtil.requestTransferAudio2AG(mUartCommunication);
                break;
            case SoundChannel.CHANNEL_HFP:
                AtPlusProtocalUtil.requestTransferAudio2HFP(mUartCommunication);
                break;
            case SoundChannel.CHANNEL_ANOTHER:
                AtPlusProtocalUtil.requestTransferAudio2Another(mUartCommunication);
                break;
        }
    }

    @Override
    public void rejectWaittingCall()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestReleaseHoldingCallOrRejuctWaittingCall(mUartCommunication);
    }

    @Override
    public void releaseActiveCallAndAcceptOtherCall()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestReleaseActiveCallAndAcceptAnotherCall(mUartCommunication);
    }

    @Override
    public void holdActiveCallAndAcceptOtherCall()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestHoldActiveCallAndAcceptAnotherCall(mUartCommunication);
    }

    @Override
    public void addHoldCall2Conversation()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestAddHeldCall(mUartCommunication);
    }

    @Override
    public void setMute(boolean isMute)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetMuteStatus(mUartCommunication, isMute ? MuteStatus.MUTE : MuteStatus.UNMUTE);
    }

    @Override
    public void getPhoneBook(int phoneBookType, int index, int count)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetPhoneBookData(mUartCommunication, phoneBookType, index, count);
    }

    @Override
    public void setPower(boolean isOn)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetPowerStatus(mUartCommunication, isOn);
    }

    @Override
    public void reset()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestReset(mUartCommunication);
    }

    @Override
    public void setDiscoverable(int timeout)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestPair(mUartCommunication, timeout);
    }

    @Override
    public void setCallEffect(int level)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetCallEffect(mUartCommunication, level);
    }

    @Override
    public void queryCallEffect()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestQueryCallEffect(mUartCommunication);
    }

    @Override
    public void setPIO4Level(int level)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSetPIO4(mUartCommunication, level);
    }

    @Override
    public void queryModuleVersion()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestModule(mUartCommunication);
    }

    @Override
    public void querySpkVolume()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestGetSpeakerVolume(mUartCommunication);
    }

    @Override
    public void sendATCommand(String command)
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestSendAtCommand(mUartCommunication, command);
    }

    @Override
    public void queryPIO4Level()
    {
        // TODO Auto-generated method stub
        AtPlusProtocalUtil.requestQueryPIO4(mUartCommunication);
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
    public void setAutoFeature(int featureType, boolean isOn)
    {
    	switch(featureType)
        {
            case BC03BTConstant.AutoFeature.AUTO_ACCEPT_CALL:
            	LOG.print("accpet_feature="+isOn);
            	autoFeaturePreference.edit().putBoolean("accpet_feature", isOn).commit();
                break;
            case BC03BTConstant.AutoFeature.AUTO_CONNECT_DEVICE:
            	LOG.print("connect_feature="+isOn);
            	autoFeaturePreference.edit().putBoolean("connect_feature", isOn).commit();
                break;
        }
        // TODO Auto-generated method stub
        mBC03Callback.onAutoFeatureChange(featureType, isOn);
    }

    @Override
    public void queryAutoFeature()
    {
    	LOG.print("queryAutoFeature ="+isAutoConnect);
    	if(isAutoConnect)
    	{
    		connectLastA2dpDevice();
    	    connectLastHFPDevice();
    	}
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBtMusicMute(boolean isMute)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void requestUpdate()
    {
        // TODO Auto-generated method stub
        //文强专用,目前AT+不实现
    }

	@Override
	public void requestBtMusicVolume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBtMusicVolume(int vol) {
		// TODO Auto-generated method stub
		
	}

}
