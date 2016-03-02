package com.ls.bt.bc03.feature;

public interface IBC03PhoneBookCallback
{
    /**
     * 获取到电话本记录
     * @param index
     * @param number
     * @param name
     * @param time
     */
    public void onGetPhoneBook(int index,String number,String name,String time);
    
    /**
     * 本次请求的获取电话本记录结束
     */
    public void onGetPhoneBookFinish();
    
    /**
     * 错误处理
     */
    public void onError();
}
