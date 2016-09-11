package vng.paygate.bank.dao.ibatis.mapper;

import java.util.Map;
import vng.paygate.bank.bo.BoBS;
import vng.paygate.bank.bo.BoNotify;

/**
 *
 * @author trinm2
 */
public interface BankDaoMapper {

    public String verifyPayment(Map<String, Object> result);

    public String loadOrderVCard(Map<String, Object> result);
    
    public String loadOrderVOTP(Map<String, Object> result);

    public String verifyCard(BoBS boEIB);
    
    public String updateNotifyPending(BoBS boEIB);
    
    public String updateNotifySuccess(BoBS boEIB);
    
    public String updateNotifyFail(BoBS boEIB);

    public String updateOtpReinput(BoBS boEIB);
    
    public String loadOrderForQuery(Map<String, Object> result);
    
    public String updateOrderSuccess(BoNotify boNotify);
    
    public String updateOrderFail(BoNotify boNotify);
    
    public String getOrderInfo(Map<String, Object> result);
    

}
