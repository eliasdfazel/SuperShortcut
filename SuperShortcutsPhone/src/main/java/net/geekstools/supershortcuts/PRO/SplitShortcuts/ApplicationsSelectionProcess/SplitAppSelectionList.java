/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/3/20 10:00 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.ApplicationsSelectionProcess;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.ApplicationsSelectionProcess.Adapters.SplitSavedListAdapter;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.ApplicationsSelectionProcess.Adapters.SplitSelectionListAdapter;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SplitAppSelectionList extends AppCompatActivity implements View.OnClickListener {

    Activity activity;
    Context context;
    FunctionsClass functionsClass;

    ScrollView nestedScrollView, nestedIndexScrollView;
    ListPopupWindow listPopupWindow;
    RelativeLayout popupAnchorView;
    RelativeLayout wholeAuto, confirmLayout;
    LinearLayout indexView;
    RelativeLayout loadingSplash;
    TextView desc, counterView, popupIndex;
    ImageView tempIcon, loadIcon;

    RecyclerView recyclerView;
    RecyclerView.Adapter splitSelectionListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    List<String> appName;
    Map<String, Integer> mapIndex;
    Map<Integer, String> mapRangeIndex;
    ArrayList<AdapterItemsData> navDrawerItems, navDrawerItemsSaved;
    SplitSavedListAdapter splitSavedListAdapter;

    String PackageName;
    String AppName = "Application";
    Drawable AppIcon;

    boolean resetAdapter = false;

    LoadCustomIcons loadCustomIcons;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        setContentView(R.layout.split_app_selection_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        functionsClass = new FunctionsClass(getApplicationContext());
        PublicVariable.SplitMaxAppShortcuts = 2;

        context = getApplicationContext();
        activity = this;

        listPopupWindow = new ListPopupWindow(activity);
        desc = (TextView) findViewById(R.id.desc);
        counterView = (TextView) findViewById(R.id.selected_shortcut_counter_view);
        loadIcon = (ImageView) findViewById(R.id.loadingLogo);
        tempIcon = (ImageView) findViewById(R.id.temporary_falling_icon);
        tempIcon.bringToFront();
        popupAnchorView = (RelativeLayout) findViewById(R.id.popupAnchorView);
        nestedIndexScrollView = (ScrollView) findViewById(R.id.nestedIndexScrollView);
        indexView = (LinearLayout) findViewById(R.id.sideIndex);
        popupIndex = (TextView) findViewById(R.id.popupIndex);
        wholeAuto = (RelativeLayout) findViewById(R.id.MainView);
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        confirmLayout = (RelativeLayout) findViewById(R.id.confirmLayout);
        confirmLayout.bringToFront();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_list);
        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        nestedScrollView = (ScrollView) findViewById(R.id.nestedScrollView);
        nestedScrollView.setSmoothScrollingEnabled(true);

        wholeAuto.setBackgroundColor(getColor(R.color.light));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color_darker)));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>"
                + PublicVariable.categoryName.split("_")[0] + "</font>", Html.FROM_HTML_MODE_LEGACY));
        getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.SplitMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
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
        appName = new ArrayList<String>();
        mapIndex = new LinkedHashMap<String, Integer>();
        mapRangeIndex = new LinkedHashMap<Integer, String>();

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        desc.setTypeface(face);
        desc.setText(PublicVariable.categoryName.split("_")[0]);
        counterView.setTypeface(face);
        counterView.bringToFront();

        ProgressBar loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgress);
        loadingBarLTR.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.counterActionSplit));
        intentFilter.addAction(context.getString(R.string.savedActionSplit));
        intentFilter.addAction(context.getString(R.string.savedActionHideSplit));
        intentFilter.addAction(context.getString(R.string.checkboxActionSplit));
        BroadcastReceiver counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.counterActionSplit))) {
                    counterView.setText(String.valueOf(functionsClass.countLine(PublicVariable.categoryName)));
                } else if (intent.getAction().equals(context.getString(R.string.savedActionSplit))) {
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
                        splitSavedListAdapter = new SplitSavedListAdapter(activity, context, navDrawerItemsSaved);
                        listPopupWindow = new ListPopupWindow(activity);
                        listPopupWindow.setAdapter(splitSavedListAdapter);
                        listPopupWindow.setAnchorView(popupAnchorView);
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
                                sendBroadcast(new Intent(getString(R.string.visibilityActionSplit)));
                            }
                        });
                    }
                } else if (intent.getAction().equals(getString(R.string.savedActionHideSplit))) {
                    if (listPopupWindow.isShowing()) {
                        listPopupWindow.dismiss();
                    } else {
                        listPopupWindow.dismiss();
                    }
                } else if ((intent.getAction().equals(getString(R.string.checkboxActionSplit)))) {
                    resetAdapter = true;
                    loadDataOff();
                    listPopupWindow.dismiss();
                    sendBroadcast(new Intent(getString(R.string.visibilityActionSplit)));
                }
            }
        };
        context.registerReceiver(counterReceiver, intentFilter);

        loadDataOff();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            PublicVariable.setAppIndex = PublicVariable.categoryName + " | " + "Split";
            PublicVariable.setAppIndexUrl = String.valueOf(PublicVariable.BASE_URL.buildUpon().appendPath(PublicVariable.setAppIndex).build());

            functionsClass.IndexAppInfo(PublicVariable.setAppIndex, PublicVariable.setAppIndexUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
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
            functionsClass.overrideBackPress(SplitAppSelectionList.this, SplitShortcuts.class,
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
                    functionsClass.overrideBackPress(SplitAppSelectionList.this, SplitShortcuts.class,
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
                recyclerView.getRecycledViewPool().clear();
                indexView.removeAllViews();
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
                    if (BuildConfig.DEBUG) {
                        FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                    }
                }

                Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(packageManager));
                for (ApplicationInfo applicationInfo : applicationInfoList) {
                    try {
                        if (packageManager.getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                            try {
                                PackageName = applicationInfo.packageName;
                                AppName = functionsClass.appName(PackageName);
                                appName.add(AppName);
                                AppIcon = functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.appIconDrawable(PackageName)) : functionsClass.appIconDrawable(PackageName);

                                navDrawerItems.add(new AdapterItemsData(AppName, PackageName, AppIcon));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                splitSelectionListAdapter = new SplitSelectionListAdapter(activity, context, navDrawerItems);
                splitSelectionListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            recyclerView.setAdapter(splitSelectionListAdapter);
            registerForContextMenu(recyclerView);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);
                    if (resetAdapter == false) {
                        loadingSplash.startAnimation(anim);
                    }
                    context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionSplit)));

                    Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    counterView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            counterView.setText(String.valueOf(functionsClass.countLine(PublicVariable.categoryName)));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            counterView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    PublicVariable.SplitMaxAppShortcutsCounter = functionsClass.countLine(PublicVariable.categoryName);
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
