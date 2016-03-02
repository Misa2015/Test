package com.ls.bt.bc03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.utils.log.JLog;
import com.bt.BTDefine;
import com.bt.BTDefineSender;
import com.bt.BTConstant.CallLog;
import com.ls.bt.bc03.BC03BTConstant.PhoneBookType;
import com.ls.bt.bc03.BTService.DataChangeListener;
import com.ls.bt.bc03.feature.IBC03Feature;
import com.ls.bt.bc03.feature.IBC03PhoneBookCallback;

public class PhoneBookManager implements IBC03PhoneBookCallback {
	
	JLog LOG = new JLog("PhoneBookManager",PhoneBookManager.DEBUG,JLog.TYPE_DEBUG);
	public static final boolean DEBUG = true;
	
	private ArrayList<PhoneBookData> mCurrentPhoneBookList = new ArrayList<PhoneBookData>();
	private ArrayList<PhoneBookData> mCurrentPhoneBookCallOutList = new ArrayList<PhoneBookData>();
	private ArrayList<PhoneBookData> mCurrentPhoneBookCallInList = new ArrayList<PhoneBookData>();
	private ArrayList<PhoneBookData> mCurrentPhoneBookMissedCallList = new ArrayList<PhoneBookData>();
	private HashMap<String, String> mNumberToNameMap = new HashMap<String, String>();
	/** 联系人同步完成 */
	private boolean isSyncContactComplete;
	/** 通话记录同步完成 */
	private boolean isSyncCallLogComplete;
	/** 当前同步的PIM类型 */
	private int mCurrentSyncPhoneBookType;
	/** 是否允许同步pim */
	private boolean isAllowSyncPim = true;
	/** 当前已经取到的记录数量 */
	private int mCurrentFetchRecordNum;
	/** 最后一次取到的记录数量 */
	private int mLastFetchRecordNum;
	/** 每次取的记录数量 */
	private static final int FETCH_RECORD_NUM = 4;
	/** 最大获取联系人数量 */
	private static final int MAX_CONTACT_COUNT = 9999;
	/** */
	private static final int MAX_CALLLOG_COUNT = 20;

	private Context mContext;
	private IBC03Feature mBTFeature;
	private DataChangeListener mDataChangeListener;

	/** 联系人缓存 */
	private ArrayList<String> marrayList;
	private Intent mIntent;
	private String PHONE_CONTACT = "cn.yunzhisheng.intent.action.custom.order.contact";
	private String CURRENT_COUNT = "current_count_phone";
	private String PHONE_CALLLOG = "phone_call_log";
	
	public PhoneBookManager(Context context, IBC03Feature btFeature) {
		mContext = context;
		mBTFeature = btFeature;
		marrayList = new ArrayList<String>();
	}

	public void release() {
		mContext = null;
		mBTFeature = null;
		mCurrentPhoneBookList = null;
		mCurrentPhoneBookCallOutList = null;
		mCurrentPhoneBookCallInList = null;
		mCurrentPhoneBookMissedCallList = null;
		mDataChangeListener = null;
	}

	public void reset() {
		mCurrentPhoneBookList.clear();
		mCurrentPhoneBookCallOutList.clear();
		mCurrentPhoneBookCallInList.clear();
		mCurrentPhoneBookMissedCallList.clear();
		isSyncContactComplete = false;
		isSyncCallLogComplete = false;
		isAllowSyncPim = true;
	}

	public void setDataChangeListener(DataChangeListener listener) {
		mDataChangeListener = listener;
	}

	/**
	 * 根据号码查找联系人
	 */
	public PhoneBookData findContactByPhoneNumber(String number) {
		for (PhoneBookData phoneBook : mCurrentPhoneBookList) {
			if (phoneBook.getNumber().equals(number)) {
				return phoneBook;
			}
		}
		return null;
	}

	/**
	 * 开始同步PIM数据
	 */
	private void startSyncPim(int phoneBookType) {
		LOG.print("PhoneBookManager 开始同步PIM ————> IBC03Feature getPhoneBook ");
		isAllowSyncPim = false;
		mCurrentSyncPhoneBookType = phoneBookType;
		mCurrentFetchRecordNum = 0;
		mLastFetchRecordNum = 0;
		mBTFeature.getPhoneBook(phoneBookType, mCurrentFetchRecordNum + 1,FETCH_RECORD_NUM);
	}

	/**
	 * 根据号码去联系人中查找联系人名字
	 * @param number
	 * @param name
	 * @return
	 */
	public String getNameFromContact(String number, String name) {
		if (mNumberToNameMap.size() > 0 && mNumberToNameMap.containsKey(number)) {
			name = mNumberToNameMap.get(number);
		} else {
			name = (name != null) ? name : "";
		}
		return name;
	}

