package vng.paygate.bank.ws.endpoint.restful;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.xml.ws.client.ClientTransportException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vng.paygate.bank.BankAbstract.BankAbstract;
import vng.paygate.bank.BankAbstract.BankFactory;
import vng.paygate.bank.bo.*;
import vng.paygate.bank.common.CommonService;
import vng.paygate.bank.common.ConstantBS;
import vng.paygate.bank.common.MyConstants;
import vng.paygate.bank.jaxb.adapter.BoBaseBank;
import vng.paygate.bank.jaxb.adapter.BoBaseBankNew;
import vng.paygate.bank.service.IBankService;
import vng.paygate.domain.bo.BoBaseResponse;
import vng.paygate.domain.bo.BoMiNotifyResponse;
import vng.paygate.domain.bo.BoNotifyQueryBOrder;
import vng.paygate.domain.bo.BoOrder;
import vng.paygate.domain.common.Constants;
import vng.paygate.domain.common.util.ChecksumGeneration;
import vng.paygate.domain.common.util.CustomRestClient;
import vng.paygate.domain.common.util.DateUtils;
import vng.paygate.domain.exception.TechniqueException;
import vng.paygate.domain.factory.signature.AbsSignature;
import vng.paygate.domain.signature.ISignatureService;

/**
 *
 * @author trinm2
 * @since 123Pay @created on: Oct 27, 2012
 */
@Component
@Path("/verifyOTP")
@Produces(MediaType.APPLICATION_JSON)
public class VerifyOTPServiceResource extends CommonService<BoProcessPaymentResponse> {

    @Autowired
    IBankService bankService;
    @Autowired
    private ISignatureService signatureService;
    @Context
    private HttpServletRequest request;
    private String contextPath;
    private static final String serviceName = "verifyOTP";
    private StringBuilder logMessage;
    @Context
    ServletContext servletContext;

    @POST
    @Consumes("application/json")
    public BoProcessPaymentResponse verifyOTP(Map boRequest) {

        BoProcessPaymentResponse boResponse;
        contextPath = request.getContextPath().replace("/", "");
        logMessage = new StringBuilder();
        Gson gson = new Gson();
        try {
            logService.initLogMessage(DateUtils.getDate(new Date(), dateFormat), request.getLocalAddr(), Constants.BI + "-"
                    + contextPath.toUpperCase(), serviceName, "", request.getRemoteHost(), request.getRemoteAddr());
            if (boRequest == null) {
                appendMessage(logMessage, "INPUT IS NULL");
                boResponse = getResponse(Constants.ERROR_5000);
                writeErrorLog(boResponse, logMessage);
            }
            final String orderNo = (String) boRequest.get("orderNo");
            String otp = (String) boRequest.get("otp");
            if (otp == null) {
                otp = (String) boRequest.get("authenInfo");
            }
            final String numberInput = (String) boRequest.get("numberInput");
            String checksum = "";
            if (boRequest.containsKey("checksum")) {
                checksum = (String) boRequest.remove("checksum");
            }

            appendParams(logMessage, orderNo, otp, numberInput, checksum);

            appendMessage(logMessage, "validateParams");
            //validate required paramters from MI
            String responseCode = validate(orderNo, otp, numberInput, checksum);
            boResponse = getResponse(responseCode);
            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
                //return list of response code,123Pay transaction id, url input card
                return writeErrorLog(boResponse, logMessage);
            }
            appendParams(logMessage, request.getRemoteAddr());
            appendMessage(logMessage, "checkAllowIP");
            responseCode = checkAllowIP(Constants.INTERNAL_MODULE, request.getRemoteAddr());
            boResponse = getResponse(responseCode);
            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            if (!Constants.RESPONSE_CODE_1.equals(responseCode)) {
                return writeErrorLog(boResponse, logMessage);
            }

            appendMessage(logMessage, "getSignature");
            AbsSignature signature = signatureService.getSignature(Constants.INTERNAL_MODULE);
            if (signature == null) {
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
//            appendParams(logMessage, rawData, checksum);
            appendMessage(logMessage, "verifySignature");
            //Just pass raw data to verify. ISignature try to load key by its self
            BoBaseResponse baseResponse = signature.verifySignature(ChecksumGeneration.ALGORITHM_SHA1, rawData, checksum);
            boResponse = getResponse(baseResponse.getDetailResponseCode());
            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
                //return list of response code,123Pay transaction id, url input card
                return writeErrorLog(boResponse, logMessage);
            }

            //query order from database
            appendMessage(logMessage, "loadOrderVerifyOTP");
            // sp_bi_bs_get_order_info_v_otp(
            BoBSOtp boOrder = bankService.loadOrderVerifyOTP(orderNo);
            System.out.println("BoBSOtp: " + gson.toJson(boOrder));
            boResponse = getResponse(boOrder.getResponseCode());
            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
                logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(), logMessage.toString());
                return boResponse;
//                return writeErrorLog(boResponse, logMessage);
            }
            //load xml config
            appendParams(logMessage, contextPath.toLowerCase());
            appendMessage(logMessage, "loadConfig");

            BoBaseBankNew boBank = configService.getModuleConfig().getBankCodeMap().get(boOrder.getBankCode());
            if (boBank == null) {
                boBank = configService.getModuleConfig().getBankCodeMap().get("EIB");
            }
            //update data to response result (in case: neu xay ra exception thi cung co data gui ve)
            buildResponse(boResponse, boOrder);

            //copy boOrder to boEIB
            appendMessage(logMessage, "copyBoOrderToBoEIB");
            BoBS boEIB = new BoBS();
            copyBoOrderToBoEIB(boOrder, boEIB);
            appendMessage(logMessage, Constants.RESPONSE_CODE_1);

            String keyPath = servletContext.getRealPath(ConstantBS.PATH_ASC_KEYS);
            boEIB.setOtp(otp);
            boEIB.setPathKey(keyPath);
            boEIB.setBankService("verifyOTP");
            boEIB.setNumberInput(Integer.valueOf(numberInput).intValue());
            boEIB.setNotifyOrQuery(1);

            //check otp
            appendMessage(logMessage, "processOTP", otp);
            BankFactory bankFactory = new BankFactory();
            BankAbstract bank = bankFactory.createBank(boEIB.getBanksimCode());
            String response = bank.VerifyOTP(logMessage, boEIB);
            boEIB.setBanksimCode(ConstantBS.BANK_SIM_CODE);
