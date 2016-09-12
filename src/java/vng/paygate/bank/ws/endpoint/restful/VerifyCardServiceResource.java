package vng.paygate.bank.ws.endpoint.restful;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.xml.ws.client.ClientTransportException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vng.paygate.bank.bo.BoBS;
import vng.paygate.bank.bo.BoCardInfo;
import vng.paygate.bank.bo.BoOrderNew;
import vng.paygate.bank.bo.BoProcessPaymentResponse;
import vng.paygate.bank.common.CommonService;
import vng.paygate.bank.common.ConstantBS;
import vng.paygate.bank.jaxb.adapter.BoBIModuleConfigNew;
import vng.paygate.bank.jaxb.adapter.BoBaseBank;
import vng.paygate.bank.jaxb.adapter.BoBaseBankNew;
import vng.paygate.bank.service.IBankService;
import vng.paygate.domain.bo.BoBaseResponse;
import vng.paygate.domain.bo.BoMiNotifyResponse;
import vng.paygate.domain.bo.BoOrder;
import vng.paygate.domain.common.Constants;
import vng.paygate.domain.common.util.*;
import vng.paygate.domain.exception.TechniqueException;
import vng.paygate.domain.factory.signature.AbsSignature;
import vng.paygate.domain.factory.signature.SynSignature;
import vng.paygate.domain.signature.ISignatureService;

/**
 *
 * @author trinm2
 * @since 123Pay @created on: Oct 24, 2012
 *
 */
@Component
@Path("/verifyCard")
@Produces(MediaType.APPLICATION_JSON)
public class VerifyCardServiceResource extends CommonService<BoProcessPaymentResponse> {

    @Autowired
    IBankService bankService;
    @Autowired
    private ISignatureService signatureService;
    @Context
    private HttpServletRequest request;
    private String contextPath;
    private static final String serviceName = "verifyCard";
    private StringBuilder logMessage;
    //VuongTM
//    static Logger logger = Logger.getLogger(VerifyCardServiceResource.class);
    
    
    @Context
    ServletContext servletContext; //you can specify in your method argument

