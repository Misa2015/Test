package com.ls.bt.bc03.feature;

/**
 * 蓝牙功能
 * @author yewei
 *
 */
/**
 * @author user
 *
 */
public interface IBC03Feature
{
    /**
     * 接收协议
     * @param protocal
     */
    public void receive(byte[] protocal);
    
    /**
     * 释放方法
     */
    public void release();
    
    /**
     * 发送AT指令,Debug用
     * @param command
     */
    public void sendATCommand(String command);
    /**
     * 查询软件版本
     */
    public void querySoftwareVersion();
    
    /**
     * 查询设备名字
     */
    public void queryDeviceName();
    
    /**
     * 设置设备名字
     * @param name
     */
    public void setDeviceName(String name);
    
    /**
     * 查询pin码
     */
    public void queryPinCode();
    
    /**
     * 设置pinCode
     * @param pinCode
     */
    public void setPinCode(String pinCode);
    
    /**
     * 开始搜索范围内的设备
     */
    public void startDiscover();
    
    /**
     * 停止搜索设备
     */
    public void stopDiscover();
    
    /**
     * 查询匹配设备记录
     */
    public void queryPairDeviceList();
    
    /**
     * 查询连接设备记录
     */
    public void queryConnectDeviceList();
    
    /**
     * 查询电源状态
     */
    public void queryPowerState();
    
    /**
     * 查询配对状态
     */
    public void queryPairState();
    
    /**
     * 查询A2DP状态
     */
    public void queryA2dpState();
    
    /**
     * 查询AVRCP状态
     */
    public void queryAVRCPState();
    
    /**
     * 查询HFP状态
     */
    public void queryHFPState();
    
    /**
     * 查询ConnectProfile
     */
    public void queryConnectProfileState();
    
    /**
     * 查询SCO状态
     */
    public void querySCOState();
    
    /**
     * 查询静音状态
     */
    public void queryMuteState();
    
    /**
     * 连接最后一次连接的A2dp设备
     */
    public void connectLastA2dpDevice();
    
    /**
     * 连接记录中指定索引的A2dp设备
     * @param index
     * @param listType @See BC03Constant
     */
    public void connectA2dpDeviceFromList(int index,int listType);
    
    
    /**
     * 断开A2DP设备
     */
    public void disconnectA2dpDevice();
    
    /**
     * 连接最后一次连接的HFP设备
     */
    public void connectLastHFPDevice();
    
    /**
     * 连接记录中指定索引的HFP设备
     * @param index
     * @param listType @See BC03Constant
     */
    public void connectHFPDeviceFromList(int index,int listType);
    
    /**
     * 断开HFP设备
     */
    public void disconnectHFPDevice();
    
    /**
     * 设置音量
     * @param volume
     */
    public void setSpkVolume(int volume);
    
    /**
     * 查询音量
     */
    public void querySpkVolume();
    
    /**
     * 蓝牙音乐控制
     * @param command @See BC03BTConstant
     */
    public void playControl(int command);
    
    /**
     * 蓝牙音乐的声音值
     */
    public void  requestBtMusicVolume();
    
    /**
     * 设置蓝牙音乐的声音
     */
    public void setBtMusicVolume(int vol);
    
    /**
     * 拨号/
     * @param number
     * 假如number为Null.则为重拨
     */
    public void dial(String number);
    
    /**
     * 接听电话
     */
    public void acceptCall();
    
    /**
     * 拒接电话
     */
    public void rejectCall();
    
    /**
     * 挂断电话
     */
    public void handUpCall();
    
    /**
     * 发送DTMF
     * @param number
     */
    public void sendDTMF(String number);
    
    /**
     * 切换声音通道
     * @param channel
     */
    public void changeSoundChannel(int channel);
    
    /**
     * 三方通话-拒接等待的来电
     */
    public void rejectWaittingCall();
    
    /**
     * 三方通话-挂断当前通话并接听等待/挂起中的来电
     */
    public void releaseActiveCallAndAcceptOtherCall();
    
    /**
     * 三方通话-挂起当前通话并接听等待/挂起中的来电
     */
    public void holdActiveCallAndAcceptOtherCall();
    
    /**
     * 三方通话-添加挂起的来电到当前通话中
     */
    public void addHoldCall2Conversation();
    
    /**
     * 设置静音状态
     * @param isMute
     */
    public void setMute(boolean isMute);
    
    /**
     * 获取电话本内容
     * @param phoneBookType
     * @see BC03BTConstant
     * @param index
     * @param count
     */
    public void getPhoneBook(int phoneBookType,int index, int count);
    
    /**
     * 设置电源开关状态
     * @param isOn
     */
    public void setPower(boolean isOn);
    
    /**
     * 蓝牙复位
     */
    public void reset();
    
    /**
     * 开启配对模式
     * @param timeout
     * 假如 timeout = 0,则停止配对模式
     */
    public void setDiscoverable(int timeout);
    
    /**
     * 设置通话效果等级
     * @param level
     */
    public void setCallEffect(int level);
    
    /**
     * 查询通话效果等级
     */
    public void queryCallEffect();
    
    /**
     * 设置PIO4等级
     * @param level
     */
    public void setPIO4Level(int level);
    
    /**
     * 查询PIO4等级
     */
    public void queryPIO4Level();
    
    /**
     * 查询模块版本号
     */
    public void queryModuleVersion();
    
    /**
     * 设置自动功能
     * @param featureType
     * @param isOn
     */
    public void setAutoFeature(int featureType,boolean isOn);
    
    /**
     * 查询自动功能
     */
    public void queryAutoFeature();
    
    /**
     * 设置蓝牙音乐是否静音
     * @param isMute
     */
    public void setBtMusicMute(boolean isMute);
    
    /**
     * 请求升级模块
     */
    public void requestUpdate();
}
