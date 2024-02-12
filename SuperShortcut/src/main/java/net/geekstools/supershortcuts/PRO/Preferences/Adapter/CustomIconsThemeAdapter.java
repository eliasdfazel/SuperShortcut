/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 6/11/20 10:12 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Preferences.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.CustomIconInterface;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.util.ArrayList;

public class CustomIconsThemeAdapter extends RecyclerView.Adapter<CustomIconsThemeAdapter.ViewHolder> {

    FunctionsClass functionsClass;

    private Context context;
    private ArrayList<AdapterItemsData> adapterItemsData;

    CustomIconInterface customIconInterface;

    public CustomIconsThemeAdapter(Context context, ArrayList<AdapterItemsData> adapterItemsData, FunctionsClass functionsClass,CustomIconInterface customIconInterface) {
        this.context = context;
        this.adapterItemsData = adapterItemsData;

        this.customIconInterface = customIconInterface;

        this.functionsClass = functionsClass;
    }

    @Override
    public CustomIconsThemeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_icon_items, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {

        viewHolderBinder.appName.setText(adapterItemsData.get(position).getAppName());
        viewHolderBinder.appIcon.setImageDrawable(adapterItemsData.get(position).getAppIcon());

        viewHolderBinder.customIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                functionsClass.saveDefaultPreference("customIcon", adapterItemsData.get(position).getPackageName());

                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, adapterItemsData.get(position).getPackageName());
                loadCustomIcons.load();

                customIconInterface.customIconPackageSelected(adapterItemsData.get(position).getPackageName());

            }
        });
        viewHolderBinder.customIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, adapterItemsData.get(position).getPackageName());
                loadCustomIcons.load();
                functionsClass.Toast(String.valueOf(loadCustomIcons.getTotalIconsNumber()), context.getColor(R.color.light), context.getColor(R.color.dark), Gravity.BOTTOM, true);

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterItemsData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout customIcon;
        TextView appName;
        ImageView appIcon;

        public ViewHolder(View view) {
            super(view);
            customIcon = (RelativeLayout) view.findViewById(R.id.customIcon);
            appName = (TextView) view.findViewById(R.id.appName);
            appIcon = (ImageView) view.findViewById(R.id.appIcon);
        }
    }
}
