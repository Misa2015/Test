package com.ls.bt.bc03.feature.atplus;

import java.io.IOException;

import android.R.string;
import android.util.Log;

import com.android.utils.log.JLog;
import com.android.utils.uart.UartCommunication;
import com.ls.bt.bc03.BC03BTConstant.CallStatus;
import com.ls.bt.bc03.BC03BTConstant.PhoneBookType;
import com.ls.bt.bc03.BC03BTConstant.PlayCommand;
import com.ls.bt.bc03.feature.IBC03Callback;

/**
 * AT+协议解析类
 * @author yewei
 *
 */
class AtPlusProtocalUtil
{
    private static final JLog LOG = new JLog("AtPlusProtocalUtil", true, JLog.TYPE_DEBUG);
    /**软件版本*/
    private static final String CMD_SOFTWARE_VERSION = "APP";
    /**设备名字*/
    private static final String CMD_DEVICE_NAME = "NAME";
    /**pin码*/
    private static final String CMD_PIN_CODE = "PIN";
    /**搜索设备*/
    private static final String CMD_SEARCH_DEIVCE = "INQ";
    /**获取匹配列表*/
    private static final String CMD_LIST_PAIR_DEVICE = "LSP";
    /**获取已连接设备信息*/
    private static final String CMD_LIST_CONNECT_DEVICE = "LSC";
    /**蓝牙状态*/
    private static final String CMD_BLUETOOTH_STATUS = "ST";
    /**连接A2DP*/
    private static final String CMD_CONNECT_A2DP = "LA+";
    /**断开连接A2DP*/
    private static final String CMD_DISCONNECT_A2DP = "LA-";
    /**A2DP状态*/
    private static final String CMD_A2DP_STATUS = "A2DP";
    /**连接HFP*/
    private static final String CMD_CONNECT_HFP = "LH+";
    /**断开连接HFP*/
    private static final String CMD_DISCONNECT_HFP = "LH-";
    /**HFP状态*/
    private static final String CMD_HFP_STATUS = "HFP";
    /**音量调节*/
    private static final String CMD_SPK_VOLUME = "VS";
    /**A2DP音量*/
    private static final String CMD_SPK_VOLUME_A2DP = "VSA";
    /**HFP音量*/
    private static final String CMD_SPK_VOLUME_HFP = "VSH";
    /**停止播放*/
    private static final String CMD_AVRCP_STOP = "STP";
    /**播放/暂停*/
    private static final String CMD_AVRCP_PLAY_PAUSE = "P/P";
    /**下一曲*/
    private static final String CMD_AVRCP_NEXT = "FWD";
    /**上一曲*/
    private static final String CMD_AVRCP_PREVIOUS = "BWD";
    /**重新拨号*/
    private static final String CMD_REDIAL = "DL";
    /**语音拨号*/
    private static final String CMD_VOICE_DIAL = "DV";
    /**拨号*/
    private static final String CMD_DIAL = "D";
    /**接听电话*/
    private static final String CMD_ANSWER_CALL = "CA";
    /**拒接电话*/
    private static final String CMD_REJECT_CALL = "CR";
    /**挂起电话*/
    private static final String CMD_HANDUP_CALL = "CH";
    /**DTMF数字按键*/
    private static final String CMD_DTMF = "DTMF";
    /**切换音频到AG*/
    private static final String CMD_TRANSFER_AG = "2AG";
    /**切换音频到HFP*/
    private static final String CMD_TRANSFER_HFP = "2HF";
    /**切换音频到另一个*/
    private static final String CMD_TRANSFER_ANOTHER = "TRN";
    /**释放挂起的电话或者拒绝等待中的电话*/
    private static final String CMD_MULTIPULCALL_RELEASE_HELD_CALL = "RHRW";
    /**释放当前通话并接通等待中/挂起中的电话*/
    private static final String CMD_MULTIPULCALL_RELEASE_ACTIVE_CALL_AND_ACCEPT_ANOTHER_CALL = "RAWH";
    /**挂起当前通话并接通等待中/挂起中的电话*/
    private static final String CMD_MULTIPULCALL_HOLD_ACTIVE_CALL_AND_ACCEPT_ANOTHER_CALL = "SWAP";
    /**添加挂起中的电话到通话中*/
    private static final String CMD_MULTIPULCALL_ADD_HELD_CALL = "ADDH";
    /**来电等待.当有第一通电话，有新来电时反馈*/
    private static final String CMD_MULTIPULCALL_NEW_WAITTING_INCOMING_CALL = "CLWD";
    /**去电等待，当有一通电话，有新去电时反馈*/
    private static final String CMD_MULTIPULCALL_NEW_WAITTING_OUTGOING_CALL = "CLOD";
    /**两通电话时，Hold住的电话*/
    private static final String CMD_MULTIPULCALL_HOLD_CALL_NUMBER = "CLOH";
    /**有电话时，有一桶电话挂断*/
    private static final String CMD_MULTIPULCALL_CANCEL_CALL = "CWIL";
    /**挂断保持的电话成功*/
    private static final String CMD_MULTIPULCALL_RELEASE_HELD_CALL_SUCCESS = "CWIN";
    /**接听等待的电话，挂断当前电话成功*/
    private static final String CMD_MULTIPULCALL_RELEASE_ACTIVE_CALL_AND_ACCEPT_ANOTHER_CALL_SUCCESS = "CWIT";
    /**进入三方通话*/
    private static final String CMD_MULTIPULCALL_ENTER_THRID_COMMUNICATION = "CWIM";
    /**静音*/
    private static final String CMD_MUTE = "MUTE";
    /**下载未接来电记录*/
    private static final String CMD_PB_MISS_CALL = "PBMC";
    /**下载已接来电记录*/
    private static final String CMD_PB_RECEIVE_CALL = "PBRC";
    /**下载去电记录*/
    private static final String CMD_PB_DIALED_CALL = "PBDC";
    /**下载手机中的联系人*/
    private static final String CMD_PB_PHONE_CONTACT = "PBME";
    /**下载sim卡中的联系人*/
    private static final String CMD_PB_SIM_CONTACT = "PBSM";
    /**电话本记录*/
    private static final String CMD_PHONEBOOK = "PB";
    /**查询电源状态*/
    private static final String CMD_POWER = "PWR";
    /**设置蓝牙电源开*/
    private static final String CMD_POWER_ON = "ON";
    /**设置蓝牙电源关*/
    private static final String CMD_POWER_OFF = "OFF";
    /**配对*/
    private static final String CMD_PAIR = "PAIR";
    /**AVRCP状态*/
    private static final String CMD_AVRCP_STATUS = "AVRCP";
    /**来电*/
    private static final String CMD_INCOMMING_CALL = "RING";
    /**来电号码*/
    private static final String CMD_INCOMING_CALL_NUMBER = "CLID";
    /**来电姓名*/
    private static final String CMD_INCOMING_CALL_NAME = "CLNM";
    /**等待中的电话号码*/
    private static final String CMD_WAITTING_CALL_NUMBER = "CLWT";
    /**通话状态*/
    private static final String CMD_CALL_STATUS = "CALL";
    /**SCO状态*/
    private static final String CMD_SCO_STATUS = "SCO";
    /**HFP连接断开*/
    private static final String CMD_HFP_LOST = "HLOST";
    /**A2DP连接断开*/
    private static final String CMD_A2DP_LOST = "ALOST";
    /**通话效果*/
    private static final String CMD_CALL_EFFECT = "EC";
    /**设置PIO4等级*/
    private static final String CMD_SET_PIO4 = "TENP";
    /**获取PIO4等级*/
    private static final String CMD_GET_PIO4 = "EA";
    /**查询模块类型*/
    private static final String CMD_MODULE = "MODULE";
    /**重置为默认状态*/
    private static final String CMD_RESET = "DFLT";
    /**错误处理*/
    private static final String CMD_ERROR = "ERR";
    
