package com.keysales.gamekeysalesapp;

import com.keysales.gamekeysalesapp.ui.main.Fragment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileLog {

    // 파일 저장
    public static void FileSave(String saveDate, String name, String[] arrayData, boolean saveType) {
        String filename = saveDate + "." + name;
        String fileContents = "";

        if (name.equals("")) {
            filename = saveDate;
        }

        if (!saveType) {
            fileContents = FileLoad(filename, false);
        }

        for (int i=0; i < arrayData.length; i++) {
            fileContents = fileContents + arrayData[i];

            if (i == arrayData.length - 1) {
                fileContents = fileContents + "\r==============================\r";
            } else {
                fileContents = fileContents + "\r";
            }
        }
        File directory = MainActivity.thisContext.getFilesDir();
        File file = new File(directory, filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(fileContents.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 파일 불러오기
    public static String FileLoad(String filename, boolean type) {
        File directory = MainActivity.thisContext.getFilesDir();
        File file = new File(directory, filename);

        StringBuffer stringBuffer = new StringBuffer();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            String[] insertData;
            String[] tempData = new String [18];

            // 빈칸 만들기
            for (int i=0; i<18; i++) {
                tempData[i] = " ";
            }

            int i = 0; // 줄 수

            while (line != null) {
                if (type) {
                    if(!line.equals("==============================")) {
                        // 아닐경우 insertData에 넣기
                        tempData[i] = line;
                        i++;
                    } else {
                        // 줄 내용이 "==============================" 이면
                        int j;
                        for (j=17; j>0; j--) {
                            if (!tempData[j].equals(" ")) {
                                break;
                            }
                        }

                        // insertData에 tempData 넣기
                        insertData = new String [j+1];
                        for (int k=0; k<insertData.length; k++) {
                            insertData[k] = tempData[k];
                        }

                        //filesContent에 내용물 넣기
                        Fragment2.filesContent.add(insertData);

                        i = 0;

                        // 빈칸 만들기
                        for (int k=0; k<18; k++) {
                            tempData[k] = " ";
                        }
                    }
                } else {
                    stringBuffer.append(line).append('\n');
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred opening raw file for reading.
        } finally {
            if (type) {
                Fragment2.filesList.add((ArrayList) Fragment2.filesContent);
                Fragment2.filesContent = new ArrayList<>();
                return null;
            } else {
                String contents = stringBuffer.toString();
                return contents;
            }
        }
    }

    public static boolean FileNameLoad() {
        File directory = MainActivity.thisContext.getFilesDir();
        File file = new File(String.valueOf(directory));
        File[] files = file.listFiles();

        for (int i=0; i < files.length; i++) {
            String[] fileName = new String[1];
            fileName[0] = files[i].getName();
            Fragment2.filesContent.add(fileName); // filesContent에 파일 이름 넣기

            FileLoad(files[i].getName(), true);
        }

        return true;
    }

    // 파일 삭제
    public static boolean fileDelete(String filename) {
        //filePath : 파일경로 및 파일명이 포함된 경로입니다.

        File directory = MainActivity.thisContext.getFilesDir();
        File file = new File(directory, filename);

        try {
            // 파일이 존재 하는지 체크
            if (file.exists()) {
                file.delete();
                return true;  // 파일 삭제 성공여부를 리턴값으로 반환해줄 수 도 있습니다.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
