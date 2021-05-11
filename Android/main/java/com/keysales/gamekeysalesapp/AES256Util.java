package com.keysales.gamekeysalesapp;

import android.util.Log;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;

import org.apache.commons.codec.binary.Base64;

public class AES256Util {

    private String AES = "AES";
    private volatile AES256Util INSTANCE;

    //암호화에 사용할 비밀키를 입력한다.
    final String secretKey   = "g7iIUaTrz3DJyT3UE2mLQIEC4U97ufMG"; //32bit
    String  IV = secretKey.substring(0,16);

    public AES256Util getInstance() {
        if(INSTANCE==null){
            synchronized(AES256Util.class){
                if(INSTANCE==null)
                    INSTANCE=new AES256Util();
            }
        }
        return INSTANCE;
    }

    //암호화
    public String AES_Encode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        Log.d(AES, "암호화 시작합니다. 문자열 : " + str);
        byte[] keyData = secretKey.getBytes();

        SecretKey secureKey = new SecretKeySpec(keyData, "AES");

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes(Charset.forName("UTF-8"))));

        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        String enStr = new String(Base64.encodeBase64(encrypted));

        Log.d(AES, "암호화 완료했습니다. 결과 : " + enStr);
        return enStr;
    }

    //복호화
    public String AES_Decode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        Log.d(AES, "복호화 시작합니다. 문자열 : " + str);
        byte[] keyData = secretKey.getBytes();
        SecretKey secureKey = new SecretKeySpec(keyData, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes(Charset.forName("UTF-8"))));

        byte[] byteStr = Base64.decodeBase64(str.getBytes());

        Log.d(AES, "복호화 완료했습니다. 문자열 : " + new String(c.doFinal(byteStr),"UTF-8"));
        return new String(c.doFinal(byteStr),"UTF-8");
    }
}