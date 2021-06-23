package com.uhf.reader;

import android.content.Context;
import android.util.Log;

import com.BRMicro.Tools;
import com.handheld.uhfr.UHFRManager;
import com.kiloway.commonscanner.base.BaseUtil;
import com.kiloway.commonscanner.base.Constant;
import com.kiloway.commonscanner.base.Device;
import com.kiloway.commonscanner.base.DeviceSetting;
import com.kiloway.commonscanner.base.Utils;
import com.kiloway.commonscanner.interfaces.ReaderConfig;
import com.kiloway.commonscanner.model.EpcInfo;
import com.uhf.api.cls.Reader;

import java.util.ArrayList;
import java.util.List;

public class RlkUHK71V164BSP_B_Device extends Device {
    private UHFRManager mReader;
    private boolean startFlag = false;
    private Context context;

    @Override
    public void initReader(Context context) {
        this.context = context;
        mReader = UHFRManager.getInstance();
        if (mReader!=null) {
            setReader(mReader);
            Reader.READER_ERR err = mReader.setPower(33,33);//set uhf module power
            if(err== Reader.READER_ERR.MT_OK_ERR){
                mReader.setRegion(Reader.Region_Conf.valueOf(1));
//                showToast(getString(R.string.inituhfsuccess));
            }
         //   setPower(getPower());
        }
    }

    @Override
    public void unitReader() {
        if (null != mReader) {
             mReader.close();
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
        mReader.stopTagInventory();
    }

    public void setReader(UHFRManager reader) {
        this.mReader = reader;
    }

    class InventoryThread extends Thread {
        private List<Reader.TAGINFO> epcList;

        @Override
        public void run() {
            super.run();
            while (startFlag) {
                try {
                    if (mReader != null) {
                        epcList = mReader.tagInventoryByTimer((short) 50); //实时盘存
                        if (epcList != null && epcList.size() > 0) {
                            //播放提示音
                            for (Reader.TAGINFO tag  : epcList) {
                                byte[] epcdata = tag.EpcId;
                                String data = Tools.Bytes2HexString(epcdata, epcdata.length);
                                String tid = Tools.Bytes2HexString(tag.EmbededData, tag.EmbededDatalen);
                                //O1D表示kiloway的标签
                                onTagReadedEvent(new EpcInfo(data, tid));
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
        Reader.READER_ERR error =  mReader.writeTagData((char)1,2,BaseUtil.getHexByteArray(epc)
                ,BaseUtil.getHexByteArray(epc).length,BaseUtil.getHexByteArray("00000000"),(short) 1000);
        return  error == Reader.READER_ERR.MT_OK_ERR?0:1;
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
        return mReader.setPower(power,power)==Reader.READER_ERR.MT_OK_ERR?true:false;
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
        mReader.getTagDataByFilter(0, 4, 1, BaseUtil.getHexByteArray("00000000"), (short) 1000, BaseUtil.getHexByteArray(epc), 1, 2, true);
        return false;
    }

    @Override
    public boolean readData() {
        //向保留区发送指令，起始位置为4，长度为1
        //mReader.readFrom6C(IReader.Bank_RESERVED, 4, 1, BaseUtil.getHexByteArray("00000000"));
        mReader.getTagData(0, 4, 1, new byte[1*2],  BaseUtil.getHexByteArray("00000000"),(short) 1000);
        return false;
    }

}
