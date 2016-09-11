/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.jaxb.adapter;

import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import vng.paygate.domain.bo.BoBaseResponse;
import vng.paygate.domain.bo.BoModuleConfig;

/**
 *
 * @author Kuti
 */
@XmlRootElement(name = "BiModuleConfig")
public class BoBIModuleConfigNew extends BoModuleConfig {
    //Contain all config for merchants. Map<MerchantCode, BoMerchantConfig>    
    //private BoEIBConfig bankConfig;
    //Internal sectetKey
    private Map<String, BoBaseBankNew> bankCodeMap;
    private String secretKey;
    //Internal IPs
    private List<String> allowedIPs;
    private Map<String, BoBaseResponse> responseCodeMap;
    private String frontEndSecretKey;
    private String urlNotify;
    private String waitingTime;

    public BoBIModuleConfigNew() {
    }

    @XmlJavaTypeAdapter(BankCodeNewAdapter.class)
    @XmlElement(name = "banks")
    public Map<String, BoBaseBankNew> getBankCodeMap() {
        return bankCodeMap;
    }
    @XmlElement(name = "secretKey")
    public String getSecretKey() {
        return secretKey;
    }
    @XmlElementWrapper(name = "allowedIPs")
    @XmlElement(name = "ip")
    public List<String> getAllowedIPs() {
        return allowedIPs;
    }
    @XmlJavaTypeAdapter(ResponseCodeMapAdapter.class)
    @XmlElement(name = "responses")
    @Override
    public Map<String, BoBaseResponse> getResponseCodeMap() {
        return responseCodeMap;
    }
    @XmlElement(name = "frontEndSecretKey")
    public String getFrontEndSecretKey() {
        return frontEndSecretKey;
    }
    @XmlElement(name = "urlNotify")
    public String getUrlNotify() {
        return urlNotify;
    }
    @XmlElement(name = "waitingTime")
    public String getWaitingTime() {
        return waitingTime;
    }

    public void setBankCodeMap(Map<String, BoBaseBankNew> bankCodeMap) {
        this.bankCodeMap = bankCodeMap;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAllowedIPs(List<String> allowedIPs) {
        this.allowedIPs = allowedIPs;
    }

    public void setResponseCodeMap(Map<String, BoBaseResponse> responseCodeMap) {
        this.responseCodeMap = responseCodeMap;
    }

    public void setFrontEndSecretKey(String frontEndSecretKey) {
        this.frontEndSecretKey = frontEndSecretKey;
    }

    public void setUrlNotify(String urlNotify) {
        this.urlNotify = urlNotify;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }
    
    
}
