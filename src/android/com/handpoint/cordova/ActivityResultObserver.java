package com.handpoint.cordova;

import android.content.Intent;

public interface ActivityResultObserver {

  void onActivityResult(int requestCode, final int resultCode, final Intent data);

}
