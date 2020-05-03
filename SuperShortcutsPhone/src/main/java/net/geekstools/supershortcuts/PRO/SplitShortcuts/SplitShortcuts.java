/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/3/20 10:00 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts;

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
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionList;
import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.Adapters.SplitShortcutsAdapter;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.databinding.SplitShortcutsViewBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SplitShortcuts extends AppCompatActivity {

    FunctionsClass functionsClass;

    RecyclerView.Adapter splitSelectionListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;


    ArrayList<AdapterItemsData> createdSplitListItem;

    int appShortcutLimitCounter;

    boolean resetAdapter = false;



    LoadCustomIcons loadCustomIcons;



    SplitShortcutsViewBinding splitShortcutsViewBinding;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        splitShortcutsViewBinding = SplitShortcutsViewBinding.inflate(getLayoutInflater());
        setContentView(splitShortcutsViewBinding.getRoot());

        functionsClass = new FunctionsClass(getApplicationContext());

        new FunctionsClassDialogues(SplitShortcuts.this, functionsClass).changeLog();

//        if (functionsClass.mixShortcuts() == true) {
//            PublicVariable.SplitShortcutsMaxAppShortcuts
//                    = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
//            getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.SplitShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
//        } else {
//            limitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".SplitSuperSelected");
//            getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + limitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
//            PublicVariable.SplitShortcutsMaxAppShortcuts = functionsClass.getSystemMaxAppShortcut();
//        }

        splitShortcutsViewBinding.confirmLayout.bringToFront();

        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        splitShortcutsViewBinding.recyclerViewList.setLayoutManager(recyclerViewLayoutManager);

        splitShortcutsViewBinding.MainView.setBackgroundColor(getColor(R.color.light));
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color)));
//        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + getString(R.string.app_name) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.default_color));
        window.setNavigationBarColor(getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        createdSplitListItem = new ArrayList<AdapterItemsData>();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        splitShortcutsViewBinding.loadingDescription.setTypeface(typeface);
        splitShortcutsViewBinding.selectedShortcutCounterView.setTypeface(typeface);
        splitShortcutsViewBinding.selectedShortcutCounterView.bringToFront();

        splitShortcutsViewBinding.loadingProgress.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.counterActionSplitShortcuts));
        intentFilter.addAction(getString(R.string.checkboxActionSplitShortcuts));
        intentFilter.addAction(getString(R.string.dynamicShortcutsSplit));
        BroadcastReceiver counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(getString(R.string.counterActionSplitShortcuts))) {
                    splitShortcutsViewBinding.selectedShortcutCounterView.setText(String.valueOf(functionsClass.countLine(".SplitSuperSelected")));
                } else if ((intent.getAction().equals(getString(R.string.checkboxActionSplitShortcuts)))) {
                    resetAdapter = true;
                    loadSplitData();
                } else if (intent.getAction().equals(getString(R.string.dynamicShortcutsSplit))) {
                    if (functionsClass.mixShortcuts() == true) {
                        PublicVariable.SplitShortcutsMaxAppShortcuts
                                = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
//                        getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.SplitShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        appShortcutLimitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".SplitSuperSelected");
//                        getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + limitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
                    }
                }
            }
        };
        registerReceiver(counterReceiver, intentFilter);



        loadSplitData();


        //In-App Billing
        new PurchasesCheckpoint(SplitShortcuts.this).trigger();
    }

    @Override
    public void onStart() {
        super.onStart();

        splitShortcutsViewBinding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (functionsClass.mixShortcuts() == true) {
                    functionsClass.addMixAppShortcuts();
                } else {
                    functionsClass.addAppsShortcutSplit();
                    SharedPreferences sharedPreferences = getSharedPreferences(".PopupShortcut", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("PopupShortcutMode", "SplitShortcuts");
                    editor.apply();
                }
            }
        });

        splitShortcutsViewBinding.confirmButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    functionsClass.deleteSelectedFiles();

                    sendBroadcast(new Intent(getString(R.string.checkboxActionSplitShortcuts)));
                    sendBroadcast(new Intent(getString(R.string.counterActionSplitShortcuts)));
                    sendBroadcast(new Intent(getString(R.string.dynamicShortcutsSplit)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                functionsClass.clearDynamicShortcuts();
                return true;
            }
        });

        splitShortcutsViewBinding.autoApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(SplitShortcuts.this, NormalAppShortcutsSelectionList.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        splitShortcutsViewBinding.autoCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(SplitShortcuts.this, FolderShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }






    public void loadSplitData() {
        loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());

        LoadInstalledCustomIcons loadInstalledCustomIcons = new LoadInstalledCustomIcons();
        loadInstalledCustomIcons.execute();

        if (functionsClass.UsageAccessEnabled()) {
            splitShortcutsViewBinding.loadingProgress.setVisibility(View.INVISIBLE);
            splitShortcutsViewBinding.loadingLogo.setImageDrawable(getDrawable(R.drawable.draw_smart));
            splitShortcutsViewBinding.loadingDescription.setText(Html.fromHtml(getString(R.string.smartInfo), Html.FROM_HTML_MODE_LEGACY));

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
                createdSplitListItem.clear();

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
        List<String> freqApps = letMeKnow(SplitShortcuts.this, 25, (86400000 * 7), System.currentTimeMillis());
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
            if (!getFileStreamPath(".SplitSuper").exists()) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                splitShortcutsViewBinding.loadingSplash.setVisibility(View.INVISIBLE);
                splitShortcutsViewBinding.loadingSplash.startAnimation(anim);
            }

            if (!PublicVariable.firstLoad) {
                splitShortcutsViewBinding.autoSelect.setVisibility(View.VISIBLE);
            }

            try {
                splitShortcutsViewBinding.recyclerViewList.getRecycledViewPool().clear();
                createdSplitListItem.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!getFileStreamPath(".SplitSuper").exists()) {
                createdSplitListItem = new ArrayList<AdapterItemsData>();
                createdSplitListItem.add(new AdapterItemsData(getPackageName(), new String[]{getPackageName()}));
                splitSelectionListAdapter = new SplitShortcutsAdapter(SplitShortcuts.this, getApplicationContext(), createdSplitListItem);
            } else {
                try {
                    if (functionsClass.customIconsEnable()) {
                        loadCustomIcons.load();
                        if (BuildConfig.DEBUG) {
                            FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                        }
                    }

                    String[] splitListItem = functionsClass.readFileLine(".SplitSuper");
                    createdSplitListItem = new ArrayList<AdapterItemsData>();
                    for (int navItem = 0; navItem < splitListItem.length; navItem++) {
                        try {
                            createdSplitListItem.add(new AdapterItemsData(
                                    splitListItem[navItem],
                                    functionsClass.readFileLine(splitListItem[navItem])));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    createdSplitListItem.add(new AdapterItemsData(getPackageName(), new String[]{getPackageName()}));
                    splitSelectionListAdapter = new SplitShortcutsAdapter(SplitShortcuts.this, getApplicationContext(), createdSplitListItem);
                    splitSelectionListAdapter.notifyDataSetChanged();
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
            splitShortcutsViewBinding.recyclerViewList.setAdapter(splitSelectionListAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Animation slideDown_select = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_button);
                    if (PublicVariable.firstLoad) {
                        PublicVariable.firstLoad = false;

                        splitShortcutsViewBinding.autoSelect.startAnimation(slideDown_select);
                        slideDown_select.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                splitShortcutsViewBinding.autoSelect.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    splitShortcutsViewBinding.loadingSplash.setVisibility(View.INVISIBLE);

                    if (!resetAdapter) {
                        splitShortcutsViewBinding.loadingSplash.startAnimation(anim);
                    }
                    splitShortcutsViewBinding.confirmButton.setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                    splitShortcutsViewBinding.selectedShortcutCounterView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            splitShortcutsViewBinding.selectedShortcutCounterView.setText(String.valueOf(functionsClass.countLine(".SplitSuperSelected")));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            splitShortcutsViewBinding.selectedShortcutCounterView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    PublicVariable.SplitShortcutsMaxAppShortcutsCounter = functionsClass.countLine(".SplitSuperSelected");
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
