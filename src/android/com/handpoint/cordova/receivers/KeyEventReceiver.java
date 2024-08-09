package com.handpoint.cordova.receivers;

import org.apache.cordova.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KeyEventReceiver extends BroadcastReceiver {

  private CordovaWebView webView;

  private void sendJavascript(String js) {
    if (this.webView != null) {
      this.webView.loadUrl(js);
    }
  }

  public KeyEventReceiver(CordovaWebView webView) {
    this.webView = webView;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    int keyCode = intent.getIntExtra("keyCode", -1);
    if (keyCode != -1) {
      sendJavascript("javascript:cordova.fireDocumentEvent('keydown', {'key': '" + keyCode + "'});");
    }
  }

}