    /**结束符号*/
    private static final String PARAM_END = "E";
    
    public static final void receive(byte[] protocal,IBC03Callback callback)
    {
        LOG.print(AtPlusProtocal.getProtocalValue(protocal));
        String cmd = AtPlusProtocal.getIND(protocal).toUpperCase();
        
        LOG.print("返回指令————————————>"+cmd);
        
        if(CMD_SOFTWARE_VERSION.equals(cmd))
        {
            responseAPP(protocal, callback);
        }
        else if(CMD_DEVICE_NAME.equals(cmd))
        {
            responseDeviceName(protocal, callback);
        }
        else if(CMD_PIN_CODE.equals(cmd))
        {
            responsePinCode(protocal, callback);
        }
        else if(CMD_SEARCH_DEIVCE.equals(cmd))
        {
            responseSearchDevice(protocal, callback);
        }
        else if(CMD_LIST_PAIR_DEVICE.equals(cmd))
        {
            responsePairDevice(protocal, callback);
        }
        else if(CMD_LIST_CONNECT_DEVICE.equals(cmd))
        {
            responseConnectDevice(protocal, callback);
        }
        else if(CMD_BLUETOOTH_STATUS.equals(cmd))
        {
            responseBlueToothStatus(protocal, callback);
        }
        else if(CMD_A2DP_STATUS.equals(cmd))
        {
            responseA2DPStatus(protocal, callback);
        }
        else if(CMD_HFP_STATUS.equals(cmd))
        {
            responseHFPStatus(protocal, callback);
        }
        else if(CMD_SPK_VOLUME.equals(cmd))
        {
            responseSpeakerVolume(protocal, callback);
        }
        else if(CMD_SPK_VOLUME_A2DP.equals(cmd))
        {
            responseA2DPVolume(protocal, callback);
        }
        else if(CMD_SPK_VOLUME_HFP.equals(cmd))
        {
            responseHFPVolume(protocal, callback);
        }
        else if(cmd.startsWith(CMD_DTMF))
        {
            responseDTMF(cmd, callback);
        }
        else if(CMD_MUTE.equals(cmd))
        {
            responseMuteStatus(protocal, callback);
        }
        else if(CMD_PHONEBOOK.equals(cmd))
        {
        	//电话本记录
            responsePhoneBookData(protocal, callback);
        }
        else if(CMD_POWER.equals(cmd))
        {
            responsePowerStatus(protocal, callback);
        }
        else if(CMD_AVRCP_STATUS.equals(cmd))
        {
            responseAVRCPStatus(protocal, callback);
        }
        else if(CMD_INCOMMING_CALL.equals(cmd))
        {
            LOG.print("onIncommingCall");
            callback.onRing();
        }
        else if(cmd.startsWith(CMD_INCOMING_CALL_NUMBER))
        {
            responseIncomingCallNumber(cmd, callback);
        }
        else if(cmd.startsWith(CMD_INCOMING_CALL_NAME))
        {
            responseIncomingCallerName(cmd, callback);
        }
        else if(cmd.startsWith(CMD_WAITTING_CALL_NUMBER))
        {
            responseWaittingCallNumber(cmd, callback);
        }
        else if(CMD_CALL_STATUS.equals(cmd))
        {
            responseCallStatus(protocal, callback);
        }
        else if(CMD_SCO_STATUS.equals(cmd))
        {
            responseSCOStatus(protocal, callback);
        }
        else if(CMD_HFP_LOST.equals(cmd))
        {
            callback.onHTPRelease();
        }
        else if(CMD_A2DP_LOST.equals(cmd))
        {
            callback.onA2dpRelease();
        }
        else if(CMD_PAIR.equals(cmd))
        {
            responsePairStatus(protocal, callback);
        }
        else if(cmd.startsWith(CMD_MULTIPULCALL_NEW_WAITTING_INCOMING_CALL))
        {
            responseNewIncomingCallNumber(cmd, callback);
        }
        else if(cmd.startsWith(CMD_MULTIPULCALL_NEW_WAITTING_OUTGOING_CALL))
        {
            responseNewOutGoingCallNumber(cmd, callback);
        }
        else if(cmd.startsWith(CMD_MULTIPULCALL_HOLD_CALL_NUMBER))
        {
            responseHoldCallNumber(cmd, callback);
        }
        else if(CMD_MULTIPULCALL_CANCEL_CALL.equals(cmd))
        {
            LOG.print("onMultipulCall_CancelCall");
            callback.onMultipulCall_CancelCall();
        }
        else if(CMD_MULTIPULCALL_RELEASE_ACTIVE_CALL_AND_ACCEPT_ANOTHER_CALL_SUCCESS.equals(cmd))
        {
            LOG.print("onMultipulCall_AcceptWaittingCallAndCancelCurrentCall");
            callback.onMultipulCall_AcceptWaittingCallAndCancelCurrentCall();
        }
        else if(CMD_MULTIPULCALL_RELEASE_HELD_CALL_SUCCESS.equals(cmd))
        {
            LOG.print("onMultipulCall_CancelHoldCall");
            callback.onMultipulCall_CancelHoldCall();
        }
        else if(CMD_MULTIPULCALL_ENTER_THRID_COMMUNICATION.equals(cmd))
        {
            LOG.print("onMultipulCall_EnterThridCalling");
            callback.onMultipulCall_EnterThirdCalling();
        }
        else if(CMD_CALL_EFFECT.equals(cmd))
        {
            responseCallEffect(protocal, callback);
        }
        else if(CMD_GET_PIO4.equals(cmd))
        {
            responsePIO4(protocal, callback);
        }
        else if(CMD_MODULE.equals(cmd))
        {
            responseModule(protocal, callback);
        }
        else if(CMD_ERROR.equals(cmd))
        {
        	responseError(protocal, callback);
        }
    }
    
