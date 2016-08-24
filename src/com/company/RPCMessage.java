package com.company;

import java.io.Serializable;


/**
 * Created by blahblah Team on 2016/8/22.
 */
public class RPCMessage implements Serializable {

    public static final short REQUEST = 0;
    public static final short REPLY = 1;
    public static final short REQUEST_NEXT_STOP = 0;
    public static final short REQUEST_CURRENT_LOC = 1;
    public static final short REPLY_NEXT_STOP = 2;
    public static final short REPLY_CURRENT_LOC = 3;
    public static final short SUCCESS = 0;
    public static final short RETRIEVE_NEXT_STOP_FAILED = 1;
    public static final short UPDATE_LOC_FAILED = 2;
    public static final int NEXT_STOP_INDEX = 0;

    public enum MessageType{
        REQUEST, REPLY
    }
    private MessageType messageType;
    private long TransactionId; /* transaction id */
    private long RPCId; /* Globally unique identifier */
    private long RequestId; /* Client request message counter */
    private short procedureId; /* e.g.(1,2,3,4) */
    private String csv_data; /* data as comma separated values*/
    private short status;

    public long getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(long transactionId) {
        TransactionId = transactionId;
    }

    public String getCsv_data() {
        return csv_data;
    }

    public void setCsv_data(String csv_data) {
        this.csv_data = csv_data;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public short getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(short procedureId) {
        this.procedureId = procedureId;
    }

    public long getRequestId() {
        return RequestId;
    }

    public void setRequestId(long requestId) {
        RequestId = requestId;
    }

    public long getRPCId() {
        return RPCId;
    }

    public void setRPCId(long RPCId) {
        this.RPCId = RPCId;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }


}
