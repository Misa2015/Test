package com.ls.bt.bc03;

import com.android.utils.log.JLog;
import com.android.utils.uart.UartWorker;
import com.android.utils.uart.UartWorker.OnProtocalDistributeListener;
import com.bt.BTPIMCallback;
import com.ls.bt.bc03.feature.atplus.AtPlusProtocal;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;

public class BTService extends Service {
    private BTManager mBtManager;
    private RemoteCallbackList<BTPIMCallback> mPimCallBackList;
    private int mCallbackSize;
    /**回调被调用的次数*/
    private int mNotifyCallbackCount;
    
    @Override
    public IBinder onBind(Intent arg0) {
        if(mBtManager != null)
        {
        	//服务被调用的时候会进行初始化动作	
            mBtManager.initData();
        }
        return mBtManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //蓝牙控模块
        mBtManager = new BTManager(getBaseContext());
        //监听数据变化
        mBtManager.setDataChangeListener(mDataChangeListener);
        
        mPimCallBackList = new RemoteCallbackList<BTPIMCallback>();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (null == mBtManager) {
	        mBtManager = new BTManager(getBaseContext());
	        //数据监听，
	        mBtManager.setDataChangeListener(mDataChangeListener);
        }
        //onStartCommand每次启动service都会被调用，方法有四中返回类行。
        //当service被kill系统会尝试重新启动服务，下面的返回值表示被kill的服务会保存状态作为下次启动的初始状态
        return Service.START_STICKY;
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBtManager != null)
        {
            mBtManager.release();
            mBtManager = null;
        }
        if(mPimCallBackList != null)
        {
            mPimCallBackList.kill();
            mPimCallBackList = null;
        }
    }
    
  
    /**
     * 通知侦听器PIM数据
     * @param type
     * @param objects
     */
    private void notifyPimCallBack(int type,Object...objects)
    {
        try {
            mNotifyCallbackCount++;
            if(mCallbackSize == 0)
            {
                mCallbackSize = mPimCallBackList.beginBroadcast();
            }
            int size = mCallbackSize;
            while(size > 0)
            {
                size--;
                BTPIMCallback pimCallback = mPimCallBackList.getBroadcastItem(size);
                switch(type)
                {
                case DataChangeListener.TYPE_GET_CONTACT:
                    pimCallback.onGetContact((String)objects[0], (String)objects[1]);
                    break;
                case DataChangeListener.TYPE_GET_CALLLOG:
                    pimCallback.onGetCallLog((Integer)objects[0], (String)objects[1], (String)objects[2],"");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            mNotifyCallbackCount--;
            if(mNotifyCallbackCount <= 0)
            {
                mPimCallBackList.finishBroadcast();
                mCallbackSize = 0;
                mNotifyCallbackCount = 0;
            }
        }
    }
    
    public interface DataChangeListener
    {
        /**获取联系人*/
        public static final int TYPE_GET_CONTACT = 0;
        /**获取通话记录*/
        public static final int TYPE_GET_CALLLOG = 1;
        public void registPIMCallback(BTPIMCallback callback);
        public void unregistPIMCallback(BTPIMCallback callback);
        public void onDataChange(int type,Object...objects);
    }       
    
    private DataChangeListener mDataChangeListener = new DataChangeListener() {
        
        @Override
        public void unregistPIMCallback(BTPIMCallback callback) {
            if(mPimCallBackList != null)
                mPimCallBackList.unregister(callback);
        }
        
        @Override
        public void registPIMCallback(BTPIMCallback callback) {
            if(mPimCallBackList != null)
                mPimCallBackList.register(callback);
        }
        
        @Override
        public void onDataChange(int type, Object... objects) {
            notifyPimCallBack(type, objects);
        }
    };
}
