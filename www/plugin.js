var exec = require('cordova/exec');

var PLUGIN_NAME = 'DownloadManager';
var METHOD_NAME = 'enque';

var DownloadManager = {
    
    enqueue: function (path, success, error) {
        exec(success, error, PLUGIN_NAME, METHOD_NAME, [path]);
    },

   /* progress: function (, success, error) {
        value = parseFloat(progressPercentage);
        exec(success, error, PLUGIN_NAME, "progress", [value]);
    }*/
};
module.exports = DownloadManager;
