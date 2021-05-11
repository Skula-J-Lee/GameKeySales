package com.keysales.gamekeysalesapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver {

    // 권한
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    // 날짜 형식 설정

    private static String SMSTAG = "SmsReceiver";
    private static String GKTAG = "GameKey";

    @Override
    public void onReceive(Context context, Intent intent) {
        // SMS_RECEIVED에 대한 액션일때와 서비스가 실행되어있을 경우 실행
        if (intent.getAction().equals(SMS_RECEIVED) && ForegroundService.isMyServiceRunning) {
            Log.d(SMSTAG, "문자 수신 중...");

            // Bundle을 이용해서 메세지 내용을 가져옴
            Bundle bundle = intent.getExtras();

            SmsMessage[] saveSmsMessage = parseSmsMessage(bundle);

            String sender = saveSmsMessage[0].getOriginatingAddress();
            String contents = saveSmsMessage[0].getMessageBody().toString();

            // 메세지가 왔을 경우
            if (saveSmsMessage.length > 0) {
                if (sender.equals("16449999") && contents.contains("입금")) {
                    Log.d("처리 과정", "맞는 문자");
                    KeyProcess keyProcessing = new KeyProcess();
                    keyProcessing.SmsProcess(saveSmsMessage); // 문자 내용 처리
                    Log.d("처리 과정", "문자 내용 처리");
                    if (keyProcessing.StartDBLink("3")) { // DB 연동 (이름 대조 및 게임 키 추출)
                        Log.d("처리 과정", "DB 연동 완료");
                        // 연동이 잘 끝났다면 게임 키 전송
                        keyProcessing.sendEmail(); // 게임 키 이메일 전송
                        Log.d("처리 과정", "이메일 전송완료");
                        keyProcessing.StartDBLink("4"); // DB 연동 (유저 상태 및 게임 키 상태 업데이트)
                        Log.d("처리 과정", "DB 연동 완료");
                        keyProcessing.saveLog("SAVE");
                        Log.d("처리 과정", "파일 저장");
                        PersonalData data = keyProcessing.getDBPersonalData();
                        String name = data.getMember_name();
                        String email = data.getMember_email();
                        ForegroundService.SetNo ("게임 키를 정상적으로 보냈습니다.", "구매자 : " + name + "(" + email + ")");
                    } else {
                        // 연동이 실패했다면
                        if (keyProcessing.getDBErr().contains("(02)")) {
                            Log.d("처리 과정", "이름 없음");
                            // DB 이름 대조 결과 없는 이름이라면 무시
                        } else {
                            // 어떤 문제로 실패했다면 로그 저장
                            Log.d("처리 과정", "문제 발생");
                            keyProcessing.saveLog("SMS");
                        }
                    }
                }
            }
        }
    }


    // 안드로이드 버전에 따른 SMS 처리
    private SmsMessage[] parseSmsMessage(Bundle bundle){
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for(int i=0; i<objs.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }
        return messages;
    }
}
