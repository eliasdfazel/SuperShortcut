/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 12:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.nav.AdvanceSavedListAdapter;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.nav.AdvanceSelectionListAdapter;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Utils.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdvanceAppSelectionList extends Activity implements View.OnClickListener {

    Activity activity;
    Context context;
    FunctionsClass functionsClass;

    RecyclerView recyclerView;
    RecyclerView.Adapter splitSelectionListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    ScrollView nestedScrollView, nestedIndexScrollView;
    ListPopupWindow listPopupWindow;
    RelativeLayout popupAnchorView;
    RelativeLayout wholeAuto, confirmLayout;
    LinearLayout indexView;
    RelativeLayout loadingSplash;
    TextView desc, counterView, popupIndex;
    ImageView tempIcon, loadIcon;

    List<String> appName;
    Map<String, Integer> mapIndex;
    Map<Integer, String> mapRangeIndex;
    ArrayList<NavDrawerItem> navDrawerItems, navDrawerItemsSaved;
    AdvanceSelectionListAdapter advanceSelectionListAdapter;
    AdvanceSavedListAdapter advanceSavedListAdapter;

    String PackageName;
    String AppName = "Application";
    Drawable AppIcon;

    boolean resetAdapter = false;

    LoadCustomIcons loadCustomIcons;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        setContentView(R.layout.advance_app_selection_list);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        functionsClass = new FunctionsClass(getApplicationContext(), this);

        context = getApplicationContext();
        activity = this;

        listPopupWindow = new ListPopupWindow(activity);
        desc = (TextView) findViewById(R.id.desc);
        counterView = (TextView) findViewById(R.id.counter);
        loadIcon = (ImageView) findViewById(R.id.loadLogo);
        tempIcon = (ImageView) findViewById(R.id.tempIcon);
        tempIcon.bringToFront();
        popupAnchorView = (RelativeLayout) findViewById(R.id.popupAnchorView);
        nestedIndexScrollView = (ScrollView) findViewById(R.id.nestedIndexScrollView);
        indexView = (LinearLayout) findViewById(R.id.side_index);
        popupIndex = (TextView) findViewById(R.id.popupIndex);
        wholeAuto = (RelativeLayout) findViewById(R.id.wholeAuto);
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        confirmLayout = (RelativeLayout) findViewById(R.id.confirmLayout);
        confirmLayout.bringToFront();

        recyclerView = (RecyclerView) findViewById(R.id.listFav);
        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        nestedScrollView = (ScrollView) findViewById(R.id.scrollListFav);
        nestedScrollView.setSmoothScrollingEnabled(true);

        wholeAuto.setBackgroundColor(getColor(R.color.light));
        getActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color_darker)));
        getActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>"
                + PublicVariable.categoryName.split("_")[0] + "</font>", Html.FROM_HTML_MODE_LEGACY));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.default_color_darker));
        window.setNavigationBarColor(getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItemsSaved = new ArrayList<NavDrawerItem>();
        appName = new ArrayList<String>();
        mapIndex = new LinkedHashMap<String, Integer>();
        mapRangeIndex = new LinkedHashMap<Integer, String>();

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        desc.setTypeface(face);
        desc.setText(PublicVariable.categoryName.split("_")[0]);
        counterView.setTypeface(face);
        counterView.bringToFront();

        ProgressBar loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
        loadingBarLTR.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.counterActionAdvance));
        intentFilter.addAction(context.getString(R.string.savedActionAdvance));
        intentFilter.addAction(context.getString(R.string.savedActionHideAdvance));
        intentFilter.addAction(context.getString(R.string.checkboxActionAdvance));
        BroadcastReceiver counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.counterActionAdvance))) {
                    counterView.setText(String.valueOf(functionsClass.countLine(PublicVariable.categoryName)));
                } else if (intent.getAction().equals(context.getString(R.string.savedActionAdvance))) {
                    if (getFileStreamPath(PublicVariable.categoryName).exists() && functionsClass.countLine(PublicVariable.categoryName) > 0) {
                        navDrawerItemsSaved.clear();
                        String[] savedLine = functionsClass.readFileLine(PublicVariable.categoryName);
                        for (String aSavedLine : savedLine) {
                            navDrawerItemsSaved.add(new NavDrawerItem(
                                    functionsClass.appName(aSavedLine),
                                    aSavedLine,
                                    functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClass.appIconDrawable(aSavedLine)) : functionsClass.appIconDrawable(aSavedLine)
                            ));
                        }
                        advanceSavedListAdapter = new AdvanceSavedListAdapter(activity, context, navDrawerItemsSaved);
                        listPopupWindow = new ListPopupWindow(activity);
                        listPopupWindow.setAdapter(advanceSavedListAdapter);
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
    public void onResume() {
        super.onResume();
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
            functionsClass.overrideBackPress(AdvanceShortcuts.class,
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
                    functionsClass.overrideBackPress(AdvanceShortcuts.class,
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

                if (functionsClass.loadCustomIcons()) {
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
                                AppIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.appIconDrawable(PackageName)) : functionsClass.appIconDrawable(PackageName);

                                navDrawerItems.add(new NavDrawerItem(AppName, PackageName, AppIcon));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                advanceSelectionListAdapter = new AdvanceSelectionListAdapter(activity, context, navDrawerItems);
                advanceSelectionListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            recyclerView.setAdapter(advanceSelectionListAdapter);
            registerForContextMenu(recyclerView);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);
                    if (resetAdapter == false) {
                        loadingSplash.startAnimation(anim);
                    }
                    context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionAdvance)));

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

                    PublicVariable.advMaxAppShortcutsCounter = functionsClass.countLine(PublicVariable.categoryName);
                    resetAdapter = false;
                }
            }, 100);

            LoadApplicationsIndex loadApplicationsIndex = new LoadApplicationsIndex();
            loadApplicationsIndex.execute();
        }
    }

    private class LoadApplicationsIndex extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indexView.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int navItem = 0; navItem < appName.size(); navItem++) {
                try {
                    String index = (appName.get(navItem)).substring(0, 1).toUpperCase();
                    if (mapIndex.get(index) == null) {
                        mapIndex.put(index, navItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            LayerDrawable drawIndex = (LayerDrawable) getDrawable(R.drawable.draw_index);
            GradientDrawable backIndex = (GradientDrawable) drawIndex.findDrawableByLayerId(R.id.backtemp);
            backIndex.setColor(Color.TRANSPARENT);

            TextView textView = null;
            List<String> indexList = new ArrayList<String>(mapIndex.keySet());
            for (String index : indexList) {
                textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.side_index_item, null);
                textView.setBackground(drawIndex);
                textView.setText(index.toUpperCase());
                textView.setTextColor(getColor(R.color.dark));
                indexView.addView(textView);
            }

            PublicVariable.advMaxAppShortcuts = appName.size();
            getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.advMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));

            TextView finalTextView = textView;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int upperRange = (int) (indexView.getY() - finalTextView.getHeight());
                    for (int i = 0; i < indexView.getChildCount(); i++) {
                        String indexText = ((TextView) indexView.getChildAt(i)).getText().toString();
                        int indexRange = (int) (indexView.getChildAt(i).getY() + indexView.getY() + finalTextView.getHeight());
                        for (int jRange = upperRange; jRange <= (indexRange); jRange++) {
                            mapRangeIndex.put(jRange, indexText);
                        }

                        upperRange = indexRange;
                    }

                    setupFastScrollingIndexing();
                }
            }, 700);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupFastScrollingIndexing() {
        Drawable popupIndexBackground = getDrawable(R.drawable.ic_launcher_balloon).mutate();
        popupIndexBackground.setTint(getColor(R.color.default_color_darker));
        popupIndex.setBackground(popupIndexBackground);

        nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        nestedIndexScrollView.setVisibility(View.VISIBLE);

        float popupIndexOffsetY = PublicVariable.statusBarHeight + PublicVariable.actionBarHeight + PublicVariable.navigationBarHeight + functionsClass.DpToInteger(7);
        nestedIndexScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        String indexText = mapRangeIndex.get((((int) motionEvent.getY())));

                        if (indexText != null) {
                            popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                            popupIndex.setText(indexText);
                            popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                            popupIndex.setVisibility(View.VISIBLE);
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        String indexText = mapRangeIndex.get(((int) motionEvent.getY()));

                        if (indexText != null) {
                            if (!popupIndex.isShown()) {
                                popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                popupIndex.setVisibility(View.VISIBLE);
                            }
                            popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                            popupIndex.setText(indexText);

                            try {
                                nestedScrollView.smoothScrollTo(
                                        0,
                                        ((int) recyclerView.getChildAt(mapIndex.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY()) - functionsClass.DpToInteger(7)
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (popupIndex.isShown()) {
                                popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                popupIndex.setVisibility(View.INVISIBLE);
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (popupIndex.isShown()) {
                            try {
                                nestedScrollView.smoothScrollTo(
                                        0,
                                        ((int) recyclerView.getChildAt(mapIndex.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY()) - functionsClass.DpToInteger(7)
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                            popupIndex.setVisibility(View.INVISIBLE);
                        }

                        break;
                    }
                }
                return true;
            }
        });
    }
}
