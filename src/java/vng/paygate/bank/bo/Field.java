/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.bo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author trinm2
 */
public class Field {

    
    private String id;
    private String value;
    
    @XmlAttribute
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    
    @XmlValue
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    
}
