package net.geekstools.supershortcuts.PRO.advanced;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.TextView;

import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.advanced.nav.AdvanceSavedListAdapter;
import net.geekstools.supershortcuts.PRO.advanced.nav.AdvanceSelectionListAdapter;

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

    ListPopupWindow listPopupWindow;
    RelativeLayout popupAnchorView;
    RelativeLayout wholeAuto, confirmLayout;
    LinearLayout indexView;
    RelativeLayout loadingSplash;
    TextView desc, counterView;
    ImageView tempIcon, loadIcon;

    List<String> appName;
    Map<String, Integer> mapIndex;
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
        indexView = (LinearLayout) findViewById(R.id.side_index);
        wholeAuto = (RelativeLayout) findViewById(R.id.wholeAuto);
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        confirmLayout = (RelativeLayout) findViewById(R.id.confirmLayout);
        confirmLayout.bringToFront();

        recyclerView = (RecyclerView) findViewById(R.id.listFav);
        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        wholeAuto.setBackgroundColor(getResources().getColor(R.color.light));
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.default_color_darker)));
        getActionBar().setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.light) + "'>"
                + PublicVariable.categoryName.split("_")[0] + "</font>"));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.default_color_darker));
        window.setNavigationBarColor(getResources().getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItemsSaved = new ArrayList<NavDrawerItem>();
        appName = new ArrayList<String>();
        mapIndex = new LinkedHashMap<String, Integer>();

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        desc.setTypeface(face);
        desc.setText(PublicVariable.categoryName.split("_")[0]);
        counterView.setTypeface(face);
        counterView.bringToFront();

        ProgressBar loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
        loadingBarLTR.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

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
        if (view instanceof TextView) {
            final TextView selectedIndex = (TextView) view;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(mapIndex.get(selectedIndex.getText().toString()));
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pref_menu, menu);
        menu.findItem(R.id.facebook).setVisible(false);
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
                        System.out.println("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
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
            LayerDrawable drawIndex = (LayerDrawable) getResources().getDrawable(R.drawable.draw_index);
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
                textView.setOnClickListener(AdvanceAppSelectionList.this);
                indexView.addView(textView);
            }

            PublicVariable.advMaxAppShortcuts = appName.size();
            getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getResources().getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getResources().getColor(R.color.light) + "'>" + PublicVariable.advMaxAppShortcuts + "</font></b></small>"));

        }
    }
}
