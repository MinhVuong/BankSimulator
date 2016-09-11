/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.bo;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author trinm2
 * @since 123Pay
 * @created on July, 25, 2012
 */
@XmlRootElement(name = "isomsg")
public class BoBSMessage {
    
    private String name;
    private List<Field> lstField;
    private String responseCode;
    
    //serve for transfer service
    private String transref;
    private String merchid;
    private List<Param> lstParam;
            
    private String encryptedData;
    
    
    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @XmlElement(name="field")
    public List<Field> getLstField() {
        return lstField;
    }

    public void setLstField(List<Field> lstField) {
        this.lstField = lstField;
    }

    
    @XmlElement(name="encrypted")
    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }
    
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @XmlElement(name="merchid")
    public String getMerchid() {
        return merchid;
    }

    public void setMerchid(String merchid) {
        this.merchid = merchid;
    }

    @XmlElement(name="transref")
    public String getTransref() {
        return transref;
    }

    public void setTransref(String transref) {
        this.transref = transref;
    }

    @XmlElement(name="para")
    public List<Param> getLstParam() {
        return lstParam;
    }

    public void setLstParam(List<Param> lstParam) {
        this.lstParam = lstParam;
    }
    
    
}
