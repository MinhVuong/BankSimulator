/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.jaxb.adapter;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author kietn
 */
public class BankCodes {
    private BoBaseBank[] banks;

    @XmlElement(name="bank")
    public BoBaseBank[] getBanks() {
        return banks;
    }

    public void setBanks(BoBaseBank[] banks) {
        this.banks = banks;
    }
}
