package com.tarento.downloadmanager;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;



public class DownloadManager extends CordovaPlugin {

    private static final String ACTION_NAME = "download";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_NAME)) {
            String message = args.getString(0);
            this.startDownload(message, callbackContext);
            return true;
        }
        return false;
    }

    private void startDownload(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            String filename = message.substring(message.lastIndexOf("/")+1, message.length());
            try {
                filename = URLDecoder.decode(filename,"UTF-8");
            } catch (UnsupportedEncodingException e) {

                callbackContext.error("Error in converting filename");
            }
            android.app.DownloadManager downloadManager = (android.app.DownloadManager) cordova.getActivity().getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);            
            Uri Download_Uri = Uri.parse(message);
            android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Download_Uri);
         
            request.setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_WIFI | android.app.DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle(filename);
            request.setDescription("DataSync File Download.");
            request.setDestinationInExternalFilesDir(cordova.getActivity().getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, filename);
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            long downloadReference = downloadManager.enqueue(request);
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}