    /**
     * 处理错误
     * @param protocal
     * @param callback
     */
    private static final void responseError(byte[] protocal, IBC03Callback callback)
    {
    	callback.onError();
    }
    
    /**
     * 请求恢复初始化,这个指令会断开所有连接,删除所有匹配记录
     * @param uartCommunication
     */
    public static final void requestReset(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_RESET);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析模块类型
     * @param protocal
     * @param callback
     */
    private static final void responseModule(byte[] protocal, IBC03Callback callback)
    {
        String module = AtPlusProtocal.getINDParam(protocal);
        LOG.print("module = " + module);
        callback.onGetModuleVersion(module);
    }
    
    
    /**
     * 请求查询模块类型
     * @param uartCommunication
     */
    public static final void requestModule(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_MODULE);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析PIO4状态
     * @param protocal
     * @param callback
     */
    private static final void responsePIO4(byte[] protocal, IBC03Callback callback)
    {
        int state = AtPlusProtocal.getIntINDParam(protocal);
        LOG.print("PIO4 level = " + state);
        callback.onGetPIO4State(state);
    }
    
    /**
     * 设置PIO4输出状态
     * @param uartCommunication
     * @param level
     * 1 - 高       0 - 低
     */
    public static final void requestSetPIO4(UartCommunication uartCommunication, int level)
    {
        if(level <= 0)
        {
            level = 0;
        }
        else
        {
            level = 1;
        }
        String cmd = AtPlusProtocal.createATCommand(CMD_SET_PIO4, String.valueOf(level), false);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求查询PIO4等级
     * @param uartCommunication
     */
    public static final void requestQueryPIO4(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_SET_PIO4);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析通话效果等级
     * @param protocal
     * @param callback
     */
    private static final void responseCallEffect(byte[] protocal, IBC03Callback callback)
    {
        int level = AtPlusProtocal.getIntINDParam(protocal);
        LOG.print("call effect level = " + level);
        callback.onGetCallEffectLevel(level);
    }
    
    /**
     * 请求设置通话效果等级
     * @param uartCommunication
     * @param level
     * 0-2
     */
    public static final void requestSetCallEffect(UartCommunication uartCommunication, int level)
    {
        level = Math.max(level, 0);
        level = Math.min(level, 2);
        String cmd = AtPlusProtocal.createATCommand(CMD_CALL_EFFECT, String.valueOf(level), false);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 查询通话效果等级
     * @param uartCommunication
     */
    public static final void requestQueryCallEffect(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_CALL_EFFECT);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 两通电话时，Hold住的电话
     * @param cmd
     * @param callback
     */
    private static final void responseHoldCallNumber(String cmd, IBC03Callback callback)
    {
        String number = cmd.replaceFirst(CMD_MULTIPULCALL_HOLD_CALL_NUMBER, "");
        LOG.print("hold call number = " + number);
        callback.onMultipulCall_HoldCall(number);
    }
    
    /**
     * 去电等待，当有一通电话，有新去电时反馈
     * @param cmd
     * @param callback
     */
    private static final void responseNewOutGoingCallNumber(String cmd, IBC03Callback callback)
    {
        String number = cmd.replaceFirst(CMD_MULTIPULCALL_NEW_WAITTING_OUTGOING_CALL, "");
        LOG.print("new outgoing call number = " + number);
        callback.onMultipulCall_NewOutGoingCall(number);
    }
    
    /**
     * 来电等待，当有第一通电话，有新来电时反馈
     * @param cmd
     * @param callback
     */
    private static final void responseNewIncomingCallNumber(String cmd, IBC03Callback callback)
    {
        String number = cmd.replaceFirst(CMD_MULTIPULCALL_NEW_WAITTING_INCOMING_CALL, "");
        LOG.print("new incoming call number = " + number);
        callback.onMultipulCall_NewIncommingCall(number);
    }
    
    /**
     * 解析配对状态
     * @param protocal
     * @param callback
     */
    private static final void responsePairStatus(byte[] protocal, IBC03Callback callback)
    {
        int status = AtPlusProtocal.getIntINDParam(protocal);
        LOG.print("pair status = " + status);
        callback.onGetPairState(status);
    }
    
    /**
     * 解析SCO状态
     * @param protocal
     * @param callback
     */
    private static final void responseSCOStatus(byte[] protocal, IBC03Callback callback)
    {
        int status = AtPlusProtocal.getIntINDParam(protocal);
        LOG.print("sco status = " + status);
        callback.onGetSCOState(status);
    }
    
    /**
     * 解析通话状态
     * @param protocal
     * @param callback
     */
    private static final void responseCallStatus(byte[] protocal, IBC03Callback callback)
    {
        int status = AtPlusProtocal.getIntINDParam(protocal);
        LOG.print("call status = " + status);
        if(status == CallStatus.CALL_ESTABLISH)
        {
            callback.onCallOnline();
        }
        else
        {
            callback.onCancelCall();
        }
    }
    
    /**
     * 解析等待中的来电号码
     * @param cmd
     * @param callback
     */
    private static final void responseWaittingCallNumber(String cmd, IBC03Callback callback)
    {
        String number = cmd.replaceFirst(CMD_WAITTING_CALL_NUMBER, "");
        LOG.print("waitting call number = " + number);
        callback.onGetWaittingCallNumber(number);
    }
    
    /**
     * 解析来电姓名
     * @param cmd
     * @param callback
     */
    private static final void responseIncomingCallerName(String cmd, IBC03Callback callback)
    {
        String name = cmd.replaceFirst(CMD_INCOMING_CALL_NAME, "");
        LOG.print("incoming call name = " + name);
        callback.onGetIncommingCallName(name);
    }
    
    /**
     * 解析来电号码
     * @param protocal
     * @param callback
     */
    private static final void responseIncomingCallNumber(String cmd, IBC03Callback callback)
    {
        String number = cmd.replaceFirst(CMD_INCOMING_CALL_NUMBER, "");
        LOG.print("imcoming call number = " + number);
        callback.onGetIncommingOrOutGoingCallNumber(number);
    }
    
    /**
     * 解析AVRCP状态
     * @param protocal
     * @param callback
     */
    private static final void responseAVRCPStatus(byte[] protocal, IBC03Callback callback)
    {
        int status = AtPlusProtocal.getIntINDParam(protocal);
        LOG.print("AVRCP status = " + status);
        callback.onGetAVRCPState(status);
    }
    
    /**
     * 请求开始/停止配对
     * @param uartCommunication
     * @param timeoutSecond
     * 0 则停止配对. >0 则开始配对timeoutSecond秒,最大65535
     */
    public static final void requestPair(UartCommunication uartCommunication, int timeoutSecond)
    {
        timeoutSecond = Math.min(timeoutSecond, 65535);
        String cmd = AtPlusProtocal.createATCommand(CMD_PAIR, String.valueOf(timeoutSecond), false);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析蓝牙电源状态
     * @param protocal
     * @param callback
     */
    private static final void responsePowerStatus(byte[] protocal, IBC03Callback callback)
    {
        int status = AtPlusProtocal.getIntINDParam(protocal);
        LOG.print("power status = " + status);
        callback.onGetPowerState(status);
    }
    
    /**
     * 请求设置蓝牙电源状态
     * @param uartCommunication
     * @param isOn
     */
    public static final void requestSetPowerStatus(UartCommunication uartCommunication, boolean isOn)
    {
        if(isOn)
        {
            sendData2Uart(uartCommunication, AtPlusProtocal.createATCommand(CMD_POWER_ON));
        }
        else
        {
            sendData2Uart(uartCommunication, AtPlusProtocal.createATCommand(CMD_POWER_OFF));
        }
    }
    
    /**
     * 请求查询蓝牙电源状态
     * @param uartCommunication
     */
    public static final void requestQueryPowerStatus(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_POWER);
        sendData2Uart(uartCommunication, cmd);
    }
    
    
    /**
     * 解析电话本记录
     * @param protocal
     * @param callback
     */
    private static final void responsePhoneBookData(byte[] protocal,IBC03Callback callback)
    {
        String phoneBook = AtPlusProtocal.getINDParam(protocal);
        if(phoneBook.equalsIgnoreCase(PARAM_END))
        {
            callback.onGetPhoneBookFinish();
            LOG.print("onGetPhoneBookFinish");
        }
        else
        {
            String[] value = phoneBook.split(",");
            int index = Integer.parseInt(value[0]);
            String number = value[1];
            String name = null;
            if(value.length > 2)
            {
                name = value[2];
            }
            String time = null;
            if(value.length > 3)
            {
                time = value[3];
            }
            
            Log.i("phone", "value[0]="+value[0]+"  value[1]="+value[1]+"  value[2]="+value[2]+"  value[3=]"+value[3]);
            
            callback.onGetPhoneBook(index, number, name, time);
        }
    }
    
    /**
     * 请求获取电话本信息
     * @param uartCommunication
     * @param phoneBookType
     * @param index
     */
    public static final void requestGetPhoneBookData(UartCommunication uartCommunication, int phoneBookType, int index, int count)
    {
    	Log.i("plus", "请求电话本信息  phoneBookType="+phoneBookType);
        count = Math.min(count, 12);
        String phoneBook = null;
        switch(phoneBookType)
        {
            case PhoneBookType.DIAL_CALL:
                phoneBook = CMD_PB_DIALED_CALL;
                break;
            case PhoneBookType.MISS_CALL:
                phoneBook = CMD_PB_MISS_CALL;
                break;
            case PhoneBookType.PHONE_CONTACT:
                phoneBook = CMD_PB_PHONE_CONTACT;
                break;
            case PhoneBookType.RECEIVE_CALL:
                phoneBook = CMD_PB_RECEIVE_CALL;
                break;
            case PhoneBookType.SIM_CONTACT:
                phoneBook = CMD_PB_SIM_CONTACT;
                break;
        }
        
        String cmd = AtPlusProtocal.createATCommand(phoneBook, index + "," + (index + count), false);
        LOG.print("发送指令 ————————>"+cmd);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析静音状态
     * @param protocal
     * @param callback
     */
    private static final void responseMuteStatus(byte[] protocal, IBC03Callback callback)
    {
        int status = AtPlusProtocal.getIntINDParam(protocal);
        LOG.print("mute status = " + status);
        callback.onGetMuteState(status);
    }
    
    /**
     * 请求设置静音状态
     * @param uartCommunication
     * @param muteStatus
     */
    public static final void requestSetMuteStatus(UartCommunication uartCommunication, int muteStatus)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_MUTE, String.valueOf(muteStatus), false);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求把挂起中的电话添加到通话中
     * @param uartCommunication
     */
    public static final void requestAddHeldCall(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_MULTIPULCALL_ADD_HELD_CALL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求挂起当前通话并接通等待中/挂起中的电话
     * @param uartCommunication
     */
    public static final void requestHoldActiveCallAndAcceptAnotherCall(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_MULTIPULCALL_HOLD_ACTIVE_CALL_AND_ACCEPT_ANOTHER_CALL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求释放当前通话并接听等待中/挂起中的电话
     * @param uartCommunication
     */
    public static final void requestReleaseActiveCallAndAcceptAnotherCall(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_MULTIPULCALL_RELEASE_ACTIVE_CALL_AND_ACCEPT_ANOTHER_CALL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求放弃挂起的电话或者拒接等待中的来电
     * @param uartCommunication
     */
    public static final void requestReleaseHoldingCallOrRejuctWaittingCall(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_MULTIPULCALL_RELEASE_HELD_CALL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求切换音频到另外一个
     * @param uartCommunication
     */
    public static final void requestTransferAudio2Another(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_TRANSFER_ANOTHER);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求切换音频到HFP
     * @param uartCommunication
     */
    public static final void requestTransferAudio2HFP(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_TRANSFER_HFP);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求切换音频到AG
     * @param uartCommunication
     */
    public static final void requestTransferAudio2AG(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_TRANSFER_AG);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析DTMF内容
     * @param protocal
     * @param callback
     */
    private static final void responseDTMF(String protocal, IBC03Callback callback)
    {
        String number = protocal.replaceFirst(CMD_DTMF, "");
        LOG.print("DTMF number = " + number);
        callback.onSendDTMF(number);
    }
    
    /**
     * 发送DTMF按键
     * @param uartCommunication
     * @param number
     */
    public static final void requestSendDTMF(UartCommunication uartCommunication, String number)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_DTMF + number);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求挂起电话
     * @param uartCommunication
     */
    public static final void requestHandUpCall(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_HANDUP_CALL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求拒接电话
     * @param uartCommunication
     */
    public static final void requestRejectCall(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_REJECT_CALL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求接听电话
     * @param uartCommunication
     */
    public static final void requestAnswerCall(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_ANSWER_CALL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求拨号
     * @param uartCommunication
     * @param number
     */
    public static final void requestDial(UartCommunication uartCommunication,String number)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_DIAL + number);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求语音拨号
     * @param uartCommunication
     */
    public static final void requestVoiceDial(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_VOICE_DIAL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求重新拨号
     * @param uartCommunication
     */
    public static final void requestReDial(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_REDIAL);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * AVRCP控制
     * @param uartCommunication
     * @param command
     */
    public static final void requestPlayControl(UartCommunication uartCommunication,int command)
    {
        String control = null;
        switch(command)
        {
            case PlayCommand.STOP:
                control = CMD_AVRCP_STOP;
                break;
            case PlayCommand.PLAY:
            case PlayCommand.PAUSE:
                control = CMD_AVRCP_PLAY_PAUSE;
                break;
            case PlayCommand.PREVIOUS:
                control = CMD_AVRCP_PREVIOUS;
                break;
            case PlayCommand.NEXT:
                control = CMD_AVRCP_NEXT;
                break;
        }
        String cmd = AtPlusProtocal.createATCommand(control);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析音量
     * @param protocal
     * @param callback
     */
    private static final void responseSpeakerVolume(byte[] protocal,IBC03Callback callback)
    {
        String[] value = AtPlusProtocal.getINDParam(protocal).split(",");
        int a2dpVolume = Integer.parseInt(value[0]);
        int hfpVolume = Integer.parseInt(value[1]);
        LOG.print("a2dpVolume = " + a2dpVolume + ",hfpVolume = " + hfpVolume);
        callback.onGetA2dpVolume(a2dpVolume);
        callback.onGetHFPVolume(hfpVolume);
    }
    
    /**
     * 解析A2DP音量
     * @param protocal
     * @param callback
     */
    private static final void responseA2DPVolume(byte[] protocal, IBC03Callback callback)
    {
        int a2dpVolume = Integer.parseInt(AtPlusProtocal.getINDParam(protocal));
        LOG.print("a2dpVolume = " + a2dpVolume);
        callback.onGetA2dpVolume(a2dpVolume);
    }
    
    /**
     * 解析HFP音量
     * @param protocal
     * @param callback
     */
    private static final void responseHFPVolume(byte[] protocal, IBC03Callback callback)
    {
        int hfpVolume = Integer.parseInt(AtPlusProtocal.getINDParam(protocal));
        LOG.print("hfpVolume = " + hfpVolume);
        callback.onGetHFPVolume(hfpVolume);
    }
    
    /**
     * 设置音量
     * @param uartCommunication
     * @param volumeLevel
     * 0 - 15
     */
    public static final void requestSetSpeakerVolume(UartCommunication uartCommunication,int volumeLevel)
    {
        volumeLevel = Math.max(volumeLevel, 0);
        volumeLevel = Math.min(volumeLevel, 15);
        String cmd = AtPlusProtocal.createATCommand(CMD_SPK_VOLUME, String.valueOf(volumeLevel), false);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 请求获取音量
     * @param uartCommunication
     */
    public static final void requestGetSpeakerVolume(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_SPK_VOLUME);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析HFP连接状态
     * @param protocal
     * @param callback
     */
    private static final void responseHFPStatus(byte[] protocal, IBC03Callback callback)
    {
        String[] value = AtPlusProtocal.getINDParam(protocal).split(",");
        int status = Integer.parseInt(value[0]);
        LOG.print("hfp status = " + status);
        callback.onGetHFPState(status);
        if(value.length > 1)
        {
            int profile = Integer.parseInt(value[1]);
            LOG.print("connect profile = " + profile);
            callback.onGetConnectProfile(profile);
        }
    }
    
    /**
     * 连接最后一次连接的HFP设备
     * @param uartCommunication
     */
    public static final void requestConnectHFPDeviceFromLastUsed(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_CONNECT_HFP);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 连接匹配列表中的HFP设备
     * @param uartCommunication
     * @param index
     */
    public static final void requestConnectHFPDeviceFromPairList(UartCommunication uartCommunication,int index)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_CONNECT_HFP + "P" + index);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 连接搜索列表中的HFP设备
     * @param uartCommunication
     * @param index
     */
    public static final void requestConenctHFPDeviceFromSearchList(UartCommunication uartCommunication,int index)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_CONNECT_HFP + "I" + index);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 断开HFP连接
     * @param uartCommunication
     */
    public static final void requestDisconnectHFPDevice(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_DISCONNECT_HFP);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析A2DP连接状态
     * @param protocal
     * @param callback
     */
    private static final void responseA2DPStatus(byte[] protocal, IBC03Callback callback)
    {
        int status = Integer.parseInt(AtPlusProtocal.getINDParam(protocal));
        LOG.print("a2dp status = " + status);
        callback.onGetA2dpState(status);
    }
    
    /**
     * 连接最后一次连接的A2DP设备
     * @param uartCommunication
     */
    public static final void requestConnectA2DPDeviceFromLastUsed(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_CONNECT_A2DP);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 连接匹配列表中的A2DP设备
     * @param uartCommunication
     * @param index
     */
    public static final void requestConnectA2DPFromPairList(UartCommunication uartCommunication,int index)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_CONNECT_A2DP + "P" + index);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 连接搜索列表中的A2DP设备
     * @param uartCommunication
     * @param index
     */
    public static final void requestConnectA2DPFromSearchList(UartCommunication uartCommunication,int index)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_CONNECT_A2DP + "I" + index);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 断开连接设备
     * @param uartCommunication
     */
    public static final void requestDisconnectA2DP(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_DISCONNECT_A2DP);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析蓝牙状态
     * @param protocal
     * @param callback
     */
    private static final void responseBlueToothStatus(byte[] protocal, IBC03Callback callback)
    {
        String[] status = AtPlusProtocal.getINDParam(protocal).split(",");
        callback.onGetPowerState(Integer.parseInt(status[0]));
        callback.onGetPairState(Integer.parseInt(status[1]));
        callback.onGetA2dpState(Integer.parseInt(status[2]));
        callback.onGetAVRCPState(Integer.parseInt(status[3]));
        if(status.length > 4)
        {
            //in snk mode
            callback.onGetHFPState(Integer.parseInt(status[4]));
            callback.onGetConnectProfile(Integer.parseInt(status[5]));
            callback.onGetSCOState(Integer.parseInt(status[6]));
            callback.onGetMuteState(Integer.parseInt(status[7]));
        }
    }
    
    /**
     * 请求获取蓝牙状态
     * @param uartCommunication
     */
    public static final void requestGetBlueToothStatus(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_BLUETOOTH_STATUS);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析连接设备信息
     * @param protocal
     * @param callback
     */
    private static final void responseConnectDevice(byte[] protocal, IBC03Callback callback)
    {
        String deviceInfo = AtPlusProtocal.getINDParam(protocal);
        if(deviceInfo.equalsIgnoreCase(PARAM_END))
        {
            callback.onGetConnectedDeviceFinish();
            LOG.print("onGetConnectedDeviceFinish");
        }
        else
        {
            String[] value = deviceInfo.split(",");
            String profile = value[0];
            String name = value[1];
            LOG.print("profile = " + profile + ",name = " + name);
            callback.onGetConnectedDevice(profile, name,null);
        }
    }
    
    /**
     * 请求获取连接设备信息
     * @param uartCommunication
     */
    public static final void requestListConnectDevice(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_LIST_CONNECT_DEVICE);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析匹配设备信息
     * @param protocal
     * @param callback
     */
    private static final void responsePairDevice(byte[] protocal, IBC03Callback callback)
    {
        String deviceInfo = AtPlusProtocal.getINDParam(protocal);
        if(deviceInfo.equalsIgnoreCase(PARAM_END))
        {
            callback.onGetPairDeviceFinish();
            LOG.print("onGetPairDeviceFinish");
        }
        else
        {
            String[] value = analyzeDeviceInfo(deviceInfo);
            int index = Integer.parseInt(value[0]);
            String name = value[4];
            String address = value[1];
            LOG.print("index = " + index + ",name = " + name + ",address = " + address);
            callback.onGetPairDevice(index, address, name);
        }
    }
    
    /**
     * 请求获取匹配列表
     * @param uartCommunication
     */
    public static final void requestListPairDevice(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_LIST_PAIR_DEVICE);
        sendData2Uart(uartCommunication, cmd);
    }
    
    
    /**
     * 解析查找到的设备信息
     * @param protocal
     * @param callback
     */
    private static final void responseSearchDevice(byte[] protocal, IBC03Callback callback)
    {
        String deviceInfo = AtPlusProtocal.getINDParam(protocal);
        if(deviceInfo.equalsIgnoreCase(PARAM_END))
        {
            callback.onGetSearchDeivceFinish();
            LOG.print("onGetSearchDeivceFinish");
        }
        else
        {
            String[] value = analyzeDeviceInfo(deviceInfo);
            int index = Integer.parseInt(value[0]);
            String name = value[2];
            String address = value[1];
            LOG.print("index = " + index + ",name = " + name + ",address = " + address);
            callback.onGetSearchDevice(index, address, name);
        }
    }
    
    /**
     * 解析设备信息
     * @param deviceInfo
     * <index>,<bluetooth_address>,<name>
     * @return
     */
    private static final String[] analyzeDeviceInfo(String deviceInfo)
    {
        String[] result = deviceInfo.split(",");
        /*
         * For example, the device found first is named handsfree, and its Bluetooth address 
           is NAP=0x03, UAP=0x55, LAP=0x003304, the response will be: 

           INQ=1,"3,55,3304","handsfree" 
           (Other inquir results) */
        StringBuilder address = new StringBuilder();
//        String[] value = result[1].split(",");
        addZero(address, result[1], 2);
        address.append(result[1]);
        addZero(address, result[2], 2);
        address.append(result[2]);
        addZero(address, result[3], 6);
        address.append(result[3]);
        result[1] = address.toString();
        return result;
    }
    
    /**
     * 补0
     * @param src
     * @param value
     * @param count
     * 位数
     */
    private static final void addZero(StringBuilder src,String value,int count)
    {
        for(int i = 0; i < count - value.length(); i++)
        {
            src.append("0");
        }
    }
    
    /**
     * 开始查找/停止周边设备
     * @param uartCommunication
     * @param isStartSearch
     */
    public static final void requestSearchDevice(UartCommunication uartCommunication,boolean isStartSearch)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_SEARCH_DEIVCE, isStartSearch ? "1" : "0", false);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析pin码
     * @param protocal
     * @param callback
     */
    private static final void responsePinCode(byte[] protocal, IBC03Callback callback)
    {
        String pinCode = AtPlusProtocal.getINDParam(protocal);
        LOG.print("pinCode = " + pinCode);
        callback.onGetPinCode(pinCode);
    }
    
    /**
     * 设置/查询pin码
     * @param uartCommunication
     * @param pinCode
     */
    public static final void requestSetOrQueryPinCode(UartCommunication uartCommunication, String pinCode)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_PIN_CODE, pinCode, true);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 解析软件版本
     * @param protocal
     * @param callback
     */
    private static final void responseAPP(byte[] protocal,IBC03Callback callback)
    {
        String appVersion = AtPlusProtocal.getINDParam(protocal);
        LOG.print("appVersion = " + appVersion);
        callback.onGetSoftwareVersion(appVersion);
    }
    
    /**
     * 解析设备名字
     * @param protocal
     * @param callback
     */
    private static final void responseDeviceName(byte[] protocal,IBC03Callback callback)
    {
        String deviceName = AtPlusProtocal.getINDParam(protocal);
        LOG.print("device name = " + deviceName);
        callback.onGetDeviceName(deviceName);
    }
    
    /**
     * 请求/查询设置设备名字
     * @param uartCommunication
     * @param name
     * name=null则为查询
     */
    public static final void requestSetOrQueryDeviceName(UartCommunication uartCommunication, String name)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_DEVICE_NAME, name, true);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 查询软件版本号
     * @param uartCommunication
     */
    public static final void requestQuerySofewareVersion(UartCommunication uartCommunication)
    {
        String cmd = AtPlusProtocal.createATCommand(CMD_SOFTWARE_VERSION);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 发送AT指令
     * @param uartCommunication
     * @param command
     */
    public static final void requestSendAtCommand(UartCommunication uartCommunication, String command)
    {
        String cmd = AtPlusProtocal.createATCommand(command);
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 发送数据到串口中
     * @param uartCommunication
     * @param protocal
     */
    private static final void sendData2Uart(UartCommunication uartCommunication, String cmd)
    {
        LOG.print("发送指令————————————>"+cmd);
        byte[] protocal = cmd.getBytes();
        try
        {
            uartCommunication.writeData(protocal);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
