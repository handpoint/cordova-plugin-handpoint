package com.handpoint.cordova.autostart.receivers;

public class KeyPressBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equals(Intent.ACTION_MEDIA_BUTTON))  
{
          KeyEvent  
keyEvent = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
          if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
              int  
keyCode = keyEvent.getKeyCode();
              if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { // Reemplaza con el código correcto
                  // Envía el evento a JavaScript
                  HandpointApiCordova.sendButtonPressedEvent();
              }
          }
      }
  }
}
