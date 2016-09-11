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
public class SubBanks {
    private SubBank[] subBanks;

    @XmlElement(name="subBank")
    public SubBank[] getSubBanks() {
        return subBanks;
    }

    public void setSubBanks(SubBank[] subBanks) {
        this.subBanks = subBanks;
    }
    
}
