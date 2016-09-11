package vng.paygate.bank.jaxb.adapter;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import vng.paygate.domain.bo.BoBaseResponse;

/**
 *
 * @author CheHC
 * @since 123Pay
 * @created on: Aug 15, 2012
 */
public class ResponseCodeMapAdapter extends XmlAdapter<ResponseCodes, Map<String,BoBaseResponse> > {

    @Override
    public ResponseCodes marshal(Map<String, BoBaseResponse> v) throws Exception {
        if (v == null) {
//            System.out.println("Map is null");
            return null;
        }
        BoBaseResponse[] configs = new BoBaseResponse[v.size()];

        for (int i = 0; i < v.size(); i++) {
            configs[i] = v.get(i + "");
        }
        ResponseCodes res = new ResponseCodes();
        res.setResponses(configs);
        return res;
    }

    @Override
    public Map<String, BoBaseResponse> unmarshal(ResponseCodes v) throws Exception {
         Map<String, BoBaseResponse> map = new HashMap<String, BoBaseResponse>();
        for (BoBaseResponse config : v.getResponses()) {
            map.put(config.getDetailResponseCode(), config);
        }
        return map;
    }
    
}
