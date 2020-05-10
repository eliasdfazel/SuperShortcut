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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.wearable.complications.ProviderUpdateRequester;
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
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.RemoteTask.RecoveryComplication;

import java.io.File;
import java.util.ArrayList;

public class SelectionListAdapter extends RecyclerView.Adapter<SelectionListAdapter.ViewHolder> {

    private Context context;

    FunctionsClass functionsClass;

    ImageView temporaryIcon;

    View view;
    ViewHolder viewHolder;

    private ArrayList<AdapterItemsData> adapterItemsData;

    float fromX, fromY, toX, toY, dpHeight, dpWidth;
    int animationType;

    public SelectionListAdapter(Context context,
                                ArrayList<AdapterItemsData> adapterItemsData,
                                ImageView temporaryIcon) {

        this.context = context;
        this.adapterItemsData = adapterItemsData;

        functionsClass = new FunctionsClass(context);
        this.temporaryIcon = temporaryIcon;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels;
        dpWidth = displayMetrics.widthPixels;

        fromX = toX = PublicVariable.confirmButtonX;
        toY = PublicVariable.confirmButtonY;
        animationType = Animation.ABSOLUTE;
    }

    @Override
    public SelectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.selection_item_card_list, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {
        RelativeLayout item = viewHolderBinder.item;
        ImageView imgIcon = viewHolderBinder.imgIcon;
        TextView txtDesc = viewHolderBinder.txtDesc;

        imgIcon.setImageDrawable(adapterItemsData.get(position).getIcon());
        txtDesc.setText(adapterItemsData.get(position).getDesc());

        final String pack = adapterItemsData.get(position).getTitle();
        File autoFile = context.getFileStreamPath(pack + ".Super");
        viewHolderBinder.autoChoice.setChecked(false);
        if (autoFile.exists()) {
            viewHolderBinder.autoChoice.setChecked(true);
        } else {
            viewHolderBinder.autoChoice.setChecked(false);
        }

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("onTouch ");

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fromY = -((dpHeight - motionEvent.getRawY()) - (view.getHeight()));
                        break;
                    case MotionEvent.ACTION_UP:
                        final String pack = adapterItemsData.get(position).getTitle();
                        File autoFile = context.getFileStreamPath(pack + ".Super");
                        if (autoFile.exists()) {
                            context.deleteFile(
                                    adapterItemsData.get(position).getTitle() + ".Super");
                            functionsClass.removeLine(".autoSuper", adapterItemsData.get(position).getTitle());
                            viewHolderBinder.autoChoice.setChecked(false);
                            context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));

                            context.sendBroadcast(new Intent(context.getString(R.string.savedActionHide)));
                            context.sendBroadcast(new Intent(context.getString(R.string.visibilityAction)));
                        } else {
                            if (PublicVariable.maxAppShortcutsCounter < PublicVariable.maxAppShortcuts) {
                                functionsClass.saveFile(
                                        adapterItemsData.get(position).getTitle() + ".Super",
                                        adapterItemsData.get(position).getTitle());
                                functionsClass.saveFileAppendLine(
                                        ".autoSuper",
                                        adapterItemsData.get(position).getTitle());
                                viewHolderBinder.autoChoice.setChecked(true);

                                TranslateAnimation translateAnimation =
                                        new TranslateAnimation(animationType, fromX,
                                                animationType, toX,
                                                animationType, fromY,
                                                animationType, toY);
                                translateAnimation.setDuration((long) Math.abs(fromY));


                                temporaryIcon.setImageDrawable(functionsClass.appIconDrawable(adapterItemsData.get(position).getTitle()));
                                temporaryIcon.startAnimation(translateAnimation);
                                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        context.sendBroadcast(new Intent(context.getString(R.string.savedActionHide)));
                                        context.sendBroadcast(new Intent(context.getString(R.string.visibilityAction)));

                                        temporaryIcon.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        temporaryIcon.setVisibility(View.INVISIBLE);
                                        context.sendBroadcast(new Intent(context.getString(R.string.animtaionAction)));
                                        context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            }
                        }
                        try {
                            ProviderUpdateRequester requester = new ProviderUpdateRequester(context, new ComponentName(context, RecoveryComplication.class));
                            requester.requestUpdate(functionsClass.readPreference("ComplicationProviderService", "ComplicationedId", 0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });
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

        PublicVariable.maxAppShortcutsCounter = functionsClass.countLine(".autoSuper");

        item.bringToFront();
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
