package com.kiloway.railway.rfid.device.xt_device;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.kiloway.commonscanner.base.BaseUtil;
import com.kiloway.commonscanner.base.Constant;
import com.kiloway.commonscanner.base.Device;
import com.kiloway.commonscanner.base.DeviceSetting;
import com.kiloway.commonscanner.base.Utils;
import com.kiloway.commonscanner.interfaces.IReader;
import com.kiloway.commonscanner.interfaces.ReaderConfig;
import com.kiloway.commonscanner.model.EpcInfo;
import com.seuic.uhf.EPC;
import com.seuic.uhf.UHFService;

import java.util.ArrayList;
import java.util.List;

/**
 * 东大集成手持机
 */
public class RlkUH55ED1Device extends Device {
    private UHFService mReader;
    private boolean startFlag = false;
    private InventoryRunnable mInventoryRunable;
    private Thread mInventoryThread;
    private Context context;
    private boolean validateKilowayLabel;
    private boolean isAnyInventory = false;
    @Override
    public void initReader(Context context) {
        this.context = context;
        validateKilowayLabel = DeviceSetting.getNeedValidateKlwLabel(context);
        try{
            mReader = UHFService.getInstance();
            byte[] embd = new byte[255];
            embd[0] = (byte)2;
            embd[1] = 0;
            embd[2] = 12;
            System.arraycopy(BaseUtil.getHexByteArray("00000000"), 0, embd, 3, 4);
            mReader.setParamBytes(UHFService.PARAMETER_TAG_EMBEDEDDATA,embd);
            mReader.setParameters(UHFService.PARAMETER_HIDE_PC,1);
            boolean ret = mReader.open();
            mInventoryRunable = new InventoryRunnable();
            if (ret) {
                setReader(mReader);
                int power =  DeviceSetting.getPower(this.context, Constant.KLWUH55ED1_POWER, 33);
                setPower(power);
            }
        }catch (Exception e){

        }
    }
    @Override
    public void unitReader() {
        startFlag = false;

        if (null != mReader) {
            setReadMode(startFlag);
            mReader.inventoryStop();
            mReader.close();
        }
    }

    @Override
    public ReaderConfig configReader() {
        return null;
    }

    @Override
    public boolean inventoryTag() {
        start(false);
        return false;
    }
    void start(boolean isAnyInventory){
        if (mReader==null){
            return;
        }
        this.isAnyInventory = isAnyInventory;
        if (isAnyInventory){
           if (mReader.inventoryStart()){
               startFlag = true;
           }
        }else {
           startFlag = true;
        }
        setReadMode(startFlag);
        mInventoryThread = new Thread(mInventoryRunable);
        mInventoryThread.start();
    }
    @Override
    public boolean inventoryAnyTag() {
        start(true);
        return false;
    }

    @Override
    public void stopReading() {
        startFlag = false;
        setReadMode(startFlag);
        if (mReader!=null){
            mReader.inventoryStop();
        }
    }

    public void setReader(UHFService reader) {
        this.mReader = reader;
    }

     Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    EPC epc = (EPC) msg.obj;
                    String id = epc.getId();
                    String tid = epc.getEmbeded();
                    if (validateKilowayLabel){
                        if (Utils.ValidateKilowayTidSign(tid)){
                            onTagReadedEvent(new EpcInfo( id, tid));
                        }
                    }else {
                        onTagReadedEvent(new EpcInfo( id, tid));
                    }
                   break;
                case 2:
                    synchronized (context) {
                        List<EPC> tagIDs = mReader.getTagIDs();
                        List<EpcInfo> epcInfos = new ArrayList<>();
                        int size = tagIDs.size();
                        for (int i = 0; i < size; i++) {
                            String embeded = tagIDs.get(i).getEmbeded();
                            if (validateKilowayLabel){
                                if (Utils.ValidateKilowayTidSign(embeded)){
                                    epcInfos.add(new EpcInfo(tagIDs.get(i).getId(), embeded));
                                }else {
                                    continue;
                                }
                            }else {
                                epcInfos.add(new EpcInfo(tagIDs.get(i).getId(), tagIDs.get(i).getEmbeded()));
                            }
                        }
                        onAnyTagReadedEvent(epcInfos);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private class InventoryRunnable implements Runnable {
        @Override
        public void run() {
            while (startFlag) {
                Message message = Message.obtain();
                if (!isAnyInventory){
                    EPC epc = new EPC();
                    if (mReader.inventoryOnce(epc, 150)) {
                        message.what = 1;
                        message.obj = epc;
                        Log.e("tag",epc.getEmbeded()+"tiiii");
                        handler.sendMessage(message);
                    }
                }else {
                    message.what = 2;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(2*100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public int writeEPC(String epc) {
        return -1;
    }

    @Override
    public int getPower() {
       return mReader==null?0:mReader.getPower();
    }

    @Override
    public boolean setPower(int power) {
        if (mReader==null){
            return false;
        }
        boolean success =  mReader.setPower(power);
        if (success){
            DeviceSetting.setPower(this.context,Constant.KLWUH55ED1_POWER,power);
        }
        return success;
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
        if (mReader==null){
            return false;
        }
        return mReader.readTagData(BaseUtil.getHexByteArray(epc), BaseUtil.getHexByteArray("00000000"),
                IReader.Bank_RESERVED, 8, 1, new byte[128]);
    }

    @Override
    public boolean readData() {
        if (mReader==null){
            return false;
        }
        return mReader.readTagData(new byte[128], BaseUtil.getHexByteArray("00000000"),IReader.Bank_RESERVED,8,1,null);
    }

}
