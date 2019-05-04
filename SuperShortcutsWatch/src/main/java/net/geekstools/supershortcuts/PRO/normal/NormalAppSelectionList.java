package net.geekstools.supershortcuts.PRO.normal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.LicenseValidator;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.normal.nav.SavedListAdapter;
import net.geekstools.supershortcuts.PRO.normal.nav.SelectionListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NormalAppSelectionList extends WearableActivity implements View.OnClickListener {

    Activity activity;
    Context context;
    FunctionsClass functionsClass;

    RecyclerView recyclerView;
    RecyclerView.Adapter selectionListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    ScrollView nestedScrollView;
    ListPopupWindow listPopupWindow;
    RelativeLayout popupAnchorView;
    RelativeLayout wholeAuto, confirmLayout;
    LinearLayout indexView;
    RelativeLayout loadingSplash;
    TextView counterView;
    ImageView tempIcon, loadIcon;
    Button supportView;

    List<String> appName;
    Map<String, Integer> mapIndex;
    ArrayList<NavDrawerItem> navDrawerItems, navDrawerItemsSaved;
    SavedListAdapter savedListAdapter;

    String PackageName;
    String AppName = "Application";
    Drawable AppIcon;

    boolean resetAdapter = false;

    FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        setContentView(R.layout.activity_app_selection_list);
        setAmbientEnabled();
        functionsClass = new FunctionsClass(getApplicationContext(), this);

        context = getApplicationContext();
        activity = this;

        listPopupWindow = new ListPopupWindow(activity);
        counterView = (TextView) findViewById(R.id.counter);
        loadIcon = (ImageView) findViewById(R.id.loadLogo);
        tempIcon = (ImageView) findViewById(R.id.tempIcon);
        tempIcon.bringToFront();
        supportView = (Button) findViewById(R.id.supportView);
        popupAnchorView = (RelativeLayout) findViewById(R.id.popupAnchorView);
        indexView = (LinearLayout) findViewById(R.id.side_index);
        wholeAuto = (RelativeLayout) findViewById(R.id.wholeAuto);
        wholeAuto.setBackgroundColor(getColor(R.color.light));
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        confirmLayout = (RelativeLayout) findViewById(R.id.confirmLayout);
        confirmLayout.bringToFront();

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItemsSaved = new ArrayList<NavDrawerItem>();
        appName = new ArrayList<String>();
        mapIndex = new LinkedHashMap<String, Integer>();

        recyclerView = (RecyclerView) findViewById(R.id.listFav);
        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        nestedScrollView = (ScrollView) findViewById(R.id.scrollListFav);
        nestedScrollView.setSmoothScrollingEnabled(true);

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        counterView.setTypeface(face);
        counterView.bringToFront();

        ProgressBar loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
        loadingBarLTR.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.loadOffline));
        intentFilter.addAction(context.getString(R.string.counterAction));
        intentFilter.addAction(context.getString(R.string.savedAction));
        intentFilter.addAction(context.getString(R.string.savedActionHide));
        intentFilter.addAction(context.getString(R.string.checkboxAction));
        intentFilter.addAction(context.getString(R.string.license));
        BroadcastReceiver counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.loadOffline))) {
                    loadDataOff();
                } else if (intent.getAction().equals(context.getString(R.string.counterAction))) {
                    counterView.setText(String.valueOf(functionsClass.countLine(".autoSuper")));
                } else if (intent.getAction().equals(context.getString(R.string.savedAction))) {
                    if (getFileStreamPath(".autoSuper").exists() && functionsClass.countLine(".autoSuper") > 0) {
                        navDrawerItemsSaved.clear();
                        String[] savedLine = functionsClass.readFileLine(".autoSuper");
                        for (String aSavedLine : savedLine) {
                            navDrawerItemsSaved.add(new NavDrawerItem(
                                    functionsClass.appName(aSavedLine),
                                    aSavedLine,
                                    functionsClass.appIconDrawable(aSavedLine)));
                        }
                        savedListAdapter = new SavedListAdapter(activity, context, navDrawerItemsSaved);
                        listPopupWindow = new ListPopupWindow(activity);
                        listPopupWindow.setAdapter(savedListAdapter);
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
                                sendBroadcast(new Intent(getString(R.string.visibilityAction)));
                            }
                        });
                    }
                } else if (intent.getAction().equals(getString(R.string.savedActionHide))) {
                    if (listPopupWindow.isShowing()) {
                        listPopupWindow.dismiss();
                    } else {
                        listPopupWindow.dismiss();
                    }
                } else if ((intent.getAction().equals(getString(R.string.checkboxAction)))) {
                    resetAdapter = true;
                    loadDataOff();
                    listPopupWindow.dismiss();
                    sendBroadcast(new Intent(getString(R.string.visibilityAction)));
                } else if (intent.getAction().equals(getString(R.string.license))) {
                    functionsClass.dialogueLicense();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopService(new Intent(getApplicationContext(), LicenseValidator.class));
                        }
                    }, 1000);
                }
            }
        };
        context.registerReceiver(counterReceiver, intentFilter);

        loadDataOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default);

        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(NormalAppSelectionList.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.updateAvailable), Toast.LENGTH_LONG).show();
                                    } else {

                                    }
                                }
                            });
                        } else {

                        }
                    }
                });

        if (!getFileStreamPath(".License").exists() && functionsClass.networkConnection() == true) {
            if (!BuildConfig.DEBUG || !functionsClass.appVersionName(getPackageName()).contains("[BETA]")) {
                startService(new Intent(getApplicationContext(), LicenseValidator.class));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        supportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textMsg = "\n\n\n\n\n"
                        + functionsClass.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClass.getCountryIso().toUpperCase();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.support)});
                email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_tag) + " [" + functionsClass.appVersionName(getPackageName()) + "] ");
                email.putExtra(Intent.EXTRA_TEXT, textMsg);
                //email.setType("text/*");
                email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(email, getString(R.string.feedback_tag)));
            }
        });
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
    }

    @Override
    public void onPause() {
        super.onPause();
        functionsClass.Toast(null, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof TextView) {
            final TextView selectedIndex = (TextView) view;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    nestedScrollView.smoothScrollTo(
                            0,
                            ((int) recyclerView.getChildAt(mapIndex.get(selectedIndex.getText().toString())).getY())
                    );
                }
            });
        }
    }

    public void loadDataOff() {
        try {
            navDrawerItems.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoadApplicationsOff loadApplicationsOff = new LoadApplicationsOff();
        loadApplicationsOff.execute();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {

        } else {
        }
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

                Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(packageManager));
                for (ApplicationInfo applicationInfo : applicationInfoList) {
                    try {
                        if (packageManager.getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                            try {
                                PackageName = applicationInfo.packageName;
                                AppName = functionsClass.appName(PackageName);
                                appName.add(AppName);
                                AppIcon = functionsClass.appIconDrawable(PackageName);

                                navDrawerItems.add(new NavDrawerItem(AppName, PackageName, AppIcon));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                selectionListAdapter = new SelectionListAdapter(activity, context, navDrawerItems);
                selectionListAdapter.notifyDataSetChanged();
                functionsClass.savePreference("InstalledApps", "countApps", navDrawerItems.size());
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            recyclerView.setAdapter(selectionListAdapter);
            registerForContextMenu(recyclerView);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);

                    if (resetAdapter == false) {
                        loadingSplash.startAnimation(anim);
                    }
                    context.sendBroadcast(new Intent(context.getString(R.string.visibilityAction)));

                    Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    counterView.startAnimation(animation);
                    supportView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            counterView.setText(String.valueOf(functionsClass.countLine(".autoSuper")));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            counterView.setVisibility(View.VISIBLE);
                            supportView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    PublicVariable.maxAppShortcutsCounter = functionsClass.countLine(".autoSuper");
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
                textView.setOnClickListener(NormalAppSelectionList.this);
                indexView.addView(textView);
            }

            PublicVariable.maxAppShortcuts = appName.size();
        }
    }
}
