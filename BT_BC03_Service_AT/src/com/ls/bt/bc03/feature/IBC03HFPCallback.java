package com.ls.bt.bc03.feature;

public interface IBC03HFPCallback
{
    /**
     * 三方通话-在通话中.有新来电时触发的方法
     * @param number
     */
    public void onMultipulCall_NewIncommingCall(String number);
    
    /**
     * 三方通话-在通话中.有新去电时触发的方法
     * @param number
     */
    public void onMultipulCall_NewOutGoingCall(String number);
    
    /**
     * 三方通话-两通电话时.hold住的那方的号码
     * @param number
     */
    public void onMultipulCall_HoldCall(String number);
    
//    /**
//     * 三方通话-两通电话时.接通的那方的号码
//     * @param number
//     */
//    public void onMultipulCall_Calling(String number);
    
    /**
     * 三方通话-三方通话时.有一方通话结束
     */
    public void onMultipulCall_CancelCall();
    
    /**
     * 三方通话-挂断保持的电话 
     */
    public void onMultipulCall_CancelHoldCall();
    
    /**
     * 三方通话-接听等待
     */
    public void onMultipulCall_AcceptWaittingCallAndCancelCurrentCall();
    
    /**
     * 三方通话-进入三方通话
     */
    public void onMultipulCall_EnterThirdCalling();
    
    /**
     * 接通电话
     */
    public void onCallOnline();
    
    /**
     * 挂断电话,通话接通后再挂断时会触发
     */
    public void onCancelCall();
    
    /**
     * HFP服务断开
     */
    public void onHTPRelease();
    
    /**
     * HFP服务连接成功
     */
    public void onHTPEstablish();
    
    /**
     * 来电铃声
     */
    public void onRing();
    
    /**
     * 获取来电号码
     * @param number
     */
    public void onGetIncommingOrOutGoingCallNumber(String number);
    
    /**
     * 获取来电号码所对应的名字
     * @param name
     */
    public void onGetIncommingCallName(String name);
    
    /**
     * 获取等待中的电话号码
     * @param number
     */
    public void onGetWaittingCallNumber(String number);
    
    /**
     * 获取HFP音量
     * @param volume
     */
    public void onGetHFPVolume(int volume);
    
    /**
     * 发送DTMF的值给到设备
     * @param number
     */
    public void onSendDTMF(String number);    
    
    /**
     * 获取HFP状态
     * @param state
     */
    public void onGetHFPState(int state);
}
