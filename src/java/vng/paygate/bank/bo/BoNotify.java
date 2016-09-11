/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.bo;

/**
 *
 * @author trinm2
 */
public class BoNotify {

    private String orderNo;
    private String bankCode;
    private String bankService;
    private String bankResponseCode;
    private int notifyOrQuery;
    private Integer responseCode;
    private String notifyUrl;
    
    private String orderStatus;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankResponseCode() {
        return bankResponseCode;
    }

    public void setBankResponseCode(String bankResponseCode) {
        this.bankResponseCode = bankResponseCode;
    }

    public String getBankService() {
        return bankService;
    }

    public void setBankService(String bankService) {
        this.bankService = bankService;
    }

    public int getNotifyOrQuery() {
        return notifyOrQuery;
    }

    public void setNotifyOrQuery(int notifyOrQuery) {
        this.notifyOrQuery = notifyOrQuery;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }
    
    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
