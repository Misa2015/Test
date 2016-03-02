package com.ls.bt.call.detail;

public interface ParseCallBack {
    public void onContactSync(String name, String cellNumber);
    public void onCalllogSync(int type, String name, String phoneNumber);
    public void onSmsSync(int boxNumber, String smsCallerID, String smsBody, int readState);
}
