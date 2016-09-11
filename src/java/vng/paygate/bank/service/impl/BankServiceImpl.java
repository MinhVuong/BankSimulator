package vng.paygate.bank.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import vng.paygate.bank.bo.BoBS;
import vng.paygate.bank.bo.BoBSOtp;
import vng.paygate.bank.bo.BoNotify;
import vng.paygate.bank.dao.ibatis.mapper.BankDaoMapper;
import vng.paygate.bank.service.IBankService;
import vng.paygate.domain.bo.BoOrder;
import vng.paygate.domain.common.Constants;
import vng.paygate.domain.exception.TechniqueException;

/**
 *
 * @author trinm2
 */
public class BankServiceImpl implements IBankService {

    private BankDaoMapper daoMapper;

    public void setDaoMapper(BankDaoMapper daoMapper) {
        this.daoMapper = daoMapper;
    }

    @Override
    public BoOrder verifyPayment(String orderNo) throws TechniqueException {
        Map<String, Object> result = new HashMap<String, Object>();
        BoOrder boOrder = new BoOrder();
        result.put("orderNo", orderNo);
        daoMapper.verifyPayment(result);
        boOrder.setResponseCode(Constants.ERROR_5000);
        List<BoOrder> lstOrder = (List) result.get("lstOrder");
        if (lstOrder != null && !lstOrder.isEmpty()) {
            boOrder = lstOrder.get(0);
            boOrder.setResponseCode(result.get("responseCode").toString());
        }
        return boOrder;
    }

    @Override
    public BoOrder loadOrderVerifyCard(String orderNo) throws TechniqueException {
        Map<String, Object> result = new HashMap<String, Object>();
        BoOrder boOrder = new BoOrder();
        result.put("orderNo", orderNo);
        daoMapper.loadOrderVCard(result);
        
        List<BoOrder> lstOrder = (List) result.get("lstOrder");
        if (lstOrder != null && !lstOrder.isEmpty()) {
            boOrder = lstOrder.get(0);
//            boOrder.setResponseCode(result.get("responseCode").toString());
        }
        if(result.get("responseCode") == null || 
                StringUtils.isEmpty(result.get("responseCode").toString())){
            boOrder.setResponseCode(Constants.ERROR_5000);
        }else{
            boOrder.setResponseCode(result.get("responseCode").toString());
        }
        
        return boOrder;
    }
    
    @Override
    public BoBSOtp loadOrderVerifyOTP(String orderNo) throws TechniqueException {
        Map<String, Object> result = new HashMap<String, Object>();
        BoBSOtp boOrder = new BoBSOtp();
        result.put("orderNo", orderNo);
        daoMapper.loadOrderVOTP(result);
        List<BoBSOtp> lstOrder = (List) result.get("lstOrder");
        if (lstOrder != null && !lstOrder.isEmpty()) {
            boOrder = lstOrder.get(0);
//            boOrder.setResponseCode(result.get("responseCode").toString());
        }
        if(result.get("responseCode") == null || 
                StringUtils.isEmpty(result.get("responseCode").toString())){
            boOrder.setResponseCode(Constants.ERROR_5000);
        }else{
            boOrder.setResponseCode(result.get("responseCode").toString());
        }
        return boOrder;
    }

    @Override
    public String verifyCard(BoBS boEIB) throws TechniqueException{

        daoMapper.verifyCard(boEIB);

        return ""+boEIB.getResponseCode().intValue();
    }
    
    @Override
    public void updateNotify(BoBS boEIB, String notifyStatus) throws TechniqueException {
        if (notifyStatus.equals(Constants.NOTIFY_PENDING)) {
            daoMapper.updateNotifyPending(boEIB);
        } else if (notifyStatus.equals(Constants.NOTIFY_SUCCESS)) {
            daoMapper.updateNotifySuccess(boEIB);
        } else if (notifyStatus.equals(Constants.NOTIFY_FAIL)) {
            daoMapper.updateNotifyFail(boEIB);
        } else {
            boEIB.setResponseCode(Integer.valueOf(Constants.ERROR_5000));
        }
    }
    @Override
    public void updateOtpReinput(BoBS boEIB) throws TechniqueException {
        daoMapper.updateOtpReinput(boEIB);
    }

    @Override
    public BoBSOtp loadOrderForQuery(String orderNo) throws TechniqueException {
        Map<String, Object> result = new HashMap<String, Object>();
        BoBSOtp boOrder = new BoBSOtp();
        result.put("orderNo", orderNo);
        daoMapper.loadOrderForQuery(result);
        boOrder.setResponseCode(Constants.ERROR_5000);
        List<BoBSOtp> lstOrder = (List) result.get("lstOrder");
        if (lstOrder != null && !lstOrder.isEmpty()) {
            boOrder = lstOrder.get(0);
            boOrder.setResponseCode(result.get("responseCode").toString());
        }
        return boOrder;
    }

    @Override
    public void updateOrder(BoNotify boNotify, String notifyStatus) throws TechniqueException {
        if (notifyStatus.equals(Constants.NOTIFY_SUCCESS)) {
            daoMapper.updateOrderSuccess(boNotify);
        } else if (notifyStatus.equals(Constants.NOTIFY_FAIL)) {
            daoMapper.updateOrderFail(boNotify);
        } else {
            boNotify.setResponseCode(Integer.valueOf(Constants.ERROR_5000));
        }
    }

    @Override
    public BoOrder loadOrderInfo(String orderNo) {
        BoOrder boOrder = new BoOrder();
        Map<String, Object> result = new HashMap<String, Object>();
        boOrder.setOrderNo(orderNo);
        result.put("orderNo", orderNo);
        daoMapper.getOrderInfo(result);
        Integer responseCode = (Integer) result.get("responseCode");
        if (responseCode.intValue() == 1) {
            // get order success
            List<BoOrder> lstOrder = (List) result.get("lstOrder");
            if (lstOrder != null && !lstOrder.isEmpty()) {
                boOrder = lstOrder.get(0);
            } else {
                responseCode = Integer.parseInt(Constants.RESPONSE_CODE_NO_DATA_FOUND_IN_DB);
            }
        }
        boOrder.setResponseCode(Integer.toString(responseCode.intValue()));

        return boOrder;
    }
    
}
