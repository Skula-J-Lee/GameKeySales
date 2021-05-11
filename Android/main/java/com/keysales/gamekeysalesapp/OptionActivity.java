package com.keysales.gamekeysalesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keysales.gamekeysalesapp.ui.main.GamekeyActivity;

import java.util.ArrayList;
import java.util.List;

public class OptionActivity extends AppCompatActivity {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //상단바 제목 설정
        setTitle("설정");

        //뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListViewList();

        // 리스트 아이템 클릭 시 행동
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //int i = (ForegroundService.isMyServiceRunning) ? 1 : 0;

            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                switch (position) {
                    case 0:
                        emailDialog();
                        break;
                    case 1:
                        dbDialog();
                        break;
                    case 2:
                        ServiceOnOff();
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable()  {
                            public void run() {
                                // 시간 지난 후 실행할 코딩
                                ListViewList();
                            }
                        }, 10);
                        break;
                    case 3:
                        if (PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
                            Intent intent = new Intent(OptionActivity.this, GamekeyActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_left,R.anim.slide_none);
                        } else {
                            // DB 등록이 되어있지 않을 경우
                            Toast.makeText(getApplicationContext(), "DB를 연결 하십시오.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 4:
                        if (PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
                            Toast.makeText(getApplicationContext(), "입금 계좌 정보를 불러옵니다.", Toast.LENGTH_SHORT).show();
                            bankDialog();
                            overridePendingTransition(R.anim.slide_left,R.anim.slide_none);
                        } else {
                            // DB 등록이 되어있지 않을 경우
                            Toast.makeText(getApplicationContext(), "DB를 연결 하십시오.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }) ;
    }

    // 뒤로가기 실행 시 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작

                finish();
                overridePendingTransition(R.anim.slide_none,R.anim.silde_right);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 백(뒤로가기)버튼 클릭 시 동작
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.slide_none,R.anim.silde_right);
    }


    // 리스트뷰 설정
    public void ListViewList () {
        // 리스트뷰
        list = (ListView)findViewById(R.id.listview);

        List<String> data = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);
        list.setAdapter(adapter);

        data.add("구글 이메일 계정 설정");

        data.add("데이터베이스 설정");

        if (ForegroundService.isMyServiceRunning) {
            // MyService.isMyServiceRunning = true : 서비스 실행 상태
            data.add("서비스 중지");
        } else {
            // MyService.isMyServiceRunning = false : 서비스 꺼짐 상태
            data.add("서비스 실행");
        }
        data.add("게임 키 설정");
        data.add("입금 계좌 정보 설정");
        adapter.notifyDataSetChanged();
    }

