package vng.paygate.bank.service;

import vng.paygate.bank.bo.BoBS;
import vng.paygate.bank.bo.BoBSOtp;
import vng.paygate.bank.bo.BoNotify;
import vng.paygate.bank.bo.BoOrderNew;
import vng.paygate.domain.bo.BoOrder;
import vng.paygate.domain.exception.TechniqueException;

/**
 *
 * @author trinm2
 */
public interface IBankService {

    public BoOrder verifyPayment(String orderNo) throws TechniqueException;
    
    public BoOrderNew loadOrderVerifyCard(String orderNo) throws TechniqueException;
    
    public String verifyCard(BoBS boEIB)  throws TechniqueException;
    
    public BoBSOtp loadOrderVerifyOTP(String orderNo) throws TechniqueException;
    
    public void updateNotify(BoBS boEIB, String notifyStatus) throws TechniqueException;
    
    public void updateOrder(BoNotify boNotify, String notifyStatus) throws TechniqueException;
    
    public void updateOtpReinput(BoBS boEIB) throws TechniqueException;

    public BoBSOtp loadOrderForQuery(String orderNo) throws TechniqueException;
    
    public BoOrder loadOrderInfo(String orderNo);
    
}
