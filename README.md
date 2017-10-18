# Cordova Handpoint SDK Plugin
# Still Work In Progress. Not ready for live use

Cross-platform Handpoint SDK for Cordova / PhoneGap.

Follows the [Cordova Plugin specification](https://cordova.apache.org/docs/en/latest/guide/hybrid/plugins/index.html).

# State of ios:
Just a prototype (not even that), what is missing:
* implementation!
* linker settings (objective C++ settings, see Handpoint iOS SDK documentation)
* plist settings (probably)

# How to implement:
Copy the implementation from our iOS client (mPos). Do the sale, handle the callback event in the object and call the javascript callback from there.

N.b. the names of the methods are not the same in iOS and Android/.Net so names must be mapped (I added the dictionary for that)

Handle system events (when the app is closed/paused, put in background etc.)
Add the current_reader variable to handle disconnect/reconnect when app goes got background, comes to foreground.
<<<<<<< HEAD
=======

>>>>>>> ea376fba59c2fffe9d9176efc804cd4caa25426d
