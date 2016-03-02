package com.ls.bt.bc03;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.utils.log.JLog;
import com.bt.BTConstant;
import com.bt.BTDefine;
import com.bt.BTDefineSender;
import com.ls.bt.bc03.BC03BTConstant.A2DPStatus;
import com.ls.bt.bc03.BC03BTConstant.AVRCPStatus;
import com.ls.bt.bc03.BC03BTConstant.ListType;
import com.ls.bt.bc03.BC03BTConstant.PlayCommand;
import com.ls.bt.bc03.feature.IBC03A2DPCallback;
import com.ls.bt.bc03.feature.IBC03Feature;

public class A2DPManager implements IBC03A2DPCallback
{
    private static final JLog LOG = new JLog("A2DPManager", true, JLog.TYPE_DEBUG);
    /** 蓝牙功能对象 */
    private IBC03Feature mBtFeature;
    private Context mContext;
    /** 蓝牙音乐是否播放中 */
    private boolean isBTMusicPlaying;
    /** A2DP状态 */
    private int mA2DPStatus;
    /** AVRCP状态 */
    private int mAVRCPStatus;
    private HFPManager mHFPManager;

    public A2DPManager(Context context, IBC03Feature btFeature, HFPManager hfpManager)
    {
        mContext = context;
        mBtFeature = btFeature;
        mHFPManager = hfpManager;
    }

    public void release()
    {
        mContext = null;
        mBtFeature = null;
        mHFPManager = null;
    }

    /**
     * 断开连接
     */
    public void disconnect()
    {
        if (isConnected())
        {
            mBtFeature.disconnectA2dpDevice();
        }
    }

    /**
     * 连接设备
     * @param listType
     * @param index
     */
    public void connectDevice(int listType, int index)
    {
        if (isConnected()) return;
        switch (listType)
        {
            case ListType.LAST_USED:
                mBtFeature.connectLastA2dpDevice();
                break;
            default:
                mBtFeature.connectA2dpDeviceFromList(index, listType);
                break;
        }
    }

    /**
     * 是否已经连接设备
     */
    public boolean isConnected()
    {
        return mA2DPStatus >= A2DPStatus.CONNECTED;
    }

    /**
     * 是否已连接AVRCP
     * @return
     */
    public boolean isConnectAVRCP()
    {
        return mAVRCPStatus == AVRCPStatus.CONNECTED;
    }
    
    /**
     * 蓝牙音乐是否播放中
     * 
     * @return
     */
    public boolean isMusicPlaying()
    {
        return isBTMusicPlaying;
    }

    /**
     * 播放控制
     * @param command
     */
    public void playControl(int command)
    {
        switch (command)
        {
            case BTConstant.MusicCommand.PLAY:
                mBtFeature.playControl(PlayCommand.PLAY);
                break;
            case BTConstant.MusicCommand.PAUSE:
                mBtFeature.playControl(PlayCommand.PAUSE);
                break;
            case BTConstant.MusicCommand.NEXT:
                mBtFeature.playControl(PlayCommand.NEXT);
                break;
            case BTConstant.MusicCommand.PREVIOUS:
                mBtFeature.playControl(PlayCommand.PREVIOUS);
                break;
        }
    }
    
    /**
     * 请求蓝牙音乐声音
     */
    @Override
	public void requestBtMusicVolume() {
		// TODO Auto-generated method stub
		mBtFeature.requestBtMusicVolume();
		Log.i("dp", "A2DPManager requestBtMusicVolume");
	}

    /**
     * 设置蓝牙音乐音量
     */
    public void setBtMusicVolume(int vol){
    	Log.i("dp", "A2DPManager  setBtMusicVolume");
    	mBtFeature.setBtMusicVolume(vol);
    }
    
    @Override
    public void onA2dpRelease()
    {
        // TODO Auto-generated method stub
        BTDefineSender.sendA2DPRelease(mContext);
    }

    @Override
    public void onGetA2dpVolume(int volume)
    {
    	Log.i("dp", "发送广播————————————》 ");
    	//BTDefineSender.sendA2dpVolume(mContext,volume);
    	Intent intent = new Intent(BTDefine.ACTION_A2DP_VOLUME);
    	intent.putExtra(BTDefine.EXTRA_A2DP_VOLUME, volume);
    	mContext.sendBroadcast(intent);
    }

    @Override
    public void onGetA2dpState(int state)
    {
        // TODO Auto-generated method stub
        int lastStatus = mA2DPStatus;
        
        Log.i("dp", "state="+state);
        
        switch (state)
        {
            case A2DPStatus.CONNECTED:  //音乐暂停
                if (mHFPManager.isEndCallState())
                {
                    LOG.print("onMusicPause");
                    Log.i("dp", "发送音乐暂停Broadcast");
                    isBTMusicPlaying = false;
                    BTDefineSender.sendBTMusicPause(mContext);
                }
                else
                {
                    LOG.print("this is not a music pause status because callState = " + mHFPManager.getCallState());
                }
                mA2DPStatus = state;
                if (lastStatus != mA2DPStatus)
                {
                    //从Connecting -> connect才算A2DP服务连接成功
                    BTDefineSender.sendA2DPEstablished(mContext);
                }
                break;
            case A2DPStatus.CONNECTING:
                mA2DPStatus = state;
                break;
            case A2DPStatus.READY:
                mA2DPStatus = state;
                if (lastStatus != mA2DPStatus)
                {
                    BTDefineSender.sendA2DPRelease(mContext);
                }
                break;
            case A2DPStatus.STREAMING:  
                if (mHFPManager.isEndCallState())
                {
                	Log.i("dp", "发送音乐播放Broadcast");
                    LOG.print("onMusicPlay");
                    isBTMusicPlaying = true;
                    BTDefineSender.sendBTMusicPlay(mContext);
                }
                else
                {
                    LOG.print("this is not a music play status because callState = " + mHFPManager.getCallState());
                }
                mA2DPStatus = state;
                break;
        }
    }

    @Override
    public void onGetAVRCPState(int state)
    {
        // TODO Auto-generated method stub
        int lastStatus = mAVRCPStatus;
        mAVRCPStatus = state;
        if (lastStatus != mAVRCPStatus)
        {
            switch (state)
            {
                case AVRCPStatus.READY:
                	BTDefineSender.sendAVRCPRelease(mContext);
                    break;
                case AVRCPStatus.CONNECTED:
                    BTDefineSender.sendAVRCPEstablished(mContext);
                    break;
            }
        }
    }

    @Override
    public void onA2dpEstablish(){}

	
}
