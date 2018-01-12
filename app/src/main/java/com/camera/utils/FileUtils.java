package com.camera.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by lw on 2017/12/24.
 */

public class FileUtils {

    public static String getVideoFilePath() {
        String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera/audio/" +
                System.currentTimeMillis() + ".mp4";
        filePathToFile(path);
        return path;
    }
    public static String getPhotoFilePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera/image/" +
                System.currentTimeMillis() + ".jpg";
    }
    public static String getAudioFilePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera/image/" +
                System.currentTimeMillis() + ".mp3";
    }

    public static boolean isFile(File path){
        return path.mkdir();
    }

    public static File filePathToFile(String path){
        File file=new File(path);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void insertProvider(Context context,File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}