    // 서비스 ON / OFF
    public void ServiceOnOff() {
        boolean chkEmail = PreferenceManager.getBoolean(MainActivity.thisContext, "EMAIL");
        boolean chkDB = PreferenceManager.getBoolean(MainActivity.thisContext, "DB");

        Intent intent = new Intent(OptionActivity.this, ForegroundService.class);

        Log.d("[Info]", "진입");
        if (ForegroundService.isMyServiceRunning) {
            // MyService.isMyServiceRunning = true : 서비스 실행 상태 -> 포그라운드 종료
            stopService(intent);
            Toast.makeText(getApplicationContext(), "서비스가 중지 되었습니다.", Toast.LENGTH_SHORT).show();
            Log.d("[Info]", "서비스 중지");
        } else if (chkEmail && chkDB) {
            // MyService.isMyServiceRunning = false : 서비스 꺼짐 상태 + 이메일 DB 등록 되어있으면 -> 포그라운드 실행
            intent.setAction("startForeground");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Toast.makeText(getApplicationContext(), "서비스가 실행 되었습니다.", Toast.LENGTH_SHORT).show();
            Log.d("[Info]", "서비스 실행");
        } else if (!chkEmail) {
            Toast.makeText(getApplicationContext(), "이메일이 등록 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "데이터베이스가 등록 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 이메일 팝업창 설정
    public void emailDialog() {
        Log.d("[Info]", "emailLogoutDialog");
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);

        final View layout;

        if (PreferenceManager.getBoolean(MainActivity.thisContext, "EMAIL")) {
            // 이메일 등록이 되어 있을 경우
            layout = inflater.inflate(R.layout.dialog_logout,(ViewGroup) findViewById(R.id.layout_logout));

            TextView textView = layout.findViewById(R.id.textView);
            textView.setText(String.format("%s 등록 되어 있습니다.", PreferenceManager.getString(MainActivity.thisContext, "ID")));

            emailDialog.setNeutralButton("등록 해제", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (!ForegroundService.isMyServiceRunning) {
                        PreferenceManager.clearEmail(MainActivity.thisContext);
                        Toast.makeText(getApplicationContext(), "이메일 등록이 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                        emailDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "서비스를 중지해야 등록을 해제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        emailDialog();
                    }
                }
            });

        } else {
            // 이메일 등록이 되어있지 않을 경우
            layout = inflater.inflate(R.layout.dialog_login,(ViewGroup) findViewById(R.id.layout_root)); // 레이아웃 저장
            emailDialog.setMessage("로그인 시 해당 계정에 이메일이 옵니다.\n로그인이 안될 경우 보안 설정 하십시오.");
            emailDialog.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                // 로그인 버튼 클릭 시 동작
                public void onClick(DialogInterface dialog, int which) {

                    EditText textID = layout.findViewById(R.id.mailID);
                    EditText textPW = layout.findViewById(R.id.mailPW);

                    String saveID = textID.getText().toString().concat("@gmail.com");
                    String savePW = textPW.getText().toString();

                    if (saveID.length() == 0) {
                        // 아이디 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                        emailDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    } else if (savePW.length() == 0) {
                        // 비밀번호 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                        emailDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    }
                    PreferenceManager.setEmail(MainActivity.thisContext, saveID, savePW);
                    String subject = "키파라 메일 등록 알림"; // 이메일 제목
                    String message = "<div style=\"font-family: 맑은 고딕; width: 540px; height: 600px; border-top: 4px solid #0084ff; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                            "\t<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">\n" +
                            "\t\t<span style=\"color:#0084ff;\">메일 등록 알림</span>입니다. \n" +
                            "\t</h1>\n" +
                            "\t<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">\n" +
                            "\t\t안녕하세요.<br />\n" +
                            "\t\t키파라에 구글 계정의 등록이 되었습니다.<br />\n" +
                            "\t\t감사합니다.\n" +
                            "\t</p>\n" +
                            "\t<div style=\"border-top: 1px solid #DDD; padding: 5px;\">\n" +
                            "\t\t<p style=\"font-size: 13px; line-height: 21px; color: #555;\">\n" +
                            "\t\t\t참고 : 계정을 확인하기 위해 자기 자신에게 보내는 이메일입니다.<br />\n" +
                            "\t\t</p>\n" +
                            "\t</div>\n" +
                            "</div>"; // 이메일 내용
                    if (PreferenceManager.sendEmail(MainActivity.thisContext, "", subject, message)) {
                        // 이메일 테스트 결과 true(테스트 통과) 나올 경우
                        Toast.makeText(getApplicationContext(), "이메일 등록이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        // 이메일 테스트 결과 false(테스트 실패) 나올 경우
                        // ID, PW 삭제
                        PreferenceManager.clearEmail(MainActivity.thisContext);
                        Toast.makeText(getApplicationContext(), "인터넷 연결 확인 또는 계정을 다시 입력하십시오.", Toast.LENGTH_SHORT).show();
                    }
                    emailDialog(); // 다이얼로그(팝업창) 실행
                }
            });
            emailDialog.setNeutralButton("구글 계정 보안 설정", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://myaccount.google.com/lesssecureapps"));
                    startActivity(intent);
                }
            });
        }

        emailDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        emailDialog.setTitle("구글 이메일 계정 설정"); // 다이얼로그(팝업창) 제목 설정
        emailDialog.setView(layout); // 다이얼로그(팝업창) 화면 추가

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = emailDialog.create();
        ad.show();
    }

    // DB 팝업창 설정
    public void dbDialog() {
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(this);

        final View layout;

        if (PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
            // DB 등록이 되어 있을 경우
            layout = inflater.inflate(R.layout.dialog_db_on,(ViewGroup) findViewById(R.id.layout_db_on));

            TextView textView = layout.findViewById(R.id.textView);
            textView.setText(String.format("%s 연결 되어 있습니다.", PreferenceManager.getString(MainActivity.thisContext, "DB_NAME")));

            dbDialog.setNeutralButton("등록 해제", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (!ForegroundService.isMyServiceRunning) {
                        PreferenceManager.clearDB(MainActivity.thisContext);
                        Toast.makeText(getApplicationContext(), "DB 등록이 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                        dbDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "서비스를 중지해야 등록 해제 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        dbDialog();
                    }
                }
            });
        } else {
            // DB 등록이 되어있지 않을 경우
            layout = inflater.inflate(R.layout.dialog_db_off,(ViewGroup) findViewById(R.id.layout_db_off)); // 레이아웃 저장

            dbDialog.setNeutralButton("등록", new DialogInterface.OnClickListener() {
                // 로그인 버튼 클릭 시 동작
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "DB 등록 중입니다.", Toast.LENGTH_SHORT).show();
                    EditText textADD = layout.findViewById(R.id.dbADD);
                    EditText textID = layout.findViewById(R.id.dbID);
                    EditText textPW = layout.findViewById(R.id.dbPW);
                    EditText textNAME = layout.findViewById(R.id.dbNAME);

                    String saveADD = textADD.getText().toString();
                    String saveID = textID.getText().toString();
                    String savePW = textPW.getText().toString();
                    String saveNAME = textNAME.getText().toString();

                    if (saveADD.length() == 0) {
                        // 웹 주소 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "웹 주소(http://...)를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        dbDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    } else if (saveID.length() == 0) {
                        // 비밀번호 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "계정 아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        dbDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    } else if (savePW.length() == 0) {
                        // 비밀번호 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "계정 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        dbDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    } else if (saveNAME.length() == 0) {
                        // 비밀번호 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "DATABASE 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        dbDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "DB 등록을 시작합니다.", Toast.LENGTH_SHORT).show();
                    PreferenceManager.setDB(MainActivity.thisContext, saveADD, saveID, savePW, saveNAME);

                    KeyProcess DBLink = new KeyProcess();
                    if (DBLink.StartDBLink("0")) {
                        Toast.makeText(getApplicationContext(), "DB 등록이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        PreferenceManager.setBoolean(MainActivity.thisContext, "DB", false);
                        Toast.makeText(getApplicationContext(), "DB 등록을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                    dbDialog(); // 다이얼로그(팝업창) 실행
                }
            });
        }

        dbDialog.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dbDialog.setTitle("DATABASE 설정"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout); // 다이얼로그(팝업창) 화면 추가

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }

    // 입금계좌 설정 팝업창 설정
    public void bankDialog() {
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(this);

        final View layout = inflater.inflate(R.layout.dialog_bank,(ViewGroup) findViewById(R.id.layout_bank));

        EditText textbank = layout.findViewById(R.id.editbank);
        EditText textname = layout.findViewById(R.id.editname);
        EditText textaccount = layout.findViewById(R.id.editaccount);
        EditText textmoney = layout.findViewById(R.id.editmoney);

        KeyProcess loadBank = new KeyProcess();
        if (loadBank.StartDBLink("9")) {
            String[] array_Bank = loadBank.getArray_Bank();

            String textButton = "수정";

            if (array_Bank[0] == null || array_Bank[0].equals("null") || array_Bank[0].equals("")) {
                array_Bank[0] = "";
                textButton = "설정";
            }
            if (array_Bank[1] == null || array_Bank[1].equals("null") || array_Bank[1].equals("")) {
                array_Bank[1] = "";
                textButton = "설정";
            }
            if (array_Bank[2] == null || array_Bank[2].equals("null") || array_Bank[2].equals("")) {
                array_Bank[2] = "";
                textButton = "설정";
            }
            if (array_Bank[3] == null || array_Bank[3].equals("null") || array_Bank[3].equals("")) {
                array_Bank[3] = "";
                textButton = "설정";
            }
            if (textButton.equals("설정")) {
                Toast.makeText(getApplicationContext(), "입금 계좌 정보를 설정해 주세요.", Toast.LENGTH_SHORT).show();
            }

            textbank.setText(array_Bank[0]);
            textname.setText(array_Bank[1]);
            textaccount.setText(array_Bank[2]);
            textmoney.setText(array_Bank[3]);

            dbDialog.setNeutralButton(textButton, new DialogInterface.OnClickListener() {
                // 로그인 버튼 클릭 시 동작
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "입금 계좌 정보를 설정합니다.", Toast.LENGTH_SHORT).show();
                    EditText textbank = layout.findViewById(R.id.editbank);
                    EditText textname = layout.findViewById(R.id.editname);
                    EditText textaccount = layout.findViewById(R.id.editaccount);
                    EditText textmoney = layout.findViewById(R.id.editmoney);

                    String savebank = textbank.getText().toString();
                    String savename = textname.getText().toString();
                    String saveaccount = textaccount.getText().toString();
                    String savemoney = textmoney.getText().toString();

                    if (savebank.length() == 0) {
                        // 웹 주소 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "은행명을 입력해 주세요. (**은행)", Toast.LENGTH_SHORT).show();
                        bankDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    } else if (savename.length() == 0) {
                        // 비밀번호 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "예금주 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        bankDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    } else if (saveaccount.length() == 0) {
                        // 비밀번호 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "계좌번호를 입력해 주세요. (****-****-****)", Toast.LENGTH_SHORT).show();
                        bankDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    } else if (savemoney.length() == 0) {
                        // 비밀번호 칸이 빈칸 이면
                        Toast.makeText(getApplicationContext(), "판매 금액을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        bankDialog(); // 다이얼로그(팝업창) 실행
                        return;
                    }

                    try {
                        int setMoney = Integer.parseInt(savemoney);
                        PreferenceManager.setInt(MainActivity.thisContext, "MONEY", setMoney);
                    } catch(NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "숫자를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        bankDialog();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "입금 계좌 설정을 시작합니다.", Toast.LENGTH_SHORT).show();

                    KeyProcess setBank = new KeyProcess();
                    if (setBank.StartDBLink("10", savebank, savename, saveaccount, savemoney)) {
                        Toast.makeText(getApplicationContext(), "입금계좌 정보 설정을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "입금계좌 정보 설정을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    bankDialog(); // 다이얼로그(팝업창) 실행
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "입금 계좌 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }

        dbDialog.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dbDialog.setTitle("입금 계좌 정보 설정"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout); // 다이얼로그(팝업창) 화면 추가

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }

}
