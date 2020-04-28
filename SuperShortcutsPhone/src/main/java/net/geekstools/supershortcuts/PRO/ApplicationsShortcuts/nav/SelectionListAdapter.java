/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 12:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.nav;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Utils.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;

import java.io.File;
import java.util.ArrayList;

public class SelectionListAdapter extends RecyclerView.Adapter<SelectionListAdapter.ViewHolder> {

    private Context context;
    private AppCompatActivity activity;

    FunctionsClass functionsClass;

    ImageView tempIcon;
    View view;
    ViewHolder viewHolder;

    LoadCustomIcons loadCustomIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;

    float fromX, fromY, toX, toY, dpHeight, dpWidth, systemUiHeight;
    int animationType;

    public SelectionListAdapter(AppCompatActivity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        functionsClass = new FunctionsClass(context, activity);
        tempIcon = (ImageView) activity.findViewById(R.id.tempIcon);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels;
        dpWidth = displayMetrics.widthPixels;
        systemUiHeight = activity.getSupportActionBar().getHeight();

        fromX = toX = PublicVariable.confirmButtonX;
        toY = PublicVariable.confirmButtonY;
        animationType = Animation.ABSOLUTE;

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
        }
    }

    @Override
    public SelectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.selection_item_card_list, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {

        viewHolderBinder.imgIcon.setImageDrawable(navDrawerItems.get(position).getAppIcon());
        viewHolderBinder.txtDesc.setText(navDrawerItems.get(position).getAppName());

        try {
            viewHolderBinder.autoChoice = viewHolderBinder.autoChoice;
            final String packageName = navDrawerItems.get(position).getPackageName();
            final String className = navDrawerItems.get(position).getClassName();
            File autoFile = context.getFileStreamPath(functionsClass.appPackageNameClassName(packageName, className) + ".Super");
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
                        if (PublicVariable.maxAppShortcutsCounter < PublicVariable.maxAppShortcuts) {
                            PublicVariable.maxAppShortcutsCounter++;
                        }
                    } else if (isChecked == false) {
                        PublicVariable.maxAppShortcutsCounter = PublicVariable.maxAppShortcutsCounter - 1;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolderBinder.listItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fromY = -((dpHeight - motionEvent.getRawY()) - (systemUiHeight));
                        break;
                    case MotionEvent.ACTION_UP:
                        try {
                            final String packageName = navDrawerItems.get(position).getPackageName();
                            final String className = navDrawerItems.get(position).getClassName();
                            File autoFile = context.getFileStreamPath(functionsClass.appPackageNameClassName(packageName, className) + ".Super");
                            if (autoFile.exists()) {
                                context.deleteFile(
                                        functionsClass.appPackageNameClassName(packageName, className) + ".Super");
                                functionsClass.removeLine(".autoSuper", functionsClass.appPackageNameClassName(packageName, className));
                                if (functionsClass.mixShortcuts() == true) {
                                    functionsClass.removeLine(".mixShortcuts", functionsClass.appPackageNameClassName(packageName, className));
                                }
                                try {
                                    viewHolderBinder.autoChoice.setChecked(false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));
                                context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcuts)));

                                context.sendBroadcast(new Intent(context.getString(R.string.savedActionHide)));
                                context.sendBroadcast(new Intent(context.getString(R.string.visibilityAction)));
                            } else {
                                if (functionsClass.mixShortcuts() == true) {
                                    if (functionsClass.countLine(".mixShortcuts") < functionsClass.getSystemMaxAppShortcut()) {
                                        functionsClass.saveFile(
                                                functionsClass.appPackageNameClassName(packageName, className) + ".Super",
                                                functionsClass.appPackageNameClassName(packageName, className));
                                        functionsClass.saveFileAppendLine(
                                                ".autoSuper",
                                                functionsClass.appPackageNameClassName(packageName, className));
                                        functionsClass.saveFileAppendLine(
                                                ".mixShortcuts",
                                                functionsClass.appPackageNameClassName(packageName, className));
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

                                        tempIcon.setImageDrawable(functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packageName, navDrawerItems.get(position).getAppIcon()) : navDrawerItems.get(position).getAppIcon());
                                        tempIcon.startAnimation(translateAnimation);
                                        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                                context.sendBroadcast(new Intent(context.getString(R.string.savedActionHide)));
                                                context.sendBroadcast(new Intent(context.getString(R.string.visibilityAction)));

                                                tempIcon.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                tempIcon.setVisibility(View.INVISIBLE);
                                                context.sendBroadcast(new Intent(context.getString(R.string.animtaionAction)));
                                                context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));
                                                context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcuts)));
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                    }
                                } else {
                                    if (PublicVariable.maxAppShortcutsCounter < PublicVariable.maxAppShortcuts) {
                                        functionsClass.saveFile(
                                                functionsClass.appPackageNameClassName(packageName, className) + ".Super",
                                                packageName);
                                        functionsClass.saveFileAppendLine(
                                                ".autoSuper",
                                                functionsClass.appPackageNameClassName(packageName, className));
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

                                        tempIcon.setImageDrawable(functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packageName, navDrawerItems.get(position).getAppIcon()) : navDrawerItems.get(position).getAppIcon());
                                        tempIcon.startAnimation(translateAnimation);
                                        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                                context.sendBroadcast(new Intent(context.getString(R.string.savedActionHide)));
                                                context.sendBroadcast(new Intent(context.getString(R.string.visibilityAction)));

                                                tempIcon.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                tempIcon.setVisibility(View.INVISIBLE);
                                                context.sendBroadcast(new Intent(context.getString(R.string.animtaionAction)));
                                                context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));
                                                context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcuts)));
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });

        PublicVariable.maxAppShortcutsCounter = functionsClass.countLine(".autoSuper");
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

        RelativeLayout listItem;
        ImageView imgIcon;
        TextView txtDesc;
        CheckBox autoChoice;

        public ViewHolder(View view) {
            super(view);
            listItem = (RelativeLayout) view.findViewById(R.id.item);
            imgIcon = (ImageView) view.findViewById(R.id.icon);
            txtDesc = (TextView) view.findViewById(R.id.desc);
            autoChoice = (CheckBox) view.findViewById(R.id.autoChoice);
        }
    }
}
