/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 12:51 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.RemoteTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.geekstools.supershortcuts.PRO.FoldersShortcuts.LoadFolderPopupShortcuts;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices.SplitTransparentPair;


public class DeepLinkedShortcuts extends Activity {

    String shortcutInfo, shortcutName, shortcutType;

    //
    String htmlSymbol = "%20%7C%20";
    String htmlSlash = "/";

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        try {
            //https://www.geeksempire.net/FAQ/super.html/vhhjdfh%20kbokk*%7C*Split
            String incomingURI = getIntent().getDataString();

            shortcutInfo = incomingURI.substring(incomingURI.lastIndexOf(htmlSlash) + 1);
            shortcutName = shortcutInfo.split(htmlSymbol)[0];
            shortcutType = shortcutInfo.split(htmlSymbol)[1];
            if (shortcutType.equals("Category")) {
                Intent category = new Intent(getApplicationContext(), LoadFolderPopupShortcuts.class);
                category.setAction("load_category_action_shortcut");
                category.putExtra(Intent.EXTRA_TEXT, shortcutName);
                startActivity(category);
            } else if (shortcutType.equals("Split")) {
                Intent category = new Intent(getApplicationContext(), SplitTransparentPair.class);
                category.setAction("load_split_action_pair_shortcut");
                category.putExtra(Intent.EXTRA_TEXT, shortcutName);
                startActivity(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
