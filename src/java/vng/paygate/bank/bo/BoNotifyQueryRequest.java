/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.bo;

/**
 *
 * @author trinm2
 */
public class BoNotifyQueryRequest {

    private String command;
    private String tranxId;
    private String responseCode;
    private String checksum;
    private String orderNo;
    private String bankCode;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTranxId() {
        return tranxId;
    }

    public void setTranxId(String tranxId) {
        this.tranxId = tranxId;
    }
    
    
}
