/*
 * This file is part of the PhoneRemoteControl application.
 *
 * Copyright (C) 2014 Yoann Laissus (Arakmar) <yoann dot laissus at gmail dot com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.phoneremotecontrol.app.http;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import org.phoneremotecontrol.app.MainActivity;
import org.phoneremotecontrol.app.R;
import org.phoneremotecontrol.app.http.HttpServer;
import org.phoneremotecontrol.app.sms.SMSHttpWorker;
import org.phoneremotecontrol.app.sms.SMSUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpServerService extends Service {
    public static final String TAG = "HttpServerService";
    private static final String ROOT_DIR_SUFFIX = "web";
    private File rootPath;

    private HttpServer httpServer;
    private NotificationManager notificationManager;
    private final IBinder binder = new LocalBinder();

    public static File getRootDir(Context context) {
        return new File(context.getCacheDir() + "/" + ROOT_DIR_SUFFIX);
    }

    public File getRootDir() {
        return HttpServerService.getRootDir(this);
    }

    @Override
    public void onCreate() {
        rootPath = getRootDir();
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Stopping the service ...");
        httpServer.stop();
        removeDirectory(rootPath);
        closeNotification();
    }

    private void closeNotification() {
        notificationManager.cancel(R.string.http_service_notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Starting the service...");

        copyAssetFolder(getAssets(), ROOT_DIR_SUFFIX, rootPath.getPath());

        SMSHttpWorker smsWorker = new SMSHttpWorker(getApplicationContext(), "/sms");
        int port = intent.getIntExtra("http_port", 9999);
        String host = intent.getStringExtra("http_host");
        httpServer = new HttpServer(port, host, getRootDir());
        httpServer.addWorker(smsWorker);
        try {
            httpServer.start();
        } catch (IOException e) {
            Log.e(TAG, "IOException while starting the http server.");
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void showNotification() {
        Intent stopIntent = new Intent(getApplicationContext(), HttpServerServiceStopReceiver.class);

        Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
        clickIntent.addCategory("android.intent.category.LAUNCHER");
        clickIntent.setAction("android.intent.action.MAIN");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, clickIntent, 0);

        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Builder builder = new Builder(this)
                .setContentTitle(getString(R.string.notification_title))
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.app_icon)
                .addAction(R.drawable.quit2, getString(R.string.quit), stopPendingIntent)
                .setOngoing(true)
                .setContentText(getString(R.string.notification_content));

        // Send the notification.
        notificationManager.notify(R.string.http_service_notification, builder.build());
    }

    private boolean copyAssetFolder(AssetManager assetManager,
                                    String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains("."))
                    res &= copyAsset(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                else
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean copyAsset(AssetManager assetManager,
                              String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            dir.delete();
        }
    }

    public class LocalBinder extends Binder {
        public void stopService() {
            HttpServerService.this.stopSelf();
        }
    }
}
