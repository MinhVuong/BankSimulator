package vng.paygate.bank.common;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import vng.paygate.bank.bo.BoBS;
import vng.paygate.bank.bo.BoCardInfo;
import vng.paygate.bank.jaxb.adapter.BoBIModuleConfigNew;
import vng.paygate.bank.jaxb.adapter.BoBaseBank;
import vng.paygate.config.service.IConfigService;
import vng.paygate.domain.bo.BoBaseResponse;
import vng.paygate.domain.common.Constants;
import vng.paygate.domain.common.util.DateUtils;
import vng.paygate.domain.log.service.ILogService;

/**
 *
 * @param <T>
 * @author CheHC
 * @since 123Pay @created on: Aug 21, 2012
 */
public abstract class CommonService<T extends BoBaseResponse> extends SpringBeanAutowiringSupport {

    @Autowired
    protected IConfigService<BoBIModuleConfigNew> configService;
    @Autowired
    protected ILogService logService;
    protected static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public String verifyRequireParams(String... params) {
        for (String param : params) {
            if (StringUtils.isEmpty(param)) {
                return Constants.ERROR_6101;
            }
        }
        return Constants.RESPONSE_CODE_1;
    }

    protected String getWsCallerIP(WebServiceContext wsContext) {
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
        return req.getRemoteAddr();
    }

    protected String getLocalIP(WebServiceContext wsContext) {
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
        return req.getLocalAddr();
    }

    protected String getContextPath(WebServiceContext wsContext) {
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
        return req.getContextPath().replace("/", "");
    }

    /**
     *
     * @param moduleName : if caller is internal system (CRON, BI...) then
     * moduleName = "INTERNAL" else moduleName = MerchantCode.
     * @param callerIP
     * @return
     */
    protected String checkAllowIP(String moduleName, String callerIP) {
        return Constants.RESPONSE_CODE_1;
//        BoBIModuleConfig boModuleConfig = configService.getModuleConfig();
//        if (Constants.INTERNAL_MODULE.equals(moduleName)) {
//            for (String ip : boModuleConfig.getAllowedIPs()) {
//                if (callerIP.equals(ip.trim())) {
//                    return Constants.RESPONSE_CODE_1;
//                }
//            }
//        } 
        /*else {
            for (String ip : boModuleConfig.getBankConfig().getAllowedIPs()) {
                if (callerIP.equals(ip.trim())) {
                    return Constants.RESPONSE_CODE_1;
                }
            }
        }*/
//        return Constants.ERROR_6005;
    } 

    public void appendMessage(StringBuilder logMessage, String... messages) {
        for (String mess : messages) {
            logMessage.append(Constants.LOG_SEPARATOR).append(mess);
        }
    }

    /**
     * append log message with parameters and returns
     *
     * @param logMessage
     * @param messages
     */
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

    protected T getResponse(String responseCode) throws InstantiationException, IllegalAccessException {
        BoBaseResponse boResponse = configService.getModuleConfig().getResponseCodeMap().get(responseCode);
        if (boResponse == null) {
            boResponse = configService.getModuleConfig().getResponseCodeMap().get(Constants.ERROR_5000);
        }

        T result = getNewGenericInstanse();
        result.setDetailDescription(boResponse.getDetailDescription());
        result.setDetailResponseCode(boResponse.getDetailResponseCode());
        result.setGroupDescription(boResponse.getGroupDescription());
        result.setGroupResponseCode(boResponse.getGroupResponseCode());
        result.setAuthSite(boResponse.getAuthSite());
        result.setVerifyOtpURL(boResponse.getVerifyOtpURL());
        result.setOrderNo(boResponse.getOrderNo());
        return result;
    }
 
    private T getNewGenericInstanse() throws InstantiationException, IllegalAccessException {

        ParameterizedType genericSuperClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<T> clazz = (Class<T>) genericSuperClass.getActualTypeArguments()[0];
        return clazz.newInstance();

    }

    protected T writeErrorLog(T boResponse, StringBuilder logMessage) throws InstantiationException, IllegalAccessException {
        logService.logError(DateUtils.getDate(new Date(), dateFormat), boResponse.getGroupResponseCode(), boResponse.getDetailResponseCode(), logMessage.toString());
//        boResponse.setGroupResponseCode(Constants.ERROR_5000);
        T result = getNewGenericInstanse();
        result.setDetailDescription(boResponse.getDetailDescription());
        result.setDetailResponseCode(boResponse.getDetailResponseCode());
        result.setGroupDescription("He thong ban");
        result.setGroupResponseCode(Constants.ERROR_5000);
        result.setOrderNo(boResponse.getOrderNo());
        return result;
    }
    
}
