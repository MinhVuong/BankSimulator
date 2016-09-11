/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.bo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author MINH TRI
 */
public class Param {
    private String id;
    private String value;
    private String name;
            
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

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
