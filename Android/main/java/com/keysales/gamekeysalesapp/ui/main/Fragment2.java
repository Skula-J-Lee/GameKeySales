package com.keysales.gamekeysalesapp.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keysales.gamekeysalesapp.FileLog;
import com.keysales.gamekeysalesapp.KeyProcess;
import com.keysales.gamekeysalesapp.MainActivity;
import com.keysales.gamekeysalesapp.PersonalData;
import com.keysales.gamekeysalesapp.PreferenceManager;
import com.keysales.gamekeysalesapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {

    private ListView listview;
    private ListViewAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View view;

    public static List<ArrayList> filesList = new ArrayList<>();
    public static List<String[]> filesContent = new ArrayList<>();

    public static ArrayList<ListViewItem> listdata = new ArrayList<ListViewItem>() ;

    public Fragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_2, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        ListViewList();

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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ListViewList ();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return view;
    }

    // 리스트뷰 설정
    public void ListViewList () {
        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) view.findViewById(R.id.listviewemail);
        listview.setAdapter(adapter);

        // 초기화
        filesList = new ArrayList<>();
        filesContent = new ArrayList<>();
        listdata = new ArrayList<ListViewItem>();
        ListViewItem dataItem = new ListViewItem();

        // 파일 불러오기
        FileLog.FileNameLoad();

        // 첫 번째 아이템 추가.
        for (int i = filesList.size() - 1; i >= 0; i--) {
            // 대 단위
            dataItem.setTextNum(String.valueOf(i + 1)); // 번호

            filesContent = new ArrayList<>();
            filesContent = filesList.get(i); // 하나의 파일

            int numberDB = 0;
            int numberEmail = 0;
            int numberGamekey = 0;

            for (int j = 0; j < filesContent.size(); j++) {
                int fileMiddle = filesContent.get(j).length; // 중 단위에서 소 단위 개수
                // 중 단위
                for (int k = 0; k < fileMiddle; k++) {
                    // 소 단위
                    if (j == 0 && k == 0) { // 0 -> 0 : 파일 이름
                        dataItem.setTextFile(filesContent.get(j)[k]);
                    }

                    if (j == 1) {
                        if (k == 0) {
                            // 이름 불러오기
                            dataItem.setTextDepTime(filesContent.get(j)[k]);
                        }

                        if (k == 2) {
                            // 이름 불러오기
                            dataItem.setTextName(filesContent.get(j)[k]);
                        }
                    }
                    if (fileMiddle == 18) { // 중단위 길이 18 = DB연동1
                        if (k == 10) { // 소단위 10 : 유저 ID
                            // 유저 ID 불러오기
                            dataItem.setTextUserID(filesContent.get(j)[k]);
                        } else if (k == 12) { // 소단위 12 : 이메일
                            // 이메일 불러오기
                            dataItem.setTextEmail(filesContent.get(j)[k]);
                        } else if (k == 13) { // 소단위 13 : 개암키 ID
                            // 게임 키 ID 불러오기
                            dataItem.setTextGamekeyID(filesContent.get(j)[k]);
                        } else if (k == 14) { // 소단위 14 : 게임 키
                            // 게임키 불러오기
                            dataItem.setTextGamekey(filesContent.get(j)[k]);
                        } else if (k == 16) {
                            // DB(게임 키 불러오기) 상태
                            if (filesContent.get(j)[k] == null) {
                                dataItem.setTextDB1Process("알 수 없음");
                            } else if (filesContent.get(j)[k].contains("완료")) {
                                dataItem.setTextDB1Process("완료");
                            } else {
                                // DB 접속 실패, 돈 없음, 동명이인
                                dataItem.setTextDB1Process(filesContent.get(j)[k]);
                            }
                        }
                    }

                    if (fileMiddle >= 8 && fileMiddle <= 10 && k == 7) { // 중단위 길이 8 = 이메일 전송 로그
                        numberEmail++;
                        // 이메일 전송 상태 불러오기
                        if (filesContent.get(j)[k] == null) {
                            dataItem.setTextEmailProcess("알 수 없음");
                        } else if (filesContent.get(j)[k].contains("정상")) {
                            dataItem.setTextEmailProcess("정상");
                        } else {
                            // 에러 발생 시
                            dataItem.setTextEmailProcess(filesContent.get(j)[k]);
                        }
                    }

                    if (fileMiddle == 11 && k == 0) { // 중단위 길이 11 = DB연동2
                        // 처리 시간 불러오기
                        dataItem.setTextProcesstime(filesContent.get(j)[k]);
                    }

                    if (fileMiddle == 11 && k == 9) { // 중단위 길이 11 = DB연동2
                        numberDB++;
                        // DB(처리 완료) 상태
                        if (filesContent.get(j)[k] == null) {
                            dataItem.setTextDB2Process("알 수 없음");
                        } else if (filesContent.get(j)[k].contains("완료")) {
                            dataItem.setTextDB2Process("완료");
                        } else {
                            // 에러 발생 시
                            dataItem.setTextDB2Process(filesContent.get(j)[k]);
                        }
                    }

                    // 게임 키 지정 못받은 상태에서 재 전송한 경우 처리과정
                    if (fileMiddle == 15) { // 중단위 길이 15 = 게임 키 가져오기
                        numberGamekey++;
                        if (k == 10) {
                            // 게임키 ID 불러오기
                            dataItem.setTextGamekeyID(filesContent.get(j)[k]);
                        } else if (k == 11) {
                            // 게임키 불러오기
                            dataItem.setTextGamekey(filesContent.get(j)[k]);
                        } else if (k == 13) {
                            // DB1연동 타입 불러오기
                            dataItem.setTextDB1Process(filesContent.get(j)[k]);
                        }
                    }
                }

                if (dataItem.getTextDB1Process() == null) {
                    dataItem.setTextDB1Process("");
                }

                if (dataItem.getTextDB2Process() == null) {
                    dataItem.setTextDB2Process("");
                }

                if (dataItem.getTextEmailProcess() == null) {
                    dataItem.setTextEmailProcess("");
                }


                if (j == filesContent.size() - 1) { // 파일 내용 끝부분일 경우

                    if (numberDB <= 1 && numberEmail <= 1) { // 재 송신 한 경우 아니라면
                        if (dataItem.getTextDB1Process().contains("완료")) {
                            if (dataItem.getTextEmailProcess().contains("정상")) {
                                if (dataItem.getTextDB2Process().contains("완료")) {
                                    dataItem.setTextProcess("전송 성공");
                                } else {
                                    dataItem.setTextProcess("전송 성공(DB 업데이트 실패)");
                                }
                            } else {
                                dataItem.setTextProcess("전송 실패");
                            }
                        } else {
                            if (dataItem.getTextDB1Process().contains("(02)")) {
                                dataItem.setTextProcess("미 전송(동일 이름 존재)");
                            } else if (dataItem.getTextDB1Process().contains("(03)")) {
                                dataItem.setTextProcess("미 전송(게임키 없음)");
                            } else if (dataItem.getTextDB1Process().contains("(04)")) {
                                dataItem.setTextProcess("미 전송(금액 부족)");
                            } else if (dataItem.getTextDB1Process().contains("(00)")) {
                                dataItem.setTextProcess("DB 접속 실패");
                            } else {
                                dataItem.setTextProcess("미 전송(알 수 없는 에러)");
                            }
                        }
                    } else if (numberGamekey > 0) {// 게임 키를 새로 받아 온 상태라면
                        if (dataItem.getTextDB1Process().contains("완료")) {
                            if (dataItem.getTextEmailProcess().contains("정상")) {
                                if (dataItem.getTextDB2Process().contains("완료")) {
                                    dataItem.setTextProcess("전송 성공");
                                } else {
                                    dataItem.setTextProcess("전송 성공(DB 업데이트 실패)");
                                }
                            } else {
                                dataItem.setTextProcess("전송 실패");
                            }
                        } else if (dataItem.getTextDB1Process().contains("(03)")) {
                            dataItem.setTextProcess("미 전송(게임키 없음)");
                        } else if (dataItem.getTextDB1Process().contains("(00)")) {
                            dataItem.setTextProcess("DB 접속 실패");
                        } else {
                            dataItem.setTextProcess("미 전송(알 수 없는 에러)");
                        }
                    }
                    if (numberDB > 1 || numberEmail > 1) {
                        if (dataItem.getTextEmailProcess().contains("정상")) {
                            if (dataItem.getTextDB2Process().contains("완료")) {
                                dataItem.setTextProcess("재 전송 성공");
                            } else {
                                dataItem.setTextProcess("재 전송 성공(DB 업데이트 실패)");
                            }
                        } else {
                            dataItem.setTextProcess("재 전송 실패");
                        }
                    }

                    if (dataItem.getTextProcess() == null) {
                        dataItem.setTextProcess("미 전송");
                    }

                    String process = dataItem.getTextProcess();


                    String textprocess = process;
                    if (textprocess.contains("성공")) {
                        textprocess = "전송 성공";
                    } else if (textprocess.contains("미 전송")) {
                        textprocess = "미 전송";
                    } else if (textprocess.contains("실패")) {
                        textprocess = "전송 실패";
                    }

                    SpannableString spannableString = new SpannableString(textprocess);

                    int start = 0;
                    int end = textprocess.length();

                    if (textprocess.contains("전송 성공")) {
                        if (process.contains("DB")) {
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#e79c2b")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 주황색
                        } else {
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#65b5ce")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 파란색
                        }
                    } else {
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#d1656f")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //빨간색
                    }
                    adapter.addItem(dataItem.getTextNum(), dataItem.getTextName(), dataItem.getTextEmail(), spannableString);
                    listdata.add(dataItem);
                    dataItem = new ListViewItem();
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // 팝업 설정
    public void Dialog(int dataNum) {
        Context mContext = MainActivity.thisContext; // 메인 엑티비티 컨텍트를 가져옴

        // 다이얼로그(팝업창) 준비
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(mContext);

        View layout = inflater.inflate(R.layout.dialog_email,(ViewGroup) view.findViewById(R.id.layout_email)); // 레이아웃 뷰를 가져옴

        TextView textViewName = layout.findViewById(R.id.textDialogName);
        TextView textViewEmail = layout.findViewById(R.id.textDialogEmail);
        TextView textViewDepTime = layout.findViewById(R.id.textDialogDepTime);
        TextView textViewSend = layout.findViewById(R.id.textDialogSend);
        TextView textViewGamekey = layout.findViewById(R.id.textDialogGamekey);
        TextView textViewTime = layout.findViewById(R.id.textDialogTime);

        ListViewItem viewDataItem = new ListViewItem();

        viewDataItem = listdata.get(dataNum);

        String textName = viewDataItem.getTextName();
        String textEmail = viewDataItem.getTextEmail();
        String ViewDepTime = viewDataItem.getTextDepTime();
        String textSend = viewDataItem.getTextProcess();
        String textGamekey = viewDataItem.getTextGamekey();
        String textTime = viewDataItem.getTextProcesstime();

        if (textName == null) {
            textName = "";
        }
        if (textEmail == null) {
            textEmail = "";
        }
        if (textSend == null) {
            textSend = "";
        }
        if (textGamekey == null) {
            textGamekey = "";
        }
        if (textTime == null) {
            textTime = "";
        }

        textViewName.setText(textName);
        textViewEmail.setText(textEmail);
        textViewDepTime.setText(ViewDepTime);
        textViewSend.setText(textSend);
        textViewGamekey.setText(textGamekey);
        textViewTime.setText(textTime);

        final int final_dataNum = dataNum;
        final ListViewItem finalViewDataItem = viewDataItem;
        final String final_textGamekey = textGamekey;

        dbDialog.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dbDialog.setNegativeButton("로그", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {
                logDialog(final_dataNum);
            }
        });

        String dialogButton = "게임 키 재 전송"; // 게임 키 칸이 빈칸이 아닐 경우

        if (!textEmail.equals("")) {
            // Email 칸이 빈칸이 아니면 (빈칸일 경우는 DB1 단계에서 DB 접속 X or 금액 부족 or 동일 이름 존재)
            if (final_textGamekey.equals("")) {
                // 게임 키 칸이 빈칸 이면
                dialogButton = "게임 키 전송";
            }

            dbDialog.setNeutralButton(dialogButton, new DialogInterface.OnClickListener() {
                // 전송 버튼 클릭 시 동작
                public void onClick(DialogInterface dialog, int which) {
                    if (PreferenceManager.getBoolean(MainActivity.thisContext, "EMAIL") && PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
                        // DB와 이메일이 등록이 되어 있을 경우
                        Toast.makeText(MainActivity.thisContext.getApplicationContext(), "게임 키를 전송합니다.", Toast.LENGTH_SHORT).show();
                        KeyProcess resendGamekey = new KeyProcess();
                        if (final_textGamekey.equals("")) {
                            // 게임 키 칸이 빈칸 이면
                            if(resendGamekey.StartDBLink("5")) {
                                resendGamekey.saveLog("GAMEKEY");
                                String[] array_Gamekey = resendGamekey.getArray_Gamekey();

                                finalViewDataItem.setTextGamekeyID(array_Gamekey[10]);
                                finalViewDataItem.setTextGamekey(array_Gamekey[11]);
                            } else {
                                Toast.makeText(MainActivity.thisContext.getApplicationContext(), "게임 키를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        String textName = finalViewDataItem.getTextName();
                        String textEmail = finalViewDataItem.getTextEmail();
                        String textGamekey = finalViewDataItem.getTextGamekey();

                        String textUserID = finalViewDataItem.getTextUserID();
                        String textGamekeyID = finalViewDataItem.getTextGamekeyID();
                        String textFile = finalViewDataItem.getTextFile();

                        if (!textGamekey.equals("")) {
                            resendGamekey.setResendGamekey(textName, textEmail, textGamekey, textUserID, textGamekeyID, textFile);

                            if (resendGamekey.sendEmail()) {// 메일 보내기
                                Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "(" + textEmail + ") 님에게 게임 키를 보냈습니다.", Toast.LENGTH_SHORT).show();
                                finalViewDataItem.setTextProcess("재 전송 성공");
                            } else {
                                // 메일 보내기 실패한 경우
                                Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "(" + textEmail + ") 님에게 이메일 전송하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                finalViewDataItem.setTextProcess("재 전송 실패");
                            }

                            resendGamekey.StartDBLink("4"); // DB 연동 (유저 상태 및 게임 키 상태 업데이트)

                            resendGamekey.saveLog("RESEND");

                            String DB2Time = resendGamekey.getDB2Time();
                            finalViewDataItem.setTextProcesstime(DB2Time);
                            listdata.set(final_dataNum, finalViewDataItem);
                        }
                        Dialog(final_dataNum);

                    } else {
                        if (!PreferenceManager.getBoolean(MainActivity.thisContext, "EMAIL")) {
                            // 이메일이 등록이 되어 있지 않을 경우
                            Toast.makeText(MainActivity.thisContext.getApplicationContext(), "이메일을 등록해야 전송이 가능합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB를 등록해야 전송이 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        Dialog(final_dataNum);
                    }
                }
            });
        }
        dbDialog.setTitle("상세 정보"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout);

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }

    // 롱 클릭 시 다이얼로그
    public void longDialog(int dataNum) {
        Context mContext = MainActivity.thisContext;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(mContext);

        View layout = inflater.inflate(R.layout.dialog_db_on,(ViewGroup) view.findViewById(R.id.layout_db_on));
        TextView textViewName = layout.findViewById(R.id.textView);

        ListViewItem viewDataItem = new ListViewItem();
        viewDataItem = listdata.get(dataNum);

        final String textFile = viewDataItem.getTextFile();
        final String textName = viewDataItem.getTextName();

        String textViewText = textName + " 님의 전송 내역을 삭제하시겠습니까?";

        textViewName.setText(textViewText);

        dbDialog.setNeutralButton("예", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "님의 전송 내역을 삭제합니다.", Toast.LENGTH_SHORT).show();
                if (FileLog.fileDelete(textFile)) {
                    Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "님의 전송 내역을 삭제 하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "님의 전송 내역을 삭제하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                ListViewList ();
            }
        });

        dbDialog.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dbDialog.setTitle("전송 내역 삭제"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout);

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }

    // 로그 다이얼로그
    public void logDialog(int dataNum) {
        Context mContext = MainActivity.thisContext;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(mContext);

        View layout = inflater.inflate(R.layout.dialog_db_on,(ViewGroup) view.findViewById(R.id.layout_db_on));
        TextView textViewName = layout.findViewById(R.id.textView);
        textViewName.setMovementMethod(new ScrollingMovementMethod());
        textViewName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        ListViewItem viewDataItem = new ListViewItem();
        viewDataItem = listdata.get(dataNum);

        String textFile = viewDataItem.getTextFile();
        String textName = viewDataItem.getTextName();

        String textViewText = FileLog.FileLoad(textFile, false);

        textViewName.setText(textViewText);

        dbDialog.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dbDialog.setTitle(textName + " 님의 로그 내역"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout);

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }
}