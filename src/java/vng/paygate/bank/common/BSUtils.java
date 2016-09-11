/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.common;

import com.sun.xml.bind.marshaller.XMLWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import vng.paygate.bank.bo.*;
import vng.paygate.bank.jaxb.adapter.BoBIModuleConfigNew;
import vng.paygate.config.service.IConfigService;

/**
 *
 * @author trinm2
 */
public class BSUtils {

    private static String mtiSale;
    private static String mtiReversal;
    private static String mtiVoid;
    private static String mtiInquiry;
    private static String processingCodeSale;
    private static String processingCodeReversal;
    private static String processingCodeVoid;
    private static String processingCodeInquiry;
    private static String terminalId;
    private static String merchantId;

    public void init(IConfigService<BoBIModuleConfigNew> configService, String merchantCode) {

        /*mtiSale = boConfig.getMtiRequestSale();
        processingCodeSale = boConfig.getProcessingCodeSale();

        mtiReversal = boConfig.getMtiRequestReversal();
        processingCodeReversal = boConfig.getProcessingCodeReversal();

        mtiVoid = boConfig.getMtiRequestVoid();
        processingCodeVoid = boConfig.getProcessingCodeVoid();

        mtiInquiry = boConfig.getMtiRequestInquiry();
        processingCodeInquiry = boConfig.getProcessingCodeInquiry();*/

        //if merchant has its own TID,MID then load from properties
        List lstId = loadSpecialMerchant(merchantCode, configService);
        if (lstId != null) {
            merchantId = (String)lstId.get(0);
            terminalId = (String)lstId.get(1);
        } else {
           /* merchantId = boConfig.getMerchantId();
            terminalId = boConfig.getTerminalId();*/
        }
    }

    /**
     *
     * @param message
     * @return
     */
    public String convertBoMessage2String(BoBSMessage message) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BoBSMessage.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter strWriter = new StringWriter();
            XMLWriter result = new XMLWriter(strWriter, "utf-8");
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(message, result);
//            System.out.println(strWriter.toString());
            return strWriter.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param message
     * @return
     */
    public BoBSMessage convertString2EIBMessageObject(String message) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BoBSMessage.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(message);

            BoBSMessage eibMessage = (BoBSMessage) unmarshaller.unmarshal(reader);
            return eibMessage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public BoBSMessage createSaleMessage(BoBS eib) {
        BoBSMessage boMessage = new BoBSMessage();
        boMessage.setName(ConstantBS.REQUEST_MESSAGE_SALE);

        List<Field> lstField = new ArrayList<Field>();
        //field 0
        Field field = new Field();
        field.setId("0");
        if (StringUtils.isEmpty(mtiSale)) {
//            System.out.println("MTI is invalid");
            return null;
        }
        field.setValue(mtiSale);
        lstField.add(field);

        //field 2
        field = new Field();
        field.setId("2");
//        try {
//            decryptCard(eib);
//        } catch (TechniqueException ex) {
//            log.error("Invalid card info: ", ex);
//            return null;
//        }
        String cardNumber = eib.getCardNo().trim();
        int length = cardNumber.length();
        if (length > 99) {
//            System.out.println("Invalid card number id: " + cardNumber);
            return null;
        }
        String strLength = String.format("%02d%n", length).trim();
        field.setValue(strLength + cardNumber);
        lstField.add(field);

        //field 3
        field = new Field();
        field.setId("3");
//        String processingCode = "";
//        try {
//            processingCode = FileUtils.loadEIBValue("eib.pay.processingCode.sale", this.getClass());
//        } catch (TechniqueException ex) {
//            log.error("load mti request sale is fail: ", ex);
//            return null;
//        }
        if (StringUtils.isEmpty(processingCodeSale)) {
//            System.out.println("Processing code is invalid");
            return null;
        }
        field.setValue(processingCodeSale.trim());
        lstField.add(field);

        //field 4
        field = new Field();
        field.setId("4");
        double amount = eib.getAmount();
        String amountFormated = String.format("%012.0f%n", amount * 100).trim();
        field.setValue(amountFormated);
        lstField.add(field);

        //field 11
        field = new Field();
        field.setId("11");
        //order num

//        String field11 = StringUtils.removeStart(eib.getTransactionId(), "123P");
        field.setValue(RandomStringUtils.randomNumeric(6));
        lstField.add(field);

        //field 12
        field = new Field();
        field.setId("12");
        Date date = new Date();
        Format formatter = new SimpleDateFormat("hhmmss");
        String s = formatter.format(date);
        field.setValue(s);
        lstField.add(field);

        //field 13
        field = new Field();
        field.setId("13");
        formatter = new SimpleDateFormat("ddMM");
        String d = formatter.format(date);
        field.setValue(d);
        lstField.add(field);

        //field 14
        field = new Field();
        field.setId("14");
        String strDate;
        if (eib.getExpireDate() != null) {
            Date dateExpiration = eib.getExpireDate();
            formatter = new SimpleDateFormat("yyMM");
            strDate = formatter.format(dateExpiration);
        } else {
            strDate = "4912";
        }
        field.setValue(strDate);
        lstField.add(field);

        //field 22
        field = new Field();
        field.setId("22");
        field.setValue("012");
        lstField.add(field);

        //field 25
        field = new Field();
        field.setId("25");
        field.setValue("00");
        lstField.add(field);

        //field 41
        field = new Field();
        field.setId("41");
//        String terminalId = "";
//        try {
//            terminalId = FileUtils.loadEIBValue("eib.pay.terminalID", this.getClass());
//        } catch (TechniqueException ex) {
//            log.error("load terminal ID is fail: ", ex);
//            return null;
//        }
        if (StringUtils.isEmpty(terminalId)) {
//            System.out.println("Terminal ID is invalid");
            return null;
        }
        field.setValue(terminalId);
        lstField.add(field);

        //field 42
        field = new Field();
        field.setId("42");
//        String merchantId = "";
//        try {
//            merchantId = FileUtils.loadEIBValue("eib.pay.merchantID", this.getClass());
//        } catch (TechniqueException ex) {
//            log.error("load terminal ID is fail: ", ex);
//            return null;
//        }
        if (StringUtils.isEmpty(merchantId)) {
//            System.out.println("Terminal ID is invalid");
            return null;
        }
        field.setValue(merchantId);
        lstField.add(field);

        //field 62
        field = new Field();
        field.setId("62");
        String vngTransId = eib.getTransactionId();
        vngTransId = StringUtils.removeStart(vngTransId, "123P");
        int len = vngTransId.length();
        if (len > 999) {
//            System.out.println("Invalid transaction id: " + vngTransId);
            return null;
        }
        String strLen = String.format("%03d%n", len).trim();
        field.setValue(strLen + vngTransId);
        lstField.add(field);

        //field 102
        field = new Field();
        field.setId("102");
        String otp = eib.getOtp().trim();
        int l = otp.length();
        if (l > 999) {
//            System.out.println("Invalid card number id: " + otp);
            return null;
        }
        String strLeng = String.format("%03d%n", l).trim();
        field.setValue(strLeng + otp);
        lstField.add(field);

        boMessage.setLstField(lstField);

        return boMessage;
    }

