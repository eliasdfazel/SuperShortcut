/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/8/20 11:40 AM
 * Last modified 3/8/20 11:40 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Util.IAP;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import net.geekstools.floatshort.PRO.Util.IAP.Util.PurchasesCheckpoint;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.IAP.billing.BillingManager;
import net.geekstools.supershortcuts.PRO.Util.IAP.billing.BillingProvider;

public class InAppBilling extends AppCompatActivity implements BillingProvider {

    private static final String TAG = "InAppBilling";
    private static final String DIALOG_TAG = "InAppBillingDialogue";

    FunctionsClass functionsClass;

    private BillingManager billingManager;
    private AcquireFragment acquireFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.GeeksEmpire_Material_IAP_LIGHT);

        functionsClass = new FunctionsClass(getApplicationContext(), InAppBilling.this);

        if (savedInstanceState != null) {
            acquireFragment = (AcquireFragment) getFragmentManager().findFragmentByTag(DIALOG_TAG);
        }

        billingManager = new BillingManager(InAppBilling.this, getIntent().hasExtra("UserEmailAddress") ? getIntent().getStringExtra("UserEmailAddress") : null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                proceedToPurchaseFragment();
            }
        }, 777);

        showRefreshedUi();
    }

    @Override
    public void onResume() {
        super.onResume();

        new PurchasesCheckpoint(InAppBilling.this).trigger();
    }

    @Override
    public BillingManager getBillingManager() {
        return billingManager;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    public void proceedToPurchaseFragment() {
        if (acquireFragment == null) {
            acquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            acquireFragment.show(getFragmentManager(), DIALOG_TAG);
        }
    }

    public void showRefreshedUi() {
        if (isAcquireFragmentShown()) {
            acquireFragment.refreshUI();
        }
    }

    public boolean isAcquireFragmentShown() {
        return acquireFragment != null && acquireFragment.isVisible();
    }
}
