package net.geekstools.supershortcuts.PRO.advanced;

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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.Configurations;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.IAP.InAppBilling;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.Util.SettingGUI;
import net.geekstools.supershortcuts.PRO.Util.SimpleGestureFilterSwitch;
import net.geekstools.supershortcuts.PRO.advanced.nav.AdvanceShortcutsAdapter;
import net.geekstools.supershortcuts.PRO.normal.NormalAppSelectionList;
import net.geekstools.supershortcuts.PRO.split.SplitShortcuts;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AdvanceShortcuts extends Activity implements View.OnClickListener, SimpleGestureFilterSwitch.SimpleGestureListener {

    Activity activity;
    Context context;
    FunctionsClass functionsClass;

    RecyclerView.Adapter advanceShortcutsAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager recyclerViewLayoutManager;

    RelativeLayout wholeAuto, confirmLayout, loadingSplash;
    LinearLayout autoSelect;
    TextView desc, counterView;
    ImageView loadIcon;
    Button confirmButton, apps, split, categories;
    ProgressBar loadingBarLTR;

    MenuItem mixShortcutsMenuItem;

    String[] appData;
    ArrayList<NavDrawerItem> navDrawerItems;

    int limitCounter;
    BroadcastReceiver counterReceiver;
    boolean resetAdapter = false;

    SimpleGestureFilterSwitch simpleGestureFilterSwitch;

    FirebaseRemoteConfig firebaseRemoteConfig;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        setContentView(R.layout.advance_shortcuts_view);

        simpleGestureFilterSwitch = new SimpleGestureFilterSwitch(getApplicationContext(), this);
        functionsClass = new FunctionsClass(getApplicationContext(), this);
        functionsClass.ChangeLog(false);
        if (functionsClass.mixShortcuts() == true) {
            PublicVariable.advanceShortcutsMaxAppShortcuts
                    = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
            getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.advanceShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            limitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".categorySuperSelected");
            getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + limitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
            PublicVariable.advanceShortcutsMaxAppShortcuts = functionsClass.getSystemMaxAppShortcut();
        }

        context = getApplicationContext();
        activity = this;

        desc = (TextView) findViewById(R.id.desc);
        counterView = (TextView) findViewById(R.id.counter);
        loadIcon = (ImageView) findViewById(R.id.loadLogo);
        wholeAuto = (RelativeLayout) findViewById(R.id.wholeAuto);
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        confirmLayout = (RelativeLayout) findViewById(R.id.confirmLayout);
        confirmLayout.bringToFront();
        confirmButton = (Button) findViewById(R.id.confirmButton);
        autoSelect = (LinearLayout) findViewById(R.id.autoSelect);
        apps = (Button) findViewById(R.id.autoApps);
        split = (Button) findViewById(R.id.autoSplit);
        categories = (Button) findViewById(R.id.autoCategories);

        recyclerView = (RecyclerView) findViewById(R.id.categoryList);
        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        wholeAuto.setBackgroundColor(getColor(R.color.light));
        getActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color)));
        getActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + getString(R.string.app_name) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.default_color));
        window.setNavigationBarColor(getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        navDrawerItems = new ArrayList<NavDrawerItem>();

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        desc.setTypeface(face);
        counterView.setTypeface(face);
        counterView.bringToFront();

        loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
        loadingBarLTR.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.counterActionAdvanceShortcuts));
        intentFilter.addAction(context.getString(R.string.checkboxActionAdvanceShortcuts));
        intentFilter.addAction(context.getString(R.string.dynamicShortcutsAdvance));
        counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.counterActionAdvanceShortcuts))) {
                    counterView.setText(String.valueOf(functionsClass.countLine(".categorySuperSelected")));
                } else if ((intent.getAction().equals(getString(R.string.checkboxActionAdvanceShortcuts)))) {
                    resetAdapter = true;
                    loadCategoryData();
                } else if (intent.getAction().equals(getString(R.string.dynamicShortcutsAdvance))) {
                    if (functionsClass.mixShortcuts() == true) {
                        PublicVariable.advanceShortcutsMaxAppShortcuts
                                = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
                        getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.advanceShortcutsMaxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        limitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".categorySuperSelected");
                        getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + limitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
                        PublicVariable.advanceShortcutsMaxAppShortcuts = functionsClass.getSystemMaxAppShortcut();
                    }
                }
            }
        };
        context.registerReceiver(counterReceiver, intentFilter);

        RippleDrawable drawApps = (RippleDrawable) getDrawable(R.drawable.auto_apps_enable);
        GradientDrawable backApps = (GradientDrawable) drawApps.findDrawableByLayerId(R.id.category_item);
        GradientDrawable backAppsRipple = (GradientDrawable) drawApps.findDrawableByLayerId(android.R.id.mask);
        backApps.setColor(getColor(R.color.default_color_darker));
        backAppsRipple.setColor(getColor(R.color.default_color));
        drawApps.setColor(ColorStateList.valueOf(getColor(R.color.default_color)));
        apps.setBackground(drawApps);

        RippleDrawable drawSplit = (RippleDrawable) getDrawable(R.drawable.auto_split_enable);
        GradientDrawable backSplit = (GradientDrawable) drawSplit.findDrawableByLayerId(R.id.category_item);
        GradientDrawable backSplitRipple = (GradientDrawable) drawSplit.findDrawableByLayerId(android.R.id.mask);
        backSplit.setColor(getColor(R.color.default_color_darker));
        backSplitRipple.setColor(getColor(R.color.default_color_darker));
        drawSplit.setColor(ColorStateList.valueOf(getColor(R.color.default_color_darker)));
        split.setBackground(drawSplit);

        RippleDrawable drawCategories = (RippleDrawable) getDrawable(R.drawable.auto_categories_enable);
        GradientDrawable backCategories = (GradientDrawable) drawCategories.findDrawableByLayerId(R.id.category_item);
        GradientDrawable backCategoriesRipple = (GradientDrawable) drawCategories.findDrawableByLayerId(android.R.id.mask);
        backCategories.setColor(getColor(R.color.default_color));
        backCategoriesRipple.setColor(getColor(R.color.default_color_darker));
        drawCategories.setColor(ColorStateList.valueOf(getColor(R.color.default_color_darker)));
        categories.setBackground(drawCategories);

        loadCategoryData();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(AdvanceShortcuts.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                        getActionBar().setDisplayHomeAsUpEnabled(true);
                                        LayerDrawable layerDrawableNewUpdate = (LayerDrawable) getDrawable(R.drawable.ic_update);
                                        BitmapDrawable gradientDrawableNewUpdate = (BitmapDrawable) layerDrawableNewUpdate.findDrawableByLayerId(R.id.ic_launcher_back_layer);
                                        gradientDrawableNewUpdate.setTint(getColor(R.color.default_color_light));

                                        Bitmap tempBitmap = functionsClass.drawableToBitmap(layerDrawableNewUpdate);
                                        Bitmap scaleBitmap = Bitmap.createScaledBitmap(tempBitmap, tempBitmap.getWidth() / 4, tempBitmap.getHeight() / 4, false);
                                        Drawable logoDrawable = new BitmapDrawable(getResources(), scaleBitmap);
                                        activity.getActionBar().setHomeAsUpIndicator(logoDrawable);

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
    public void onStart() {
        super.onStart();
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (functionsClass.mixShortcuts() == true) {
                    functionsClass.addMixAppShortcuts();
                } else {
                    functionsClass.addAppsShortcutCategory();
                    SharedPreferences sharedPreferences = context.getSharedPreferences(".PopupShortcut", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("PopupShortcutMode", "CategoryShortcuts");
                    editor.apply();
                }
            }
        });
        confirmButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    functionsClass.deleteSelectedFiles();

                    context.sendBroadcast(new Intent(context.getString(R.string.checkboxActionAdvanceShortcuts)));
                    context.sendBroadcast(new Intent(context.getString(R.string.counterActionAdvanceShortcuts)));
                    context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcutsAdvance)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                functionsClass.clearDynamicShortcuts();
                return true;
            }
        });
        apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(NormalAppSelectionList.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TabsView", AdvanceShortcuts.class.getSimpleName());
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
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public void onClick(View view) {
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
                System.out.println("Swipe Right");
                try {
                    functionsClass.overrideBackPress(SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
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

        if (functionsClass.mixShortcutssPurchased()) {
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
                startActivity(new Intent(getApplicationContext(), SettingGUI.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.up_down, android.R.anim.fade_out).toBundle());
                finish();
                break;
            }
            case R.id.mixShortcuts: {
                if (functionsClass.mixShortcutssPurchased()) {
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

                    Intent intent = new Intent(getApplicationContext(), Configurations.class);
                    startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                } else {
                    startActivity(new Intent(getApplicationContext(), InAppBilling.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                break;
            }
            case android.R.id.home: {
                functionsClass.upcomingChangeLog(
                        AdvanceShortcuts.this,
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

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    File betaFile = new File("/data/data/" + getPackageName() + "/shared_prefs/.UserInformation.xml");
                                                    Uri uriBetaFile = Uri.fromFile(betaFile);
                                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

                                                    StorageReference storageReference = firebaseStorage.getReference("/Users/" + "API" + functionsClass.returnAPI() + "/" +
                                                            functionsClass.readPreference(".UserInformation", "userEmail", null));
                                                    UploadTask uploadTask = storageReference.putFile(uriBetaFile);

                                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            exception.printStackTrace();
                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 333);

                                        if (!functionsClass.mixShortcutssPurchased()) {
                                            BillingClient billingClient = BillingClient.newBuilder(AdvanceShortcuts.this).setListener(new PurchasesUpdatedListener() {
                                                @Override
                                                public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                                                    if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {

                                                    } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {

                                                    } else {

                                                    }

                                                }
                                            }).build();
                                            billingClient.startConnection(new BillingClientStateListener() {
                                                @Override
                                                public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                                                    if (billingResponseCode == BillingClient.BillingResponse.OK) {
                                                        List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                                                        for (Purchase purchase : purchases) {
                                                            System.out.println("*** Purchased Item: " + purchase + " ***");
                                                            if (purchase.getSku().equals("mix.shortcuts")) {
                                                                functionsClass.savePreference(".PurchasedItem", "MixShortcuts", true);

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

    public void loadCategoryData() {
        LoadInstalledCustomIcons loadInstalledCustomIcons = new LoadInstalledCustomIcons();
        loadInstalledCustomIcons.execute();

        if (functionsClass.UsageAccessEnabled()) {
            loadingBarLTR.setVisibility(View.INVISIBLE);
            loadIcon.setImageDrawable(getDrawable(R.drawable.draw_smart));
            desc.setText(Html.fromHtml(getString(R.string.smartInfo), Html.FROM_HTML_MODE_LEGACY));

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
                    if (context.getFileStreamPath(".mixShortcuts").exists()) {
                        String[] mixContent = functionsClass.readFileLine(".mixShortcuts");
                        for (String mixLine : mixContent) {
                            if (mixLine.contains(".CategorySelected")) {
                                context.deleteFile(functionsClass.categoryNameSelected(mixLine));
                            } else if (mixLine.contains(".SplitSelected")) {
                                context.deleteFile(functionsClass.splitNameSelected(mixLine));
                            } else {
                                context.deleteFile(functionsClass.packageNameSelected(mixLine));
                            }
                        }
                        context.deleteFile(".mixShortcuts");
                    }
                }
                if (context.getFileStreamPath(".superFreq").exists()) {
                    context.deleteFile(".superFreq");
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
                    if (!aPackageName.equals(context.getPackageName())) {
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

    public void retrieveFreqUsedApp() throws Exception {
        List<String> freqApps = letMeKnow(activity, 25, (86400000 * 7), System.currentTimeMillis());
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
                Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                loadingSplash.setVisibility(View.INVISIBLE);
                loadingSplash.startAnimation(anim);
            }

            if (!PublicVariable.firstLoad) {
                autoSelect.setVisibility(View.VISIBLE);
            }

            try {
                recyclerView.getRecycledViewPool().clear();
                navDrawerItems.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!getFileStreamPath(".categorySuper").exists()) {
                navDrawerItems = new ArrayList<NavDrawerItem>();
                navDrawerItems.add(new NavDrawerItem(getPackageName(), new String[]{getPackageName()}));
                advanceShortcutsAdapter = new AdvanceShortcutsAdapter(activity, context, navDrawerItems);
            } else {
                try {
                    appData = functionsClass.readFileLine(".categorySuper");
                    navDrawerItems = new ArrayList<NavDrawerItem>();
                    for (int navItem = 0; navItem < appData.length; navItem++) {
                        try {
                            navDrawerItems.add(new NavDrawerItem(
                                    appData[navItem],
                                    functionsClass.readFileLine(appData[navItem])));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    navDrawerItems.add(new NavDrawerItem(getPackageName(), new String[]{getPackageName()}));
                    advanceShortcutsAdapter = new AdvanceShortcutsAdapter(activity, context, navDrawerItems);
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
            recyclerView.setAdapter(advanceShortcutsAdapter);

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

                    Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);

                    if (resetAdapter == false) {
                        loadingSplash.startAnimation(anim);
                    }
                    confirmButton.setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    counterView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            counterView.setText(String.valueOf(functionsClass.countLine(".categorySuperSelected")));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            counterView.setVisibility(View.VISIBLE);
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
                        System.out.println("*** CustomIconPackages ::: " + resolveInfo.activityInfo.packageName);
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
