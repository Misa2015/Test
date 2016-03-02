package com.ls.bt.bc03.feature.atplus;

import java.io.UnsupportedEncodingException;

import com.android.utils.uart.IUartProtocal;

public class AtPlusProtocal implements IUartProtocal
{
    private static final byte CR = '\r';
    private static final byte LF = '\n';
    private static final String CR_S = "\r";
    private static final String LF_S = "\n";
    private static final String HEAD = "AT+";
    private static final String SEPERATOR = "=";
    @Override
    public int checkCompleteProtocal(byte[] data, int offset, int len)
    {
        // TODO Auto-generated method stub
        //每条合格的协议最少是5个字节
        if(len > 5)
        {
            //首字节是\r\n 
            if(data[offset] == CR && data[offset + 1] == LF)
            {
                //倒数第二位是\r
                if(data[offset + len - 2] == CR)
                {
                    //最后一位是\n的则为完整协议,否则协议有错误
                    if(data[offset + len - 1] == LF)
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
            {
                return CHECK_BAD_PROTOCAL;
            }
        }
        return CHECK_UNCOMPLETE;
    }

    /**
     * 创建一条AT指令
     * @param command
     * @param param
     * param为空则没参数
     * @param isNeedQuotationMark
     * 是否需要在参数中加上引号
     * @return
     */
    public static final String createATCommand(String command,String param,boolean isNeedQuotationMark)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(HEAD).append(command);
        if(param != null)
        {
            builder.append(SEPERATOR);
            if(isNeedQuotationMark)
            {
                builder.append("\"").append(param).append("\"");
            }
            else
            {
                builder.append(param);
            }
        }
        builder.append(CR_S).append(LF_S);
        return builder.toString();
    }
    
    /**
     * 创建一条只有命令的AT指令
     * @param command
     * @return
     */
    public static final String createATCommand(String command)
    {
        return createATCommand(command,null,false);
    }
    
    /**
     * 获取蓝牙模块返回的消息指令
     * @param protocal
     * @return
     */
    public static final String getIND(byte[] protocal)
    {
        String value = getProtocalValue(protocal);
        int seperatorIndex = value.indexOf(SEPERATOR); 
        if(seperatorIndex != -1)
        {
            value = value.substring(0, seperatorIndex);
        }
        return value;
    }
    
    /**
     * 获取返回消息指令的参数
     * @param protocal
     * @return
     */
    public static final String getINDParam(byte[] protocal)
    {
        String value = getProtocalValue(protocal);
        int seperatorIndex = value.indexOf(SEPERATOR);
        if(seperatorIndex != -1)
        {
            value = value.substring(seperatorIndex + 1, value.length());
            value = getStringFromQuotationMark(value);
            return value;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 获取int类型的参数
     * @param protocal
     * @return
     */
    public static final int getIntINDParam(byte[] protocal)
    {
        String value = getINDParam(protocal);
        if(value != null)
        {
            return Integer.parseInt(value);
        }
        else
        {
            return -1;
        }
    }
    
    /**
     * 从引号中获取字符串
     * @param value
     * @return
     */
    private static final String getStringFromQuotationMark(String value)
    {
        String result = value.replaceAll("\"", "");
        return result;
    }
    
    /**
     * 获取协议的内容
     * @param protocal
     * @return
     */
    protected static final String getProtocalValue(byte[] protocal)
    {
        try
        {
            return new String(protocal, 2, protocal.length - 4,"utf-8");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new String(protocal, 2, protocal.length - 4);
        }
    }
}
