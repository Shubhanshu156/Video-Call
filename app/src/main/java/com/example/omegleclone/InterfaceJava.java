package com.example.omegleclone;

import android.webkit.JavascriptInterface;

import com.example.omegleclone.CallActivity;

public class InterfaceJava {

    CallActivity callActivity;

    public InterfaceJava(CallActivity callActivity) {
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected(){
        callActivity.onPeerConnected();
    }

}