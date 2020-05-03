/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/3/20 9:22 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts.Adapters;

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

import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitTransparentSingle;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;

import java.util.ArrayList;

public class FolderItemListAdapter extends BaseAdapter {

    private Context context;
    private Activity activity;

    FunctionsClass functionsClass;

    private ArrayList<AdapterItemsData> navDrawerItems;

    private ListPopupWindow listPopupWindow;

    public FolderItemListAdapter(Activity activity, Context context, ArrayList<AdapterItemsData> navDrawerItems, ListPopupWindow listPopupWindow) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.listPopupWindow = listPopupWindow;

        functionsClass = new FunctionsClass(context);
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public AdapterItemsData getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_category_apps, null);
        }

        final RelativeLayout items = (RelativeLayout) convertView.findViewById(R.id.items);
        ImageView iconItem = (ImageView) convertView.findViewById(R.id.iconItem);
        TextView itemAppName = (TextView) convertView.findViewById(R.id.itemAppName);

        iconItem.setImageDrawable(navDrawerItems.get(position).getAppIcon());
        itemAppName.setText(navDrawerItems.get(position).getAppName());

        items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (navDrawerItems.get(position).getAppName().contains(context.getString(R.string.edit_advanced_shortcut))) {
                        context.startActivity(new Intent(context, FolderShortcuts.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        String packageName = navDrawerItems.get(position).getPackageName();
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
                String packageName = navDrawerItems.get(position).getPackageName();
                context.startActivity(new Intent(context, SplitTransparentSingle.class).putExtra("package", packageName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            }
        });

        return convertView;
    }
}
