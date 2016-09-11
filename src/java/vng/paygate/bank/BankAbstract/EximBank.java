/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.BankAbstract;

import java.util.Date;
import org.apache.commons.lang.StringUtils;
import vng.paygate.bank.bo.BoBS;
import vng.paygate.bank.common.ConstantBS;
import vng.paygate.bank.jaxb.adapter.BoBaseBankNew;
import vng.paygate.bank.service.IBankService;
import vng.paygate.domain.common.Constants;

/**
 *
 * @author Kuti
 */
public class EximBank extends BankAbstract{

    @Override
    public String VerifyCard(StringBuilder logMessage, BoBS boEIB, BoBaseBankNew boBank, IBankService bankService) {
        try {
            appendMessage(logMessage, "processCardInfo");
            if (StringUtils.isEmpty(boEIB.getCardNo()) || boEIB.getCardNo().length() != 16) {
                return Constants.ERROR_6100;
            }
            String cardNo = boEIB.getCardNo();
            String cardHolderName = boEIB.getCardHolderName();
            String checkPrefix = boEIB.getCardNo().substring(10, 16);
            String checkPrefixCardNo = Constants.RESPONSE_CODE_1;
            if (checkPrefix.equals(ConstantBS.ERROR_VERIFY_888888) || checkPrefix.equals(ConstantBS.ERROR_VERIFY_999999)) {
                appendMessage(logMessage, "checkPrefixCardNo");
                return checkPrefix;
            }
            Date expiredDate = boEIB.getExpireDate();
            String cashHash = boEIB.getCardHash();
            removeCardInfo(logMessage, boEIB);
            appendMessage(logMessage, "checkAllowCardInfo");
            String checkAllowCardInfo = checkAllowCardInfo(logMessage, cardHolderName, cardNo, boBank, "1");
            appendMessage(logMessage, checkAllowCardInfo);
            if (Constants.RESPONSE_CODE_1.equals(checkAllowCardInfo)) {
                boEIB.setBankResponseCode(Constants.RESPONSE_CODE_1);
                boEIB.setIsSuccess(1);
                appendMessage(logMessage, Constants.RESPONSE_CODE_1);
                boEIB.setCardNo(cardNo);
                boEIB.setCardHolderName(cardHolderName);
                boEIB.setExpireDate(expiredDate);
                boEIB.setCardHash(cashHash);
            } else {
                if (!ConstantBS.ERROR_VERIFY_CARD_10.equals(checkAllowCardInfo)) {
                    boEIB.setBankResponseCode(checkAllowCardInfo);
                } else {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_CARD_10);
                }
                appendMessage(logMessage, "set -2 line 410");
                boEIB.setIsSuccess(-2);
            }
            appendMessage(logMessage, "SP_BI_BANKSIM_VERIFY_CARD");
            int iOrderStatus = 11;
            if (boEIB.getCardNo6First().startsWith("686868")
                    || // VCB
                    boEIB.getCardNo6First().startsWith("970436")
                    || // VCB
                    boEIB.getCardNo6First().startsWith("620160")
                    || // Vietin
                    boEIB.getCardNo6First().startsWith("970415")
                    || // Vietin
                    boEIB.getCardNo6First().startsWith("1792")
                    || // Dong A
                    boEIB.getCardNo6First().startsWith("970406")
                    || // Dong A
                    boEIB.getCardNo6First().startsWith("970407")
                    || // Techcombank
                    boEIB.getCardNo6First().startsWith("889988")
                    || // Techcombank
                    boEIB.getCardNo6First().startsWith("888899")
                    || // Techcombank
                    boEIB.getCardNo6First().startsWith("970441")
                    || // VIB
                    boEIB.getCardNo6First().startsWith("180909")
                    || // VIB
                    boEIB.getCardNo6First().startsWith("180906")
                    || // VIB
                    boEIB.getCardNo6First().startsWith("970420")
                    || // HDBank
                    boEIB.getCardNo6First().startsWith("970437")
                    || // HDBank
                    boEIB.getCardNo6First().startsWith("970423")
                    || // Tienphong
                    boEIB.getCardNo6First().startsWith("126688")
                    || // SHB
                    boEIB.getCardNo6First().startsWith("970443")
                    || // SHB
                    boEIB.getCardNo6First().startsWith("16688")
                    || // VietA
                    boEIB.getCardNo6First().startsWith("970427")
                    || // VietA
                    boEIB.getCardNo6First().startsWith("193939")
                    || // MB
                    boEIB.getCardNo6First().startsWith("970422")
                    || // MB
                    boEIB.getCardNo6First().startsWith("970414")
                    || // OCEAN
                    boEIB.getCardNo6First().startsWith("970432")
                    || // VPBANK
                    boEIB.getCardNo6First().startsWith("981957") // VPBANK
                    ) {
                iOrderStatus = 20;

            }
            if (!"1111".equals(boEIB.getCardNo4Last())) {
                try {
                    iOrderStatus = Integer.parseInt(boEIB.getCardNo4Last());
                } catch (Exception ex) {
                    iOrderStatus = 6100;
                }
            }boEIB.setiOrderStatus(iOrderStatus);

            appendMessage(logMessage, "boEIB.getiOrderStatus:" + boEIB.getiOrderStatus() + "Last:" + boEIB.getCardNo4Last());
            appendMessage(logMessage, "boEIB.getIsSuccess:" + boEIB.getIsSuccess());
            // Call BankService SP_BI_BANKSIM_VERIFY_CARD
            bankService.verifyCard(boEIB);

            if (boEIB.getResponseCode() != null && boEIB.getResponseCode().intValue() == Integer.valueOf(Constants.RESPONSE_CODE_1)) {
                appendParams(logMessage, "getResponseCode: " + boEIB.getResponseCode().intValue(), "getOrderStatus:" + boEIB.getOrderStatus().intValue(), boEIB.getMiNotifyUrl());
                String responseCode = boEIB.getResponseCode().toString();

                appendMessage(logMessage, "processCardInfo");
                appendParams(logMessage, responseCode);
                return responseCode;
            }
            appendParams(logMessage, boEIB.getResponseCode() == null ? "null" : "" + boEIB.getResponseCode());

            appendMessage(logMessage, "processCardInfo");
            appendMessage(logMessage, ConstantBS.ERROR_5000);
            return Constants.ERROR_5000;
        }catch (Exception ex) {
            ex.printStackTrace();
            appendMessage(logMessage, "processCardInfo");
            appendParams(logMessage, ConstantBS.ERROR_5000, ex.toString());
            return Constants.ERROR_5000;
        }
    }
    
}
