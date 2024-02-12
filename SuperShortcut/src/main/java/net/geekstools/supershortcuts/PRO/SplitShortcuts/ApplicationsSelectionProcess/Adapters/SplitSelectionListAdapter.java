/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 12:10 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.ApplicationsSelectionProcess.Adapters;

import android.annotation.SuppressLint;
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
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.io.File;
import java.util.ArrayList;

public class SplitSelectionListAdapter extends RecyclerView.Adapter<SplitSelectionListAdapter.ViewHolder> {

    private AppCompatActivity appCompatActivity;

    FunctionsClass functionsClass;

    ImageView tempIcon;
    View view;
    ViewHolder viewHolder;

    LoadCustomIcons loadCustomIcons;

    private ArrayList<AdapterItemsData> splitSelectionListData;

    float fromX, fromY, toX, toY, dpHeight, dpWidth;
    int animationType;

    public SplitSelectionListAdapter(AppCompatActivity appCompatActivity, ArrayList<AdapterItemsData> splitSelectionListData) {
        this.appCompatActivity = appCompatActivity;
        this.splitSelectionListData = splitSelectionListData;

        functionsClass = new FunctionsClass(appCompatActivity);
        tempIcon = (ImageView) appCompatActivity.findViewById(R.id.temporary_falling_icon);

        DisplayMetrics displayMetrics = appCompatActivity.getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels;
        dpWidth = displayMetrics.widthPixels;

        fromX = toX = PublicVariable.confirmButtonX;
        toY = PublicVariable.confirmButtonY;
        animationType = Animation.ABSOLUTE;

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(appCompatActivity, functionsClass.customIconPackageName());
            loadCustomIcons.load();
        }
    }

    @Override
    public SplitSelectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(appCompatActivity).inflate(R.layout.selection_item_card_list, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(SplitSelectionListAdapter.ViewHolder viewHolderBinder, final int position) {

        viewHolderBinder.appIconView.setImageDrawable(splitSelectionListData.get(position).getAppIcon());
        viewHolderBinder.appNameView.setText(splitSelectionListData.get(position).getAppName());

        final String pack = splitSelectionListData.get(position).getPackageName();
        File autoFile = appCompatActivity.getFileStreamPath(pack + PublicVariable.categoryName);
        viewHolderBinder.autoChoice.setChecked(false);
        if (autoFile.exists()) {
            viewHolderBinder.autoChoice.setChecked(true);
        } else {
            viewHolderBinder.autoChoice.setChecked(false);
        }

        viewHolderBinder.fullItemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        });

        viewHolderBinder.fullItemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        fromY = -((dpHeight - motionEvent.getRawY()) - (viewHolderBinder.fullItemView.getHeight()));

                        break;
                    case MotionEvent.ACTION_UP:
                        final String pack = splitSelectionListData.get(position).getPackageName();
                        File autoFile = appCompatActivity.getFileStreamPath(pack + PublicVariable.categoryName);
                        if (autoFile.exists()) {
                            appCompatActivity.deleteFile(pack + PublicVariable.categoryName);
                            functionsClass.removeLine(PublicVariable.categoryName, splitSelectionListData.get(position).getPackageName());
                            try {
                                viewHolderBinder.autoChoice.setChecked(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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


                                tempIcon.setImageDrawable(functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(splitSelectionListData.get(position).getPackageName(), functionsClass.appIconDrawable(splitSelectionListData.get(position).getPackageName())) : functionsClass.appIconDrawable(splitSelectionListData.get(position).getPackageName()));
                                tempIcon.startAnimation(translateAnimation);
                                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                        tempIcon.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        tempIcon.setVisibility(View.INVISIBLE);
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

        viewHolderBinder.autoChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (PublicVariable.SplitMaxAppShortcutsCounter < PublicVariable.SplitMaxAppShortcuts) {
                        PublicVariable.SplitMaxAppShortcutsCounter++;
                    }
                } else if (!isChecked) {
                    PublicVariable.SplitMaxAppShortcutsCounter = PublicVariable.SplitMaxAppShortcutsCounter - 1;
                }

            }
        });

        PublicVariable.SplitMaxAppShortcutsCounter = functionsClass.countLine(PublicVariable.categoryName);
    }

    @Override
    public int getItemCount() {
        return splitSelectionListData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout fullItemView;
        ImageView appIconView;
        TextView appNameView;
        CheckBox autoChoice;

        public ViewHolder(View view) {
            super(view);
            fullItemView = (RelativeLayout) view.findViewById(R.id.fullItemView);
            appIconView = (ImageView) view.findViewById(R.id.appIconView);
            appNameView = (TextView) view.findViewById(R.id.appNameView);
            autoChoice = (CheckBox) view.findViewById(R.id.autoChoice);
        }
    }
}
