package com.keysales.gamekeysalesapp.ui.main;

import android.text.SpannableString;

public class ListViewItem {
    private String textnum;
    private String textname;
    private String textUserID;
    private String TextEmail;
    private String TextProcess;
    private String Textgamekey;
    private String TextgamekeyID;
    private String TextProcesstime;
    private String TextEmailProcess;
    private String TextDB1Process;
    private String TextDB2Process;
    private String TextFile;
    private String TextDepTime;
    private SpannableString SpanProcess;


    public void setTextNum(String num) {
        textnum = num ;
    }
    public void setTextName(String name) {
        textname = name ;
    }
    public void setTextUserID(String UserID) {
        textUserID = UserID ;
    }
    public void setTextEmail(String email) {
        TextEmail = email ;
    }
    public void setTextProcess(String process) {
        TextProcess = process ;
    }
    public void setTextGamekey(String gamekey) {
        Textgamekey = gamekey ;
    }
    public void setTextGamekeyID(String gamekeyID) {
        TextgamekeyID = gamekeyID ;
    }
    public void setTextProcesstime(String setTextProcesstime) { TextProcesstime = setTextProcesstime ; }
    public void setTextEmailProcess(String emailProcess) {
        TextEmailProcess = emailProcess ;
    }
    public void setTextDB1Process(String db1process) {
        TextDB1Process = db1process ;
    }
    public void setTextDB2Process(String db2Process) {
        TextDB2Process = db2Process ;
    }
    public void setTextFile(String TextFileName) {
        TextFile = TextFileName ;
    }
    public void setTextDepTime(String TextDepTime) {
        this.TextDepTime = TextDepTime ;
    }
    public void setSpanProcess(SpannableString TextSpanProcess) {
        this.SpanProcess = TextSpanProcess ;
    }


    public String getTextNum() {
        return this.textnum ;
    }
    public String getTextName() {
        return this.textname ;
    }
    public String getTextUserID() {
        return this.textUserID ;
    }
    public String getTextEmail() {
        return this.TextEmail ;
    }
    public String getTextProcess() { return this.TextProcess ; }
    public String getTextGamekey() {
        return this.Textgamekey ;
    }
    public String getTextGamekeyID() {
        return this.TextgamekeyID ;
    }
    public String getTextProcesstime() {
        return this.TextProcesstime ;
    }
    public String getTextEmailProcess() {
        return this.TextEmailProcess ;
    }
    public String getTextDB1Process() {
        return this.TextDB1Process ;
    }
    public String getTextDB2Process() {
        return this.TextDB2Process ;
    }
    public String getTextFile() {
        return this.TextFile ;
    }
    public String getTextDepTime() {
        return this.TextDepTime ;
    }
    public SpannableString getSpanProcess() {
        return this.SpanProcess ;
    }
}