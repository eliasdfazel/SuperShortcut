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
