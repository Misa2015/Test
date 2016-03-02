package com.ls.bt.inout;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.utils.log.JLog;
import com.bt.BTDefine;
import com.bt.BTConstant.AutoFeature;
import com.ls.android.bt.AbsBTMainActivity;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.bt.contact.AbsBtContactNumFragment;
import com.ls.bt.contact.Contact;
import com.ls.bt.service.AbsBTContactService;
import com.ls.bt.utils.BtHelper;
import com.ls.bt.utils.Utils;
import com.ls.android.phone.R;
import com.nwd.kernel.utils.KernelConstant;
import com.nwd.kernel.utils.KernelUtils;

/**
 * 蓝牙来电界面
 */
abstract public class AbsBtInComingCallFragment extends AbsBaseFragment implements OnClickListener
{
    public static final boolean DEBUG = true;
    private TextView mTextViewName;
    private TextView mTextViewNumber;
    private ImageButton mImageButtonAnswer, mImageButtonHangup, mImageButtonSwitch;
    public static final String INCOMING_STATE = "isInComingState", INCOMING_NUMBER = "inComingNumber";
    private static final String TAG = "BtIncomingFragment";
    String mInComingNumber = "";
    //String mInComingName = "";
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private static final int SEND_AUTO_FEATURE = 0;
    
	//语音控制蓝牙广播
	private final String rejectIncoming = "com.bt.action_bt.rejectIncoming";
	private final String acceptIncoming = "com.bt.action_bt.acceptIncoming";
	
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SEND_AUTO_FEATURE:
                    if (null == btDeviceFeature)
                    {
                        handler.sendEmptyMessageDelayed(SEND_AUTO_FEATURE, 1000);
                    }
                    else
                    {
                        sendIfAutoReceiver();
                    }
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mInComingNumber = BtBroadcastReceiver.mInCommingCallNumber;

        Log.i("lai", "onCreate: "+mInComingNumber);
        
        IntentFilter mMcuKeyIntentFilter = new IntentFilter(KernelConstant.ACTION_KEY_VALUE);
        mMcuKeyIntentFilter.addAction(BTDefine.ACTION_BT_END_CALL);
        mMcuKeyIntentFilter.addAction(KernelConstant.ACTION_KEY_VALUE);
        mMcuKeyIntentFilter.addAction(rejectIncoming);
        mMcuKeyIntentFilter.addAction(acceptIncoming);
        getActivity().registerReceiver(McuKeyValueReciever, mMcuKeyIntentFilter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = onCreateViewBtIncomingLayout();

        mTextViewName = (TextView) view.findViewById(R.id.incoming_name);
        mTextViewNumber = (TextView) view.findViewById(R.id.incoming_number);
        mImageButtonAnswer = (ImageButton) view.findViewById(R.id.ibt_listener_phone);
        mImageButtonAnswer.setOnClickListener(this);
        mImageButtonHangup = (ImageButton) view.findViewById(R.id.ibt_off_phone);
        mImageButtonHangup.setOnClickListener(this);
        mTextViewNumber.setText("来电号码:"+mInComingNumber);
        
        mInComingName = Utils.getCallingName(mInComingNumber, AbsBtContactNumFragment.mContactsList);
        
        List<Contact> mContactsList = AbsBtContactNumFragment.mContactsList;
        
        for(Contact contact:mContactsList){
        	Log.i("lai", "name="+contact.getName());
        }
        
        Log.i("lai", "mInComingName="+mInComingName);
        
        if(mInComingName == null){
        	mTextViewName.setVisibility(View.GONE);
        }else{
        	mTextViewName.setText("来电姓名:"+mInComingName);
        }
        return view;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.ibt_listener_phone)
        {
            answer();  //接听
        }
        else if (id == R.id.ibt_off_phone)
        {
            cancel_answer();  //挂断
        }
        else if (id == R.id.ibt_bt_phone)
        {
            switchSound();  //
        }
    }

    private void answer()
    {
        try
        {
        	//应答
            btDeviceFeature.answerCall();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void switchSound()
    {
        try
        {
            btDeviceFeature.changeSoundChannel();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                return false;
        }
        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getActivity().getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
    }

    private void cancel_answer()
    {
        try
        {
            btDeviceFeature.cancelCall();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getActivity().getWindow().clearFlags(FLAG_HOMEKEY_DISPATCHED);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (McuKeyValueReciever != null)
        {
            getActivity().unregisterReceiver(McuKeyValueReciever);
        }
    }

    private void sendIfAutoReceiver()
    {
        try
        {
            int reciverFlag = btDeviceFeature.getAutoFeature();
            if ((reciverFlag & AutoFeature.FEATURE_AUTO_ACCEPT_CALL) != 0)
            {
                answer();
            }
            else
            {
            }
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver McuKeyValueReciever = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (KernelConstant.ACTION_KEY_VALUE.equals(action))
            {
                byte keyValue = intent.getByteExtra(KernelConstant.EXTRA_KEY_VALUE, (byte) 0x00);
                switch (keyValue)
                {
                    case KernelConstant.McuKeyValue.PHONE:
                    case KernelConstant.McuKeyValue.ACCEPT_CALL:
                        
                        mImageButtonAnswer.performClick();

                        break;
                    case KernelConstant.McuKeyValue.CANCEL_CALL:
                        mImageButtonHangup.performClick();
                        break;

                    default:
                        break;
                }
            }else if(BTDefine.ACTION_BT_END_CALL.equals(action))
            {
            	if (!BtHelper.APP_CLASS_NAME.equals(BtBroadcastReceiver.mTopAppClassName))
            	{
            		getActivity().finish();
            	}
            }
            else if(acceptIncoming.equals(action)){
            	answer();
            }
            else if(rejectIncoming.equals(action)){
            	cancel_answer();
            }

        }

    };
	private String mInComingName;

    protected abstract View onCreateViewBtIncomingLayout();

}
