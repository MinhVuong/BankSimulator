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
 * @author CPU01661-local
 */
public class VietinBank extends BankAbstract{

    @Override
    public String VerifyCard(StringBuilder logMessage, BoBS boEIB, BoBaseBankNew boBank, IBankService bankService) {
        return "";
    }

    @Override
    public String checkAllowCardInfo(StringBuilder logMessage, String cardHolderName, String cardNo, BoBaseBankNew boBaseBank, String subBankCodeConfig, String flag) {
        appendMessage(logMessage, "checkAllowCardInfo");
        List<BoCardInfo> lstBoCardInfo = boBaseBank.getSubBanks().get(subBankCodeConfig).getBoCardInfo();
        if (lstBoCardInfo.size() > 0) {

            for (BoCardInfo boCardInfo : lstBoCardInfo) {

                if (boCardInfo.getCardHolderName().equals(cardHolderName.toUpperCase().trim())
                        && boCardInfo.getCardNo().equals(cardNo.trim())) {

                    // check error card
                    if (boCardInfo.getBankCode() != null) {
                        appendMessage(logMessage, "boCardInfo.getBankCode()" + boCardInfo.getBankCode());
                        if(boCardInfo.getBankCode().equals("0"))
                            return "1";
                        else if(boCardInfo.getBankCode().equals("1")){
                            return "0";
                        }else {
                            return boCardInfo.getBankCode();
                        }
                    }
                    appendMessage(logMessage, "return:" + Constants.RESPONSE_CODE_1);
                    return Constants.RESPONSE_CODE_1;
                }
            }
        }
        appendMessage(logMessage, "Can not find this card.");
        return ConstantBS.ERROR_VERIFY_CARD_10;

    }

    @Override
    public String VerifyOTP(StringBuilder logMessage, BoBS boEIB) {
        return "1";
    }
    
}
