var exec = require('cordova/exec');

var PLUGIN_NAME = 'DownloadManager';

var DownloadManager = {

    download: function (path) {
        exec(null, null, PLUGIN_NAME, "download", [path]);
    },

};

module.exports = DownloadManager;