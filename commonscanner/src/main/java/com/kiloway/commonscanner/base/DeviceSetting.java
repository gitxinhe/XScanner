package com.kiloway.commonscanner.base;

import android.content.Context;

/**
 * Created by 10158 on 2020/9/2.
 */

public class DeviceSetting {
    private static final String CURRENT_READER = "CURRENT_READER";
    private static final String NEED_VALIDATE_KLW_LABEL = "NEED_VALIDATE_KLW_LABEL";
    private static final String DEFAULT_DEVICE = Constant.KLWUH55ED1;
    public static String getDevice(Context context){
        return PreferencesUtils.getString(context, CURRENT_READER, DEFAULT_DEVICE);
    }
    public static void setCurrentReader(Context context,String currentReader) {
        PreferencesUtils.putString(context, CURRENT_READER, currentReader);
    }
    public static int getPower(Context context,String key,int defaultPower) {
        return PreferencesUtils.getInt(context, key,defaultPower);
    }
    public static void setPower(Context context,String key,int currentReaderPower) {
        PreferencesUtils.putInt(context, key, currentReaderPower);
    }
    public static boolean getNeedValidateKlwLabel(Context context){
        return PreferencesUtils.getBoolean(context, NEED_VALIDATE_KLW_LABEL);
    }
    public static void setNeedValidateKlwLabel(Context context,boolean NeedValidateKlwLabel) {
        PreferencesUtils.putBoolean(context, NEED_VALIDATE_KLW_LABEL, NeedValidateKlwLabel);
    }
}
