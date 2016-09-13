/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.bo;

import vng.paygate.domain.bo.BoOrder;

/**
 *
 * @author trinm2
 */
public class BoBSOtp extends BoOrder{
    private String cardNo;
    private String miNotifyUrl;
    private String subbankCode;
    
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getMiNotifyUrl() {
        return miNotifyUrl;
    }

    public void setMiNotifyUrl(String miNotifyUrl) {
        this.miNotifyUrl = miNotifyUrl;
    }

    public String getSubbankCode() {
        return subbankCode;
    }

    public void setSubbankCode(String subbankCode) {
        this.subbankCode = subbankCode;
    }
    
}
