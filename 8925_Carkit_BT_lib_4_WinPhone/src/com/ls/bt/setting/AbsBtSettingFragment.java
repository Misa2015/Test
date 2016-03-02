package com.ls.bt.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.bt.BTConstant.AutoFeature;
import com.bt.BTDefine;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.phone.R;

/**
 * 蓝牙设置界面
 */
abstract public class AbsBtSettingFragment extends AbsBaseFragment implements OnEditorActionListener{
	public static final boolean DEBUG = true;
	private CheckBox AutoConnenct;
	private CheckBox AutoReceive;
	/**BT获取PIN码*/
	private final int GET_LOCALDEVICE_NAME_PIN = 0;
	/**BT特性更改*/
	private final int BTSETTINGRESULT = 1;   
	private EditText editTextDeviceName, editTextPinName;
	private TextView textViewDeviceName, textViewPinName;
	private TextView textViewAutoAccept, textViewAutoConnect;
	public final static String BLUE_STATE_CHANGE = BTDefine.ACTION_AUTO_FEATURE_CHANGE;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// super.clearActionBarInfo();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BLUE_STATE_CHANGE);
		getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onResume() {
		super.onResume();
		view.requestFocus();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			//BT获取PIN码
			case GET_LOCALDEVICE_NAME_PIN:
				try {
					if (btDeviceFeature != null) {
						editTextDeviceName.setText(btDeviceFeature.getLocalDeviceName());
						editTextPinName.setText(btDeviceFeature.getPinCode());
						int re = btDeviceFeature.getAutoFeature();
						changCheckBox(re);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			case BTSETTINGRESULT:
				int result = (Integer) msg.obj;
				changCheckBox(result);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * checkBox选择
	 */
	public void changCheckBox(int result) {
		if ((result & AutoFeature.FEATURE_AUTO_ACCEPT_CALL) != 0) {
			AutoReceive.setChecked(true);
		} else {
			AutoReceive.setChecked(false);
		}
		if ((result & AutoFeature.FEATURE_AUTO_CONNECT_DEVICE) != 0) {
			AutoConnenct.setChecked(true);
		} else {
			AutoConnenct.setChecked(false);
		}
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BLUE_STATE_CHANGE.equals(action)) {
				//蓝牙自动功能改变，功能状态
				int btChangeResult = intent.getIntExtra(BTDefine.EXTRA_AUTO_FEATURE, 0);
				Message msg = new Message();
				msg.what = BTSETTINGRESULT;
				msg.obj = btChangeResult;
				mHandler.sendMessage(msg);
			}
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		mHandler.sendEmptyMessage(GET_LOCALDEVICE_NAME_PIN);
	}
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		mHandler.sendEmptyMessage(GET_LOCALDEVICE_NAME_PIN);
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = onCreatViewSettingLayout();
		AutoConnenct = (CheckBox) view.findViewById(R.id.autoconnect_ibt_setting);
		AutoReceive = (CheckBox) view.findViewById(R.id.autolistenr_ibt_setting);
		
		textViewAutoConnect = (TextView) view.findViewById(R.id.tv_auto_connect);
		textViewAutoAccept = (TextView) view.findViewById(R.id.tv_auto_accept);
		textViewDeviceName = (TextView) view.findViewById(R.id.setting_bt_devname);
		textViewPinName = (TextView) view.findViewById(R.id.setting_bt_devpwd);
		
		editTextDeviceName = (EditText) view.findViewById(R.id.username_tv_setting);
		editTextDeviceName.setOnEditorActionListener(this);
		editTextPinName = (EditText) view.findViewById(R.id.password_tv_setting);
		editTextPinName.setOnEditorActionListener(this);

		/**
		 * 自动连接
		 * */
		AutoConnenct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//		        KernelUtils.requestBee(getActivity());
				try {
					if (btDeviceFeature != null) {
						btDeviceFeature.setAutoFeature_BC(
								AutoFeature.FEATURE_AUTO_CONNECT_DEVICE,
								AutoConnenct.isChecked());
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});

		/***
		 * 自动接听广播
		 */
		AutoReceive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//		        KernelUtils.requestBee(getActivity());
				try {
					if (btDeviceFeature != null) {
						btDeviceFeature.setAutoFeature_BC(
								AutoFeature.FEATURE_AUTO_ACCEPT_CALL,
								AutoReceive.isChecked());
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		try {
			textViewDeviceName.setText(R.string.setting_machine_name);
			textViewPinName.setText(R.string.setting_pin_name);
			textViewAutoConnect.setText(R.string.bt_auto_connect);
			textViewAutoAccept.setText(R.string.bt_auto_accept);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  /***
   * 监听是否输入完成，如果完成则提交命令
   */
	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent event) {
		if(arg1 == EditorInfo.IME_ACTION_DONE){
			int id = arg0.getId();
			if(id == R.id.username_tv_setting){
				String name = editTextDeviceName.getText().toString().trim();
				if(name.length()>0){
					if (btDeviceFeature != null) {
						try {
							btDeviceFeature.setLocalDeviceName(name);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				
			}else if(id == R.id.password_tv_setting){
				String pinCode = editTextPinName.getText().toString().trim();
				if(pinCode.length()>0){
					if(btDeviceFeature != null){
						try {
							btDeviceFeature.setPinCode(pinCode);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return false;
	}

	protected abstract View onCreateViewPWD();
	protected abstract View onCreateViewSettingEdit();
	protected abstract View onCreatViewSettingLayout();
}
