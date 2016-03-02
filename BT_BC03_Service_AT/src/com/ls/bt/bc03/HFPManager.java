package com.ls.bt.bc03;

import android.content.Context;
import android.util.Log;

import com.android.utils.log.JLog;
import com.bt.BTConstant;
import com.bt.BTDefineSender;
import com.ls.bt.bc03.BC03BTConstant.HFPStatus;
import com.ls.bt.bc03.BC03BTConstant.ListType;
import com.ls.bt.bc03.BC03BTConstant.SoundChannel;
import com.ls.bt.bc03.feature.IBC03Feature;
import com.ls.bt.bc03.feature.IBC03HFPCallback;

public class HFPManager implements IBC03HFPCallback
{
    private static final JLog LOG = new JLog("HFPManager", true, JLog.TYPE_DEBUG);
    /** 当前声道是否在车机 */
    private int mCurrentSoundChannel = BTConstant.SOUND_CHANNEL_CAR;

    /** 通话状态 */
    public static enum CallState
    {
        /** 通话结束 */
        END_CALL,
        /** 通话中 */
        CALL_ONLINE,
        /** 接入 */
        CALL_IN,
        /** 拨出 */
        CALL_OUT,
    };

    /** 当前通话状态 */
    private CallState mCallState;

    /** 来电或去电号码 */
    private String mCallNumber;

    /** 蓝牙功能对象 */
    private IBC03Feature mBtFeature;
    private Context mContext;
    /**HFP连接状态*/
    private int mHFPStatus;
    /**HFP音量*/
    private int mHFPVolume;
    
    private PhoneBookManager mPhoneBookManager;
    
    public HFPManager(Context context,IBC03Feature btFeature)
    {
        mContext = context;
        mBtFeature = btFeature;
        mCallState = CallState.END_CALL;
    }
    
    public void setPhoneBookManager(PhoneBookManager phoneBookManager)
    {
        mPhoneBookManager = phoneBookManager;
    }
    
    public void release()
    {
        mContext = null;
        mBtFeature = null;
    }
    
    /**
     * 断开连接 
     */
    public void disconnect()
    {
        if(isConnected())
        {
            mBtFeature.disconnectHFPDevice();
        }
    }
    
    /**
     * 连接设备
     * @param listType
     * @param index
     */
    public void connectDevice(int listType,int index)
    {
        if(isConnected()) return;
        switch(listType)
        {
            case ListType.LAST_USED:
                mBtFeature.connectLastHFPDevice();
                break;
                default:
                    mBtFeature.connectHFPDeviceFromList(index, listType);
                    break;
        }
    }
    
    /**
     * 是否已经连接
     * @return
     */
    public boolean isConnected()
    {
        return mHFPStatus >= HFPStatus.CONNECTED;
    }
    
    /**
     * 获取通话状态
     * @return
     */
    public CallState getCallState()
    {
        return mCallState;
    }
    
    /**
     * 挂断/拒接电话 
     */
    public void cancelCall()
    {
        LOG.print("mCallState = " + mCallState);
        switch(mCallState)
        {
        case CALL_IN:
            mBtFeature.rejectCall();
            break;
        case CALL_ONLINE:
        case CALL_OUT:
            mBtFeature.handUpCall();
            break;
        default:
            mBtFeature.rejectCall();
            break;
        }
    }
    
    /**
     * 接听电话 
     */
    public void acceptCall()
    {
        mBtFeature.acceptCall();
    }
    
    /**
     * 拨号
     * @param number
     */
    public void dial(String number)
    {
        mBtFeature.dial(number);
    }
    
    /**
     * 发送DTMF按键
     * @param code
     */
    public void sendDTMF(char code)
    {
        mBtFeature.sendDTMF(String.valueOf(code));
    }
    
    /**
     * 声道切换
     * @return
     */
    public int changeSoundChannel()
    {
//        mBtFeature.changeSoundChannel(SoundChannel.CHANNEL_ANOTHER);
        if(mCurrentSoundChannel == BTConstant.SOUND_CHANNEL_CAR)
        {
            mCurrentSoundChannel = BTConstant.SOUND_CHANNEL_PHONE;
            mBtFeature.changeSoundChannel(SoundChannel.CHANNEL_AG);
        }
        else
        {
            mCurrentSoundChannel = BTConstant.SOUND_CHANNEL_CAR;
            mBtFeature.changeSoundChannel(SoundChannel.CHANNEL_HFP);
        }
        return mCurrentSoundChannel;
    }
    
    /**
     * 是否处于挂断电话状态
     * @return
     */
    public boolean isEndCallState()
    {
        return mCallState == CallState.END_CALL;
    }
   
    private void reset()
    {
        mCallState = CallState.END_CALL;
    }
    
    /**
     * 通话结束 
     */
    private void endCall()
    {
        mCallNumber = null;
        LOG.print("onCallHangup mCallState = " + mCallState);
        CallState lastCallState = mCallState;
        mCallState = CallState.END_CALL;
        if(lastCallState != mCallState)
        {
            BTDefineSender.sendBTEndCall(mContext);
        }
    }

