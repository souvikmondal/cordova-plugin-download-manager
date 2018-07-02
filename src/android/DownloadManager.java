package com.tarento.downloadmanager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import org.apache.cordova.PluginResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DownloadManager extends CordovaPlugin {

    private static final String ACTION_NAME_ENQUE = "enque";
    // private static final String ACTION_NAME_PROGRESS = "progress";
    public int prog=-1;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_NAME_ENQUE)) {
            String path = args.getString(0);
            this.startDownload(path, callbackContext);
            
            String progress = "";
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, progress);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }
        /*
         * else if(action.equals(ACTION_NAME_PROGRESS)){ String path =
         * args.getString(0); this.getProgress(path, callbackContext); return true; }
         */
        return false;
    }

    private void startDownload(String path, CallbackContext callbackContext) {
        if (path == null || path.length() < 0) {
            callbackContext.error("Expected one non-empty string argument.");
        } else {
            String filename = path.substring(path.lastIndexOf("/") + 1, path.length());
            try {
                filename = URLDecoder.decode(filename, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                callbackContext.error("Error in converting filename");
            }

            Log.e("DownloadManager", "startDownload: Path - " + path);
            Log.e("DownloadManager", "startDownload: Filename - " + filename);

            final android.app.DownloadManager downloadManager = (android.app.DownloadManager) cordova.getActivity()
                    .getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(path);
            android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Download_Uri);

            request.setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_WIFI
                    | android.app.DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle(filename);
            request.setDescription("DataSync File Download.");
            request.setDestinationInExternalFilesDir(cordova.getActivity().getApplicationContext(),
                    Environment.DIRECTORY_DOWNLOADS, filename);
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            final long downloadReference = downloadManager.enqueue(request);

            new Thread(new Runnable() {

                @Override
                public void run() {

                    boolean downloading = true;

                    while (downloading) {

                        android.app.DownloadManager.Query q = new android.app.DownloadManager.Query();
                        q.setFilterById(downloadReference);

                        Cursor cursor = downloadManager.query(q);
                        cursor.moveToFirst();
                        int bytes_downloaded = cursor.getInt(
                                cursor.getColumnIndex(android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor
                                .getInt(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        final double dl_progress = (int) ((double) bytes_downloaded / (double) bytes_total * 100f);

                        if (cursor.getInt(cursor.getColumnIndex(
                                android.app.DownloadManager.COLUMN_STATUS)) == android.app.DownloadManager.STATUS_SUCCESSFUL) {
                            callbackContext.success((int) dl_progress);
                            downloading = false;
                        }

                        // Log.e("DownloadManager", "run: Total Bytes - " + bytes_total);
                        Log.e("DownloadManager", "run: Total Bytes Downloaded- " + bytes_downloaded);

                        cordova.getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Log.e("DownloadManager", "run: Progress - " + (int) dl_progress);
                                sendUpdate((int)dl_progress, callbackContext);
                                // callbackContext.success((int)dl_progress);
                            }
                        });

                        // Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor));
                        cursor.close();
                    }

                }
            }).start();
        }
    }

    /**
     * Create a new plugin result and send it back to JavaScript
     *
     * @param connection the network info to set as navigator.connection
     */
    private void sendUpdate(int progress, CallbackContext callbackContext) {
        
        if (callbackContext != null && prog != progress) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, progress);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            prog = progress;
        }
        //webView.postMessage("networkconnection", progress);
    }
}
