package com.ls.bt.contact;

import java.util.Set;

/**
 * 一个实体类，存储一些数据
 * 
 * @author user
 * 
 */
public class Contact {
	private String mName;
	private String mNumber;
	private String mSortKey;
	private String[] mInitialArray;

	public void setmInitialArray(String[] mInitialArray) {
		this.mInitialArray = mInitialArray;
	}

	public String[] getmInitialArray() {
		return mInitialArray;
	}

	public Contact(String name, String number) {
		mName = name;
		mNumber = number;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getSortKey() {
		return mSortKey;
	}

	public void setSortKey(String c) {
		mSortKey = c;
	}

	public String getNumber() {
		return mNumber;
	}

	public void setNumbet(String number) {
		mNumber = number;
	}

	public String toString() {
		return "name: " + mName + " number: " + mNumber;
	}

}
