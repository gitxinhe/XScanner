package com.kiloway.commonscanner.interfaces;

import java.util.Map;

/**
 * RFID 高频扫描头配置基类，对于不同的设备需要具体实现
 * Created by cym1497 on 2019/3/7
 */
public interface ReaderConfig {

    /**
     * 默认配置
     */
    void defaultConfig();

    /**
     * Q 值
     * @param q
     * @return
     */
    boolean setQ(int q);
    int getQ();

    /**
     * 设置功率 0-26？
     * @param level
     * @return
     */
    boolean setPowerLevel(int level);
    int getPowerLevel();

    /**
     *  map.put("session", session);
     *  map.put("flag", flag);
     * @return
     */
    Map<String, Integer> getSession();
    boolean setSession(int session, int flag);

    /**
     * 标签过滤时间
     * @param time
     * @return
     */
    boolean setFilterByTime(int time);
    boolean cancelFilterByTime();

    /**
     * rssi 开启/关闭
     * @param enable
     * @return
     */
    boolean enableRssi(boolean enable);
}
