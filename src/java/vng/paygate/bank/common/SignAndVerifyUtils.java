/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.bank.common;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base64;
import vng.paygate.domain.common.util.ChecksumGeneration;
import vng.paygate.domain.common.util.DigitalSignature;
import vng.paygate.domain.common.util.SymmetricCipher;
import vng.paygate.domain.exception.TechniqueException;

/**
 *
 * @author trinm2
 */
public class SignAndVerifyUtils {

    
    public static String sign(byte[] data, String keyPath) {
        try {
            String sCR = new String(new byte[]{13});
            String sNL = new String(new byte[]{10});
            //production env
//            PrivateKey privateKey = DigitalSignature.loadPrivateKey(keyPath + ConstantBS.PRIVATE_KEYS_VNG);
            //sandbox env
            PrivateKey privateKey = DigitalSignature.loadPrivateKey(keyPath + ConstantBS.PRIVATE_KEYS_OTP_VNG);
            
            String signatureString64 = DigitalSignature.signDataExt(DigitalSignature.SHA1_WITH_RSA, data, privateKey);
            // log.debug("signatureString64: {}", signatureString64);

            String encryptedString64 = Base64.encodeBase64String(data).replace(sCR, "").replace(sNL, "");
            // log.debug("encryptedString64: {}", encryptedString64);

            String dataSecurity = encryptedString64 + signatureString64;
            // log.debug("dataSecurity: {}", dataSecurity);

            return dataSecurity;
        } catch (Exception ex) {
            ex.printStackTrace();
            // log.error("sign fail: " + ex);
        }
        return null;
    }

    public static byte[] encryptData(String message, String key, String sIV) {
        try {
            byte[] iv = sIV.getBytes("ASCII");
            byte keys[] = ChecksumGeneration.generateMD5Ext(key);
            SecretKey secretKey = SymmetricCipher.generateKey(SymmetricCipher.ALGORITHM_TRIPLEDES, keys);
            byte[] encryptedString = SymmetricCipher.encryptExt(SymmetricCipher.TRANSFORMATION_TRIPLEDES, message, iv, secretKey);
            return encryptedString;
        } catch (TechniqueException ex2) {
            ex2.printStackTrace();
            // log.error("signature fail : " + ex2);
        } catch (UnsupportedEncodingException ex3) {
            ex3.printStackTrace();
            // log.error("signature fail: " + ex3);
        }
        return null;
    }

    public static boolean verify(String dataSecurity, String privateKeyPath) {
        try {
            String encryptedString64 = dataSecurity.substring(0, dataSecurity.length() - 172);
            String signatureString64 = dataSecurity.substring(dataSecurity.length() - 172, dataSecurity.length());
            PublicKey publicKey = DigitalSignature.loadDotNetPublicKey(privateKeyPath + ConstantBS.PUBLIC_KEYS_EIB);
            boolean result = DigitalSignature.verifySigExt(DigitalSignature.SHA1_WITH_RSA, encryptedString64, publicKey, signatureString64);
            // log.debug("verify: {}", result);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            // log.error("verify exception: " + ex);
        }
        return false;
    }

    public static String decrypt(String encrytedString, String key, String sIV) {
        try {
            byte[] iv = sIV.getBytes("ASCII");
            byte keys[] = ChecksumGeneration.generateMD5Ext(key);
            SecretKey secretKey = SymmetricCipher.generateKey(SymmetricCipher.ALGORITHM_TRIPLEDES, keys);
            String decryptedData = SymmetricCipher.decrypt(SymmetricCipher.TRANSFORMATION_TRIPLEDES, encrytedString, iv, secretKey);
//            System.out.println("decryptedData: " + decryptedData);
            return decryptedData;
        } catch (TechniqueException ex) {
            ex.printStackTrace();
            // log.error("decrypt data exception: ", ex);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            // log.error("decrypt data exception: ", ex);
        }
        return null;
    }
}