/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 10/5/21, 6:40 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.Functions;

import static android.content.Context.APP_OPS_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone;
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Utils.AppShortcutsMediatedActivity;
import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Adapters.FolderItemListAdapter;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.LoadFolderPopupShortcuts;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices.SplitScreenService;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices.SplitTransparentPair;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionsClass {

    Context context;

    int API;

    String[] categoryNamesSelected, SplitNamesSelected;
    LayerDrawable drawCategory;

    public FunctionsClass(Context context) {
        this.context = context;

        API = Build.VERSION.SDK_INT;
    }

    public void addToSuperShortcuts(String fileName) {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(fileName));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            int maxLoop;
            if (appShortcuts.size() > context.getSystemService(ShortcutManager.class).getMaxShortcutCountPerActivity()) {
                maxLoop = context.getSystemService(ShortcutManager.class).getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }
            for (int i = 0; i < maxLoop; i++) {
                try {
                    Intent intent = new Intent(context, AppShortcutsMediatedActivity.class);
                    intent.setAction("MEDIATED_ACTIVITY_PRO");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("PackageName", appShortcuts.get(i));

                    ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, String.valueOf(i))
                            .setShortLabel(appName(appShortcuts.get(i)))
                            .setLongLabel(appName(appShortcuts.get(i)))
                            .setIcon(Icon.createWithBitmap(getAppIconBitmapCustomIcon(appShortcuts.get(i))))
                            .setIntent(intent)
                            .setRank(i)
                            .build();

                    shortcutInfos.add(shortcutInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            shortcutManager.addDynamicShortcuts(shortcutInfos);
            if (context.getSystemService(ShortcutManager.class).getDynamicShortcuts().size() == countLine(fileName)) {

                Toast(context.getString(R.string.done), context.getColor(R.color.default_color_darker),
                        103,
                        true);

                appToDesktop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDynamicShortcuts() throws NullPointerException {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(250, 250));
        } else {
            vibrator.vibrate(150);
        }

        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        shortcutManager.removeAllDynamicShortcuts();

        Toast("Removed", context.getColor(R.color.red),
                103,
                true);
    }

    public void removeHomeShortcut(String className, String intentAction, String intentCategory, String shortcutName) {
        Intent differentIntent = new Intent();
        differentIntent.setClassName(context.getPackageName(), context.getPackageName() + className);
        differentIntent.setAction(intentAction);
        differentIntent.addCategory(intentCategory);

        Intent removeIntent = new Intent();
        removeIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, differentIntent);
        removeIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        removeIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        context.sendBroadcast(removeIntent);
    }

    /*Shortcut*/
    public void addAppShortcuts() {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            int maxLoop;
            if (appShortcuts.size() > shortcutManager.getMaxShortcutCountPerActivity()) {
                maxLoop = shortcutManager.getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }
            for (int i = 0; i < maxLoop; i++) {
                FunctionsClassDebug.Companion.PrintDebug(appShortcuts.get(i));
                String packageName = appShortcuts.get(i).split("\\|")[0];
                String className = appShortcuts.get(i).split("\\|")[1];
                if (isAppInstalled(packageName) == false) {
                    context.deleteFile(appShortcuts.get(i) + ".Super");
                    removeLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile, appShortcuts.get(i));
                } else {
                    try {
                        ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(new ComponentName(packageName, className), 0);

                        Intent intent = new Intent(context, AppShortcutsMediatedActivity.class);
                        intent.setAction("MEDIATED_ACTIVITY_PRO");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("PackageName", packageName);
                        intent.putExtra("ClassName", className);

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, (appShortcuts.get(i)))
                                .setShortLabel(activityLabel(activityInfo))
                                .setLongLabel(activityLabel(activityInfo))
                                .setIcon(Icon.createWithBitmap(getAppIconBitmapCustomIcon(activityInfo)))
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
            if (shortcutManager.getDynamicShortcuts().size() == countLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile)) {
                Toast(context.getString(R.string.done), context.getColor(R.color.default_color_darker),
                        103,
                        true);
                appToDesktop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAppShortcutsCustomIconsPref() {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            int maxLoop;
            if (appShortcuts.size() > shortcutManager.getMaxShortcutCountPerActivity()) {
                maxLoop = shortcutManager.getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }
            for (int i = 0; i < maxLoop; i++) {
                FunctionsClassDebug.Companion.PrintDebug(appShortcuts.get(i));
                String packageName = appShortcuts.get(i).split("\\|")[0];
                String className = appShortcuts.get(i).split("\\|")[1];
                if (isAppInstalled(packageName) == false) {
                    context.deleteFile(appShortcuts.get(i) + ".Super");
                    removeLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile, appShortcuts.get(i));
                } else {
                    try {
                        ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(new ComponentName(packageName, className), 0);

                        Intent intent = new Intent(context, AppShortcutsMediatedActivity.class);
                        intent.setAction("MEDIATED_ACTIVITY_PRO");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("PackageName", packageName);
                        intent.putExtra("ClassName", className);

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, (appShortcuts.get(i)))
                                .setShortLabel(activityLabel(activityInfo))
                                .setLongLabel(activityLabel(activityInfo))
                                .setIcon(Icon.createWithBitmap(getAppIconBitmapCustomIcon(activityInfo)))
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
            if (shortcutManager.getDynamicShortcuts().size() == countLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile)) {
                Toast(context.getString(R.string.done), context.getColor(R.color.light), context.getColor(R.color.default_color_darker), Gravity.BOTTOM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAppShortcutsFreqApps() {
        try {
            final PackageManager manager = context.getPackageManager();
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(".superFreq"));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            int maxLoop;
            if (appShortcuts.size() > shortcutManager.getMaxShortcutCountPerActivity()) {
                maxLoop = shortcutManager.getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }
            for (int i = 0; i < maxLoop; i++) {
                if (isAppInstalled(appShortcuts.get(i)) == false) {
                    removeLine(".superFreq", appShortcuts.get(i));
                } else {
                    try {
                        Intent intent = manager.getLaunchIntentForPackage(appShortcuts.get(i));
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, (appShortcuts.get(i)))
                                .setShortLabel(appName(appShortcuts.get(i)))
                                .setLongLabel(appName(appShortcuts.get(i)))
                                .setIcon(Icon.createWithBitmap(getAppIconBitmapCustomIcon(appShortcuts.get(i))))
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
            if (shortcutManager.getDynamicShortcuts().size() == countLine(".superFreq")) {
                Toast(context.getString(R.string.done), context.getColor(R.color.light), context.getColor(R.color.default_color_darker), Gravity.BOTTOM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String packageNameSelected(String packageName) {
        return packageName + ".Super";
    }

    public void appToDesktop() {
        Toast(context.getString(R.string.cautionShortcutsHome), context.getColor(R.color.light), context.getColor(R.color.dark), Gravity.BOTTOM, true);
    }

    /*Mix*/
    public void addMixAppShortcuts() {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            shortcutManager.removeAllDynamicShortcuts();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }


            List<String> lineShortcuts = Arrays.asList(readFileLine(".mixShortcuts"));
            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();
            int maxLoop;
            if (lineShortcuts.size() > getSystemMaxAppShortcut()) {
                maxLoop = getSystemMaxAppShortcut();
            } else {
                maxLoop = lineShortcuts.size();
            }
            for (int i = 0; i < maxLoop; i++) {
                if (lineShortcuts.get(i).contains(".CategorySelected")) {
                    try {
                        String[] packagesName = readFileLine(lineShortcuts.get(i));
                        Intent intent = new Intent(context, LoadFolderPopupShortcuts.class);
                        intent.putExtra("categoryName", lineShortcuts.get(i));
                        intent.setAction("load_category_action");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        switch (i) {
                            case 0:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_one);
                                break;
                            case 1:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_two);
                                break;
                            case 2:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_three);
                                break;
                            case 3:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_four);
                                break;
                            case 4:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_five);
                                break;
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.one, getAppIconDrawableCustomIcon(packagesName[0]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.one, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.two, getAppIconDrawableCustomIcon(packagesName[1]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.two, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.three, getAppIconDrawableCustomIcon(packagesName[2]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.three, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.four, getAppIconDrawableCustomIcon(packagesName[3]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.four, null);
                        }

                        Bitmap bitmap = Bitmap
                                .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
                        drawCategory.draw(new Canvas(bitmap));

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, lineShortcuts.get(i).replace(".CategorySelected", ""))
                                .setShortLabel(lineShortcuts.get(i).replace(".CategorySelected", "").split("_")[0])
                                .setLongLabel(lineShortcuts.get(i).replace(".CategorySelected", "").split("_")[0])
                                .setIcon(Icon.createWithBitmap(bitmap))
                                .setIntent(intent)
                                .setRank(i)
                                .build();

                        shortcutInfos.add(shortcutInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (lineShortcuts.get(i).contains(".SplitSelected")) {
                    try {
                        String[] packagesName = readFileLine(lineShortcuts.get(i));
                        Intent intent = new Intent(context, SplitTransparentPair.class);
                        intent.putExtra("packages", packagesName);
                        intent.putExtra("categoryName", lineShortcuts.get(i));
                        intent.setAction("load_split_action_pair");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        switch (i) {
                            case 0:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_one);
                                break;
                            case 1:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_two);
                                break;
                            case 2:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_three);
                                break;
                            case 3:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_four);
                                break;
                            case 4:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_five);
                                break;
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.one, getAppIconDrawableCustomIcon(packagesName[0]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.one, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.two, null);
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.two, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.three, null);
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.three, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.four, getAppIconDrawableCustomIcon(packagesName[1]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.four, null);
                        }


                        Bitmap bitmap = Bitmap
                                .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
                        drawCategory.draw(new Canvas(bitmap));

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, lineShortcuts.get(i).replace(".SplitSelected", ""))
                                .setShortLabel(lineShortcuts.get(i).replace(".SplitSelected", "").split("_")[0])
                                .setLongLabel(lineShortcuts.get(i).replace(".SplitSelected", "").split("_")[0])
                                .setIcon(Icon.createWithBitmap(bitmap))
                                .setIntent(intent)
                                .setRank(i)
                                .build();

                        shortcutInfos.add(shortcutInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String packageName = lineShortcuts.get(i).split("\\|")[0];
                    String className = lineShortcuts.get(i).split("\\|")[1];
                    if (!isAppInstalled(packageName)) {
                        context.deleteFile(lineShortcuts.get(i) + ".Super");
                        removeLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile, lineShortcuts.get(i));
                        removeLine(".mixShortcuts", lineShortcuts.get(i));
                    } else {
                        try {
                            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(new ComponentName(packageName, className), 0);

                            Intent intent = new Intent(context, AppShortcutsMediatedActivity.class);
                            intent.setAction("MEDIATED_ACTIVITY_PRO");
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("PackageName", packageName);
                            intent.putExtra("ClassName", className);

                            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, (lineShortcuts.get(i)))
                                    .setShortLabel(activityLabel(activityInfo))
                                    .setLongLabel(activityLabel(activityInfo))
                                    .setIcon(Icon.createWithBitmap(getAppIconBitmapCustomIcon(activityInfo)))
                                    .setIntent(intent)
                                    .setRank(i)
                                    .build();

                            shortcutInfos.add(shortcutInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            shortcutManager.addDynamicShortcuts(shortcutInfos);
            if (context.getSystemService(ShortcutManager.class).getDynamicShortcuts().size() == countLine(".mixShortcuts")) {
                Toast(context.getString(R.string.done), context.getColor(R.color.default_color_darker),
                        103,
                        true);
                Toast(context.getString(R.string.cautionShortcutsHome), context.getColor(R.color.light), context.getColor(R.color.dark), Gravity.BOTTOM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMixAppShortcutsCustomIconsPref() {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            shortcutManager.removeAllDynamicShortcuts();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            List<String> lineShortcuts = Arrays.asList(readFileLine(".mixShortcuts"));
            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();
            int maxLoop;
            if (lineShortcuts.size() > getSystemMaxAppShortcut()) {
                maxLoop = getSystemMaxAppShortcut();
            } else {
                maxLoop = lineShortcuts.size();
            }
            for (int i = 0; i < maxLoop; i++) {
                if (lineShortcuts.get(i).contains(".CategorySelected")) {
                    try {
                        String[] packagesName = readFileLine(lineShortcuts.get(i));
                        Intent intent = new Intent(context, LoadFolderPopupShortcuts.class);
                        intent.putExtra("categoryName", lineShortcuts.get(i));
                        intent.setAction("load_category_action");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        switch (i) {
                            case 0:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_one);
                                break;
                            case 1:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_two);
                                break;
                            case 2:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_three);
                                break;
                            case 3:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_four);
                                break;
                            case 4:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_five);
                                break;
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.one, getAppIconDrawableCustomIcon(packagesName[0]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.one, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.two, getAppIconDrawableCustomIcon(packagesName[1]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.two, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.three, getAppIconDrawableCustomIcon(packagesName[2]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.three, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.four, getAppIconDrawableCustomIcon(packagesName[3]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.four, null);
                        }

                        Bitmap bitmap = Bitmap
                                .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
                        drawCategory.draw(new Canvas(bitmap));

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, lineShortcuts.get(i).replace(".CategorySelected", ""))
                                .setShortLabel(lineShortcuts.get(i).replace(".CategorySelected", "").split("_")[0])
                                .setLongLabel(lineShortcuts.get(i).replace(".CategorySelected", "").split("_")[0])
                                .setIcon(Icon.createWithBitmap(bitmap))
                                .setIntent(intent)
                                .setRank(i)
                                .build();

                        shortcutInfos.add(shortcutInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (lineShortcuts.get(i).contains(".SplitSelected")) {
                    try {
                        String[] packegesName = readFileLine(lineShortcuts.get(i));
                        Intent intent = new Intent(context, SplitTransparentPair.class);
                        intent.putExtra("packages", packegesName);
                        intent.putExtra("categoryName", lineShortcuts.get(i));
                        intent.setAction("load_split_action_pair");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        switch (i) {
                            case 0:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_one);
                                break;
                            case 1:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_two);
                                break;
                            case 2:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_three);
                                break;
                            case 3:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_four);
                                break;
                            case 4:
                                drawCategory = (LayerDrawable) context.getResources()
                                        .getDrawable(R.drawable.category_icons_five);
                                break;
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.one, getAppIconDrawableCustomIcon(packegesName[0]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.one, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.two, null);
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.two, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.three, null);
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.three, null);
                        }

                        try {
                            drawCategory.setDrawableByLayerId(R.id.four, getAppIconDrawableCustomIcon(packegesName[1]));
                        } catch (Exception e) {
                            drawCategory.setDrawableByLayerId(R.id.four, null);
                        }


                        Bitmap bitmap = Bitmap
                                .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
                        drawCategory.draw(new Canvas(bitmap));

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, lineShortcuts.get(i).replace(".SplitSelected", ""))
                                .setShortLabel(lineShortcuts.get(i).replace(".SplitSelected", "").split("_")[0])
                                .setLongLabel(lineShortcuts.get(i).replace(".SplitSelected", "").split("_")[0])
                                .setIcon(Icon.createWithBitmap(bitmap))
                                .setIntent(intent)
                                .setRank(i)
                                .build();

                        shortcutInfos.add(shortcutInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String packageName = lineShortcuts.get(i).split("\\|")[0];
                    String className = lineShortcuts.get(i).split("\\|")[1];
                    if (!isAppInstalled(packageName)) {
                        context.deleteFile(lineShortcuts.get(i) + ".Super");
                        removeLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile, lineShortcuts.get(i));
                        removeLine(".mixShortcuts", lineShortcuts.get(i));
                    } else {
                        try {
                            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(new ComponentName(packageName, className), 0);

                            Intent intent = new Intent(context, AppShortcutsMediatedActivity.class);
                            intent.setAction("MEDIATED_ACTIVITY_PRO");
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("PackageName", packageName);
                            intent.putExtra("ClassName", className);

                            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, (lineShortcuts.get(i)))
                                    .setShortLabel(activityLabel(activityInfo))
                                    .setLongLabel(activityLabel(activityInfo))
                                    .setIcon(Icon.createWithBitmap(getAppIconBitmapCustomIcon(activityInfo)))
                                    .setIntent(intent)
                                    .setRank(i)
                                    .build();

                            shortcutInfos.add(shortcutInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            shortcutManager.addDynamicShortcuts(shortcutInfos);
            if (context.getSystemService(ShortcutManager.class).getDynamicShortcuts().size() == countLine(".mixShortcuts")) {
                Toast(context.getString(R.string.done), context.getColor(R.color.light), context.getColor(R.color.default_color_darker), Gravity.BOTTOM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Split*/
    public void addAppsShortcutSplit() {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(".SplitSuperSelected"));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            int maxLoop;
            if (appShortcuts.size() > shortcutManager.getMaxShortcutCountPerActivity()) {
                maxLoop = shortcutManager.getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }
            SplitNamesSelected = new String[maxLoop];
            SplitNamesSelected = (String[]) appShortcuts.toArray();

            for (int i = 0; i < maxLoop; i++) {
                try {
                    String[] packgesName = readFileLine(SplitNamesSelected[i]);
                    Intent intent = new Intent(context, SplitTransparentPair.class);
                    intent.putExtra("packages", packgesName);
                    intent.putExtra("categoryName", SplitNamesSelected[i]);
                    intent.setAction("load_split_action_pair");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    switch (i) {
                        case 0:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_one);
                            break;
                        case 1:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_two);
                            break;
                        case 2:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_three);
                            break;
                        case 3:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_four);
                            break;
                        case 4:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_five);
                            break;
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.one, null);
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.one, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.two, getAppIconDrawableCustomIcon(packgesName[0]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.two, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.three, getAppIconDrawableCustomIcon(packgesName[1]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.three, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.four, null);
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.four, null);
                    }


                    Bitmap bitmap = Bitmap
                            .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
                    drawCategory.draw(new Canvas(bitmap));

                    ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, SplitNamesSelected[i].replace(".SplitSelected", ""))
                            .setShortLabel(SplitNamesSelected[i].replace(".SplitSelected", "").split("_")[0])
                            .setLongLabel(SplitNamesSelected[i].replace(".SplitSelected", "").split("_")[0])
                            .setIcon(Icon.createWithBitmap(bitmap))
                            .setIntent(intent)
                            .setRank(i)
                            .build();

                    shortcutInfos.add(shortcutInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            shortcutManager.addDynamicShortcuts(shortcutInfos);
            if (context.getSystemService(ShortcutManager.class).getDynamicShortcuts().size() == countLine(".SplitSuperSelected")) {
                Toast(context.getString(R.string.done), context.getColor(R.color.default_color_darker),
                        103,
                        true);
                Toast(context.getString(R.string.cautionShortcutsHome), context.getColor(R.color.light), context.getColor(R.color.dark), Gravity.BOTTOM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAppsShortcutSplitCustomIconsPref() {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(".SplitSuperSelected"));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            int maxLoop;
            if (appShortcuts.size() > shortcutManager.getMaxShortcutCountPerActivity()) {
                maxLoop = shortcutManager.getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }
            SplitNamesSelected = new String[maxLoop];
            SplitNamesSelected = (String[]) appShortcuts.toArray();

            for (int i = 0; i < maxLoop; i++) {
                try {
                    String[] packgesName = readFileLine(SplitNamesSelected[i]);
                    Intent intent = new Intent(context, SplitTransparentPair.class);
                    intent.putExtra("packages", packgesName);
                    intent.putExtra("categoryName", SplitNamesSelected[i]);
                    intent.setAction("load_split_action_pair");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    switch (i) {
                        case 0:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_one);
                            break;
                        case 1:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_two);
                            break;
                        case 2:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_three);
                            break;
                        case 3:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_four);
                            break;
                        case 4:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_five);
                            break;
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.one, null);
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.one, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.two, getAppIconDrawableCustomIcon(packgesName[0]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.two, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.three, getAppIconDrawableCustomIcon(packgesName[1]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.three, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.four, null);
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.four, null);
                    }


                    Bitmap bitmap = Bitmap
                            .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
                    drawCategory.draw(new Canvas(bitmap));

                    ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, SplitNamesSelected[i].replace(".SplitSelected", ""))
                            .setShortLabel(SplitNamesSelected[i].replace(".SplitSelected", "").split("_")[0])
                            .setLongLabel(SplitNamesSelected[i].replace(".SplitSelected", "").split("_")[0])
                            .setIcon(Icon.createWithBitmap(bitmap))
                            .setIntent(intent)
                            .setRank(i)
                            .build();

                    shortcutInfos.add(shortcutInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            shortcutManager.addDynamicShortcuts(shortcutInfos);
            if (context.getSystemService(ShortcutManager.class).getDynamicShortcuts().size() == countLine(".SplitSuperSelected")) {
                Toast(context.getString(R.string.done), context.getColor(R.color.light), context.getColor(R.color.default_color_darker), Gravity.BOTTOM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void splitToDesktop(String splitName) {
        Intent differentIntent = new Intent(context, SplitTransparentPair.class);
        differentIntent.setAction("load_split_action_pair_shortcut");
        differentIntent.addCategory(Intent.CATEGORY_DEFAULT);
        differentIntent.putExtra(Intent.EXTRA_TEXT, splitName);

        String[] packages = readFileLine(splitName);

        if (customIconsEnable()) {
            LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
            loadCustomIcons.load();
        }

        Drawable forNull = context.getDrawable(R.drawable.ic_launcher);
        forNull.setAlpha(0);
        LayerDrawable drawCategory
                = (LayerDrawable) context.getDrawable(R.drawable.split_shortcuts);
        try {
            drawCategory.setDrawableByLayerId(R.id.one, getAppIconDrawableCustomIcon(packages[0]));
        } catch (Exception e) {
            drawCategory.setDrawableByLayerId(R.id.one, forNull);
        }

        try {
            drawCategory.setDrawableByLayerId(R.id.two, getAppIconDrawableCustomIcon(packages[1]));
        } catch (Exception e) {
            drawCategory.setDrawableByLayerId(R.id.two, forNull);
        }

        final Bitmap shortcutApp = Bitmap
                .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
        drawCategory.draw(new Canvas(shortcutApp));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, splitName)
                    .setShortLabel(splitName.split("_")[0])
                    .setLongLabel(splitName.split("_")[0])
                    .setIcon(Icon.createWithAdaptiveBitmap(shortcutApp))
                    .setIntent(differentIntent)
                    .build();

            context.getSystemService(ShortcutManager.class).requestPinShortcut(shortcutInfo, null);
        } else {
            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, differentIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, splitName.split("_")[0]);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcutApp);
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.sendBroadcast(addIntent);
        }
    }

    public String splitNameSelected(String splitName) {
        return splitName + ".SplitSelected";
    }

    /*Category*/
    public void addAppsShortcutCategory() {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(".categorySuperSelected"));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            int maxLoop;
            if (appShortcuts.size() > shortcutManager.getMaxShortcutCountPerActivity()) {
                maxLoop = shortcutManager.getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }
            categoryNamesSelected = new String[maxLoop];
            categoryNamesSelected = (String[]) appShortcuts.toArray();

            for (int i = 0; i < maxLoop; i++) {
                try {
                    String[] packgesName = readFileLine(categoryNamesSelected[i]);
                    Intent intent = new Intent(context, LoadFolderPopupShortcuts.class);
                    intent.putExtra("categoryName", categoryNamesSelected[i]);
                    intent.setAction("load_category_action");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    switch (i) {
                        case 0:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_one);
                            break;
                        case 1:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_two);
                            break;
                        case 2:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_three);
                            break;
                        case 3:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_four);
                            break;
                        case 4:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_five);
                            break;
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.one, getAppIconDrawableCustomIcon(packgesName[0]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.one, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.two, getAppIconDrawableCustomIcon(packgesName[1]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.two, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.three, getAppIconDrawableCustomIcon(packgesName[2]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.three, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.four, getAppIconDrawableCustomIcon(packgesName[3]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.four, null);
                    }

                    Bitmap bitmap = Bitmap
                            .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
                    drawCategory.draw(new Canvas(bitmap));

                    ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, categoryNamesSelected[i].replace(".CategorySelected", ""))
                            .setShortLabel(categoryNamesSelected[i].replace(".CategorySelected", "").split("_")[0])
                            .setLongLabel(categoryNamesSelected[i].replace(".CategorySelected", "").split("_")[0])
                            .setIcon(Icon.createWithBitmap(bitmap))
                            .setIntent(intent)
                            .setRank(i)
                            .build();

                    shortcutInfos.add(shortcutInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            shortcutManager.addDynamicShortcuts(shortcutInfos);
            if (context.getSystemService(ShortcutManager.class).getDynamicShortcuts().size() == countLine(".categorySuperSelected")) {
                Toast(context.getString(R.string.done), context.getColor(R.color.default_color_darker),
                        103,
                        true);
                Toast(context.getString(R.string.cautionShortcutsHome), context.getColor(R.color.light), context.getColor(R.color.dark), Gravity.BOTTOM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAppsShortcutCategoryCustomIconsPref() {
        try {
            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            shortcutManager.removeAllDynamicShortcuts();
            List<String> appShortcuts = Arrays.asList(readFileLine(".categorySuperSelected"));

            List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
            shortcutInfos.clear();

            if (customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
                loadCustomIcons.load();
            }

            int maxLoop;
            if (appShortcuts.size() > shortcutManager.getMaxShortcutCountPerActivity()) {
                maxLoop = shortcutManager.getMaxShortcutCountPerActivity();
            } else {
                maxLoop = appShortcuts.size();
            }
            categoryNamesSelected = new String[maxLoop];
            categoryNamesSelected = (String[]) appShortcuts.toArray();

            for (int i = 0; i < maxLoop; i++) {
                try {
                    String[] packgesName = readFileLine(categoryNamesSelected[i]);
                    Intent intent = new Intent(context, LoadFolderPopupShortcuts.class);
                    intent.putExtra("categoryName", categoryNamesSelected[i]);
                    intent.setAction("load_category_action");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    switch (i) {
                        case 0:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_one);
                            break;
                        case 1:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_two);
                            break;
                        case 2:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_three);
                            break;
                        case 3:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_four);
                            break;
                        case 4:
                            drawCategory = (LayerDrawable) context.getResources()
                                    .getDrawable(R.drawable.category_icons_five);
                            break;
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.one, getAppIconDrawableCustomIcon(packgesName[0]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.one, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.two, getAppIconDrawableCustomIcon(packgesName[1]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.two, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.three, getAppIconDrawableCustomIcon(packgesName[2]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.three, null);
                    }

                    try {
                        drawCategory.setDrawableByLayerId(R.id.four, getAppIconDrawableCustomIcon(packgesName[3]));
                    } catch (Exception e) {
                        drawCategory.setDrawableByLayerId(R.id.four, null);
                    }

                    Bitmap bitmap = Bitmap
                            .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
                    drawCategory.draw(new Canvas(bitmap));

                    ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, categoryNamesSelected[i].replace(".CategorySelected", ""))
                            .setShortLabel(categoryNamesSelected[i].replace(".CategorySelected", "").split("_")[0])
                            .setLongLabel(categoryNamesSelected[i].replace(".CategorySelected", "").split("_")[0])
                            .setIcon(Icon.createWithBitmap(bitmap))
                            .setIntent(intent)
                            .setRank(i)
                            .build();

                    shortcutInfos.add(shortcutInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            shortcutManager.addDynamicShortcuts(shortcutInfos);
            if (context.getSystemService(ShortcutManager.class).getDynamicShortcuts().size() == countLine(".categorySuperSelected")) {
                Toast(context.getString(R.string.done), context.getColor(R.color.light), context.getColor(R.color.default_color_darker), Gravity.BOTTOM, true);
                Toast(context.getString(R.string.cautionShortcutsHome), context.getColor(R.color.light), context.getColor(R.color.dark), Gravity.BOTTOM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPopupCategoryItem(Activity instanceOfActivity, RelativeLayout popupAnchorView,
                                      String categoryName, LoadCustomIcons loadCustomIcons) {

        ArrayList<AdapterItemsData> folderItemListAdapter = new ArrayList<AdapterItemsData>();
        folderItemListAdapter.clear();
        for (String packageName : readFileLine(categoryName)) {
            if (isAppInstalled(packageName)) {
                folderItemListAdapter.add(new AdapterItemsData(
                        appName(packageName),
                        packageName,
                        customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(packageName, appIconDrawable(packageName)) : appIconDrawable(packageName)
                ));
            } else {
                context.deleteFile(packageName + "." + categoryName);
                removeLine(categoryName, packageName);
            }
        }
        folderItemListAdapter.add(new AdapterItemsData(
                context.getString(R.string.edit_advanced_shortcut) + " " + categoryName.replace(".CategorySelected", "").split("_")[0],
                context.getPackageName(),
                context.getDrawable(R.drawable.draw_pref)));

        ListPopupWindow listPopupWindow = new ListPopupWindow(instanceOfActivity);

        FolderItemListAdapter lowerListAdapter = new FolderItemListAdapter(context,
                folderItemListAdapter,
                listPopupWindow);

        listPopupWindow.setAdapter(lowerListAdapter);
        listPopupWindow.setAnchorView(popupAnchorView);
        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
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

    public void categoryToDesktop(String categoryName) {
        Intent differentIntent = new Intent(context, LoadFolderPopupShortcuts.class);
        differentIntent.setAction("load_category_action_shortcut");
        differentIntent.addCategory(Intent.CATEGORY_DEFAULT);
        differentIntent.putExtra(Intent.EXTRA_TEXT, categoryName);

        String[] packages = readFileLine(categoryName);

        if (customIconsEnable()) {
            LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
            loadCustomIcons.load();
        }

        Drawable forNull = context.getDrawable(R.drawable.ic_launcher);
        forNull.setAlpha(0);
        LayerDrawable drawCategory
                = (LayerDrawable) context.getDrawable(R.drawable.category_shortcuts);
        try {
            drawCategory.setDrawableByLayerId(R.id.one, getAppIconDrawableCustomIcon(packages[0]));
        } catch (Exception e) {
            drawCategory.setDrawableByLayerId(R.id.one, forNull);
        }

        try {
            drawCategory.setDrawableByLayerId(R.id.two, getAppIconDrawableCustomIcon(packages[1]));
        } catch (Exception e) {
            drawCategory.setDrawableByLayerId(R.id.two, forNull);
        }

        try {
            drawCategory.setDrawableByLayerId(R.id.three, getAppIconDrawableCustomIcon(packages[2]));
        } catch (Exception e) {
            drawCategory.setDrawableByLayerId(R.id.three, forNull);
        }

        try {
            drawCategory.setDrawableByLayerId(R.id.four, getAppIconDrawableCustomIcon(packages[3]));
        } catch (Exception e) {
            drawCategory.setDrawableByLayerId(R.id.four, forNull);
        }

        final Bitmap shortcutApp = Bitmap
                .createBitmap(drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        drawCategory.setBounds(0, 0, drawCategory.getIntrinsicWidth(), drawCategory.getIntrinsicHeight());
        drawCategory.draw(new Canvas(shortcutApp));


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, categoryName)
                    .setShortLabel(categoryName.split("_")[0])
                    .setLongLabel(categoryName.split("_")[0])
                    .setIcon(Icon.createWithAdaptiveBitmap(shortcutApp))
                    .setIntent(differentIntent)
                    .build();

            context.getSystemService(ShortcutManager.class).requestPinShortcut(shortcutInfo, null);
        } else {
            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, differentIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, categoryName.split("_")[0]);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcutApp);
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.sendBroadcast(addIntent);
        }
    }

    public String categoryNameSelected(String categoryName) {
        return categoryName + ".CategorySelected";
    }

    /*File*/
    public void saveFileEmpty(String fileName) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedFiles() throws Exception {
        if (context.getFileStreamPath(".mixShortcuts").exists()) {
            String[] mixContent = readFileLine(".mixShortcuts");
            for (String mixLine : mixContent) {
                if (mixLine.contains(".CategorySelected")) {
                    context.deleteFile(categoryNameSelected(mixLine));
                } else if (mixLine.contains(".SplitSelected")) {
                    context.deleteFile(splitNameSelected(mixLine));
                } else {
                    context.deleteFile(packageNameSelected(mixLine));
                }
            }
            context.deleteFile(".mixShortcuts");
        }
        if (context.getFileStreamPath(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile).exists()) {
            String[] arrayContent = readFileLine(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile);
            for (String lineContent : arrayContent) {
                context.deleteFile(packageNameSelected(lineContent));
            }
            context.deleteFile(NormalAppShortcutsSelectionListPhone.NormalApplicationsShortcutsFile);
        }
        if (context.getFileStreamPath(".categorySuperSelected").exists()) {
            String[] arrayContent = readFileLine(".categorySuperSelected");
            for (String lineContent : arrayContent) {
                context.deleteFile((lineContent));
            }
            context.deleteFile(".categorySuperSelected");
        }
        if (context.getFileStreamPath(".SplitSuperSelected").exists()) {
            String[] arrayContent = readFileLine(".SplitSuperSelected");
            for (String lineContent : arrayContent) {
                context.deleteFile((lineContent));
            }
            context.deleteFile(".SplitSuperSelected");
        }
    }

    public void saveFile(String fileName, String content) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, MODE_PRIVATE);
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

    public @Nullable String[] readFileLine(String fileName) {

        return new FunctionsClassIO(context).readFileLines(fileName);
    }

    public @Nullable String readFile(String fileName) {

        return new FunctionsClassIO(context).readFile(fileName);
    }

    public int countLineInnerFile(String fileName) {

        return new FunctionsClassIO(context).fileLinesCounter(fileName);
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
        int nLines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(context.getFileStreamPath(fileName)));
            while (reader.readLine() != null) {
                nLines++;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            nLines = 0;
        }
        return nLines;
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

    public void saveDefaultPreference(String KEY, String VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putString(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void saveDefaultPreference(String KEY, boolean VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putBoolean(KEY, VALUE);
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

    /**
     * 255 is Transparent.
     */
    public int setColorAlpha(int color, float alphaValue /*1 -- 255*/) {
        int alpha = Math.round(Color.alpha(color) * alphaValue);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /*Dialogue Checkpoint Function*/
    public boolean AccessibilityServiceEnabled() {
        ComponentName expectedComponentName = new ComponentName(context, SplitScreenService.class);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    public void AccessibilityService(final Activity activity, boolean finishIt) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);
        alertDialog.setTitle(
                Html.fromHtml("<font color='" + context.getColor(R.color.default_color) + "'>" +
                        context.getString(R.string.splitTitle) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        alertDialog.setMessage(Html.fromHtml(context.getString(R.string.observeDesc), Html.FROM_HTML_MODE_LEGACY));
        alertDialog.setIcon(context.getDrawable(R.drawable.ic_launcher));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(R.string.grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (finishIt) {

                    activity.finish();
                }
            }
        });
        alertDialog.show();
    }

    public void UsageAccess(Activity instanceOfActivity, final Switch prefSwitch) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(instanceOfActivity, R.style.GeeksEmpire_Dialogue_Light);
        alertDialog.setTitle(
                Html.fromHtml("<font color='" + context.getColor(R.color.default_color) + "'>" +
                        context.getString(R.string.smartTitle) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        alertDialog.setMessage(Html.fromHtml(context.getString(R.string.smartPermission), Html.FROM_HTML_MODE_LEGACY));
        alertDialog.setIcon(context.getDrawable(R.drawable.ic_launcher));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(R.string.grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("smart", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                prefSwitch.setChecked(true);
                editor.putBoolean("smartPick", true);
                editor.apply();

                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        alertDialog.show();
    }

    public boolean UsageAccessEnabled() {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
        int mode = appOps.checkOp("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        return (mode == AppOpsManager.MODE_ALLOWED);
    }

    public void upcomingChangeLog(Activity activity, String updateInfo, String versionCode) {
        if (returnAPI() > 22) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);

            alertDialog.setTitle(Html.fromHtml("<small>" + context.getString(R.string.whatsnew) + " | " + versionCode + "</small>", Html.FROM_HTML_MODE_LEGACY));
            alertDialog.setMessage(Html.fromHtml(updateInfo, Html.FROM_HTML_MODE_LEGACY));

            LayerDrawable layerDrawableNewUpdate = (LayerDrawable) context.getDrawable(R.drawable.ic_update);
            BitmapDrawable gradientDrawableNewUpdate = (BitmapDrawable) layerDrawableNewUpdate.findDrawableByLayerId(R.id.temporaryBackground);
            gradientDrawableNewUpdate.setTint(context.getColor(R.color.default_color));

            alertDialog.setIcon(layerDrawableNewUpdate);
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton(context.getString(R.string.followIt), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.link_facebook_app)))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
            alertDialog.setNeutralButton(context.getString(R.string.installUpdate), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.play_store_link) + context.getPackageName()))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    Toast(context.getString(R.string.rate), Gravity.BOTTOM);
                }
            });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    /*Checkpoint Function*/
    public String activityLabel(ActivityInfo activityInfo) {
        String Name = context.getString(R.string.app_name);
        try {
            Name = activityInfo.loadLabel(context.getPackageManager()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Name = appName(activityInfo.packageName);
        }
        return Name;
    }

    public Drawable activityIcon(ActivityInfo activityInfo) {
        Drawable icon = null;
        try {
            icon = activityInfo.loadIcon(context.getPackageManager());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (icon == null) {
                try {
                    icon = context.getPackageManager().getDefaultActivityIcon();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return icon;
    }

    public Bitmap activityIconBitmap(ActivityInfo activityInfo) {
        Drawable icon = null;
        try {
            icon = activityInfo.loadIcon(context.getPackageManager());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (icon == null) {
                try {
                    icon = context.getPackageManager().getDefaultActivityIcon();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return drawableToBitmap(icon);
    }

    public String appPackageNameClassName(String PackageName, String ClassName) {
        return PackageName + "|" + ClassName;
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

    public boolean isAppInstalled(String packName) {
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

    public boolean canLaunch(String packageName) {

        return (context.getPackageManager().getLaunchIntentForPackage(packageName) != null);
    }

    public String appName(String packageName) {
        String Name = null;

        try {
            PackageManager packManager = context.getPackageManager();
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
            Name = packManager.getApplicationLabel(app).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Name;
    }

    public String appVersionName(String packageName) {
        String Version = "0";

        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            Version = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Version;
    }

    public int appVersionCode(String packageName) {
        int VersionCode = 0;

        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            VersionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return VersionCode;
    }

    public Drawable appIconDrawable(String packageName) {
        Drawable icon = null;
        try {
            PackageManager packManager = context.getPackageManager();
            icon = packManager.getApplicationIcon(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return icon;
    }

    public Bitmap appIconBitmap(String packageName) {
        Bitmap bitmap = null;
        try {
            Drawable drawableIcon = context.getPackageManager().getApplicationIcon(packageName);
            if (drawableIcon instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawableIcon).getBitmap();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (drawableIcon instanceof AdaptiveIconDrawable) {
                    Drawable backgroundDrawable = ((AdaptiveIconDrawable) drawableIcon).getBackground();
                    Drawable foregroundDrawable = ((AdaptiveIconDrawable) drawableIcon).getForeground();

                    Drawable[] drawables = new Drawable[2];
                    drawables[0] = backgroundDrawable;
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
            } else {
                bitmap = Bitmap.createBitmap(drawableIcon.getIntrinsicWidth(), drawableIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof VectorDrawable) {
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                bitmap = bitmapDrawable.getBitmap();
            }
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;

            bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth(), layerDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            layerDrawable.draw(canvas);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public Bitmap appIconBitmap(Drawable iconDrawable) {
        Bitmap bitmap = null;
        try {
            Drawable drawableIcon = iconDrawable;
            if (drawableIcon instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawableIcon).getBitmap();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (drawableIcon instanceof AdaptiveIconDrawable) {
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
            } else {
                bitmap = Bitmap.createBitmap(drawableIcon.getIntrinsicWidth(), drawableIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public void openApplication(String packageName) {
        if (isAppInstalled(packageName)) {
            try {
                Toast.makeText(context,
                        appName(packageName), Toast.LENGTH_SHORT).show();

                Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
                if (i != null) {
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getString(R.string.not_install), Toast.LENGTH_LONG).show();
                Intent playStore = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.play_store_link) + packageName));
                playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(playStore);
            }
        } else {
            Toast.makeText(context, context.getString(R.string.not_install), Toast.LENGTH_LONG).show();
            Intent playStore = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.play_store_link) + packageName));
            playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(playStore);
        }
    }

    public void goToSettingInfo(String packageName) {
        Intent goToSettingInfo = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        goToSettingInfo.addCategory(Intent.CATEGORY_DEFAULT);
        goToSettingInfo.setData(Uri.parse("package:" + packageName));
        goToSettingInfo.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        goToSettingInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(goToSettingInfo);
    }

    public void overrideBackPress(Activity instanceOfActivity, Class returnClass) throws Exception {
        context.startActivity(new Intent(context, returnClass)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY));

        instanceOfActivity.finish();
    }

    public void overrideBackPress(Activity instanceOfActivity, Class returnClass, ActivityOptions activityOptions) throws Exception {
        context.startActivity(new Intent(context, returnClass)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY), activityOptions.toBundle());

        instanceOfActivity.finish();
    }

    public void navigateToClass(Activity instanceOfActivity, Class returnClass, ActivityOptions activityOptions) {
        Intent intentOverride = new Intent(context, returnClass);
        instanceOfActivity.startActivity(intentOverride, activityOptions.toBundle());
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
        return API;
    }

    public Drawable getActivityIcon(String packageName, String activityName) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityName));
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);

        return resolveInfo.loadIcon(packageManager);
    }

    public Drawable getActivityIcon(Intent intent) {
        Drawable drawable = null;
        try {
            drawable = context.getPackageManager().getActivityIcon(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    public boolean mixShortcuts() {

        return context.getSharedPreferences("mix", MODE_PRIVATE).getBoolean("mixShortcuts", false);
    }

    public int getSystemMaxAppShortcut() {

        return context.getSystemService(ShortcutManager.class).getMaxShortcutCountPerActivity();
    }

    public void notificationCreator(String titleText, String contentText, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setContentTitle(Html.fromHtml("<b><font color='" + context.getColor(R.color.default_color) + "'>" + titleText + "</font></b>", Html.FROM_HTML_MODE_LEGACY));
        mBuilder.setContentText(Html.fromHtml("<font color='" + context.getColor(R.color.default_color) + "'>" + contentText + "</font>", Html.FROM_HTML_MODE_LEGACY));
        mBuilder.setTicker(context.getString(R.string.app_name));
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setAutoCancel(true);
        mBuilder.setColor(context.getColor(R.color.default_color));
        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        Intent newUpdate = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.play_store_link) + context.getPackageName()));
        PendingIntent newUpdatePendingIntent = PendingIntent.getActivity(context, 5, newUpdate, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(context.getPackageName(), context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            mBuilder.setChannelId(context.getPackageName());
        }


        Notification.Action.Builder builderActionNotification = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.draw_share_menu),
                context.getString(R.string.rate),
                newUpdatePendingIntent
        );
        mBuilder.addAction(builderActionNotification.build());
        mBuilder.setContentIntent(newUpdatePendingIntent);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public boolean SettingServiceRunning(Class aClass) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (aClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*UI*/
    public void Toast(String toastContent, int toastColor,
                      int toastTopOffset,
                      boolean showToast) {

        Toast toast = new Toast(context);
        if (showToast) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.toast_view, null);

            LayerDrawable drawToast = (LayerDrawable) context.getDrawable(R.drawable.toast_background);
            GradientDrawable backToast = (GradientDrawable) drawToast.findDrawableByLayerId(R.id.temporaryBackground);
            backToast.setColor(toastColor);

            TextView textView = (TextView) layout.findViewById(R.id.toastText);
            textView.setText(Html.fromHtml("<small>" + toastContent + "</small>", Html.FROM_HTML_MODE_LEGACY));
            textView.setBackground(drawToast);
            textView.setShadowLayer(0.02f, 2, 2, context.getColor(R.color.dark_transparent_high));

            int yOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toastTopOffset, context.getResources().getDisplayMetrics());

            toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, yOffset);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } else if (!showToast) {
            toast.cancel();
        }
    }

    public void Toast(String toastContent, int toastColor, int textColor, int toastGravity, boolean showToast) {
        try {
            Toast toast = new Toast(context);
            if (showToast == true) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.toast_view, null);

                LayerDrawable drawToast = (LayerDrawable) context.getDrawable(R.drawable.toast_background);
                GradientDrawable backToast = (GradientDrawable) drawToast.findDrawableByLayerId(R.id.temporaryBackground);
                backToast.setColor(toastColor);

                TextView textView = (TextView) layout.findViewById(R.id.toastText);
                textView.setText(Html.fromHtml("<small>" + toastContent + "</small>", Html.FROM_HTML_MODE_LEGACY));
                textView.setBackground(drawToast);
                textView.setShadowLayer(0.02f, 2, 2, context.getColor(R.color.dark_transparent_high));
                textView.setTextColor(textColor);

                int yOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 57, context.getResources().getDisplayMetrics());

                toast.setGravity(Gravity.FILL_HORIZONTAL | toastGravity, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            } else if (showToast == false) {
                toast.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Toast(String toastContent, int gravity) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.toast_view, null/*(ViewGroup) activity.findViewById(R.id.toastView)*/);

            LayerDrawable drawToast = null;
            drawToast = (LayerDrawable) context.getDrawable(R.drawable.toast_background);
            GradientDrawable backToast = (GradientDrawable) drawToast.findDrawableByLayerId(R.id.temporaryBackground);

            TextView textView = (TextView) layout.findViewById(R.id.toastText);
            textView.setText(Html.fromHtml("<small>" + toastContent + "</small>", Html.FROM_HTML_MODE_LEGACY));
            backToast.setColor(context.getColor(R.color.dark_transparent));
            textView.setBackground(drawToast);
            textView.setTextColor(context.getColor(R.color.light));
            textView.setShadowLayer(0.02f, 2, 2, context.getColor(R.color.light_transparent_high));

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.FILL_HORIZONTAL | gravity, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int DpToInteger(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /*Custom Icons*/
    public String customIconPackageName() {
        //com.Fraom.Smugy
        return readDefaultPreference("customIcon", context.getPackageName());
    }

    public boolean customIconsEnable() {
        boolean doLoadCustomIcon = false;
        if (customIconPackageName().equals(context.getPackageName())) {
            doLoadCustomIcon = false;
        } else if (!customIconPackageName().equals(context.getPackageName())) {
            doLoadCustomIcon = true;
        }
        return doLoadCustomIcon;
    }

    public Drawable getAppIconDrawableCustomIcon(String packageName) {
        LoadCustomIcons loadCustomIcons = null;
        if (customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
            loadCustomIcons.load();
        }
        return customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(packageName, appIconDrawable(packageName)) : appIconDrawable(packageName);
    }

    public Bitmap getAppIconBitmapCustomIcon(String packageName) {
        LoadCustomIcons loadCustomIcons = null;
        if (customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
            loadCustomIcons.load();
        }
        return customIconsEnable() ? loadCustomIcons.getIconForPackage(packageName, appIconBitmap(packageName)) : appIconBitmap(packageName);
    }

    public Bitmap getAppIconBitmapCustomIcon(ActivityInfo activityInfo) {
        LoadCustomIcons loadCustomIcons = null;
        if (customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
            loadCustomIcons.load();
        }
        return customIconsEnable() ? loadCustomIcons.getIconForPackage(activityInfo.packageName, (activityIconBitmap(activityInfo))) : (activityIconBitmap(activityInfo));
    }

    /*AppShortcuts Mode*/
    public String AppShortcutsMode() {
        //AppShortcuts
        //SplitShortcuts
        //CategoryShortcuts
        return readPreference(".PopupShortcut", "PopupShortcutMode", "AppShortcuts");
    }

    /*Firebase Remote Config*/
    public void isFirstToCheckTutorial(Boolean firstTimeTutorial) {
        savePreference(".Tutorial", "Checked", firstTimeTutorial);
    }

    public Boolean isFirstToCheckTutorial() {
        return readPreference(".Tutorial", "Checked", false);
    }

    public boolean joinedBetaProgram() {
        return readDefaultPreference("JoinedBetaProgrammer", false);
    }

    public String versionCodeRemoteConfigKey() {
        String versionCodeKey = null;
        if (joinedBetaProgram()) {
            versionCodeKey = context.getString(R.string.BETAintegerVersionCodeNewUpdatePhone);
        } else {
            versionCodeKey = context.getString(R.string.integerVersionCodeNewUpdatePhone);
        }
        return versionCodeKey;
    }

    public String versionNameRemoteConfigKey() {
        String versionCodeKey = null;
        if (joinedBetaProgram()) {
            versionCodeKey = context.getString(R.string.BETAstringVersionNameNewUpdatePhone);
        } else {
            versionCodeKey = context.getString(R.string.stringVersionNameNewUpdatePhone);
        }
        return versionCodeKey;
    }

    public String upcomingChangeLogRemoteConfigKey() {
        String versionCodeKey = null;
        if (joinedBetaProgram()) {
            versionCodeKey = context.getString(R.string.BETAstringUpcomingChangeLogPhone);
        } else {
            versionCodeKey = context.getString(R.string.stringUpcomingChangeLogPhone);
        }
        return versionCodeKey;
    }

    public String upcomingChangeLogSummaryConfigKey() {
        String versionCodeKey = null;
        if (joinedBetaProgram()) {
            versionCodeKey = context.getString(R.string.BETAstringUpcomingChangeLogSummaryPhone);
        } else {
            versionCodeKey = context.getString(R.string.stringUpcomingChangeLogSummaryPhone);
        }
        return versionCodeKey;
    }

    /*In-App Purchase*/
    public boolean mixShortcutsPurchased() {

        return BuildConfig.DEBUG ? false :
                readPreference(".PurchasedItem", "mix.shortcuts", false);
    }

    public boolean alreadyDonated() {

        return readPreference(".PurchasedItem", "donation", false);
    }
}