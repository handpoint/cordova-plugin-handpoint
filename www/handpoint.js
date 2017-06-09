var exec = cordova.require("cordova/exec");

var echoInProgress = false;

/**
 * Constructor.
 * @returns {Handpoint}
 */
function Handpoint() {

    /**
     * Connection Method enum
     * @type Object
     */
    this.ConnectionMethod = {
        USB: "USB",
        SERIAL: "SERIAL",
        BLUETOOTH: "BLUETOOTH",
        HTTPS: "HTTPS",
        WIFI: "WIFI",
        ETHERNET: "ETHERNET",
        SIMULATOR: "SIMULATOR"
    };
}

/**
 * @param config
 * @param {Function} successCallback This function will get back the message
 * @param {Function} errorCallback
 */
Handpoint.prototype.echo = function (config, successCallback, errorCallback) {

    if (config instanceof Array) {
        // do nothing
    } else if (typeof (config) === 'object') {
        config = [config];
    } else {
        config = [];
    }

    if (errorCallback === null) {
        errorCallback = function () {};
    }

    if (typeof errorCallback != "function") {
        console.log("Handpoint.echo failure: failure parameter not a function");
        return;
    }

    if (typeof successCallback != "function") {
        console.log("Handpoint.echo failure: success callback parameter must be a function");
        return;
    }

    if (echoInProgress) {
        errorCallback('Scan is already in progress');
        return;
    }

    echoInProgress = true;

    exec(
        function (result) {
            echoInProgress = false;
            successCallback(result);
        },
        function (error) {
            echoInProgress = false;
            errorCallback(error);
        },
        'Handpoint',
        'echo',
        config
    );
};

var handpoint = new Handpoint();
module.exports = handpoint;
