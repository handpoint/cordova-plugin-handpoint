/**
 * Constructor.
 * @returns {XurlEngine}
 */
function XurlEngine() {

    /**
     * Protocol Version enum
     * @type Object
     */
    this.protocol = function()
    {
        this.versions = function () {
            this.v1 = 'v1';
            this.v2 = 'v2';
        }

        this.protocolFromString = function (version) {
            if (version == this.versions.v1) {
                return V1Engine();
<<<<<<< HEAD
            }
=======
            }   
>>>>>>> aa564d5c1dfa9e9c09018ebc32ee22adaeaa0ffd
            else if (version == this.versions.v2) {
                return V2Engine();
            }
            else
            {
                return null;
            }
        }
    }

    this

}