//            String response = processOTP(boEIB);
            appendMessage(logMessage, "processOTP", response);
            String status;
            if (ConstantBS.ERROR_VERIFY_OTP_77777777.equals(response)) {
                appendMessage(logMessage, "Sleep 7 seconds");
                Thread.sleep(Integer.parseInt(configService.getModuleConfig().getWaitingTime()));

                appendMessage(logMessage, "Init data");
                BoNotifyQueryRequest boNQRequest = new BoNotifyQueryRequest();
                boNQRequest.setOrderNo(boOrder.getOrderNo());
                boNQRequest.setBankCode(boOrder.getBankCode());

                String rawDataNotify = boOrder.getOrderNo();
                signature = signatureService.getSignature(Constants.INTERNAL_MODULE);
                String checksumMerchant = signature.createSignature(ChecksumGeneration.ALGORITHM_SHA1, rawDataNotify);
                appendParams(logMessage, boOrder.getOrderNo(), checksumMerchant);
                boNQRequest.setChecksum(checksumMerchant);
                String notifyUrl = configService.getModuleConfig().getUrlNotify();

                appendMessage(logMessage, "invokeNotify", notifyUrl + " " + boOrder.getBankCode());
                logService.log(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(),
                        boResponse.getDetailResponseCode(), logMessage.toString());
                invokeNotify(notifyUrl, boNQRequest);
                return boResponse;
            } else {

                if (Constants.ERROR_7222.equals(response)
                        || Constants.ERROR_7231.equals(response)
                        || Constants.ERROR_7211.equals(response)
                        || Constants.ERROR_7232.equals(response)
                        || Constants.ERROR_7213.equals(response)
                        || Constants.ERROR_7300.equals(response)
                        || Constants.ERROR_7234.equals(response)
                        || Constants.ERROR_7201.equals(response)
                        || MyConstants.ERROR_7230.equals(response)
                        || MyConstants.ERROR_7255.equals(response)
                        || Constants.ERROR_7302.equals(response)
                        || Constants.ERROR_7007.equals(response)
                        || Constants.ERROR_7235.equals(response)
                        || Constants.ERROR_7202.equals(response)
                        || Constants.ERROR_7233.equals(response)) {
                    //invalid OTP
                    //TODO: call SP_BI_EIB_NOTIFY_REINPUT (update 20 to 11)
                    appendParams(logMessage, boEIB.getTransactionId(), boEIB.getBankCode(), boEIB.getBankService(),
                            boEIB.getBankResponseCode(), "" + boEIB.getNotifyOrQuery(), "" + boEIB.getNumberInput());
                    appendMessage(logMessage, "updateOtpReinput");
                    // SP_BI_BS_123PAY_OTP_RE_INPUT(?,?,?,?,?,?,?,?,?,?)}
                    bankService.updateOtpReinput(boEIB);
                    if (boEIB.getResponseCode() == null || !Constants.RESPONSE_CODE_1.equals("" + boEIB.getResponseCode().intValue())) {
                        appendParams(logMessage, "updateOtpReinput is fail");
                        boResponse = getResponse(Constants.ERROR_5000);
                        return writeErrorLog(boResponse, logMessage);
                    }
                    appendParams(logMessage, "" + boEIB.getResponseCode(), "" + boEIB.getOrderStatus(), boEIB.getMiNotifyUrl(), numberInput);
                    if (Integer.valueOf(numberInput).intValue() < 3) {
                        boResponse = getResponse(response);
                        //boResponse = getResponse("" + boEIB.getOrderStatus().intValue());
                        appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getGroupResponseCode());
                        logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(),
                                boResponse.getDetailResponseCode(), logMessage.toString());
                        return boResponse;
                    } else {

                        // no update notify because SP_BI_EIB_NOTIFY_REINPUT updated notify fail
                        boOrder.setOrderStatus(boEIB.getOrderStatus().intValue());
                        boOrder.setBankResponseCode(boEIB.getBankResponseCode());
                        // then notify merchant
                        appendParams(logMessage, "notifyMerchant");
                        notifyMerchant(boOrder, boEIB, boResponse, logMessage);
                        //                    boResponse = getResponse(boResponse.getDetailResponseCode());
                        boResponse = getResponse("" + boEIB.getOrderStatus().intValue());
                        //build payport URL
                        String payportURL = boBank.getPayportURL() + "?" + ConstantBS.TRANX_REF_PARAM + "=" + boOrder.getOrderNo();
                        boResponse.setRedirectURL(payportURL);

                        appendParams(logMessage, boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(),
                                boEIB.getOrderStatus() == null ? "" : ("" + boEIB.getOrderStatus()),
                                boEIB.getBankResponseCode(), boResponse.getRedirectURL());
                        logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(),
                                boResponse.getDetailResponseCode(), logMessage.toString());
                        return boResponse;

                    }
                } else if (Constants.RESPONSE_TIME_OUT.equals(response)) {
                    // notify Pending
                    status = Constants.NOTIFY_PENDING;
                } else if (Constants.RESPONSE_CODE_1.equals(response) && boEIB.getIsSuccess() == 1) {
                    status = Constants.NOTIFY_SUCCESS;
                } else {
                    // notify Fail
                    status = Constants.NOTIFY_FAIL;
                }

                //update bank notify success/fail
                boolean isUpdatedSuccess = updateNotify(status, boEIB, boOrder, boResponse, logMessage);
                if (!isUpdatedSuccess) {
                    boResponse = getResponse(boResponse.getDetailResponseCode());
                    appendParams(logMessage, boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(),
                            boOrder.getOrderStatus() == null ? "" : ("" + boOrder.getOrderStatus()),
                            boOrder.getBankResponseCode(), "verifyOTP fail");
                    return writeErrorLog(boResponse, logMessage);
                } else {
                    boResponse = getResponse(Constants.RESPONSE_CODE_1);
                    if (status.equals(Constants.NOTIFY_SUCCESS) || status.equals(Constants.NOTIFY_FAIL)) {
                        //success or fail then notify merchant
                        notifyMerchant(boOrder, boEIB, boResponse, logMessage);
                    }
                }

                buildResponse(boResponse, boOrder);
                //build payport URL
                String payportURL = boBank.getPayportURL() + "?" + ConstantBS.TRANX_REF_PARAM + "=" + boOrder.getOrderNo();
                appendMessage(logMessage, "UrlRedirect", payportURL);
                boResponse.setRedirectURL(payportURL);

                appendParams(logMessage, boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(),
                        boResponse.getOrderStatus() == null ? "" : ("" + boResponse.getOrderStatus()),
                        boResponse.getBankResponseCode(), boResponse.getRedirectURL());
                if (Constants.RESPONSE_CODE_1.equals(boResponse.getGroupResponseCode())) {
                    logService.log(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(),
                            boResponse.getDetailResponseCode(), logMessage.toString());
                } else {
                    logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(),
                            boResponse.getDetailResponseCode(), logMessage.toString());
                }
                return boResponse;
            }
        } catch (Throwable exception) {
            exception.printStackTrace();
            boResponse = new BoProcessPaymentResponse();
            boResponse.setGroupResponseCode(Constants.ERROR_5000);
            boResponse.setDetailResponseCode(Constants.ERROR_5000);
            boResponse.setDetailDescription("Unhandle Excpetion");
            logService.logException(DateUtils.getDate(new Date(), dateFormat), Constants.ERROR_5001, Constants.ERROR_5001, logMessage.toString(), exception);
            return boResponse;
        }

    }

    private void notifyMerchant(BoBSOtp boOrder, BoBS boEIB, BoProcessPaymentResponse boResponse, StringBuilder logMessage) throws TechniqueException {
        //success or fail then notify merchant
        /*
         * call notify to merchant to inform status of order
         */
        String rawData = boOrder.getOrderNo();
        AbsSignature signature = signatureService.getSignature(Constants.INTERNAL_MODULE);
        String checksumMerchant = signature.createSignature(ChecksumGeneration.ALGORITHM_SHA1, rawData);
        appendParams(logMessage, boOrder.getOrderNo(), checksumMerchant);
        appendMessage(logMessage, "invokeNotifyMerchant", boEIB.getMiNotifyUrl());
        // check status of notify merchant and order status
        try {
            BoMiNotifyResponse boMiNotify = invokeNotifyMerchant(boEIB.getMiNotifyUrl(), boOrder.getOrderNo(), checksumMerchant);

            if (boMiNotify.getGroupResponseCode().equals(Constants.RESPONSE_CODE_1)
                    && boOrder.getOrderStatus().intValue() == 1) {
                boOrder.setNotifyStatus(1);
            } else {
                boOrder.setNotifyStatus(0);
            }
            appendParams(logMessage, boMiNotify.getGroupResponseCode(), boMiNotify.getDetailResponseCode(),
                    boOrder.getOrderStatus().intValue() + "", boOrder.getNotifyStatus() + "");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (boOrder.getOrderStatus().intValue() == 1) {
                boResponse.setGroupResponseCode(Constants.RESPONSE_CODE_1);
                boResponse.setDetailResponseCode(Constants.RESPONSE_CODE_1);
                boOrder.setNotifyStatus(0);
            } else {
                boResponse.setGroupResponseCode(Constants.ERROR_5000);
                boResponse.setDetailResponseCode(Constants.ERROR_5001);
            }
            if (ex instanceof ClientTransportException || ex instanceof ConnectException
                    || ex instanceof RemoteException || ex instanceof SocketException
                    || ex instanceof IOException || ex instanceof ClientHandlerException) {
                // connection timeout
                appendParams(logMessage, Constants.RESPONSE_TIME_OUT, "" + boOrder.getOrderStatus(), "" + boOrder.getNotifyStatus());
            } else {
                // exception
                appendParams(logMessage, Constants.ERROR_5000, "" + boOrder.getOrderStatus(), "" + boOrder.getNotifyStatus());
            }
        }
    }

    private void buildResponse(BoProcessPaymentResponse boResponse, BoOrder boOrder) {
        boResponse.setOrderNo(boOrder.getOrderNo());
        boResponse.setOrderStatus(boOrder.getOrderStatus());
        boResponse.setMerchantTransactionId(boOrder.getMerchantTransactionId());
        boResponse.setTotalAmount(boOrder.getTotalAmount());
        boResponse.setOpAmount(boOrder.getOpAmount());
        boResponse.setBankCode(boOrder.getBankCode());
        boResponse.setMerchantCode(boOrder.getMerchantCode());
        boResponse.setBankResponseCode(boOrder.getBankResponseCode());
        boResponse.setDescription(boOrder.getDescription());
        boResponse.setNotifyStatus(boOrder.getNotifyStatus());
    }

    private String validate(String orderNo, String otp, String numberInput, String checksum) {
        if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(otp)
                || StringUtils.isEmpty(numberInput) || StringUtils.isEmpty(checksum)) {
            return Constants.ERROR_6101;
        }
        if (orderNo.length() > 17 || !StringUtils.isAlphanumeric(orderNo)
                || !StringUtils.isNumeric(otp) || otp.length() != 8) {
            return Constants.ERROR_6100;
        }

        return Constants.RESPONSE_CODE_1;
    }

    private void copyBoOrderToBoEIB(BoBSOtp boOrder, BoBS boEIB) {
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
        boEIB.setCardNo(boOrder.getCardNo());
        String subBankCode = boOrder.getSubbankCode();
        subBankCode = subBankCode.substring(4, subBankCode.length());
        boEIB.setBanksimCode(subBankCode);
    }

    private String processOTP(BoBS boEIB) {
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
                if (result.equals(ConstantBS.ERROR_VERIFY_OTP_8)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_8);
                    return Constants.ERROR_7222;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_2)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_2);
                    return Constants.ERROR_7231;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_5)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_5);
                    return Constants.ERROR_7211;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_14)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_14);
                    return Constants.ERROR_7232;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_20)
                        || result.equals(ConstantBS.ERROR_VERIFY_OTP_21)
                        || result.equals(ConstantBS.ERROR_VERIFY_OTP_58)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_20);
                    return Constants.ERROR_7213;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_41)
                        || result.equals(ConstantBS.ERROR_VERIFY_OTP_43)
                        || result.equals(ConstantBS.ERROR_VERIFY_OTP_4)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_41);
                    return Constants.ERROR_7234;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_25)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_25);
                    return Constants.ERROR_7300;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_51)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_51);
                    return Constants.ERROR_7201;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_54)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_54);
                    return Constants.ERROR_7233;
                } else if (result.equals(ConstantBS.ERROR_VERIFY_OTP_91)) {
                    boEIB.setBankResponseCode(ConstantBS.ERROR_VERIFY_OTP_91);
                    boEIB.setIsSuccess(-1);

                } else if (result.equals(ConstantBS.INVOKE_BANK_SUCCESS)) {
                    boEIB.setBankResponseCode(ConstantBS.INVOKE_BANK_SUCCESS);
                    boEIB.setIsSuccess(1);
                } else {
                    boEIB.setBankResponseCode(result);
                    boEIB.setIsSuccess(-1);
                }
            }
        }
        return Constants.RESPONSE_CODE_1;
    }

    private boolean updateNotify(String status, BoBS boEIB, BoOrder boOrder, BoProcessPaymentResponse boResponse, StringBuilder logMessage) throws TechniqueException {
        boEIB.setNotifyOrQuery(1);
        appendParams(logMessage, boEIB.getTransactionId(), boEIB.getBankCode(), "" + boEIB.getBankService(), boEIB.getBankResponseCode(),
                "" + boEIB.getNotifyOrQuery());
        if (status.equals(Constants.NOTIFY_PENDING)) {
            appendMessage(logMessage, "SP_BI_EIB_NOTIFY_PENDING");
            //Update BI notify pending
            bankService.updateNotify(boEIB, Constants.NOTIFY_PENDING);

            if (!Constants.RESPONSE_CODE_1.equals("" + boEIB.getResponseCode().intValue())) {
                appendMessage(logMessage, "" + boEIB.getResponseCode());
//                writeLogError(Constants.ERROR_5000, StringUtils.defaultIfEmpty(Integer.toString(boEIB.getResponseCode()), 
//                Constants.ERROR_5000), logMessage.toString());
                boResponse.setGroupResponseCode(Constants.ERROR_5000);
                boResponse.setDetailResponseCode("" + boEIB.getResponseCode().intValue());
                return false;
            }
            appendMessage(logMessage, Constants.RESPONSE_CODE_1);
            boResponse.setGroupResponseCode(Constants.RESPONSE_CODE_1);
            boResponse.setDetailResponseCode(Constants.RESPONSE_CODE_1);

            buildResponse(boResponse, boOrder);

            appendMessage(logMessage, "Result: ");
            appendParams(logMessage, boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(),
                    boResponse.getOrderNo(), boResponse.getMerchantTransactionId(),
                    Integer.toString(boResponse.getOrderStatus()), Integer.toString(boResponse.getTotalAmount()),
                    Integer.toString(boResponse.getOpAmount()), boResponse.getBankCode(), boResponse.getMerchantCode(),
                    boResponse.getBankResponseCode(), boResponse.getDescription());
//            writeLogSuccess(Constants.RESPONSE_CODE_1, logMessage.toString());
            return true;
        } else if (status.equals(Constants.NOTIFY_SUCCESS)) {
            appendMessage(logMessage, "SP_BI_123PAY_NOTIFY_SUCCESS");
            //Update BI notify success
            bankService.updateNotify(boEIB, Constants.NOTIFY_SUCCESS);

            if (!Constants.RESPONSE_CODE_1.equals("" + boEIB.getResponseCode().intValue())) {
                appendParams(logMessage, "" + boEIB.getResponseCode());
//                writeLogError(Constants.ERROR_5000, StringUtils.defaultIfEmpty(Integer.toString(boEIB.getResponseCode()), Constants.ERROR_5000), 
//                    logMessage.toString());
                boResponse.setGroupResponseCode(Constants.ERROR_5000);
                boResponse.setDetailResponseCode("" + boEIB.getResponseCode().intValue());
                return false;
            }
            appendParams(logMessage, "" + boEIB.getResponseCode(), boEIB.getMiNotifyUrl());
            boEIB.setOrderStatus(Integer.parseInt(Constants.RESPONSE_CODE_1));
        } else {
            appendMessage(logMessage, "SP_BI_123PAY_NOTIFY_FAIL");
            //Update BI notify fail
            bankService.updateNotify(boEIB, Constants.NOTIFY_FAIL);
            if (!Constants.RESPONSE_CODE_1.equals("" + boEIB.getResponseCode().intValue())) {
                appendParams(logMessage, "" + boEIB.getResponseCode().intValue());
//                writeLogError(Constants.ERROR_5000, StringUtils.defaultIfEmpty(Integer.toString(boEIB.getResponseCode()), Constants.ERROR_5000), 
//                logMessage.toString());
                boResponse.setGroupResponseCode(Constants.ERROR_5000);
                boResponse.setDetailResponseCode("" + boEIB.getResponseCode().intValue());
                return false;
            }
            appendParams(logMessage, "" + boEIB.getResponseCode(), "" + boEIB.getOrderStatus(), boEIB.getMiNotifyUrl());
//            boResponse.setOrderStatus(Integer.parseInt(boEIB.getOrderStatus()));
        }
        boOrder.setOrderStatus(boEIB.getOrderStatus().intValue());
        boOrder.setBankResponseCode(boEIB.getBankResponseCode());

        return true;
    }

    private BoMiNotifyResponse invokeNotifyMerchant(String notifyUrl, String orderNo, String dataSign) throws Exception {

//        return null;
        System.out.println("URL NOTIFY: " + notifyUrl);
        System.out.println("OrderNO : " + orderNo);
        System.out.println("dataSign : " + dataSign);
         BoMiNotifyResponse bo = new BoMiNotifyResponse(Constants.RESPONSE_CODE_1, Constants.RESPONSE_CODE_1);

         Map m = new HashMap();
         m.put("orderNo", orderNo);
         m.put("checksum", dataSign);
         CustomRestClient client = new CustomRestClient();
         client.setWebResource(notifyUrl);
         bo = client.post(m, bo.getClass());

         return bo;
    }

    private BoNotifyQueryBOrder invokeNotify(String notifyUrl, BoNotifyQueryRequest boNQRequest) throws Exception {
        BoNotifyQueryBOrder bo = new BoNotifyQueryBOrder();

        Map m = new HashMap();
        m.put("orderNo", boNQRequest.getOrderNo());
        m.put("bankCode", boNQRequest.getBankCode());
        m.put("checksum", boNQRequest.getChecksum());
        CustomRestClient client = new CustomRestClient();
        client.setWebResource(notifyUrl);
        bo = client.post(m, bo.getClass());

        return bo;
    }
}
