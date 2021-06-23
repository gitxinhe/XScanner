package com.rfid.trans;

import android.media.SoundPool;
import android.os.SystemClock;

public class UHFLib{
	private BaseReader reader = new BaseReader();
	private ReaderParameter param = new ReaderParameter();
	private volatile boolean mWorking = true; 
	private volatile Thread mThread=null;
	private volatile boolean soundworking = true;
	private volatile boolean isSound = false;
	private volatile Thread sThread=null;
	private byte[]pOUcharIDList=new byte[25600];
	private volatile int NoCardCOunt=0;
	private  Integer soundid=null;
	private SoundPool soundPool=null;
	public UHFLib()
	{
		param.ComAddr =(byte)255;//读写器地址，默认255
		param.ScanTime =2000;//询查命令最大响应时间，
		param.Session = 0;//Session
		param.QValue = 4;//q
		param.TidLen = 0;//读取TID长度
		param.TidPtr =0;//读取TID起始地址
		param.Antenna =0x80;//单口模块固定0x80
	}
    public void beginSound(boolean sound)
    {
        isSound =sound;
    }
    public void setsoundid(int id,SoundPool soundPool)
	{
		soundid =id;
		this.soundPool = soundPool;
	}

	public  void playSound() {
		 if((soundid==null)||(soundPool==null))return;
		try {
			soundPool.play(soundid, 1, // 左声道音量
					1, // 右声道音量
					1, // 优先级，0为最低
					0, // 循环次数，0无不循环，-1无永远循环
					1  // 回放速度 ，该值在0.5-2.0之间，1为正常速度
			);
			//SystemClock.sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//连接串口,ComPort:串口名字，BaudRate波特率
	public int Connect(String ComPort,int BaudRate)
	{
		int result = reader.Connect(ComPort, BaudRate, 1);
		if(result ==0)
		{
			byte[] Version=new byte[2];
			byte[] Power=new byte[1];
			byte[] band = new byte[1];
			byte[] MaxFre= new byte[1];
			byte[] MinFre = new byte[1];
			byte[] BeepEn = new byte[1];
			byte[] Ant =new byte[1];
			result = GetUHFInformation(Version,Power,band,MaxFre,MinFre,BeepEn,Ant);
			if(result!=0)
			{
				reader.DisConnect();
			}
			isSound=false;
			soundworking=true;
			sThread  = new Thread(new Runnable() {
				@Override
				public void run() {
					while(soundworking)
					{
						if(isSound)
						{
							playSound();
							SystemClock.sleep(50);
						}
					}
				}
			});
			sThread.start();
		}
		return result;
	}
	
	///关闭串口连接
	public int DisConnect()
	{
		try{
			isSound=false;
			soundworking=false;
			sThread =null;
		}catch(Exception ex)
		{}
		return reader.DisConnect();
	}
	
	//设置询查参数
	public void SetInventoryPatameter(ReaderParameter param)
	{
		this.param = param;
	}
	
	//读取当前的询查参数
	public ReaderParameter GetInventoryPatameter()
	{
		return this.param;
	}
	
	//获取读写器信息
	public int GetUHFInformation(byte Version[],byte Power[],byte band[],byte MaxFre[],byte MinFre[],byte BeepEn[],byte Ant[])
	{
		byte[]ReaderType=new byte[1];
		byte[]TrType=new byte[1];
		byte[]OutputRep=new byte[1];
		byte[]CheckAnt=new byte[1];
		byte[]ComAddr =new byte[1];
		ComAddr[0]=(byte)255;
		byte[]ScanTime = new byte[1];
		int result = reader.GetReaderInformation(ComAddr, Version, ReaderType, TrType, band, MaxFre, MinFre, Power, ScanTime, Ant, BeepEn, OutputRep, CheckAnt);
		if(result==0)
		{
			param.ComAddr = ComAddr[0];
			param.Antenna = Ant[0];
		}
		return result;
	}
	
	//设置RFID模块功率
	public int SetRfPower(int Power)
	{
		return reader.SetRfPower(param.ComAddr, (byte)Power);
	}
	
	//设置频段
	public int SetRegion(int band,int maxfre,int minfre)
	{
		return reader.SetRegion(param.ComAddr, band, maxfre, minfre);
	}
	
	

	//epc掩码读存储区
	public String ReadDataByEPC(String EPCStr,byte Mem,byte WordPtr,byte Num,byte Password[])
	{
		if(EPCStr.length() % 4 !=0) return "FF";
		byte ENum = (byte)(EPCStr.length()/4);
		byte[] EPC = reader.hexStringToBytes(EPCStr);
		byte MaskMem =0;
		byte[]MaskAdr=new byte[2];
		byte MaskLen=0;
		byte[]MaskData=new byte[12];
		byte MaskFlag=0;
		byte[]Data=new byte[Num*2];
		byte[]Errorcode=new byte[1];
		int result = reader.ReadData_G2(param.ComAddr, ENum, EPC, Mem, WordPtr, Num, Password, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, Data, Errorcode);
		if(result==0)
		{
			return reader.bytesToHexString(Data, 0, Data.length);
		}
		else
		{
			return String.format("%2X", result);
		}
	}
	
	///tid掩码读存储区
	public String ReadDataByTID(String TIDStr,byte Mem,byte WordPtr,byte Num,byte Password[])
	{
		if(TIDStr.length() % 4 !=0) return "FF";
		byte ENum = (byte)255;
		byte[] EPC = new byte[12];
		byte[]TID = reader.hexStringToBytes(TIDStr);
		byte MaskMem =2;
		byte[]MaskAdr=new byte[2];
		MaskAdr[0]=MaskAdr[1]=0;
		byte MaskLen=(byte)(TIDStr.length()*4);
		byte[]MaskData=new byte[TIDStr.length()];
		System.arraycopy(TID, 0, MaskData, 0, TID.length);
		byte MaskFlag=1;
		byte[]Data=new byte[Num*2];
		byte[]Errorcode=new byte[1];
		int result = reader.ReadData_G2(param.ComAddr, ENum, EPC, Mem, WordPtr, Num, Password, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, Data, Errorcode);
		if(result==0)
		{
			return reader.bytesToHexString(Data, 0, Data.length);
		}
		else
		{
			return String.format("%2X", result);
		}
	}
	
	///epc掩码写存储区
	public int WriteDataByEPC(String EPCStr,byte Mem,byte WordPtr,byte Password[],String wdata)
	{
		if(EPCStr.length() % 4 !=0) return 255;
		if(wdata.length() % 4 !=0) return 255;
		byte ENum = (byte)(EPCStr.length()/4);
		byte WNum = (byte)(wdata.length()/4);
		byte[] EPC = reader.hexStringToBytes(EPCStr);
		byte[] data = reader.hexStringToBytes(wdata); 
		byte MaskMem =0;
		byte[]MaskAdr=new byte[2];
		byte MaskLen=0;
		byte[]MaskData=new byte[12];
		byte MaskFlag=0;
		byte[]Errorcode=new byte[1];
		return reader.WriteData_G2(param.ComAddr, WNum, ENum, EPC, Mem, WordPtr, data, Password, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, Errorcode);
	}
	
	///tid掩码写存储区
	public int WriteDataByTID(String TIDStr,byte Mem,byte WordPtr,byte Password[],String wdata)
	{
		if(TIDStr.length() % 4 !=0) return 255;
		if(wdata.length() % 4 !=0) return 255;
		byte ENum = (byte)255;
		byte WNum = (byte)(wdata.length()/4);
		byte[] EPC = new byte[12];
		byte[] data = reader.hexStringToBytes(wdata); 
		byte[]TID = reader.hexStringToBytes(TIDStr);
		
		byte MaskMem =2;
		byte[]MaskAdr=new byte[2];
		MaskAdr[0]=MaskAdr[1]=0;
		byte MaskLen=(byte)(TIDStr.length()*4);
		byte[]MaskData=new byte[TIDStr.length()];
		System.arraycopy(TID, 0, MaskData, 0, TID.length);
		byte MaskFlag=1;
		byte[]Errorcode=new byte[1];
		return reader.WriteData_G2(param.ComAddr, WNum, ENum, EPC, Mem, WordPtr, data, Password, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, Errorcode);
	}

	public int LedOn_kx2005x(String EPCStr,byte CarrierTime, byte Password[],byte MaskMem,byte MaskAdr[],byte MaskLen,byte[]MaskData,byte MaskFlag)
	{
		byte ENum=0;
		byte[] EPC = null;
		if(EPCStr!=null && EPCStr.length()>0)
		{
			EPC = reader.hexStringToBytes(EPCStr);
			ENum = (byte)(EPCStr.length()/4);
		}
		return reader.LedOn_kx2005x(param.ComAddr,ENum,EPC,CarrierTime,Password,MaskMem,MaskAdr,MaskLen,MaskData,MaskFlag);
	}
	
	///tid掩码写epc号
	public int WriteEPCByTID(String TIDStr,String EPCStr,byte Password[])
	{
		if(TIDStr.length() % 4 !=0) return 255;
		if(EPCStr.length() % 4 !=0) return 255;
		byte ENum = (byte)255;
		byte WNum = (byte)(EPCStr.length()/4);
		byte[] EPC = new byte[12];
		String PCStr="";
		switch(WNum)
		{
		case 1:
			PCStr="0800"; break;
		case 2:
			PCStr="1000"; break;
		case 3:
			PCStr="1800"; break;
		case 4:
			PCStr="2000"; break;
		case 5:
			PCStr="2800"; break;
		case 6:
			PCStr="3000"; break;
		case 7:
			PCStr="3800"; break;
		case 8:
			PCStr="4000"; break;
		case 9:
			PCStr="4800"; break;
		case 10:
			PCStr="5000"; break;
		case 11:
			PCStr="5800"; break;
		case 12:
			PCStr="6000"; break;
		case 13:
			PCStr="6800"; break;
		case 14:
			PCStr="7000"; break;
		case 15:
			PCStr="7800"; break;
		case 16:
			PCStr="8000"; break;
		}
		String wdata=PCStr +EPCStr;
		WNum+=1;
		byte[] data = reader.hexStringToBytes(wdata); 
		byte[]TID = reader.hexStringToBytes(TIDStr);
		
		byte MaskMem =2;
		byte[]MaskAdr=new byte[2];
		MaskAdr[0]=MaskAdr[1]=0;
		byte MaskLen=(byte)(TIDStr.length()*4);
		byte[]MaskData=new byte[TIDStr.length()];
		System.arraycopy(TID, 0, MaskData, 0,TID.length);
		byte MaskFlag=1;
		byte[]Errorcode=new byte[1];
		byte Mem=1;
		byte WordPtr=1;
		return reader.WriteData_G2(param.ComAddr, WNum, ENum, EPC, Mem, WordPtr, data, Password, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, Errorcode);
	}
	
	public int ReadData_G2(byte ComAddr, byte ENum, byte EPC[], byte Mem,
			byte WordPtr, byte Num, byte Password[],byte MaskMem,byte MaskAdr[],byte MaskLen,byte[]MaskData,byte MaskFlag,byte Data[],byte Errorcode[])
	{
		return reader.ReadData_G2(param.ComAddr, ENum, EPC, Mem, WordPtr, Num, Password, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, Data, Errorcode);
	}
	
	public int ExtReadData_G2(byte ComAddr, byte ENum, byte EPC[], byte Mem,
			byte[] WordPtr, byte Num, byte Password[], byte Data[],byte Errorcode[])
	{
		return reader.ExtReadData_G2(ComAddr, ENum, EPC, Mem, WordPtr, Num, Password, Data, Errorcode);
	}
	public int WriteData_G2(byte ComAddr, byte WNum, byte ENum, byte EPC[],
			byte Mem, byte WordPtr, byte Writedata[], byte Password[],byte MaskMem,byte MaskAdr[],byte MaskLen,byte[]MaskData,byte MaskFlag,
			byte Errorcode[])
	{
		return reader.WriteData_G2(ComAddr, WNum, ENum, EPC, Mem, WordPtr, Writedata, Password, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, Errorcode);
	}
	
	public int ExtWriteData_G2(byte ComAddr, byte WNum, byte ENum, byte EPC[],
			byte Mem, byte[] WordPtr, byte Writedata[], byte Password[],
			byte Errorcode[])
	{
		return reader.ExtWriteData_G2(ComAddr, WNum, ENum, EPC, Mem, WordPtr, Writedata, Password, Errorcode);
	}
	
	//锁定标签的存储区
	public int Lock(String EPCStr,byte select,byte setprotect,String PasswordStr)
	{
		if(EPCStr.length() % 4 !=0) return 255;
		if(PasswordStr.length() !=8) return 255;
		byte ENum = (byte)(EPCStr.length()/4);
		byte[] EPC = reader.hexStringToBytes(EPCStr);
		byte[] Password = reader.hexStringToBytes(PasswordStr);
		byte[]Errorcode=new byte[1];
		return reader.Lock_G2(param.ComAddr, ENum, EPC, select, setprotect, Password, Errorcode);
	}
	
	//销毁标签
	public int Kill(String EPCStr,String PasswordStr)
	{
		if(EPCStr.length() % 4 !=0) return 255;
		if(PasswordStr.length() !=8) return 255;
		byte ENum = (byte)(EPCStr.length()/4);
		byte[] EPC = reader.hexStringToBytes(EPCStr);
		byte[] Password = reader.hexStringToBytes(PasswordStr);
		byte[]Errorcode=new byte[1];
		return reader.Kill_G2(param.ComAddr, ENum, EPC, Password, Errorcode);
	}
	
	//设置回调接口
	public void SetCallBack(TagCallback callback)
    {
		reader.SetCallBack(callback);
    }
	//启动盘点
	public int StartRead(final int readType)
	{
		if(mThread==null)
		{

			mWorking=true;
			mThread = new Thread(new Runnable() {  
	        @Override  
	        public void run() {  
	        	byte Target=0;
	            while(mWorking)
	            {
					byte Ant=(byte)0x80;
					int[]pOUcharTagNum=new int[1];
					int[]pListLen=new int[1];
					pOUcharTagNum[0]=pListLen[0]=0;
					if((param.Session==0)||(param.Session==1))
					{Target=0;NoCardCOunt=0;}
					if(readType==0)
	            	{
	            		int result = reader.Inventory_G2(param.ComAddr, (byte)param.QValue, (byte)param.Session, (byte)param.TidPtr, (byte)param.TidLen, Target, Ant, (byte)10, pOUcharIDList, pOUcharTagNum, pListLen);
	            	}
	            	else if(readType==1)
	            	{
	            		byte SelTarget = 7;
	                    byte SelAction =4;
	                    byte WaitTime =4;
	                    byte ENum=0;
	                    byte[] EPC =new byte[12];
	                    byte QValue =(byte)(param.QValue| 0x80);
	            		int result=  reader.Inventory_Temperature(param.ComAddr,ENum,EPC,SelTarget,SelAction,WaitTime,QValue, (byte)param.Session,Target,Ant,(byte)10,pOUcharIDList,pOUcharTagNum,pListLen);
	            	}					
					if(pOUcharTagNum[0]==0)
					{
                        isSound=false;
						if(param.Session>1)
						{
							NoCardCOunt++;
							if(NoCardCOunt>7)
							{
								Target=(byte)(1-Target);
								NoCardCOunt=0;
							}
						}
					}
					else
					{
						NoCardCOunt=0;
						isSound=true;
					}
	            }
	            mThread=null;
				isSound=false;
	        }
	        });
			mThread.start();
			return 0;
		}
		return -1;
	}
	
	//停止盘点
	public void StopRead()
	{
		if(mThread!=null)
		{
			isSound=false;
			mWorking=false;
		}
	}
	
	public int InventoryOnce(byte[]epclist,int[]cardNum,int[]pListLen)
	{
		return reader.Inventory_G2(param.ComAddr, (byte)param.QValue, (byte)param.Session, (byte)param.TidPtr, (byte)param.TidLen, (byte)0, (byte)0x80, (byte)10, epclist, cardNum, pListLen);
	}
	public void ReadOneTime(boolean readType)
	{
		int[]pOUcharTagNum=new int[1];
		int[]pListLen=new int[1];
		if(!readType)
    	{
    		int result = reader.Inventory_G2(param.ComAddr, (byte)param.QValue, (byte)param.Session, (byte)param.TidPtr, (byte)param.TidLen, (byte)0, (byte)0x80, (byte)10, pOUcharIDList, pOUcharTagNum, pListLen);
    	}
    	else
    	{
    		byte SelTarget = 7;
            byte SelAction =4;
            byte WaitTime =4;
            byte ENum=0;
            byte[] EPC =new byte[12];
            byte QValue =(byte)(param.QValue| 0x80);
    		int result=  reader.Inventory_Temperature(param.ComAddr,ENum,EPC,SelTarget,SelAction,WaitTime,QValue, (byte)param.Session,(byte)0,(byte)0x80,(byte)10,pOUcharIDList,pOUcharTagNum,pListLen);

    	}
		if(pOUcharTagNum[0]>0)
		{
			playSound();
		}
	}
}

