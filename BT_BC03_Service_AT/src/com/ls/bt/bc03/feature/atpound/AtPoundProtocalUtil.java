package com.ls.bt.bc03.feature.atpound;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.content.Intent;
import android.util.Log;

import com.android.utils.log.JLog;
import com.android.utils.uart.UartCommunication;
import com.bt.BTDefine;
import com.ls.bt.bc03.A2DPManager;
import com.ls.bt.bc03.BC03BTConstant.A2DPStatus;
import com.ls.bt.bc03.BC03BTConstant.AVRCPStatus;
import com.ls.bt.bc03.BC03BTConstant.AutoFeature;
import com.ls.bt.bc03.BC03BTConstant.HFPStatus;
import com.ls.bt.bc03.BC03BTConstant.MuteStatus;
import com.ls.bt.bc03.BC03BTConstant.PairStatus;
import com.ls.bt.bc03.BC03BTConstant.PlayCommand;
import com.ls.bt.bc03.feature.IBC03Callback;

public class AtPoundProtocalUtil
{
	private static final String TAG = "AtPoundProtocalUtil";
	
    private static final JLog LOG = new JLog(TAG, true, JLog.TYPE_DEBUG);
    /**进入配对模式*/
    private static final String CMD_START_DISCOVER = "CA";
    /**取消配对模式*/
    private static final String CMD_STOP_DISCOVER = "CB";
    /**连接HFP*/
    private static final String CMD_CONNECT_HFP = "SC";
    /**断开HFP*/
    private static final String CMD_DISCONNECT_HFP = "SE";
    /**接听来电*/
    private static final String CMD_ACCEPT_CALL = "CE";
    /**拒接来电*/
    private static final String CMD_REJECT_CALL = "CF";
    /**挂断电话 通话结束*/
    private static final String CMD_CANCEL_CALL = "CG";
    /**重拨*/
    private static final String CMD_REDIAL = "CH";
    /**音量+*/
    private static final String CMD_VOLUME_UP = "VD";
    /**音量-*/
    private static final String CMD_VOLUME_DOWN = "VU";
    /**声道切换手机端/车机端*/
    private static final String CMD_CHANGE_SOUND_CHANNEL = "CO";
    
    private static final String CMD_CHANGE_SOUND_TO_BLUE = "CP";// ————————》添加
    
    /**拨号*/
    private static final String CMD_DIAL = "CW";
    /**拨分机号 eg:CX0 表示按下0键*/
    private static final String CMD_DIAL_EXTENSION_NUMBER = "CX";
    /**查询HFP状态*/
    private static final String CMD_QUERY_HFP_STATUS = "CY";
    /**蓝牙复位*/
    private static final String CMD_RESET = "CZ";
    /**播放/暂停音乐 */
    private static final String CMD_AVRCP_PLAY = "MA";
    /**上一曲*/
    private static final String CMD_AVRCP_NEXT = "MD";
    /**下一曲*/
    private static final String CMD_AVRCP_PREVIOUS = "ME";
    
    /**设定/查询设备名字*/
    // private static final String CMD_SET_OR_QUERY_DEVICE_NAME = "MM";
    private static final String CMD_SET_DEVICE_NAME = "MM";
    /**待完善*/
    private static final String CMD_QUERY_DEVICE_NAME = "";
    /**设定/查询Pin码*/
    //private static final String CMD_SET_OR_QUERY_PINCODE = "MN";
    private static final String CMD_SET_PINCODE = "MN";
    /**待完善*/
    private static final String CMD_QUERY_PINCODE = "";
    
    /**设定获取SIM的联系人*/
    private static final String CMD_GET_CONTACT_FROM_SIM = "PA";
    /**设定获取手机的联系人*/
    private static final String CMD_GET_CONTACT_FROM_PHONE = "PB";
    /**设定获取手机的联系人*/
    private static final String CMD_GET_ALL_CONTACT = "PX";
    /**获取下N条记录(1-9)*/
    private static final String CMD_GET_NEXT_RECORD = "PC";
    /**获取上N条记录(1-9)*/
    private static final String CMD_GET_PREVIOUS_RECORD = "PD";
    /**设定获取已拨号码记录*/
    private static final String CMD_GET_DIAL_CALL_LOG = "JM"; 
    /**设定获取已接号码记录*/
    private static final String CMD_GET_RECEIVE_CALL_LOG = "JL"; 
    /**设定获取未接号码记录*/
    private static final String CMD_GET_MISS_CALL_LOG = "JN";
    
    /**上翻通话记录*/
    private static final String CMD_GET_UP_CALL_LOG = "JP";
    /**下翻通话记录*/
    private static final String CMD_GET_NEXT_CALL_LOG = "JQ";
    
    /**请求蓝牙音乐的声音值*/
    private static final String CMD_A2DP_VOLUME = "VS";
    
    // 2015-04-18 未实现的指令================================================================================
    /**查询AVRCP状态*/
    private static final String CMD_QUERY_AVRCP_STATUS = "MO";
    /**开启来电自动接听*/
    private static final String CMD_SET_AUTO_ACCEPT_CALL_ON = "MP";
    /**关闭来电自动接听*/
    private static final String CMD_SET_AUTO_ACCEPT_CALL_OFF = "MQ";
    /**查询A2DP状态*/
    private static final String CMD_QUERY_A2DP_STATUS = "MV";
    /**设定/查询蓝牙开关状态*/
    private static final String CMD_SET_OR_QUERY_POWER_STATUS = "MW";
    /**查询配对设备列表*/
    private static final String CMD_QUERY_PAIR_DEVICE_LIST = "MX";
    /**查询当前连接手机信息*/
    private static final String CMD_QUERY_CONNECT_DEVICE = "MX0";
    /**查询软件版本日期*/
    private static final String CMD_QUERY_SOFTWARE_VERSION = "MY";
    /**蓝牙音乐静音*/
    private static final String CMD_BT_MUSIC_MUTE = "VA";
    /**蓝牙音乐非静音*/
    private static final String CMD_BT_MUSIC_UNMUTE = "VB";
    /**请求升级模块指令*/
    private static final String CMD_REQUEST_UPDATE = "UP";
    /**麦克风打开/关闭*/
    private static final String CMD_SPK_OPEN_CLOSE = "CM";
	/**挂断/拒接等待来电*/
    private static final String CMD_REJECT_WAITTING_CALL = "CQ";
	/**挂断当前来电并接听等待中的来电*/
    private static final String CMD_RELEASE_ACTIVE_CALL_AND_ACCEPT_WAITTING_CALL = "CR";
    /**保持当前通话并接听等待中的来电*/
    private static final String CMD_HOLD_ACTIVE_CALL_AND_ACCEPT_WAITTING_CALL = "CS";
    /**进入三方通话*/
    private static final String CMD_ENTER_THIRD_CONMUNICATE = "CT";
	/**删除已配对记录*/
    private static final String CMD_DELETE_PAIR_DEVICE_LIST = "CV";
	/**断开A2DP连接*/
    private static final String CMD_DISCONNECT_A2DP = "DA";
    /**查询/设定通话效果*/
    private static final String CMD_QUERY_OR_SET_CALL_EFFECT = "EC";
	/**暂停音乐*/
    private static final String CMD_AVRCP_PAUSE = "MJ";
	/**停止播放*/
    private static final String CMD_AVRCP_STOP = "MC";
	/**查询自动功能状态*/
    private static final String CMD_QUERY_AUTO_FEATURE = "MF";
    /**开启开机自动连接*/
    private static final String CMD_SET_AUTO_CONNECT_DEVICE_ON = "MG";
    /**关闭开机自动连接*/
    private static final String CMD_SET_AUTO_CONNECT_DEVICE_OFF = "MH";
    // end no implements 2015-04-18 ===============================================================================
    
