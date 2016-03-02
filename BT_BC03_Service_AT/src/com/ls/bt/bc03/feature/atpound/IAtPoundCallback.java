package com.ls.bt.bc03.feature.atpound;

public interface IAtPoundCallback
{
    /**
     * 设置电话本状态的结果
     */
    public void onSetPhonebookStatus(boolean isOk);
    
    /**
     * 获取到电话本内容
     */
    public void onGetPhoneBook(String name,String number);
    
    /**
     * HFP断开连接
     */
    public void onHfpRelease();
    /**连接上HFP时*/
    public void onHfpConnected(String profile);
    /**通话结束或挂断*/
    public void onCallEnd();
    /**通话中*/
    public void onCalling();
    /**当有电话进来*/
    public void onCallIn();
    /**是否有来电*/
    public void onCallNum(String num);
}
