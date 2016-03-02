package com.ls.bt.match;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.utils.log.JLog;
import com.bt.BTDefine;
import com.bt.BTDevice;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.android.phone.R;

/**
 * DVD列表界面
 */
abstract public class AbsBtMatchRecordFragment extends AbsBaseFragment {
	private static final JLog LOG = new JLog("AbsBtMatchRecordFragment",
			AbsBtMatchRecordFragment.DEBUG, JLog.TYPE_DEBUG);
	public static final boolean DEBUG = true;
	private ListView listview;
	private ImageView ivPhone;
	private ImageView ivConnectStatus;
	private TextView txMatchName;
	private List<BTDevice> listMatchRecord = new ArrayList<BTDevice>();
	private ListView myListView;
	private MyAdapter myAdapter = new MyAdapter();
	public final static String BLUE_GET_DEVICES = BTDefine.ACTION_GET_PAIR_DEVICE_RECORD;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BLUE_GET_DEVICES);
		filter.addAction(BTDefine.ACTION_BT_CONNECTION_CHANGE);
		getActivity().registerReceiver(mMatchReceiver, filter);
	}

	@Override
	public void onResume() {
		super.onResume();
		view.requestFocus();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		 menu.clear();
		getPairDeviceRecord();
		setCurrentDeviceName();
		onInflateMenuMatchRecord(inflater, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delBt) {
			listMatchRecord.removeAll(listMatchRecord);
			if(null!=btDeviceFeature)
			{
				try {
					btDeviceFeature.deletePairDeviceRecord(myAdapter.getSelect());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			myAdapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LOG.print("onCreateView");
		view = onCreateViewBtMatchRecord();
		listview = (ListView) view.findViewById(R.id.list);
		listview.setAdapter(myAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				if (myAdapter.getSelect() != arg2) {

					try {
						if (listMatchRecord.size() > 0) {
							//连接设备
							btDeviceFeature.connectDevice_BC(listMatchRecord
									.get(arg2));
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}

				} else {
					if (listMatchRecord.size() > 0) {
						try {
							//断开设备
							btDeviceFeature.disConnectDevice_BC();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
					}
				}
				myAdapter.notifyDataSetInvalidated();
			}
		});
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mMatchReceiver);
	}

	class MyAdapter extends BaseAdapter {
		private int mySelect = -1;
		@Override
		public int getCount() {
			return listMatchRecord.size();
		}

		public void setSelect(int select) {
			mySelect = select;
		}

		public int getSelect() {
			return mySelect;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = onCreateViewItemMatchRecord();
			ivPhone = (ImageView) v.findViewById(R.id.iv_phone);
			ivConnectStatus = (ImageView) v.findViewById(R.id.iv_connect_status);
			txMatchName = (TextView) v.findViewById(R.id.contact_textview);
			String name = listMatchRecord.get(arg0).getName();
			if (name.length() > 14) {
				name = name.substring(0, 13);
			}

			txMatchName.setText(name);
			onChangeViewItemConnectedState(ivConnectStatus, arg0 == mySelect);
			return v;
		}

	}

	private void getPairDeviceRecord() {
		try {
			if (btDeviceFeature != null) {
				btDeviceFeature.getPairDeviceRecord_BC();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void setCurrentDeviceName() {
		/**从BT服务上获取当前设备*/
		BTDevice btCurrentDevice = null;
		String btDevcieAddress = "";
		try {
			if(null!=btDeviceFeature)
			{
				btCurrentDevice = btDeviceFeature.getConnectDevice();
				if (null!=btCurrentDevice)
				{
					btDevcieAddress = btCurrentDevice.getAddress();
				}
				if (btDeviceFeature.isConnectHFP()) {
					for (int i = 0; i < listMatchRecord.size(); i++) {
						if (btDevcieAddress.equals(listMatchRecord.get(i).getAddress())) {
							myAdapter.setSelect(i);
							break;
						}
					}
				} else {
					myAdapter.setSelect(-1);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		myAdapter.notifyDataSetChanged();
	}

	BroadcastReceiver mMatchReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BLUE_GET_DEVICES.equals(action)) {
				BTDevice device = intent.getExtras().getParcelable(
						"extra_pair_device");
				String deviceName = device.getName();
				String deviceAddress = device.getAddress();
				if(null==deviceName||"".equals(deviceName)||null==deviceAddress||"".equals(deviceAddress))
				{
					return; //不接收设备为空的记录
					/*deviceName = device.getAddress();
					device.setName(deviceName);*/
				}
				int index = device.getPairRecordIndex();
				if (listMatchRecord.size() > 0) {
					boolean isNeedAdd = true;
					for (int i = 0; i < listMatchRecord.size(); i++) {
						if (listMatchRecord.get(i).getName()
								.equals(deviceName)) {
							isNeedAdd = false;
							break;
						}
					}
					if (isNeedAdd) {
						listMatchRecord.add(device);
					}
				} else {
					listMatchRecord.add(device);
				}
				myAdapter.notifyDataSetChanged();
			} else if (BTDefine.ACTION_BT_CONNECTION_CHANGE.equals(action)) {
                /**更改设备状态*/
				int status = intent.getIntExtra(BTDefine.EXTRA_BT_CONNECTION_EVENT, 0);
				if (BTDefine.VALUE_CONNECTION_EVENT_CONNECT == status) {

					BtBroadcastReceiver.BT_PHONE_STATUS = BTDefine.ACTION_BT_HFP_ESTABLISHED;
				} else {
					BtBroadcastReceiver.BT_PHONE_STATUS = BTDefine.ACTION_BT_HFP_RELEASE;
				}
			}
			setCurrentDeviceName();
		}
	};

	protected abstract View onCreateViewBtMatchRecord();

	protected abstract View onCreateViewItemMatchRecord();

	protected abstract void onInflateMenuMatchRecord(MenuInflater inflater,Menu menu);

	protected abstract void onChangeViewItemConnectedState(View view,boolean select);
}