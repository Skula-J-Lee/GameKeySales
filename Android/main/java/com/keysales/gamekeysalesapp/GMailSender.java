package com.keysales.gamekeysalesapp;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;
    private String emailCode;

    String[] array_Email = new String[8];

    public static String TAG = "SendMail";

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;
        emailCode = createEmailCode();
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.setProperty("mail.smtp.quitwait", "false");

        //구글에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달해준다.
        session = Session.getDefaultInstance(props, this);
    }

    public String getEmailCode() {
        return emailCode;
    } //생성된 이메일 인증코드 반환

    private String createEmailCode() { //이메일 인증코드 생성
        String[] str = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String newCode = new String();

        for (int x = 0; x < 8; x++) {
            int random = (int) (Math.random() * str.length);
            newCode += str[random];
        }

        return newCode;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        //해당 메서드에서 사용자의 계정(id & password)을 받아 인증받으며 인증 실패시 기본값으로 반환됨.
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String recipients, boolean type) throws Exception {
        MimeMessage message = new MimeMessage(session);
        if (type) {
            array_Email[2] = "구글 계정을 설정 합니다.";
        }
        Log.d(TAG, "구글 계정을 설정 합니다.");
        message.setSender(new InternetAddress(user));  //본인 이메일 설정

        if (type) {
            array_Email[3] = "내용을 설정합니다.";
        }
        Log.d(TAG, "내용을 설정합니다.");
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/html;charset=euc-kr")); //본문 내용을 byte단위로 쪼개어 전달

        if (type) {
            array_Email[4] = "제목을 설정합니다.";
        }
        Log.d(TAG, "제목을 설정합니다.");
        message.setSubject(subject); //해당 이메일의 본문 설정

        message.setDataHandler(handler);

        if (type) {
            array_Email[5] = "상대방 이메일을 설정합니다.";
        }
        Log.d(TAG, "상대방 이메일을 설정합니다.");
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

        if (type) {
            array_Email[6] = "이메일을 전송합니다.";
        }
        Log.d(TAG, "이메일을 전송합니다.");
        Transport.send(message); //메시지 전달
    }

    public String[] getArray_Email () {
        return array_Email;
    }

    public void setArray_Email (String[] array_Email) {
        this.array_Email = array_Email;
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }

}
