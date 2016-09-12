/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author Kuti
 */
@XmlRootElement
public class SubBank extends BoBase{
    private String subBankCode;
    private String isInputOTP;
    private List<BoCardInfo> boCardInfo;
    public SubBank() {
    }

    @XmlElement
    public String getSubBankCode() {
        return subBankCode;
    }
    @XmlElementWrapper(name = "cardInfos")
    @XmlElement(name = "cardInfo")
    public List<BoCardInfo> getBoCardInfo() {
        return boCardInfo;
    }
    @XmlElement
    public String getIsInputOTP() {
        return isInputOTP;
    }

    public void setIsInputOTP(String isInputOTP) {
        this.isInputOTP = isInputOTP;
    }
    
    public void setSubBankCode(String subBankCode) {
        this.subBankCode = subBankCode;
    }

    public void setBoCardInfo(List<BoCardInfo> boCardInfo) {
        this.boCardInfo = boCardInfo;
    }
    
}
