# Cordova Handpoint SDK Plugin
# Still Work In Progress. Not ready for live use

Cross-platform Handpoint SDK for Cordova / PhoneGap.

Follows the [Cordova Plugin specification](https://cordova.apache.org/docs/en/latest/guide/hybrid/plugins/index.html).

# State of ios:
Just a prototype, what is missing:
* implementatio!
* linker settings
* plist settings (probably)

# How to implement:
Copy the implementation from our iOS client (mPos). Do the sale, handle the callback event in the object and call the javascript callback from there.

Add the current_reader variable.

Handle system events (when the app is closed/paused, put in background etc.)
