/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.jaxb.adapter;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Kuti
 */
public class SubBankAdapter extends XmlAdapter<SubBanks, Map<String, SubBank> >{

    @Override
    public Map<String, SubBank> unmarshal(SubBanks v) throws Exception {
        Map<String, SubBank> map = new HashMap<String, SubBank>();
        for (SubBank config : v.getSubBanks()) {
            map.put(config.getSubBankCode(), config);
        }
        return map;
    }

    @Override
    public SubBanks marshal(Map<String, SubBank> v) throws Exception {
        if (v == null) {
//            System.out.println("Map is null");
            return null;
        }
        SubBank[] configs = new SubBank[v.size()];

        for (int i = 0; i < v.size(); i++) {
            configs[i] = v.get(i + "");
        }
        SubBanks res = new SubBanks();
        res.setSubBanks(configs);
        return res;
    }
    
}
