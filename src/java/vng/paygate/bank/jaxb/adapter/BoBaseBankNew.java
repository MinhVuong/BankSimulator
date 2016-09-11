/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.jaxb.adapter;

import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import vng.paygate.domain.bo.BoBase;

/**
 *
 * @author Kuti
 */
@XmlRootElement
public class BoBaseBankNew  extends BoBase{
    private String bankCode;
    private String payportURL;
    private String cardInfoUrl;
    private String otpInfoUrl;
    private String maxInput;
    private String rc4SecretKey;
    private Map<String, SubBank> subBanks;

    public BoBaseBankNew() {
    }
    @XmlElement
    public String getBankCode() {
        return bankCode;
    }
    @XmlElement
    public String getPayportURL() {
        return payportURL;
    }
    @XmlElement
    public String getCardInfoUrl() {
        return cardInfoUrl;
    }
    @XmlElement
    public String getOtpInfoUrl() {
        return otpInfoUrl;
    }
    @XmlElement
    public String getMaxInput() {
        return maxInput;
    }
    @XmlElement
    public String getRc4SecretKey() {
        return rc4SecretKey;
    }
    @XmlJavaTypeAdapter(SubBankAdapter.class)
    @XmlElement(name = "subBanks")
    public Map<String, SubBank> getSubBanks() {
        return subBanks;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setPayportURL(String payportURL) {
        this.payportURL = payportURL;
    }

    public void setCardInfoUrl(String cardInfoUrl) {
        this.cardInfoUrl = cardInfoUrl;
    }

    public void setOtpInfoUrl(String otpInfoUrl) {
        this.otpInfoUrl = otpInfoUrl;
    }

    public void setMaxInput(String maxInput) {
        this.maxInput = maxInput;
    }

    public void setRc4SecretKey(String rc4SecretKey) {
        this.rc4SecretKey = rc4SecretKey;
    }

    public void setSubBanks(Map<String, SubBank> subBanks) {
        this.subBanks = subBanks;
    }
    
}
