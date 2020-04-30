/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 6:30 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.nav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.io.File;
import java.util.ArrayList;

public class SplitSelectionListAdapter extends RecyclerView.Adapter<SplitSelectionListAdapter.ViewHolder> {

    private Context context;
    private Activity activity;

    FunctionsClass functionsClass;

    ImageView tempIcon;
    View view;
    ViewHolder viewHolder;

    LoadCustomIcons loadCustomIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;

    float fromX, fromY, toX, toY, dpHeight, dpWidth, systemUiHeight;
    int animationType;

    public SplitSelectionListAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        functionsClass = new FunctionsClass(context);
        tempIcon = (ImageView) activity.findViewById(R.id.tempIcon);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels;
        dpWidth = displayMetrics.widthPixels;
        systemUiHeight = activity.getActionBar().getHeight();
        fromX = toX = PublicVariable.confirmButtonX;
        toY = PublicVariable.confirmButtonY;
        animationType = Animation.ABSOLUTE;

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
            loadCustomIcons.load();
        }
    }

    @Override
    public SplitSelectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.selection_item_card_list, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SplitSelectionListAdapter.ViewHolder viewHolderBinder, final int position) {

        viewHolderBinder.imgIcon.setImageDrawable(navDrawerItems.get(position).getAppIcon());
        viewHolderBinder.txtDesc.setText(navDrawerItems.get(position).getAppName());

        try {
            final String pack = navDrawerItems.get(position).getPackageName();
            File autoFile = context.getFileStreamPath(pack + PublicVariable.categoryName);
            viewHolderBinder.autoChoice.setChecked(false);
            if (autoFile.exists()) {
                viewHolderBinder.autoChoice.setChecked(true);
            } else {
                viewHolderBinder.autoChoice.setChecked(false);
            }
            viewHolderBinder.autoChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked == true) {
                        if (PublicVariable.SplitMaxAppShortcutsCounter < PublicVariable.SplitMaxAppShortcuts) {
                            PublicVariable.SplitMaxAppShortcutsCounter++;
                        }
                    } else if (isChecked == false) {
                        PublicVariable.SplitMaxAppShortcutsCounter = PublicVariable.SplitMaxAppShortcutsCounter - 1;
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolderBinder.item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fromY = -((dpHeight - motionEvent.getRawY()) - (systemUiHeight));
                        break;
                    case MotionEvent.ACTION_UP:
                        final String pack = navDrawerItems.get(position).getPackageName();
                        File autoFile = context.getFileStreamPath(pack + PublicVariable.categoryName);
                        if (autoFile.exists()) {
                            context.deleteFile(pack + PublicVariable.categoryName);
                            functionsClass.removeLine(PublicVariable.categoryName, navDrawerItems.get(position).getPackageName());
                            try {
                                viewHolderBinder.autoChoice.setChecked(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            context.sendBroadcast(new Intent(context.getString(R.string.counterActionSplit)));

                            context.sendBroadcast(new Intent(context.getString(R.string.savedActionHideSplit)));
                            context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionSplit)));
                        } else {
                            if (PublicVariable.SplitMaxAppShortcutsCounter < PublicVariable.SplitMaxAppShortcuts) {
                                functionsClass.saveFile(
                                        pack + PublicVariable.categoryName, pack);
                                functionsClass.saveFileAppendLine(
                                        PublicVariable.categoryName, pack);
                                try {
                                    viewHolderBinder.autoChoice.setChecked(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                TranslateAnimation translateAnimation =
                                        new TranslateAnimation(animationType, fromX,
                                                animationType, toX,
                                                animationType, fromY,
                                                animationType, toY);
                                translateAnimation.setDuration((long) Math.abs(fromY));


                                tempIcon.setImageDrawable(functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(navDrawerItems.get(position).getPackageName(), functionsClass.appIconDrawable(navDrawerItems.get(position).getPackageName())) : functionsClass.appIconDrawable(navDrawerItems.get(position).getPackageName()));
                                tempIcon.startAnimation(translateAnimation);
                                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        context.sendBroadcast(new Intent(context.getString(R.string.savedActionHideSplit)));
                                        context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionSplit)));

                                        tempIcon.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        tempIcon.setVisibility(View.INVISIBLE);
                                        context.sendBroadcast(new Intent(context.getString(R.string.animtaionActionSplit)));
                                        context.sendBroadcast(new Intent(context.getString(R.string.counterActionSplit)));
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            }
                        }
                        break;
                }
                return true;
            }
        });

        PublicVariable.SplitMaxAppShortcutsCounter = functionsClass.countLine(PublicVariable.categoryName);
    }

    @Override
    public int getItemCount() {
        return navDrawerItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout item;
        ImageView imgIcon;
        TextView txtDesc;
        CheckBox autoChoice;

        public ViewHolder(View view) {
            super(view);
            item = (RelativeLayout) view.findViewById(R.id.item);
            imgIcon = (ImageView) view.findViewById(R.id.icon);
            txtDesc = (TextView) view.findViewById(R.id.desc);
            autoChoice = (CheckBox) view.findViewById(R.id.autoChoice);
        }
    }
}
