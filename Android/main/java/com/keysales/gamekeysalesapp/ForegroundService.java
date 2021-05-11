package com.keysales.gamekeysalesapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {

    public static boolean isMyServiceRunning = false;;

    private static Context thisContext;
    private static String Title = "키파라";
    private Thread mThread;
    private int mCount = 0;
    private static int noCount = 1;

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(Title);
        builder.setContentText("서비스 실행 중");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {     // 오레오 버전 이상 노티피케이션 알림 설정
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        startForeground(1, builder.build());

        Log.d("ForegroundService", "서비스 실행 완료");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {   //포그라운드 서비스 실행
        noCount = 1;
        thisContext = this;
        Log.d("ForegroundService", "실행 시작 중 ...");
        isMyServiceRunning = true;
        Log.d("ForegroundService", "실행 상태 : true");
        if ("startForeground".equals(intent.getAction())) {
            startForegroundService();
        } else if (mThread == null) {
            mThread = new Thread("ServiceThread") {

                @Override
                public void run() {
                    for (int i =0; i< 1000; i++){
                        try{
                            mCount++;
                            Thread.sleep(100);
                        } catch (InterruptedException e){
                            break;
                        }
                        Log.d("ForegroundService", "서비스 동작 중 " + mCount);
                    }
                }
            };
            mThread.start();
        }
        return START_STICKY;
    }

    // 포그라운드 서비스 중지
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ForegroundService", "실행 상태 : false");
        isMyServiceRunning = false;
    }

    public static void stopFGService(String noTitle, String noText) {
        noCount = 0;
        SetNo(noTitle, noText);
        Intent intent = new Intent(MainActivity.thisContext, ForegroundService.class);
        thisContext.stopService(intent);
    }

    public static void SetNo (String noTitle, String noText) {
        noCount++;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(thisContext, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(noTitle);
        builder.setContentText(noText);
        builder.setAutoCancel(true);

        Intent notificationIntent = new Intent(thisContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(thisContext, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) thisContext.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {     // 오레오 버전 이상 노티피케이션 알림 설정
            manager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        manager.notify(noCount, builder.build());
    }
}