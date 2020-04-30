/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 11:45 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;

import java.util.ArrayList;

public class SavedApplicationsListAdapter extends BaseAdapter {

    private Context context;
    private Activity activity;

    FunctionsClass functionsClass;

    private ArrayList<AdapterItemsData> navDrawerItems;

    public SavedApplicationsListAdapter(Activity activity, Context context, ArrayList<AdapterItemsData> navDrawerItems) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        functionsClass = new FunctionsClass(context);
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_saved_app, null);
        }

        RelativeLayout items = (RelativeLayout) convertView.findViewById(R.id.items);
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.iconItem);
        TextView textAppName = (TextView) convertView.findViewById(R.id.itemAppName);
        Button deleteItem = (Button) convertView.findViewById(R.id.deleteItem);

        imgIcon.setImageDrawable(navDrawerItems.get(position).getAppIcon());
        textAppName.setText(navDrawerItems.get(position).getAppName());

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appToDelete = functionsClass.appPackageNameClassName(navDrawerItems.get(position).getPackageName(), navDrawerItems.get(position).getClassName());
                context.deleteFile(appToDelete + ".Super");
                functionsClass.removeLine(".autoSuper", appToDelete);
                if (functionsClass.mixShortcuts() == true) {
                    functionsClass.removeLine(".mixShortcuts", appToDelete);
                }
                context.sendBroadcast(new Intent(context.getString(R.string.checkboxAction)));
                context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));
                context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcuts)));
            }
        });

        return convertView;
    }
}
