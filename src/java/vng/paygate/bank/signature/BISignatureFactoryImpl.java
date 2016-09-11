package vng.paygate.bank.signature;

import vng.paygate.bank.jaxb.adapter.BoBIModuleConfigNew;
import vng.paygate.config.service.IConfigService;
import vng.paygate.domain.common.Constants;
import vng.paygate.domain.factory.signature.AbsSignature;
import vng.paygate.domain.factory.signature.ISignatureFactory;
import vng.paygate.domain.factory.signature.SynSignature;

/**
 *
 * @author trinm2
 */
public class BISignatureFactoryImpl implements ISignatureFactory {

    private IConfigService configService;

    @Override
    public AbsSignature getSignature(String moduleCode) {

        BoBIModuleConfigNew moduleConfig = (BoBIModuleConfigNew) configService.getModuleConfig();

        if (moduleCode == null || Constants.INTERNAL_MODULE.equals(moduleCode)) {
            SynSignature synSignature = new SynSignature();
            synSignature.setSecretKey(moduleConfig.getSecretKey());
            return synSignature;
        } else if(Constants.FE_MODULE.equals(moduleCode)){
            SynSignature synSignature = new SynSignature();
            synSignature.setSecretKey(moduleConfig.getFrontEndSecretKey());
            return synSignature;
        }

        return null;

    }

    public void setConfigService(IConfigService configService) {
        this.configService = configService;
    }
}
