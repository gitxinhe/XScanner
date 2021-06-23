package com.kiloway.xscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.kiloway.commonscanner.base.AllDevice;
import com.kiloway.commonscanner.base.Constant;
import com.kiloway.commonscanner.base.Device;
import com.kiloway.commonscanner.model.EpcInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Device.OnEventListener{
    public Device reader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reader = AllDevice.initUHF(this);
        reader.init(this);
        reader.inventoryAnyTag();
        reader.setOnEventListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reader.unitReader();
    }

    @Override
    public void onTagReadedEvent(EpcInfo info) {
        Log.e("tag",info.getEpc());
    }

    @Override
    public void onAnyTagReadedEvent(List<EpcInfo> infos) {
        Log.e("tag",infos.toString());
    }
}