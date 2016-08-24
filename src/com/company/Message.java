package com.company;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by blahblah Team on 2016/8/22.
 */


public class Message implements Serializable {
    public static final int MSG_TYPE_INDEX = 0;
    public static final int TRNSC_ID_INDEX = 2;
    public static final int RPC_ID_INDEX = 10;
    public static final int REQ_ID_INDEX = 18;
    public static final int PROC_ID_INDEX = 26;
    public static final int STATUS_INDEX = 28;
    public static final int CSV_DATA_INDEX = 30;
    protected byte data[] = null;
    protected int length = 0;

    public void marshal(RPCMessage rpcMessage){
//        private MessageType messageType;                  2 bytes
//        private long TransactionId; /* transaction id */  8 bytes
//        private long RPCId; /* Globally unique identifier */  8 bytes
//        private long RequestId; /* Client request message counter */ 8 bytes
//        private short procedureId; /* e.g.(1,2,3,4) */    2 bytes
//        private String csv_data; /* data as comma separated values*/ length * 2 Bytes
//        private short status; 2 bytes

        length = 2 + 8 + 8 + 8 + 2 + 2 + rpcMessage.getCsv_data().length() * 2;
        ByteBuffer buf = ByteBuffer.allocate(length);
        RPCMessage.MessageType typeNum = rpcMessage.getMessageType();
        buf.putShort(MSG_TYPE_INDEX,(short)typeNum.ordinal());
        buf.putLong(TRNSC_ID_INDEX,rpcMessage.getTransactionId());
        buf.putLong(RPC_ID_INDEX,rpcMessage.getRPCId());
        buf.putLong(REQ_ID_INDEX,rpcMessage.getRequestId());
        buf.putShort(PROC_ID_INDEX,rpcMessage.getProcedureId());
        buf.putShort(STATUS_INDEX,rpcMessage.getStatus());
        String csv_data = rpcMessage.getCsv_data();
        int index = CSV_DATA_INDEX;
        for(int i = 0; i < csv_data.length(); i++, index += 2) {
            buf.putChar(index, csv_data.charAt(i));
        }
        data = new byte[length];
        buf.get(data, 0, length);
    }

    public RPCMessage unMarshal(){
        RPCMessage rpc_m = new RPCMessage();
        ByteBuffer buf = ByteBuffer.wrap(data);
        rpc_m.setMessageType(RPCMessage.MessageType.values()[buf.getShort(MSG_TYPE_INDEX)]);
        rpc_m.setTransactionId(buf.getLong(TRNSC_ID_INDEX));
        rpc_m.setRPCId(buf.getLong(RPC_ID_INDEX));
        rpc_m.setRequestId(buf.getLong(REQ_ID_INDEX));
        rpc_m.setProcedureId(buf.getShort(PROC_ID_INDEX));
        rpc_m.setStatus(buf.getShort(STATUS_INDEX));
        StringBuffer sb = new StringBuffer();
        int index = CSV_DATA_INDEX;
        for(; index < buf.array().length; index += 2) {
            sb.append(buf.getChar(index));
        }
        rpc_m.setCsv_data(sb.toString());
        return rpc_m;
    }

    public int getLength() {
        return length;
    }
}