    @Override
    public void onMultipulCall_NewIncommingCall(String number)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMultipulCall_NewOutGoingCall(String number)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMultipulCall_HoldCall(String number)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMultipulCall_CancelCall()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMultipulCall_CancelHoldCall()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMultipulCall_AcceptWaittingCallAndCancelCurrentCall()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMultipulCall_EnterThirdCalling()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCallOnline()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCancelCall()
    {
        // TODO Auto-generated method stub
        endCall();
    }

    @Override
    public void onHTPRelease()
    {
        // TODO Auto-generated method stub
        BTDefineSender.sendBTHFPRelease(mContext);
        reset();
    }

    @Override
    public void onRing()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGetIncommingOrOutGoingCallNumber(String number)
    {
        // TODO Auto-generated method stub
        mCallNumber = number;
        String name = null;
        if(mPhoneBookManager != null)
        {
            name = mPhoneBookManager.getNameFromContact(mCallNumber, null);
//            PhoneBookData phoneBook = mPhoneBookManager.findContactByPhoneNumber(mCallNumber);
//            if(phoneBook != null)
//            {
//                name = phoneBook.getName();
//            }
        }        
        
        if(mCallState == CallState.CALL_IN)
        {
            LOG.print("onGetIncommingCallNumber = " + number);
            BTDefineSender.sendBTIncomingCallNumber(mContext, number);
            if(name != null)
            {
                BTDefineSender.sendBTIncomingCallName(mContext, name);
            }
        }
        else if(mCallState == CallState.CALL_OUT)
        {
            LOG.print("onCallOutNumber = " + number);
            Log.i("call", "onCallOutNumber = " + number);
            BTDefineSender.sendBTOutGoingCallNumber(mContext, number);
            if(name != null)
            {
                BTDefineSender.sendBTOutGoingCallName(mContext, name);
            }
        }
        else
        {
            LOG.print("may be this is not incomming or outgoing call,because callState = " + mCallState);
        }        
    }

    @Override
    public void onGetIncommingCallName(String name)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGetWaittingCallNumber(String number)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGetHFPVolume(int volume)
    {
        // TODO Auto-generated method stub
        mHFPVolume = volume;
    }

    @Override
    public void onSendDTMF(String number)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGetHFPState(int state)
    {
        // TODO Auto-generated method stub
        int lastStatus = mHFPStatus;
        Log.i("call", "lastStatus="+lastStatus);
        switch(state)
        {
            case HFPStatus.READY:
                //ready代表连接已断开
                mHFPStatus = state;
                if(mHFPStatus != lastStatus)
                {
                    BTDefineSender.sendBTHFPRelease(mContext);
                }
                break;
            case HFPStatus.CONNECTING:
                mHFPStatus = state;
                break;
            case HFPStatus.CONNECTED:
                mHFPStatus = state;
                // connecting -> connected的时候.通知HFP连接上
                if(mHFPStatus != lastStatus)
                {
                    BTDefineSender.sendBTHFPEstablish(mContext);
                }
                // noted by fanjc 2015-06-13
                // endCall();//这种状态也代表着挂断
                break;
            case HFPStatus.CALL_INCOMING:
                if(mCallState == CallState.END_CALL)
                {
                    LOG.print("onIncommingCall");
                    mCallState = CallState.CALL_IN;
                    BTDefineSender.sendBTIncomingCall(mContext);
                    //假如有了来电号码.则一起发送过去
                    if(mCallNumber != null)
                    {
                        BTDefineSender.sendBTIncomingCallNumber(mContext, mCallNumber);
                    }
                }
                else
                {
                    LOG.print("may be this is not incomming call,because callState = " + mCallState);
                }                     
                break;
            case HFPStatus.CALL_OUTGOING:
            	Log.i("call", "发送蓝牙去电广播");
                if(mCallState == CallState.END_CALL)
                {
                    mCallState = CallState.CALL_OUT;
                    
                    BTDefineSender.sendBTOutGoingCall(mContext);
                    if(mCallNumber != null)
                    {
                        BTDefineSender.sendBTOutGoingCallNumber(mContext, mCallNumber);
                    }
                }
                else
                {
                    LOG.print("may be this is not out going call,because callState = " + mCallState);
                }                    
                break;
            case HFPStatus.CALL_ACTIVE:
                if(mCallState != CallState.CALL_ONLINE)
                {
                    LOG.print("onCallOnline");
                    Log.i("call", "正在通话广播");
                    mCallState = CallState.CALL_ONLINE;
                    BTDefineSender.sendBTBeginCallOnline(mContext);
                }
                else
                {
                    LOG.print("may be this is not calling online,because callState = " + mCallState);
                }                    
                break;
        }
        LOG.print("mCallState = " + mCallState);
    }

    @Override
    public void onHTPEstablish()
    {
        // TODO Auto-generated method stub
        BTDefineSender.sendBTHFPEstablish(mContext);
    }
}
