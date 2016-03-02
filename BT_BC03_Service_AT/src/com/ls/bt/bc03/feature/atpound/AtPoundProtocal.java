package com.ls.bt.bc03.feature.atpound;

import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.android.utils.uart.IUartProtocal;

public class AtPoundProtocal implements IUartProtocal
{
    private static final byte CR = '\r';
    private static final byte LF = '\n';
    private static final String CR_S = "\r";
    private static final String LF_S = "\n";
    private static final String HEAD = "AT#";
    
    private static final byte P = 'P';
    private static final byte B = 'B';
    private static final byte L = 'L';
    private static final byte M = 'M';
    private static final byte N = 'N';
    /**含有这些头的需要特殊处理下**/
    private final static String indHeads = "PB,PL,PM,PN";

    @Override
    public int checkCompleteProtocal(byte[] data, int offset, int len)
    {
        // TODO Auto-generated method stub
        //每条合格的协议长度最少为4位
        if (len >= 4)
        {
            // \r\n开头 \r\n结束
        	if (data[offset] == CR) {
        		if (data[offset + 1] == LF) {
        			/**是否为特殊的头*/
        			/*boolean isSpecial = false;
        			int moreLen = 0;
        			String aa = new String(data, offset + 2, 2);
        			if (indHeads.contains(aa)) {
        				isSpecial = true;
        				if (len > 9) {
        					moreLen = calcLen(data, offset);
        					if (moreLen < 0) {
        						Log.i("AtPoundProtocalUtil", "==calcLen result is error. moreLen == " + moreLen);
        						moreLen = 0;
        					}
        				}
        			}
        			
        			if (isSpecial && len < (moreLen + 4)) {
        				return CHECK_UNCOMPLETE;
        			}*/
        			
	        		//倒数第二位是\r
	                if (data[offset + len - 2] == CR )
	                {
	                    //最后一位是\n的则为完整协议,否则协议有错误
	                    if (data[offset + len - 1] == LF )
	                    {
	                        return CHECK_COMPLETE;
	                    }
	                    else
	                    {
	                        return CHECK_BAD_PROTOCAL;
	                    }
	                }
	                
        		}
        		else 
            		return CHECK_BAD_PROTOCAL;
        	}
        	else if (data[offset] == P) {
        		if (data[offset + 1] == B || data[offset + 1] == M 
        				|| data[offset + 1] == N || data[offset + 1] == L) {
	        		//倒数第二位是\r
	                if (data[offset + len - 2] == CR )
	                {
	                    //最后一位是\n的则为完整协议,否则协议有错误
	                    if (data[offset + len - 1] == LF )
	                    {
	                        return CHECK_COMPLETE;
	                    }
	                    else
	                    {
	                        return CHECK_BAD_PROTOCAL;
	                    }
	                }
	                
        		}
        		else 
            		return CHECK_BAD_PROTOCAL;
        	}
        	else 
        		return CHECK_BAD_PROTOCAL;

        }
        return CHECK_UNCOMPLETE;
    }
    /***
     * 计算要取的长度
     * @param data
     * @param offset
     * @return
     */
    private int calcLen(byte[] data, int offset) {
    	int iLen = 0;
    	String num = new String(data, offset + 4, 4);
    	try {
	    	int i1 = Integer.parseInt(num.substring(0,2));
	    	int i2 = Integer.parseInt(num.substring(2,4));
	    	iLen = i1 + i2 + 2;
    	}
    	catch (Exception e) {
    		Log.i("AtPoundProtocalUtil", "calcLen==error==" + e.getMessage());
    	}
    	return iLen;
    }

    /**
     * 创建一条AT指令
     * 
     * @param command
     * @return
     */
    public static final String createATCommand(String command)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(HEAD).append(command).append(CR_S).append(LF_S);
        return builder.toString();
    }

    /**
     * 获取蓝牙模块返回的消息指令
     * @param protocal
     * @return
     */
    public static final String getIND(byte[] protocal)
    {
    	String value = getSpIND(protocal);
    	if (value != null)
    		return value;
        value = new String(protocal, 2, 2);
        return value;
    }
    
    /**
     * 获取蓝牙模块返回的消息指令
     * @param protocal
     * @return
     */
    private static final String getSpIND(byte[] protocal)
    {
        String value = new String(protocal, 0, 2);
        if (indHeads.contains(value))
        	return value;
        return null;
    }

    /**
     * 获取返回消息指令的参数
     * @param protocal
     * @return
     */
    public static final String getINDParam(byte[] protocal)
    {
        if (protocal.length > 6)
        {
            String value = null;
            try
            {
                value = new String(protocal, 4, protocal.length - 6, "utf-8");
            }
            catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                value = new String(protocal, 4, protocal.length - 6);
            }
            return value;
        }
        else
        {
            return null;
        }
    }

}
