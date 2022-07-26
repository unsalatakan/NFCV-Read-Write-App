package com.example.server1.nfcv.service;

import android.app.IntentService;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;

import androidx.annotation.Nullable;

public class NfcService extends IntentService {
    private boolean tagFlag=true;
    public NfcService(){
        super("NfcService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Tag tag=intent.getParcelableExtra("tag");
        while (tagFlag){
            if (tag==null){
                Log.i("NormalActivity","this is NfcService");
                tagFlag=false;
            }else {
                try{
                    NfcV nfcV=NfcV.get(tag);
                    nfcV.connect();
                    while (tagFlag){
                        if (nfcV.isConnected()){
                        }else {
                            Intent intent1=new Intent("cn.ident.nas.tester.CHANGEUI");
                            sendBroadcast(intent1);
                            Log.i("NormalActivity","tag null");
                            tagFlag=false;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        tagFlag=true;
    }
}
