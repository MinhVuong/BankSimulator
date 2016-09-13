/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.BankAbstract;

import java.util.List;
import vng.paygate.bank.bo.BoBS;
import vng.paygate.bank.bo.BoCardInfo;
import vng.paygate.bank.common.ConstantBS;
import vng.paygate.bank.jaxb.adapter.BoBaseBankNew;
import vng.paygate.bank.service.IBankService;
import vng.paygate.domain.common.Constants;

/**
 *
 * @author Kuti
 */
public abstract class BankAbstract {
    public abstract String VerifyCard(StringBuilder logMessage, BoBS boEIB, BoBaseBankNew boBank, IBankService bankService);
    public abstract String checkAllowCardInfo(StringBuilder logMessage, String cardHolderName, String cardNo, BoBaseBankNew boBaseBank, String subBankCodeConfig, String flag);
    public abstract String VerifyOTP(StringBuilder logMessage, BoBS boEIB);
    
    public void appendMessage(StringBuilder logMessage, String... messages) {
        for (String mess : messages) {
            logMessage.append(Constants.LOG_SEPARATOR).append(mess);
        }
    }
    
    public void appendParams(StringBuilder logMessage, String... messages) {
        if (messages == null) {
            return;
        }
        logMessage.append(Constants.LOG_SEPARATOR).append(messages[0]);

        for (int i = 1; i < messages.length; i++) {
            String mess = messages[i];
            logMessage.append(Constants.PARAMS_SEPARATOR).append(mess);
        }
    }
    
    public String checkAllowCardInfo(StringBuilder logMessage, String cardHolderName, String cardNo, BoBaseBankNew boBaseBank, String flag) {
        appendMessage(logMessage, "checkAllowCardInfo");
        List<BoCardInfo> lstBoCardInfo = boBaseBank.getSubBanks().get("EIB").getBoCardInfo();
        if (lstBoCardInfo.size() > 0) {

            for (BoCardInfo boCardInfo : lstBoCardInfo) {

                if (boCardInfo.getCardHolderName().equals(cardHolderName.toUpperCase().trim())
                        && boCardInfo.getCardNo().equals(cardNo.trim())) {

                    // check error card
                    if (boCardInfo.getBankCode() != null) {
                        appendMessage(logMessage, "boCardInfo.getBankCode()" + boCardInfo.getBankCode());

                        return boCardInfo.getBankCode();
                    }
                    appendMessage(logMessage, "return:" + Constants.RESPONSE_CODE_1);
                    return Constants.RESPONSE_CODE_1;
                }
            }
        }
        appendMessage(logMessage, "Can not find this card.");
        return ConstantBS.ERROR_VERIFY_CARD_10;
    }
    
    public void removeCardInfo(StringBuilder logMessage, BoBS boEIB) {
        appendMessage(logMessage, "removeCardInfo");
        boEIB.setCardNo(null);
        boEIB.setCardHolderName(null);
        boEIB.setExpireDate(null);
        boEIB.setCardHash(null);
        appendMessage(logMessage, Constants.RESPONSE_CODE_1);
    }
}