    @POST
    @Consumes("application/json")
    public BoProcessPaymentResponse verifyCard(Map boRequest) {
        logMessage = new StringBuilder();
        BoProcessPaymentResponse boResponse;
        contextPath = request.getContextPath().replace("/", "");
        try {
            Gson gson = new Gson();
            logService.initLogMessage(DateUtils.getDate(new Date(), dateFormat), request.getLocalAddr(), Constants.BI + "-"
                    + contextPath.toUpperCase(), serviceName, "", request.getRemoteHost(), request.getRemoteAddr());
            
            if (boRequest == null) {
                appendMessage(logMessage, "INPUT IS NULL");
                boResponse = getResponse(Constants.ERROR_5000);
                writeErrorLog(boResponse, logMessage);
            }
            String orderNo = (String) boRequest.get("orderNo");
            String cardInfo = (String) boRequest.get("cardInfo");
            String clientIp = StringUtils.defaultIfEmpty((String) boRequest.get("clientIp"), "");
            String checksum = (String) boRequest.remove("checksum");
            appendParams(logMessage, orderNo, cardInfo, clientIp, checksum);
            BoBS boEIB = new BoBS();
            boEIB.setClientIp(clientIp);

            appendMessage(logMessage, "validateParams");
            //validate required paramters from MI
            String responseCode = validate(orderNo, cardInfo, checksum);
            boResponse = getResponse(responseCode);
            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
                return writeErrorLog(boResponse, logMessage);
            }
            appendParams(logMessage, request.getRemoteAddr());
            appendMessage(logMessage, "checkAllowIP");
            //responseCode = checkAllowIP(Constants.INTERNAL_MODULE, request.getRemoteAddr());
            //boResponse = getResponse(responseCode);
            //appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            //if (!Constants.RESPONSE_CODE_1.equals(responseCode)) {
            //    return writeErrorLog(boResponse, logMessage);
            //}

            appendMessage(logMessage, "getSignature");
            AbsSignature aSignature = signatureService.getSignature(Constants.INTERNAL_MODULE);
            if (aSignature == null) {
                boResponse = getResponse(Constants.ERROR_5000);
                appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
                return writeErrorLog(boResponse, logMessage);
            }
            appendParams(logMessage, Constants.RESPONSE_CODE_1); //getSignature succes

            String rawData = "";
            Map<String, String> treeMap = new TreeMap<String, String>();
            treeMap.putAll(boRequest);
            for (String item : treeMap.keySet()) {
                if (!StringUtils.isEmpty(item)) {
                    rawData += treeMap.get(item);
                }
            }
//            appendParams(logMessage, rawData);
            appendMessage(logMessage, "verifySignature");
            //Just pass raw data to verify. ISignature try to load key by its self
            BoBaseResponse baseResponse = aSignature.verifySignature(ChecksumGeneration.ALGORITHM_SHA1, rawData, checksum);
            boResponse = getResponse(baseResponse.getDetailResponseCode());
            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
                //return list of response code,123Pay transaction id, url input card
                return writeErrorLog(boResponse, logMessage);
            }
            //query order from database
            appendMessage(logMessage, "loadOrderVerifyCard");
            BoOrderNew boOrder = bankService.loadOrderVerifyCard(orderNo);
            System.out.println("BoOrder: " + gson.toJson(boOrder));
            String subBankCodeConfig = boOrder.getSubbankCode();
            System.out.println("SubBankCode: " + subBankCodeConfig);
            boResponse = getResponse(boOrder.getResponseCode());
            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
//                return writeErrorLog(boResponse, logMessage);
                logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(), logMessage.toString());
                return boResponse;
            }
            //load xml config
            appendParams(logMessage, contextPath.toLowerCase());
            appendMessage(logMessage, "loadConfig");
            BoBaseBankNew boBank = configService.getModuleConfig().getBankCodeMap().get(boOrder.getBankCode());
            if (boBank == null) {
                boBank = configService.getModuleConfig().getBankCodeMap().get("EIB");
            }
            System.out.println("BoBaseBankNew: " + gson.toJson(boBank));
            //Verify RC4 encryption signature
            responseCode = verifyEncryptedMessage(cardInfo, boEIB, boBank);
            boResponse = getResponse(responseCode);
            appendMessage(logMessage, "verifyEncryptedMessage");
            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            if (!Constants.RESPONSE_CODE_1.equals(responseCode)) {
                return writeErrorLog(boResponse, logMessage);
            }
            appendParams(logMessage, Constants.RESPONSE_CODE_1);

//            //TODO
////            if (boOrder.getOrderStatus() != null && boOrder.getOrderStatus().intValue() != Constants.ORDER_STATUS_NEW.intValue()) {
////                log.info("Can't verify card. Invalid order status : {}", boOrder.getOrderStatus());
////                return this.getReturnMessage(Constants.ERROR_2109, orderNo, "", "Can't verify card. Invalid order status");
////            }
            //copy boOrder to boEIB
            copyBoOrderToBoEIB(boOrder, boEIB);
            //test
