package com.keysales.gamekeysalesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.keysales.gamekeysalesapp.ui.main.VPAdapter;

public class MainActivity extends AppCompatActivity {
    public static Context thisContext;
    static final int SMS_RECEIVE_PERMISSON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로딩창 띄우기
        Intent intent = new Intent(this, LodingActivity.class);
        startActivity(intent);

        // 제목 제거
        setTitle("");

        // 상단바 그림자 제거
        getSupportActionBar().setElevation(0);

        thisContext = this;

        // 이메일
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        // 선택된 탭에 해당되는 페이지를 뷰페이지에 띄우기
        ViewPager vp = findViewById(R.id.viewpager);
        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);


        // 탭과 뷰페이지를 연동시키기
        TabLayout tab = findViewById(R.id.tab);
        tab.setupWithViewPager(vp);

        // 탭 아이템 사이 간격 늘리기
        for(int i=0; i < tab.getTabCount(); i++) {
            View view = ((ViewGroup) tab.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (i == tab.getTabCount()-1) {
                p.setMargins(0, 0, 0, 0);
            } else {
                p.setMargins(0, 0, 30, 0);
            }
            tab.requestLayout();
        }

        // SMS 수신 권한 있는지 확인
        ReceiveSMSPermission ();
    }

    @Override
    protected void onResume() {
        super.onResume();
        processList();
    }


    // 상단바에 메뉴를 보여주는 메소드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // 메뉴의 아이템 클릭 시 발생하는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 클릭 한 아이템의 id를 가져옴
        int id = item.getItemId();

        // 아이템의 id가 해당되면 실행
        if (id == R.id.action_settings) {
            // SubActivity(옵션)으로 이동
            Intent intent = new Intent(MainActivity.this, OptionActivity.class);
            startActivity(intent);

            overridePendingTransition(R.anim.slide_left,R.anim.slide_none);
            return true;
        }
        // 아이템의 id 모두 해당되지 않으면 item 반환
        return super.onOptionsItemSelected(item);
    }

    // SMS 수신 권한 있는지 확인하는 메소드
    public void ReceiveSMSPermission () {
        // SMS 수신 권한이 부여되어 있는지 확인
        // SMS 수신 권한을 가지고 있는지 없는지를 반환하는 메소드
        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        // permissonCheck => 권한 묻는 것
        // PackageManager.PERMISSION_GRANTED == SMS 수신 권한 있다고 반환하는 값
        if (permissonCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            // PackageManager.PERMISSION_GRANTED이 아닐 경우 (SMS 수신권한 없음으로)
            Toast.makeText(getApplicationContext(), "SMS 수신권한이 필요합니다", Toast.LENGTH_SHORT).show();

            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(getApplicationContext(), "SMS 수신권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            }

        }
    }

    public void processList() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.activity_main,(ViewGroup) findViewById(R.id.main_activty));

        TextView text1 = layout.findViewById(R.id.textView1);
        TextView text21 = layout.findViewById(R.id.textView21);
        TextView text22 = layout.findViewById(R.id.textView22);
        TextView text23 = layout.findViewById(R.id.textView23);
        TextView text24 = layout.findViewById(R.id.textView24);
        TextView text3 = layout.findViewById(R.id.textView3);

        if (PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
            KeyProcess dateCount = new KeyProcess();
            if(dateCount.StartDBLink("11")) {
                String count = dateCount.getdateCount();
                text1.setText("금일");
                text21.setText(count);
                text22.setText(" 건의 ");
                text23.setText("주문내역");
                text24.setText("이");
                text3.setText("있습니다.");

                text1.setTextSize(26);
                text21.setTextSize(26);
                text22.setTextSize(26);
                text23.setTextSize(26);
                text24.setTextSize(26);
                text3.setTextSize(26);

                text1.setHeight(90);
                text21.setHeight(90);
                text22.setHeight(90);
                text23.setHeight(90);
                text24.setHeight(90);
                text3.setHeight(90);

            } else {
                text1.setText("DB 연결에 실패하였습니다.");
                text22.setText("");
                text23.setText("");
                text24.setText("");
                text3.setText("");

                text1.setTextSize(18);

                text21.setHeight(0);
                text22.setHeight(0);
                text23.setHeight(0);
                text24.setHeight(0);

                text3.setHeight(0);
            }
        } else {
            text1.setText("DB 설정이 필요합니다.");
            text22.setText("");
            text23.setText("");
            text24.setText("");
            text3.setText("");

            text1.setTextSize(18);

            text21.setHeight(0);
            text22.setHeight(0);
            text23.setHeight(0);
            text24.setHeight(0);

            text3.setHeight(0);
        }

        Log.d("text1", text1.getText().toString());
        Log.d("text2", text21.getText().toString()+text22.getText().toString()+text23.getText().toString()+text24.getText().toString());
        Log.d("text3", text3.getText().toString());
    }
}