	/**
	 * 通知获取到一条联系人信息
	 */
	private void notifyGetContact(String name, String number) {
		if (mDataChangeListener != null) {
			mDataChangeListener.onDataChange(
					DataChangeListener.TYPE_GET_CONTACT, name, number);
		}
	}

	/**
	 * 通知本地缓存的所有联系人信息
	 */
	private void notifyAllContactFromCurrent() {
		int size = mCurrentPhoneBookList.size();
		for (int i = 0; i < size; i++) {
			PhoneBookData phoneBook = mCurrentPhoneBookList.get(i);
			notifyGetContact(phoneBook.getName(), phoneBook.getNumber());
		}
	}

	/**
	 * 通知获取到一条通话记录
	 */
	private void notifyGetCallLog(int type, String name, String number) {
		if (mDataChangeListener != null) {
			LOG.print("通知获取到一条通话记录    ————————————> name="+name+"  number="+number);
			mDataChangeListener.onDataChange(DataChangeListener.TYPE_GET_CALLLOG, type, name, number);
		}
	}

	/**
	 * 通知获取到list中所有的通话记录
	 * @param type
	 * @param list
	 */
	private void notifyCallLogFromList(int type, List<PhoneBookData> list) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			PhoneBookData phoneBook = list.get(i);
			notifyGetCallLog(type, phoneBook.getName(), phoneBook.getNumber());
		}
	}

	/**
	 * 通知 本地缓存的所有通话记录
	 */
	private void notifyAllCallLogFromCurrent() {
		notifyCallLogFromList(CallLog.TYPE_INCOMING_CALL,
				mCurrentPhoneBookCallInList);
		notifyCallLogFromList(CallLog.TYPE_OUTGOING_CALL,
				mCurrentPhoneBookCallOutList);
		notifyCallLogFromList(CallLog.TYPE_MISSING_CALL,
				mCurrentPhoneBookMissedCallList);
	}

	/**
	 * 检查是否需要加载下一页的pim数据
	 */
	private void checkIsNeedToReadNextPageOfPim(int maxRecordCount) {
		if ((mCurrentFetchRecordNum < maxRecordCount - 1) && (mLastFetchRecordNum + FETCH_RECORD_NUM == mCurrentFetchRecordNum)) {
			// 数据没有达到极限,则继续获取
			mLastFetchRecordNum = mCurrentFetchRecordNum;
			mBTFeature.getPhoneBook(mCurrentSyncPhoneBookType,mCurrentFetchRecordNum, FETCH_RECORD_NUM);
		} else {
			LOG.print("通讯录同步完成");
			processAddPhoneBook(null, null);
		}
	}

	/**
	 * 添加联系人信息到联系人列表中
	 */
	private void addContactToPhoneBookList(List<PhoneBookData> list,
			String name, String number) {
		PhoneBookData phoneBookData = new PhoneBookData(name, number);
		// if (!list.contains(phoneBookData))
		{
			list.add(phoneBookData);
		}
	}

	/**
	 * 去掉名字后面多余的字符串 如输入小王\M 返回 小王
	 */
	private String fixExcessNameString(String name) {
		return name.replaceAll("/\\w+", "");// 去掉名字后面的/X
	}

	/**
	 * 添加一个联系人信息到联系人记录中
	 * @param name
	 * @param number
	 */
	private void addContact2PhoneBook(String name, String number) {
		name = fixExcessNameString(name);
		addContactToPhoneBookList(mCurrentPhoneBookList, name, number);
		if (!mNumberToNameMap.containsKey(number))
			mNumberToNameMap.put(number, name);
		// 通知callback
		notifyGetContact(name, number);
		mCurrentFetchRecordNum++;
	}

	/**
	 * 添加一个通话记录信息到通话记录中
	 */
	private void addCallLog2PhoneBook(int callLogType, String name,String number) {
		name = getNameFromContact(number, name);
		List<PhoneBookData> callLogList = null;
		switch (callLogType) {
		case CallLog.TYPE_INCOMING_CALL:
			callLogList = mCurrentPhoneBookCallInList;
			break;
		case CallLog.TYPE_MISSING_CALL:
			callLogList = mCurrentPhoneBookMissedCallList;
			break;
		case CallLog.TYPE_OUTGOING_CALL:
			callLogList = mCurrentPhoneBookCallOutList;
			break;
		}
		addContactToPhoneBookList(callLogList, name, number);
		// 通知callback
		notifyGetCallLog(callLogType, name, number);
		mCurrentFetchRecordNum++;
	}
	/**
	 * 添加记录到电话本中
	 * @param name
	 * @param number
	 */
	private void processAddPhoneBook(String name, String number) {
		switch (mCurrentSyncPhoneBookType) {
		//电话本上的联系人
		case PhoneBookType.PHONE_CONTACT:
			if (name != null && number != null) {
				 marrayList.add(name + "_" + number);
				 Log.i("call", "name="+name+"  number="+number);
			    //添加一个联系人记录到电话本
				addContact2PhoneBook(name, number);
			} else {
				
				if (marrayList.size() > 0) {
					mIntent = new Intent(PHONE_CONTACT);
					mIntent.putExtra("contactList", marrayList);
					mContext.sendBroadcast(mIntent);
					Log.i("call", "marrayList="+marrayList.size());
					marrayList.clear();
				}
				Log.i("call", "同步完成");
				// 记录全部取完.
				BTDefineSender.sendBTPIMSyncFinish(mContext,BTDefine.VALUE_SYNC_CONTACT_SUCCESS);
				if (mCurrentPhoneBookList.size() > 0) {
					isSyncContactComplete = true;
				}
				isAllowSyncPim = true;
			}
			break;
		//SIM卡上联系人	
		case PhoneBookType.SIM_CONTACT:
			if (name != null && number != null) {
				marrayList.add(name + "_" + number);
				addContact2PhoneBook(name, number);
			} else {
				// 记录全部取完.去取phone的
				startSyncPim(PhoneBookType.PHONE_CONTACT);
			}
			break;
		//接收到的电话	
		case PhoneBookType.RECEIVE_CALL:
			if (number != null) {
				addCallLog2PhoneBook(CallLog.TYPE_INCOMING_CALL, name, number);
			} else {
				// 记录全部取完.去取去电记录
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startSyncPim(PhoneBookType.DIAL_CALL);
			}
			break;
		case PhoneBookType.DIAL_CALL:
			if (number != null) {
				addCallLog2PhoneBook(CallLog.TYPE_OUTGOING_CALL, name, number);
			} else {
				// 记录读取完,去取未接记录
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startSyncPim(PhoneBookType.MISS_CALL);
			}
			break;
			
		case PhoneBookType.MISS_CALL:
			if (number != null) {
				addCallLog2PhoneBook(CallLog.TYPE_MISSING_CALL, name, number);
			} else {
				// 未接记录读取完.通知记录读取完毕
				BTDefineSender.sendBTPIMSyncFinish(mContext,BTDefine.VALUE_SYNC_CALL_LOG_SUCCESS);
				if (mCurrentPhoneBookCallInList.size() > 0
						|| mCurrentPhoneBookCallOutList.size() > 0
						|| mCurrentPhoneBookMissedCallList.size() > 0) {
					isSyncCallLogComplete = true;
				}
				isAllowSyncPim = true;
			}
			break;
		}
	}
	
	/**
	 * 获取通话记录
	 * 
	 * @return
	 */
	public boolean getCallLog() {
		if (isSyncCallLogComplete) {
			notifyAllCallLogFromCurrent();
			BTDefineSender.sendBTPIMSyncFinish(mContext,BTDefine.VALUE_SYNC_CALL_LOG_SUCCESS);
			return true;
		} else {
			if (isAllowSyncPim) {
				//startSyncPim(PhoneBookType.MISS_CALL);
				startSyncPim(PhoneBookType.RECEIVE_CALL);
				//startSyncPim(PhoneBookType.DIAL_CALL);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 获取联系人
	 */
	public boolean getContact() {
		if (isSyncContactComplete) {
			notifyAllContactFromCurrent();
			BTDefineSender.sendBTPIMSyncFinish(mContext,BTDefine.VALUE_SYNC_CONTACT_SUCCESS);
			return true;
		} else {
			// 允许同步的时候才能进行同步pim的操作
			if (isAllowSyncPim) {
				LOG.print("同步PIM数据");
				startSyncPim(PhoneBookType.PHONE_CONTACT);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 获取到电话本记录
	 */
	@Override
	public void onGetPhoneBook(int index, String number, String name,
			String time) {
		//添加记录到电话本中
		processAddPhoneBook(name, number);
	}

	@Override
	public void onGetPhoneBookFinish() {
		// 在本次获取电话本记录结束后.检查一下是否已经获取完电话本或者获取的记录已经达到上限
		int maxRecordCount = 0;
		switch (mCurrentSyncPhoneBookType) {
		case PhoneBookType.DIAL_CALL:
		
		case PhoneBookType.MISS_CALL:
			
		case PhoneBookType.RECEIVE_CALL:
			maxRecordCount = MAX_CALLLOG_COUNT;
			break;
		case PhoneBookType.PHONE_CONTACT:
			maxRecordCount = MAX_CONTACT_COUNT;
		case PhoneBookType.SIM_CONTACT:
			//最大获取联系人数量
			//maxRecordCount = MAX_CONTACT_COUNT;
			break;
		}
		LOG.print("电话本同步完成 onGetPhoneBookFinish");
		//检查是否需要同步下一页PIM数据
		checkIsNeedToReadNextPageOfPim(maxRecordCount);
	}

	@Override
	public void onError() {
		if (isAllowSyncPim) {
			// 同步中的时候报错了。先清空所有状态
			reset();
		}
	}
}
