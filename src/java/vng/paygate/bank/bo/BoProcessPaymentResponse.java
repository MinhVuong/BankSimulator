package vng.paygate.bank.bo;

import vng.paygate.domain.bo.BoBaseResponse;

/**
 *
 * @author trinm2
 * @since 123Pay 
 * @created on: Oct 25, 2012
 */
public class BoProcessPaymentResponse extends BoBaseResponse {

    private String checksum;
//    private String returnURL;
    private String redirectURL;
    private String merchantTransactionId;
    private Integer orderStatus;
    private Integer totalAmount;
    private Integer opAmount;
    private String bankCode;
    private String merchantCode;
    private String bankResponseCode;
    private String description;
    private Integer notifyStatus;

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
    
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantTransactionId() {
        return merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId) {
        this.merchantTransactionId = merchantTransactionId;
    }

    public Integer getOpAmount() {
        return opAmount;
    }

    public void setOpAmount(Integer opAmount) {
        this.opAmount = opAmount;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Integer getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(Integer notifyStatus) {
        this.notifyStatus = notifyStatus;
    }
    
    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

//    public String getReturnURL() {
//        return returnURL;
//    }
//
//    public void setReturnURL(String returnURL) {
//        this.returnURL = returnURL;
//    }
}
