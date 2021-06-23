package com.kiloway.railway.rfid.device.xt_device;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.kiloway.commonscanner.base.BaseUtil;
import com.kiloway.commonscanner.base.Device;
import com.kiloway.commonscanner.interfaces.ReaderConfig;
import com.kiloway.commonscanner.model.EpcInfo;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.IUHF;

import java.util.ArrayList;
import java.util.List;

public class RlkC72Device extends Device {
	private RFIDWithUHFUART mReader ; //超高频读写器
	private boolean startFlag = false;
	private boolean saveLocalPower = false;
	private Context context;

	@Override
	public void initReader(Context context) {
		this.context = context;
			try {
				 mReader = RFIDWithUHFUART.getInstance();
				//mReader.setEPCAndTIDMode();
			} catch (Exception ex) {
				return;
			}
			if (mReader != null) {
				new InitTask().execute();
			}
	}

	@Override
	public void unitReader() {
		if (null!=mReader){
			mReader.free();
		}
	}

	public class InitTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return mReader.init();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				setReader(mReader);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	private boolean isNeedConnect(){
		return true;
	}
	@Override
	public ReaderConfig configReader() {
		return null;
	}
	@Override
	public boolean inventoryTag() {
		startFlag = true;
		setReadMode(startFlag);
		mReader.setEPCAndTIDMode();
		if (mReader.startInventoryTag()) {
			new InventoryThread().start();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean inventoryAnyTag() {
		inventoryTag();
		return false;
	}

	@Override
	public void stopReading() {
		startFlag  = false;
		setReadMode(startFlag);
		mReader.stopInventory();
	}
	public void setReader(RFIDWithUHFUART reader){
		this.mReader = reader;
	}

	//处理线程
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			UHFTAGInfo info = (UHFTAGInfo) msg.obj;
			String epc = info.getEPC();
			if (!TextUtils.isEmpty(epc)) {
				onTagReadedEvent(new EpcInfo(epc,info.getTid()));
			}
		}
	};
	class InventoryThread extends Thread{
		@Override
		public void run() {
			super.run();
			while(startFlag){
				try {
					if( mReader != null) {
						UHFTAGInfo res1 = mReader.readTagFromBuffer();
						if (res1 != null) {
							Message msg = handler.obtainMessage();
							msg.obj = res1;
							handler.sendMessage(msg);
						}
					}
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}catch(Exception e) {
					Log.e("tag",e.getMessage());
				}
			}
		}
	}

	@Override
	public int writeEPC(String epc) {
		boolean success = mReader.writeData("000000", IUHF.Bank_EPC,2,epc.getBytes().length/2,epc);
		return success?0:1;
	}

	@Override
	public int getPower() {
		return mReader.getPower();
	}

	@Override
	public boolean setPower(int power) {
		return mReader.setPower(power);
	}

	@Override
	public List<String> getPowerList() {
		List<String> powers = new ArrayList<>();
		for (int i = 5; i < 30 ; i++) {
			powers.add(i+"");
		}
		return powers;
	}

	@Override
	public boolean readFilterData(String epc) {
		String result = mReader.readData("000000", IUHF.Bank_EPC, 16, BaseUtil.EPCToPC(epc).length()*4+epc.length()*4, BaseUtil.EPCToPC(epc)+epc, IUHF.Bank_RESERVED, 4, 1);
		return result == null ? false : true;
	}

	@Override
	public boolean readData() {
		String result = mReader.readData("000000", IUHF.Bank_RESERVED, 4, 1);
		return result == null ? false : true;
	}
}
