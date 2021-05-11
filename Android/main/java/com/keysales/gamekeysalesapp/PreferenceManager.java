package com.keysales.gamekeysalesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class PreferenceManager {
    public static final String PREFERENCES_NAME = "rebuild_preference";

    private static final String DEFAULT_VALUE_STRING = null;

    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final int DEFAULT_VALUE_MONEY = 5000;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    public static final String AES = "AES";


    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * String 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * boolean 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * int 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * long 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setLong(Context context, String key, long value) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * float 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setFloat(Context context, String key, float value) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * String 값 로드
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

    /**
     * boolean 값 로드
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
        return value;
    }

    /**
     * int 값 로드
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        int value;
        if (key == "MONEY") {
            value = prefs.getInt(key, DEFAULT_VALUE_MONEY);
        } else {
            value = prefs.getInt(key, DEFAULT_VALUE_INT);
        }
        return value;
    }


    /**
     * long 값 로드
     * @param context
     * @param key
     * @return
     */
    public static long getLong(Context context, String key) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        long value = prefs.getLong(key, DEFAULT_VALUE_LONG);
        return value;
    }

    /**
     * float 값 로드
     * @param context
     * @param key
     * @return
     */

    public static float getFloat(Context context, String key) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        float value = prefs.getFloat(key, DEFAULT_VALUE_FLOAT);
        return value;
    }

    /**
     * 키 값 삭제
     * @param context
     * @param key
     */
    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }

    /**
     * 모든 저장 데이터 삭제
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    // 이메일 보내기 / 테스트
    public static boolean sendEmail(Context mContext, String sendid, String subject, String message) {
        String id = PreferenceManager.getString(mContext, "ID");
        String pw = PreferenceManager.getStringAES(mContext, "PW");

        boolean type = true;
        if (sendid == "") {
            sendid = id;
            type = false;
        }

        try {
            GMailSender gMailSender = new GMailSender(id, pw);

            //GMailSender.sendMail(제목, 내용, 받는사람);
            gMailSender.sendMail(subject, message, sendid, type);

            return true; // 이메일 보내기 성공 시 true 반환
        } catch (Exception e) {

            e.printStackTrace();
            return false; // 이메일 보내기 실패 시 false 반환
        }
    }

    // 이메일 계정 등록
    public static void setEmail (Context mContext, String ID, String PW) {
        PreferenceManager.setString(mContext, "ID", ID);
        PreferenceManager.setStringAES(mContext, "PW", PW);
        PreferenceManager.setBoolean(mContext, "EMAIL", true);
    }

    // 이메일 삭제
    public static void clearEmail (Context mContext) {
        PreferenceManager.setString(mContext, "ID", DEFAULT_VALUE_STRING);
        PreferenceManager.setString(mContext, "PW", DEFAULT_VALUE_STRING);
        PreferenceManager.setBoolean(mContext, "EMAIL", DEFAULT_VALUE_BOOLEAN);
    }

    // DB 등록
    public static void setDB (Context mContext, String add, String user, String pw, String name) {
        PreferenceManager.setString(mContext, "DB_ADD", add);
        PreferenceManager.setString(mContext, "DB_ID", user);
        PreferenceManager.setStringAES(mContext, "DB_PW", pw);
        PreferenceManager.setString(mContext, "DB_NAME", name);
        PreferenceManager.setBoolean(mContext, "DB", true);
    }

    // DB 삭제
    public static void clearDB (Context mContext) {
        PreferenceManager.setString(mContext, "DB_ADD", DEFAULT_VALUE_STRING);
        PreferenceManager.setString(mContext, "DB_ID", DEFAULT_VALUE_STRING);
        PreferenceManager.setString(mContext, "DB_PW", DEFAULT_VALUE_STRING);
        PreferenceManager.setString(mContext, "DB_NAME", DEFAULT_VALUE_STRING);
        PreferenceManager.setBoolean(mContext, "DB", DEFAULT_VALUE_BOOLEAN);
    }

    public static String getStringAES (Context context, String key) {
        SharedPreferences prefs = (SharedPreferences) getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);

        try {
            AES256Util decode = new AES256Util();
            value = decode.AES_Decode(value);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            Log.d(AES, "비멀번호 복호화에 실패했습니다.");
        }

        return value;
    }

    public static void setStringAES (Context mContext, String key, String pw) {

        try {
            AES256Util encode = new AES256Util();
            pw = encode.AES_Encode(pw);
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
            Log.d(AES, "비멀번호 암호화에 실패했습니다.");
            e.printStackTrace();
            pw = e.toString();
        }

        PreferenceManager.setString(mContext, key, pw);
    }
}