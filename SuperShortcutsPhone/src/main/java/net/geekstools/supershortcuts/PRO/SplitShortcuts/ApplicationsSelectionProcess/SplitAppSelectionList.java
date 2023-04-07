/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 6/11/20 10:13 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.ApplicationsSelectionProcess;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.ApplicationsSelectionProcess.Adapters.SplitSelectionListAdapter;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.UI.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.databinding.SplitAppSelectionListBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplitAppSelectionList extends AppCompatActivity implements View.OnClickListener {

    FunctionsClass functionsClass;

    RecyclerView.Adapter splitSelectionListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    List<String> listOfNewCharOfItemsForIndex;
    ArrayList<AdapterItemsData> installedApplicationsList;

    String PackageName;
    String AppName = "Application";
    Drawable AppIcon;

    boolean resetAdapter = false;

    LoadCustomIcons loadCustomIcons;

    SplitAppSelectionListBinding splitAppSelectionListBinding;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        splitAppSelectionListBinding = SplitAppSelectionListBinding.inflate(getLayoutInflater());
        setContentView(splitAppSelectionListBinding.getRoot());


        functionsClass = new FunctionsClass(getApplicationContext());
        PublicVariable.SplitMaxAppShortcuts = 2;

        splitAppSelectionListBinding.temporaryFallingIcon.bringToFront();
        splitAppSelectionListBinding.confirmButton.bringToFront();


        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        splitAppSelectionListBinding.recyclerViewList.setLayoutManager(recyclerViewLayoutManager);

        splitAppSelectionListBinding.nestedScrollView.setSmoothScrollingEnabled(true);

        splitAppSelectionListBinding.MainView.setBackgroundColor(getColor(R.color.light));

        splitAppSelectionListBinding.confirmButtonFolderName.setText(PublicVariable.categoryName.split("_")[0]);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.light));
        window.setNavigationBarColor(getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        installedApplicationsList = new ArrayList<AdapterItemsData>();
        listOfNewCharOfItemsForIndex = new ArrayList<String>();

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        splitAppSelectionListBinding.loadingDescription.setTypeface(face);
        splitAppSelectionListBinding.loadingDescription.setText(PublicVariable.categoryName.split("_")[0]);

        splitAppSelectionListBinding.loadingProgress.getIndeterminateDrawable().setColorFilter(getColor(R.color.default_color), PorterDuff.Mode.MULTIPLY);

        loadDataOff();
    }

    @Override
    public void onStart() {
        super.onStart();

        splitAppSelectionListBinding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(SplitAppSelectionList.this, SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_down));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        splitAppSelectionListBinding.confirmButtonFolderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(SplitAppSelectionList.this, SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_down));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            PublicVariable.setAppIndex = PublicVariable.categoryName + " | " + "Split";
            PublicVariable.setAppIndexUrl = String.valueOf(PublicVariable.BASE_URL.buildUpon().appendPath(PublicVariable.setAppIndex).build());

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
            installedApplicationsList.clear();
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
                splitAppSelectionListBinding.recyclerViewList.getRecycledViewPool().clear();
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
                        FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIconsNumber());
                    }
                }

                Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(packageManager));
                for (ApplicationInfo applicationInfo : applicationInfoList) {
                    try {
                        if (packageManager.getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                            try {
                                PackageName = applicationInfo.packageName;
                                AppName = functionsClass.appName(PackageName);
                                listOfNewCharOfItemsForIndex.add(AppName);
                                AppIcon = functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.appIconDrawable(PackageName)) : functionsClass.appIconDrawable(PackageName);

                                installedApplicationsList.add(new AdapterItemsData(AppName, PackageName, AppIcon));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                splitSelectionListAdapter = new SplitSelectionListAdapter(SplitAppSelectionList.this, installedApplicationsList);
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
            splitAppSelectionListBinding.recyclerViewList.setAdapter(splitSelectionListAdapter);
            registerForContextMenu(splitAppSelectionListBinding.recyclerViewList);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    splitAppSelectionListBinding.loadingSplash.setVisibility(View.INVISIBLE);
                    if (!resetAdapter) {
                        splitAppSelectionListBinding.loadingSplash.startAnimation(anim);
                    }
                    sendBroadcast(new Intent(getString(R.string.visibilityActionSplit)));

                    PublicVariable.SplitMaxAppShortcutsCounter = functionsClass.countLine(PublicVariable.categoryName);
                    resetAdapter = false;
                }
            }, 100);

            /*Indexed Popup Fast Scroller*/
//            IndexedFastScroller indexedFastScroller = new IndexedFastScroller(
//                    getApplicationContext(),
//                    getLayoutInflater(),
//                    splitAppSelectionListBinding.MainView,
//                    splitAppSelectionListBinding.nestedScrollView,
//                    splitAppSelectionListBinding.recyclerViewList,
//                    splitAppSelectionListBinding.fastScrollerIndexInclude,
//                    new IndexedFastScrollerFactory());
//            indexedFastScroller.initializeIndexView().getOnAwait();
//            indexedFastScroller.loadIndexData(listOfNewCharOfItemsForIndex).getOnAwait();
            /*Indexed Popup Fast Scroller*/
        }
    }
}