    //////////////////应答指令  开始
    /**HFP已断开 / 连接失败*/
    private static final String IND_HFP_RELEASE = "IA";
    /**HFP已连接*/
    private static final String IND_HFP_CONNECTED = "IB";
    /**连接中*/
    private static final String IND_CONNECTING = "IV";
    /**拨出电话*/
    private static final String IND_OUTGOING_CALL = "IR";
    /**来电*/
    private static final String IND_INCOMMING_CALL = "ID";
    /**通话结束 、挂机*/
    private static final String IND_END_CALL = "IF";
    /**通话中*/
    private static final String IND_CALL_ONLINE = "IG";
    /**去电号码 IR+NUM*/
    private static final String IND_OUTGOING_CALL_NUMBER = "IR";
    /**来电号码 IR+NUM*/
    private static final String IND_COMING_CALL_NUMBER = "ID";
    /**上电初始化成功*/
    private static final String IND_INIT_SUCCESS = "IS";
    /**暂停*/
    private static final String IND_A2DP_STOP_PLAY = "MA";
    /**播放*/
    private static final String IND_A2DP_STREAMING = "MB";
    /**通话记录的号码 IW+NUM*/
    private static final String IND_CALL_LOG_NUM = "IW";
    /**一条通讯录*/
    private static final String IND_PHONE_BOOK = "PB";    
    /**一条已接电话记录*/
    private static final String IND_COMING_PB = "PD";
    /**一条已拔电话记录*/
    private static final String IND_CALL_PB = "PD";
    /**一条未接电话记录*/
    private static final String IND_MISING_PB = "PD";
    /**HFP状态, MGx蓝牙工作状态, x:0----初始化状态	<br>
     * 1----待机状态<br>
     * 2----正在搜索/正在连接<br>
     * 3----连接成功<br>
     * 4----播出电话状态<br>	
     * 5----电话呼入状态<br>		
	 * 6----通话中状态*/
    private static final String IND_HFP_STATUS = "MG";
    /**通话在车机*/
    private static final String IND_ON_CAR = "IR";
    /**通话在手机*/
    private static final String IND_ON_MOBILE = "IO";
    /**电话本同步状态 PAx, x: 0同步成功，1同步失败*/
    private static final String IND_SET_PHONEBOOK_STATUS = "PA";
    /**获取联系人资料 PB+name+num*/
    private static final String IND_GET_CONTACT = "PB";
    /**下载电话本结束*/
    private static final String IND_DOWNLOAD_PHONEBOOK_FINISH = "PC";
    //// 应答指令结束======================================================================
    
    
    /**当前连接手机的地址*/
    private static final String IND_CONNECTED_DEVICE_NAME = "AD";
    /**当前通话效果*/
    private static final String IND_CALL_EFFECT = "EC";
    /**进入配对模式*/
    private static final String IND_DISCOVER_ON = "II";
    /**结束配对模式*/
    private static final String IND_DISCOVER_FINISH = "IJ";
    /**呼叫等待*/
    private static final String IND_NEW_WAITTING_CALL = "IK";
    /**保持当前通话并接听等待中的电话*/
    private static final String IND_HOLD_ACTIVE_CALL_AND_ACCEPT_WAITTING_CALL = "IL";
    /**进入三方通话*/
    private static final String IND_ENTER_THRID_CONMUNICATION = "IM";
    /**挂断挂起/等待中的电话*/
    private static final String IND_RELEASE_HOLD_OR_WAITTING_CALL = "IN";
    /**来电姓名*/
    private static final String IND_INCOMMING_CALL_NAME = "IQ";
    /**挂断当前电话并接听等待中的电话*/
    private static final String IND_RELEASE_ACTIVE_CALL_AND_ACCEPT_WAITTING_CALL = "IT";
    /**开启SPK音频输出*/
    private static final String IND_SPK_UNMUTE = "IY";
    /**关闭SPK音频输出*/
    private static final String IND_SPK_MUTE = "IZ";
    /**语音连接建立*/
    private static final String IND_VOICE_CONNECT = "MC";
    /**语音连接断开*/
    private static final String IND_VOICE_DISCONNECT = "MD";
    /**自动功能状态*/
    private static final String IND_AUTO_FEATURE_STATUS_2 = "MF";
    /**自动功能状态*/
    private static final String IND_AUTO_FEATURE_STATUS = "MH";
    /**AVRCP状态*/
    private static final String IND_AVRCP_STATUS = "ML";
    /**获取设备名字*/
    private static final String IND_DEVICE_NAME = "MM";
    /**获取pin码*/
    private static final String IND_PINCODE = "MN";
    /**A2DP状态*/
    private static final String IND_A2DP_STATUS = "MU";
    /**蓝牙开关机状态*/
    private static final String IND_POWER_STATUS = "";
    /**已配对列表*/
    private static final String IND_PAIR_DEVICE_LIST = "MX";
    /**A2DP连接已断开*/
    private static final String IND_A2DP_RELEASE = "MY";
    /**获取通话记录资料*/
    private static final String IND_GET_CALLLOG = "PD"; 
    /**软件版本日期*/
    private static final String IND_SOFTWARE_VERSION = "MW";
    /**模块升级成功*/
    private static final String IND_MODULE_UPDATE_SUCCESS = "US";
    
    /**请求蓝牙音乐的声音值*/
    private static final String IND_A2DP_VOLUME = "VS";
    
    private static final HashMap<String, Integer> IND_MAP = new HashMap<String, Integer>();

