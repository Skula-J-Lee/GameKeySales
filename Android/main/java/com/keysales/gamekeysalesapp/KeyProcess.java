package com.keysales.gamekeysalesapp;

import android.os.AsyncTask;
import android.telephony.SmsMessage;
import android.util.Log;

import com.keysales.gamekeysalesapp.ui.main.ListViewItemGamekey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class KeyProcess {

    // 로그 태그
    private String SMSTAG = "SmsReceiver";
    private String DBTAG = "DBLink";
    private String AES = "AES";


    // 날짜 포맷
    private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");

    public String[] array_SMS = new String[6];
    public String[] array_Insert = new String[18];
    public String[] array_Email = new String[8];
    public String[] array_Game = new String[11];
    public String[] array_Gamekey = new String[15];    // 게임 키 재 전송 시 사용

    public ArrayList<PersonalData> DBListArrayList = new ArrayList<>(); // DB 유저 정보 목록 저장
    public ArrayList<ListViewItemGamekey> GamekeyArrayList = new ArrayList<>(); // 게임 키 목록 저장
    public PersonalData DBPersonalData = new PersonalData(); // 받은 데이터 저장
    public SmsDataItem smsData = new SmsDataItem(); // 문자 추출 저장
    public String[] array_Bank = new String[4]; // 입금 계좌 저장

    public String emailState = null;

    public String dateCount = null;

    // SMS 처리 부분
    public void SmsProcess (SmsMessage[] smsMessages) {

        // 전화번호
        String sender = smsMessages[0].getOriginatingAddress();

        // 문자 내용
        String contents = smsMessages[0].getMessageBody().toString();

        // 수신시간
        Date receivedDate = new Date(smsMessages[0].getTimestampMillis());

        String realyear = yearformat.format(receivedDate);

        // 문자 내용 엔터로 구분
        String[] change_target = contents.split("\\r?\\n");
        for(int i=0;i<change_target.length;i++) {
            switch (i) {
                case 1:
                    // 입금 날짜
                    change_target[i] = change_target[i].substring(change_target[i].lastIndexOf("]")+1);
                    change_target[i] = change_target[i].replace('/','-');
                    change_target[i] = realyear + "-" + change_target[i];
                    smsData.setTextDate(change_target[i]);
                    break;
                case 3:
                    // 문자 내용에서 입금자 명 추출
                    smsData.setTextName(change_target[i]);
                    break;
                case 5:
                    // 문자 내용에서 입금 금액 추출
                    // 문자 온 내용은 콤마(,)를 포함함으로 콤마(,)를 제거하여 숫자형으로 변환
                    smsData.setTextMoney(change_target[i].replaceAll(",",""));
                    break;
                default:
                    break;
            }
        }

        Date time = new Date(); // 현 시간 불러오기

        array_SMS[0] = timeFormat.format(time);
        array_SMS[1] = "입금 문자를 수신하였습니다.";
        array_SMS[2] = smsData.getTextName();
        array_SMS[3] = smsData.getTextDate();
        array_SMS[4] = smsData.getTextMoney();

        // 저장된 설정 금액 불러오기
        int set_money = PreferenceManager.getInt(MainActivity.thisContext, "MONEY");
        int smsMoney = Integer.parseInt(smsData.getTextMoney());

        if (smsMoney == set_money) {
            smsData.setTextState("Y");
            array_SMS[5] = "설정된 금액과 동일합니다.";
        } else if (smsMoney > set_money) {
            smsData.setTextState("O");
            array_SMS[5] = "설정된 금액보다 초과입니다.";
        } else {
            smsData.setTextState("L");
            array_SMS[5] = "설정된 금액보다 미만입니다.";
        }

        String filename = smsData.getTextDate() + "." + smsData.getTextName();
        DBPersonalData.setMember_filename(filename);
    }

    // DB 연동 부분
    public boolean StartDBLink(String DBType, String... insertDate) {
        Log.d(DBTAG, "DB 연동이 시작 됩니다.");

        DBLink task = new DBLink();

        String Site = PreferenceManager.getString(MainActivity.thisContext, "DB_ADD"); // DB 주소 불러오기

        String DBResult = null;

        Date time = new Date();

        Log.d(DBTAG, "DB 접속이 시작됩니다.");

        switch (DBType) {
            case "0":
                // DB 연동 체크
                Log.d(DBTAG, "DB 연동 체크");
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "1":
                // 유저 상태 조회
                Log.d(DBTAG, "유저 상태 조회");
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "2":
                // 게임 키 추가
                Log.d(DBTAG, "게임 키 추가");
                // 게임 키 / DB 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, insertDate[0], DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "3":
                // 이름 대조 및 게임 키 추출
                Log.d(DBTAG, "이름 대조 및 게임 키 추출");

                array_Insert[0] = timeFormat.format(time);
                array_Insert[1] = "유저 업데이트 및 게임 키를 가져오기 위해 DB 연동이 시작됩니다.";

                // 이름 / 입금 날짜 / 입금 성태 / 입금 금액 / DB 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, smsData.getTextName(), smsData.getTextDate(), smsData.getTextState(), smsData.getTextMoney(), DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "4":
                // 유저 상태 및 게임 키 상태 업데이트
                Log.d(DBTAG, "유저 상태 및 게임 키 상태 업데이트");
                array_Game[0] = timeFormat.format(time);
                array_Game[1] = "유저 상태 및 게임 키 상태 업데이트를 위해 DB 연동이 시작됩니다.";

                // 유저 ID / 게임 키 ID / 유저 상태 / 처리 시간 / 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, DBPersonalData.getMember_id(), DBPersonalData.getMember_gamekeyid(), emailState, array_Game[0], DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "5":
                // 게임 키 추가
                array_Gamekey[0] = timeFormat.format(time);
                array_Gamekey[1] = "게임 키를 가져오기 위해 DB 연동이 시작됩니다.";

                Log.d(DBTAG, "게임 키 가져오기");
                // 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "6":
                // 유저 정보 삭제
                Log.d(DBTAG, "유저 정보 삭제");
                // 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, insertDate[0], DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "7":
                // 게임 키 정보 가져오기
                Log.d(DBTAG, "게임 키 정보 가져오기");
                // 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "8":
                // 게임키 삭제
                Log.d(DBTAG, "게임키 삭제");
                // 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, insertDate[0], DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "9":
                // 입금계좌 불러오기
                Log.d(DBTAG, "입금계좌 불러오기");
                // 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "10":
                // 입금계좌 설정
                Log.d(DBTAG, "입금계좌 설정");
                // 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, insertDate[0], insertDate[1], insertDate[2], insertDate[3], DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "11":
                // 금일 목록 가져오기
                Log.d(DBTAG, "금일 목록 가져오기");
                // 연동 타입
                try {
                    DBResult = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Site, DBType).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                DBResult = null;
                Log.d(DBTAG, "연동 타입이 없습니다.");
                Log.d(DBTAG, "DB 접속을 취소합니다.");
                break;
        }

        String[] arrayResult = task.getArrayResult();

        String result = arrayResult[0];
        String error = arrayResult[1];

        for (int i=2; i < arrayResult.length; i++) {
            switch (DBType) {
                case "3":
                    array_Insert[i] = arrayResult[i];
                    break;
                case "4":
                    array_Game[i] = arrayResult[i];
                    break;
                case "5":
                    array_Gamekey[i] = arrayResult[i];
                    break;
            }
        }
        return processResult(result, DBType, error);
    }

    // 결과물 처리하는 곳
    public boolean processResult (String result, String DBType, String errorString) {
        boolean resultState = true;

        if (result != null) {
            // 수신 값이 null 이 아닐 경우
            switch (DBType) {
                case "3":
                    array_Insert[8] = "수신한 데이터 : " + result;
                    break;
                case "4":
                    array_Game[8] = "수신한 데이터 : " + result;
                    break;
                case "5":
                    array_Gamekey[8] = "수신한 데이터 : " + result;
                    break;
            }

            Log.d(DBTAG, "수신한 데이터 : " + result);

            if (result.contains("success")) {
                // 수신한 데이터에 success 단어가 존재 한다면
                if (DBType.equals("3")) {
                    conversionResult(DBType, "Y", result);
                    array_Insert[16] = "수신한 데이터를 처리 완료 했습니다.";
                } else if (DBType.equals("5")) {
                    conversionResult(DBType, "GY", result);
                    array_Gamekey[13] = "수신한 데이터를 처리 완료 했습니다.";
                } else if (DBType.equals("1")) {
                    conversionResult(DBType, "A", result);
                } else if (DBType.equals("7")) {
                    conversionResult(DBType, "AG", result);
                } else if (DBType.equals("9")) {
                    conversionResult(DBType, "AN", result);
                } else if (DBType.equals("11")) {
                    conversionResult(DBType, "D", result);
                }
            } else if (result.equals("")) {
                // 수신한 데이터에 아무것도 없을 경우
                switch (DBType) {
                    case "0":
                    case "1":
                    case "2":
                    case "6":
                    case "8":
                    case "10":
                        Log.d(DBTAG, "완료");
                        break;
                    default:
                        resultState = false;
                        Log.d(DBTAG, "수신한 데이터가 없습니다.");
                        break;
                }
            } else if (result.contains("nothing") && DBType.equals("9")) {

            } else {
                resultState = false;
                // 위 경우가 아닐 경우 (보통은 에러 메시지)
                if (DBType.equals("3")) {
                    // null 값 빈칸으로 처리
                    for (int i=9; i<=15; i++) {
                        if(array_Insert[i] == null) {
                            array_Insert[i] = "";
                        }
                    }
                } else if (DBType.equals("5")) {
                    for (int i=9; i<=12; i++) {
                        if(array_Gamekey[i] == null) {
                            array_Gamekey[i] = "";
                        }
                    }
                }

                if (result.contains("fail(00)")) {
                    // 에러 코드 (00) : DB 연결 실패
                    switch (DBType) {
                        case "3":
                            array_Insert[16] = "에러 메시지 (00) : DB 연결에 실패했습니다.";
                            break;
                        case "4":
                            array_Game[9] = "에러 메시지 (00) : DB 연결에 실패했습니다.";
                            break;
                        case "5":
                            array_Gamekey[13] = "에러 메시지 (00) : DB 연결에 실패했습니다.";
                            break;
                    }

                    Log.d(DBTAG, "DB 연결에 실패했습니다.");

                    if (ForegroundService.isMyServiceRunning) {
                        ForegroundService.stopFGService("DB 연결에 실패하였습니다.", "서비스를 중지합니다.");
                    }

                } else if (result.contains("fail(01)")) {
                    // 에러 코드 (01) : 이름이 없습니다.
                    array_Insert[16] = "에러 메시지 (01) : 이름이 없습니다.";
                    Log.d(DBTAG, "에러를 수신하였습니다.");

                } else if (result.contains("fail(02)")) {
                    // 에러 코드 (02) : 중복된 이름이 있습니다.
                    array_Insert[16] = "에러 메시지 (02) : 중복된 이름이 있습니다.";
                    Log.d(DBTAG, "에러를 수신하였습니다. : 중복된 이름이 있습니다.");
                    ForegroundService.SetNo ("중복된 이름이 있습니다.", "구매자 : " + array_SMS[2]);

                } else if (result.contains("fail(03)")) {
                    // 에러 코드 (03) : 게임 키가 없습니다.
                    Log.d(DBTAG, "에러를 수신하였습니다. : 게임 키가 없습니다.");
                    if (DBType.equals("3")) {
                        conversionResult(DBType, "G", result);
                    } else if (DBType.equals("5")) {
                        array_Gamekey[13] = "에러 메시지 (03) : 게임 키가 없습니다.";
                    }

                } else if (result.contains("fail(04)")) {
                    // 에러 코드 (04) : 돈이 부족합니다.
                    Log.d(DBTAG, "에러를 수신하였습니다. : 돈이 부족합니다.");
                    conversionResult(DBType, "L", result);
                } else {
                    array_Insert[16] = "에러 메시지 (99) : 알수 없는 에러가 발생하였습니다.";
                    Log.d(DBTAG, "에러가 발생하였습니다.");
                }
            }
            switch (DBType) {
                case "3":
                    if (array_Insert[16] == null) {
                        array_Insert[16] = "수신한 데이터를 처리 완료 했습니다.";
                    }
                    array_Insert[17] = "DB 연동을 완료했습니다.";
                    break;
                case "4":
                    if (array_Game[9] == null) {
                        array_Game[9] = "수신한 데이터를 처리 완료 했습니다.";
                    }
                    array_Game[10] = "DB 연동을 완료했습니다.";
                    break;
                case "5":
                    if (array_Gamekey[13] == null) {
                        array_Gamekey[13] = "수신한 데이터를 처리 완료 했습니다.";
                    }
                    array_Gamekey[14] = "DB 연동을 완료했습니다.";
                    break;
            }
            Log.d(DBTAG, "DB 연동을 완료했습니다.");
        } else {
            // 수신 값이 null 일 경우 (인터넷 접속 중 오류 발생)
            resultState = false;
            switch (DBType) {
                case "3":
                    for (int i=6; i<=15; i++) {
                        if(array_Insert[i] == null) {
                            array_Insert[i] = "";
                        }
                        array_Insert[7] = "에러 메시지  : " + errorString;
                        array_Insert[16] = "에러 메시지 (00) : DB 연결에 실패했습니다. (인터넷 접속 실패)";
                    }
                    array_Insert[17] = "DB 연동을 완료했습니다.";
                    break;
                case "4":
                    for (int i=6; i<=9; i++) {
                        if(array_Game[i] == null) {
                            array_Game[i] = "";
                        }
                        array_Game[7] = "에러 메시지  : " + errorString;
                        array_Game[9] = "에러 메시지 (00) : DB 연결에 실패했습니다. (인터넷 접속 실패)";
                    }
                    array_Game[10] = "DB 연동을 완료했습니다.";
                    break;
                case "5":
                    for (int i=6; i<=12; i++) {
                        if(array_Game[i] == null) {
                            array_Game[i] = "";
                        }
                        array_Gamekey[7] = "에러 메시지  : " + errorString;
                        array_Gamekey[13] = "에러 메시지 (00) : DB 연결에 실패했습니다. (인터넷 접속 실패)";
                    }
                    array_Game[10] = "DB 연동을 완료했습니다.";
                    break;
            }

            if (ForegroundService.isMyServiceRunning) {
                ForegroundService.stopFGService("DB 연결에 실패하였습니다.", "서비스를 중지합니다.");
            }
        }
        return resultState;
    }


    // 결과 보여주기
    public void conversionResult (String DBType, String ResultType, String mJsonString) {

        if (DBType.equals("3")) {
            array_Insert[9] = "수신한 데이터를 변환합니다.";
        } else if (DBType.equals("5")) {
            array_Gamekey[9] = "수신한 데이터를 변환합니다.";
        }
        Log.d(DBTAG, "수신한 데이터를 변환합니다.");

        if (ResultType.equals("Y")) {
            // 가져올 아이템 태그 설정
            String TAG_JSON = "success";
            String TAG_ID = "id";
            String TAG_NAME = "name";
            String TAG_EMAIL = "email";
            String TAG_GAMEKEYID = "gamekeyid";
            String TAG_GAMEKEY = "gamekey";

            try {
                // 첫번째 괄호는 중괄호 {} 이므로 JSONObject입니다.
                JSONObject jsonObject = new JSONObject(mJsonString);

                // jsonObject에서 TAG_JSON 키를 갖는 JSONArray를 가져옵니다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // JsonArray에서 JSONObject를 하나씩 가져옵니다.
                for (int i = 0; i < jsonArray.length(); i++) {
                    // 데이터를 새로 생성한 PersonalData 클래스의 멤버변수에 입력하고 ArrayList에 추가합니다.
                    JSONObject item = jsonArray.getJSONObject(i);
                    String id = item.getString(TAG_ID);
                    String name = item.getString(TAG_NAME);
                    String email = item.getString(TAG_EMAIL);
                    String gamekeyid = item.getString(TAG_GAMEKEYID);
                    String gamekey = item.getString(TAG_GAMEKEY);

                    gamekey = decodeStringAES(gamekey);

                    DBPersonalData.setMember_id(id);
                    DBPersonalData.setMember_name(name);
                    DBPersonalData.setMember_email(email);
                    DBPersonalData.setMember_gamekeyid(gamekeyid);
                    DBPersonalData.setMember_gamekey(gamekey);

                    if (DBType.equals("3")) {
                        array_Insert[10] = id;
                        array_Insert[11] = name;
                        array_Insert[12] = email;
                        array_Insert[13] = gamekeyid;
                        array_Insert[14] = gamekey;
                        array_Insert[15] = "변환을 완료했습니다.";

                        Log.d(DBTAG, "유저ID : " + id);
                        Log.d(DBTAG, "이름 : " + name);
                        Log.d(DBTAG, "이메일 : " + email);
                        Log.d(DBTAG, "게임키ID : " + gamekeyid);
                        Log.d(DBTAG, "게임키 : " + gamekey);
                    }
                    Log.d(DBTAG, "변환을 완료했습니다.");
                }
            } catch (JSONException e) {

                if (DBType.equals("3")) {
                    for (int i=10; i<=14; i++) {
                        if(array_Insert[i] == null) {
                            array_Insert[i] = "";
                        }
                    }
                    array_Insert[15] = "변환 과정 중에 에러가 발생하였습니다. 에러메시지 : " + e;
                }
                Log.d(DBTAG, "변환 과정 중에 에러가 발생하였습니다. 에러 메시지 : " + e);
            }

        } else if (ResultType.equals("L") || (ResultType.equals("G"))) {
            String TAG_JSON = "fail";
            String TAG_ID = "id";
            String TAG_NAME = "name";
            String TAG_EMAIL = "email";
            String TAG_ERRCODE =  "errcode";

            try {
                // 첫번째 괄호는 중괄호 {} 이므로 JSONObject입니다.
                JSONObject jsonObject = new JSONObject(mJsonString);

                // jsonObject에서 TAG_JSON 키를 갖는 JSONArray를 가져옵니다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // JsonArray에서 JSONObject를 하나씩 가져옵니다.
                for (int i = 0; i < jsonArray.length(); i++) {
                    // 데이터를 새로 생성한 PersonalData 클래스의 멤버변수에 입력하고 ArrayList에 추가합니다.
                    JSONObject item = jsonArray.getJSONObject(i);
                    String id = item.getString(TAG_ID);
                    String name = item.getString(TAG_NAME);
                    String email = item.getString(TAG_EMAIL);
                    String errcode = item.getString(TAG_ERRCODE);

                    DBPersonalData.setMember_id(id);
                    DBPersonalData.setMember_name(name);
                    DBPersonalData.setMember_email(email);
                    DBPersonalData.setMember_errcode(errcode);

                    if (DBType.equals("3")) {
                        array_Insert[10] = id;
                        array_Insert[11] = name;
                        array_Insert[12] = email;
                        array_Insert[15] = "변환을 완료했습니다.";

                        if (ResultType.equals("L")) {
                            array_Insert[16] = "에러 메시지 (04) : 돈이 부족합니다.";
                            ForegroundService.SetNo ("입금된 금액이 부족합니다.", "구매자 : " + name + "(" + email + ")");
                        } else if (ResultType.equals("G")) {
                            array_Insert[16] = "에러 메시지 (03) : 게임 키가 없습니다.";
                            ForegroundService.SetNo ("게임 키가 없어 보내지 못하였습니다.", "구매자 : " + name + "(" + email + ")");
                        }
                    }
                    Log.d(DBTAG, "변환을 완료했습니다.");
                }

            } catch (JSONException e) {

                if (DBType.equals("3")) {
                    for (int i=10; i<=14; i++) {
                        if(array_Insert[i] == null) {
                            array_Insert[i] = "";
                        }
                    }
                    array_Insert[15] = "변환 과정 중에 에러가 발생하였습니다. 에러메시지 : " + e;
                }
                Log.d(DBTAG, "변환 과정 중에 에러가 발생하였습니다. 에러 메시지 : " + e);
            }

        } else if (ResultType.equals("GY")) {
            String TAG_JSON = "success";
            String TAG_GAMEKEYID = "gamekeyid";
            String TAG_GAMEKEY = "gamekey";

            try {
                // 첫번째 괄호는 중괄호 {} 이므로 JSONObject입니다.
                JSONObject jsonObject = new JSONObject(mJsonString);

                // jsonObject에서 TAG_JSON 키를 갖는 JSONArray를 가져옵니다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // JsonArray에서 JSONObject를 하나씩 가져옵니다.
                for (int i = 0; i < jsonArray.length(); i++) {
                    // 데이터를 새로 생성한 PersonalData 클래스의 멤버변수에 입력하고 ArrayList에 추가합니다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    String gamekeyid = item.getString(TAG_GAMEKEYID);
                    String gamekey = item.getString(TAG_GAMEKEY);

                    gamekey = decodeStringAES(gamekey);

                    DBPersonalData.setMember_gamekeyid(gamekeyid);
                    DBPersonalData.setMember_gamekey(gamekey);


                    if (DBType.equals("5")) {
                        array_Gamekey[10] = gamekeyid;
                        array_Gamekey[11] = gamekey;
                        array_Gamekey[12] = "변환을 완료했습니다.";
                    }
                    Log.d(DBTAG, "변환을 완료했습니다.");
                }

            } catch (JSONException e) {

                if (DBType.equals("3")) {
                    for (int i = 10; i <= 11; i++) {
                        if (array_Gamekey[i] == null) {
                            array_Gamekey[i] = "";
                        }
                    }
                    array_Gamekey[12] = "변환 과정 중에 에러가 발생하였습니다. 에러메시지 : " + e;
                }
                Log.d(DBTAG, "변환 과정 중에 에러가 발생하였습니다. 에러 메시지 : " + e);
            }
        } else if (ResultType.equals("A")) {
            String TAG_JSON = "success";
            String TAG_id = "id";
            String TAG_name = "name";
            String TAG_email = "email";
            String TAG_regdate = "regdate";
            String TAG_process = "process";
            String TAG_depdate = "depdate";
            String TAG_depmoney = "depmoney";
            String TAG_prodate = "prodate";

            try {
                // 첫번째 괄호는 중괄호 {} 이므로 JSONObject입니다.
                JSONObject jsonObject = new JSONObject(mJsonString);

                // jsonObject에서 TAG_JSON 키를 갖는 JSONArray를 가져옵니다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // JsonArray에서 JSONObject를 하나씩 가져옵니다.
                for (int i = 0; i < jsonArray.length(); i++) {
                    // 데이터를 새로 생성한 PersonalData 클래스의 멤버변수에 입력하고 ArrayList에 추가합니다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_id);
                    String name = item.getString(TAG_name);
                    String email = item.getString(TAG_email);
                    String regdate = item.getString(TAG_regdate);
                    String process = item.getString(TAG_process);
                    String depdate = item.getString(TAG_depdate);
                    String depmoney = item.getString(TAG_depmoney);
                    String prodate = item.getString(TAG_prodate);

                    PersonalData DBListData = new PersonalData();

                    DBListData.setMember_id(id);
                    DBListData.setMember_name(name);
                    DBListData.setMember_email(email);
                    DBListData.setMember_regdate(regdate);
                    DBListData.setMember_process(process);
                    DBListData.setMember_depdate(depdate);
                    DBListData.setMember_depmoney(depmoney);
                    DBListData.setMember_prodate(prodate);

                    DBListArrayList.add(DBListData);

                    Log.d(DBTAG, "변환을 완료했습니다.");
                }

            } catch (JSONException e) {

                Log.d(DBTAG, "변환 과정 중에 에러가 발생하였습니다. 에러 메시지 : " + e);
            }
        } else if (ResultType.equals("AG")) {
            String TAG_JSON = "success";
            String TAG_id = "id";
            String TAG_gamekey = "gamekey";
            String TAG_used = "used";
            String TAG_usedate = "usedate";

            try {
                // 첫번째 괄호는 중괄호 {} 이므로 JSONObject입니다.
                JSONObject jsonObject = new JSONObject(mJsonString);

                // jsonObject에서 TAG_JSON 키를 갖는 JSONArray를 가져옵니다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // JsonArray에서 JSONObject를 하나씩 가져옵니다.
                for (int i = 0; i < jsonArray.length(); i++) {
                    // 데이터를 새로 생성한 PersonalData 클래스의 멤버변수에 입력하고 ArrayList에 추가합니다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_id);
                    String gamekey = item.getString(TAG_gamekey);
                    String used = item.getString(TAG_used);
                    String usedate = item.getString(TAG_usedate);

                    gamekey = decodeStringAES(gamekey);

                    ListViewItemGamekey personalData = new ListViewItemGamekey();

                    personalData.setTextNum(id);
                    personalData.setTextGamekey(gamekey);
                    personalData.setTextUsed(used);
                    personalData.setTextTime(usedate);

                    GamekeyArrayList.add(personalData);

                    Log.d(DBTAG, "변환을 완료했습니다.");
                }

            } catch (JSONException e) {

                Log.d(DBTAG, "변환 과정 중에 에러가 발생하였습니다. 에러 메시지 : " + e);
            }
        } else if (ResultType.equals("AN")) {
            String TAG_JSON = "success";
            String TAG_BANK = "bank";
            String TAG_NAME = "name";
            String TAG_ACCOUNT = "account";
            String TAG_MONEY = "money";

            try {
                // 첫번째 괄호는 중괄호 {} 이므로 JSONObject입니다.
                JSONObject jsonObject = new JSONObject(mJsonString);

                // jsonObject에서 TAG_JSON 키를 갖는 JSONArray를 가져옵니다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // JsonArray에서 JSONObject를 하나씩 가져옵니다.
                for (int i = 0; i < jsonArray.length(); i++) {
                    // 데이터를 새로 생성한 PersonalData 클래스의 멤버변수에 입력하고 ArrayList에 추가합니다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    String bank = item.getString(TAG_BANK);
                    String name = item.getString(TAG_NAME);
                    String account = item.getString(TAG_ACCOUNT);
                    String money = item.getString(TAG_MONEY);

                    array_Bank[0] = bank;
                    array_Bank[1] = name;
                    array_Bank[2] = account;
                    array_Bank[3] = money;

                    Log.d(DBTAG, "변환을 완료했습니다.");
                }

            } catch (JSONException e) {
                Log.d(DBTAG, "변환 과정 중에 에러가 발생하였습니다. 에러 메시지 : " + e);
            }
        } else if (ResultType.equals("D")) {
            String TAG_JSON = "success";
            String TAG_NUM = "count";

            try {
                // 첫번째 괄호는 중괄호 {} 이므로 JSONObject입니다.
                JSONObject jsonObject = new JSONObject(mJsonString);

                // jsonObject에서 TAG_JSON 키를 갖는 JSONArray를 가져옵니다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // JsonArray에서 JSONObject를 하나씩 가져옵니다.
                for (int i = 0; i < jsonArray.length(); i++) {
                    // 데이터를 새로 생성한 PersonalData 클래스의 멤버변수에 입력하고 ArrayList에 추가합니다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    String num = item.getString(TAG_NUM);
                    dateCount = num;

                    Log.d(DBTAG, "변환을 완료했습니다.");
                }

            } catch (JSONException e) {
                Log.d(DBTAG, "변환 과정 중에 에러가 발생하였습니다. 에러 메시지 : " + e);
            }
        }
    }

    // 이메일 처리 부분
    public boolean sendEmail() {
        Date time = new Date();

        String name = DBPersonalData.getMember_name();
        String email = DBPersonalData.getMember_email();
        String gamekey = DBPersonalData.getMember_gamekey();

        String id = PreferenceManager.getString(MainActivity.thisContext, "ID");
        String pw = PreferenceManager.getStringAES(MainActivity.thisContext, "PW");

        array_Email[0] = timeFormat.format(time);
        array_Email[1] = "이메일 전송을 시작합니다.";

        String subject = "게임 코드를 구매해주셔서 감사합니다."; // 이메일 제목
        String message = "<div style=\"font-family: 맑은 고딕; width: 540px; height: 600px; border-top: 4px solid #0084ff; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                "\t<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">\n" +
                "\t\t<span style=\"color:#0084ff;\">게임 코드</span>를 구매해주셔서 감사합니다.\n" +
                "\t</h1>\n" +
                "\t<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">\n" +
                "\t\t안녕하세요.<br />\n" +
                "\t\t게임 코드를 구매해주셔서 감사합니다.<br />\n" +
                "\t\t아래 <b style=\"color: #0084ff;\">'게임 코드'</b>를 등록하시면 됩니다.<br />\n" +
                "\t\t감사합니다.\n" +
                "\t</p>\n" +
                "\t<p style=\"font-size: 16px; margin: 40px 5px 20px; line-height: 28px;\">\n" +
                "\t\t게임 코드 : " + gamekey + "<br />\n" +
                "\t\t<span style=\"font-size: 24px;\"></span>\n" +
                "\t</p>\n" +
                "\t<div style=\"border-top: 1px solid #DDD; padding: 5px;\">\n" +
                "\t\t<p style=\"font-size: 13px; line-height: 21px; color: #555;\">\n" +
                "\t\t\t본 제품은 등록 후에는 절대 환불이 불가능하니 환불이 필요하시면 등록 전 연락주세요.<br />\n" +
                "\t\t</p>\n" +
                "\t</div>\n" +
                "</div>"; // 이메일 내용

        GMailSender gMailSender = new GMailSender(id, pw);

        gMailSender.setArray_Email(array_Email);

        String errMSG = null;

        try {
            //GMailSender.sendMail(제목, 내용, 받는사람);
            gMailSender.sendMail(subject, message, email, true);

            emailState = "Y";
        } catch (Exception e) {
            if (ForegroundService.isMyServiceRunning) {
                ForegroundService.stopFGService("이메일 전송에 실패했습니다.",name + "님(" + email + ")에게 이메일 전송에 실패하였습니다. / 서비스를 중지합니다.");
            }
            errMSG = e.toString();
            emailState = "N";

        }
        array_Email = gMailSender.getArray_Email();

        if (emailState.equals("Y")) {
            array_Email[7] = "이메일을 정상적으로 보냈습니다.";
            Log.d(GMailSender.TAG, "이메일을 정상적으로 보냈습니다.");
            return true;
        } else {
            Log.d(GMailSender.TAG, "이메일을 보내는데 실패했습니다.");
            array_Email[7] = "이메일 전송하는데 에러가 발생 하였습니다. 에러 메시지 : " + errMSG;
            return false;
        }
    }

    public void saveLog(String state) {
        String filename = DBPersonalData.getMember_filename();

        switch (state) {
            case "SMS" :
                FileLog.FileSave(filename, "", array_SMS, true);
                FileLog.FileSave(filename, "", array_Insert, false);
                break;
            case "SAVE" :
                FileLog.FileSave(filename, "", array_SMS, true);
                FileLog.FileSave(filename, "", array_Insert, false);
                FileLog.FileSave(filename, "", array_Email, false);
                FileLog.FileSave(filename, "", array_Game, false);
                break;
            case "GAMEKEY" :
                FileLog.FileSave(filename, "", array_Gamekey, false);
                break;
            case "RESEND" :
                FileLog.FileSave(filename, "", array_Email, false);
                FileLog.FileSave(filename, "", array_Game, false);
                break;
            case "NEWGAMEKEY" :
                FileLog.FileSave(filename, "", array_SMS, true);
                FileLog.FileSave(filename, "", array_Insert, false);
                FileLog.FileSave(filename, "", array_Gamekey, false);
                break;
        }
    }

    public void setResendGamekey (String name, String email, String gamekey, String ID, String GamekeyID, String Filename) {
        DBPersonalData.setMember_name(name);
        DBPersonalData.setMember_email(email);
        DBPersonalData.setMember_gamekey(gamekey);

        DBPersonalData.setMember_id(ID);
        DBPersonalData.setMember_gamekeyid(GamekeyID);
        DBPersonalData.setMember_filename(Filename);
    }

    // 복호화
    public String decodeStringAES (String str) {
        String DecodeValue = "";
        try {
            AES256Util decode = new AES256Util();
            DecodeValue = decode.AES_Decode(str);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            Log.d(AES, "복호화에 실패했습니다.");
            DecodeValue = "관리자에게 문의바랍니다.";
        }
        return DecodeValue;
    }


    // 암호화
    public String encodeStringAES (String str) {
        String encodeValue = "";
        try {
            AES256Util encode = new AES256Util();
            encodeValue = encode.AES_Encode(str);
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
            Log.d(AES, "암호화에 실패했습니다.");
            e.printStackTrace();
            encodeValue = "관리자에게 문의바랍니다.";
        }
        return encodeValue;
    }


    public ArrayList<PersonalData> getDBListArrayList() {
        return DBListArrayList;
    }

    public PersonalData getDBPersonalData() {
        return DBPersonalData;
    }

    public ArrayList<ListViewItemGamekey> getGamekeyArrayList() {
        return GamekeyArrayList;
    }

    public String[] getArray_Gamekey () {
        return array_Gamekey;
    }

    public String getDB2Time () {
        return array_Game[0];
    }

    public String getDBErr () {
        return array_Insert[16];
    }

    public String[] getArray_Bank () {
        return array_Bank;
    }

    public String getdateCount() {
        return dateCount;
    }
}

