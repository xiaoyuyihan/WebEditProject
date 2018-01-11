package com.utils;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2018/1/4.
 */

public class TypeConverUtil {

    public static ArrayList<Integer> intToList(Parcel paramParcel) {
        ArrayList localArrayList = new ArrayList();
        int[] arrayOfInt = new int[paramParcel.readInt()];
        paramParcel.readIntArray(arrayOfInt);
        for (int i = 0; i < arrayOfInt.length; i++)
            localArrayList.add(Integer.valueOf(arrayOfInt[i]));
        return localArrayList;
    }

    public static int[] listToInt(List<Integer> paramList) {
        int[] arrayOfInt = new int[paramList.size()];
        for (int i = 0; i < paramList.size(); i++)
            arrayOfInt[i] = paramList.get(i).intValue();
        return arrayOfInt;
    }

    public static void TimeMSToMin(int ms,int[] mins){
        double min = ms/1000/60;
        String[] minString=String.valueOf(min).split(".");
        if (!(mins.length<2)){
            mins[0]=Integer.valueOf(minString[0]);
            mins[1]=Integer.valueOf(minString[1]);
        }
    }

    public static String TimeMSToMin(String ms){
        long l = Long.parseLong(ms) / 1000L;
        String str = String.valueOf(l % 60L);
        if (str.length() <= 1)
            str = "0" + str;
        return l / 60L + ":" + str;
    }

    public static String TimeMSToMin(int ms){
        long l = ms / 1000L;
        String str = String.valueOf(l % 60L);
        if (str.length() <= 1)
            str = "0" + str;
        return l / 60L + ":" + str;
    }
}
