package vng.paygate.bank.ws.soap.impl;

import javax.jws.WebMethod;
import javax.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import vng.paygate.bank.jaxb.adapter.BoBIModuleConfigNew;
import vng.paygate.config.service.IConfigService;

/**
 *
 * @author CheHC
 * @since 123Pay 
 * @created on: Sep 22, 2012
 */
@WebService(serviceName = "refreshCache")
public class RefreshCacheServiceEndpointImpl  extends SpringBeanAutowiringSupport{

    @Autowired
    IConfigService<BoBIModuleConfigNew> configService;

    @WebMethod(operationName = "refreshCache")
    public String refreshCache() {
        try {
            configService.refreshModuleConfig();
//            configService.loadDatabaseConfigs();
            return "success";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
