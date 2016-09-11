/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.jaxb.adapter;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author kietn
 */
public class BankCodeMapAdapter extends XmlAdapter<BankCodes, Map<String,BoBaseBank> >{

    @Override
    public Map<String, BoBaseBank> unmarshal(BankCodes v) throws Exception {
        Map<String, BoBaseBank> map = new HashMap<String, BoBaseBank>();
        for (BoBaseBank config : v.getBanks()) {
            map.put(config.getBankCode(), config);
        }
        return map;
    }

    @Override
    public BankCodes marshal(Map<String, BoBaseBank> v) throws Exception {
        if (v == null) {
//            System.out.println("Map is null");
            return null;
        }
        BoBaseBank[] configs = new BoBaseBank[v.size()];

        for (int i = 0; i < v.size(); i++) {
            configs[i] = v.get(i + "");
        }
        BankCodes res = new BankCodes();
        res.setBanks(configs);
        return res;
    }
    
}
