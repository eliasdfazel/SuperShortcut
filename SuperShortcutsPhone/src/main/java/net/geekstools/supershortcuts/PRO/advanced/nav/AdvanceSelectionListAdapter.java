package net.geekstools.supershortcuts.PRO.advanced.nav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.NavDrawerItem;

import java.io.File;
import java.util.ArrayList;

public class AdvanceSelectionListAdapter extends RecyclerView.Adapter<AdvanceSelectionListAdapter.ViewHolder> {

    FunctionsClass functionsClass;
    ImageView tempIcon;
    float fromX, fromY, toX, toY, dpHeight, dpWidth, systemUiHeight;
    int animationType;
    CheckBox[] autoChoice;
    View view;
    ViewHolder viewHolder;
    LoadCustomIcons loadCustomIcons;
    private Context context;
    private Activity activity;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public AdvanceSelectionListAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        autoChoice = new CheckBox[navDrawerItems.size()];
        functionsClass = new FunctionsClass(context, activity);
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
    public AdvanceSelectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.selection_item_card_list, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdvanceSelectionListAdapter.ViewHolder viewHolderBinder, final int position) {
        RelativeLayout item = viewHolderBinder.item;
        ImageView imgIcon = viewHolderBinder.imgIcon;
        TextView txtDesc = viewHolderBinder.txtDesc;
        autoChoice[position] = viewHolderBinder.autoChoice;

        imgIcon.setImageDrawable(navDrawerItems.get(position).getAppIcon());
        txtDesc.setText(navDrawerItems.get(position).getAppName());

        final String pack = navDrawerItems.get(position).getPackageName();
        File autoFile = context.getFileStreamPath(pack + PublicVariable.categoryName);
        autoChoice[position].setChecked(false);
        if (autoFile.exists()) {
            autoChoice[position].setChecked(true);
        } else {
            autoChoice[position].setChecked(false);
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
                            autoChoice[position].setChecked(false);
                            context.sendBroadcast(new Intent(context.getString(R.string.counterActionAdvance)));

                            context.sendBroadcast(new Intent(context.getString(R.string.savedActionHideAdvance)));
                            context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionAdvance)));
                        } else {
                            if (PublicVariable.advMaxAppShortcutsCounter < PublicVariable.advMaxAppShortcuts) {
                                functionsClass.saveFile(
                                        pack + PublicVariable.categoryName, pack);
                                functionsClass.saveFileAppendLine(
                                        PublicVariable.categoryName, pack);
                                autoChoice[position].setChecked(true);

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
                                        context.sendBroadcast(new Intent(context.getString(R.string.savedActionHideAdvance)));
                                        context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionAdvance)));

                                        tempIcon.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        tempIcon.setVisibility(View.INVISIBLE);
                                        context.sendBroadcast(new Intent(context.getString(R.string.animtaionActionAdvance)));
                                        context.sendBroadcast(new Intent(context.getString(R.string.counterActionAdvance)));
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
        autoChoice[position].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == true) {
                    if (PublicVariable.advMaxAppShortcutsCounter < PublicVariable.advMaxAppShortcuts) {
                        PublicVariable.advMaxAppShortcutsCounter++;
                    }
                } else if (isChecked == false) {
                    PublicVariable.advMaxAppShortcutsCounter = PublicVariable.advMaxAppShortcutsCounter - 1;
                }

            }
        });

        PublicVariable.advMaxAppShortcutsCounter = functionsClass.countLine(PublicVariable.categoryName);
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
