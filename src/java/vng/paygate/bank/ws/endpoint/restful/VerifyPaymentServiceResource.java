//package vng.paygate.bank.ws.endpoint.restful;
//
//import java.net.URLEncoder;
//import java.util.Date;
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import vng.paygate.bank.common.CommonService;
//import vng.paygate.bank.jaxb.adapter.BoBaseBank;
//import vng.paygate.bank.service.IBankService;
//import vng.paygate.domain.bo.BoBaseResponse;
//import vng.paygate.domain.bo.BoOrder;
//import vng.paygate.domain.bo.BoVerifyPaymentRequest;
//import vng.paygate.domain.bo.BoVerifyPaymentResponse;
//import vng.paygate.domain.common.Constants;
//import vng.paygate.domain.common.util.ChecksumGeneration;
//import vng.paygate.domain.common.util.DateUtils;
//import vng.paygate.domain.factory.signature.AbsSignature;
//import vng.paygate.domain.signature.ISignatureService;
//
///**
// *
// * @author trinm2
// * @since 123Pay
// * @created on: Oct 24, 2012
// *
// */
//@Component
//@Path("/verifyPayment")
//@Produces(MediaType.APPLICATION_JSON)
//public class VerifyPaymentServiceResource extends CommonService<BoVerifyPaymentResponse> {
//
//    @Autowired
//    IBankService bankService;
//    @Autowired
//    private ISignatureService signatureService;
//    @Context
//    private HttpServletRequest request;
//    private String contextPath;
//    private static final String serviceName = "verifyPayment";
//
//    @POST
//    @Consumes("application/json")
//    public BoVerifyPaymentResponse verifyPayment(BoVerifyPaymentRequest verifyPaymentRequest) {
//        BoVerifyPaymentResponse boResponse;
//        contextPath = request.getContextPath().replace("/", "");
//        StringBuilder logMessage = new StringBuilder();
//
//        try {
//            logService.initLogMessage(DateUtils.getDate(new Date(), dateFormat), request.getLocalAddr(), Constants.BI + "-"
//                    + contextPath.toUpperCase(), serviceName, "", request.getRemoteHost(), request.getRemoteAddr());
//            if (verifyPaymentRequest == null) {
//                appendMessage(logMessage, "INPUT IS NULL");
//                boResponse = getResponse(Constants.ERROR_5000);
//                writeErrorLog(boResponse, logMessage);
//            }
//            appendParams(logMessage, verifyPaymentRequest.getOrderNo(), verifyPaymentRequest.getChecksum());
//
//            appendMessage(logMessage, "validateParams");
//            //validate required paramters from MI
//            String responseCode = validate(verifyPaymentRequest.getOrderNo(), verifyPaymentRequest.getChecksum());
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
//            appendMessage(logMessage, "verifySignature");
//            String rawData = verifyPaymentRequest.getOrderNo();
//            //Just pass raw data to verify. ISignature try to load key by its self
//            BoBaseResponse baseResponse = signature.verifySignature(ChecksumGeneration.ALGORITHM_SHA1, rawData, verifyPaymentRequest.getChecksum());
//            boResponse = getResponse(baseResponse.getDetailResponseCode());
//            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
//            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
//                //return list of response code,123Pay transaction id, url input card
//                return writeErrorLog(boResponse, logMessage);
//            }
//
//            //query order afrom database
//            appendMessage(logMessage, "queryOrderVerifyPayment");
//            BoOrder boOrder = bankService.verifyPayment(verifyPaymentRequest.getOrderNo());
//            boResponse = getResponse(boOrder.getResponseCode());
//            appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription(), "" + boOrder.getOrderStatus());
//            if (!Constants.RESPONSE_CODE_1.equals(boResponse.getDetailResponseCode())) {
//                return writeErrorLog(boResponse, logMessage);
//            }
//            
//            /*
//            String accountName="accountNoName";
//            if(!StringUtils.isEmpty(boOrder.getAccountName())){
//                accountName = boOrder.getAccountName().replace(".", "__");
//            }
//            String messageParams = boOrder.getOrderNo() + "." + boOrder.getMerchantCode() + "." + accountName + "." + boOrder.getOpAmount() + "." + new Date().getTime();
//            appendParams(logMessage, messageParams);
//            appendMessage(logMessage, "createSignature");
//            signature = signatureService.getSignature(Constants.FE_MODULE);
//            String resChecksum = signature.createSignature(ChecksumGeneration.ALGORITHM_SHA1, messageParams);
//            if (resChecksum == null) {
//                boResponse = getResponse(Constants.ERROR_5000);
//                appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
//                return writeErrorLog(boResponse, logMessage);
//            }
//            appendMessage(logMessage, "bankcode: " + boOrder.getBankCode());
//            BoBaseBank boBank = configService.getModuleConfig().getBankCodeMap().get(boOrder.getBankCode());
//            if (boBank == null) {
//                boBank = configService.getModuleConfig().getBankCodeMap().get("EIB");
//            }
//            //encode description
//            String description = StringUtils.defaultIfEmpty(boOrder.getDescription(), "");
//            byte[] bData = description.getBytes("UTF-8");
//            String sCR = new String(new byte[]{13});
//            String sNL = new String(new byte[]{10});
//            String desc = Base64.encodeBase64String(bData).replace(sCR, "").replace(sNL, "").replace("=", "");
//            messageParams += "."+resChecksum + "." + desc;
//           
//            //bankId: for show correct bank logo
//            messageParams += "." + boOrder.getSelectedBank();
//            
//            //Append parameter to 123pay url
//            String url123Pay = URLEncoder.encode(messageParams, "UTF-8");
//           
//            String urlRedirect = boBank.getCardInfoUrl();
//            url123Pay =  urlRedirect+ "." + url123Pay;
//
//            url123Pay = url123Pay + ".html";
//             */
//            String url123Pay = this.buildRedirectURL(logMessage, boOrder);
//            appendParams(logMessage, Constants.RESPONSE_CODE_1, url123Pay);
//
//            boResponse.setOrderNo(verifyPaymentRequest.getOrderNo());
//            boResponse.setRedirectUrl(url123Pay);
//            boResponse.setDetailResponseCode(Constants.RESPONSE_CODE_1);
//            boResponse.setGroupResponseCode(Constants.RESPONSE_CODE_1);
//            boResponse.setDetailDescription("Success");
//            appendMessage(logMessage, "verifyPaymentResult success");
//            logService.log(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(), logMessage.toString());
//            return boResponse;
//        } catch (Throwable exception) {
//            boResponse = new BoVerifyPaymentResponse();
//            boResponse.setDetailResponseCode(Constants.ERROR_5000);
//            boResponse.setDetailDescription("Unhandle Excpetion");
//            logService.logException(DateUtils.getDate(new Date(), dateFormat), Constants.ERROR_5001, Constants.ERROR_5001, logMessage.toString(), exception);
//            return boResponse;
//        }
////        return null;
//    }
//
//    protected String buildRedirectURL(StringBuilder logMessage, BoOrder boOrder) {
//        try {
//            appendMessage(logMessage, "buildRedirectURL");
//            BoBaseBank boBank = configService.getModuleConfig().getBankCodeMap().get(boOrder.getBankCode());
//            String description = StringUtils.defaultIfEmpty(boOrder.getDescription(), "");
//            byte[] bData = description.getBytes("UTF-8");
//            String sCR = new String(new byte[]{13});
//            String sNL = new String(new byte[]{10});
//            String desc = Base64.encodeBase64String(bData).replace(sCR, "").replace(sNL, "").replace("=", "");
//            // START - new url atm
//            long ts = System.currentTimeMillis();
//            String messageParams = boOrder.getOrderNo() + "|" + boOrder.getMerchantCode()
//                    + "|" + boOrder.getOpAmount() + "|" + boOrder.getBankCode()
//                    + "|" + boOrder.getSelectedBank() + "|" + ts;
//            appendParams(logMessage, messageParams);
//            appendMessage(logMessage, "createSignature");
//            AbsSignature signature = signatureService.getSignature(Constants.FE_MODULE);
//            String resChecksum = signature.createSignature("SHA-256", messageParams);
//            if (resChecksum == null) {
//                BoVerifyPaymentResponse boResponse = getResponse(Constants.ERROR_5000);
//                appendParams(logMessage, boResponse.getDetailResponseCode(), boResponse.getDetailDescription());
//                return null;
//            }
//            String url123Pay = boBank.getCardInfoUrl()
//                    + "orderNo=" + boOrder.getOrderNo()
//                    + "&merchantCode=" + boOrder.getMerchantCode()
//                    + "&amount=" + boOrder.getOpAmount()
//                    + "&bankCode=" + boOrder.getBankCode()
//                    + "&subBank=" + boOrder.getSelectedBank()
//                    + "&accId=" + boOrder.getAccountName()
//                    + "&time=" + ts
//                    + "&description=" + desc
//                    + "&checksum=" + resChecksum;
//            // END - new url atm
//            appendParams(logMessage, Constants.RESPONSE_CODE_1, url123Pay);
//
//            return url123Pay;
//        } catch (Throwable ex) {
//            ex.printStackTrace();
//            //throw new TechniqueException("Exception in function buildRedirectURL()", ex);
//            return "";
//        }
//    }
//
//    private String validate(String orderNo, String checksum) {
//        if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(checksum)) {
//            return Constants.ERROR_6101;
//        }
//        if (orderNo.length() > 30 || !StringUtils.isAlphanumeric(orderNo)) {
//            return Constants.ERROR_6100;
//        }
//
//        return Constants.RESPONSE_CODE_1;
//    }
//}
