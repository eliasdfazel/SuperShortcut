/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 12:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.nav;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitAppSelectionList;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Utils.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;

import java.io.File;
import java.util.ArrayList;

public class SplitShortcutsAdapter extends RecyclerView.Adapter<SplitShortcutsAdapter.ViewHolder> {

    private Context context;
    private Activity activity;

    FunctionsClass functionsClass;

    ImageView imageView;
    RelativeLayout freqLayout;
    View view;
    ViewHolder viewHolder;

    LoadCustomIcons loadCustomIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;

    String endEdited = "", endFocus = "";

    public SplitShortcutsAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        functionsClass = new FunctionsClass(context, activity);

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
        }
    }

    @Override
    public SplitShortcutsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_split_shortcuts, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SplitShortcutsAdapter.ViewHolder viewHolderBinder, final int position) {

        final String category = navDrawerItems.get(position).getCategory();
        final String[] packages = navDrawerItems.get(position).getPackageNames();

        try {
            viewHolderBinder.categoryName.setText(navDrawerItems.get(position).getCategory().split("_")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (category.equals(context.getPackageName())) {
            try {
                viewHolderBinder.categoryName.setText("");
                viewHolderBinder.autoChoice.setChecked(false);
                viewHolderBinder.addApps.setVisibility(View.INVISIBLE);
                viewHolderBinder.selectedApps.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File autoFile = context.getFileStreamPath(category);
            if (autoFile.exists() && autoFile.isFile()) {
                viewHolderBinder.selectedApps.removeAllViews();
                viewHolderBinder.selectedApps.setVisibility(View.VISIBLE);
                for (int i = 0; i < packages.length; i++) {
                    freqLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.split_selected_apps_item, null);
                    imageView = (ImageView) freqLayout.findViewById(R.id.appSelectedItem);
                    imageView.setImageDrawable(functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packages[i], functionsClass.appIconDrawable(packages[i])) : functionsClass.appIconDrawable(packages[i]));
                    viewHolderBinder.selectedApps.addView(freqLayout);
                    viewHolderBinder.addApps.setVisibility(View.VISIBLE);
                }
            }

            File autoFileSelected = context.getFileStreamPath(functionsClass.splitNameSelected(category));
            viewHolderBinder.autoChoice.setChecked(false);
            if (autoFileSelected.exists() && autoFileSelected.isFile()) {
                viewHolderBinder.autoChoice.setChecked(true);
            } else {
                viewHolderBinder.autoChoice.setChecked(false);
            }
        }

        viewHolderBinder.categoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PublicVariable.categoryName = textView.getText().toString();

                    File file = context.getFileStreamPath(navDrawerItems.get(position).getCategory());
                    if (file.exists() && file.isFile()) {
                        if (navDrawerItems.get(position).getCategory().equals(PublicVariable.categoryName)) {
                            PublicVariable.categoryName = navDrawerItems.get(position).getCategory();
                            context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                        } else {

                            //selectedCategory
                            if (context.getFileStreamPath(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory())).exists()) {
                                String[] appsContent = functionsClass.readFileLine(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory()));
                                for (String appContent : appsContent) {
                                    context.deleteFile(functionsClass.splitNameSelected(appContent + navDrawerItems.get(position).getCategory()));
                                    functionsClass.saveFileAppendLine(functionsClass.splitNameSelected(PublicVariable.categoryName), appContent);
                                    functionsClass.saveFile(functionsClass.splitNameSelected(appContent + PublicVariable.categoryName), appContent);
                                }
                                functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryName);
                                functionsClass.removeLine(".SplitSuperSelected", navDrawerItems.get(position).getCategory());
                                context.deleteFile(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory()));
                            }

                            //selectedSplit
                            if (functionsClass.mixShortcuts()) {
                                functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                            }

                            String[] appsContent = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                            for (String appContent : appsContent) {
                                context.deleteFile(appContent + navDrawerItems.get(position).getCategory());
                                functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                                functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                            }
                            functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                            functionsClass.removeLine(".SplitSuper", navDrawerItems.get(position).getCategory());
                            context.deleteFile(navDrawerItems.get(position).getCategory());
                            context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                        }
                    } else {
                        PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                        functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                        functionsClass.saveFileEmpty(PublicVariable.categoryName);
                        context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                }
                return true;
            }
        });
        viewHolderBinder.categoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                endEdited = editable.toString();
                if (viewHolderBinder.addApps.isShown()) {
                    viewHolderBinder.addApps.setVisibility(View.INVISIBLE);
                }
            }
        });
        viewHolderBinder.selectedApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endFocus.length() > 0) {
                    PublicVariable.categoryName = endFocus;
                } else {
                    if (!navDrawerItems.get(position).getCategory().equals(context.getPackageName())) {
                        PublicVariable.categoryName = navDrawerItems.get(position).getCategory();
                    } else {
                        if (endEdited.length() > 0) {
                            PublicVariable.categoryName = endEdited;
                        }
                    }
                }

                File file = context.getFileStreamPath(navDrawerItems.get(position).getCategory());
                if (file.exists() && file.isFile()) {
                    if (navDrawerItems.get(position).getCategory().equals(PublicVariable.categoryName)) {
                        PublicVariable.categoryName = navDrawerItems.get(position).getCategory();
                        context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    } else {

                        //selectedCategory
                        if (context.getFileStreamPath(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory())).exists()) {
                            String[] appsContent = functionsClass.readFileLine(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory()));
                            for (String appContent : appsContent) {
                                context.deleteFile(functionsClass.splitNameSelected(appContent + navDrawerItems.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(functionsClass.splitNameSelected(PublicVariable.categoryName), appContent);
                                functionsClass.saveFile(functionsClass.splitNameSelected(appContent + PublicVariable.categoryName), appContent);
                            }
                            functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryName);
                            functionsClass.removeLine(".SplitSuperSelected", navDrawerItems.get(position).getCategory());
                            context.deleteFile(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory()));
                        }

                        //selectedSplit
                        if (functionsClass.mixShortcuts()) {
                            functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
                            functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                        }

                        String[] appsContent = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                        for (String appContent : appsContent) {
                            context.deleteFile(appContent + navDrawerItems.get(position).getCategory());
                            functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                            functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                        }
                        functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                        functionsClass.removeLine(".SplitSuper", navDrawerItems.get(position).getCategory());
                        context.deleteFile(navDrawerItems.get(position).getCategory());
                        context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                } else {
                    PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                    functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                    functionsClass.saveFileEmpty(PublicVariable.categoryName);
                    context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                }
            }
        });
        viewHolderBinder.addApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!navDrawerItems.get(position).getCategory().equals(context.getPackageName())) {
                    PublicVariable.categoryName = navDrawerItems.get(position).getCategory();
                } else {
                    if (endEdited.length() > 0) {
                        PublicVariable.categoryName = endEdited;
                    }
                }

                File file = context.getFileStreamPath(navDrawerItems.get(position).getCategory());
                if (file.exists() && file.isFile()) {
                    if (navDrawerItems.get(position).getCategory().equals(PublicVariable.categoryName)) {
                        PublicVariable.categoryName = navDrawerItems.get(position).getCategory();
                        context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    } else {

                        //selectedCategory
                        if (context.getFileStreamPath(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory())).exists()) {
                            String[] appsContent = functionsClass.readFileLine(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory()));
                            for (String appContent : appsContent) {
                                context.deleteFile(functionsClass.splitNameSelected(appContent + navDrawerItems.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(functionsClass.splitNameSelected(PublicVariable.categoryName), appContent);
                                functionsClass.saveFile(functionsClass.splitNameSelected(appContent + PublicVariable.categoryName), appContent);
                            }
                            functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryName);
                            functionsClass.removeLine(".SplitSuperSelected", navDrawerItems.get(position).getCategory());
                            context.deleteFile(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory()));
                        }

                        //selectedSplit
                        if (functionsClass.mixShortcuts()) {
                            functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
                            functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                        }

                        String[] appsContent = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                        for (String appContent : appsContent) {
                            context.deleteFile(appContent + navDrawerItems.get(position).getCategory());
                            functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                            functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                        }
                        functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                        functionsClass.removeLine(".SplitSuper", navDrawerItems.get(position).getCategory());
                        context.deleteFile(navDrawerItems.get(position).getCategory());
                        context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                } else {
                    PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                    functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                    functionsClass.saveFileEmpty(PublicVariable.categoryName);
                    context.startActivity(new Intent(context, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                }
            }
        });

        viewHolderBinder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!category.equals(context.getPackageName())) {
                        PublicVariable.categoryNameSelected = functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory());

                        File fileSelected = context.getFileStreamPath(PublicVariable.categoryNameSelected);
                        if (fileSelected.exists() && fileSelected.isFile()) {
                            context.deleteFile(PublicVariable.categoryNameSelected);
                            functionsClass.removeLine(".SplitSuperSelected", PublicVariable.categoryNameSelected);
                            if (functionsClass.mixShortcuts() == true) {
                                functionsClass.removeLine(".mixShortcuts", PublicVariable.categoryNameSelected);
                            }
                            viewHolderBinder.autoChoice.setChecked(false);
                            context.sendBroadcast(new Intent(context.getString(R.string.counterActionSplitShortcuts)));
                            context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcutsSplit)));
                        } else {
                            if (functionsClass.mixShortcuts() == true) {
                                if (functionsClass.countLine(".mixShortcuts") < functionsClass.getSystemMaxAppShortcut()) {
                                    String[] appsContent = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                                    for (String appContent : appsContent) {
                                        functionsClass.saveFileAppendLine(PublicVariable.categoryNameSelected, appContent);
                                    }
                                    functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryNameSelected);
                                    functionsClass.saveFileAppendLine(".mixShortcuts", PublicVariable.categoryNameSelected);

                                    viewHolderBinder.autoChoice.setChecked(true);
                                    context.sendBroadcast(new Intent(context.getString(R.string.counterActionSplitShortcuts)));
                                    context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcutsSplit)));
                                }
                            } else {
                                if (PublicVariable.SplitShortcutsMaxAppShortcutsCounter < PublicVariable.SplitShortcutsMaxAppShortcuts) {
                                    String[] appsContent = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                                    for (String appContent : appsContent) {
                                        functionsClass.saveFileAppendLine(PublicVariable.categoryNameSelected, appContent);
                                    }
                                    functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryNameSelected);

                                    viewHolderBinder.autoChoice.setChecked(true);
                                    context.sendBroadcast(new Intent(context.getString(R.string.counterActionSplitShortcuts)));
                                    context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcutsSplit)));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        viewHolderBinder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!navDrawerItems.get(position).getCategory().equals(context.getPackageName())) {
                    String[] categoryChoices = new String[]{
                            context.getString(R.string.addSuper),
                            context.getString(R.string.addToDesktop),
                            context.getString(R.string.deleteCategory)
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(Html.fromHtml("<small>" + context.getString(R.string.option) + " " + "<b>" + navDrawerItems.get(position).getCategory().split("_")[0] + "</b></small>", Html.FROM_HTML_MODE_LEGACY));
                    builder.setSingleChoiceItems(categoryChoices, 0, null);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            switch (selectedPosition) {
                                case 0:
                                    functionsClass.addToSuperShortcuts(navDrawerItems.get(position).getCategory());
                                    break;
                                case 1:
                                    functionsClass.splitToDesktop(navDrawerItems.get(position).getCategory());
                                    break;
                                case 2:
                                    try {
                                        if (context.getFileStreamPath(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory())).exists()) {
                                            functionsClass.removeLine(".SplitSuperSelected", functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory()));
                                            context.deleteFile(functionsClass.splitNameSelected(navDrawerItems.get(position).getCategory()));
                                        }
                                        String[] packsToDelete = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                                        for (String packToDelete : packsToDelete) {
                                            context.deleteFile(packToDelete + navDrawerItems.get(position).getCategory());
                                        }
                                        functionsClass.removeLine(".SplitSuper", navDrawerItems.get(position).getCategory());
                                        context.deleteFile(navDrawerItems.get(position).getCategory());
                                        functionsClass.removeHomeShortcut(".split.SplitTransparentPair", "load_split_action_pair_shortcut", Intent.CATEGORY_DEFAULT, navDrawerItems.get(position).getCategory());
                                        context.sendBroadcast(new Intent(context.getString(R.string.checkboxActionSplitShortcuts)));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    builder.show();
                }
                return true;
            }
        });

        viewHolderBinder.autoChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == true) {
                    if (PublicVariable.SplitShortcutsMaxAppShortcutsCounter < PublicVariable.SplitShortcutsMaxAppShortcuts) {
                        PublicVariable.SplitShortcutsMaxAppShortcutsCounter++;
                    }
                } else if (isChecked == false) {
                    PublicVariable.SplitShortcutsMaxAppShortcutsCounter = PublicVariable.SplitShortcutsMaxAppShortcutsCounter - 1;
                }
            }
        });

        PublicVariable.SplitShortcutsMaxAppShortcutsCounter = functionsClass.countLine(".SplitSuperSelected");
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
        LinearLayout selectedApps;
        EditText categoryName;
        CheckBox autoChoice;
        ImageView addApps;

        public ViewHolder(View view) {
            super(view);
            item = (RelativeLayout) view.findViewById(R.id.categoryItem);
            selectedApps = (LinearLayout) view.findViewById(R.id.selectedApps);
            categoryName = (EditText) view.findViewById(R.id.categoryName);
            autoChoice = (CheckBox) view.findViewById(R.id.autoChoice);
            addApps = (ImageView) view.findViewById(R.id.addApps);
        }
    }
}
