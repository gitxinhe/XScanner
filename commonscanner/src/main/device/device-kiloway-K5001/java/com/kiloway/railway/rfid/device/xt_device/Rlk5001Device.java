package com.kiloway.railway.rfid.device.xt_device;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.BRMicro.Tools;
import com.kiloway.commonscanner.base.BaseUtil;
import com.kiloway.commonscanner.base.Device;
import com.kiloway.commonscanner.interfaces.ReaderConfig;
import com.kiloway.commonscanner.model.EpcInfo;
import com.senter.iot.support.openapi.uhf.UhfI;

import java.util.ArrayList;
import java.util.List;

public class Rlk5001Device extends Device {
	private UhfI mReader ; //超高频读写器
	private boolean startFlag = false;
	private boolean saveLocalPower = false;
	private Context context;

	@Override
	public void initReader(Context context) {
		this.context = context;
		if (!UhfI.getInstance().isInited()){
				new InitTask().execute();
		}
	}

	@Override
	public void unitReader() {
		if (null!=mReader){
			mReader.uninit();
		}
	}

	public class InitTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			try{
				return UhfI.getInstance().init();
			}catch (Exception e){
				return  false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				Log.e("tag","设备初始化成功");
				setReader(UhfI.getInstance());
			}
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

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
		try {

			UhfI.getInstance().startInventory(new UhfI.OnNewTagInventoried() {
				@Override
				public void onNewUiiReceived(UhfI.ST_TagInfo st_tagInfo) {
					Log.e("tag",st_tagInfo.EpcId+"");
					String epcStr = Tools.Bytes2HexString(st_tagInfo.EpcId, st_tagInfo.EpcId.length);
					//UhfI.getInstance().readTag(UhfI.Bank.TID,0,2,"0000".getBytes(), new UhfI.ST_TagFilter());
					onTagReadedEvent(new EpcInfo(epcStr,""));
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}catch (Exception e){
			Log.e("tag",e.getMessage());
		}

		return false;
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
		try{
			UhfI.getInstance().stopInventory();
		}catch (Exception e){

		}
	}


	@Override
	public int writeEPC(String epc) {
		boolean success =UhfI.getInstance().writeTagEpc( epc.getBytes(),new byte[]{0,0,0,0});
		return success?0:1;
	}

	@Override
	public int getPower() {
		int readPower = 0;
		try {
			readPower = UhfI.getInstance().getAntennaPower().readPower;
		} catch (Exception e) {
			Log.e("tag",e.getMessage());
		}
		return readPower;
	}


	@Override
	public List<String> getPowerList() {
		List<String> powers = new ArrayList<>();
		for (int i = 500; i <= 3300 ; i=i+100) {
			powers.add(i+"");
		}
		return powers;
	}

	@Override
	public boolean readFilterData(String epc) {
		//UhfI.UII filter = new UhfI.UII();
	//	filter.bank = UhfI.UII.EPC
		mReader.readTag(UhfI.Bank.Reserved,4,1, BaseUtil.getHexByteArray("00000000"),null);
		return false;
	}

	@Override
	public boolean readData() {
		mReader.readTag(UhfI.Bank.Reserved,4,1, BaseUtil.getHexByteArray("00000000"),null);
		return false;
	}

	@Override
	public boolean setPower(int curPower) {
		UhfI.ST_AntPower power = new UhfI.ST_AntPower();
		power.readPower =(short)curPower;
		power.writePower =(short)curPower;
		return UhfI.getInstance().setAntennaPower(power);
	}

	public void setReader(UhfI reader){
		this.mReader = reader;
	}
}
