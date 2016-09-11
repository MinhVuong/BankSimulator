package vng.paygate.bank.jaxb.adapter;

import javax.xml.bind.annotation.XmlElement;
import vng.paygate.domain.bo.BoBaseResponse;

/**
 *
 * @author CheHC
 * @since 123Pay
 * @created on: Aug 15, 2012
 */
public class ResponseCodes {
    private BoBaseResponse[] responses;

    @XmlElement(name="response")
    public BoBaseResponse[] getResponses() {
        return responses;
    }

    public void setResponses(BoBaseResponse[] responses) {
        this.responses = responses;
    }

}
