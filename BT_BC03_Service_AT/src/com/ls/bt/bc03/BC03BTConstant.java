package com.ls.bt.bc03;

public class BC03BTConstant
{
    /**
     * 列表类型
     * @author yewei
     *
     */
    public interface ListType
    {
        /**最后一次*/
        public static final int LAST_USED = 0;
        /**匹配列表*/
        public static final int PAIR_LIST = 1;
        /**搜索列表*/
        public static final int SEARCH_LIST = 2;
    }
    
    /**
     * AVRCP播放命令
     * @author yewei
     *
     */
    public interface PlayCommand
    {
        public static final int STOP = 0;
        public static final int PLAY = 1;
        public static final int PAUSE = 2;
        public static final int NEXT = 3;
        public static final int PREVIOUS = 4;
    }
    
    public interface A2DPStatus
    {
        public static final int READY = 0;
        public static final int CONNECTING = 1;
        public static final int CONNECTED = 2;
        public static final int STREAMING = 3;
    }
    
    public interface AVRCPStatus
    {
        public static final int READY = 0;
        public static final int CONNECTING = 1;
        public static final int CONNECTED = 2;
    }
    
    public interface HFPStatus
    {
        public static final int READY = 0;
        public static final int CONNECTING = 1;
        public static final int CONNECTED = 2;
        public static final int CALL_INCOMING = 3;
        public static final int CALL_OUTGOING = 4;
        public static final int CALL_ACTIVE = 5;
    }
    
    public interface ConnectProfile
    {
        public static final int NO_CONNECT_PROFILE = 0;
        public static final int HSP = 1;
        public static final int HFP = 2;
    }
    
    public interface SCOStatus
    {
        public static final int DISCONNECTED = 0;
        public static final int CONNECTED = 1;
    }
    
    public interface MuteStatus
    {
        public static final int UNMUTE = 0;
        public static final int MUTE = 1;
    }
    
    public interface PowerStatus
    {
        public static final int OFF = 0;
        public static final int ON = 1;
        public static final int POWER_DOWN = 2;
    }
    
    public interface PairStatus
    {
        public static final int SUCCESS = 0;
        public static final int TIMEOUT = 1;
        public static final int FAILD = 2;
        public static final int PAIRING_IN_PROGRESS = 3;
        public static final int PAIR_CANCEL = 4;
    }
    
    public interface PhoneBookType
    {
        public static final int MISS_CALL = 0;
        public static final int RECEIVE_CALL = 1;
        public static final int DIAL_CALL = 2;
        public static final int PHONE_CONTACT = 3;
        public static final int SIM_CONTACT = 4;
    }
    
    public interface CallStatus
    {
        public static final int CALL_ESTABLISH = 1;
        public static final int CALL_HOOK = 0;
    }
    
    public interface SoundChannel
    {
        public static final int CHANNEL_AG = 0;
        public static final int CHANNEL_HFP = 1;
        public static final int CHANNEL_ANOTHER = 2;
    }
    
    public interface AutoFeature
    {
        public static final int AUTO_ACCEPT_CALL = 0;
        public static final int AUTO_CONNECT_DEVICE = 1;
    }
}
