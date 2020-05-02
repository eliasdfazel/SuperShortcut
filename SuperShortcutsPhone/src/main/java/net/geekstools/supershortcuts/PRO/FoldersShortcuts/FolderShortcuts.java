/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 12:26 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionList;
import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Adapters.FolderShortcutsAdapter;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint;
import net.geekstools.supershortcuts.PRO.Utils.SimpleGestureFilterSwitch;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.databinding.FolderShortcutsViewBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FolderShortcuts extends AppCompatActivity implements SimpleGestureFilterSwitch.SimpleGestureListener {

    FunctionsClass functionsClass;

    RecyclerView.Adapter advanceShortcutsAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    String[] appData;
    ArrayList<AdapterItemsData> navDrawerItems;

    int limitCounter;
    boolean resetAdapter = false;

    SimpleGestureFilterSwitch simpleGestureFilterSwitch;

    LoadCustomIcons loadCustomIcons;

    FirebaseRemoteConfig firebaseRemoteConfig;

    FolderShortcutsViewBinding folderShortcutsViewBinding;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        folderShortcutsViewBinding = FolderShortcutsViewBinding.inflate(getLayoutInflater());
        setContentView(folderShortcutsViewBinding.getRoot());

        simpleGestureFilterSwitch = new SimpleGestureFilterSwitch(getApplicationContext(), this);
        functionsClass = new FunctionsClass(getApplicationContext());

        new FunctionsClassDialogues(FolderShortcuts.this, functionsClass).changeLog();

        if (functionsClass.mixShortcuts() == true) {
            PublicVariable.advanceShortcutsMaxAppShortcuts
                    = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
            getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.advanceShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            limitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".categorySuperSelected");
            getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + limitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
            PublicVariable.advanceShortcutsMaxAppShortcuts = functionsClass.getSystemMaxAppShortcut();
        }

        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        folderShortcutsViewBinding.categoryList.setLayoutManager(recyclerViewLayoutManager);

        folderShortcutsViewBinding.MainView.setBackgroundColor(getColor(R.color.light));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color)));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + getString(R.string.app_name) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.default_color));
        window.setNavigationBarColor(getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        navDrawerItems = new ArrayList<AdapterItemsData>();

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        folderShortcutsViewBinding.loadingDescription.setTypeface(face);
        folderShortcutsViewBinding.appSelectedCounterView.setTypeface(face);
        folderShortcutsViewBinding.appSelectedCounterView.bringToFront();

        folderShortcutsViewBinding.loadingProgress.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.counterActionAdvanceShortcuts));
        intentFilter.addAction(getString(R.string.checkboxActionAdvanceShortcuts));
        intentFilter.addAction(getString(R.string.dynamicShortcutsAdvance));
        BroadcastReceiver counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.counterActionAdvanceShortcuts))) {

                    folderShortcutsViewBinding.appSelectedCounterView.setText(String.valueOf(functionsClass.countLine(".categorySuperSelected")));

                } else if ((intent.getAction().equals(getString(R.string.checkboxActionAdvanceShortcuts)))) {

                    resetAdapter = true;
                    loadCategoryData();

                } else if (intent.getAction().equals(getString(R.string.dynamicShortcutsAdvance))) {

                    if (functionsClass.mixShortcuts()) {

                        PublicVariable.advanceShortcutsMaxAppShortcuts
                                = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
                        getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.advanceShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));

                    } else {

                        limitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".categorySuperSelected");
                        getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + limitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
                        PublicVariable.advanceShortcutsMaxAppShortcuts = functionsClass.getSystemMaxAppShortcut();

                    }

                }
            }
        };
        registerReceiver(counterReceiver, intentFilter);


        folderShortcutsViewBinding.autoApps.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.default_color_darker)));
        folderShortcutsViewBinding.autoSplit.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.default_color_darker)));
        folderShortcutsViewBinding.autoCategories.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.default_color)));

        loadCategoryData();

        //In-App Billing
        new PurchasesCheckpoint(FolderShortcuts.this).trigger();
    }

    @Override
    public void onStart() {
        super.onStart();

        folderShortcutsViewBinding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (functionsClass.mixShortcuts() == true) {
                    functionsClass.addMixAppShortcuts();
                } else {
                    functionsClass.addAppsShortcutCategory();
                    SharedPreferences sharedPreferences = getSharedPreferences(".PopupShortcut", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("PopupShortcutMode", "CategoryShortcuts");
                    editor.apply();
                }
            }
        });

        folderShortcutsViewBinding.confirmButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    functionsClass.deleteSelectedFiles();

                    sendBroadcast(new Intent(getString(R.string.checkboxActionAdvanceShortcuts)));
                    sendBroadcast(new Intent(getString(R.string.counterActionAdvanceShortcuts)));
                    sendBroadcast(new Intent(getString(R.string.dynamicShortcutsAdvance)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                functionsClass.clearDynamicShortcuts();
                return true;
            }
        });

        folderShortcutsViewBinding.autoApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(FolderShortcuts.this, NormalAppShortcutsSelectionList.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        folderShortcutsViewBinding.autoSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(FolderShortcuts.this, SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(FolderShortcuts.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                        LayerDrawable layerDrawableNewUpdate = (LayerDrawable) getDrawable(R.drawable.ic_update);
                                        BitmapDrawable gradientDrawableNewUpdate = (BitmapDrawable) layerDrawableNewUpdate.findDrawableByLayerId(R.id.temporaryBackground);
                                        gradientDrawableNewUpdate.setTint(getColor(R.color.default_color_light));

                                        Bitmap tempBitmap = functionsClass.drawableToBitmap(layerDrawableNewUpdate);
                                        Bitmap scaleBitmap = Bitmap.createScaledBitmap(tempBitmap, tempBitmap.getWidth() / 4, tempBitmap.getHeight() / 4, false);
                                        Drawable logoDrawable = new BitmapDrawable(getResources(), scaleBitmap);
                                        getSupportActionBar().setHomeAsUpIndicator(logoDrawable);

                                        functionsClass.notificationCreator(
                                                getString(R.string.updateAvailable),
                                                firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                                (int) firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey())
                                        );
                                    } else {

                                    }
                                }
                            });
                        } else {

                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TabsView", FolderShortcuts.class.getSimpleName());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (functionsClass.UsageAccessEnabled()) {
            finish();
        } else {
            Intent homeScreen = new Intent(Intent.ACTION_MAIN);
            homeScreen.addCategory(Intent.CATEGORY_HOME);
            homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreen);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.simpleGestureFilterSwitch.onTouchEvent(motionEvent);

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilterSwitch.SWIPE_RIGHT:
                FunctionsClassDebug.Companion.PrintDebug("Swipe Right");
                try {
                    functionsClass.overrideBackPress(FolderShortcuts.this, SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void loadCategoryData() {
        loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());

        LoadInstalledCustomIcons loadInstalledCustomIcons = new LoadInstalledCustomIcons();
        loadInstalledCustomIcons.execute();

        if (functionsClass.UsageAccessEnabled()) {
            folderShortcutsViewBinding.loadingProgress.setVisibility(View.INVISIBLE);
            folderShortcutsViewBinding.loadingLogo.setImageDrawable(getDrawable(R.drawable.draw_smart));
            folderShortcutsViewBinding.loadingDescription.setText(Html.fromHtml(getString(R.string.smartInfo), Html.FROM_HTML_MODE_LEGACY));

            try {
                functionsClass.deleteSelectedFiles();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                retrieveFreqUsedApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                navDrawerItems.clear();

                if (!functionsClass.mixShortcuts()) {
                    if (getFileStreamPath(".mixShortcuts").exists()) {
                        String[] mixContent = functionsClass.readFileLine(".mixShortcuts");
                        for (String mixLine : mixContent) {
                            if (mixLine.contains(".CategorySelected")) {
                                deleteFile(functionsClass.categoryNameSelected(mixLine));
                            } else if (mixLine.contains(".SplitSelected")) {
                                deleteFile(functionsClass.splitNameSelected(mixLine));
                            } else {
                                deleteFile(functionsClass.packageNameSelected(mixLine));
                            }
                        }
                        deleteFile(".mixShortcuts");
                    }
                }
                if (getFileStreamPath(".superFreq").exists()) {
                    deleteFile(".superFreq");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoadApplicationsOff loadApplicationsOff = new LoadApplicationsOff();
            loadApplicationsOff.execute();
        }
    }

    public List<String> letMeKnow(Activity activity, int maxValue, long startTime /*‪86400000‬ = 1 days*/, long endTime  /*System.currentTimeMillis()*/) {
        /*‪86400000 = 24h --- 82800000 = 23h‬*/
        List<String> freqApps = new ArrayList<String>();
        try {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> queryUsageStats = mUsageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                            System.currentTimeMillis() - startTime,
                            endTime);
            Collections.sort(
                    queryUsageStats,
                    new Comparator<UsageStats>() {
                        @Override
                        public int compare(UsageStats left, UsageStats right) {
                            return Long.compare(
                                    right.getTotalTimeInForeground(), left.getTotalTimeInForeground()
                            );
                        }
                    }
            );
            for (int i = 0; i < maxValue; i++) {
                String aPackageName = queryUsageStats.get(i).getPackageName();
                try {
                    if (!aPackageName.equals(getPackageName())) {
                        if (functionsClass.isAppInstalled(aPackageName)) {
                            if (!functionsClass.ifSystem(aPackageName)) {
                                if (!functionsClass.ifDefaultLauncher(aPackageName)) {
                                    if (functionsClass.canLaunch(aPackageName)) {
                                        freqApps.add(aPackageName);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Set<String> stringHashSet = new LinkedHashSet<>(freqApps);
            freqApps.clear();
            freqApps.addAll(stringHashSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return freqApps;
    }

    public void retrieveFreqUsedApp() throws Exception {
        List<String> freqApps = letMeKnow(FolderShortcuts.this, 25, (86400000 * 7), System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            functionsClass.saveFileAppendLine(
                    ".superFreq",
                    freqApps.get(i));
        }
        functionsClass.addAppShortcutsFreqApps();
    }

    public class LoadApplicationsOff extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!getFileStreamPath(".categorySuper").exists()) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                folderShortcutsViewBinding.loadingSplash.setVisibility(View.INVISIBLE);
                folderShortcutsViewBinding.loadingSplash.startAnimation(animation);
            }

            if (!PublicVariable.firstLoad) {
                folderShortcutsViewBinding.autoSelect.setVisibility(View.VISIBLE);
            }

            try {
                folderShortcutsViewBinding.categoryList.getRecycledViewPool().clear();
                navDrawerItems.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!getFileStreamPath(".categorySuper").exists()) {
                navDrawerItems = new ArrayList<AdapterItemsData>();
                navDrawerItems.add(new AdapterItemsData(getPackageName(), new String[]{getPackageName()}));
                advanceShortcutsAdapter = new FolderShortcutsAdapter(FolderShortcuts.this, getApplicationContext(), navDrawerItems);
            } else {
                try {
                    if (functionsClass.customIconsEnable()) {
                        loadCustomIcons.load();
                        if (BuildConfig.DEBUG) {
                            FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                        }
                    }

                    appData = functionsClass.readFileLine(".categorySuper");
                    navDrawerItems = new ArrayList<AdapterItemsData>();
                    for (int navItem = 0; navItem < appData.length; navItem++) {
                        try {
                            navDrawerItems.add(new AdapterItemsData(
                                    appData[navItem],
                                    functionsClass.readFileLine(appData[navItem])));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    navDrawerItems.add(new AdapterItemsData(getPackageName(), new String[]{getPackageName()}));
                    advanceShortcutsAdapter = new FolderShortcutsAdapter(FolderShortcuts.this, getApplicationContext(), navDrawerItems);
                    advanceShortcutsAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    this.cancel(true);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            folderShortcutsViewBinding.categoryList.setAdapter(advanceShortcutsAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Animation slideDown_select = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_button);
                    if (PublicVariable.firstLoad) {
                        PublicVariable.firstLoad = false;

                        folderShortcutsViewBinding.autoSelect.startAnimation(slideDown_select);
                        slideDown_select.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                folderShortcutsViewBinding.autoSelect.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    folderShortcutsViewBinding.loadingSplash.setVisibility(View.INVISIBLE);

                    if (resetAdapter == false) {
                        folderShortcutsViewBinding.loadingSplash.startAnimation(anim);
                    }
                    folderShortcutsViewBinding.confirmButton.setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                    folderShortcutsViewBinding.appSelectedCounterView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            folderShortcutsViewBinding.appSelectedCounterView.setText(String.valueOf(functionsClass.countLine(".categorySuperSelected")));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            folderShortcutsViewBinding.appSelectedCounterView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    PublicVariable.advanceShortcutsMaxAppShortcutsCounter = functionsClass.countLine(".categorySuperSelected");
                    resetAdapter = false;
                }
            }, 250);
        }
    }

    private class LoadInstalledCustomIcons extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PackageManager packageManager = getApplicationContext().getPackageManager();

                //ACTION: com.novalauncher.THEME
                //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
                Intent intentCustomIcons = new Intent();
                intentCustomIcons.setAction("com.novalauncher.THEME");
                intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER");
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0);
                try {
                    PublicVariable.customIconsPackages.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (BuildConfig.DEBUG) {
                        FunctionsClassDebug.Companion.PrintDebug("*** CustomIconPackages ::: " + resolveInfo.activityInfo.packageName);
                    }
                    PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName);
                }

            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
        }
    }
}
