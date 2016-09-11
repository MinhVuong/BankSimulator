/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vng.paygate.bank.bo;

import java.util.Date;
import vng.paygate.domain.bo.BoBaseResponse;

/**
 *
 * @author trieunv
 */
public class BoBS extends BoBaseResponse{
    private String transactionId;
    private String cardNo;
    private String cardHolderName;
    private Date expireDate;
    private String otp;
    private int isSuccess;
    private String bankResponseCode;
    private Integer responseCode;
    private Integer orderStatus;
    private double amount;
    private String merchantCode;
    private String checksum;
    private String refNo;
    private String approvalCode;
    private String pathKey;
    private String transactionIdSuffix;
    private String cardNoChecksum;
    private String cardNo4Last;
    private String cardNo6First;
    private String miNotifyUrl;
    private String bankCode;
    private String customerId;
    private String description;
    private String bankService;
    private int notifyOrQuery;
    private String cardHash;
    private int numberInput;
    private String clientIp;
    private String banksimCode;
    private int iOrderStatus;

    public int getiOrderStatus() {
        return iOrderStatus;
    }

    public void setiOrderStatus(int iOrderStatus) {
        this.iOrderStatus = iOrderStatus;
    }
    
    
    public String getTransactionIdSuffix() {
        return transactionIdSuffix;
    }

    public void setTransactionIdSuffix(String transactionIdSuffix) {
        this.transactionIdSuffix = transactionIdSuffix;
    }

    public String getPathKey() {
        return pathKey;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }
    
    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getBankResponseCode() {
        return bankResponseCode;
    }

    public void setBankResponseCode(String bankResponseCode) {
        this.bankResponseCode = bankResponseCode;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCardNoChecksum() {
        return cardNoChecksum;
    }

    public void setCardNoChecksum(String cardNoChecksum) {
        this.cardNoChecksum = cardNoChecksum;
    }

    public String getCardNo4Last() {
        return cardNo4Last;
    }

    public void setCardNo4Last(String cardNo4Last) {
        this.cardNo4Last = cardNo4Last;
    }

    public String getMiNotifyUrl() {
        return miNotifyUrl;
    }

    public void setMiNotifyUrl(String miNotifyUrl) {
        this.miNotifyUrl = miNotifyUrl;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardNo6First() {
        return cardNo6First;
    }

    public void setCardNo6First(String cardNo6First) {
        this.cardNo6First = cardNo6First;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCardHash() {
        return cardHash;
    }

    public void setCardHash(String cardHash) {
        this.cardHash = cardHash;
    }

    public int getNumberInput() {
        return numberInput;
    }

    public void setNumberInput(int numberInput) {
        this.numberInput = numberInput;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getBanksimCode() {
        return banksimCode;
    }

    public void setBanksimCode(String banksimCode) {
        this.banksimCode = banksimCode;
    }
    
}
