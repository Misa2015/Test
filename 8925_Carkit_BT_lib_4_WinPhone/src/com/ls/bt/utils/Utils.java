/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.ls.bt.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;

import com.android.utils.log.JLog;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.bt.contact.BTReceiver;
import com.ls.bt.contact.Contact;
import com.nwd.kernel.source.SettingTableKey;
import com.nwd.kernel.source.SettingTableKey.PublicSettingTable;
import com.nwd.kernel.utils.KernelConstant;

/**
 * This utility class provides customized sort key and name lookup key according
 * the locale.
 */
public class Utils {
	private static final JLog LOG = new JLog("Utils", Utils.DEBUG,
			JLog.TYPE_DEBUG);
	public static final boolean DEBUG = true;
	private static final String TAG = "Utils";
	public final static String KEY_CONNECTED_DEVICE_ADDRESS = "key_connected_device_address";
	private static final Intent VOLUME_STATE_CHANGE_INTENT = new Intent(
			KernelConstant.ACTION_VOLUME_STATE_CHANGE);
	private static Object lockObj = new Object();
	private static final String MUTE_SOUND_FILE = "/proc/audio_mute";
	private static final String IIC_SOUND_FILE = "/proc/sound_iic";
	private static final String IIC_HISTORY_SOUND_FILE = "/proc/sound_iic_history";
	/**
	 * 获取拼音集合
	 *
	 * @author wyh
	 * @param src
	 * @return Set<String>
	 */
	public static String[] getInitial(String src) {
		char[] srcChar;
		srcChar = src.toCharArray();

		// 1:多少个汉字
		// 2:每个汉字多少种读音
		String[][] temp = new String[src.length()][];
		for (int i = 0; i < srcChar.length; i++) {
			char c = srcChar[i];
			// 是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
			if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
				String[] t = BtHelper.getUnformattedHanyuPinyinStringArray(c);
				temp[i] = new String[t.length];
				for (int j = 0; j < t.length; j++) {
					temp[i][j] = t[j].substring(0, 1);// 获取首字母
				}
			} else if (((int) c >= 65 && (int) c <= 90)
					|| ((int) c >= 97 && (int) c <= 122) || c >= 48 && c <= 57
					|| c == 42) {// a-zA-Z0-9*
				temp[i] = new String[] { String.valueOf(srcChar[i]) };
			} else {
				// temp[i] = new String[] {"null!"};
				temp[i] = new String[] { "" };
			}
		}
		String[] pingyinArray = paiLie(temp);
		String temp22 = "";
		for (int i = 0; i < pingyinArray.length; i++) {
			temp22 = temp22 + pingyinArray[i];
		}
		LOG.print("Name=" + src + " Pinyin=" + temp22);
		return array2Set(pingyinArray);// 为了去掉重复项
	}

	public static String[] array2Set(String[] tArray) {
		Set<String> tSet = new HashSet<String>(Arrays.asList(tArray));
		Iterator iterator= tSet.iterator();
		String[] resultArray = new String[tSet.size()];
		int position  = 0;
		while (iterator.hasNext()) {
			resultArray[position] = iterator.next().toString().toLowerCase();
			position++;
		}
		// TODO 没有一步到位的方法，根据具体的作用，选择合适的Set的子类来转换。
		return resultArray;
	}

	/*
	 * 求2维数组所有排列组合情况 比如:{{1,2},{3},{4},{5,6}}共有2中排列,为:1345,1346,2345,2346
	 */
	public static  String[] paiLie(String[][] str) {
		int max = 1;
		for (int i = 0; i < str.length; i++) {
			max *= str[i].length;
		}
		String[] result = new String[max];
		for (int i = 0; i < max; i++) {
			String s = "";
			int temp = 1; // 注意这个temp的用法。
			for (int j = 0; j < str.length; j++) {
				temp *= str[j].length;
				s += str[j][i / (max / temp) % str[j].length];
			}
			result[i] = s;
		}
		return result;
	}

	/**
	 * 写入按键值信息
	 * @param path
	 * @param btnName
	 */
	public static void switchBtSoundChannel(Context context,
			final String btSoundChannel) {
		if (BTReceiver.BT_DISABLE.equals(btSoundChannel)) {
//			SettingTableKey.writeDataToTable(context.getContentResolver(),
//					PublicSettingTable.BT_PHONE_VOLUME, 0);

			sysEnable(); // added by june
			closeSpeaker(context);
		    //KernelUtils.setBtVolumeState(context, 0);
		} else if (BTReceiver.BT_ENABLE.equals(btSoundChannel)) {
//			SettingTableKey.writeDataToTable(context.getContentResolver(),
//					PublicSettingTable.BT_PHONE_VOLUME, 1);

			btEnable(); // added by june
			openSpeaker(context);
		    //KernelUtils.setBtVolumeState(context, 1);
		}
	//	context.sendBroadcast(VOLUME_STATE_CHANGE_INTENT);
	}

	public static String getTopActivity(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		return cn.getClassName();
	}

	public static void saveBTAddress(Context context, String address) {
		Log.d(TAG, "save " + address + " to db, key is "
				+ KEY_CONNECTED_DEVICE_ADDRESS);
		Settings.System.putString(context.getContentResolver(),
				KEY_CONNECTED_DEVICE_ADDRESS, address);
	}

	public static String readBTAddress(Context context) {
		String address = Settings.System.getString(
				context.getContentResolver(), KEY_CONNECTED_DEVICE_ADDRESS);
		Log.d(TAG, "read address from db, key is "
				+ KEY_CONNECTED_DEVICE_ADDRESS + " address:" + address);
		return address;
	}
	public static void switchBtSound(){
        switchSoundChannel(BtBroadcastReceiver.BT_ENABLE, 3500);
    }
	 public static void switchSystemSound(){
	    	switchSoundChannel(BtBroadcastReceiver.BT_DISABLE, 3500);
	    }
	 /**
	     * 写入按键值信息
	     * @param path
	     * @param btnName
	     */
	    public static void switchSoundChannel(final String soundChannel,int sleepTime){
	    	synchronized (lockObj) {
	    		if(soundChannel.equals(BtBroadcastReceiver.BT_ENABLE) && !needSwitchBTSoundChannel()){
	    			Log.d(TAG,"switchSoundChannel no need switchBtSound");
	    			return;
	    		}
	    		
	    		if(soundChannel.equals(BtBroadcastReceiver.BT_DISABLE) && !needSwitchSystemSoundChannel()) {
	    			Log.d(TAG,"switchSoundChannel no need switchSystemSound");
	    			return;
	    		}
	    		
	            FileOutputStream stream = null;
	            try {
	            	mute("mute");
	                stream = new FileOutputStream(IIC_SOUND_FILE);
	                stream.write(soundChannel.getBytes());  
	                stream.flush();
	                Log.d(TAG,"------------------------------------->write " + soundChannel + " to " + IIC_SOUND_FILE);
	                if(sleepTime > 0) {
	                    try {
	                    	Log.d(TAG," sleep " + sleepTime + "---------------------");
	            			Thread.sleep(sleepTime);
	            			Log.d(TAG," sleep " + sleepTime + "----------------------end");
	            		} catch (InterruptedException e) {
	            			e.printStackTrace();
	            		}
	                }
	                mute("unmute");
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    if (stream != null) {
	                        stream.close();
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }  
	            }
	        } // end of synchronized  
	    }
	
	    public static void mute(final String command){
	        FileOutputStream stream = null;
	        try {
	        	Log.d(TAG,"write " + command + " to " + MUTE_SOUND_FILE);
	            stream = new FileOutputStream(MUTE_SOUND_FILE);
	            stream.write(command.getBytes());  
	            stream.flush();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (stream != null) {
	                    stream.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }  
	        }
	    }
	    public static boolean needSwitchBTSoundChannel(){
	    	BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(IIC_HISTORY_SOUND_FILE)));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (reader != null) {
				try {
					String value = reader.readLine().trim();
					if(value.equalsIgnoreCase(BtBroadcastReceiver.BT_ENABLE)){
						Log.d(TAG,"needSwitchBTSoundChannel value: " + value + " no need to switch BT sound");
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	    	return true;
	    }
	    public static boolean needSwitchSystemSoundChannel(){
	    	BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(IIC_HISTORY_SOUND_FILE)));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (reader != null) {
				try {
					String value = reader.readLine().trim();
					if(value.equalsIgnoreCase(BtBroadcastReceiver.BT_ENABLE)){
						Log.d(TAG,"needSwitchSystemSoundChannel value: " + value + " need to switch system sound");
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	    	return false;
	    }
	public static synchronized String getCallingName(String mNumberString,
			List<Contact> contactList) {
		// TODO Auto-generated method stub
		if (contactList != null) {
			Log.i("wjz", "contactList != null——mNumberString="+mNumberString);
			LOG.print("contact.size=" + contactList.size() + " mNumberString="
					+ mNumberString);
			for (int i = 0; i < contactList.size(); i++) {
//				Log.i("wjz", "contactList.get("+i+").getName()="+contactList.get(i).getName()+contactList.get(i).getNumber());
				if ((contactList.get(i).getNumber().trim()).equals(mNumberString.trim())) {
					LOG.print(" Name=" + contactList.get(i).getName()
							+ " Number=" + mNumberString);
					Log.i("wjz", "contactList.get("+i+").getName()="+contactList.get(i).getName()+mNumberString);
					return contactList.get(i).getName();
				}
			}
		}
		Log.i("wjz", "contactList.get(i).getName()=空");
		return "";
	}
	/**
	 * 是否在倒车状态
	 * @return
	 */
	public static boolean isInBackCarState(Context mContext){
	    return SettingTableKey.getIntValue(mContext.getContentResolver(), PublicSettingTable.BACKCAR_STATE)==1?true:false;
	}
	
	
	private static final String AU_PATH = "/sys/devices/virtual/misc/mtgpio/pin";
	/***
	 * 使蓝牙声音可用
	 * @return 1成功，否则失败
	 */
	public static int btEnable() {
		File f = new File(AU_PATH);
		
		try {
			FileOutputStream os = new FileOutputStream(f);
			os.write("-wdout54 1".getBytes());
			os.flush();
			os.close();
			os = null;
			
			FileInputStream in = new FileInputStream(f);
			
			int res = in.read();
			in.close();
			return res - 48;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/***
	 * 使系统声音可用
	 * @return 1成功，否则失败
	 */
	public static int sysEnable() {
		File f = new File(AU_PATH);
		
		try {
			FileOutputStream os = new FileOutputStream(f);
			os.write("-wdout54 0".getBytes());
			os.flush();
			os.close();
			os = null;
			
			FileInputStream in = new FileInputStream(f);
			
			int res = in.read();
			in.close();
			return res - 48;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	private static int currVolume = 0;
	/**
	 * 打开扬声器
	 * @param ctx
	 */
	public static void openSpeaker(Context ctx) {
		try {

			// 判断扬声器是否在打开
			AudioManager audioManager = (AudioManager) ctx
					.getSystemService(Context.AUDIO_SERVICE);
			
			// audioManager.setMode(AudioManager.ROUTE_SPEAKER);

			// 获取当前通话音量
			currVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);

			if (!audioManager.isSpeakerphoneOn()) {
				Log.i("mode", "openStream==" + audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
				audioManager.setSpeakerphoneOn(true);
			}
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			audioManager
					.setStreamVolume(
							AudioManager.STREAM_VOICE_CALL,
							audioManager
									.getStreamVolume(AudioManager.STREAM_VOICE_CALL),
							0);
			Log.i("mode", "=mode=" + audioManager.getMode());
			Log.i("mode", "=mode=" + AudioManager.MODE_NORMAL);
			Log.i("mode", "=mode=" + AudioManager.MODE_RINGTONE);
			Log.i("mode", "=mode=" + AudioManager.MODE_IN_CALL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 打开扬声器
	 * @param ctx
	 */
	public static void openSpeakerMusic(Context ctx) {
		switchBtSoundChannel(ctx, BTReceiver.BT_ENABLE);
	}

	/**
	 * 关闭扬声器
	 * @param ctx
	 */
	public static void closeSpeaker(Context ctx) {
		try {
			AudioManager audioManager = (AudioManager) ctx
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager != null) {
				if (audioManager.isSpeakerphoneOn()) {
					Log.i("mode", "StreamVolume1==" + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
					audioManager.setSpeakerphoneOn(false);
					audioManager.setMode(AudioManager.MODE_NORMAL);
					
//					audioManager.setStreamVolume(
//							AudioManager.STREAM_VOICE_CALL, currVolume,
//							AudioManager.STREAM_VOICE_CALL);
					
//					audioManager.setStreamVolume(
//							AudioManager.STREAM_MUSIC, audioManager
//							.getStreamVolume(AudioManager.STREAM_MUSIC),
//							0);
				}
				else
				{
					Log.i("mode", "StreamVolume2==" + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
//					audioManager.setMode(AudioManager.MODE_NORMAL);
//					audioManager.setStreamVolume(
//							AudioManager.STREAM_MUSIC, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
//							0);
				}
				Log.i("mode", "===mode===1==" + audioManager.getMode());
				Log.i("mode", "===mode===1==" + AudioManager.MODE_NORMAL);
				Log.i("mode", "===mode===1==" + AudioManager.MODE_RINGTONE);
				Log.i("mode", "===mode===1==" + AudioManager.MODE_IN_CALL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
