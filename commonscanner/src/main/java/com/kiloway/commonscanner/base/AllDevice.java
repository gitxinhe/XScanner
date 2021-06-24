package com.kiloway.commonscanner.base;

import android.content.Context;

import com.kiloway.railway.rfid.device.xt_device.Rlk5000Device;
import com.kiloway.railway.rfid.device.xt_device.Rlk5001Device;
import com.kiloway.railway.rfid.device.xt_device.RlkC72Device;
import com.kiloway.railway.rfid.device.xt_device.RlkUH55ED1Device;
import com.uhf.reader.RlkUHK71V164BSPDevice;
import com.uhf.reader.RlkUHK71V164BSP_B_Device;

import java.util.ArrayList;
import java.util.List;

import static com.kiloway.commonscanner.base.Constant.KLWUH45EX1;
import static com.kiloway.commonscanner.base.Constant.KLWUH55EC2;
import static com.kiloway.commonscanner.base.Constant.KLWUH55ED1;
import static com.kiloway.commonscanner.base.Constant.KLWUH55EH2;
import static com.kiloway.commonscanner.base.Constant.KLWUUHK71V164BSP;
import static com.kiloway.commonscanner.base.Constant.KLWUUHK71V164BSP_B;

/**
 * Created by 10158 on 2020/9/2.
 */

public class AllDevice {
    private static Device getDevice(String deviceName) {
        Device device = null;
        switch (deviceName) {
            case Constant.KLWUH55EH2:
                device = new Rlk5000Device();
                break;
            case Constant.KLWUH45EX1:
                device = new Rlk5001Device();
                break;
            case Constant.KLWUH55EC2:
                device = new RlkC72Device();
                break;
            case Constant.KLWUH55ED1:
                device = new RlkUH55ED1Device();
                break;
            case KLWUUHK71V164BSP:
                device = new RlkUHK71V164BSPDevice();
                break;
            case KLWUUHK71V164BSP_B:
                device = new RlkUHK71V164BSP_B_Device();
                break;
        }
        return device;
    }
    public static List<String> getAllDevice(){
        List<String> strings = new ArrayList<>();
        strings.add(KLWUH55EH2);
        strings.add(KLWUH45EX1);
        strings.add(KLWUH55EC2);
        strings.add(KLWUH55ED1);
        strings.add(KLWUUHK71V164BSP);
        strings.add(KLWUUHK71V164BSP_B);
        return strings;
    }
    public static Device initUHF(Context context) {
        return  getDevice(DeviceSetting.getDevice(context));
    }
    public static void setDefaultDevice(Context context,String deviceName) {
        DeviceSetting.setCurrentReader(context,deviceName);
    }
    public static void needValidateKlwLabel(Context context,boolean needValidate){
        DeviceSetting.setNeedValidateKlwLabel(context,needValidate);
    }
}
