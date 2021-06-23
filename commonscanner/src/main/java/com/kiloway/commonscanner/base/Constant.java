package com.kiloway.commonscanner.base;

/**
 * Created by 10158 on 2020/11/6.
 */

public class Constant {
    public static final String KLWUH55EH2 = "KLW-UH55EH2";//内部型号，对应的是c5000手持机
    public static final String KLWUH45EX1 = "KLW-UH45EX1";//内部型号，对应的是信通手持机
    public static final String KLWUH55EC2 = "KLW-UH55EC2";//内部型号，对应的是c72手持机
    public static final String KLWUH55ED1 = "KLW-UH55ED1";//内部型号，对应的是东大集成手持机
    public static final String KLWUUHK71V164BSP = "KLW-UHK71V164BSP";//内部型号，对应的是汉德霍尔手持机
    public static final String KLWUUHK71V164BSP_B = "KLW-UHK71V164BSP_B";//内部型号，对应的是汉德霍尔手持机
    public static final String KLWUH55EH2_POWER = KLWUH55EH2 + "POWER";//内部型号，对应的是c5000手持机
    public static final String KLWUH55ED1_POWER = KLWUH55ED1 + "POWER";//内部型号，对应的是东大集成手持机

    public static enum Profile {
        USER, //用户存储区
        TID, //TID存储区
        EPC, // EPC存储区
        RESERVE //保留存储区
    }
}