//            BoOrder boOrder = new BoOrder();
//            boEIB.setAmount(50000);
//            boEIB.setTransactionIdSuffix("123P1210080000432".substring(4));
//            boEIB.setTransactionId("123P1210080000432");
//            boEIB.setMerchantCode("ZXU");
            //end test

            //call cardPreCheck
            appendMessage(logMessage, "processCardInfo", boEIB.getCardNo());

            responseCode = processCardInfo(boEIB, boBank);
            appendMessage(logMessage, "processCardInfo", responseCode);
            boResponse = getResponse(responseCode);
            System.out.println("boResponse after processCardInfo: " + gson.toJson(boResponse));
            appendMessage(logMessage, "getResponse", boResponse.getDetailResponseCode());

            //check input incorrect card info 3 times 
            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())
                    && boOrder.getOrderNoSuffix() != null
                    && boOrder.getOrderNoSuffix().intValue() >= (Integer.parseInt(boBank.getMaxInput()) - 1)) {
                appendParams(logMessage, Constants.ERROR_7221, "Can't input incorrect card " + boBank.getMaxInput() + " times");
                boResponse = getResponse(Constants.ERROR_7221);
            }

            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
                //allow re-input card info
                //7232,7231,7211,7212,7201,7300,-999999
                if (boEIB.getIsSuccess() == -2 && (Constants.ERROR_7232.equals(boResponse.getDetailResponseCode())
                        || Constants.ERROR_7231.equals(boResponse.getDetailResponseCode())
                        || Constants.ERROR_7211.equals(boResponse.getDetailResponseCode())
                        || Constants.ERROR_7212.equals(boResponse.getDetailResponseCode())
                        || Constants.ERROR_7201.equals(boResponse.getDetailResponseCode())
                        || Constants.ERROR_7300.equals(boResponse.getDetailResponseCode())
                        || Constants.RESPONSE_TIME_OUT.equals(boResponse.getDetailResponseCode()))) {
                    appendParams(logMessage, boResponse.getDetailResponseCode(), "Invalid card info/Allow re-input card info");
                    logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(), logMessage.toString());
                    return boResponse;
                } //7302,7007,7100,7221,-888888 => fail => notify merchant
                else if (Constants.ERROR_7302.equals(boResponse.getDetailResponseCode())
                        || Constants.ERROR_7007.equals(boResponse.getDetailResponseCode())
                        || Constants.ERROR_7100.equals(boResponse.getDetailResponseCode())
                        || Constants.ERROR_7221.equals(boResponse.getDetailResponseCode())
                        || Constants.INVALID_TRANX_RESULT.equals(boResponse.getDetailResponseCode())) {
                    // return error code for show match message => notify merchant => return payport url
                    notifyMerchant(boEIB, boResponse, boOrder);

                    boResponse = getResponse(boResponse.getDetailResponseCode());
                    //build response result
                    buildResponse(boResponse, boOrder);

                    //build payport URL
                    String payportURL = boBank.getPayportURL() + "?" + ConstantBS.TRANX_REF_PARAM + "=" + boOrder.getOrderNo();
                    appendMessage(logMessage, "RedirectURL:" + payportURL);
                    boResponse.setRedirectURL(payportURL);
                    //update order status
                    appendMessage(logMessage, "Result");
                    appendParams(logMessage, boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(),
                            boResponse.getOrderNo(), boResponse.getMerchantTransactionId(),
                            Integer.toString(boResponse.getOrderStatus()),
                            Integer.toString(boResponse.getNotifyStatus()),
                            Integer.toString(boResponse.getTotalAmount()),
                            Integer.toString(boResponse.getOpAmount()), boResponse.getBankCode(),
                            boResponse.getMerchantCode(), boResponse.getBankResponseCode(),
                            boResponse.getDescription(),
                            boResponse.getRedirectURL());
                    logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(),
                            boResponse.getDetailResponseCode(), logMessage.toString());
                    return boResponse;

                } else {
                    //build payport URL
                    String payportURL = boBank.getPayportURL() + "?" + ConstantBS.TRANX_REF_PARAM + "=" + boOrder.getOrderNo();
                    appendMessage(logMessage, "RedirectURL:" + payportURL);
                    boResponse.setRedirectURL(payportURL);
                    appendParams(logMessage, boResponse.getDetailResponseCode(), "Verify Card fail / Internal Error",
                            boResponse.getRedirectURL());
                    return writeErrorLog(boResponse, logMessage);
                }

            }

            boResponse.setOrderNo(boEIB.getTransactionId());
//            boResponse.setDetailResponseCode(Constants.RESPONSE_CODE_1);
//            boResponse.setGroupResponseCode(Constants.RESPONSE_CODE_1);
            if (boEIB.getOrderStatus() == 20) {
                boResponse.setAuthSite("BANK");
                boResponse.setVerifyOtpURL("https://sandbox2.123pay.vn/otp.php?id=" + boResponse.getOrderNo());
            }
            if (boEIB.getOrderStatus() != 20 && boEIB.getOrderStatus() != 11) {
                boResponse.setDetailResponseCode(String.valueOf(boEIB.getOrderStatus()));
            }
            String checksumReturn = buildChecksum(boResponse);
            if (StringUtils.isEmpty(checksumReturn)) {
                boResponse = getResponse(Constants.ERROR_5000);
                appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
                return writeErrorLog(boResponse, logMessage);
            }
