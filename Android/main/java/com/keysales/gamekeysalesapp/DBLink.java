package com.keysales.gamekeysalesapp;

import android.os.AsyncTask;
import android.util.Log;

import com.keysales.gamekeysalesapp.ui.main.ListViewItemGamekey;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URLEncoder;
import java.util.ArrayList;

public class DBLink extends AsyncTask<String, Void, String> {

    public ArrayList<PersonalData> mArrayList = new ArrayList<>();
    public ArrayList<ListViewItemGamekey> GamekeyArrayList = new ArrayList<>();;

    public String DBTAG = "DBLink";

    private String mJsonString;

    private String DBType;

    String[] returnResult = new String[8];

    // AsyncTask 실행 시작 부분
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // AsyncTask 실행 끝 부분
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }

    // [0] = Result
    // [1] = 에러 발생 시 에러 메시지 (기본 값 null)
    // [2] = 웹 주소
    // [3] = 계정 아이디
    // [4] = 이름
    // [5] = DB 연동 웹에 접속합니다.
    // [6] = 인터넷 응답 코드
    // [7] = 인터넷 접속 여부 / 실패할 경우 에러메시지
    @Override
    protected String doInBackground(String... params) {
        DBType = params[params.length - 1];

        String postParameters = "";

        String DB_ID = PreferenceManager.getString(MainActivity.thisContext, "DB_ID");
        String DB_PW = PreferenceManager.getStringAES(MainActivity.thisContext, "DB_PW");
        String DB_NAME = PreferenceManager.getString(MainActivity.thisContext, "DB_NAME");

        Log.d(DBTAG, "DB 연동 웹 주소 : " + params[0]);
        Log.d(DBTAG, "DB 계정 아이디 : " + DB_ID);
        Log.d(DBTAG, "DB 이름 : " + DB_NAME);

        switch (DBType) {
            case "0":
            case "1":
                // 유저 상태 조회
                // DB 연동 체크
                postParameters = "DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME +
                        "&dbtype=" + params[1];
                break;
            case "2":
                // 게임 키 추가

                try {
                    params[1] = URLEncoder.encode(params[1], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                postParameters = "DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME +
                        "&gamekey=" + params[1] +
                        "&dbtype=" + params[2];

                Log.d("dd", postParameters);
                break;
            case "3":
                // 이름 대조 및 게임 키 추출
                returnResult[2] = "DB 연동 웹 주소 : " + params[0];
                returnResult[3] = "DB 계정 아이디 : " + DB_ID;
                returnResult[4] = "DB 이름 : " + DB_NAME;
                postParameters = "username=" + (String) params[1] +
                        "&depdate=" + (String) params[2] +
                        "&depstate=" + (String) params[3] +
                        "&depmoney=" + (String) params[4] +
                        "&dbtype=" + (String) params[5] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            case "4":
                // 유저 상태 및 게임 키 상태 업데이트
                returnResult[2] = "DB 연동 웹 주소 : " + params[0];
                returnResult[3] = "DB 계정 아이디 : " + DB_ID;
                returnResult[4] = "DB 이름 : " + DB_NAME;

                postParameters = "userid=" + (String) params[1] +
                        "&gamekeyid=" + (String) params[2] +
                        "&userstate=" + (String) params[3] +
                        "&dbtype=" + (String) params[5] +
                        "&protime=" + (String) params[4] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            case "5":
                // 게임 키 가져오기
                returnResult[2] = "DB 연동 웹 주소 : " + params[0];
                returnResult[3] = "DB 계정 아이디 : " + DB_ID;
                returnResult[4] = "DB 이름 : " + DB_NAME;

                postParameters = "dbtype=" + (String) params[1] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            case "6":
                // 유저 정보 삭제
                postParameters = "userid=" + (String) params[1] +
                        "&dbtype=" + (String) params[2] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            case "7":
                // 게임 키 정보 가져오기
                postParameters = "dbtype=" + (String) params[1] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            case "8":
                // 게임 키 삭제
                postParameters = "gamekeyid=" + (String) params[1] +
                        "&dbtype=" + (String) params[2] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            case "9":
                // 계좌번호 정보 가져오기
                postParameters = "dbtype=" + (String) params[1] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            case "10":
                // 계좌번호 설정
                postParameters = "bank=" + (String) params[1] +
                        "&name=" + (String) params[2] +
                        "&account=" + (String) params[3] +
                        "&money=" + (String) params[4] +
                        "&dbtype=" + (String) params[5] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            case "11":
                // 금일 개수 구하기
                postParameters = "dbtype=" + (String) params[1] +
                        "&DBID=" + DB_ID +
                        "&DBPW=" + DB_PW +
                        "&DBNAME=" + DB_NAME;
                break;
            default:
                Log.d(DBTAG, "입력할 값이 없습니다.");
                return null;
        }

        // 1. PHP 파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비합니다.
        // POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않습니다.
        String serverURL = params[0];

        // HTTP 메시지 본문에 포함되어 전송되기 때문에 따로 데이터를 준비해야 합니다.
        // 전송할 데이터는 “이름=값” 형식이며 여러 개를 보내야 할 경우에는 항목 사이에 &를 추가합니다.
        // 여기에 적어준 이름을 나중에 PHP에서 사용하여 값을 얻게 됩니다.

        try {
            switch (DBType) {
                case "3":
                case "4":
                case "5":
                    returnResult[5] = "DB 연동 웹에 접속합니다.";
                    break;
            }

            Log.d(DBTAG, "DB 연동 웹에 접속합니다.");

            // 2. HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송합니다.
            URL url = new URL(serverURL); // 주소가 저장된 변수를 이곳에 입력합니다.

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(3000); //3초안에 응답이 오지 않으면 예외가 발생합니다.
            httpURLConnection.setConnectTimeout(3000); //3초안에 연결이 안되면 예외가 발생합니다.
            httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 합니다.
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8")); //전송할 데이터가 저장된 변수를 이곳에 입력합니다. 인코딩을 고려해줘야 합니다.

            outputStream.flush();
            outputStream.close();

            // 3. 응답을 읽습니다.
            int responseStatusCode = httpURLConnection.getResponseCode();

            switch (DBType) {
                case "3":
                case "4":
                case "5":
                    returnResult[6] = "인터넷 응답 코드 : " + responseStatusCode;
                    break;
            }
            Log.d(DBTAG, "인터넷 응답 코드 : " + responseStatusCode);

            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                // 정상적인 응답 데이터
                switch (DBType) {
                    case "3":
                    case "4":
                    case "5":
                        returnResult[7] = "정상적으로 인터넷에 접속되었습니다.";
                        break;
                }
                Log.d(DBTAG, "정상적으로 인터넷에 접속되었습니다.");
                inputStream = httpURLConnection.getInputStream();
            } else {
                // 에러 발생
                switch (DBType) {
                    case "3":
                    case "4":
                    case "5":
                        returnResult[7] = "인터넷에 접속을 실패했습니다.";
                        break;
                }
                Log.d(DBTAG, "인터넷에 접속을 실패했습니다.");

                inputStream = httpURLConnection.getErrorStream();
            }

            // 4. StringBuilder를 사용하여 수신되는 데이터를 저장합니다.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();

            returnResult[0] = sb.toString().trim();
            return "success";
        } catch (Exception e) {
            switch (DBType) {
                case "3":
                case "4":
                case "5":
                    returnResult[7] = "인터넷에 접속하는데 에러가 발생하였습니다. 에러 메시지 : " + e;
                    break;
            }
            Log.d(DBTAG, "인터넷에 접속하는데 에러가 발생하였습니다. 에러 메시지 : " + e);
            if (ForegroundService.isMyServiceRunning) {
                ForegroundService.stopFGService("DB 연결에 실패하였습니다.", "서비스를 중지합니다.");
            }
            returnResult[0] = null;
            returnResult[1] = e.toString();
            return "error";
        }

        // [0] = Result
        // [1] = 에러 발생 시 에러 메시지 (기본 값 null)
        // [2] = 웹 주소
        // [3] = 계정 아이디
        // [4] = 이름
        // [5] = DB 연동 웹에 접속합니다.
        // [6] = 인터넷 응답 코드
        // [7] = 인터넷 접속 여부 / 실패할 경우 에러메시지
    }

    public String[] getArrayResult () {
        return returnResult;
    }
}