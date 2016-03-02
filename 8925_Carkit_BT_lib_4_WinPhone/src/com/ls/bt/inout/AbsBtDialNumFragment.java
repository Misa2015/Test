package com.ls.bt.inout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.utils.log.JLog;
import com.bt.BTDefine;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.bt.BTApplication;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.android.phone.R;
import com.ls.bt.call.detail.AbsCallDetailAdapter;
import com.nwd.kernel.utils.KernelUtils;

/**
 * 蓝牙拨号界面
 * 
 * @author user
 * 
 */
abstract public class AbsBtDialNumFragment extends AbsBaseFragment implements OnClickListener, OnLongClickListener
{
    private static final JLog LOG = new JLog("BtDialNumFragment", AbsBtDialNumFragment.DEBUG, JLog.TYPE_DEBUG);
    public static final boolean DEBUG = false;
    private final static String CURRENT_INFO = BTDefine.ACTION_BT_OUTGOING_NUMBER;
    private static final String CANCELCALL = BTDefine.ACTION_BT_END_CALL;
    private ImageButton one, two, three, four, five, six, seven, eight, nine, zero, del, xing, jing, callPhone/*
                                                                                                               */;
    private String numberString = "";
    private TextView tvNumber;
    private StringBuffer mCallNumber = new StringBuffer();

    private boolean mCalling = false;
    private final static int MESSAGE_UPDATE_NAME_NUMBER = 1;
    public final static String MESSAGE_NAME = "name", MESSAGE_NUMBER = "number";
    /** 最大号码长度 */
    private static final int MAX_CALL_NUM = 22;
    
