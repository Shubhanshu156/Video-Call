package com.example.omegleclone

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import android.os.Build

import android.webkit.PermissionRequest

import android.webkit.WebChromeClient
import kotlinx.android.synthetic.main.activity_call.*
import android.webkit.WebView

import android.webkit.WebViewClient
import android.widget.Toast
import android.content.pm.PackageManager
import android.util.Log

import androidx.core.app.ActivityCompat

class CallActivity : AppCompatActivity() {
    lateinit var uniqueid:String
    lateinit var roomid:String
    var isPeerConnected = false
    lateinit var friendid:String
    val TAG="CAll"
    val permissions =arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        while (!isPermissionsGranted()){
            askPermissions()
        }
        connectingImage.setOnClickListener {
            callJavaScriptFunction("javascript:toggleVideo(\""+true+"\")");
            callJavaScriptFunction("javascript:toggleAudio(\""+true+"\")");
        }

        val firebaseRef = FirebaseDatabase.getInstance().reference.child("users")

         uniqueid = intent.getStringExtra("username").toString()
        val incoming = intent.getStringExtra("incoming")
        friendid= incoming.toString()
        val createdBy = intent.getStringExtra("createdBy")
        roomid=intent.getStringExtra("roomid").toString()
        Toast.makeText(this, roomid, Toast.LENGTH_SHORT).show()
        val friendsUsername = incoming;

        setupWebView();
    }

    private fun setupWebView() {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(InterfaceJava(this), "Android")
        loadVideoCall()
    }

    fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        webView.loadUrl(filePath)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                initializePeer()
            }
        }
    }

    private fun initializePeer() {
        callJavaScriptFunction("javascript:init(\"$uniqueid\")")
//        sendCallRequest()
    }

//    private fun sendCallRequest() {
//        if(!isPeerConnected) {
//            Toast.makeText(this, "You are not connected. Please check your internet.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        listenConnId();
//    }

    private fun listenConnId() {

        callJavaScriptFunction("javascript:startCall(\""+roomid+"\")");
    }

    public fun onPeerConnected() {
        isPeerConnected=true
        listenConnId();
//        callJavaScriptFunction("javascript:toggleAudio(\""+true+"\")")

        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show()

    }
    fun callJavaScriptFunction(function: String) {
        webView.post(Runnable { webView.evaluateJavascript(function, null) })
    }
    private fun isPermissionsGranted(): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }
    fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, 123)
    }
}