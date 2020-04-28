/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 12:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.RemoteTask;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;
import android.support.wearable.complications.ComplicationProviderService;
import android.support.wearable.complications.ComplicationText;

import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.LoadItems;
import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;

public class RecoveryComplication extends ComplicationProviderService {

    FunctionsClass functionsClass;

    @Override
    public void onComplicationActivated(int complicationId, int dataType, ComplicationManager complicationManager) {
        if (BuildConfig.DEBUG) {
            System.out.println("onComplicationActivated " + dataType);
        }
        functionsClass = new FunctionsClass(getApplicationContext());
        functionsClass.savePreference("ComplicationProviderService", "ComplicationedId", complicationId);
    }

    @Override
    public void onComplicationUpdate(int complicationId, int type, ComplicationManager manager) {
        if (type == ComplicationData.TYPE_LARGE_IMAGE || type == ComplicationData.TYPE_ICON || type == ComplicationData.TYPE_RANGED_VALUE) {
            functionsClass = new FunctionsClass(getApplicationContext());
            functionsClass.savePreference("ComplicationProviderService", "ComplicationedId", complicationId);

            Intent recoveryIntent = new Intent(getApplicationContext(), LoadItems.class);
            recoveryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent complicationTogglePendingIntent = PendingIntent
                    .getActivity(getApplicationContext(), 666, recoveryIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            ComplicationData complicationData;
            switch (type) {
                case ComplicationData.TYPE_LARGE_IMAGE://8
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_LARGE_IMAGE)
                            .setLargeImage(Icon.createWithResource(this, R.drawable.ic_launcher))
                            .setTapAction(complicationTogglePendingIntent)
                            .build();
                    break;
                case ComplicationData.TYPE_ICON://6
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_ICON)
                            .setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_notification))
                            .setTapAction(complicationTogglePendingIntent)
                            .build();
                    break;
                case ComplicationData.TYPE_RANGED_VALUE://5
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_RANGED_VALUE)
                            .setShortText(ComplicationText.plainText(getString(R.string.recoveryEmoji)))
                            .setMinValue(0)
                            .setMaxValue(functionsClass.readPreference("InstalledApps", "countApps", getPackageManager().getInstalledApplications(0).size()))
                            .setValue(functionsClass.countLine(".autoSuper"))
                            .setTapAction(complicationTogglePendingIntent)
                            .build();
                    break;
                default:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_ICON)
                            .setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_notification))
                            .setTapAction(complicationTogglePendingIntent)
                            .build();
                    break;
            }
            manager.updateComplicationData(complicationId, complicationData);
        }
    }

    @Override
    public void onComplicationDeactivated(int complicationId) {
        System.out.println("onComplicationDeactivated");
    }
}