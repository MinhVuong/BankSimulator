/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.BankAbstract;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import vng.paygate.bank.bo.BoBS;
import vng.paygate.bank.bo.BoCardInfo;
import vng.paygate.bank.common.ConstantBS;
import vng.paygate.bank.common.MyConstants;
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
        if (StringUtils.isEmpty(boEIB.getOtp()) || boEIB.getOtp().length() != 8) {
            return Constants.ERROR_5000;
        }
        if (boEIB.getOtp().equals(ConstantBS.ERROR_VERIFY_OTP_77777777)) {
            return boEIB.getOtp();
        }
        String otp = boEIB.getOtp().substring(2);
        System.out.println("OTP substring(2): " + otp);
        String verifyResult = null;
        String result = null;
        if (otp.equals(ConstantBS.ERROR_VERIFY_888888) || otp.equals(ConstantBS.ERROR_VERIFY_999999)) {
            verifyResult = "-" + otp;
        } else {
            if (boEIB.getOtp().substring(4, 6).equals(ConstantBS.INVOKE_BANK_SUCCESS)) {
                verifyResult = Constants.RESPONSE_CODE_1;
                result = boEIB.getOtp().substring(6);
            } else {
                verifyResult = Constants.RESPONSE_CODE_1;
                result = ConstantBS.ERROR_VERIFY_OTP_8;
            }
        }
        System.out.println("OTP substring(4, 6): " + boEIB.getOtp().substring(4, 6));
        System.out.println("verifyResult: " + verifyResult);
        System.out.println("result: " + result);

        appendMessage(logMessage, verifyResult, result, otp);
        if (verifyResult == null) {
            return Constants.ERROR_5000;
        } else {
            if (Constants.INVALID_TRANX_RESULT.equalsIgnoreCase(verifyResult)) {
                boEIB.setBankResponseCode(Constants.INVALID_TRANX_RESULT);
                boEIB.setIsSuccess(-1);
                return Constants.ERROR_5000;
            } else if (Constants.RESPONSE_TIME_OUT.equals(verifyResult)) { // If can't invoke EIB, do not invoke reversal
                boEIB.setBankResponseCode(Constants.INVALID_TRANX_RESULT);
                return Constants.INVALID_TRANX_RESULT;

            } else if (Constants.RESPONSE_CODE_1.equals(verifyResult)) {
                if (result == null) {
                    boEIB.setBankResponseCode(Constants.INVALID_TRANX_RESULT);
                    boEIB.setIsSuccess(-1);
                    return Constants.ERROR_5000;
                }
                // verify OTP fail
                if (result.equals(ConstantBS.ERROR_VERIFY_OTP_1)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_1);
                    return MyConstants.ERROR_7230;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_2)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_2);
                    return Constants.ERROR_7231;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_3)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_3);
                    return Constants.ERROR_7302;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_4)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_4);
                    return MyConstants.ERROR_7255;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_5)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_5);
                    return Constants.ERROR_7211;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_7)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_7);
                    return Constants.ERROR_7302;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_8)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_8);
                    return Constants.ERROR_7222;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_12)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_12);
                    return Constants.ERROR_7007;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_13)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_13);
                    return Constants.ERROR_7302;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_14)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_14);
                    return Constants.ERROR_7232;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_41)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_41);
                    return Constants.ERROR_7234;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_43)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_43);
                    return Constants.ERROR_7235;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_51)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_51);
                    return Constants.ERROR_7201;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_54)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_54);
                    return Constants.ERROR_7233;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_61)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_61);
                    return Constants.ERROR_7202;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_91)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_91);
                    boEIB.setIsSuccess(-1);
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_96)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_96);
                    return Constants.ERROR_7300;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_20)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_20);
                    return Constants.ERROR_7213;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_21)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_21);
                    return Constants.ERROR_7213;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_58)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_58);
                    return Constants.ERROR_7213;
                }else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_25)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_25);
                    return Constants.ERROR_7300;
                } else if (result.equals(ConstantBS.INVOKE_BANK_SUCCESS)) {
                    boEIB.setBankResponseCode(ConstantBS.INVOKE_BANK_SUCCESS);
                    boEIB.setIsSuccess(1);
                } else {
                    boEIB.setBankResponseCode(result);          // OTP khong co thi set thanh sai OTP
                    boEIB.setIsSuccess(-1);
                    return Constants.ERROR_7222;
                }
            }
        }
        return Constants.RESPONSE_CODE_1;
    }
    
}
