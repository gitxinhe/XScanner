package com.uhf.reader;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.kiloway.commonscanner.base.BaseUtil;
import com.kiloway.commonscanner.base.Device;
import com.kiloway.commonscanner.interfaces.ReaderConfig;
import com.kiloway.commonscanner.model.EpcInfo;
import com.rfid.trans.ReadTag;
import com.rfid.trans.TagCallback;
import com.rfid.trans.UHFLib;

import java.util.ArrayList;
import java.util.List;

import cn.pda.serialport.SerialPort;


public class RlkUHK71V164BSPDevice extends Device {
    private UHFLib mReader;
    private boolean startFlag = false;
    private Thread mInventoryThread;
    private Context context;
    private SerialPort mSerialPort;
    private void open() {
        try {
            mSerialPort = new SerialPort();
            mSerialPort.power_5Von();
        } catch (Exception e) {
            return;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initReader(Context context) {
        this.context = context;
        open();
        SystemClock.sleep(1500);
        MsgCallback callback = new MsgCallback();
        Reader.rrlib.SetCallBack(callback);
        String devicePath = "/dev/ttyMT1";
        int speed =57600;
        try {
            int result = Reader.rrlib.Connect(devicePath, speed);
            if(result==0){
                setReader(Reader.rrlib);
            }
            else
            {
                Log.e("tag","连接失败！");
            }
        }catch (Exception e)
        {
            Log.e("tag","连接失败！");
        }

    }


    @Override
    public void unitReader() {
        if (null != mReader) {
            mReader.DisConnect();
            close();
        }
    }
    private void close() {
        SerialPort mSerialPort = new SerialPort();
        mSerialPort.power_5Voff();
    }
    private boolean isNeedConnect() {
        return false;
    }

    @Override
    public ReaderConfig configReader() {
        return null;
    }

    @Override
    public boolean inventoryTag() {
        startFlag = true;
        setReadMode(startFlag);
        Reader.rrlib.StartRead(0);
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
        Reader.rrlib.StopRead();
    }

    public void setReader(UHFLib reader) {
        this.mReader = reader;
    }

    @Override
    public int writeEPC(String epc) {
        byte[] data = BaseUtil.getHexByteArray(epc);
        byte Mem=1;
        byte ptr=2;
        Reader.rrlib.WriteDataByEPC(epc,Mem,ptr,BaseUtil.getHexByteArray("00000000"),epc);
        return -1;
    }

    @Override
    public int getPower() {
        byte[]Version=new byte[2];
        byte[]Power=new byte[1];
        byte[]band=new byte[1];
        byte[]MaxFre=new byte[1];
        byte[]MinFre=new byte[1];
        byte[]BeepEn=new byte[1];
        byte[]Ant=new byte[1];
       int result =Reader.rrlib.GetUHFInformation(Version, Power, band, MaxFre, MinFre, BeepEn, Ant);
        if (result==0){
            return  Power[0];
        }
       return -1;
    }

    @Override
    public boolean setPower(int power) {
        int result =  mReader.SetRfPower(power);
        return result==0?true:false;
    }

    @Override
    public List<String> getPowerList() {
        List<String> powers = new ArrayList<>();
        for (int i = 5; i <= 33; i++) {
            powers.add(i+"");
        }
        return powers;
    }

    @Override
    public boolean readFilterData(String epc) {
        byte[]Password = BaseUtil.getHexByteArray("00000000");
        byte MaskMem=0;
        byte MaskAdr[]=new byte[2];
        byte MaskLen=0;
        byte[]MaskData=new byte[100];
        byte MaskFlag=0;
        Reader.rrlib.LedOn_kx2005x(epc,(byte)7,Password,MaskMem,MaskAdr,MaskLen,MaskData,MaskFlag);
        return false;
    }

    @Override
    public boolean readData() {
        byte[]Password = BaseUtil.getHexByteArray("00000000");
        byte MaskMem=0;
        byte MaskAdr[]=new byte[2];
        byte MaskLen=0;
        byte[]MaskData=new byte[100];
        byte MaskFlag=0;
        Reader.rrlib.LedOn_kx2005x("",(byte)7,Password,MaskMem,MaskAdr,MaskLen,MaskData,MaskFlag);
        return  false;
    }

    class MsgCallback implements TagCallback {
        @Override
        public void tagCallback(ReadTag tag) {
            String epc = tag.epcId;
            if (!TextUtils.isEmpty(epc)){
                onTagReadedEvent(new EpcInfo(epc,""));
            }
        }

        @Override
        public int tagCallbackFailed(int reason) {
            return 0;
        }

        @Override
        public void stopReadCallBack() {

        }
    }
}
