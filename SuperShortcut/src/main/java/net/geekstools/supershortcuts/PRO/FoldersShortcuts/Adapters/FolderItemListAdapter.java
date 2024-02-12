/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/14/20 2:37 PM
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
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices.SplitTransparentSingle;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;

import java.util.ArrayList;

public class FolderItemListAdapter extends BaseAdapter {

    private Context context;

    FunctionsClass functionsClass;

    private ArrayList<AdapterItemsData> adapterItemsData;

    private ListPopupWindow listPopupWindow;

    public FolderItemListAdapter(Context context, ArrayList<AdapterItemsData> adapterItemsData, ListPopupWindow listPopupWindow) {
        this.context = context;

        this.adapterItemsData = adapterItemsData;

        this.listPopupWindow = listPopupWindow;

        functionsClass = new FunctionsClass(context);
    }

    @Override
    public int getCount() {
        return adapterItemsData.size();
    }

    @Override
    public AdapterItemsData getItem(int position) {
        return adapterItemsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.popup_folder_items, null);
        }

        final RelativeLayout items = (RelativeLayout) convertView.findViewById(R.id.items);
        ImageView appIconItemView = (ImageView) convertView.findViewById(R.id.appIconItemView);
        TextView appNameItemView = (TextView) convertView.findViewById(R.id.appNameItemView);

        appIconItemView.setImageDrawable(adapterItemsData.get(position).getAppIcon());
        appNameItemView.setText(adapterItemsData.get(position).getAppName());

        items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adapterItemsData.get(position).getAppName().contains(context.getString(R.string.edit_advanced_shortcut))) {

                    context.startActivity(new Intent(context, FolderShortcuts.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                } else {

                    functionsClass.openApplication(adapterItemsData.get(position).getPackageName());

                }

                listPopupWindow.dismiss();
            }
        });

        items.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                context.startActivity(new Intent(context, SplitTransparentSingle.class)
                        .putExtra("package", adapterItemsData.get(position).getPackageName())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                listPopupWindow.dismiss();

                return true;
            }
        });

        return convertView;
    }
}
