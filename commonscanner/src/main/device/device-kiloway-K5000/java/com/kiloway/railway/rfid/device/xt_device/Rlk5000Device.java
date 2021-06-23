package com.kiloway.railway.rfid.device.xt_device;

import android.content.Context;
import android.util.Log;

import com.BRMicro.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.android.hdhe.uhf.readerInterface.TagModel;
import com.kiloway.commonscanner.base.BaseUtil;
import com.kiloway.commonscanner.base.Constant;
import com.kiloway.commonscanner.base.Device;
import com.kiloway.commonscanner.base.DeviceSetting;
import com.kiloway.commonscanner.base.Utils;
import com.kiloway.commonscanner.interfaces.IReader;
import com.kiloway.commonscanner.interfaces.ReaderConfig;
import com.kiloway.commonscanner.model.EpcInfo;

import java.util.ArrayList;
import java.util.List;

public class Rlk5000Device extends Device {
    private UhfReader mReader;
    private boolean startFlag = false;
    private Context context;

    @Override
    public void initReader(Context context) {
        this.context = context;
        mReader = UhfReader.getInstance();
        if (mReader!=null) {
            setReader(mReader);
            setPower(getPower());
        }
    }

    @Override
    public void unitReader() {
        if (null != mReader) {
            startFlag = false;
            setReadMode(startFlag);
        }
    }

    @Override
    public ReaderConfig configReader() {
        return null;
    }

    @Override
    public boolean inventoryTag() {
        startFlag = true;
        setReadMode(startFlag);
        new InventoryThread().start();
        return false;
    }

    @Override
    public boolean inventoryAnyTag() {
        inventoryTag();
        return false;
    }

    @Override
    public void stopReading() {
        startFlag = false;
        setReadMode(startFlag);
    }

    public void setReader(UhfReader reader) {
        this.mReader = reader;
    }

    class InventoryThread extends Thread {
        private List<TagModel> epcList;

        @Override
        public void run() {
            super.run();
            while (startFlag) {
                try {
                    if (mReader != null) {
                        epcList = mReader.inventoryRealTime(); //实时盘存
                        if (epcList != null && !epcList.isEmpty()) {
                            //播放提示音
                            for (TagModel tag  : epcList) {
                                String epcStr = Tools.Bytes2HexString(tag.getmEpcBytes(), tag.getmEpcBytes().length);
                                mReader.selectEPC(tag.getmEpcBytes());
                                byte[] tid = mReader.readFrom6C(IReader.Bank_TID,0,12,Tools.HexString2Bytes("00000000"));
                                String tidStr = Tools.Bytes2HexString(tid,tid.length);
                                //O1D表示kiloway的标签
                                onTagReadedEvent(new EpcInfo(epcStr,tidStr));
                            }
                        }
                    }
                    epcList = null;
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }
    }


    @Override
    public int writeEPC(String epc) {
        int membank = 1;// ;
        boolean isSuccess = mReader.writeTo6C(new byte[]{0, 0, 0, 0}, membank, 2, epc.getBytes().length / 2, epc.getBytes());
        Log.e("tag", isSuccess + "");
        if (isSuccess) {
            return 0;
        }
        return -1;
    }

    @Override
    public int getPower() {
        int power =  DeviceSetting.getPower(this.context, Constant.KLWUH55EH2_POWER,26);
        return power;
    }

    @Override
    public boolean setPower(int power) {
        DeviceSetting.setPower(this.context, Constant.KLWUH55EH2_POWER,power);
        Log.e("tag","功率："+power+"");
        return mReader.setOutputPower(power);
    }

    @Override
    public List<String> getPowerList() {
        List<String> powers = new ArrayList<>();
        for (int i = 16; i <= 26; i++) {
            powers.add(i+"");
        }
        return powers;
    }

    @Override
    public boolean readFilterData(String epc) {

        return false;
    }

    @Override
    public boolean readData() {
        //向保留区发送指令，起始位置为4，长度为1
        mReader.readFrom6C(IReader.Bank_RESERVED, 4, 1, BaseUtil.getHexByteArray("00000000"));
        return false;
    }

}
