package com.kiloway.commonscanner.interfaces;

import android.content.Context;

import com.kiloway.commonscanner.model.EpcInfo;

import java.util.List;

/**
 * RFID 扫描模块控制接口
 * Created by xinhe on 2019/2/28
 */
public interface IReader {
    int Bank_RESERVED = 0;//保留存储区
    int Bank_EPC = 1;// EPC存储区
    int Bank_TID = 2;//TID存储区
    int Bank_USER = 3; //用户存储区
    //初始化
    void initReader(Context context);
    //解除初始化
    void unitReader();
    ReaderConfig configReader();
    //盘点
    boolean inventoryTag();
    boolean inventoryAnyTag();
    //停止盘点
    void stopReading();
    /*
    * 状态
    * */
    void setReadMode(boolean isInventory);
    //盘点状态
    boolean isInventory();
    //数据回调
    void onTagReadedEvent(EpcInfo info);
    //数据回调
    void onAnyTagReadedEvent(List<EpcInfo> info);
    //写标签
    int writeEPC(String epc);
    //获取功率
    int getPower();
    //设置功率
    boolean setPower(int power);
    //获取功率列表
    List<String> getPowerList();
    boolean readFilterData(String epc);
    //读标签
    boolean readData();
}