    private Handler mHandler = new Handler()
    {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg)
        {
            if (MESSAGE_UPDATE_NAME_NUMBER == msg.what)
            {
                deleteCallNumber();
                mCallNumber.append(msg.getData().getString(MESSAGE_NUMBER));
                tvNumber.setText(mCallNumber.toString());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LOG.print("onCreate");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CURRENT_INFO);
        intentFilter.addAction(CANCELCALL);
        getActivity().registerReceiver(mReceiver, intentFilter);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LOG.print("onCreateView");
        view = onCreateViewBtDialNumLayout();
        one = (ImageButton) view.findViewById(R.id.ibt_one);
        two = (ImageButton) view.findViewById(R.id.ibt_two);
        three = (ImageButton) view.findViewById(R.id.ibt_three);
        four = (ImageButton) view.findViewById(R.id.ibt_four);
        five = (ImageButton) view.findViewById(R.id.ibt_five);
        six = (ImageButton) view.findViewById(R.id.ibt_six);
        seven = (ImageButton) view.findViewById(R.id.ibt_seven);
        eight = (ImageButton) view.findViewById(R.id.ibt_eight);
        nine = (ImageButton) view.findViewById(R.id.ibt_nine);
        xing = (ImageButton) view.findViewById(R.id.ibt_xing);
        zero = (ImageButton) view.findViewById(R.id.ibt_zero);
        jing = (ImageButton) view.findViewById(R.id.ibt_jing);
        callPhone = (ImageButton) view.findViewById(R.id.call_phone);
        del = (ImageButton) view.findViewById(R.id.del_num);
        tvNumber = (TextView) view.findViewById(R.id.tv_show_num);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        xing.setOnClickListener(this);
        xing.setOnLongClickListener(this);
        zero.setOnClickListener(this);
        zero.setOnLongClickListener(this);
        jing.setOnClickListener(this);
        callPhone.setOnClickListener(this);
        del.setOnClickListener(this);
        del.setOnLongClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v)
    {
        if (mCallNumber.length() >= MAX_CALL_NUM)
        {
            Toast.makeText(getActivity(), R.string.num_more_than_max_len, 1000).show();
            mCallNumber.deleteCharAt(mCallNumber.length() - 1);
        }
        int id = v.getId();
        LOG.print("Dial num onClick , id = " + id);
        if (id == R.id.ibt_one)
        {
            mCallNumber.append("1");
            onNumberDialed('1');
        }
        else if (id == R.id.ibt_two)
        {
            mCallNumber.append("2");
            onNumberDialed('2');
        }
        else if (id == R.id.ibt_three)
        {
            mCallNumber.append("3");
            onNumberDialed('3');
        }
        else if (id == R.id.ibt_four)
        {
            mCallNumber.append("4");
            onNumberDialed('4');
        }
        else if (id == R.id.ibt_five)
        {
            mCallNumber.append("5");
            onNumberDialed('5');
        }
        else if (id == R.id.ibt_six)
        {
            mCallNumber.append("6");
            onNumberDialed('6');
        }
        else if (id == R.id.ibt_seven)
        {
            mCallNumber.append("7");
            onNumberDialed('7');
        }
        else if (id == R.id.ibt_eight)
        {
            mCallNumber.append("8");
            onNumberDialed('8');
        }
        else if (id == R.id.ibt_nine)
        {
            mCallNumber.append("9");
            onNumberDialed('9');
        }
        else if (id == R.id.ibt_zero)
        {
            mCallNumber.append("0");
            onNumberDialed('0');
        }
        else if (id == R.id.ibt_xing)
        {
            mCallNumber.append("*");
            onNumberDialed('*');
        }
        else if (id == R.id.ibt_jing)
        {
            mCallNumber.append("#");
            onNumberDialed('#');
        }
        else if (id == R.id.call_phone)
        {
            if (mCallNumber.length() == 0) return;
            callOrCancelCallClicked();
        }
        else if (id == R.id.del_num)
        {
            if (mCallNumber.length() > 0) mCallNumber.deleteCharAt(mCallNumber.length() - 1);
        }
        tvNumber.setText(mCallNumber.toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        if (null != tvNumber)
        {
            mCallNumber.delete(0, mCallNumber.length());
            tvNumber.setText("");
        }
    }

    public void onNumberDialed(char key)
    {

        if (BtBroadcastReceiver.BT_PHONE_STATUS == BTDefine.ACTION_BT_OUTGOING_CALL)
        {
            try
            {
                btDeviceFeature.sendDTMF(key);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
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

    private void callOrCancelCallClicked()
    {
        if (!mCalling)
        {
            call(true);//打电话
        }
        else
        {
            cancelCall(true);//挂电话
        }
    }

    /**
     * 拨号
     */
    private void call(boolean isCallSdk)
    {
        if (isCallSdk)
        {
            try
            {
                btDeviceFeature.dial_BC(mCallNumber.toString());
                LOG.print("开始呼叫");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        mCalling = true;
    }

    private void resetCallInfo()
    {
        deleteCallNumber();
        tvNumber.setText(mCallNumber.toString());
    }

    /**
     * 设置取消通话时的状态
     */
    private void setCancelCallState()
    {
        mCalling = false;
        resetCallInfo();
        onChangeViewDialPhoneState(callPhone, 1);
        // callPhone.setBackgroundResource(R.drawable.dia_phone_selector);
    }

    /**
     * 挂断
     */
    private void cancelCall(boolean isCallSdk)
    {
        if (isCallSdk)
        {
            try
            {
                btDeviceFeature.cancelCall();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            mCalling = false;
            onChangeViewDialPhoneState(callPhone, 1);
            //    callPhone.setBackgroundResource(R.drawable.dia_phone_selector);
//            btnSwitch.setVisibility(View.INVISIBLE);
            del.setVisibility(View.VISIBLE);
        }
    }

    private void deleteCallNumber()
    {
        while (mCallNumber.length() > 0)
        {
            mCallNumber.deleteCharAt(0);
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (CURRENT_INFO.equals(action))
            {
                String number = intent.getStringExtra(BTDefine.EXTRA_PHONE_NUMBER);
                if (number != null && !number.equals(""))
                {
                    deleteCallNumber();
                    mCallNumber.append("");
                    mCallNumber.append(number);
                    mHandler.sendMessage(createMessage(MESSAGE_UPDATE_NAME_NUMBER, "", number));
                    onChangeViewDialPhoneState(callPhone, 0);
                    mCalling = true;
                }
            }
            else if (CANCELCALL.equals(action))
            {
            	Log.i("wuguisheng", "电话挂断广播AbsBtDialNumFragment");
                {
                    if (mCalling)
                    {
                        setCancelCallState();
                    }
                    resetCallInfo();
                }
            }
        }

    };

    public Message createMessage(int what, String name, String phoneNumber)
    {
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        Bundle data = new Bundle();
        data.putString(MESSAGE_NAME, name);
        data.putString(MESSAGE_NUMBER, phoneNumber);
        msg.setData(data);
        return msg;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mReceiver != null)
        {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        view.requestFocus();
        LOG.print("onResume.BT_PHONE_STATUS=" + BtBroadcastReceiver.BT_PHONE_STATUS);
        //切进来判断是否是正在拨号中
        deleteCallNumber();
        String callNum = "";
        //   String callName = "";
        if (BtBroadcastReceiver.BT_PHONE_STATUS.equals(BTDefine.ACTION_BT_OUTGOING_CALL) || BtBroadcastReceiver.BT_PHONE_STATUS
                .equals(BTDefine.ACTION_BT_BEGIN_CALL_ONLINE))
        {
            callNum = ((BTApplication) getActivity().getApplication()).getCallNumber();
            mCallNumber.append(" ");
            mCallNumber.append(callNum);
            mCalling = true;
            onChangeViewDialPhoneState(callPhone, 0);
            del.setVisibility(View.INVISIBLE);
        }
        else
        {
            mCalling = false;
            onChangeViewDialPhoneState(callPhone, 1);
            del.setVisibility(View.VISIBLE);
        }
        tvNumber.setText(mCallNumber.toString());
    }

    @Override
    public boolean onLongClick(View v)
    {
        boolean result = false;

        if (mCallNumber.length() >= MAX_CALL_NUM)
        {
            Toast.makeText(getActivity(), R.string.num_more_than_max_len, 1000).show();
            mCallNumber.deleteCharAt(mCallNumber.length() - 1);
        }
        int id = v.getId();
        if (id == R.id.ibt_xing)
        {
            mCallNumber.append("+");
            onNumberDialed('+');
            result = true;
        }
        else if (id == R.id.del_num)
        {
            deleteCallNumber();
            result = true;
        }
        else if (id == R.id.ibt_zero)
        {
            mCallNumber.append("+");
            onNumberDialed('+');
            result = true;
        }
        tvNumber.setText(mCallNumber.toString());
        return result;
    }
    protected abstract View onCreateViewBtDialNumLayout();

    /**
     * 1为可通话图标，0为可挂断图标
     * 
     * @param view
     * @param state
     */
    protected abstract void onChangeViewDialPhoneState(View view, int state);
}
