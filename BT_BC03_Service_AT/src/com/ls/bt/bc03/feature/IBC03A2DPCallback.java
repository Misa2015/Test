package com.ls.bt.bc03.feature;

public interface IBC03A2DPCallback
{
    /**
     * A2DP服务断开
     */
    public void onA2dpRelease();
    
    /**
     * A2DP服务连接成功
     */
    public void onA2dpEstablish();

    /**
     * 获取A2DP音量
     * 
     * @param volume
     */
    public void onGetA2dpVolume(int volume);
    
    /**
     * 获取A2DP状态
     * @param state
     */
    public void onGetA2dpState(int state);
    
    /**
     * 获取AVRCP状态
     * @param state
     */
    public void onGetAVRCPState(int state);
    
    /**
     * 请求蓝牙音乐的声音值
     */
    public void requestBtMusicVolume();
    
    /**
     * 获取蓝牙音乐的声音值
     */
}
