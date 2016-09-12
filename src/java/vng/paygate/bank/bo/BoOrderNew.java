/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.bo;

import java.util.Date;
import vng.paygate.domain.bo.BoBase;
import vng.paygate.domain.bo.BoBaseResponse;

/**
 *
 * @author CPU01661-local
 */
public class BoOrderNew extends BoBase{
    private String orderNo;
    private String bankCode;
    private String merchantCode;
    private Integer orderStatus;
    private Integer opAmount;
    private Integer discountAmount;
    private String bankResponseCode;
    private String bankTransactionId;
    private String bankService;
    private Integer merchantResponseCode;
    private String merchantTransactionId;
    private String merchantService;
    private String currency;
    private String description;
    private Date createDate;
    private Date bankServiceDate;
    private Date bankNotifyDate;
    private Date bankQueryDate;
    private Integer bankQueryRetryCount;
    private Integer merchantNotifyStatus;
    private Date merchantNotifyDate;
    private Integer merchantNotifyRetryCount;
    private String clientIP;
    private Integer orderNoSuffix;
    private String custName;
    private String custAddress;
    private String custPhone;
    private String custEmail;
    private String custGender;
    private String custDOB;
    private BoBaseResponse boBaseResponse;
    private String passcode;
    private String[] addInfo;
    private Integer totalAmount;
    private String queryBOrderUrl;
    private Integer merchantAmount;
    private String responseCode;
    private String accountName;
    private String accountID;
    private String emailLogin;
    private String phoneLogin;
    private String loginType;
    private Integer notifyStatus;
    private String cancelURL;
    private String redirectURL;
    private String errorURL;
    private String selectedBank;
    private String bankId;
    private String version;
    private String secretKey;
    private Integer isWaiting;
    private String subbankCode;

    public Integer getIsWaiting() {
        return this.isWaiting;
    }

    public void setIsWaiting(Integer isWaiting) {
        this.isWaiting = isWaiting;
    }

    public Integer getMerchantAmount() {
        return this.merchantAmount;
    }

    public void setMerchantAmount(Integer merchantAmount) {
        this.merchantAmount = merchantAmount;
    }

    public String getCancelURL() {
        return this.cancelURL;
    }

    public void setCancelURL(String cancelURL) {
        this.cancelURL = cancelURL;
    }

    public String getErrorURL() {
        return this.errorURL;
    }

    public void setErrorURL(String errorURL) {
        this.errorURL = errorURL;
    }

    public String getRedirectURL() {
        return this.redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public Integer getNotifyStatus() {
        return this.notifyStatus;
    }

    public void setNotifyStatus(Integer notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Integer getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String[] getAddInfo() {
        return this.addInfo;
    }

    public void setAddInfo(String[] addInfo) {
        this.addInfo = addInfo;
    }

    public String getPasscode() {
        return this.passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public Integer getDiscountAmount() {
        return this.discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getOpAmount() {
        return this.opAmount;
    }

    public void setOpAmount(Integer opAmount) {
        this.opAmount = opAmount;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getMerchantCode() {
        return this.merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Date getBankNotifyDate() {
        return this.bankNotifyDate;
    }

    public void setBankNotifyDate(Date bankNotifyDate) {
        this.bankNotifyDate = bankNotifyDate;
    }

    public Date getBankQueryDate() {
        return this.bankQueryDate;
    }

    public void setBankQueryDate(Date bankQueryDate) {
        this.bankQueryDate = bankQueryDate;
    }

    public Integer getBankQueryRetryCount() {
        return this.bankQueryRetryCount;
    }

    public void setBankQueryRetryCount(Integer bankQueryRetryCount) {
        this.bankQueryRetryCount = bankQueryRetryCount;
    }

    public String getBankResponseCode() {
        return this.bankResponseCode;
    }

    public void setBankResponseCode(String bankResponseCode) {
        this.bankResponseCode = bankResponseCode;
    }

    public String getBankService() {
        return this.bankService;
    }

    public void setBankService(String bankService) {
        this.bankService = bankService;
    }

    public Date getBankServiceDate() {
        return this.bankServiceDate;
    }

    public void setBankServiceDate(Date bankServiceDate) {
        this.bankServiceDate = bankServiceDate;
    }

    public String getBankTransactionId() {
        return this.bankTransactionId;
    }

    public void setBankTransactionId(String bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
    }

    public String getClientIP() {
        return this.clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustAddress() {
        return this.custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCustDOB() {
        return this.custDOB;
    }

    public void setCustDOB(String custDOB) {
        this.custDOB = custDOB;
    }

    public String getCustEmail() {
        return this.custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }

    public String getCustGender() {
        return this.custGender;
    }

    public void setCustGender(String custGender) {
        this.custGender = custGender;
    }

    public String getCustName() {
        return this.custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustPhone() {
        return this.custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getMerchantNotifyDate() {
        return this.merchantNotifyDate;
    }

    public void setMerchantNotifyDate(Date merchantNotifyDate) {
        this.merchantNotifyDate = merchantNotifyDate;
    }

    public Integer getMerchantNotifyRetryCount() {
        return this.merchantNotifyRetryCount;
    }

    public void setMerchantNotifyRetryCount(Integer merchantNotifyRetryCount) {
        this.merchantNotifyRetryCount = merchantNotifyRetryCount;
    }

    public Integer getMerchantNotifyStatus() {
        return this.merchantNotifyStatus;
    }

    public void setMerchantNotifyStatus(Integer merchantNotifyStatus) {
        this.merchantNotifyStatus = merchantNotifyStatus;
    }

    public Integer getMerchantResponseCode() {
        return this.merchantResponseCode;
    }

    public void setMerchantResponseCode(Integer merchantResponseCode) {
        this.merchantResponseCode = merchantResponseCode;
    }

    public String getMerchantService() {
        return this.merchantService;
    }

    public void setMerchantService(String merchantService) {
        this.merchantService = merchantService;
    }

    public String getMerchantTransactionId() {
        return this.merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId) {
        this.merchantTransactionId = merchantTransactionId;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderNoSuffix() {
        return this.orderNoSuffix;
    }

    public void setOrderNoSuffix(Integer orderNoSuffix) {
        this.orderNoSuffix = orderNoSuffix;
    }

    public Integer getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BoBaseResponse getBoBaseResponse() {
        return this.boBaseResponse;
    }

    public void setBoBaseResponse(BoBaseResponse boBaseResponse) {
        this.boBaseResponse = boBaseResponse;
    }

    public String getQueryBOrderUrl() {
        return this.queryBOrderUrl;
    }

    public void setQueryBOrderUrl(String queryBOrderUrl) {
        this.queryBOrderUrl = queryBOrderUrl;
    }

    public String getAccountID() {
        return this.accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getEmailLogin() {
        return this.emailLogin;
    }

    public void setEmailLogin(String emailLogin) {
        this.emailLogin = emailLogin;
    }

    public String getLoginType() {
        return this.loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getPhoneLogin() {
        return this.phoneLogin;
    }

    public void setPhoneLogin(String phoneLogin) {
        this.phoneLogin = phoneLogin;
    }

    public String getSelectedBank() {
        return this.selectedBank;
    }

    public void setSelectedBank(String selectedBank) {
        this.selectedBank = selectedBank;
    }

    public String getBankId() {
        return this.bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String toString() {
        return this.orderNo;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSubbankCode() {
        return subbankCode;
    }

    public void setSubbankCode(String subbankCode) {
        this.subbankCode = subbankCode;
    }
    
}
