/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.jaxb.adapter;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import vng.paygate.bank.bo.BoCardInfo;
import vng.paygate.domain.bo.BoBase;

/**
 *
 * @author kietn
 */
@XmlRootElement
public class BoBaseBank extends BoBase{
    private String bankCode;
    private String payportURL;
    private String cardInfoUrl;
    private String otpInfoUrl;
    private String maxInput;
    private String rc4SecretKey;
    private List<BoCardInfo> boCardInfo;
    public BoBaseBank() {
    
    }
    @XmlElement
    public String getRc4SecretKey() {
        return rc4SecretKey;
    }

    public void setRc4SecretKey(String rc4SecretKey) {
        this.rc4SecretKey = rc4SecretKey;
    }
    

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    @XmlElement
    public String getCardInfoUrl() {
        return cardInfoUrl;
    }

    public void setCardInfoUrl(String cardInfoUrl) {
        this.cardInfoUrl = cardInfoUrl;
    }
    @XmlElement
    public String getMaxInput() {
        return maxInput;
    }

    public void setMaxInput(String maxInput) {
        this.maxInput = maxInput;
    }
    @XmlElement
    public String getOtpInfoUrl() {
        return otpInfoUrl;
    }

    public void setOtpInfoUrl(String otpInfoUrl) {
        this.otpInfoUrl = otpInfoUrl;
    }
    @XmlElement
    public String getPayportURL() {
        return payportURL;
    }

    public void setPayportURL(String payportURL) {
        this.payportURL = payportURL;
    }
    
    @XmlElementWrapper(name = "cardInfos")
    @XmlElement(name = "cardInfo")
    public List<BoCardInfo> getCardInfos() {
        return boCardInfo;
    }

    public void setCardInfos(List<BoCardInfo> boCardInfo) {
        this.boCardInfo = boCardInfo;
    }
}
