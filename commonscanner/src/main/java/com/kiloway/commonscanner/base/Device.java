package com.kiloway.commonscanner.base;

import android.content.Context;
import android.util.Log;

import com.kiloway.commonscanner.interfaces.IReader;
import com.kiloway.commonscanner.model.EpcInfo;

import java.util.List;


/**
 * 设备控制基类
 * Created by cym1497 on 2019/2/28
 */
public abstract class Device implements IReader {
    private static Device mDevice;
    public static Device getInstance() {
        return mDevice;
    }

    public void init(Context context) {
        initReader(context);
        mDevice = this;
    }

    private OnEventListener mListener;

    public void setOnEventListener(OnEventListener listener) {
        mListener = listener;
    }

    public interface OnEventListener {
        void onTagReadedEvent(EpcInfo info);
        void onAnyTagReadedEvent(List<EpcInfo> infos);
    }

    /***Reader**********************************/

    protected boolean isInventory;

    @Override
    public void setReadMode(boolean isInventory) {
        this.isInventory = isInventory;
    }

    @Override
    public boolean isInventory() {
        return isInventory;
    }


    @Override
    public void onTagReadedEvent(EpcInfo info) {
        if (isInventory && mListener != null) {
            if (info!=null){
                Log.d("tag",info.getEpc());
            }
            mListener.onTagReadedEvent(info);
            if (!isInventory()) {
                stopReading();
            }
        }
    }
    @Override
    public void onAnyTagReadedEvent(List<EpcInfo> info) {
        if (isInventory && mListener != null) {
            mListener.onAnyTagReadedEvent(info);
            if (!isInventory()) {
                stopReading();
            }
        }
    }
    private void unInit(){

    }
}
