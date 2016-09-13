/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.common;

import vng.paygate.domain.common.Constants;

/**
 *
 * @author trinm2
 */
public class ConstantBS extends Constants{
    public static final String SEPARATOR_EIB = "=";
    public static final String SEPARATOR_EIB1 = "|";
    public static final String DEFAULT_EXPIRED_DATE = "4912";
    public static final String DEFAULT_EXPIRED_DATE_FULL = "01-12-2049";
    public static final String BANK_EIB_TIPSS = "EIB_TIPSS";
    public static final String WS_EIB_CARD_PRECHECK = "CardPreCheck";
    public static final String WS_EIB_OTP_CHECK = "OTPCheck";
    
    public static final String ERROR_VERIFY_CARD_1 = "1";
    public static final String ERROR_VERIFY_CARD_2 = "2";
    public static final String ERROR_VERIFY_CARD_3 = "3";
    public static final String ERROR_VERIFY_CARD_4 = "4";
    public static final String ERROR_VERIFY_CARD_5 = "5";
    public static final String ERROR_VERIFY_CARD_6 = "6";
    public static final String ERROR_VERIFY_CARD_7 = "7";
    public static final String ERROR_VERIFY_CARD_10 = "10";
    public static final String ERROR_VERIFY_CARD_NEG_1 = "-1";
    public static final String ERROR_VERIFY_CARD_NEG_2 = "-2";
    public static final String ERROR_VERIFY_CARD_NEG_3 = "-3";
    public static final String ERROR_VERIFY_CARD_NEG_4 = "-4";
    
    public static final String ERROR_VERIFY_CARD_0000 = "0000";
    public static final String ERROR_VERIFY_CARD_0001 = "0001";
    public static final String ERROR_VERIFY_CARD_0002 = "0002";
    public static final String ERROR_VERIFY_CARD_0003 = "0003";
    public static final String ERROR_VERIFY_CARD_0004 = "0004";
    public static final String ERROR_VERIFY_CARD_0005 = "0005";
    public static final String ERROR_VERIFY_CARD_0006 = "0006";
    public static final String ERROR_VERIFY_CARD_0007 = "0007";
    public static final String ERROR_VERIFY_CARD_0010 = "0010";
    public static final String ERROR_VERIFY_CARD_NEG_0001 = "0011";
    public static final String ERROR_VERIFY_CARD_NEG_0002 = "0022";
    public static final String ERROR_VERIFY_CARD_NEG_0003 = "0033";
    public static final String ERROR_VERIFY_CARD_NEG_0004 = "0044";
    public static final int VERIFY_CARD_SUCCESS = 11;
    
    public static final String REQUEST_MESSAGE_SALE = "sale";
    public static final String REQUEST_MESSAGE_REVERSAL = "reversal";
    public static final String REQUEST_MESSAGE_VOID = "void";
    public static final String REQUEST_MESSAGE_INQUIRY = "inquiry";
    public static final String INVOKE_BANK_SUCCESS = "00";
    public static final String TRANX_NOT_FOUND = "000";
    public static final String ERROR_VERIFY_OTP_1 = "01";
    public static final String ERROR_VERIFY_OTP_2 = "02";
    public static final String ERROR_VERIFY_OTP_3 = "03";
    public static final String ERROR_VERIFY_OTP_4 = "04";
    public static final String ERROR_VERIFY_OTP_5 = "05";
    public static final String ERROR_VERIFY_OTP_7 = "07";
    public static final String ERROR_VERIFY_OTP_12 = "12";
    public static final String ERROR_VERIFY_OTP_13 = "13";
    public static final String ERROR_VERIFY_OTP_14 = "14";
    public static final String ERROR_VERIFY_OTP_20 = "20";
    public static final String ERROR_VERIFY_OTP_21 = "21";
    public static final String ERROR_VERIFY_OTP_25 = "25";
    public static final String ERROR_VERIFY_OTP_41 = "41";
    public static final String ERROR_VERIFY_OTP_43 = "43";
    public static final String ERROR_VERIFY_OTP_51 = "51";
    public static final String ERROR_VERIFY_OTP_54 = "54";
    public static final String ERROR_VERIFY_OTP_58 = "58";
    public static final String ERROR_VERIFY_OTP_61 = "61";
    public static final String ERROR_VERIFY_OTP_8 = "08";
    public static final String ERROR_VERIFY_OTP_91 = "91";
    public static final String ERROR_VERIFY_OTP_96 = "96";
    public static final String ERROR_INQUIRY_NOT_FOUND = "25";
    
    public static final String ERROR_VERIFY_888888= "888888";
    public static final String ERROR_VERIFY_999999= "999999";
    public static final String ERROR_VERIFY_7255= "7255";
    
    public static final String BANK_CODE_BANKNET= "BANKNET";
    public static final String TRANX_REF_PARAM = "vpc_MerchTxnRef";
    
    public static final String BANK_RESPONSE_PENDING = "20";
    public static final String ERROR_VERIFY_OTP_77777777 = "77777777";
    public static final String BANK_SIM_CODE = "BANKSIM";
    
}
