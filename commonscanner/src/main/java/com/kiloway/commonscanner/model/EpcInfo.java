package com.kiloway.commonscanner.model;

/**
 * @author xinh on 2020/4/10
 * @describe
 * @Email: 1015835826@qq.com
 * @date 2020/4/10
 */

public class EpcInfo {
    private String epc;
    private String tid;

    public EpcInfo() {
    }

    public EpcInfo(String epc, String tid) {
        this.epc = epc;
        this.tid = tid;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    @Override
    public String toString() {
        return "EpcInfo{" +
                "epc='" + epc + '\'' +
                ", tid='" + tid + '\'' +
                '}';
    }
}
