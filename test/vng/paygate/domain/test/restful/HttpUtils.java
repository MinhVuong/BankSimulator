/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vng.paygate.domain.test.restful;

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.SecretKey;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import vng.paygate.domain.bo.BoBaseResponse;
import vng.paygate.domain.common.util.CustomRestClient;
import vng.paygate.domain.common.util.RC4;
import vng.paygate.domain.exception.TechniqueException;

/**
 *
 * @author trieunv
 */
public class HttpUtils {

    /**
     * Call an restful web service via POST method. Parameter is passed in JSON
     * format and is return as an instance of BoBaseResponse
     *
     * @param <T>
     * @param url
     * @param paramterValue An object contains value to post to the destination
     * web service
     * @return
     * @throws TechniqueException
     */
    public static <T extends BoBaseResponse> T invokeRestfulUrl(String url, Map<String, String> values, Class<T> clazz) throws TechniqueException {
        try {
            Gson gson = new Gson();
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(values));

//            if (response.getStatus() != 201) {
//                throw new TechniqueException("Failed : HTTP error code : " + response.getStatus());
//            }
            String output = response.getEntity(String.class);
            System.out.println(output);
            T returrObject = gson.fromJson(output, clazz);
            System.out.println("RETURN OBJECT : " + returrObject.toString());
            return returrObject;
        } catch (Exception e) {

            throw new TechniqueException("Error while invoke : " + url, e);
        }
    }

    public static void main(String[] s) throws Exception {
        Gson gson = new Gson();
//        notify
//        Map m = new HashMap();
//        m.put("command","notify");
//        m.put("tranxId", "fee396d4c4e0ccbb9f058aaf0adcd226");
//        m.put("responseCode","00");
//        m.put("checksum","e7d5dbb4575da588a498ca9d84cee4047f1e2f81");
//        String output = gson.toJson(m);
//        System.out.println("SDJKFJDS : " + output);
//         BoBaseResponse vv = invokeRestfulUrl("http://localhost:8080/BankNet/internal/notify", m, BoBaseResponse.class);
//         System.out.println("fdjglkfdd : "  + vv);

        //         queryBOrder
//        Map m = new HashMap();
//        m.put("tranxId", "123P1209250000427");
//        m.put("checksum","85e302e38d6d31a7bd3bced1be42602c73cc8dc9");
//        String output = gson.toJson(m);
//        System.out.println("SDJKFJDS : " + output);
//         BoBaseResponse vv = invokeRestfulUrl("http://localhost:8080/BankNet/internal/queryBOrder", m, BoBaseResponse.class);
//         System.out.println("fdjglkfdd : "  + vv);
//         verify payment
//        Map m = new HashMap();
//        m.put("orderNo","123P1210080000432");
//        String checksum = generateChecksum("123P1210080000432" + "b", "SHA1");
//        m.put("checksum",checksum);
//        String output = gson.toJson(m);
//        System.out.println("SDJKFJDS : " + output);
//        CustomRestClient client = new CustomRestClient();
//        client.setWebResource("http://localhost:3355/EIB/internal/verifyPayment");
//        BoBaseResponse vv = client.post(m, BoBaseResponse.class);
//         System.out.println("fdjglkfdd : "  + vv); 
        ////////////////
//        String result = "sagsdfgdf|";
//        String response[] = StringUtils.split(result, "|");
//        if (StringUtils.isEmpty(result)) {
//            System.out.println("------");
//            return;
//        }
//        if (response == null || response.length == 2) {
//            System.out.println("aaa");
//        }
//        if (response == null || response.length != 3) {
//            System.out.println("bbb");
//        }
//        System.out.println("ccc");
        // verify card
//       Map m = new HashMap();
//       m.put("orderNo","123P1304040005296");
//       m.put("bankCode","SMARTLINK");
//       String checksum = generateChecksum("123P1304040005296" + "b", "SHA1");
//       m.put("checksum",checksum);
//       String output = gson.toJson(m);
//       System.out.println("SDJKFJDS : " + output);
//       BoBaseResponse vv = invokeRestfulUrl("http://10.30.17.31:8887/BankSimulator/internal/notify", m, BoBaseResponse.class);
//       System.out.println("fdjglkfdd : "  + vv);
//        Map m = new HashMap();
//        m.put("cardNo", "1111111111117201");
//        m.put("cardHolderName", "NGUYEN VAN A");
//        BoBaseResponse vv = invokeRestfulUrl("http://10.30.17.31:8887/BankSimulator/internal/test", m, BoBaseResponse.class);
//        System.out.println("response : " + vv.getDetailResponseCode());

        /* String test = "9704310000310003";
        String result = test.substring(0,12);
        
       System.out.println("fdjglkfdd : "  + result);*/
//        String cardInfo = "1234111111119999=TEST TEST";
        String cardInfo = "1111111111117212=NGUYEN VAN A";
//        String cardInfo = "9704310000310000=pham thi kim ngan";
//        String cardInfo = "9704310000310000=PHAM THI KIM NGAN";
        String rc4SecretKey = "This is 16 bytes";
        SecretKey secretKey = RC4.generateKey(rc4SecretKey);
        String encrypCardInfo = RC4.encrypt(cardInfo, secretKey);
        Map m = new HashMap();
        m.put("orderNo", "123P1606240182673");// change orderno
        m.put("cardInfo", encrypCardInfo);
        m.put("clientIp", "10.79.13.29");

        String rawData = "";
        Map<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(m);
        for (String item : treeMap.keySet()) {
            if (!StringUtils.isEmpty(item)) {
                rawData += treeMap.get(item);
            }
        }
        System.out.println(rawData);
        String checksum = generateChecksum(rawData + "b", "SHA1");
        System.out.println("checksum " + checksum);
        m.put("checksum", checksum);
        String output = gson.toJson(m);
        System.out.println("output : " + output);

//        BoBaseResponse vv = invokeRestfulUrl("http://banksim.sandbox.123pay.vn/BankSimulator/internal/verifyCard", m, BoBaseResponse.class);
        BoBaseResponse vv = invokeRestfulUrl("http://10.30.17.31:8887/BankSimulator/internal/verifyCard", m, BoBaseResponse.class);
        System.out.println("response : " + vv);
    }

    public static String generateChecksum(String message, String algorithm) {
        String checksum = "";

//        if (StringUtils.isBlank(message)) {
//            log.info("Message to create checksum empty");
//            return checksum;
//        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(message.getBytes("UTF-8"));
            byte s[] = digest.digest();
            for (int i = 0; i < s.length; i++) {
                checksum += Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6);
            }
        } catch (NoSuchAlgorithmException ex) {
//            log.error("Can't create checksum", ex);
        } catch (UnsupportedEncodingException ex1) {
//            log.error("Can't create checksum", ex1);
        } finally {
            return checksum;
        }
    }
}
