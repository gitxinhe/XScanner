package com.kiloway.commonscanner.base;

import android.content.Context;

public interface IDeviceAdapter {
	/**
	 * 设备的名称
	 * @return
	 */
	String getDeviceName();
	void uninit();
	/**
	 * 重新初始化并启动
	 * @param context
	 */
	void reinitAndStart(Context context);
	/**
	 * 启动RFID扫描
	 * 
	 * @param ctx
	 * @return
	 */
	void start(Context ctx);
	/**
	 * 停止RFID扫描
	 */
	void stop();
	/**
	 * 设备的状态
	 * @return
	 */
	int Status();
	/**
	 * 写EPC
	 * @param epc
	 * @return
	 */
	int writeEPC(String epc);

}
