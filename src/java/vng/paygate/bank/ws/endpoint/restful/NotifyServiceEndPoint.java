///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package vng.paygate.bank.ws.endpoint.restful;
//
//import com.sun.jersey.api.client.ClientHandlerException;
//import com.sun.xml.ws.client.ClientTransportException;
//import java.io.IOException;
//import java.net.ConnectException;
//import java.net.SocketException;
//import java.rmi.RemoteException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import vng.paygate.bank.bo.*;
//import vng.paygate.bank.common.CommonService;
//import vng.paygate.bank.common.ConstantBS;
//import vng.paygate.bank.jaxb.adapter.BoBaseBank;
//import vng.paygate.bank.service.IBankService;
//import vng.paygate.domain.bo.BoBaseResponse;
//import vng.paygate.domain.bo.BoMiNotifyResponse;
//import vng.paygate.domain.bo.BoNotifyQueryBOrder;
//import vng.paygate.domain.bo.BoOrder;
//import vng.paygate.domain.common.Constants;
//import vng.paygate.domain.common.util.ChecksumGeneration;
//import vng.paygate.domain.common.util.CustomRestClient;
//import vng.paygate.domain.common.util.DateUtils;
//import vng.paygate.domain.exception.TechniqueException;
//import vng.paygate.domain.factory.signature.AbsSignature;
//import vng.paygate.domain.signature.ISignatureService;
//
//
///**
// *
// * @author trinm2
// */
//@Component
//@Path("/notify")
//@Produces(MediaType.APPLICATION_JSON)
//public class NotifyServiceEndPoint extends CommonService<BoNotifyQueryBOrder> {
//    @Autowired
//    IBankService bankService;
//    @Autowired
//    private ISignatureService signatureService;
//    @Context
//    private HttpServletRequest request;
//    private String contextPath;
//    private static final String serviceName = "notify";
//     private StringBuilder logMessage;
//    @Context
//    ServletContext servletContext;
//
//    @POST
//    @Consumes("application/json")
//    public BoNotifyQueryBOrder notify(BoNotifyQueryRequest boNQRequest) {
//
//         BoNotifyQueryBOrder boResponse;
//        contextPath = request.getContextPath().replace("/", "");
//        logMessage = new StringBuilder();
//
//        try {
//            logService.initLogMessage(DateUtils.getDate(new Date(), dateFormat), request.getLocalAddr(), Constants.BI + "-"
//                    + contextPath.toUpperCase(), serviceName, "", request.getRemoteHost(), request.getRemoteAddr());
//            if (boNQRequest == null) {
//                appendMessage(logMessage, "INPUT IS NULL");
//                boResponse = getResponse(Constants.ERROR_5000);
//                writeErrorLog(boResponse, logMessage);
//            }
//            final String orderNo  = (String) boNQRequest.getOrderNo();
//            final String bankCode = (String) boNQRequest.getBankCode();
//            String checksum = (String) boNQRequest.getChecksum();
//            
//
//            appendParams(logMessage, orderNo, bankCode,checksum);
//
//            appendMessage(logMessage, "validateParams");
//            //validate required paramters from MI
//            String responseCode = validate(orderNo,checksum);
//            boResponse = getResponse(responseCode);
//            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
//            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
//                //return list of response code,123Pay transaction id, url input card
//                return writeErrorLog(boResponse, logMessage);
//            }
//            appendParams(logMessage, request.getRemoteAddr());
//            appendMessage(logMessage, "checkAllowIP");
//            responseCode = checkAllowIP(Constants.INTERNAL_MODULE, request.getRemoteAddr());
//            boResponse = getResponse(responseCode);
//            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
//            if (!Constants.RESPONSE_CODE_1.equals(responseCode)) {
//                return writeErrorLog(boResponse, logMessage);
//            }
//
//            appendMessage(logMessage, "getSignature");
//            AbsSignature signature = signatureService.getSignature(Constants.INTERNAL_MODULE);
//            if (signature == null) {
//                boResponse = getResponse(Constants.ERROR_5000);
//                appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
//                return writeErrorLog(boResponse, logMessage);
//            }
//            appendParams(logMessage, Constants.RESPONSE_CODE_1); //getSignature succes
//
//            String rawData = orderNo;
//            
////            appendParams(logMessage, rawData, checksum);
//            appendMessage(logMessage, "verifySignature");
//            //Just pass raw data to verify. ISignature try to load key by its self
//            BoBaseResponse baseResponse = signature.verifySignature(ChecksumGeneration.ALGORITHM_SHA1, rawData, checksum);
//            boResponse = getResponse(baseResponse.getDetailResponseCode());
//            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
//            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
//                //return list of response code,123Pay transaction id, url input card
//                return writeErrorLog(boResponse, logMessage);
//            }
//
//           
//            
//            //query order from database
//            appendMessage(logMessage, "loadOrderNotify");
//
//            BoOrder boOrder = bankService.loadOrderInfo(orderNo);
//            boResponse = getResponse(boOrder.getResponseCode());
//            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
//            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
//                logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(), logMessage.toString());
//                return boResponse;
////                return writeErrorLog(boResponse, logMessage);
//            }
//            //load xml config
//            appendParams(logMessage, contextPath.toLowerCase());
//            appendMessage(logMessage, "loadConfig");
//            
//            BoBaseBank boBank = configService.getModuleConfig().getBankCodeMap().get(boOrder.getBankCode());
//            if (boBank == null) {
//                boBank = configService.getModuleConfig().getBankCodeMap().get("EIB");
//            }
//            
//
//           
//             //update properties for BoBankNet obj
//        BoNotify boNotify = new BoNotify();
//        boNotify.setOrderNo(orderNo);
//        boNotify.setBankCode(boOrder.getBankCode());
//        boNotify.setBankResponseCode(boNQRequest.getResponseCode());
//        boNotify.setBankService(serviceName);
//        boNotify.setNotifyOrQuery(1);
//
//             
//        /**
//         * Update notify status
//         */
//        appendParams(logMessage, boNotify.getOrderNo(), boNotify.getBankCode(), "" + boNotify.getBankService(), boNotify.getBankResponseCode(), "" + boNotify.getNotifyOrQuery());
//        if (!updateOrderToDB(boNotify,boResponse,boOrder)) {
//            return boResponse;
//        }
//
//        /*
//         * call notify to merchant to inform status of order
//         */
//        rawData = boNotify.getOrderNo();
//
//        signature = signatureService.getSignature(Constants.INTERNAL_MODULE);
//        String checksumMerchant = signature.createSignature(ChecksumGeneration.ALGORITHM_SHA1, rawData);
//        appendParams(logMessage, boNotify.getOrderNo(), checksumMerchant);
//        appendMessage(logMessage, "invokeNotifyMerchant",boNotify.getNotifyUrl() +" Checksum: "+ checksumMerchant);
//
//        
//        // check status of notify merchant and order status
//        BoMiNotifyResponse boMiNotify;
//        try {
//            boMiNotify = invokeNotifyMerchant(boNotify.getNotifyUrl(), boNotify.getOrderNo(), checksumMerchant);
//            boResponse.setGroupResponseCode(Constants.RESPONSE_CODE_1);
//            boResponse.setDetailResponseCode(Constants.RESPONSE_CODE_1);
//
//            if (boMiNotify.getGroupResponseCode().equals(Constants.RESPONSE_CODE_1)
//                    && boOrder.getOrderStatus().intValue() == 1) {
//                boOrder.setNotifyStatus(1);
//            } else {
//                boOrder.setNotifyStatus(0);
//            }
//            appendParams(logMessage, boMiNotify.getGroupResponseCode(), boMiNotify.getDetailResponseCode(),
//                    boOrder.getOrderStatus().intValue() + "", boOrder.getNotifyStatus().intValue() + "");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            if (boOrder.getOrderStatus().intValue() == 1) {
//                boResponse.setGroupResponseCode(Constants.RESPONSE_CODE_1);
//                boResponse.setDetailResponseCode(Constants.RESPONSE_CODE_1);
//                boOrder.setNotifyStatus(0);
//            } else {
//                boResponse.setGroupResponseCode(Constants.ERROR_5000);
//                boResponse.setDetailResponseCode(Constants.ERROR_5001);
//                boOrder.setNotifyStatus(0);
//            }
//            if (ex instanceof ClientTransportException || ex instanceof ConnectException
//                    || ex instanceof RemoteException || ex instanceof SocketException
//                    || ex instanceof IOException || ex instanceof ClientHandlerException) {
//                // connection timeout
//                appendParams(logMessage, Constants.RESPONSE_TIME_OUT,
//                        "" + boOrder.getOrderStatus(), "" + boOrder.getNotifyStatus());
//            } else {
//                // exception
//                appendParams(logMessage, Constants.ERROR_5000,
//                        "" + boOrder.getOrderStatus(), "" + boOrder.getNotifyStatus());
//            }
//        }
//            appendParams(logMessage, boResponse.getDetailResponseCode(), "Notify success");
//            logService.log(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(), logMessage.toString());
//            return boResponse;
//        } catch (Throwable exception) {
//            exception.printStackTrace();
//            boResponse = new BoNotifyQueryBOrder();
//            boResponse.setGroupResponseCode(Constants.ERROR_5000);
//            boResponse.setDetailResponseCode(Constants.ERROR_5000);
//            boResponse.setDetailDescription("Unhandle Excpetion");
//            logService.logException(DateUtils.getDate(new Date(), dateFormat), Constants.ERROR_5001, Constants.ERROR_5001, logMessage.toString(), exception);
//            return boResponse;
//        }
//
//    }
//    private String validate(String orderNo, String checksum) {
//        if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(checksum)) {
//            return Constants.ERROR_6101;
//        }
//        if (orderNo.length() > 17 || !StringUtils.isAlphanumeric(orderNo)) {
//            return Constants.ERROR_6100;
//        }
//
//        return Constants.RESPONSE_CODE_1;
//    }
//     private BoMiNotifyResponse invokeNotifyMerchant(String notifyUrl, String orderNo, String dataSign) throws Exception {
//        BoMiNotifyResponse bo = new BoMiNotifyResponse(Constants.RESPONSE_CODE_1, Constants.RESPONSE_CODE_1);
//
//        Map m = new HashMap();
//        m.put("orderNo", orderNo);
//        m.put("checksum", dataSign);
//        CustomRestClient client = new CustomRestClient();
//        client.setWebResource(notifyUrl);
//        bo = client.post(m, bo.getClass());
//
//        return bo;
//    }
//    private boolean updateOrderToDB(BoNotify boNotify,BoNotifyQueryBOrder boResponse,BoOrder boOrder) throws NumberFormatException, TechniqueException {
//        try{
//            if (boOrder.getOrderStatus().toString().equals(ConstantBS.BANK_RESPONSE_PENDING)) {
//                appendMessage(logMessage, "SP_BI_BANK_NOTIFY_SUCCESS");
//                //Update BI notify success
//                bankService.updateOrder(boNotify, Constants.NOTIFY_SUCCESS);
//                appendParams(logMessage, StringUtils.defaultIfEmpty(Integer.toString(boNotify.getResponseCode()), Constants.ERROR_5000), boNotify.getNotifyUrl());
//                if (!Constants.RESPONSE_CODE_1.equals(Integer.toString(boNotify.getResponseCode()))) {
//                    writeErrorLog(boResponse,logMessage);
//                    boResponse.setGroupResponseCode(Constants.ERROR_5000);
//                    boResponse.setDetailResponseCode(StringUtils.defaultIfEmpty(Integer.toString(boNotify.getResponseCode()), Constants.ERROR_5000));
//                    return false;
//                }
//    //            boNQResponse.setOrderStatus(Integer.parseInt(Constants.RESPONSE_CODE_1));
//            // boSML.setOrderStatus(Constants.RESPONSE_CODE_1);
//            } else {
//                appendMessage(logMessage, "SP_BI_BANK_NOTIFY_FAIL");
//                //Update BI notify fail
//                bankService.updateOrder(boNotify, Constants.NOTIFY_FAIL);
//                appendParams(logMessage, StringUtils.defaultIfEmpty(Integer.toString(boNotify.getResponseCode()), Constants.ERROR_5000), boNotify.getNotifyUrl());
//                if (!Constants.RESPONSE_CODE_1.equals(Integer.toString(boNotify.getResponseCode()))) {
//                    writeErrorLog(boResponse,logMessage);
//                    boResponse.setGroupResponseCode(Constants.ERROR_5000);
//                    boResponse.setDetailResponseCode(StringUtils.defaultIfEmpty(Integer.toString(boNotify.getResponseCode()), Constants.ERROR_5000));
//                    return false;
//                }
//    //            boNQResponse.setOrderStatus(Integer.parseInt(boSML.getOrderStatus()));
//            }
//            boOrder.setOrderStatus(Integer.parseInt(Constants.RESPONSE_CODE_1));
//            boOrder.setBankResponseCode(Constants.RESPONSE_CODE_1);
//            return true;
//        }catch(Throwable exception){
//            exception.printStackTrace();
//            return false;
//        }
//    }
//    
//}
