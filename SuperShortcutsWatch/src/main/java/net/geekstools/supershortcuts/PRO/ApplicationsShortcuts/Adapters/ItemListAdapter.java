/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/29/20 11:41 AM
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
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppSelectionList;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

    private Context context;
    private Activity activity;

    FunctionsClass functionsClass;

    private ArrayList<NavDrawerItem> navDrawerItems;

    private ListPopupWindow listPopupWindow;

    public ItemListAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems, ListPopupWindow listPopupWindow) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.listPopupWindow = listPopupWindow;

        functionsClass = new FunctionsClass(context, activity);
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
            convertView = mInflater.inflate(R.layout.item_popup_apps, null);
        }

        final RelativeLayout items = (RelativeLayout) convertView.findViewById(R.id.items);
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.iconItem);
        TextView textAppName = (TextView) convertView.findViewById(R.id.itemAppName);

        if (functionsClass.appInstalledOrNot(navDrawerItems.get(position).getTitle())) {
            imgIcon.setImageDrawable(navDrawerItems.get(position).getIcon());
            textAppName.setText(navDrawerItems.get(position).getDesc());
        } else {
        }

        items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (navDrawerItems.get(position).getDesc().contains(context.getString(R.string.edit_advanced_shortcut))) {
                        context.startActivity(new Intent(context, NormalAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        String packageName = navDrawerItems.get(position).getTitle();
                        functionsClass.openApplication(packageName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listPopupWindow.dismiss();
            }
        });
        items.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    String packageName = navDrawerItems.get(position).getTitle();
                    functionsClass.goToSettingInfo(packageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        return convertView;
    }
}
