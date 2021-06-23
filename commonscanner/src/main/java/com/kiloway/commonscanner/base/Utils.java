package com.kiloway.commonscanner.base;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by 10158 on 2020/12/2.
 */

public class Utils {
    private static final String KLW_SIGN = "01D";
    public static boolean ValidateKilowayTidSign(String tid){
        String kilowayTID = "";
        if (TextUtils.isEmpty(tid)){
            return false;
        }
        if (tid.length()>=8){
            kilowayTID = tid.substring(2,5);
        }
        if (kilowayTID.equals(KLW_SIGN)){
            return true;
        }
        return false;
    }
}
