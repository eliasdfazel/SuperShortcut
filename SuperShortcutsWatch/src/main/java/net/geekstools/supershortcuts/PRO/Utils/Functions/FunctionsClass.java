/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 3:54 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.Functions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.ItemListAdapter;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionsClass {

    Context context;

    public FunctionsClass(Context context) {
        this.context = context;
    }

    public void addAppShortcuts() {
        try {
            final PackageManager manager = context.getPackageManager();
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(".autoSuper"));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            int maxLoop;
            if (appShortcuts.size() > shortcutManager.getMaxShortcutCountPerActivity()) {
                maxLoop = shortcutManager.getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }

            for (int i = 0; i < maxLoop; i++) {
                if (appInstalledOrNot(appShortcuts.get(i)) == false) {
                    context.deleteFile(appShortcuts.get(i) + ".Super");
                    removeLine(".autoSuper", appShortcuts.get(i));
                } else {
                    try {
                        Intent intent = manager.getLaunchIntentForPackage(appShortcuts.get(i));
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, String.valueOf(i))
                                .setShortLabel(appName(appShortcuts.get(i)))
                                .setLongLabel(appName(appShortcuts.get(i)))
                                .setIcon(Icon.createWithBitmap(appIconBitmap(appShortcuts.get(i))))
                                .setIntent(intent)
                                .setRank(i)
                                .build();

                        shortcutInfos.add(shortcutInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            shortcutManager.addDynamicShortcuts(shortcutInfos);
            Toast(context.getString(R.string.done));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*File*/
    public void saveFile(String fileName, String content) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fOut.write((content).getBytes());

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFileAppendLine(String fileName, String contentLine) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_APPEND);
            fOut.write((contentLine + "\n").getBytes());

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readFileLine(String fileName) {

        return new FunctionsClassIO(context).readFileLines(fileName);
    }

    public void removeLine(String fileName, String lineToRemove) {
        try {
            FileInputStream fin = context.openFileInput(fileName);
            DataInputStream myDIS = new DataInputStream(fin);
            OutputStreamWriter fOut = new OutputStreamWriter(context.openFileOutput(fileName + ".tmp", Context.MODE_APPEND));

            String tmp = "";
            while ((tmp = myDIS.readLine()) != null) {
                if (!tmp.trim().equals(lineToRemove)) {
                    fOut.write(tmp);
                    fOut.write("\n");
                }
            }
            fOut.close();
            myDIS.close();
            fin.close();

            File tmpD = context.getFileStreamPath(fileName + ".tmp");
            File New = context.getFileStreamPath(fileName);

            if (tmpD.isFile()) {
            }
            context.deleteFile(fileName);
            tmpD.renameTo(New);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countLine(String fileName) {

        return new FunctionsClassIO(context).fileLinesCounter(fileName);
    }

    public void savePreference(String PreferenceName, String KEY, String VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putString(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void savePreference(String PreferenceName, String KEY, int VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putInt(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void savePreference(String PreferenceName, String KEY, boolean VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putBoolean(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void saveDefaultPreference(String KEY, int VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putInt(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public String readPreference(String PreferenceName, String KEY, String defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getString(KEY, defaultVALUE);
    }

    public int readPreference(String PreferenceName, String KEY, int defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE);
    }

    public boolean readPreference(String PreferenceName, String KEY, boolean defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE);
    }

    public int readDefaultPreference(String KEY, int defaultVALUE) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, defaultVALUE);
    }

    public String readDefaultPreference(String KEY, String defaultVALUE) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, defaultVALUE);
    }

    public boolean readDefaultPreference(String KEY, boolean defaultVALUE) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY, defaultVALUE);
    }

    /*Checkpoint Function*/
    public void dialogueLicense(Activity instanceOfActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(instanceOfActivity, R.style.GeeksEmpire_Dialogue_Day);
        alertDialog.setTitle(Html.fromHtml(context.getString(R.string.license_title)));
        alertDialog.setMessage(Html.fromHtml(context.getString(R.string.license_msg)));
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.buy), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent r = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.link_play_store) + context.getPackageName()));
                r.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(r);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Uri packageUri = Uri.parse("package:" + context.getPackageName());
                        Intent uninstallIntent =
                                new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(uninstallIntent);
                    }
                }, 2333);
            }
        });
        alertDialog.setNegativeButton(context.getString(R.string.free), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent r = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.link_play_store) + context.getPackageName().replace(".PRO", "")));
                r.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(r);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Uri packageUri = Uri.parse("package:" + context.getPackageName());
                        Intent uninstallIntent =
                                new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(uninstallIntent);
                    }
                }, 2333);
            }
        });
        alertDialog.setNeutralButton(context.getString(R.string.contact), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //dialog.dismiss();
                String[] contactOption = new String[]{
                        "Send an Email"};
                AlertDialog.Builder builder = new AlertDialog.Builder(instanceOfActivity);
                builder.setTitle(context.getString(R.string.supportTitle));
                builder.setSingleChoiceItems(contactOption, 0, null);
                builder.setCancelable(false);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition == 0) {
                            String textMsg = "\n\n\n\n\n"
                                    + "[Essential Information]" + "\n"
                                    + getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + getCountryIso().toUpperCase();
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.support)});
                            email.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_tag) + " [" + appVersionName(context.getPackageName()) + "] ");
                            email.putExtra(Intent.EXTRA_TEXT, textMsg);
                            email.setType("message/*");
                            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(Intent.createChooser(email, context.getString(R.string.feedback_tag)));
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        instanceOfActivity.finish();
                    }
                });
                builder.show();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        try {
            alertDialog.show();
        } catch (Exception e) {
            instanceOfActivity.finish();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.startActivity(context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, 300);
        }
    }

    public boolean networkConnection() {
        boolean networkAvailable = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);

            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    networkAvailable = true;
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    networkAvailable = true;
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    networkAvailable = true;
                }
            }
        }

        return networkAvailable;
    }

    public boolean appInstalledOrNot(String packName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(packName, 0);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        } catch (Exception e) {
            e.printStackTrace();
            app_installed = false;
        }
        return app_installed;
    }

    public boolean ifSystem(String packageName) {
        boolean ifSystem = false;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo targetPkgInfo = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            PackageInfo sys = packageManager.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            ifSystem = (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            ifSystem = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ifSystem;
    }

    public boolean ifDefaultLauncher(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
        if (defaultLauncherStr.equals(packageName)) {
            return true;
        }
        return false;
    }

    public String appName(String pack) {
        String Name = null;

        try {
            PackageManager packManager = context.getPackageManager();
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(pack, 0);
            Name = packManager.getApplicationLabel(app).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Name;
    }

    public String appVersionName(String pack) {
        String Version = "0";

        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(pack, 0);
            Version = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Version;
    }

    public int appVersionCode(String pack) {
        int Version = 0;

        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(pack, 0);
            Version = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Version;
    }

    public Drawable appIconDrawable(String pack) {
        Drawable icon = null;
        try {
            PackageManager packManager = context.getPackageManager();
            icon = packManager.getApplicationIcon(pack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return icon;
    }

    public Bitmap appIconBitmap(String pack) {
        Bitmap bitmap = null;
        try {
            Drawable drawableIcon = context.getPackageManager().getApplicationIcon(pack);
            if (drawableIcon instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawableIcon).getBitmap();
            } else if (drawableIcon instanceof AdaptiveIconDrawable) {
                Drawable backgroundDrawable = ((AdaptiveIconDrawable) drawableIcon).getBackground();
                Drawable foregroundDrawable = ((AdaptiveIconDrawable) drawableIcon).getForeground();

                Drawable[] drawables = new Drawable[2];
                drawables[0] = backgroundDrawable;
                drawables[0].setAlpha(0);
                drawables[1] = foregroundDrawable;

                LayerDrawable layerDrawable = new LayerDrawable(drawables);
                int width = layerDrawable.getIntrinsicWidth();
                int height = layerDrawable.getIntrinsicHeight();

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                layerDrawable.draw(canvas);
            } else {
                bitmap = Bitmap.createBitmap(drawableIcon.getIntrinsicWidth(), drawableIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void openApplication(String packageName) {
        if (appInstalledOrNot(packageName) == true) {
            try {
                Toast.makeText(context,
                        appName(packageName), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_MAIN);
                PackageManager manager = context.getPackageManager();
                i = manager.getLaunchIntentForPackage(packageName);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getString(R.string.not_install), Toast.LENGTH_LONG).show();
                Intent playStore = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.link_play_store) + packageName));
                playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(playStore);
            }
        } else {
            Toast.makeText(context, context.getString(R.string.not_install), Toast.LENGTH_LONG).show();
            Intent playStore = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.link_play_store) + packageName));
            playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(playStore);
        }
    }

    public void goToSettingInfo(Activity instanceOfActivity, String packageName) {
        Intent goToSettingInfo = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        goToSettingInfo.addCategory(Intent.CATEGORY_DEFAULT);
        goToSettingInfo.setData(Uri.parse("package:" + packageName));
        goToSettingInfo.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        instanceOfActivity.startActivityForResult(goToSettingInfo, 1000);
    }

    public void overrideBackPress(Activity instanceOfActivity, Class returnClass) throws Exception {
        context.startActivity(new Intent(context, returnClass).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                instanceOfActivity.finish();
            }
        }, 100);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public String getCountryIso() {
        String countryISO = "Undefined";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            countryISO = telephonyManager.getSimCountryIso();
        } catch (Exception e) {
            e.printStackTrace();
            countryISO = "Undefined";
        }
        return countryISO;
    }

    public int returnAPI() {
        return Build.VERSION.SDK_INT;
    }

    /*UI*/
    public void Toast(String toastContent) {
        Toast toast = new Toast(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_view, null);

        LayerDrawable drawToast = (LayerDrawable) context.getDrawable(R.drawable.toast_background);
        GradientDrawable backToast = (GradientDrawable) drawToast.findDrawableByLayerId(R.id.backtemp);

        TextView textView = (TextView) layout.findViewById(R.id.toastText);
        textView.setText(Html.fromHtml(toastContent));
        textView.setBackground(drawToast);
        textView.setShadowLayer(0.02f, 2, 2, context.getColor(R.color.trans_dark_high));


        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public int DpToInteger(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /*normal*/
    public void showPopupItem(Activity instanceOfActivity, RelativeLayout popupAnchorView,
                              String categoryName, String[] packagesName) {

        ArrayList<AdapterItemsData> adapterItemsData = new ArrayList<AdapterItemsData>();
        adapterItemsData.clear();

        for (String packageName : packagesName) {

            adapterItemsData.add(new AdapterItemsData(
                    appName(packageName),
                    packageName,
                    appIconDrawable(packageName)));
        }

        adapterItemsData.add(new AdapterItemsData(
                context.getString(R.string.edit_advanced_shortcut) + " " + categoryName,
                context.getPackageName(),
                context.getDrawable(R.drawable.draw_pref)));

        ListPopupWindow listPopupWindow = new ListPopupWindow(instanceOfActivity);

        ItemListAdapter lowerListAdapter = new ItemListAdapter(instanceOfActivity, context, adapterItemsData, listPopupWindow);

        listPopupWindow.setAdapter(lowerListAdapter);
        listPopupWindow.setAnchorView(popupAnchorView);
        listPopupWindow.setWidth(popupAnchorView.getWidth());
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setModal(true);
        listPopupWindow.setBackgroundDrawable(null);
        listPopupWindow.show();

        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                instanceOfActivity.finish();
            }
        });
    }

    /*Firebase Remote Config*/
    public String versionCodeRemoteConfigKey() {
        String versionCodeKey = null;
        if (readPreference(".BETA", "isBetaTester", false)) {
            versionCodeKey = context.getString(R.string.BETAintegerVersionCodeNewUpdateWear);
        } else {
            versionCodeKey = context.getString(R.string.integerVersionCodeNewUpdateWear);
        }
        return versionCodeKey;
    }

    public String versionNameRemoteConfigKey() {
        String versionCodeKey = null;
        if (readPreference(".BETA", "isBetaTester", false)) {
            versionCodeKey = context.getString(R.string.BETAstringVersionNameNewUpdateWear);
        } else {
            versionCodeKey = context.getString(R.string.stringVersionNameNewUpdateWear);
        }
        return versionCodeKey;
    }

    public String upcomingChangeLogRemoteConfigKey() {
        String versionCodeKey = null;
        if (readPreference(".BETA", "isBetaTester", false)) {
            versionCodeKey = context.getString(R.string.BETAstringUpcomingChangeLogWear);
        } else {
            versionCodeKey = context.getString(R.string.stringUpcomingChangeLogWear);
        }
        return versionCodeKey;
    }
}