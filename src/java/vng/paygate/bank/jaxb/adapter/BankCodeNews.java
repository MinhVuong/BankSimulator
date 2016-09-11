/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.jaxb.adapter;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Kuti
 */
public class BankCodeNews {
    private BoBaseBankNew[] banks;
    
    @XmlElement(name="bank")
    public BoBaseBankNew[] getBanks() {
        return banks;
    }

    public void setBanks(BoBaseBankNew[] banks) {
        this.banks = banks;
    }
}