//            appendParams(logMessage, Constants.RESPONSE_CODE_1, checksumReturn);
            boResponse.setChecksum(checksumReturn);

            appendParams(logMessage, boResponse.getDetailResponseCode(), "Verify card success");
            logService.log(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(), logMessage.toString());
            return boResponse;
        } catch (Throwable exception) {
            exception.printStackTrace();
            boResponse = new BoProcessPaymentResponse();
            boResponse.setDetailResponseCode(Constants.ERROR_5000);
            boResponse.setDetailDescription("Unhandle Excpetion");
            logService.logException(DateUtils.getDate(new Date(), dateFormat), Constants.ERROR_5000, Constants.ERROR_5001, logMessage.toString(), exception);
            return boResponse;
        }
    }

    private void notifyMerchant(BoBS boEIB, BoProcessPaymentResponse boResponse, BoOrderNew boOrder) throws TechniqueException {
        String rawData;
        /*
         * call notify to merchant to inform status of order
         */
        rawData = boEIB.getTransactionId();
        SynSignature signature = signatureService.getSignature(Constants.INTERNAL_MODULE);
        String checksumMerchant = signature.createSignature(ChecksumGeneration.ALGORITHM_SHA1, rawData);
        appendParams(logMessage, boEIB.getMiNotifyUrl(), boEIB.getTransactionId(), checksumMerchant);
        appendMessage(logMessage, "invokeNotifyMerchant");
        // check status of notify merchant and order status
        BoMiNotifyResponse boMiNotify;
        try {
            boMiNotify = invokeNotifyMerchant(boEIB.getMiNotifyUrl(), boEIB.getTransactionId(), checksumMerchant);
            boOrder.setNotifyStatus(0);
            appendParams(logMessage, boMiNotify.getGroupResponseCode(), boMiNotify.getDetailResponseCode());
        } catch (Exception ex) {
            ex.printStackTrace();
            boResponse.setGroupResponseCode(Constants.ERROR_5000);
            boResponse.setDetailResponseCode(Constants.ERROR_5001);
            if (ex instanceof ClientTransportException || ex instanceof ConnectException
                    || ex instanceof RemoteException || ex instanceof SocketException
                    || ex instanceof IOException || ex instanceof ClientHandlerException) {
                // connection timeout
                appendParams(logMessage, Constants.RESPONSE_TIME_OUT);
            } else {
                // exception
                appendParams(logMessage, Constants.ERROR_5000);
            }
        }
    }

    private String validate(String orderNo, String cardInfo, String checksum) {
        if (StringUtils.isEmpty(orderNo)
                || StringUtils.isEmpty(cardInfo)
                || StringUtils.isEmpty(checksum)) {
            return Constants.ERROR_6101;
        }

        if (orderNo.length() > 17) {
            return Constants.ERROR_6100;
        }

        return Constants.RESPONSE_CODE_1;
    }

    private String processCardInfo(BoBS boEIB, BoBaseBankNew boBank) {
        try {
            appendMessage(logMessage, "processCardInfo");
            if (StringUtils.isEmpty(boEIB.getCardNo()) || boEIB.getCardNo().length() != 16) {
                return Constants.ERROR_6100;
            }
            String cardNo = boEIB.getCardNo();
            String cardHolderName = boEIB.getCardHolderName();
            String checkPrefix = boEIB.getCardNo().substring(10, 16);
            String checkPrefixCardNo = Constants.RESPONSE_CODE_1;
            if (!checkPrefix.equals(ConstantBS.ERROR_VERIFY_888888) && !checkPrefix.equals(ConstantBS.ERROR_VERIFY_999999)) {
                appendMessage(logMessage, "checkPrefixCardNo");
                checkPrefixCardNo = checkAllowCardInfo(cardHolderName, cardNo, boBank, boEIB.getBanksimCode(), "2");
            }
            Date expiredDate = boEIB.getExpireDate();
            String cashHash = boEIB.getCardHash();
            removeCardInfo(boEIB);

            appendMessage(logMessage, "checkAllowCardInfo");
            String checkAllowCardInfo = checkAllowCardInfo(cardHolderName, cardNo, boBank, boEIB.getBanksimCode(), "1");
            appendMessage(logMessage, checkAllowCardInfo);
            if (checkAllowCardInfo.equals("0")) {
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
            String bankCode = boEIB.getBankCode();
            int iOrderStatus = 11;
            if(boEIB.getIsSuccess() == 1){
                if(boBank.getSubBanks().get(boEIB.getBanksimCode()).getIsInputOTP().equals("1")){
                    iOrderStatus = 20;
                }
            }else{
                try {
                    iOrderStatus = Integer.parseInt(boEIB.getCardNo4Last());
                } catch (Exception ex) {
                    iOrderStatus = 6100;
                }
            }
            boEIB.setiOrderStatus(iOrderStatus);

            appendMessage(logMessage, "boEIB.getiOrderStatus:" + boEIB.getiOrderStatus() + "Last:" + boEIB.getCardNo4Last());
            appendMessage(logMessage, "boEIB.getIsSuccess:" + boEIB.getIsSuccess());
            // Call BankService SP_BI_BANKSIM_VERIFY_CARD
            boEIB.setBankCode(boEIB.getBanksimCode());
            bankService.verifyCard(boEIB);
            boEIB.setBankCode(bankCode);
            
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
        } catch (Exception ex) {
            ex.printStackTrace();
            appendMessage(logMessage, "processCardInfo");
            appendParams(logMessage, ConstantBS.ERROR_5000, ex.toString());
            return Constants.ERROR_5000;
        }
    }

    private void removeCardInfo(BoBS boEIB) {
        appendMessage(logMessage, "removeCardInfo");
        boEIB.setCardNo(null);
        boEIB.setCardHolderName(null);
        boEIB.setExpireDate(null);
        boEIB.setCardHash(null);
        appendMessage(logMessage, Constants.RESPONSE_CODE_1);
    }

    private void copyBoOrderToBoEIB(BoOrderNew boOrder, BoBS boEIB) {
//        boEIB.setAmount(boOrder.getTotalAmount());
        boEIB.setAmount(boOrder.getOpAmount());
        //Cause TIPSS compare int, not string so we remove 4 first characters in 123Pay transaction id.
        String transId = boOrder.getOrderNo().substring(4);
//        String transId = boOrder.getOrderNo();
        //for test
        if (boOrder.getOrderNoSuffix() != null && boOrder.getOrderNoSuffix().intValue() > 0) {
            transId = transId + "" + boOrder.getOrderNoSuffix();
        }
        boEIB.setTransactionIdSuffix(transId);
        boEIB.setTransactionId(boOrder.getOrderNo());
        boEIB.setMerchantCode(boOrder.getMerchantCode());
        ///
        boEIB.setBankCode(boOrder.getBankCode());
        boEIB.setCustomerId(null);
        String subBankCode = boOrder.getSubbankCode();
        subBankCode = subBankCode.substring(4, subBankCode.length());
        boEIB.setBanksimCode(subBankCode);
    }

    private BoMiNotifyResponse invokeNotifyMerchant(String notifyUrl, String orderNo, String dataSign) throws Exception {
        BoMiNotifyResponse bo = new BoMiNotifyResponse(Constants.RESPONSE_CODE_1, Constants.RESPONSE_CODE_1);
        Map m = new HashMap();
        m.put("orderNo", orderNo);
        m.put("checksum", dataSign);
//        bo = HttpUtils.invokeRestfulUrl(notifyUrl, m, bo.getClass());
        CustomRestClient client = new CustomRestClient();
        client.setWebResource(notifyUrl);
        bo = client.post(m, bo.getClass());

        return bo;
    }

    private void buildResponse(BoProcessPaymentResponse boResponse, BoOrderNew boOrder) {
        boResponse.setOrderNo(boOrder.getOrderNo());
        boResponse.setOrderStatus(boOrder.getOrderStatus());
        boResponse.setMerchantTransactionId(boOrder.getMerchantTransactionId());
        boResponse.setTotalAmount(boOrder.getTotalAmount());
        boResponse.setOpAmount(boOrder.getOpAmount());
        boResponse.setBankCode(boOrder.getBankCode());
        boResponse.setMerchantCode(boOrder.getMerchantCode());
        boResponse.setBankResponseCode(boOrder.getBankResponseCode());
        boResponse.setDescription(boOrder.getDescription());
        boResponse.setNotifyStatus(boOrder.getNotifyStatus() == null ? 0 : boOrder.getNotifyStatus());
    }

    private String buildChecksum(BoProcessPaymentResponse boResponse) throws TechniqueException {
        ObjectMapper m = new ObjectMapper();
        Map<String, String> props = m.convertValue(boResponse, TreeMap.class);
        props.remove("checksum");
        String rawData = "";
        for (String item : props.keySet()) {
            rawData += props.get(item);
        }

//        appendParams(logMessage, rawData);
        appendMessage(logMessage, "createSignature");
        //Just pass raw data to verify. ISignature try to load key by its self
        AbsSignature signature = signatureService.getSignature(Constants.INTERNAL_MODULE);
        String checksum = signature.createSignature(ChecksumGeneration.ALGORITHM_SHA1, rawData);
        return checksum;
    }

    private String verifyEncryptedMessage(String encryptData, BoBS boEIB, BoBaseBankNew boBank) throws TechniqueException {
        String rc4SecretKey = boBank.getRc4SecretKey();
//        appendMessage(logMessage, "getRc4SecretKey", rc4SecretKey);
        SecretKey secretKey = RC4.generateKey(rc4SecretKey);
        String cardInfo;

        String params[];
        if (secretKey != null) {
            cardInfo = RC4.decrypt(encryptData, secretKey);
            appendMessage(logMessage, "Card information: " + cardInfo);
            if (cardInfo == null || cardInfo.equals("")) {
                appendMessage(logMessage, "Card information invalid: " + cardInfo);
                return Constants.ERROR_6007;
            }
            if (cardInfo.contains("=")) {
                params = StringUtils.split(cardInfo, ConstantBS.SEPARATOR_EIB);
            } else {
                params = StringUtils.split(cardInfo, ConstantBS.SEPARATOR_EIB1);
            }
            if (params != null) {
                String cardNo = "";
                String cardHolderName = "";
                if (params.length == 2 || params.length == 3) {
                    cardNo = params[0];
                    cardHolderName = params[1];
                } else if (params.length == 4) {
                    cardNo = params[0];
                    cardHolderName = params[2];
                }
                if (cardNo.equals("") || cardHolderName.equals("")) {
                    appendMessage(logMessage, "Invalid card no/card holder name");
                    return Constants.ERROR_6007;
                }

                if (!StringUtils.isNumeric(cardNo)) {
                    appendMessage(logMessage, "Card no must be numeric");
                    return Constants.ERROR_6007;
                }

                if (!StringUtils.isAlphanumericSpace(cardHolderName) || !StringUtils.isAsciiPrintable(cardHolderName)) {
                    appendMessage(logMessage, "Card holder name must be ascii alphabet numeric space");
                    return Constants.ERROR_6007;
                }
                cardNo = cardNo.trim();
                boEIB.setCardNo(cardNo);
                boEIB.setCardNo6First(cardNo.substring(0, 6));
                boEIB.setCardNo4Last(cardNo.substring(cardNo.length() - 4, cardNo.length()));
                boEIB.setCardHash(ChecksumGeneration.generateChecksum(ChecksumGeneration.ALGORITHM_SHA1, cardNo));
                boEIB.setCardHolderName(cardHolderName.trim());
                boEIB.setExpireDate(DateUtils.getDate(ConstantBS.DEFAULT_EXPIRED_DATE_FULL));

                return Constants.RESPONSE_CODE_1;
            } else {
                appendMessage(logMessage, "Number of parameter in encrypt message wrong : " + params.length);
                return Constants.ERROR_6007;
            }

        }
        return Constants.ERROR_5000;
    }

    /**
     * @author : BangLA
     * @param cardHolderName
     * @param cardNo
     * @param boBaseBank
     * @param flag
     * @return
     * @since 2016.06.13
     */
    public String checkAllowCardInfo(String cardHolderName, String cardNo, BoBaseBankNew boBaseBank, String subBankCodeConfig, String flag) {
        appendMessage(logMessage, "checkAllowCardInfo");
        List<BoCardInfo> lstBoCardInfo = boBaseBank.getSubBanks().get(subBankCodeConfig).getBoCardInfo();
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

    public static void main(String[] args) {
        String card = "phan thi kim ngan";
        String cardNo = "9704310000310000 ";
//        if ("PHAN THI KIM NGAN".equals(card.toUpperCase().trim())) {
//            System.out.println("dung ten");
//            if ("9704310000310000".equals(cardNo.trim())) {
//                System.out.println("dung card no");
//            } else {
//                System.out.println("k dung card no");
//            }
//        } else {
//            System.out.println("k dung ten");
//        }

        if ("PHAN THI KIM NGAN".equals(card.toUpperCase().trim())
                && "9704310000310000".equals(cardNo.trim())) {
            System.out.println("ok");
        } else {
            System.out.println("no");
        }

    }
}