    static {
    	IND_MAP.put(IND_HFP_RELEASE, 1);
    	IND_MAP.put(IND_HFP_CONNECTED, 2);
    	IND_MAP.put(IND_CONNECTING, 3);
    	IND_MAP.put(IND_OUTGOING_CALL, 4);
    	IND_MAP.put(IND_INCOMMING_CALL, 5);
    	IND_MAP.put(IND_END_CALL, 6);
    	IND_MAP.put(IND_CALL_ONLINE, 7);
    	IND_MAP.put(IND_OUTGOING_CALL_NUMBER, 8);
    	IND_MAP.put(IND_COMING_CALL_NUMBER, 9);
    	IND_MAP.put(IND_INIT_SUCCESS, 10);
    	IND_MAP.put(IND_A2DP_STOP_PLAY, 11);
    	IND_MAP.put(IND_A2DP_STREAMING, 12);
    	IND_MAP.put(IND_CALL_LOG_NUM, 13);
    	IND_MAP.put(IND_HFP_STATUS, 14);
    	//IND_MAP.put(IND_ON_CAR, 15);
    	IND_MAP.put(IND_ON_MOBILE, 16);
    	IND_MAP.put(IND_SET_PHONEBOOK_STATUS, 17);
    	IND_MAP.put(IND_GET_CONTACT, 18);
    	IND_MAP.put(IND_DOWNLOAD_PHONEBOOK_FINISH, 19);
    			 
    	//一条通讯录 
    	IND_MAP.put(IND_PHONE_BOOK, 20);
    	 
    	
    	IND_MAP.put(IND_COMING_PB, 21);
    	IND_MAP.put(IND_CALL_PB, 22);
    	IND_MAP.put(IND_MISING_PB, 23);
    	
    	IND_MAP.put(IND_CONNECTED_DEVICE_NAME, 24);
        
    	// 通话中 与 IG一样
    	IND_MAP.put(IND_A2DP_STATUS, 25);
    	
    	//已经配对设备列表
    	IND_MAP.put(IND_PAIR_DEVICE_LIST, 26);
    	
    	//蓝牙声音值
    	IND_MAP.put(IND_A2DP_VOLUME, 27);
    }
    
    String mCmd;
    
    public static final void receive(byte[] protocal, IBC03Callback callback, IAtPoundCallback atPoundCallback)
    {
        String backCmd = AtPoundProtocal.getIND(protocal);

        String rsss = new String(protocal);
     
        Log.i(TAG, "<---" + rsss.replace("\r\n", ""));
    
        Integer index = IND_MAP.get(backCmd);
	    if (index == null) {
	        	return;
	    }
	    
	    Log.i("call", "backCmd="+backCmd);
	   
	   LOG.print("receive 返回指令  ————>" + backCmd);
	   
        switch(index) {
	        case 1:
	        	callback.onGetHFPState(HFPStatus.READY);
	        	responseHFPRelease(protocal, callback, atPoundCallback);
	        	responseA2DPRelease(protocal, callback);
	        	break;
	        case 2:
	        	callback.onGetHFPState(HFPStatus.CONNECTED);
	        	responseHFPEstablish(protocal, callback, atPoundCallback);
	        	responseA2DPEstablished(protocal, callback);
	        	break;
	        case 3:
	        	responseConnecting(protocal, callback);
	        	break;
	        case 4:
	        	responseOutGoingCall(protocal, callback);
	        	break;
	        case 5:
	        	responseIncomingCall(protocal, callback, atPoundCallback);
	        	break;
	        case 6:
	        	responseEndCall(protocal, callback, atPoundCallback);
	        	break;
	        case 7:
	        	responseCallOnline(protocal, callback, atPoundCallback);
	        	break;
	        case 8: //去电话号码
	        	responseOutGoingCallNumber(protocal, callback);
	        	break;
	        case 9: // 来电话号码
	        	responseCominingCallNumber(protocal, callback, atPoundCallback);
	        	break;
	        case 10: // 上电初始化成功
	        	responseInitSuccess(protocal, callback);
	        	break;
	        case 11: // 暂停
	        	responseA2DPEstablished(protocal, callback);
	        	break;
	        case 12: //挂断保持中/等待中的电话
	        	responseA2DPStreaming(protocal, callback);
	        	break;
	        case 13: // 通话记录中的一条
	        	// responseCallLogNum(protocal, callback);
	        	break;
	        case 14: //HFP状态
	        	responseHFPStatus(protocal, callback, atPoundCallback);
	        	break;
	        case 15: // 通话在车机端
	        	// responseAudioInCar(protocal, callback);
	        	break;
	        case 16: // 通话声音在手机
	        	// responseAudioInMobile(protocal, callback);
	        	break;
	        case 17: // 同步电话本状态
	        	responseSetPhoneStatus(protocal, atPoundCallback);
	        	break;
	        case 18: // 获取到一条联系人
	        	
	        //电话本下载完毕	
	        case 19:
	        	responseDownloadPhonebookFinish(protocal, callback);
	        	break;
	        case 20: // 电话本
	        	responseContactInfo(protocal, callback,atPoundCallback);
	        	break;
	        case 21: // 已接
	        	responseCallLogInfo(protocal, callback, atPoundCallback);
	        	
	        	break;
	        case 22: // 打出
	        	responseCallLogInfo(protocal, callback, atPoundCallback);
	        	break;
	        case 23: // 未接
	        	responseCallLogInfo(protocal, callback, atPoundCallback);
	        	break;
	        case 24:
	        	responseConnectedDeviceAddress(protocal,callback);
	        	break;
	        case 26:
	        	responseConnectedDeviceList(protocal,callback);
	        break;
	        case 27:
	        	responseA2DPVolume(protocal,callback);
	        	break;
        }
    }
    
    /**
     * 获取配对记录
     */
    private static void responseConnectedDeviceList(byte[] protocal,
			IBC03Callback callback) {
		 
    	String list = new String(protocal);
    	LOG.print("配对记录————————————————>："+list);
		
	}
	/**
     * 获取模块升级成功
     * @param protocal
     * @param callback
     */
    private static final void responseModuleUpdateSuccess(byte[] protocal,IBC03Callback callback)
    {
        callback.onUpdateSuccess();
    }
    
    /**
     * 获取软件版本号
     * @param protocal
     * @param callback
     */
    private static final void responseSoftwareVersion(byte[] protocal,IBC03Callback callback)
    {
        String version = AtPoundProtocal.getINDParam(protocal);
        LOG.print("version = " + version);
        callback.onGetSoftwareVersion(version);
    }   
    
    /**
     * 获取通话记录信息
     * @param protocal
     * @param callback
     */
    private static final void responseCallLogInfo(byte[] protocal,IBC03Callback callback,IAtPoundCallback atPoundCallback)
    {
		String values = new String(protocal);
		
    	LOG.print("通话记录："+values);
    	
    	String[] value = analyzePhoneBookInfo(protocal);
        
       /* if (null == value || value.length < 3)
        	return;
        */
        String name = value[0];
        String number = value[1];
        Log.i("cmd", "通话记录： name="+name+"   number="+number);
        callback.onGetPhoneBook(-1, number, name, null);
        atPoundCallback.onGetPhoneBook(name, number);
    }   
    
