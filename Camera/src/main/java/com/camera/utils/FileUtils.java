package com.camera.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by lw on 2017/12/24.
 */

public class FileUtils {

    public static String getVideoFilePath() {
        String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera/" +
                System.currentTimeMillis() + ".mp4";
        filePathToFile(path);
        return path;
    }
    public static String getPhotoFilePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera/" +
                System.currentTimeMillis() + ".jpg";
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
}