    public BoBSMessage createReversalMessage(BoBS eib) {
        BoBSMessage boMessage = new BoBSMessage();
        boMessage.setName(ConstantBS.REQUEST_MESSAGE_REVERSAL);

        List<Field> lstField = new ArrayList<Field>();
        //field 0
        Field field = new Field();
        field.setId("0");
        if (StringUtils.isEmpty(mtiReversal)) {
//            System.out.println("MTI is invalid");
            return null;
        }
        field.setValue(mtiReversal);
        lstField.add(field);

        //field 2
        field = new Field();
        field.setId("2");
        String cardNumber = eib.getCardNo().trim();
        int length = cardNumber.length();
        if (length > 99) {
//            System.out.println("Invalid card number id: " + cardNumber);
            return null;
        }
        String strLength = String.format("%02d%n", length).trim();
        field.setValue(strLength + cardNumber);
        lstField.add(field);

        //field 3
        field = new Field();
        field.setId("3");
        if (StringUtils.isEmpty(processingCodeReversal)) {
//            System.out.println("Processing code is invalid");
            return null;
        }
        field.setValue(processingCodeReversal.trim());
        lstField.add(field);

        //field 4
        field = new Field();
        field.setId("4");
        double amount = eib.getAmount();
        String amountFormated = String.format("%012.0f%n", amount * 100).trim();
        field.setValue(amountFormated);
        lstField.add(field);

        //field 11
        field = new Field();
        field.setId("11");
        //order num

        field.setValue(RandomStringUtils.randomNumeric(6));
        lstField.add(field);

        //field 12
        field = new Field();
        field.setId("12");
        Date date = new Date();
        Format formatter = new SimpleDateFormat("hhmmss");
        String s = formatter.format(date);
        field.setValue(s);
        lstField.add(field);

        //field 13
        field = new Field();
        field.setId("13");
        formatter = new SimpleDateFormat("ddMM");
        String d = formatter.format(date);
        field.setValue(d);
        lstField.add(field);

        //field 14
        field = new Field();
        field.setId("14");
        String strDate;
        if (eib.getExpireDate() != null) {
            Date dateExpiration = eib.getExpireDate();
            formatter = new SimpleDateFormat("yyMM");
            strDate = formatter.format(dateExpiration);
        } else {
            strDate = "4912";
        }
        field.setValue(strDate);
        lstField.add(field);

        //field 22
        field = new Field();
        field.setId("22");
        field.setValue("012");
        lstField.add(field);

        //field 25
        field = new Field();
        field.setId("25");
        field.setValue("00");
        lstField.add(field);

        //field 37
        field = new Field();
        field.setId("37");
//        String refNo = eib.getRefNo();
//        if (StringUtils.isEmpty(refNo)) {
//            log.info("Ref no is invalid");
//            return null;
//        }
        field.setValue(StringUtils.defaultIfEmpty(eib.getRefNo(), ""));
        lstField.add(field);

        //field 41
        field = new Field();
        field.setId("41");

        if (StringUtils.isEmpty(terminalId)) {
//            System.out.println("Terminal ID is invalid");
            return null;
        }
        field.setValue(terminalId);
        lstField.add(field);

        //field 42
        field = new Field();
        field.setId("42");

        if (StringUtils.isEmpty(merchantId)) {
//            System.out.println("Terminal ID is invalid");
            return null;
        }
        field.setValue(merchantId);
        lstField.add(field);

        //field 62
        field = new Field();
        field.setId("62");
        String vngTransId = eib.getTransactionId();
        vngTransId = StringUtils.removeStart(vngTransId, "123P");
        int len = vngTransId.length();
        if (len > 999) {
//            System.out.println("Invalid transaction id: " + vngTransId);
            return null;
        }
        String strLen = String.format("%03d%n", len).trim();
        field.setValue(strLen + vngTransId);
        lstField.add(field);

        boMessage.setLstField(lstField);

        return boMessage;

    }

