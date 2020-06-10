/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 6/10/20 1:05 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Preferences;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionListPhone;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClassDialogues;
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling;
import net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData;
import net.geekstools.supershortcuts.PRO.databinding.PreferenceViewBinding;

public class PreferencesUI extends AppCompatActivity {

    FunctionsClass functionsClass;


    PreferenceViewBinding preferenceViewBinding;



    @Override
    public void onStart() {
        super.onStart();















    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(PreferencesUI.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                        new FunctionsClassDialogues(PreferencesUI.this, functionsClass).changeLogPreference(
                                                firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()),
                                                String.valueOf(firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()))
                                        );
                                    } else {

                                    }
                                    if (firebaseRemoteConfig.getBoolean("boolean_new_floating_shortcuts_pref_desc")) {
                                        preferenceViewBinding.prefDescfloating.setText(Html.fromHtml(firebaseRemoteConfig.getString("string_floating_shortcuts_pref_desc"), Html.FROM_HTML_MODE_LEGACY));
                                    }
                                }
                            });
                        } else {

                        }
                    }
                });

        SharedPreferences.Editor editor = getSharedPreferences("smart", MODE_PRIVATE).edit();

        if (functionsClass.UsageAccessEnabled()) {
            preferenceViewBinding.prefSwitch.setChecked(true);
            editor.putBoolean("smartPick", true);
            editor.apply();
        } else {
            preferenceViewBinding.prefSwitch.setChecked(false);
            editor.putBoolean("smartPick", false);
            editor.apply();
        }

        if (functionsClass.AccessibilityServiceEnabled()) {
            preferenceViewBinding.splitSwitch.setChecked(true);
        } else {
            preferenceViewBinding.splitSwitch.setChecked(false);
        }

        if (getSharedPreferences("mix", MODE_PRIVATE).getBoolean("mixShortcuts", false)) {
            preferenceViewBinding.mixSwitch.setChecked(true);
        } else if (!getSharedPreferences("mix", MODE_PRIVATE).getBoolean("mixShortcuts", false)) {
            preferenceViewBinding.mixSwitch.setChecked(false);
        }

        preferenceViewBinding.customIconIcon.setImageDrawable(functionsClass.customIconsEnable() ? functionsClass.appIconDrawable(functionsClass.readDefaultPreference("customIcon", getPackageName())) : getDrawable(R.drawable.draw_pref_custom_icon));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String tabView = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE).getString("TabsView", NormalAppShortcutsSelectionListPhone.class.getSimpleName());
        if (tabView.equals(NormalAppShortcutsSelectionListPhone.class.getSimpleName())) {
            startActivity(new Intent(getApplicationContext(), NormalAppShortcutsSelectionListPhone.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
        } else if (tabView.equals(SplitShortcuts.class.getSimpleName())) {
            startActivity(new Intent(getApplicationContext(), SplitShortcuts.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());

        } else if (tabView.equals(FolderShortcuts.class.getSimpleName())) {
            startActivity(new Intent(getApplicationContext(), FolderShortcuts.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
        } else {
            startActivity(new Intent(getApplicationContext(), NormalAppShortcutsSelectionListPhone.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
        }

        PreferencesUI.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_menu, menu);

        MenuItem gift = menu.findItem(R.id.donate);

        if (!functionsClass.alreadyDonated()) {

        } else {
            gift.setVisible(false);
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
            case R.id.facebook: {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app))));
                break;
            }
            case R.id.donate: {

                startActivity(new Intent(getApplicationContext(), InitializeInAppBilling.class)
                                .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                                .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemDonation)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        , ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.down_up, android.R.anim.fade_out).toBundle());

                break;
            }
            case android.R.id.home: {

                String tabView = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE).getString("TabsView", NormalAppShortcutsSelectionListPhone.class.getSimpleName());
                if (tabView.equals(NormalAppShortcutsSelectionListPhone.class.getSimpleName())) {
                    startActivity(new Intent(getApplicationContext(), NormalAppShortcutsSelectionListPhone.class),
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
                } else if (tabView.equals(SplitShortcuts.class.getSimpleName())) {
                    startActivity(new Intent(getApplicationContext(), SplitShortcuts.class),
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());

                } else if (tabView.equals(FolderShortcuts.class.getSimpleName())) {
                    startActivity(new Intent(getApplicationContext(), FolderShortcuts.class),
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
                } else {
                    startActivity(new Intent(getApplicationContext(), NormalAppShortcutsSelectionListPhone.class),
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
                }

                PreferencesUI.this.finish();

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /* Functions */
    private void shareSuperShortcuts() {
        String shareText = getString(R.string.invitation_title) +
                "\n" + getString(R.string.invitation_message) +
                "\n" + getString(R.string.play_store_link) + getPackageName();
        Intent s = new Intent(Intent.ACTION_SEND);
        s.putExtra(Intent.EXTRA_TEXT, shareText);
        s.setType("text/plain");
        startActivity(s);
    }
}