    /**
     * 已下载完电话本
     * @param protocal
     * @param callback
     */
    private static final void responseDownloadPhonebookFinish(byte[] protocal,IBC03Callback callback)
    {
    	LOG.print("电话本下载完毕");
        callback.onGetPhoneBookFinish();
    }
    
    /**
     * 获取联系人信息
     * @param protocal
     * @param callback
     */
    private static final void responseContactInfo(byte[] protocal,IBC03Callback callback,IAtPoundCallback atPoundCallback)
    {
    	
    	String[] value = analyzePbInfo(protocal);
     
       /* if (null == value || value.length < 3)
        	return;
        */
        String name = value[0];
        String number = value[1];
        
        LOG.print("联系人： name="+name+"   number="+number);
        callback.onGetPhoneBook(-1, number, name, null);
        atPoundCallback.onGetPhoneBook(name, number);
    }   
    
    /**
     * 解析电话本信息
     * @param protocal
     * @return
     */
    private static final String[] analyzePhoneBookInfo(byte[] protocal)
    {
    	String[] result = new String[2];
    	String cmd = null;
		try {
			cmd = new String(protocal,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String name;
		String num;
		
		int numLen = Integer.parseInt(cmd.substring(7,9));
		int nameLen = Integer.parseInt(cmd.substring(5,7));
		
		LOG.print("int  ————————》 numLen="+numLen+"  nameLen="+nameLen);
		
		if(nameLen == 0){
			byte[] bufferNum = new byte[numLen];
			System.arraycopy(protocal, 9, bufferNum, 0,numLen);
			num = new String(bufferNum);
			name = num;
		}else{
			byte[] bufferName = new byte[nameLen];
			System.arraycopy(protocal, 9, bufferName, 0, nameLen);
			name = new String(bufferName);
			
			byte[] bufferNum = new byte[numLen];
			System.arraycopy(protocal, 9+nameLen, bufferNum, 0,numLen);
			num = new String(bufferNum);
		}
		result[0] = name;
    	result[1] = num;
    	return result;
    }
    
    /**
     * 解析电话本信息
     * @param protocal
     * @return
     */
    private static final String[] analyzePbInfo(byte[] protocal)
    {
    	String[] result = new String[2];
    	
    	String cmd = null;
		try {
			cmd = new String(protocal,"utf-8");
			Log.i("phone", "cmd="+cmd);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		
    	int nameLen = Integer.parseInt(cmd.substring(4,6));
		int numLen = Integer.parseInt(cmd.substring(6,8));
		
		String name;
		String num;
		
		byte[] bufferNum = new byte[numLen];
		System.arraycopy(protocal, 8+nameLen, bufferNum, 0,numLen);
		num = new String(bufferNum);
		Log.i("phone", "num="+num); 
		
		byte[] bufferName = new byte[nameLen];
		System.arraycopy(protocal, 8, bufferName, 0, nameLen);
		name = new String(bufferName);
		Log.i("phone", "name="+name); 
		result[0] = name;
    	result[1] = num;

    	LOG.print("name="+result[0]+"  num="+result[1]);
    	
        return result;
    }
    
    /**
     * 设置电话本状态
     * @param protocal
     * @param callback
     */
    private static final void responseSetPhoneStatus(byte[] protocal,IAtPoundCallback callback)
    {
        int status = Integer.parseInt(AtPoundProtocal.getINDParam(protocal));
        // 修改一下，先下载全部，即不分每次下载几条 fanjc 2015-05-01
        callback.onSetPhonebookStatus(status == 0 ? false : true);
        LOG.print("phone status = " + status);
    }
    
    /**
     * A2DP连接断开
     * @param protocal
     * @param callback
     */
    private static final void responseA2DPRelease(byte[] protocal,IBC03Callback callback)
    {
        LOG.print("a2dp release");
        callback.onA2dpRelease();
        callback.onGetA2dpState(A2DPStatus.READY);
    }
    
    /**
     * 获取已配对设备信息
     * @param protocal
     * @param callback
     */
    private static final void responsePairDeviceList(byte[] protocal,IBC03Callback callback)
    {
        String deviceInfo = AtPoundProtocal.getINDParam(protocal);
        int index = Integer.parseInt(deviceInfo.substring(0,1));
        if(protocal.length > 6)
        {
            String address = deviceInfo.substring(1, 13);
            String name = deviceInfo.substring(13);
            LOG.print("pair device list,index = " + index + ",address = " + address + ",name = " + name);
            //index = 0 则为当前连接设备信息
            if(index == 0)
            {
                callback.onGetConnectedDevice("HFP", name, address);
//                callback.onGetHFPState(HFPStatus.CONNECTED);
                callback.onGetConnectedDeviceFinish();
            }
            else
            {
                callback.onGetPairDevice(index, address, name);
            }
        }
    }
    
    /**
     * 获取电源状态
     * @param protocal
     * @param callback
     */
    private static final void responsePowerStatus(byte[] protocal,IBC03Callback callback)
    {
        int status = Integer.parseInt(AtPoundProtocal.getINDParam(protocal));
        callback.onGetPowerState(status);
        LOG.print("power status = " + status);
    }
    
    /**
     * A2DP状态
     * @param protocal
     * @param callback
     */
    private static final void responseA2DPStatus(byte[] protocal,IBC03Callback callback)
    {
        int status = Integer.parseInt(AtPoundProtocal.getINDParam(protocal));
        switch(status)
        {
            case 1:
                status = A2DPStatus.READY;
                break;
            case 2:
                status = A2DPStatus.CONNECTING;
                break;
            case 3:
                status = A2DPStatus.CONNECTED;
                break;
            case 4:
                status = A2DPStatus.STREAMING;
                break;
        }
        callback.onGetA2dpState(status);
        LOG.print("a2dp status = " + status);
    }
    
    /**
     * 获取PIN码
     * @param protocal
     * @param callback
     */
    private static final void responsePinCode(byte[] protocal,IBC03Callback callback)
    {
        String pinCode = AtPoundProtocal.getINDParam(protocal);
        callback.onGetPinCode(pinCode);
        LOG.print("pincode = " + pinCode);
    }
    
    /**
     * 获取设备名字
     * @param protocal
     * @param callback
     */
    private static final void responseDeviceName(byte[] protocal,IBC03Callback callback)
    {
        String name = AtPoundProtocal.getINDParam(protocal);
        callback.onGetDeviceName(name);
        LOG.print("device name = " + name);
    }
    
    /**
     * AVRCP状态
     * @param protocal
     * @param callback
     */
    private static final void responseAVRCPStatus(byte[] protocal,IBC03Callback callback)
    {
        int status = Integer.parseInt(AtPoundProtocal.getINDParam(protocal));
        switch(status)
        {
            case 1:
                status = AVRCPStatus.READY;
                break;
            case 2:
                status = AVRCPStatus.CONNECTING;
                break;
            case 3:
                status = AVRCPStatus.CONNECTED;
                break;
        }
        callback.onGetAVRCPState(status);
        LOG.print("avrcp status = " + status);
    }
    
    /**
     * 语音连接断开
     * @param protocal
     * @param callback
     */
    private static final void responseAutoFeature(byte[] protocal,IBC03Callback callback)
    {
        String feature = AtPoundProtocal.getINDParam(protocal);
        callback.onAutoFeatureChange(AutoFeature.AUTO_ACCEPT_CALL, feature.substring(1, 2).equals("0") ? false : true);
        callback.onAutoFeatureChange(AutoFeature.AUTO_CONNECT_DEVICE, feature.substring(0, 1).equals("0") ? false : true);
        LOG.print("auto feature = " + feature);
    }
    
    /**
     * HFP状态
     * @param protocal
     * @param callback
     */
    private static final void responseHFPStatus(byte[] protocal,IBC03Callback callback,
    		IAtPoundCallback atPoundCallback)
    {
        int status = Integer.parseInt(AtPoundProtocal.getINDParam(protocal));
        Log.i("call", "status="+status);
        switch(status)
        {
            case 1:
                status = HFPStatus.READY;
                break;
            case 2:
                status = HFPStatus.CONNECTING;
                break;
            case 3:
                status = HFPStatus.CONNECTED;
                break;
            case 4:
                status = HFPStatus.CALL_OUTGOING;
                break;
            case 5:
                status = HFPStatus.CALL_INCOMING;
                break;
            case 6:
                status = HFPStatus.CALL_ACTIVE;
                break;
        }
        callback.onGetHFPState(status);
        if (status == HFPStatus.CONNECTED) {
        	responseHFPEstablish(protocal, callback, atPoundCallback);
        	responseA2DPEstablished(protocal, callback);
        }
    }
    
    /**
     * 语音连接断开
     * @param protocal
     * @param callback
     */
    private static final void responseVoiceDisconnect(byte[] protocal,IBC03Callback callback)
    {
//        callback.onCancelCall();
//        LOG.print("cancel call");
    }
    
    /**
     * 语音连接建立
     * @param protocal
     * @param callback
     */
    private static final void responseVoiceConnect(byte[] protocal,IBC03Callback callback)
    {
//        callback.onCallOnline();
//        LOG.print("call online");
    }
    
    /**
     * 挂断保持中/等待中的电话
     * @param protocal
     * @param callback
     */
    private static final void responseA2DPStreaming(byte[] protocal,IBC03Callback callback)
    {
        callback.onGetA2dpState(A2DPStatus.STREAMING);
        LOG.print("bt music playing");
    }
    
    /**
     * A2DP已连接
     * @param protocal
     * @param callback
     */
    private static final void responseA2DPEstablished(byte[] protocal,IBC03Callback callback)
    {
        callback.onA2dpEstablish();
        callback.onGetA2dpState(A2DPStatus.CONNECTED);
        LOG.print("a2dp establish");
    }
    
    private static final void responseA2DPVolume(byte[] protocal,IBC03Callback callback){
    	
    	String values = new String(protocal);
    	Log.i("dp", "values="+values+"  length="+values.length());//VS19
    	String volume = values.substring(4,6);//19
    	int vol = Integer.parseInt(volume);
    	callback.onGetA2dpVolume(vol);
    }
    
    /**
     * 静音
     * @param protocal
     * @param callback
     */
    private static final void responseSpkMute(byte[] protocal,IBC03Callback callback)
    {
        callback.onGetMuteState(MuteStatus.MUTE);
        LOG.print("speak mute");
    }
    
    /**
     * 非静音
     * @param protocal
     * @param callback
     */
    private static final void responseSpkUnmute(byte[] protocal,IBC03Callback callback)
    {
        callback.onGetMuteState(MuteStatus.UNMUTE);
        LOG.print("speak unmute");
    }
    
    /**
     * 连接中
     * @param protocal
     * @param callback
     */
    private static final void responseConnecting(byte[] protocal,IBC03Callback callback)
    {
        //do nothing
        LOG.print("connecting");
    }
    
    /**
     * 挂断保持中/等待中的电话
     * @param protocal
     * @param callback
     */
    private static final void responseReleaseActiveCallAndAcceptWaittingCall(byte[] protocal,IBC03Callback callback)
    {
        callback.onMultipulCall_AcceptWaittingCallAndCancelCurrentCall();
        LOG.print("AcceptWaittingCallAndCancelCurrentCall");
    }
    
    /**
     * 上电初始化成功
     * @param protocal
     * @param callback
     */
    private static final void responseInitSuccess(byte[] protocal,IBC03Callback callback)
    {
        //do nothing
        LOG.print("init success");
        callback.onInitSuccess();
    }    
    
    /**
     * 获取去电号码
     * @param protocal
     * @param callback
     */
    private static final void responseOutGoingCallNumber(byte[] protocal,IBC03Callback callback)
    {
    	Log.i("call", "case 8：responseOutGoingCallNumber");
    	String number = AtPoundProtocal.getINDParam(protocal);
    	
    	if(number != null){
    		callback.onGetHFPState(HFPStatus.CALL_OUTGOING);
    	}
    	
        callback.onGetIncommingOrOutGoingCallNumber(number);
        LOG.print("out going call number");
    }
    
    /**
     * 获取去电号码
     * @param protocal
     * @param callback
     */
    private static final void responseCominingCallNumber(byte[] protocal,IBC03Callback callback, IAtPoundCallback atCallback)
    {
        String number = AtPoundProtocal.getINDParam(protocal);
        
        String value = number.substring(2);
        
        Log.i("comin", "number="+value);
        
        atCallback.onCallNum(value);
        callback.onGetIncommingOrOutGoingCallNumber(value);
        LOG.print("out going call number");
    }
    
    
    /**
     * 挂断保持中/等待中的电话
     * @param protocal
     * @param callback
     */
    private static final void responseIncomingCallName(byte[] protocal,IBC03Callback callback)
    {
        String name = AtPoundProtocal.getINDParam(protocal);
        callback.onGetIncommingCallName(name);
        LOG.print("incoming call name");
    }
    
    /**
     * 挂断保持中/等待中的电话
     * @param protocal
     * @param callback
     */
    private static final void responseCancelWaittingCall(byte[] protocal,IBC03Callback callback)
    {
        callback.onMultipulCall_CancelHoldCall();
        LOG.print("CancelWaittingCall");
    }
    
    /**
     * 进入三方通话
     * @param protocal
     * @param callback
     */
    private static final void responseEnterThirdCommunication(byte[] protocal,IBC03Callback callback)
    {
        callback.onMultipulCall_EnterThirdCalling();
        LOG.print("EnterThirdCommunication");
    }
    
    /**
     * 保持当前通话并接听等待电话
     * @param protocal
     * @param callback
     */
    private static final void responseHoldActiveCallAndAcceptWaittingCall(byte[] protocal,IBC03Callback callback)
    {
        //do nothing
        LOG.print("HoldActiveCallAndAcceptWaittingCall");
    }
    
    /**
     * 呼叫等待
     * @param protocal
     * @param callback
     */
    private static final void responseWaittingCall(byte[] protocal,IBC03Callback callback)
    {
        String number = AtPoundProtocal.getINDParam(protocal);
        callback.onMultipulCall_NewIncommingCall(number);
        LOG.print("new incoming call,number = " + number);
    }
    
    /**
     * 结束配对模式
     * @param protocal
     * @param callback
     */
    private static final void responsePairFinish(byte[] protocal,IBC03Callback callback)
    {
        int result = Integer.parseInt(AtPoundProtocal.getINDParam(protocal));
        int pairState = PairStatus.PAIR_CANCEL;
        if(result == 1)
        {
            pairState = PairStatus.SUCCESS;
        }
        callback.onGetPairState(pairState);
        LOG.print("pair status = " + pairState);
    }
    
    /**
     * 进入配对模式
     * @param protocal
     * @param callback
     */
    private static final void responsePairStart(byte[] protocal,IBC03Callback callback)
    {
        //do nothing
        LOG.print("pair start");
    }
    
    /**
     * 通话中
     * @param protocal
     * @param callback
     */
    private static final void responseCallOnline(byte[] protocal,IBC03Callback callback, IAtPoundCallback atCallback)
    {
        callback.onGetHFPState(HFPStatus.CALL_ACTIVE);
        atCallback.onCalling();
        LOG.print("call online");
    }
    
    /**
     * 取消通话
     * @param protocal
     * @param callback
     */
    private static final void responseEndCall(byte[] protocal,IBC03Callback callback, IAtPoundCallback atCallback)
    {
        callback.onCancelCall();
        atCallback.onCallEnd();
        LOG.print("end call");
    }
    
    /**
     * 来电
     * @param protocal
     * @param callback
     */
    private static final void responseIncomingCall(byte[] protocal,IBC03Callback callback, IAtPoundCallback atCallback)
    {
        callback.onGetHFPState(HFPStatus.CALL_INCOMING);
        String number = AtPoundProtocal.getINDParam(protocal);
        callback.onGetIncommingOrOutGoingCallNumber(number);
        atCallback.onCallIn();
        LOG.print("incoming call,number = " + number);
    }
    
    /**
     * 打出电话
     * @param protocal
     * @param callback
     */
    private static final void responseOutGoingCall(byte[] protocal,IBC03Callback callback)
    {
    	Log.i("call", "case 4: responseOutGoingCall");
        callback.onGetHFPState(HFPStatus.CALL_OUTGOING);
        LOG.print("outgoing call");
    }
    
    /**
     * HFP连接成功
     * @param protocal
     * @param callback
     */
    private static final void responseHFPEstablish(byte[] protocal,IBC03Callback callback, 
    		IAtPoundCallback atPoundCallback)
    {
    	atPoundCallback.onHfpConnected("HFP");
        callback.onHTPEstablish();
        LOG.print("hfp establish");
    }
    
    /**
     * HFP连接断开
     * @param protocal
     * @param callback
     */
    private static final void responseHFPRelease(byte[] protocal,IBC03Callback callback, IAtPoundCallback atPoundCallback)
    {
        callback.onHTPRelease();
        callback.onGetHFPState(HFPStatus.READY);
        atPoundCallback.onHfpRelease();
        LOG.print("hfp release");
    }
    
    /**
     * 回应通话效果等级
     * @param protocal
     * @param callback
     */
    private static final void responseCallEffect(byte[] protocal,IBC03Callback callback)
    {
        int level = Integer.parseInt(AtPoundProtocal.getINDParam(protocal));
        callback.onGetCallEffectLevel(level);
        LOG.print("call effect level = " + level);
    }
    
    /**
     * 当前连接设备的地址
     * @param protocal
     * @param callback
     */
    private static final void responseConnectedDeviceAddress(byte[] protocal,IBC03Callback callback)
    {
        String address = AtPoundProtocal.getINDParam(protocal);
        LOG.print("connect device address = " + address);
    }
    
    /**
     * 设定获取未接号码记录
     * @param uartCommunication
     */
    public static final void requestGetMissCallLog(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_GET_MISS_CALL_LOG);
        LOG.print("requestGetMissCallLog");
    }
    
    /**
     * 设定获取已接号码记录
     * @param uartCommunication
     */
    public static final void requestGetReceiveCallLog(UartCommunication uartCommunication)
    {
    	LOG.print("请求 获取已接电话号码记录");
        sendData2Uart(uartCommunication, CMD_GET_RECEIVE_CALL_LOG);
    }
    
    /**
     * 设定获取拨出号码记录
     * @param uartCommunication
     */
    public static final void requestGetDialCallLog(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_GET_DIAL_CALL_LOG);
        LOG.print("requestGetDialCallLog");
    }
    
    /**
     * 获取上N条记录
     * @param uartCommunication
     */
    public static final void requestGetPreviousRecord(UartCommunication uartCommunication,int count)
    {
        sendData2Uart(uartCommunication, CMD_GET_PREVIOUS_RECORD + count);
        LOG.print("requestGetPreviousRecord,count = " + count);
    }
    
    /**
     * 获取下N条记录
     * @param uartCommunication
     */
    public static final void requestGetNextRecord(UartCommunication uartCommunication,int count)
    {
        sendData2Uart(uartCommunication, CMD_GET_NEXT_RECORD + count);
        LOG.print("requestGetNextRecord,count = " + count);
    }
    
    /***
     * 获取所有电话本
     * @param uartCommunication
     */
    public static final void requestGetAllPb(UartCommunication uartCommunication)
    {
       // sendData2Uart(uartCommunication, CMD_GET_ALL_CONTACT);
        LOG.print("requestGetAllPb");
    }
    /**
     * 设定获取手机联系人
     * @param uartCommunication
     */
    public static final void requestGetContactFromPhone(UartCommunication uartCommunication)
    {
    	LOG.print("AtPoundProtocalUtil 获取手机联系人");
        sendData2Uart(uartCommunication, CMD_GET_CONTACT_FROM_PHONE);
    }
    
    /**
     * 设定获取sim卡联系人
     * @param uartCommunication
     */
    public static final void requestGetContactFromSim(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_GET_CONTACT_FROM_SIM);
        LOG.print("requestGetContactFromSim");
    }
    