    public BoBSMessage createVoidMessage(BoBS eib) {
        BoBSMessage boMessage = new BoBSMessage();
        boMessage.setName(ConstantBS.REQUEST_MESSAGE_VOID);

        List<Field> lstField = new ArrayList<Field>();
        //field 0
        Field field = new Field();
        field.setId("0");
        if (StringUtils.isEmpty(mtiVoid)) {
//            System.out.println("MTI is invalid");
            return null;
        }
        field.setValue(mtiVoid);
        lstField.add(field);

        //field 2
        field = new Field();
        field.setId("2");
        String cardNumber = eib.getCardNo().trim();
        int length = cardNumber.length();
        if (length > 99) {
//            System.out.println("Invalid card number id: " + cardNumber);
            return null;
        }
        String strLength = String.format("%02d%n", length).trim();
        field.setValue(strLength + cardNumber);
        lstField.add(field);

        //field 3
        field = new Field();
        field.setId("3");
        if (StringUtils.isEmpty(processingCodeVoid)) {
//            System.out.println("Processing code is invalid");
            return null;
        }
        field.setValue(processingCodeVoid.trim());
        lstField.add(field);

        //field 4
        field = new Field();
        field.setId("4");
        double amount = eib.getAmount();
        String amountFormated = String.format("%012.0f%n", amount * 100).trim();
        field.setValue(amountFormated);
        lstField.add(field);

        //field 11
        field = new Field();
        field.setId("11");
        //order num

        field.setValue(RandomStringUtils.randomNumeric(6));
        lstField.add(field);

        //field 12
        field = new Field();
        field.setId("12");
        Date date = new Date();
        Format formatter = new SimpleDateFormat("hhmmss");
        String s = formatter.format(date);
        field.setValue(s);
        lstField.add(field);

        //field 13
        field = new Field();
        field.setId("13");
        formatter = new SimpleDateFormat("ddMM");
        String d = formatter.format(date);
        field.setValue(d);
        lstField.add(field);

        //field 14
        field = new Field();
        field.setId("14");
        String strDate;
        if (eib.getExpireDate() != null) {
            Date dateExpiration = eib.getExpireDate();
            formatter = new SimpleDateFormat("yyMM");
            strDate = formatter.format(dateExpiration);
        } else {
            strDate = "4912";
        }
        field.setValue(strDate);
        lstField.add(field);

        //field 22
        field = new Field();
        field.setId("22");
        field.setValue("012");
        lstField.add(field);

        //field 25
        field = new Field();
        field.setId("25");
        field.setValue("00");
        lstField.add(field);

        //field 37
        field = new Field();
        field.setId("37");
        String refNo = eib.getRefNo();
//        if (StringUtils.isEmpty(refNo)) {
////            System.out.println("Ref no is invalid");
////            return null;
//        }
        field.setValue("222707009517");
        lstField.add(field);

        //field 41
        field = new Field();
        field.setId("41");

        if (StringUtils.isEmpty(terminalId)) {
//            System.out.println("Terminal ID is invalid");
            return null;
        }
        field.setValue(terminalId);
        lstField.add(field);

        //field 42
        field = new Field();
        field.setId("42");

        if (StringUtils.isEmpty(merchantId)) {
//            System.out.println("Terminal ID is invalid");
            return null;
        }
        field.setValue(merchantId);
        lstField.add(field);

        //field 62
        field = new Field();
        field.setId("62");
        String vngTransId = eib.getTransactionId();
        vngTransId = StringUtils.removeStart(vngTransId, "123P");
        int len = vngTransId.length();
        if (len > 999) {
//            System.out.println("Invalid transaction id: " + vngTransId);
            return null;
        }
        String strLen = String.format("%03d%n", len).trim();
        field.setValue(strLen + vngTransId);
        lstField.add(field);

        boMessage.setLstField(lstField);

        return boMessage;
    }

