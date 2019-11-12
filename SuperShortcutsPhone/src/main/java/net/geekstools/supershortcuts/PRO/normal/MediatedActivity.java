/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:22 PM
 * Last modified 11/11/19 7:21 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.normal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MediatedActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        String packageName = getIntent().getStringExtra("PackageName");

        Intent intent = new Intent();
        intent.setPackage(getIntent().getStringExtra("PackageName"));
        if (getIntent().hasExtra("ClassName")) {
            intent.setClassName(packageName, getIntent().getStringExtra("ClassName"));
        }
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);

        finish();
    }
}