    /**
     * 查询软件版本
     * @param uartCommunication
     */
    public static final void requestQuerySoftwareVersion(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_SOFTWARE_VERSION);
        LOG.print("requestQuerySoftwareVersion");
    }
    
    /**
     * 查询已连接设备
     * @param uartCommunication
     */
    public static final void requestQueryConnectDeivce(UartCommunication uartCommunication)
    {
        //sendData2Uart(uartCommunication, CMD_QUERY_CONNECT_DEVICE);
        LOG.print("CMD_QUERY_CONNECT_DEVICE");
    }
    
    /**
     * 查询配对设备列表
     * @param uartCommunication
     */
    public static final void requestQueryPairDeviceList(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_PAIR_DEVICE_LIST);
        LOG.print("requestQueryPairDeviceList");
    }
    
    /**
     * 查询开/关机状态
     * @param uartCommunication
     */
    public static final void requestQueryPowerStatus(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_SET_OR_QUERY_POWER_STATUS);
        LOG.print("requestQueryPowerStatus");
    }
    
    /**
     * 设置开/关机状态
     * @param uartCommunication
     */
    public static final void requestSetPowerStatus(UartCommunication uartCommunication,int powerStatus)
    {
        sendData2Uart(uartCommunication, CMD_SET_OR_QUERY_POWER_STATUS + powerStatus);
        LOG.print("requestSetPowerStatus,status = " + powerStatus);
    }
    
    /**
     * 查询A2DP状态
     * @param uartCommunication
     */
    public static final void requestQueryA2DPStatus(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_A2DP_STATUS);
        LOG.print("requestQueryA2DPStatus");
    }
    
    /**
     * 设置自动接听来电
     * @param uartCommunication
     */
    public static final void requestSetAutoAcceptCall(UartCommunication uartCommunication,boolean isOn)
    {
        if(isOn)
        {
            sendData2Uart(uartCommunication, CMD_SET_AUTO_ACCEPT_CALL_ON);
        }
        else
        {
            sendData2Uart(uartCommunication, CMD_SET_AUTO_ACCEPT_CALL_OFF);
        }
        LOG.print("requestSetAutoAcceptCall " + isOn);
    }
    
    /**
     * 查询AVRCP状态
     * @param uartCommunication
     */
    public static final void requestQueryAVRCPStatus(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_AVRCP_STATUS);
        LOG.print("requestQueryAVRCPStatus");
    }
    
    /**
     * 查询Pincode
     * @param uartCommunication
     */
    public static final void requestQueryPinCode(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_PINCODE);
        LOG.print("requestQueryPinCode");
    }
    
    /**
     * 设置Pincode
     * @param uartCommunication
     */
    public static final void requestSetPinCode(UartCommunication uartCommunication,String pinCode)
    {
        sendData2Uart(uartCommunication, CMD_SET_PINCODE + pinCode);
        LOG.print("requestSetPinCode " + pinCode);
    }    
    
    /**
     * 查询设备名字
     * @param uartCommunication
     */
    public static final void requestQueryDeviceName(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_DEVICE_NAME);
        LOG.print("requestQueryDeviceName");
    }
    
    /**
     * 设置设备名字
     * @param uartCommunication
     */
    public static final void requestSetDeviceName(UartCommunication uartCommunication,String name)
    {
        sendData2Uart(uartCommunication, CMD_SET_DEVICE_NAME + name);
        LOG.print("requestSetDeviceName " + name);
    }
    
    /**
     * 设置自动连接设备功能
     * @param uartCommunication
     */
    public static final void requestSetAutoConnectDevice(UartCommunication uartCommunication,boolean isOn)
    {
        if(isOn)
        {
            sendData2Uart(uartCommunication, CMD_SET_AUTO_CONNECT_DEVICE_ON);
        }
        else
        {
            sendData2Uart(uartCommunication, CMD_SET_AUTO_CONNECT_DEVICE_OFF);
        }
        LOG.print("requestSetAutoConnectDevice " + isOn);
    }
    
    /**
     * 查询自动功能状态
     * @param uartCommunication
     */
    public static final void requestQueryAutoFeature(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_AUTO_FEATURE);
        LOG.print("requestQueryAutoFeature");
    }
    
    /**
     * 蓝牙音乐播放控制
     * @param uartCommunication
     * @param command
     */
    public static final void requestPlayControl(UartCommunication uartCommunication, int command)
    {
        String cmd = null;
        
        switch(command)
        {
            case PlayCommand.PLAY:
                cmd = CMD_AVRCP_PLAY;   //MA
                break;
            case PlayCommand.PAUSE:
                cmd = CMD_AVRCP_PAUSE;   //MJ
                break;
            case PlayCommand.NEXT:
                cmd = CMD_AVRCP_NEXT;    //MD
                break;
            case PlayCommand.PREVIOUS:
                cmd = CMD_AVRCP_PREVIOUS;  //ME 
                break;
            case PlayCommand.STOP:
                cmd = CMD_AVRCP_STOP;    //MC
                break;
        }
        Log.i("cmd", "播放控制   cmd = "+command);
        
        sendData2Uart(uartCommunication, cmd);
        LOG.print("requestPlayControl,command = " + command);
    }
    
    /**
     * 发送请求蓝牙音乐声音值的请求
     * @param uartCommunication
     */
    public static final void requestBtMusicVolume(UartCommunication uartCommunication){
    	Log.i("dp", "AtPoundProtocalUtil  requestBtMusicVolume");
    	sendData2Uart(uartCommunication, CMD_A2DP_VOLUME);
    }
    
    /**
     * 设置蓝牙音乐的音量值
     * @param uartCommunication
     * @param vol
     */
    public static final void setMusicVolume(UartCommunication uartCommunication,int vol){
    	Log.i("dp", ""+CMD_A2DP_VOLUME+vol);
    	sendData2Uart(uartCommunication, CMD_A2DP_VOLUME+vol);
    }
    
    /**
     * 查询通话效果
     * @param uartCommunication
     */
    public static final void requestQueryCallEffect(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_OR_SET_CALL_EFFECT);
        LOG.print("requestQueryCallEffect");
    }
    
    /**
     * 设置通话效果
     * @param uartCommunication
     */
    public static final void requestSetCallEffect(UartCommunication uartCommunication,int level)
    {
        level = Math.max(level, 0);
        level = Math.min(level, 2);
        sendData2Uart(uartCommunication, CMD_QUERY_HFP_STATUS + level);
        LOG.print("requestSetCallEffect = " + level);
    }
    
    /**
     * 断开A2DP
     * @param uartCommunication
     */
    public static final void requestDisconnectA2DP(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_DISCONNECT_A2DP);
        LOG.print("requestDisconnectA2DP");
    }
    
    /**
     * 复位
     * @param uartCommunication
     */
    public static final void requestReset(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_RESET);
        LOG.print("requestReset");
    }
    
    /**
     * 查询HFP状态
     * @param uartCommunication
     */
    public static final void requestQueryHFPStatus(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_QUERY_HFP_STATUS);
        LOG.print("requestQueryHFPStatus");
    }
    
    /**
     * 拨打分机号
     * @param uartCommunication
     */
    public static final void requestSendDTMF(UartCommunication uartCommunication,String number)
    {
        sendData2Uart(uartCommunication, CMD_DIAL_EXTENSION_NUMBER + number);
        LOG.print("requestSendDTMF,number = " + number);
    }
    
    /**
     * 拨号
     * @param uartCommunication
     */
    public static final void requestDial(UartCommunication uartCommunication,String number)
    {
        sendData2Uart(uartCommunication, CMD_DIAL + number);
        LOG.print("requestDial,number = " + number);
    }
    
    /**
     * 删除配对列表
     * @param uartCommunication
     */
    public static final void requestDeletePairDeviceList(UartCommunication uartCommunication,int index)
    {
        if(index > 0)
        {
            sendData2Uart(uartCommunication, CMD_DELETE_PAIR_DEVICE_LIST + index);
        }
        else
        {
            sendData2Uart(uartCommunication, CMD_DELETE_PAIR_DEVICE_LIST);
        }
        LOG.print("requestDeletePairDeviceList,index = " + index);
    }
    
    /**
     * 进入三方通话
     * @param uartCommunication
     */
    public static final void requestThirdCommunicate(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_ENTER_THIRD_CONMUNICATE);
        LOG.print("requestThirdCommunicate");
    }
    
    /**
     * 保持当前电话并接听等待中的来电
     * @param uartCommunication
     */
    public static final void requestHoldActiveCallAndAcceptWaittingCall(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_HOLD_ACTIVE_CALL_AND_ACCEPT_WAITTING_CALL);
        LOG.print("requestHoldActiveCallAndAcceptWaittingCall");
    }
    
    /**
     * 挂断当前电话并接听等待中来电
     * @param uartCommunication
     */
    public static final void requestReleaseActiveCallAndAcceptWaittingCall(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_RELEASE_ACTIVE_CALL_AND_ACCEPT_WAITTING_CALL);
        LOG.print("requestReleaseActiveCallAndAcceptWaittingCall");
    }
    
    /**
     * 拒接等待中的电话
     * @param uartCommunication
     */
    public static final void requestRejectWaittingCall(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_REJECT_WAITTING_CALL);
        LOG.print("requestRejectWaittingCall");
    }
    
    /**
     * 切换通话声道
     * @param uartCommunication
     */
    public static final void requestChangeSoundChannel(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_CHANGE_SOUND_CHANNEL);
        LOG.print("requestChangeSoundChannel");
    }
    
    public static final void requestChangeSoundChannelToBT(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_CHANGE_SOUND_TO_BLUE);
    }
    
    /**
     * 麦克风打开/关闭
     * @param uartCommunication
     */
    public static final void requestSpkMuteOrUnmute(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_SPK_OPEN_CLOSE);
        LOG.print("requestSpkMuteOrUnmute");
    }
    
    /**
     * 音量-
     * @param uartCommunication
     */
    public static final void requestVolumeDown(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_VOLUME_DOWN);
        LOG.print("requestVolumeDown");
    }
    
    /**
     * 音量+
     * @param uartCommunication
     */
    public static final void requestVolumeUp(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_VOLUME_UP);
        LOG.print("requestVolumeUp");
    }
    
    /**
     * 重拨
     * @param uartCommunication
     */
    public static final void requestRedial(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_REDIAL);
        LOG.print("requestRedial");
    }
    
    /**
     * 挂断电话
     * @param uartCommunication
     */
    public static final void requestCancelCall(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_CANCEL_CALL);
        LOG.print("requestCancelCall");
    }
    
    /**
     * 拒接电话
     * @param uartCommunication
     */
    public static final void requestRejectCall(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_REJECT_CALL);
        LOG.print("requestRejectCall");
    }
    
    /**
     * 接听来电
     * @param uartCommunication
     */
    public static final void requestAcceptCall(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_ACCEPT_CALL);
        LOG.print("requestAcceptCall");
    }
    
    /**
     * 断开HFP
     * @param uartCommunication
     */
    public static final void requestDisconnectHFP(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_DISCONNECT_HFP);
        LOG.print("requestDisconnectHFP");
    }
    
    /**
     * 连接HFP
     * @param uartCommunication
     */
    public static final void requestConnectHFP(UartCommunication uartCommunication,int index)
    {
        sendData2Uart(uartCommunication, CMD_CONNECT_HFP + index);
        LOG.print("requestConnectHFP,index = " + index);
    }
    
    /**
     * 停止匹配模式
     * @param uartCommunication
     */
    public static final void requestStopDiscover(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication,CMD_STOP_DISCOVER);
        LOG.print("requestConnectHFP");
    }
    
    /**
     * 请求进入匹配模式
     * @param uartCommunication
     */
    public static final void requestStartDiscover(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_START_DISCOVER);
        LOG.print("requestStartDiscover");
    }
    
    /**
     * 请求蓝牙音乐静音
     * @param uartCommunication
     */
    public static final void requestBtMusicMute(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_BT_MUSIC_MUTE);
        LOG.print("requestBtMusicMute");
    }
    
    /**
     * 请求蓝牙音乐非静音
     * @param uartCommunication
     */
    public static final void requestBtMusicUnMute(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_BT_MUSIC_UNMUTE);
        LOG.print("requestBtMusicUnMute");
    }
    
    /**
     * 请求升级模块
     * @param uartCommunication
     */
    public static final void requestUpdate(UartCommunication uartCommunication)
    {
        sendData2Uart(uartCommunication, CMD_REQUEST_UPDATE);
        LOG.print("requestUpdate");
    }
    
    /**
     * 请求发送AT指令
     * @param uartCommunication
     */
    public static final void requestSendAtCommand(UartCommunication uartCommunication,String cmd)
    {
        sendData2Uart(uartCommunication, cmd);
    }
    
    /**
     * 发送数据到串口中
     * @param uartCommunication
     * @param cmd
     */
    private static final void sendData2Uart(UartCommunication uartCommunication,String cmd)
    {
        cmd = AtPoundProtocal.createATCommand(cmd);
        LOG.print("发送指令到串口  ————————————>  cmd="+cmd);

        Log.i("book", "发送指令到串口  ————————————>  cmd="+cmd);
        
        byte[] protocal = cmd.getBytes();
        try
        {
            uartCommunication.writeData(protocal);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
