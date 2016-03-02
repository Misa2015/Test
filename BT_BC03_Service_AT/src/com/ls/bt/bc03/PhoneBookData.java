package com.ls.bt.bc03;


/**
 * 电话本单条记录项
 * @author app-lilinhuang
 */
public class PhoneBookData {

    private String mName = null;   //姓名
    private String mNumber = null; //号码
    
    public PhoneBookData(String name, String number) {
        this.mName = name;
        this.mNumber = number;
    }

    public String getName() {
        return mName;
    }
    
    public void setName(String name) {
        this.mName = name;
    }
    
    public String getNumber() {
        return mNumber;
    }
    
    public void setNumber(String number) {
        this.mNumber = number;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof PhoneBookData) {
            PhoneBookData tempPhoneBookData = (PhoneBookData)o;
            return mName.equals(tempPhoneBookData.getName()) && mNumber.equals(tempPhoneBookData.getNumber());
        } else {
            return super.equals(o);
        }
    }
    
    @Override
    public String toString() {
        
        return mName + " " + mNumber;
    }
}
