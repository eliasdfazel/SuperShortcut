package net.geekstools.supershortcuts.PRO.normal.nav;

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

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.normal.NormalAppSelectionList;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

    FunctionsClass functionsClass;
    private Context context;
    private Activity activity;
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
                if (navDrawerItems.get(position).getDesc().contains(context.getString(R.string.edit_advanced_shortcut))) {
                    context.startActivity(new Intent(context, NormalAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    return;
                }
                String packageName = navDrawerItems.get(position).getTitle();
                functionsClass.openApplication(packageName);
                listPopupWindow.dismiss();
            }
        });
        items.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String packageName = navDrawerItems.get(position).getTitle();
                functionsClass.goToSettingInfo(packageName);
                return true;
            }
        });

        return convertView;
    }
}
