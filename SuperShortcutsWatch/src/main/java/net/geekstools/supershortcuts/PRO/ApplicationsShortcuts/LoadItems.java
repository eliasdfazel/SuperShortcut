/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 12:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;

public class LoadItems extends WearableActivity {

    FunctionsClass functionsClass;

    RelativeLayout wholeLow, popupAnchorView;

    String[] packagesName;
    String categoryName;

    FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        FirebaseApp.initializeApp(getApplicationContext());

        setContentView(R.layout.anchor_show_ui);
        setAmbientEnabled();

        functionsClass = new FunctionsClass(getApplicationContext(), LoadItems.this);

        wholeLow = (RelativeLayout) findViewById(R.id.wholeLow);
        popupAnchorView = (RelativeLayout) findViewById(R.id.popupAnchorView);

        if (getFileStreamPath(".autoSuper").exists() && functionsClass.countLine(".autoSuper") > 0) {
            packagesName = new String[functionsClass.countLine(".autoSuper")];
            packagesName = functionsClass.readFileLine(".autoSuper");
            categoryName = getString(R.string.app_name);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    functionsClass.showPopupItem(popupAnchorView, categoryName, packagesName);
                }
            }, 250);

            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setFetchTimeoutInSeconds(0)
                    .build();
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);
            firebaseRemoteConfig.fetch()
                    .addOnCompleteListener(LoadItems.this, new OnCompleteListener<Void>() {
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
        } else {
            startActivity(new Intent(getApplicationContext(), NormalAppSelectionList.class));
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
