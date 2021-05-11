package com.keysales.gamekeysalesapp.ui.main;

import android.text.SpannableString;

public class ListViewItemGamekey {
    private String textNum;
    private String textGamekey;
    private String textUsed;
    private String TextTime;
    private SpannableString spanused;


    public void setTextNum(String num) {
        textNum = num ;
    }
    public void setTextGamekey(String gamekey) {
        textGamekey = gamekey ;
    }
    public void setTextUsed(String used) {
        textUsed = used ;
    }
    public void setTextTime(String time) {
        TextTime = time ;
    }
    public void setSpanUsed(SpannableString spanused) {this.spanused = spanused;}

    public String getTextNum() {
        return this.textNum ;
    }
    public String getTextGamekey() {
        return this.textGamekey ;
    }
    public String getTextUsed() {
        return this.textUsed ;
    }
    public String getTextTime() {
        return this.TextTime ;
    }
    public SpannableString getSpanUsed() {return this.spanused;}
}