    public BoBSMessage createInquiryMessage(BoBS eib) {
        BoBSMessage boMessage = new BoBSMessage();
        boMessage.setName(ConstantBS.REQUEST_MESSAGE_INQUIRY);

        List<Field> lstField = new ArrayList<Field>();
        //field 0
        Field field = new Field();
        field.setId("0");
        if (StringUtils.isEmpty(mtiInquiry)) {
//            System.out.println("MTI is invalid");
            return null;
        }
        field.setValue(mtiInquiry);
        lstField.add(field);

        //field 2
        field = new Field();
        field.setId("2");
        String cardNumber = eib.getCardNo().trim();
        int length = cardNumber.length();
        if (length > 99) {
//            System.out.println("Invalid card number id: " + cardNumber);
            return null;
        }
        String strLength = String.format("%02d%n", length).trim();
        field.setValue(strLength + cardNumber);
        lstField.add(field);

        //field 3
        field = new Field();
        field.setId("3");
        if (StringUtils.isEmpty(processingCodeInquiry)) {
//            System.out.println("Processing code is invalid");
            return null;
        }
        field.setValue(processingCodeInquiry.trim());
        lstField.add(field);

        //field 11
        field = new Field();
        field.setId("11");
        //order num

        field.setValue(RandomStringUtils.randomNumeric(6));
        lstField.add(field);

        //field 12
        field = new Field();
        field.setId("12");
        Date date = new Date();
        Format formatter = new SimpleDateFormat("hhmmss");
        String s = formatter.format(date);
        field.setValue(s);
        lstField.add(field);

        //field 13
        field = new Field();
        field.setId("13");
        formatter = new SimpleDateFormat("ddMM");
        String d = formatter.format(date);
        field.setValue(d);
        lstField.add(field);

        //field 22
        field = new Field();
        field.setId("22");
        field.setValue("012");
        lstField.add(field);

        //field 25
        field = new Field();
        field.setId("25");
        field.setValue("00");
        lstField.add(field);

        //field 38
        field = new Field();
        field.setId("38");
        String approveCode = eib.getApprovalCode();
        if (StringUtils.isEmpty(approveCode)) {
//            System.out.println("Approval Code is invalid");
            approveCode = "";
//            return null;
        }
        field.setValue(approveCode);

        //field 41
        field = new Field();
        field.setId("41");

        if (StringUtils.isEmpty(terminalId)) {
//            System.out.println("Terminal ID is invalid");
            return null;
        }
        field.setValue(terminalId);
        lstField.add(field);

        //field 42
        field = new Field();
        field.setId("42");

        if (StringUtils.isEmpty(merchantId)) {
//            System.out.println("Terminal ID is invalid");
            return null;
        }
        field.setValue(merchantId);
        lstField.add(field);

        //field 62
        field = new Field();
        field.setId("62");
        String vngTransId = eib.getTransactionId();
        vngTransId = StringUtils.removeStart(vngTransId, "123P");
        int len = vngTransId.length();
        if (len > 999) {
//            System.out.println("Invalid transaction id: " + vngTransId);
            return null;
        }
        String strLen = String.format("%03d%n", len).trim();
        field.setValue(strLen + vngTransId);
        lstField.add(field);

        boMessage.setLstField(lstField);

        return boMessage;
    }

    private List loadSpecialMerchant(String merchantCode, IConfigService<BoBIModuleConfigNew> configService) {

        String data = configService.getModuleConfig().getProperties().getProperty("config.merchant.special.id");
        if (data == null || "".equals(data)) {
            return null;
        }

        JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(data);
        if (jsonObject == null) {
            return null;
        }

        Map<String, Object> mapSpecialId = (Map<String, Object>) JSONObject.toBean(jsonObject, Map.class);
        if (mapSpecialId == null || mapSpecialId.isEmpty()) {
            return null;
        }

        if (mapSpecialId.containsKey(merchantCode)) {
            List<String> lstId = (List<String>) mapSpecialId.get(merchantCode);
            if (lstId.isEmpty() || lstId.size() != 2) {
                return null;
            }

            //mid,tid
//            boConfig.setMerchantId(lstId.get(0));
//            boConfig.setTerminalId(lstId.get(1));
            return lstId;
        }
        return null;
    }
}
