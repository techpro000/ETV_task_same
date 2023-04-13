package com.etv.util.net;

import android.text.TextUtils;
import android.util.Base64;

import com.etv.config.ApiInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.Deflater;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Constant {

    public static String userId1="10045795";
    public static String userId2="152361";

    public static final String serverIP = "122.112.169.234";
    public static Integer sdkAppID_TEST = 1400692765;
    public static Integer sdkAppID = 1400697982;
    public static String SECRETKEY="8a93534a63793497550a18c7a092c9e6a870eb94d03fb28404768f964ef1b2f7";

    public static String  UserID="";
    public static String  UserSig="";

    public static String  toUserID="";
    public static String  message="Test AAAAAAA";

    private static final int EXPIRETIME = 604800;//超时时间

    public static int getSdkAppId() {
        System.out.println("aaaaaaaaaaaaaaaa---------- " + ApiInfo.getWebIpHost());
        if (ApiInfo.getWebIpHost().contains(Constant.serverIP)) {
            return Constant.sdkAppID_TEST;
        }
        return Constant.sdkAppID;
    }

    //获取签名数据
    public static String genTestUserSig(String userId) {
        return GenTLSSignature(sdkAppID, userId, EXPIRETIME, null, SECRETKEY);
    }

    /**
     * 生成 tls 票据
     *
     * @param sdkappid      应用的 appid
     * @param userId        用户 id
     * @param expire        有效期，单位是秒
     * @param userbuf       默认填写null
     * @param priKeyContent 生成 tls 票据使用的私钥内容
     * @return 如果出错，会返回为空，或者有异常打印，成功返回有效的票据
     */

    /**
     * Generate a TLS ticket
     *
     * @param sdkappid      AppID of the application
     * @param userId        User ID
     * @param expire        Validity period, in seconds
     * @param userbuf       null by default
     * @param priKeyContent Private key required for generating a TLS ticket
     * @return If an error occurs, an empty string will be returned or exceptions printed. If the operation succeeds, a valid ticket will be returned.
     */
    private static String GenTLSSignature(long sdkappid, String userId, long expire, byte[] userbuf, String priKeyContent) {
        if (TextUtils.isEmpty(priKeyContent)) {
            return "";
        }
        long currTime = System.currentTimeMillis() / 1000;
        JSONObject sigDoc = new JSONObject();
        try {
            sigDoc.put("TLS.ver", "2.0");
            sigDoc.put("TLS.identifier", userId);
            sigDoc.put("TLS.sdkappid", sdkappid);
            sigDoc.put("TLS.expire", expire);
            sigDoc.put("TLS.time", currTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String base64UserBuf = null;
        if (null != userbuf) {
            base64UserBuf = Base64.encodeToString(userbuf, Base64.NO_WRAP);
            try {
                sigDoc.put("TLS.userbuf", base64UserBuf);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String sig = hmacsha256(sdkappid, userId, currTime, expire, priKeyContent, base64UserBuf);
        if (sig.length() == 0) {
            return "";
        }
        try {
            sigDoc.put("TLS.sig", sig);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Deflater compressor = new Deflater();
        compressor.setInput(sigDoc.toString().getBytes(Charset.forName("UTF-8")));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return new String(base64EncodeUrl(Arrays.copyOfRange(compressedBytes, 0, compressedBytesLength)));
    }


    private static String hmacsha256(long sdkappid, String userId, long currTime, long expire, String priKeyContent, String base64Userbuf) {
        String contentToBeSigned = "TLS.identifier:" + userId + "\n"
                + "TLS.sdkappid:" + sdkappid + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";
        if (null != base64Userbuf) {
            contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
        }
        try {
            byte[] byteKey = priKeyContent.getBytes("UTF-8");
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            hmac.init(keySpec);
            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes("UTF-8"));
            return new String(Base64.encode(byteSig, Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (InvalidKeyException e) {
            return "";
        }
    }

    private static byte[] base64EncodeUrl(byte[] input) {
        byte[] base64 = new String(Base64.encode(input, Base64.NO_WRAP)).getBytes();
        for (int i = 0; i < base64.length; ++i)
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        return base64;
    }


}
