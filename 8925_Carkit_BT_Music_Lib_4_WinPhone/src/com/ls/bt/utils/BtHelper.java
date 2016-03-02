package com.ls.bt.utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;

import com.android.utils.log.JLog;
import com.nwd.kernel.source.SourceConstant;
import com.nwd.kernel.utils.KernelConstant;
import com.nwd.kernel.utils.KernelUtils;

public class BtHelper {
    private static final JLog LOG = new JLog("BtHelper",true,JLog.TYPE_DEBUG);
    public static final String APP_PACKAGE_NAME = "com.ls.android.phone";
    public static final String APP_CLASS_NAME = "com.ls.android.phone.StartBT";
	public final static int MESSAGE_ADD_CALLOUT = 1;
	public final static int MESSAGE_ADD_MISSED = 2;
	public final static int MESSAGE_ADD_RECEIVED = 3;
	public final static int MESSAGE_ADD_CONTACT = 4;
	public final static int MESSAGE_UPDATE_NAME_NUMBER = 5;
	public final static int MESSAGE_CALLOUTCLICK = 6;
	public final static int MESSAGE_MISSEDCALLCLICK = 7;
	public final static int MESSAGE_RECEIVEDCLICK = 8;
	public final static int MESSAGE_CONTACTCLICK = 9;
	public final static int MESSAGE_SET_CAN_SWITCH_SOUND = 10;
	public static boolean mBlueToothPlayOrPasue = false;
	private static BtHelper instance;
	private Properties properties = null; 
    private BtHelper(){ 
        initResource(); 
    } 
    public static BtHelper getInstance(){ 
        if(instance==null){ 
            instance = new BtHelper(); 
        } 
        return instance; 
    } 
    private void initResource(){ 
        try{ 
            final String resourceName = "/assets/unicode_to_hanyu_pinyin.txt"; 
//          final String resourceName = "/assets/unicode_py.ini"; 
            properties=new Properties(); 
            properties.load(getResourceInputStream(resourceName)); 	 
        } catch (FileNotFoundException ex){ 
            ex.printStackTrace(); 
        } catch (IOException ex){ 
            ex.printStackTrace(); 
        } 
    } 
    /**
     * 通话记录
     * @author yewei
     *
     */
    public interface CallLog
    {
        public static final int TYPE_INCOMING_CALL = 0;
        public static final int TYPE_OUTGOING_CALL = 1;
        public static final int TYPE_MISSING_CALL = 2;
    }
    public static String[] getUnformattedHanyuPinyinStringArray(char ch){ 
        return getInstance().getHanyuPinyinStringArray(ch); 
    }
    private String[] getHanyuPinyinStringArray(char ch){ 
        String pinyinRecord = getHanyuPinyinRecordFromChar(ch); 
        
        if (null != pinyinRecord){ 
            int indexOfLeftBracket = pinyinRecord.indexOf(Field.LEFT_BRACKET); 
            int indexOfRightBracket = pinyinRecord.lastIndexOf(Field.RIGHT_BRACKET); 
 
            String stripedString = pinyinRecord.substring(indexOfLeftBracket 
                    + Field.LEFT_BRACKET.length(), indexOfRightBracket); 
 
            return stripedString.split(Field.COMMA); 
 
        } else 
            return null; 
    }
    
    private BufferedInputStream getResourceInputStream(String resourceName){ 
        return new BufferedInputStream(BtHelper.class.getResourceAsStream(resourceName)); 
    } 
    
    private String getHanyuPinyinRecordFromChar(char ch){ 
        int codePointOfChar = ch; 
        String codepointHexStr = Integer.toHexString(codePointOfChar).toUpperCase(); 
        String foundRecord = properties.getProperty(codepointHexStr); 
        return foundRecord; 
    } 
    /**
     * 子界面ID
     * @author yewei
     *
     */
    public interface BtScreen
    {

    	public static final byte BT_MUSIC = 0x08;
    	public static final byte BT_DIAL_NUM = 0x02;
        public static final byte BT_MATCH = 0x03;
        public static final byte BT_MATCH_RECORD = 0x04;
        public static final byte BT_CONTACT = 0x05;
        public static final byte BT_CALL_DETAIL = 0x06;
        public static final byte BT_SETTING = 0x07;
    }
    
    /**
     * app进入时告诉核心服务
     * @param aContext
     */
    public static final void appStart(Context aContext)
    {                                               
        LOG.print("app Start!");
        KernelUtils.appStart(aContext, SourceConstant.APPID_BT);
    }    
/*    *//**
     * 通知MCU蓝牙音乐是否有声音
     * 由于蓝牙音乐只需要这一条协议,所以这里使用测试用的方法来发送协议数据.
     * @param isPlay
     *//*
    public static final void notifyIsBTMusicPlay(Context context,boolean isPlay)
    {
        byte[] protocal = KernelProtocal.generateNullProtocal(4, KernelProtocal.TYPE_BT, (byte) 0x06);
        int offset = KernelProtocal.getProtocalDataStartOffset(protocal);
        protocal[offset] = (byte) (isPlay ? 1 : 0);
        KernelProtocal.calCheckSumAndWriteEndOfData(protocal);
        Intent intent = new Intent(KernelConstant.ACTION_EMU_SEND_DATA_TO_MCU);
        intent.putExtra(KernelConstant.EXTRA_PROTOCAL, protocal);
        context.sendBroadcast(intent);
    }*/
    
/*    *//**
     * 应用程序启动
     * @param context
     *//*
    public static final void appStart(Context context)
    {
        Intent intent = new Intent(KernelConstant.ACTION_MCU_PUBLIC_KEY_VALUE);
        intent.putExtra(KernelConstant.EXTRA_KEY_VALUE, KernelConstant.KEYVALUE.KEY_BT_MUSIC);
        context.sendBroadcast(intent);
    }*/
    public static final void startMaintActivity(Context context,String btStatus)
    {
    	Intent intent = new Intent();
        intent.setClassName(APP_PACKAGE_NAME, APP_CLASS_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KernelConstant.EXTRA_EVENT, btStatus);
        context.startActivity(intent);
    }

	
    class Field{ 
        static final String LEFT_BRACKET = "("; 
        static final String RIGHT_BRACKET = ")"; 
        static final String COMMA = ","; 
    } 
}