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
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keysales.gamekeysalesapp.KeyProcess;
import com.keysales.gamekeysalesapp.MainActivity;
import com.keysales.gamekeysalesapp.PersonalData;
import com.keysales.gamekeysalesapp.PreferenceManager;
import com.keysales.gamekeysalesapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    private ListView listview;
    private ListViewAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View view;

    public ArrayList<PersonalData> dbArrayList = new ArrayList<>();

    public Fragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_1, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        ListViewList();

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

        // 리스트 아이템 클릭 시 행동
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //int i = (ForegroundService.isMyServiceRunning) ? 1 : 0;
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Dialog(position);
            }
        }) ;


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
        listview = (ListView) view.findViewById(R.id.listviewitem);
        listview.setAdapter(adapter);

        // DB 불러오기
        if (PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
            Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB에서 불러오는 중입니다.", Toast.LENGTH_SHORT).show();
            Log.d("Fragment", "DB 불러오는 중");

            KeyProcess DBLoad = new KeyProcess();
            if(DBLoad.StartDBLink("1")) {
                dbArrayList = new ArrayList<>();
                dbArrayList = DBLoad.getDBListArrayList();

                // dbArrayList 개수 반복
                for (int i = dbArrayList.size() - 1; i >= 0; i--) {
                    PersonalData dbData = dbArrayList.get(i);

                    String Member_process = dbData.getMember_process();

                    int process_length = Member_process.length();
                    String process_first = Member_process.substring(0, 1);
                    String process;

                    if (process_length == 1) {
                        switch (process_first) {
                            case "L":
                                process = "금액 부족";
                                break;
                            case "Y" :
                            case "O" :
                                process = "입금 완료";
                                break;
                            default:
                                process = "미 입금";
                                break;
                        }
                    } else if (process_length == 2) {
                        String process_second = Member_process.substring(1, 2);

                        switch (process_second) {
                            case "Y":

                                process = "처리 완료";
                                break;
                            case "G":
                                process = "입금 완료(게임 키 없음)";
                                break;
                            case "E":
                                process = "입금 완료(이메일 전송 실패)";
                                break;
                            case "P":
                                process = "처리 중";
                                break;
                            default:
                                process = "알 수 없음";
                                break;
                        }
                    } else {
                        process = "알 수 없음";
                    }

                    if (process_first.equals("O")) {
                        process += "(금액 초과)";
                    }



                    String textprocess = process;
                    if (textprocess.contains("처리 완료")) {
                        textprocess = "처리 완료";
                    } else if (textprocess.contains("입금 완료")) {
                        textprocess = "입금 완료";
                    }

                    SpannableString spannableString = new SpannableString(textprocess);

                    int start = 0;
                    int end = textprocess.length();

                    if (textprocess.contains("처리 완료")) {
                        if (process.contains("금액 초과")) {
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#e79c2b")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 주황색
                        } else {
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#65b5ce")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 파란색
                        }
                    } else if (textprocess.contains("입금 완료") || textprocess.contains("처리 중")) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#e79c2b")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //주황색
                    } else if (textprocess.contains("미 입금")) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#747474")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //회색
                    } else { // 나머지 (알 수 없음, 금액 부족)
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#d1656f")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //빨간색

                    }


                    dbData.setMember_db_process(process);
                    dbArrayList.set(i, dbData);
                    adapter.addItem(dbData.getMember_id(), dbData.getMember_name(), dbData.getMember_email(), spannableString);
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB에서 불러오기 완료했습니다.", Toast.LENGTH_SHORT).show();
                Log.d("Fragment", "DB 불러오기 완료");
            } else {
                Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB에서 불러오기를 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.thisContext.getApplicationContext(), "DB가 등록되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    // 팝업 설정
    public void Dialog(int dataNum) {
        final int final_dataNum = dataNum;
        dataNum = dbArrayList.size() - dataNum - 1;

        Context mContext = MainActivity.thisContext; // 메인 엑티비티 컨텍트를 가져옴

        // 다이얼로그(팝업창) 준비
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(mContext);

        View layout = inflater.inflate(R.layout.dialog_email,(ViewGroup) view.findViewById(R.id.layout_email)); // 레이아웃 뷰를 가져옴

        TextView ViewName = layout.findViewById(R.id.DialogName);
        TextView ViewEmail = layout.findViewById(R.id.DialogEmail);
        TextView ViewDepTime = layout.findViewById(R.id.DialogDepTime);
        TextView ViewSend = layout.findViewById(R.id.DialogSend);
        TextView ViewGamekey = layout.findViewById(R.id.DialogGamekey);
        TextView ViewTime = layout.findViewById(R.id.DialogTime);

        TextView textViewName = layout.findViewById(R.id.textDialogName);
        TextView textViewEmail = layout.findViewById(R.id.textDialogEmail);
        TextView textViewDepTime = layout.findViewById(R.id.textDialogDepTime);
        TextView textViewSend = layout.findViewById(R.id.textDialogSend);
        TextView textViewGamekey = layout.findViewById(R.id.textDialogGamekey);
        TextView textViewTime = layout.findViewById(R.id.textDialogTime);

        ViewName.setText("이름");
        ViewEmail.setText("처리 현황");
        ViewDepTime.setText("신청 시간");
        ViewSend.setText("입금 시간");
        ViewGamekey.setText("처리 시간");
        ViewTime.setText("입금 금액");

        PersonalData viewDataItem = dbArrayList.get(dataNum);

        String textName = viewDataItem.getMember_name();
        String textEmail = viewDataItem.getMember_db_process();
        String textDepTime = viewDataItem.getMember_regdate();
        String textSend = viewDataItem.getMember_depdate();
        String textGamekey = viewDataItem.getMember_prodate();
        String textTime = viewDataItem.getMember_depmoney() + "원";

        if (textName.equals("null")) {
            textName = "";
        }
        if (textEmail.equals("null")) {
            textEmail = "";
        }
        if (textSend.equals("null")) {
            textSend = "";
        }
        if (textGamekey.equals("null")) {
            textGamekey = "";
        }
        if (textTime.equals("null원")) {
            textTime = "";
        }

        textViewName.setText(textName);
        textViewEmail.setText(textEmail);
        textViewDepTime.setText(textDepTime);
        textViewSend.setText(textSend);
        textViewGamekey.setText(textGamekey);
        textViewTime.setText(textTime);

        final PersonalData finalviewDataItem = viewDataItem;

        if (textEmail.equals("미 입금")) {
            dbDialog.setNeutralButton("게임키 전송", new DialogInterface.OnClickListener() {
                // 닫기 버튼 클릭 시 동작
                public void onClick(DialogInterface dialog, int which) {
                    String textName = finalviewDataItem.getMember_name();
                    String textEmail = finalviewDataItem.getMember_email();

                    if (PreferenceManager.getBoolean(MainActivity.thisContext, "EMAIL") && PreferenceManager.getBoolean(MainActivity.thisContext, "DB")) {
                        // DB와 이메일이 등록이 되어 있을 경우

                        Toast.makeText(MainActivity.thisContext.getApplicationContext(), "게임 키를 전송합니다.", Toast.LENGTH_SHORT).show();
                        KeyProcess resendGamekey = new KeyProcess();

                        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Date time = new Date();
                        String filetime = timeFormat.format(time);

                        String filename = filetime + "." + finalviewDataItem.getMember_name();
                        finalviewDataItem.setMember_filename(filename);

                        resendGamekey.DBPersonalData.setMember_filename(filename);

                        resendGamekey.array_SMS[0] = timeFormat.format(time);
                        resendGamekey.array_SMS[1] = "게임키를 재 송신 합니다. 아래 공백은 양식을 맞추기 위함입니다.";
                        resendGamekey.array_SMS[2] = finalviewDataItem.getMember_name();
                        resendGamekey.array_SMS[3] = " ";
                        resendGamekey.array_SMS[4] = " ";
                        resendGamekey.array_SMS[5] = " ";

                        resendGamekey.array_Insert[0] = timeFormat.format(time);

                        for (int i = 0; i < resendGamekey.array_Insert.length; i++) {
                            String content = "";
                            switch (i) {
                                case 0 :
                                    content = timeFormat.format(time);
                                    break;
                                case 1:
                                    content = "게임키를 재 송신 합니다. 아래 공백은 양식을 맞추기 위함입니다.";
                                    break;
                                case 12:
                                    content = textEmail;
                                    break;
                                default:
                                    content = "";
                                    break;
                            }
                            resendGamekey.array_Insert[i] = content;
                        }

                        if(resendGamekey.StartDBLink("5")) {
                            resendGamekey.saveLog("NEWGAMEKEY");
                            String[] array_Gamekey = resendGamekey.getArray_Gamekey();

                            finalviewDataItem.setMember_gamekeyid(array_Gamekey[10]);
                            finalviewDataItem.setMember_gamekey(array_Gamekey[11]);
                        } else {
                            Toast.makeText(MainActivity.thisContext.getApplicationContext(), "게임 키를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }


                        String textGamekey = finalviewDataItem.getMember_gamekey();

                        String textUserID = finalviewDataItem.getMember_id();
                        String textGamekeyID = finalviewDataItem.getMember_gamekeyid();
                        String textFile = finalviewDataItem.getMember_filename();

                        if (!textGamekey.equals("")) {
                            resendGamekey.setResendGamekey(textName, textEmail, textGamekey, textUserID, textGamekeyID, textFile);

                            if (resendGamekey.sendEmail()) {// 메일 보내기
                                Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "(" + textEmail + ") 님에게 게임 키를 보냈습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                // 메일 보내기 실패한 경우
                                Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "(" + textEmail + ") 님에게 이메일 전송하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }

                            resendGamekey.StartDBLink("4"); // DB 연동 (유저 상태 및 게임 키 상태 업데이트)

                            resendGamekey.saveLog("RESEND");
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

        dbDialog.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dbDialog.setTitle("상세 정보"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout);

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }


    // 롱 클릭 시 다이얼로그
    public void longDialog(int dataNum) {
        dataNum = dbArrayList.size() - dataNum - 1;

        Context mContext = MainActivity.thisContext;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dbDialog = new AlertDialog.Builder(mContext);

        View layout = inflater.inflate(R.layout.dialog_db_on,(ViewGroup) view.findViewById(R.id.layout_db_on));
        TextView textViewName = layout.findViewById(R.id.textView);

        PersonalData viewDataItem = new PersonalData();
        viewDataItem = dbArrayList.get(dataNum);

        final String textUserID = viewDataItem.getMember_id();
        final String textName = viewDataItem.getMember_name();

        String textViewText = textName + " 님의 데이터를 삭제하시겠습니까?";

        textViewName.setText(textViewText);

        dbDialog.setNeutralButton("예", new DialogInterface.OnClickListener() {
            // 닫기 버튼 클릭 시 동작
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "님의 데이터를 삭제합니다.", Toast.LENGTH_SHORT).show();

                KeyProcess DBLoad = new KeyProcess();
                if(DBLoad.StartDBLink("6", textUserID)) {
                    Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "님의 데이터를 삭제 하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.thisContext.getApplicationContext(), textName + "님의 데이터를 삭제하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                ListViewList ();
            }
        });

        dbDialog.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dbDialog.setTitle("데이터 삭제"); // 다이얼로그(팝업창) 제목 설정
        dbDialog.setView(layout);

        // 다이얼로그(팝업창 표시)
        AlertDialog ad = dbDialog.create();
        ad.show();
    }
}
