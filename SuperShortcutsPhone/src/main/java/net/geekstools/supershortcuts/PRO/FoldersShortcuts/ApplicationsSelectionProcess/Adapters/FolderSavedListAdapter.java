/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/14/20 2:41 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts.ApplicationsSelectionProcess.Adapters;

import android.app.Activity;
import android.content.Context;
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
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;

import java.util.ArrayList;

public class FolderSavedListAdapter extends BaseAdapter {

    private Context context;
    private Activity activity;

    FunctionsClass functionsClass;

    private ArrayList<AdapterItemsData> adapterItemsData;

    public FolderSavedListAdapter(Activity activity, Context context, ArrayList<AdapterItemsData> adapterItemsData) {
        this.activity = activity;
        this.context = context;
        this.adapterItemsData = adapterItemsData;

        functionsClass = new FunctionsClass(context);
    }

    @Override
    public int getCount() {
        return adapterItemsData.size();
    }

    @Override
    public Object getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_saved_app, null);
        }

        final RelativeLayout items = (RelativeLayout) convertView.findViewById(R.id.items);
        ImageView appIconItemView = (ImageView) convertView.findViewById(R.id.appIconItemView);
        TextView appNameItemView = (TextView) convertView.findViewById(R.id.appNameItemView);
        Button deleteItem = (Button) convertView.findViewById(R.id.deleteItem);

        appIconItemView.setImageDrawable(adapterItemsData.get(position).getAppIcon());
        appNameItemView.setText(adapterItemsData.get(position).getAppName());

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.deleteFile(adapterItemsData.get(position).getPackageName() + PublicVariable.categoryName);
                functionsClass.removeLine(PublicVariable.categoryName, adapterItemsData.get(position).getPackageName());
            }
        });

        return convertView;
    }
}
