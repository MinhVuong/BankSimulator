package vng.paygate.bank.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;
import vng.paygate.domain.bo.BoModuleConfig;
import vng.paygate.domain.exception.TechniqueException;
import vng.paygate.domain.service.IOrderService;

/**
 *
 * @param <T>
 * @author CheHC
 * @since 123Pay @created on: Aug 13, 2012
 */
public class ConfigServiceImpl<T extends BoModuleConfig> extends vng.paygate.config.service.impl.ConfigServiceImpl<T> {
    
     public ConfigServiceImpl(String envConfigFile,String propsConfigFile,String xmlConfigFile, Class<T> genericType, IOrderService orderService) throws IOException, JAXBException, FileNotFoundException, ConfigurationException, ClassNotFoundException, TechniqueException {
         super(envConfigFile, propsConfigFile, xmlConfigFile, genericType, orderService);
     }
     
    @Override
     protected void loadDatabaseConfigs(T configs) throws TechniqueException {
         //Do nothing, not load DB
     }
}
