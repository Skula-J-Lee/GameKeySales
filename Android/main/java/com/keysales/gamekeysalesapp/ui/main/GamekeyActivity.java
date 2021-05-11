package com.keysales.gamekeysalesapp.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keysales.gamekeysalesapp.KeyProcess;
import com.keysales.gamekeysalesapp.MainActivity;
import com.keysales.gamekeysalesapp.PreferenceManager;
import com.keysales.gamekeysalesapp.R;

import java.util.ArrayList;

public class GamekeyActivity extends AppCompatActivity {

    private ListView listview;
    private ListViewGamekeyAdapter adapter;

    public ArrayList<ListViewItemGamekey> dbArrayList = new ArrayList<>();

    private SwipeRefreshLayout gamekeySwipeRefreshLayout;

    public int gamekeyCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamekey);

        gamekeySwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // 상단바 그림자 제거
        getSupportActionBar().setElevation(0);

        //상단바 제목 설정
        setTitle("");

        ListViewList();
        //뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 리스트 아이템 클릭 시 행동
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //int i = (ForegroundService.isMyServiceRunning) ? 1 : 0;
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Dialog(position);
            }
        }) ;

        // 리스트 아이템 롱 클릭 시 행동
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                longDialog(pos);
                return true;
            }
        });

        // 새로 고침 시 행동
        gamekeySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gamekeySwipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ListViewList ();
                        gamekeySwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }
    // 리스트뷰 설정
    public void ListViewList () {
        // Adapter 생성
        gamekeyCount = 0;

        adapter = new ListViewGamekeyAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listviewgamekey);
        listview.setAdapter(adapter);

        // DB 불러오기
        if (PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
            Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB에서 불러오는 중입니다.", Toast.LENGTH_SHORT).show();

            KeyProcess GamekeyLoad = new KeyProcess();
            if(GamekeyLoad.StartDBLink("7")) {
                dbArrayList = new ArrayList<>();
                dbArrayList = GamekeyLoad.getGamekeyArrayList();

                // dbArrayList 개수 반복
                for (int i = dbArrayList.size() - 1; i >= 0; i--) {

                    ListViewItemGamekey dbData = dbArrayList.get(i);

                    String Member_used = dbData.getTextUsed();
                    String Member_usedtime = dbData.getTextTime();

                    if (Member_usedtime == null || Member_usedtime.equals("null")) {
                        dbData.setTextTime("");
                    }

                    switch (Member_used) {
                        case "Y":
                            dbData.setTextUsed("사용 완료");
                            break;
                        case "P":
                            dbData.setTextUsed("사용 중");
                            break;
                        default:
                            gamekeyCount++;
                            dbData.setTextUsed("미 사용");
                            break;
                    }


                    String usetime;
                    if (dbData.getTextTime() == "") {
                        usetime = "";
                    } else {
                        usetime = dbData.getTextTime().substring(5, 16);
                    }

                    String textUsed = dbData.getTextUsed();

                    SpannableString spannableString = new SpannableString(textUsed);

                    int start = 0;
                    int end = textUsed.length();

                    if (textUsed.contains("미")) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#d1656f")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //빨간색
                    } else {
                        if (textUsed.contains("중")) {
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#e79c2b")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 주황색
                        } else {
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#65b5ce")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 파란색
                        }
                    }

                    dbArrayList.set(i, dbData);
                    adapter.addItem(dbData.getTextNum(), dbData.getTextGamekey(), spannableString, usetime);
                }
                adapter.notifyDataSetChanged();

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                View layout = inflater.inflate(R.layout.activity_gamekey,(ViewGroup) findViewById(R.id.gamekey_activity));

                TextView textkey1 = (TextView) layout.findViewById(R.id.textViewkey1);
                TextView textkey21 = (TextView) layout.findViewById(R.id.textViewkey21);
                TextView textkey22 = (TextView) layout.findViewById(R.id.textViewkey22);
                TextView textkey23 = (TextView) layout.findViewById(R.id.textViewkey23);
                TextView textkey24 = (TextView) layout.findViewById(R.id.textViewkey24);
                TextView textkey3 = (TextView) layout.findViewById(R.id.textViewkey3);

                String count = Integer.toString(gamekeyCount);
                textkey1.setText("현재");
                textkey21.setText(count);
                textkey22.setText(" 개의 ");
                textkey23.setText("게임키");
                textkey24.setText("가");
                textkey3.setText("남아있습니다.");

                Log.d("text1", textkey1.getText().toString());
                Log.d("text2", textkey21.getText().toString()+textkey22.getText().toString()+textkey23.getText().toString()+textkey24.getText().toString());
                Log.d("text3", textkey3.getText().toString());

                Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB에서 불러오기 완료했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB에서 불러오기를 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB가 등록되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();

                // 액티비티 전환 애니메이션
                overridePendingTransition(R.anim.slide_none,R.anim.silde_right);
                return true;
            }
            case R.id.action_settings: { // 아이템 선택하기
                gameAddDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 백(뒤로가기)버튼 클릭 시 동작
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_none,R.anim.silde_right);
    }

    // 상단바에 메뉴를 보여주는 메소드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_gamekey, menu);
        return true;
    }

    // 게임 키 추가 팝업창 설정
    public void gameAddDialog() {
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(this);

        final View layout = inflater.inflate(R.layout.dialog_gameadd,(ViewGroup) findViewById(R.id.layout_gameadd));

        dbDialog.setNeutralButton("추가", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EditText textGameKey = layout.findViewById(R.id.gamekey);
                String saveGameKey = textGameKey.getText().toString();

                if (saveGameKey.length() == 0) {
                    // 게임 키 칸이 빈칸 이면
                    Toast.makeText(getApplicationContext(), "게임 키를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    gameAddDialog(); // 다이얼로그(팝업창) 실행
                    return;
                }

                if (PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
                    Toast.makeText(getApplicationContext(), "게임 키 추가를 시작합니다.", Toast.LENGTH_SHORT).show();

                    KeyProcess insertGamekey = new KeyProcess();
                    saveGameKey = insertGamekey.encodeStringAES(saveGameKey);
                    if (saveGameKey.contains("error")) {
                        // 암호화 실패
                        Toast.makeText(getApplicationContext(), "게임 키 추가를 실패하였습니다. (암호화 실패)", Toast.LENGTH_SHORT).show();
                    } else {
                        // 암호화 완료

                        if(insertGamekey.StartDBLink("2", saveGameKey)) {
                            Toast.makeText(getApplicationContext(), "게임 키를 추가 하였습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "게임 키 추가를 실패하였습니다. (DB 연동 실패)", Toast.LENGTH_SHORT).show();
                        }
                    }
                    gameAddDialog(); // 다이얼로그(팝업창) 실행
                } else {
                    // DB 등록이 되어있지 않을 경우
                    Toast.makeText(getApplicationContext(), "DB를 연결 하십시오.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dbDialog.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dbDialog.setTitle("게임 키 추가"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout); // 다이얼로그(팝업창) 화면 추가

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }

    // 롱 클릭 시 다이얼로그
    public void longDialog(int dataNum) {
        dataNum = dbArrayList.size() - dataNum - 1;

        Context mContext = this;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(mContext);

        View layout = inflater.inflate(R.layout.dialog_db_on,(ViewGroup) findViewById(R.id.layout_db_on));
        TextView textViewName = layout.findViewById(R.id.textView);

        ListViewItemGamekey viewDataItem = new ListViewItemGamekey();
        viewDataItem = dbArrayList.get(dataNum);

        final String textGamekey = viewDataItem.getTextGamekey();
        final String textGamekeyID = viewDataItem.getTextNum();

        String textViewText = "게임 키(" + textGamekey + ")를 삭제하시겠습니까?";

        textViewName.setText(textViewText);

        dbDialog.setNeutralButton("예", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.thisContext.getApplicationContext(), "게임 키(" + textGamekey + ")를 삭제합니다.", Toast.LENGTH_SHORT).show();

                KeyProcess delectGamekey = new KeyProcess();
                if(delectGamekey.StartDBLink("8", textGamekeyID)) {

                    Toast.makeText(MainActivity.thisContext.getApplicationContext(), "게임 키(" + textGamekey + ")를 삭제 하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.thisContext.getApplicationContext(), "게임 키(" + textGamekey + ")를 삭제하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                ListViewList ();
            }
        });

        dbDialog.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dbDialog.setTitle("게임 키 삭제"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout);

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }


    // 팝업 설정
    public void Dialog(int dataNum) {
        dataNum = dbArrayList.size() - dataNum - 1;

        // 다이얼로그(팝업창) 준비
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(this);

        View layout = inflater.inflate(R.layout.dialog_gamekey,(ViewGroup) findViewById(R.id.layout_gamekey)); // 레이아웃 뷰를 가져옴

        TextView textViewName = layout.findViewById(R.id.textDialogName);
        TextView textViewEmail = layout.findViewById(R.id.textDialogEmail);
        TextView textViewDepTime = layout.findViewById(R.id.textDialogDepTime);
        TextView textViewSend = layout.findViewById(R.id.textDialogSend);

        ListViewItemGamekey viewDataItem = dbArrayList.get(dataNum);

        String textName = viewDataItem.getTextNum();
        String textEmail = viewDataItem.getTextGamekey();
        String textDepTime = viewDataItem.getTextUsed();
        String textSend = viewDataItem.getTextTime();
        String textGamekey = "";
        String textTime = "";

        if (textName.equals("null")) {
            Log.d("ddd", textName);
            textName = "";
        }
        if (textEmail.equals("null")) {
            Log.d("ddd", textEmail);
            textEmail = "";
        }
        if (textSend.equals("null")) {
            Log.d("ddd", textSend);
            textSend = "";
        }

        textViewName.setText(textName);
        textViewEmail.setText(textEmail);
        textViewDepTime.setText(textDepTime);
        textViewSend.setText(textSend);

        final int final_dataNum = dbArrayList.size() - dataNum - 1;

        dbDialog.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dbDialog.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {
                longDialog(final_dataNum);
            }
        });

        dbDialog.setTitle("상세 정보"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout);

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }

}
