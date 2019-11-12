/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:22 PM
 * Last modified 11/11/19 7:21 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;

import androidx.annotation.RequiresApi;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;


public class LicenseValidator extends Service {

    private static final String BASE64_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw3dKO4aaiWJbo00MI6v1OC2SB5QUP/" +
                    "Y0UJ+L53Xl0QaH94l9NarYc9NvdI02SzW6XOiwR9A9y2eZeVe4F2OBRRbesqNxAakhXEcY63Enmg4tsMcaVi7SOBAXWj330LuuqqQdP/a9/oLwKNl" +
                    "RPKIuT8xYc/l+ogzBz2mRQWEDw4+CCIDP1C0hVy91Jf1ruSW4Nrie+ac0ADO6ZIeoYq3v6/aOCMusNnsgkZsNpwYDOCWGtJbuAjJM4MwY0kRKTv8c" +
                    "YU4BWS2e5VtpIKa7xxkeo3V16kjoXbupTd12IlhTvcVuH48uYEmo1j49f0Xcfw2hhxWzM5WmdPGsgd9XWYs/7wIDAQAB";
    private static final byte[] SALT = new byte[]{
            -16, -13, 30, -128, -103, -57, 74, -64, 53, 88, -97, -45, 77, -113, -36, -113, -11, 32, -64, 89
    };
    FunctionsClass functionsClass;
    LicenseChecker licenseChecker;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(111, bindServiceHIGH());
        } else {
            startForeground(111, bindServiceLOW());
        }

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        licenseChecker = new LicenseChecker(
                getApplicationContext(),
                new ServerManagedPolicy(getApplicationContext(), new AESObfuscator(SALT, getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY
        );
        final LicenseCheckerCallback licenseCheckerCallback = new LicenseCheckerCallback() {
            @Override
            public void allow(int reason) {
                functionsClass.saveFileAppendLine(".License", String.valueOf(reason));
                stopSelf();
            }

            @Override
            public void dontAllow(int reason) {
                sendBroadcast(new Intent(getString(R.string.license)));
            }

            @Override
            public void applicationError(int errorCode) {
            }
        };
        licenseChecker.checkAccess(licenseCheckerCallback);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        licenseChecker.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected Notification bindServiceHIGH() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
        notificationManager.createNotificationChannel(notificationChannel);

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
        mBuilder.setColor(getColor(R.color.default_color));
        mBuilder.setContentTitle(getString(R.string.license_info));
        mBuilder.setContentText(getString(R.string.license_info_desc));
        mBuilder.setContentTitle(Html.fromHtml("<b><font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info) + "</font></b>", Html.FROM_HTML_MODE_LEGACY));
        mBuilder.setContentText(Html.fromHtml("<font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info_desc) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        mBuilder.setTicker(getString(R.string.license_validating));
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setAutoCancel(false);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setChannelId(getPackageName());

        return mBuilder.build();
    }

    protected Notification bindServiceLOW() {
        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
        mBuilder.setColor(getColor(R.color.default_color));
        mBuilder.setContentTitle(getString(R.string.license_info));
        mBuilder.setContentText(getString(R.string.license_info_desc));
        mBuilder.setContentTitle(Html.fromHtml("<b><font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info) + "</font></b>", Html.FROM_HTML_MODE_LEGACY));
        mBuilder.setContentText(Html.fromHtml("<font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info_desc) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        mBuilder.setTicker(getString(R.string.license_validating));
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setAutoCancel(false);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setPriority(Notification.PRIORITY_MIN);

        return mBuilder.build();
    }
}
