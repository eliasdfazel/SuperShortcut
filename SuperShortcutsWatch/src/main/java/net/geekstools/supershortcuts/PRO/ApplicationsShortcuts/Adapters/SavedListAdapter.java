/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 9:34 AM
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

public class SavedListAdapter extends BaseAdapter {

    private Context context;

    FunctionsClass functionsClass;

    private ArrayList<AdapterItemsData> adapterItemsData;

    public SavedListAdapter(Context context, ArrayList<AdapterItemsData> adapterItemsData) {
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
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.iconItem);
        TextView textAppName = (TextView) convertView.findViewById(R.id.itemAppName);
        Button deleteItem = (Button) convertView.findViewById(R.id.deleteItem);

        imgIcon.setImageDrawable(adapterItemsData.get(position).getIcon());
        textAppName.setText(adapterItemsData.get(position).getDesc());

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.deleteFile(
                        adapterItemsData.get(position).getTitle() + ".Super");
                functionsClass.removeLine(".autoSuper", adapterItemsData.get(position).getTitle());
                context.sendBroadcast(new Intent(context.getString(R.string.checkboxAction)));
                context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.deleteFile(
                        adapterItemsData.get(position).getTitle() + ".Super");
                functionsClass.removeLine(".autoSuper", adapterItemsData.get(position).getTitle());
                context.sendBroadcast(new Intent(context.getString(R.string.checkboxAction)));
                context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));
            }
        });

        return convertView;
    }
}
