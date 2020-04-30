/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 2:40 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.MenuInflater;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters.SavedAppsListPopupAdapter;
import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.EntryConfigurations;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.AdvanceShortcuts;
import net.geekstools.supershortcuts.PRO.Preferences.PreferencesUI;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling;
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData;
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint;
import net.geekstools.supershortcuts.PRO.Utils.RemoteProcess.LicenseValidator;
import net.geekstools.supershortcuts.PRO.Utils.SimpleGestureFilterSwitch;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NormalAppShortcutsSelectionList extends AppCompatActivity implements SimpleGestureFilterSwitch.SimpleGestureListener {

    FunctionsClass functionsClass;

    RecyclerView recyclerViewApplicationsList;
    RecyclerView.Adapter selectionListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    ScrollView scrollListFav, nestedIndexScrollView;
    ListPopupWindow listPopupWindow;
    RelativeLayout popupAnchorView;
    RelativeLayout MainView, confirmLayout;
    LinearLayout sideIndex, autoSelect;
    RelativeLayout loadingSplash;
    TextView loadingDescription, counter, popupIndex;
    ImageView tempIcon, loadingLogo;
    ProgressBar loadingProgress;

    MaterialButton autoApps, autoSplit, autoCategories;

    MenuItem mixShortcutsMenuItem;

    List<String> indexAppName;
    Map<String, Integer> mapIndex;
    Map<Integer, String> mapRangeIndex;
    ArrayList<AdapterItemsData> navDrawerItems, savedApplicationsList;
    SavedAppsListPopupAdapter savedAppsListPopupAdapter;

    String PackageName, className, AppName = "Application";
    Drawable AppIcon;

    int appShortcutLimitCounter;
    BroadcastReceiver counterReceiver;
    boolean resetAdapter = false;

    SimpleGestureFilterSwitch simpleGestureFilterSwitch;

    LoadCustomIcons loadCustomIcons;

    FirebaseRemoteConfig firebaseRemoteConfig;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        setContentView(R.layout.normal_app_selection);

        listPopupWindow = new ListPopupWindow(NormalAppShortcutsSelectionList.this);
        loadingDescription = (TextView) findViewById(R.id.loadingDescription);
        counter = (TextView) findViewById(R.id.app_selected_counter_view);
        loadingLogo = (ImageView) findViewById(R.id.loadingLogo);
        tempIcon = (ImageView) findViewById(R.id.temporary_falling_icon);
        tempIcon.bringToFront();
        popupAnchorView = (RelativeLayout) findViewById(R.id.popupAnchorView);
        nestedIndexScrollView = (ScrollView) findViewById(R.id.nestedIndexScrollView);
        sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        popupIndex = (TextView) findViewById(R.id.popupIndex);
        MainView = (RelativeLayout) findViewById(R.id.MainView);
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        confirmLayout = (RelativeLayout) findViewById(R.id.confirmLayout);
        confirmLayout.bringToFront();
        autoSelect = (LinearLayout) findViewById(R.id.autoSelect);

        autoApps = (MaterialButton) findViewById(R.id.autoApps);
        autoSplit = (MaterialButton) findViewById(R.id.autoSplit);
        autoCategories = (MaterialButton) findViewById(R.id.autoCategories);

        recyclerViewApplicationsList = (RecyclerView) findViewById(R.id.recyclerViewApplicationsList);
        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        recyclerViewApplicationsList.setLayoutManager(recyclerViewLayoutManager);

        scrollListFav = (ScrollView) findViewById(R.id.nestedScrollView);
        scrollListFav.setSmoothScrollingEnabled(true);

        MainView.setBackgroundColor(getColor(R.color.light));
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
        savedApplicationsList = new ArrayList<AdapterItemsData>();
        indexAppName = new ArrayList<String>();
        mapIndex = new LinkedHashMap<String, Integer>();
        mapRangeIndex = new LinkedHashMap<Integer, String>();

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        loadingDescription.setTypeface(face);
        counter.setTypeface(face);
        counter.bringToFront();

        loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);
        loadingProgress.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.loadOffline));
        intentFilter.addAction(getString(R.string.counterAction));
        intentFilter.addAction(getString(R.string.savedAction));
        intentFilter.addAction(getString(R.string.savedActionHide));
        intentFilter.addAction(getString(R.string.checkboxAction));
        intentFilter.addAction(getString(R.string.dynamicShortcuts));
        intentFilter.addAction(getString(R.string.license));
        counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.loadOffline))) {

                    loadDataOff();

                } else if (intent.getAction().equals(context.getString(R.string.counterAction))) {

                    counter.setText(String.valueOf(functionsClass.countLine(NormalAppShortcutsSelectionListXYZ.NormalApplicationsShortcutsFile)));

                } else if (intent.getAction().equals(context.getString(R.string.savedAction))) {

                    if (getFileStreamPath(NormalAppShortcutsSelectionListXYZ.NormalApplicationsShortcutsFile).exists() && functionsClass.countLine(NormalAppShortcutsSelectionListXYZ.NormalApplicationsShortcutsFile) > 0) {
                        savedApplicationsList.clear();

                        String[] savedLine = functionsClass.readFileLine(NormalAppShortcutsSelectionListXYZ.NormalApplicationsShortcutsFile);
                        for (String aSavedLine : savedLine) {
                            try {
                                final String packageName = aSavedLine.split("\\|")[0];
                                final String className = aSavedLine.split("\\|")[1];
                                ActivityInfo activityInfo = getPackageManager().getActivityInfo(new ComponentName(packageName, className), 0);
                                savedApplicationsList.add(new AdapterItemsData(
                                        functionsClass.activityLabel(activityInfo),
                                        packageName,
                                        className,
                                        AppIcon = functionsClass.customIconsEnable() ?
                                                loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.activityIcon(activityInfo))
                                                :
                                                functionsClass.activityIcon(activityInfo)

                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                     //   savedAppsListPopupAdapter = new SavedAppsListPopupAdapter(NormalAppShortcutsSelectionList.this, context, savedApplicationsList);
                        listPopupWindow = new ListPopupWindow(NormalAppShortcutsSelectionList.this);
                        listPopupWindow.setAdapter(savedAppsListPopupAdapter);
                        listPopupWindow.setAnchorView(popupAnchorView);
                        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setModal(true);
                        listPopupWindow.setBackgroundDrawable(null);

                        listPopupWindow.show();
                        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                sendBroadcast(new Intent(getString(R.string.visibilityAction)));
                            }
                        });
                    }

                } else if (intent.getAction().equals(getString(R.string.savedActionHide))) {

                    listPopupWindow.dismiss();

                } else if ((intent.getAction().equals(getString(R.string.checkboxAction)))) {

                    resetAdapter = true;

                    loadDataOff();

                    listPopupWindow.dismiss();
                    sendBroadcast(new Intent(getString(R.string.visibilityAction)));

                } else if (intent.getAction().equals(getString(R.string.dynamicShortcuts))) {

                    if (functionsClass.mixShortcuts()) {

                        PublicVariable.maxAppShortcuts
                                = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
                        getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.maxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));

                    } else {

                        appShortcutLimitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(NormalAppShortcutsSelectionListXYZ.NormalApplicationsShortcutsFile);
                        getSupportActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + appShortcutLimitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
                        PublicVariable.maxAppShortcuts = functionsClass.getSystemMaxAppShortcut();

                    }

                } else if (intent.getAction().equals(getString(R.string.license))) {

                    functionsClass.dialogueLicense(NormalAppShortcutsSelectionList.this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopService(new Intent(getApplicationContext(), LicenseValidator.class));
                        }
                    }, 1000);

                }
            }
        };
        registerReceiver(counterReceiver, intentFilter);

        autoApps.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.default_color)));
        autoSplit.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.default_color_darker)));
        autoCategories.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.default_color_darker)));

        loadDataOff();

        firebaseAuth = FirebaseAuth.getInstance();

        //In-App Billing
        new PurchasesCheckpoint(NormalAppShortcutsSelectionList.this).trigger();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);
            firebaseRemoteConfig.fetch(0)
                    .addOnCompleteListener(NormalAppShortcutsSelectionList.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                            LayerDrawable layerDrawableNewUpdate = (LayerDrawable) getDrawable(R.drawable.ic_update);
                                            BitmapDrawable gradientDrawableNewUpdate = (BitmapDrawable) layerDrawableNewUpdate.findDrawableByLayerId(R.id.ic_launcher_back_layer);
                                            gradientDrawableNewUpdate.setTint(getColor(R.color.default_color_light));

                                            Bitmap tempBitmap = functionsClass.drawableToBitmap(layerDrawableNewUpdate);
                                            Bitmap scaleBitmap = Bitmap.createScaledBitmap(tempBitmap, tempBitmap.getWidth() / 4, tempBitmap.getHeight() / 4, false);
                                            Drawable logoDrawable = new BitmapDrawable(getResources(), scaleBitmap);
                                            NormalAppShortcutsSelectionList.this.getSupportActionBar().setHomeAsUpIndicator(logoDrawable);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!getFileStreamPath(".License").exists() && functionsClass.networkConnection() == true) {
            if (!BuildConfig.DEBUG || !functionsClass.appVersionName(getPackageName()).contains("[BETA]")) {
                startService(new Intent(getApplicationContext(), LicenseValidator.class));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        recyclerViewApplicationsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        autoCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(NormalAppShortcutsSelectionList.this, AdvanceShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        autoSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(NormalAppShortcutsSelectionList.this, SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (functionsClass.networkConnection()) {
                try {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user == null) {
                                        functionsClass.savePreference(".UserInformation", "userEmail", null);
                                    } else {

                                    }


                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (functionsClass.readPreference(".UserInformation", "userEmail", null) == null) {
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.webClientId))
                            .requestEmail()
                            .build();

                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
                    try {
                        googleSignInClient.signOut();
                        googleSignInClient.revokeAccess();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 666);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TabsView", NormalAppShortcutsSelectionList.class.getSimpleName());
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            firebaseUser.reload();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            NormalAppShortcutsSelectionList.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            case SimpleGestureFilterSwitch.SWIPE_LEFT:
                FunctionsClassDebug.Companion.PrintDebug("Swipe Left");
                try {
                    functionsClass.overrideBackPress(NormalAppShortcutsSelectionList.this, SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.selection_menu, menu);

        mixShortcutsMenuItem = menu.findItem(R.id.mixShortcuts);

        if (functionsClass.mixShortcutsPurchased()) {
            if (functionsClass.mixShortcuts()) {
                LayerDrawable drawMixHint = (LayerDrawable) getDrawable(R.drawable.draw_mix_hint);
                Drawable backDrawMixHint = drawMixHint.findDrawableByLayerId(R.id.backtemp);
                backDrawMixHint.setTint(getColor(R.color.default_color_light));

                mixShortcutsMenuItem.setIcon(drawMixHint);
                mixShortcutsMenuItem.setTitle(getString(R.string.mixShortcutsEnable));
            } else {
                LayerDrawable drawMixHint = (LayerDrawable) getDrawable(R.drawable.draw_mix_hint);
                Drawable backDrawMixHint = drawMixHint.findDrawableByLayerId(R.id.backtemp);
                backDrawMixHint.setTint(getColor(R.color.dark));

                mixShortcutsMenuItem.setIcon(drawMixHint);
                mixShortcutsMenuItem.setTitle(getString(R.string.mixShortcutsDisable));
            }
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.prefs: {
                startActivity(new Intent(getApplicationContext(), PreferencesUI.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.up_down, android.R.anim.fade_out).toBundle());
                finish();
                break;
            }
            case R.id.mixShortcuts: {
                if (functionsClass.mixShortcutsPurchased()) {
                    try {
                        functionsClass.deleteSelectedFiles();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("mix", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (sharedPreferences.getBoolean("mixShortcuts", false) == true) {
                        editor.putBoolean("mixShortcuts", false);
                        editor.apply();
                    } else if (sharedPreferences.getBoolean("mixShortcuts", false) == false) {
                        editor.putBoolean("mixShortcuts", true);
                        editor.apply();
                    }

                    Intent intent = new Intent(getApplicationContext(), EntryConfigurations.class);
                    startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                } else {

                    startActivity(new Intent(getApplicationContext(), InitializeInAppBilling.class)
                        .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                        .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemMixShortcuts)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    , ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.down_up, android.R.anim.fade_out).toBundle());

                }
                break;
            }
            case android.R.id.home: {
                new FunctionsClassDialogues(NormalAppShortcutsSelectionList.this, functionsClass).changeLogPreference(
                        firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()),
                        String.valueOf(firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()))
                );
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666) {
            try {
                Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

                AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                firebaseAuth.signInWithCredential(authCredential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        functionsClass.savePreference(".UserInformation", "userEmail", firebaseUser.getEmail());

                                        if (!functionsClass.mixShortcutsPurchased() || !functionsClass.alreadyDonated()) {
                                            BillingClient billingClient = BillingClient.newBuilder(NormalAppShortcutsSelectionList.this).setListener(new PurchasesUpdatedListener() {
                                                @Override
                                                public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {

                                                    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

                                                    } else {

                                                    }

                                                }
                                            }).enablePendingPurchases().build();
                                            billingClient.startConnection(new BillingClientStateListener() {
                                                @Override
                                                public void onBillingSetupFinished(BillingResult billingResult) {
                                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                                        functionsClass.savePreference(".PurchasedItem", "mix.shortcuts", false);

                                                        List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                                                        for (Purchase purchase : purchases) {
                                                            FunctionsClassDebug.Companion.PrintDebug("*** Purchased Item: " + purchase + " ***");

                                                            functionsClass.savePreference(".PurchasedItem", purchase.getSku(), true);
                                                            if (purchase.getSku().equals("mix.shortcuts")) {
                                                                if (functionsClass.mixShortcuts()) {
                                                                    LayerDrawable drawMixHint = (LayerDrawable) getDrawable(R.drawable.draw_mix_hint);
                                                                    Drawable backDrawMixHint = drawMixHint.findDrawableByLayerId(R.id.backtemp);
                                                                    backDrawMixHint.setTint(getColor(R.color.default_color_light));

                                                                    mixShortcutsMenuItem.setIcon(drawMixHint);
                                                                    mixShortcutsMenuItem.setTitle(getString(R.string.mixShortcutsEnable));
                                                                } else {
                                                                    LayerDrawable drawMixHint = (LayerDrawable) getDrawable(R.drawable.draw_mix_hint);
                                                                    Drawable backDrawMixHint = drawMixHint.findDrawableByLayerId(R.id.backtemp);
                                                                    backDrawMixHint.setTint(getColor(R.color.dark));

                                                                    mixShortcutsMenuItem.setIcon(drawMixHint);
                                                                    mixShortcutsMenuItem.setTitle(getString(R.string.mixShortcutsDisable));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onBillingServiceDisconnected() {

                                                }
                                            });
                                        }
                                    }
                                } else {

                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadDataOff() {
        loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        LoadInstalledCustomIcons loadInstalledCustomIcons = new LoadInstalledCustomIcons();
        loadInstalledCustomIcons.execute();

        if (functionsClass.UsageAccessEnabled()) {
            loadingProgress.setVisibility(View.INVISIBLE);
            loadingLogo.setImageDrawable(getDrawable(R.drawable.draw_smart));
            loadingDescription.setText(Html.fromHtml(getString(R.string.smartInfo), Html.FROM_HTML_MODE_LEGACY));

            try {
                functionsClass.deleteSelectedFiles();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                retrieveFrequentlyUsedApplications();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                navDrawerItems.clear();

                if (!functionsClass.mixShortcuts()) {
                    if (getApplicationContext().getFileStreamPath(".mixShortcuts").exists()) {
                        String[] mixContent = functionsClass.readFileLine(".mixShortcuts");
                        for (String mixLine : mixContent) {
                            if (mixLine.contains(".CategorySelected")) {
                                getApplicationContext().deleteFile(functionsClass.categoryNameSelected(mixLine));
                            } else if (mixLine.contains(".SplitSelected")) {
                                getApplicationContext().deleteFile(functionsClass.splitNameSelected(mixLine));
                            } else {
                                getApplicationContext().deleteFile(functionsClass.packageNameSelected(mixLine));
                            }
                        }
                        getApplicationContext().deleteFile(".mixShortcuts");
                    }
                }
                if (getApplicationContext().getFileStreamPath(".superFreq").exists()) {
                    getApplicationContext().deleteFile(".superFreq");
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
                    if (!aPackageName.equals(getApplicationContext().getPackageName())) {
                        if (functionsClass.appInstalledOrNot(aPackageName)) {
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

    public void retrieveFrequentlyUsedApplications() throws Exception {
        List<String> freqApps = letMeKnow(NormalAppShortcutsSelectionList.this, 25, (86400000 * 7), System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            functionsClass.saveFileAppendLine(
                    ".superFreq",
                    freqApps.get(i));
        }
        functionsClass.addAppShortcutsFreqApps();
    }

    private class LoadApplicationsOff extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                recyclerViewApplicationsList.getRecycledViewPool().clear();

                navDrawerItems.clear();
                indexAppName.clear();
                sideIndex.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!PublicVariable.firstLoad) {
                autoSelect.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PackageManager packageManager = getApplicationContext().getPackageManager();
                Intent installedIntent = new Intent();
                installedIntent.setAction(Intent.ACTION_MAIN);
                installedIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> applicationInfoList = packageManager.queryIntentActivities(installedIntent, 0);

                if (functionsClass.customIconsEnable()) {
                    loadCustomIcons.load();
                    if (BuildConfig.DEBUG) {
                        FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                    }
                }

                Collections.sort(applicationInfoList, new ResolveInfo.DisplayNameComparator(packageManager));
                for (ResolveInfo resolveInfo : applicationInfoList) {
                    try {
                        if (packageManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName) != null) {
                            try {
                                PackageName = resolveInfo.activityInfo.packageName;
                                className = resolveInfo.activityInfo.name;
                                AppName = functionsClass.activityLabel(resolveInfo.activityInfo);
                                indexAppName.add(AppName);
                                AppIcon = functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.activityIcon(resolveInfo.activityInfo)) : functionsClass.activityIcon(resolveInfo.activityInfo);

                                navDrawerItems.add(new AdapterItemsData(
                                        AppName,
                                        PackageName,
                                        className,
                                        AppIcon
                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                selectionListAdapter = new SelectionListAdapter(NormalAppShortcutsSelectionList.this, getApplicationContext(), navDrawerItems);
                selectionListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            if (functionsClass.UsageAccessEnabled()) {
                return;
            }

            recyclerViewApplicationsList.setAdapter(selectionListAdapter);
            registerForContextMenu(recyclerViewApplicationsList);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Animation slideDown_select = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_button);
                    if (PublicVariable.firstLoad) {
                        PublicVariable.firstLoad = false;

                        autoSelect.startAnimation(slideDown_select);
                        slideDown_select.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                autoSelect.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);

                    if (resetAdapter == false) {
                        loadingSplash.startAnimation(anim);
                    }
                    getApplicationContext().sendBroadcast(new Intent(getApplicationContext().getString(R.string.visibilityAction)));

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                    counter.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            counter.setText(String.valueOf(functionsClass.countLine(NormalAppShortcutsSelectionListXYZ.NormalApplicationsShortcutsFile)));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            counter.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    PublicVariable.maxAppShortcutsCounter = functionsClass.countLine(NormalAppShortcutsSelectionListXYZ.NormalApplicationsShortcutsFile);
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
            sideIndex.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int navItem = 0; navItem < indexAppName.size(); navItem++) {
                try {
                    String index = (indexAppName.get(navItem)).substring(0, 1).toUpperCase();
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

            TextView textView = (TextView) getLayoutInflater()
                    .inflate(R.layout.side_index_item, null);
            ;
            List<String> indexList = new ArrayList<String>(mapIndex.keySet());
            for (String index : indexList) {
                textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.side_index_item, null);
                textView.setBackground(drawIndex);
                textView.setText(index.toUpperCase());
                textView.setTextColor(getColor(R.color.dark));
                sideIndex.addView(textView);
            }

            TextView finalTextView = textView;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int upperRange = (int) (sideIndex.getY() - finalTextView.getHeight());
                    for (int i = 0; i < sideIndex.getChildCount(); i++) {
                        String indexText = ((TextView) sideIndex.getChildAt(i)).getText().toString();
                        int indexRange = (int) (sideIndex.getChildAt(i).getY() + sideIndex.getY() + finalTextView.getHeight());
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
                                scrollListFav.smoothScrollTo(
                                        0,
                                        ((int) recyclerViewApplicationsList.getChildAt(mapIndex.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY()) - functionsClass.DpToInteger(37)
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
                                scrollListFav.smoothScrollTo(
                                        0,
                                        ((int) recyclerViewApplicationsList.getChildAt(mapIndex.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY())
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
