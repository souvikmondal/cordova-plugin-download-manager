var exec = require('cordova/exec');
var CLASS_NAME='DownloadManager';
var METHOD_NAME='download';

var dmanager={
downloading: function(arg0, success, error) {
    exec(success, error, CLASS_NAME, METHOD_NAME, [arg0]);
}
};
module.exports=dmanager;
