/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 7:53 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

public class LoadCategoryItems extends Activity {

    FunctionsClass functionsClass;

    RelativeLayout wholeLow, popupAnchorView;

    Intent intent;
    String categoryName;

    LoadCustomIcons loadCustomIcons;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.category_show_ui);
        functionsClass = new FunctionsClass(getApplicationContext());

        intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("load_category_action")) {
                    categoryName = intent.getStringExtra("categoryName");
                } else if (intent.getAction().equals("load_category_action_shortcut")) {
                    categoryName = intent.getStringExtra(Intent.EXTRA_TEXT);
                }
            } else {
                finish();
                return;
            }
        } else {
            finish();
            return;
        }

        wholeLow = (RelativeLayout) findViewById(R.id.wholeLow);
        popupAnchorView = (RelativeLayout) findViewById(R.id.popupAnchorView);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (intent.getAction().equals("load_category_action")) {
                    functionsClass.showPopupCategoryItem(LoadCategoryItems.this, popupAnchorView, categoryName.replace(".CategorySelected", ""), loadCustomIcons);
                } else if (intent.getAction().equals("load_category_action_shortcut")) {
                    functionsClass.showPopupCategoryItem(LoadCategoryItems.this, popupAnchorView, categoryName, loadCustomIcons);
                }
            }
        }, 250);

        wholeLow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                return true;
            }
        });
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
