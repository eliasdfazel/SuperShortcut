/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 9:57 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts.ApplicationsSelectionProcess;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;

import net.geekstools.supershortcuts.PRO.FoldersShortcuts.ApplicationsSelectionProcess.Adapters.FolderSavedListAdapter;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.ApplicationsSelectionProcess.Adapters.FolderSelectionListAdapter;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.databinding.FolderAppsSelectionViewBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FolderAppSelectionList extends AppCompatActivity implements View.OnClickListener {

    AppCompatActivity activity;
    Context context;
    FunctionsClass functionsClass;

    LinearLayoutManager recyclerViewLayoutManager;

    ListPopupWindow listPopupWindow;

    ArrayList<String> listOfNewCharOfItemsForIndex;

    ArrayList<AdapterItemsData> navDrawerItems, navDrawerItemsSaved;
    FolderSelectionListAdapter folderSelectionListAdapter;
    FolderSavedListAdapter folderSavedListAdapter;

    String appPackageName;
    String appName = "Application";
    Drawable appIcon;

    boolean resetAdapter = false;

    LoadCustomIcons loadCustomIcons;

    FolderAppsSelectionViewBinding folderAppsSelectionViewBinding;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        folderAppsSelectionViewBinding = FolderAppsSelectionViewBinding.inflate(getLayoutInflater());
        setContentView(folderAppsSelectionViewBinding.getRoot());

        functionsClass = new FunctionsClass(getApplicationContext());

        PublicVariable.advMaxAppShortcuts = getPackageManager()
                .queryIntentActivities(new Intent()
                        .setAction(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_LAUNCHER), 0).size();

        context = getApplicationContext();
        activity = this;

        listPopupWindow = new ListPopupWindow(activity);

        folderAppsSelectionViewBinding.temporaryFallingIcon.bringToFront();
        folderAppsSelectionViewBinding.confirmLayout.bringToFront();

        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        folderAppsSelectionViewBinding.recyclerViewList.setLayoutManager(recyclerViewLayoutManager);

        folderAppsSelectionViewBinding.nestedScrollView.setSmoothScrollingEnabled(true);

        folderAppsSelectionViewBinding.MainView.setBackgroundColor(getColor(R.color.light));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color_darker)));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>"
                + PublicVariable.categoryName.split("_")[0] + "</font>", Html.FROM_HTML_MODE_LEGACY));
        getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.advMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.default_color_darker));
        window.setNavigationBarColor(getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        navDrawerItems = new ArrayList<AdapterItemsData>();
        navDrawerItemsSaved = new ArrayList<AdapterItemsData>();
        listOfNewCharOfItemsForIndex = new ArrayList<String>();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        folderAppsSelectionViewBinding.loadingDescription.setTypeface(typeface);
        folderAppsSelectionViewBinding.loadingDescription.setText(PublicVariable.categoryName.split("_")[0]);
        folderAppsSelectionViewBinding.selectedShortcutCounterView.setTypeface(typeface);
        folderAppsSelectionViewBinding.selectedShortcutCounterView.bringToFront();

        folderAppsSelectionViewBinding.loadingProgress.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.counterActionAdvance));
        intentFilter.addAction(context.getString(R.string.savedActionAdvance));
        intentFilter.addAction(context.getString(R.string.savedActionHideAdvance));
        intentFilter.addAction(context.getString(R.string.checkboxActionAdvance));
        BroadcastReceiver counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.counterActionAdvance))) {
                    folderAppsSelectionViewBinding.selectedShortcutCounterView.setText(String.valueOf(functionsClass.countLine(PublicVariable.categoryName)));
                } else if (intent.getAction().equals(context.getString(R.string.savedActionAdvance))) {
                    if (getFileStreamPath(PublicVariable.categoryName).exists() && functionsClass.countLine(PublicVariable.categoryName) > 0) {
                        navDrawerItemsSaved.clear();
                        String[] savedLine = functionsClass.readFileLine(PublicVariable.categoryName);
                        for (String aSavedLine : savedLine) {
                            navDrawerItemsSaved.add(new AdapterItemsData(
                                    functionsClass.appName(aSavedLine),
                                    aSavedLine,
                                    functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClass.appIconDrawable(aSavedLine)) : functionsClass.appIconDrawable(aSavedLine)
                            ));
                        }
                        folderSavedListAdapter = new FolderSavedListAdapter(activity, context, navDrawerItemsSaved);
                        listPopupWindow = new ListPopupWindow(activity);
                        listPopupWindow.setAdapter(folderSavedListAdapter);
                        listPopupWindow.setAnchorView(folderAppsSelectionViewBinding.popupAnchorView);
                        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setModal(true);
                        listPopupWindow.setBackgroundDrawable(null);
                        try {
                            listPopupWindow.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                sendBroadcast(new Intent(getString(R.string.visibilityActionAdvance)));
                            }
                        });
                    }
                } else if (intent.getAction().equals(getString(R.string.savedActionHideAdvance))) {
                    if (listPopupWindow.isShowing()) {
                        listPopupWindow.dismiss();
                    } else {
                        listPopupWindow.dismiss();
                    }
                } else if ((intent.getAction().equals(getString(R.string.checkboxActionAdvance)))) {
                    resetAdapter = true;
                    loadDataOff();
                    listPopupWindow.dismiss();
                    sendBroadcast(new Intent(getString(R.string.visibilityActionAdvance)));
                }
            }
        };
        context.registerReceiver(counterReceiver, intentFilter);

        loadDataOff();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            PublicVariable.setAppIndex = PublicVariable.categoryName + " | " + "Category";
            PublicVariable.setAppIndexUrl = String.valueOf(PublicVariable.BASE_URL.buildUpon().appendPath(PublicVariable.setAppIndex).build());

            functionsClass.IndexAppInfo(PublicVariable.setAppIndex, PublicVariable.setAppIndexUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        functionsClass.endIndexAppInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
        PublicVariable.categoryName = "";
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            functionsClass.overrideBackPress(FolderAppSelectionList.this, FolderShortcuts.class,
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_down));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                try {
                    functionsClass.overrideBackPress(FolderAppSelectionList.this, FolderShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_down));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadDataOff() {
        loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        try {
            navDrawerItems.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoadApplicationsOff loadApplicationsOff = new LoadApplicationsOff();
        loadApplicationsOff.execute();
    }

    public class LoadApplicationsOff extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                folderAppsSelectionViewBinding.recyclerViewList.getRecycledViewPool().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PackageManager packageManager = getApplicationContext().getPackageManager();
                List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(0);

                if (functionsClass.customIconsEnable()) {
                    loadCustomIcons.load();
                }

                Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(packageManager));
                for (ApplicationInfo applicationInfo : applicationInfoList) {
                    try {
                        if (packageManager.getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                            try {
                                appPackageName = applicationInfo.packageName;
                                appName = functionsClass.appName(appPackageName);
                                appIcon = functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(appPackageName, functionsClass.appIconDrawable(appPackageName)) : functionsClass.appIconDrawable(appPackageName);

                                navDrawerItems.add(new AdapterItemsData(appName, appPackageName, appIcon));

                                listOfNewCharOfItemsForIndex.add(appName.substring(0, 1).toUpperCase(Locale.getDefault()));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                folderSelectionListAdapter = new FolderSelectionListAdapter(activity, context, navDrawerItems);
                folderSelectionListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            folderAppsSelectionViewBinding.recyclerViewList.setAdapter(folderSelectionListAdapter);
            registerForContextMenu(folderAppsSelectionViewBinding.recyclerViewList);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                    folderAppsSelectionViewBinding.loadingSplash.setVisibility(View.INVISIBLE);
                    if (!resetAdapter) {
                        folderAppsSelectionViewBinding.loadingSplash.startAnimation(anim);
                    }
                    context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionAdvance)));

                    Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    folderAppsSelectionViewBinding.selectedShortcutCounterView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            folderAppsSelectionViewBinding.selectedShortcutCounterView.setText(String.valueOf(functionsClass.countLine(PublicVariable.categoryName)));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            folderAppsSelectionViewBinding.selectedShortcutCounterView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    PublicVariable.advMaxAppShortcutsCounter = functionsClass.countLine(PublicVariable.categoryName);
                    resetAdapter = false;
                }
            }, 100);

            /*Indexed Popup Fast Scroller*/
//            IndexedFastScroller indexedFastScroller = new IndexedFastScroller(
//                    getApplicationContext(),
//                    getLayoutInflater(),
//                    folderAppsSelectionViewBinding.MainView,
//                    folderAppsSelectionViewBinding.nestedScrollView,
//                    folderAppsSelectionViewBinding.recyclerViewList,
//                    folderAppsSelectionViewBinding.fastScrollerIndexInclude,
//                    new IndexedFastScrollerFactory());
//            indexedFastScroller.initializeIndexView().getOnAwait();
//            indexedFastScroller.loadIndexData(listOfNewCharOfItemsForIndex).getOnAwait();
            /*Indexed Popup Fast Scroller*/

        }
    }
}
