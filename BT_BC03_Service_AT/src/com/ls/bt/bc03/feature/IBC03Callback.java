package com.ls.bt.bc03.feature;


public interface IBC03Callback extends IBC03A2DPCallback,IBC03HFPCallback,IBC03PhoneBookCallback
{                 
    /**
     * 获取软件版本号
     * 
     * @param version
     */
    public void onGetSoftwareVersion(String version);

    /**
     * 获取设备名字
     * 
     * @param name
     */
    public void onGetDeviceName(String name);

    /**
     * 获取pin码
     * 
     * @param pinCode
     */
    public void onGetPinCode(String pinCode);

    /**
     * 获取到搜索的设备信息
     * 
     * @param index
     * @param address
     * @param name
     */
    public void onGetSearchDevice(int index, String address, String name);

    /**
     * 搜索设备结束
     */
    public void onGetSearchDeivceFinish();

    /**
     * 获取已匹配的设备信息
     * 
     * @param index
     * @param address
     * @param name
     */
    public void onGetPairDevice(int index, String address, String name);

    /**
     * 获取匹配记录结束
     */
    public void onGetPairDeviceFinish();

    /**
     * 获取已连接的设备信息
     * 
     * @param profile
     * @param name
     */
    public void onGetConnectedDevice(String profile, String name, String address);

    /**
     * 获取已连接设备信息结束
     */
    public void onGetConnectedDeviceFinish();

    /**
     * 获取电源状态
     * 
     * @param state
     */
    public void onGetPowerState(int state);

    /**
     * 获取配对状态
     * 
     * @param state
     */
    public void onGetPairState(int state);

    /**
     * 获取连接配置文件
     * 
     * @param profile
     */
    public void onGetConnectProfile(int profile);

    /**
     * 获取SCO状态
     * 
     * @param state
     */
    public void onGetSCOState(int state);

    /**
     * 获取静音状态
     * 
     * @param state
     */
    public void onGetMuteState(int state);

    /**
     * 获取通话等级
     * 
     * @param level
     */
    public void onGetCallEffectLevel(int level);

    /**
     * 获取PIO4状态
     * 
     * @param state
     */
    public void onGetPIO4State(int state);

    /**
     * 获取模块版本号
     * 
     * @param version
     */
    public void onGetModuleVersion(String version);
    
    /**
     * 自动功能发生改变
     * @param autoFeatureType
     * @param isOn
     */
    public void onAutoFeatureChange(int featureType,boolean isOn);
    
    /**
     * 上电初始化成功
     */
    public void onInitSuccess();
    
    /**
     * 升级模块成功
     */
    public void onUpdateSuccess();
    
    /**
     * 
     */
}
