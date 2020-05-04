/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 9:26 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.Adapters;

import android.app.ActivityOptions;
import android.app.AlertDialog;
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
import net.geekstools.supershortcuts.PRO.SplitShortcuts.ApplicationsSelectionProcess.SplitAppSelectionList;
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.io.File;
import java.util.ArrayList;

public class SplitShortcutsAdapter extends RecyclerView.Adapter<SplitShortcutsAdapter.ViewHolder> {

    private SplitShortcuts splitShortcuts;

    FunctionsClass functionsClass;

    String endEdited = "",
            endFocus = "";

    LoadCustomIcons loadCustomIcons;

    private ArrayList<AdapterItemsData> createdSplitListItem;

    public SplitShortcutsAdapter(SplitShortcuts splitShortcuts,
                                 ArrayList<AdapterItemsData> createdSplitListItem) {

        this.splitShortcuts = splitShortcuts;

        this.createdSplitListItem = createdSplitListItem;

        functionsClass = new FunctionsClass(splitShortcuts);

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(splitShortcuts, functionsClass.customIconPackageName());
        }
    }

    @Override
    public SplitShortcutsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(splitShortcuts).inflate(R.layout.item_split_shortcuts, parent, false));
    }

    @Override
    public void onBindViewHolder(SplitShortcutsAdapter.ViewHolder viewHolderBinder, final int position) {

        final String category = createdSplitListItem.get(position).getCategory();
        final String[] packages = createdSplitListItem.get(position).getPackageNames();

        try {
            viewHolderBinder.categoryName.setText(createdSplitListItem.get(position).getCategory().split("_")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (category.equals(splitShortcuts.getPackageName())) {
            try {
                viewHolderBinder.categoryName.setText("");
                viewHolderBinder.autoChoice.setChecked(false);
                viewHolderBinder.addApps.setVisibility(View.INVISIBLE);
                viewHolderBinder.selectedApps.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File autoFile = splitShortcuts.getFileStreamPath(category);
            if (autoFile.exists() && autoFile.isFile()) {
                viewHolderBinder.selectedApps.removeAllViews();
                viewHolderBinder.selectedApps.setVisibility(View.VISIBLE);
                for (int i = 0; i < packages.length; i++) {
                    RelativeLayout selectedApplicationsPreview = (RelativeLayout) splitShortcuts.getLayoutInflater().inflate(R.layout.split_selected_apps_item, null);
                    ImageView imageView = (ImageView) selectedApplicationsPreview.findViewById(R.id.appSelectedItem);
                    imageView.setImageDrawable(functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(packages[i], functionsClass.appIconDrawable(packages[i])) : functionsClass.appIconDrawable(packages[i]));
                    viewHolderBinder.selectedApps.addView(selectedApplicationsPreview);
                    viewHolderBinder.addApps.setVisibility(View.VISIBLE);
                }
            }

            File autoFileSelected = splitShortcuts.getFileStreamPath(functionsClass.splitNameSelected(category));
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

                    File file = splitShortcuts.getFileStreamPath(createdSplitListItem.get(position).getCategory());
                    if (file.exists() && file.isFile()) {
                        if (createdSplitListItem.get(position).getCategory().equals(PublicVariable.categoryName)) {
                            PublicVariable.categoryName = createdSplitListItem.get(position).getCategory();
                            splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                        } else {

                            //selectedCategory
                            if (splitShortcuts.getFileStreamPath(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory())).exists()) {
                                String[] appsContent = functionsClass.readFileLine(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory()));
                                for (String appContent : appsContent) {
                                    splitShortcuts.deleteFile(functionsClass.splitNameSelected(appContent + createdSplitListItem.get(position).getCategory()));
                                    functionsClass.saveFileAppendLine(functionsClass.splitNameSelected(PublicVariable.categoryName), appContent);
                                    functionsClass.saveFile(functionsClass.splitNameSelected(appContent + PublicVariable.categoryName), appContent);
                                }
                                functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryName);
                                functionsClass.removeLine(".SplitSuperSelected", createdSplitListItem.get(position).getCategory());
                                splitShortcuts.deleteFile(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory()));
                            }

                            //selectedSplit
                            if (functionsClass.mixShortcuts()) {
                                functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(createdSplitListItem.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                            }

                            String[] appsContent = functionsClass.readFileLine(createdSplitListItem.get(position).getCategory());
                            for (String appContent : appsContent) {
                                splitShortcuts.deleteFile(appContent + createdSplitListItem.get(position).getCategory());
                                functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                                functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                            }
                            functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                            functionsClass.removeLine(".SplitSuper", createdSplitListItem.get(position).getCategory());
                            splitShortcuts.deleteFile(createdSplitListItem.get(position).getCategory());
                            splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                        }
                    } else {
                        PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                        functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                        functionsClass.saveFileEmpty(PublicVariable.categoryName);
                        splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
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
                    if (!createdSplitListItem.get(position).getCategory().equals(splitShortcuts.getPackageName())) {
                        PublicVariable.categoryName = createdSplitListItem.get(position).getCategory();
                    } else {
                        if (endEdited.length() > 0) {
                            PublicVariable.categoryName = endEdited;
                        }
                    }
                }

                File file = splitShortcuts.getFileStreamPath(createdSplitListItem.get(position).getCategory());
                if (file.exists() && file.isFile()) {
                    if (createdSplitListItem.get(position).getCategory().equals(PublicVariable.categoryName)) {
                        PublicVariable.categoryName = createdSplitListItem.get(position).getCategory();
                        splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    } else {

                        //selectedCategory
                        if (splitShortcuts.getFileStreamPath(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory())).exists()) {
                            String[] appsContent = functionsClass.readFileLine(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory()));
                            for (String appContent : appsContent) {
                                splitShortcuts.deleteFile(functionsClass.splitNameSelected(appContent + createdSplitListItem.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(functionsClass.splitNameSelected(PublicVariable.categoryName), appContent);
                                functionsClass.saveFile(functionsClass.splitNameSelected(appContent + PublicVariable.categoryName), appContent);
                            }
                            functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryName);
                            functionsClass.removeLine(".SplitSuperSelected", createdSplitListItem.get(position).getCategory());
                            splitShortcuts.deleteFile(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory()));
                        }

                        //selectedSplit
                        if (functionsClass.mixShortcuts()) {
                            functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(createdSplitListItem.get(position).getCategory()));
                            functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                        }

                        String[] appsContent = functionsClass.readFileLine(createdSplitListItem.get(position).getCategory());
                        for (String appContent : appsContent) {
                            splitShortcuts.deleteFile(appContent + createdSplitListItem.get(position).getCategory());
                            functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                            functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                        }
                        functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                        functionsClass.removeLine(".SplitSuper", createdSplitListItem.get(position).getCategory());
                        splitShortcuts.deleteFile(createdSplitListItem.get(position).getCategory());
                        splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                } else {
                    PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                    functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                    functionsClass.saveFileEmpty(PublicVariable.categoryName);
                    splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                }
            }
        });
        viewHolderBinder.addApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!createdSplitListItem.get(position).getCategory().equals(splitShortcuts.getPackageName())) {
                    PublicVariable.categoryName = createdSplitListItem.get(position).getCategory();
                } else {
                    if (endEdited.length() > 0) {
                        PublicVariable.categoryName = endEdited;
                    }
                }

                File file = splitShortcuts.getFileStreamPath(createdSplitListItem.get(position).getCategory());
                if (file.exists() && file.isFile()) {
                    if (createdSplitListItem.get(position).getCategory().equals(PublicVariable.categoryName)) {
                        PublicVariable.categoryName = createdSplitListItem.get(position).getCategory();
                        splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    } else {

                        //selectedCategory
                        if (splitShortcuts.getFileStreamPath(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory())).exists()) {
                            String[] appsContent = functionsClass.readFileLine(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory()));
                            for (String appContent : appsContent) {
                                splitShortcuts.deleteFile(functionsClass.splitNameSelected(appContent + createdSplitListItem.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(functionsClass.splitNameSelected(PublicVariable.categoryName), appContent);
                                functionsClass.saveFile(functionsClass.splitNameSelected(appContent + PublicVariable.categoryName), appContent);
                            }
                            functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryName);
                            functionsClass.removeLine(".SplitSuperSelected", createdSplitListItem.get(position).getCategory());
                            splitShortcuts.deleteFile(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory()));
                        }

                        //selectedSplit
                        if (functionsClass.mixShortcuts()) {
                            functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(createdSplitListItem.get(position).getCategory()));
                            functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                        }

                        String[] appsContent = functionsClass.readFileLine(createdSplitListItem.get(position).getCategory());
                        for (String appContent : appsContent) {
                            splitShortcuts.deleteFile(appContent + createdSplitListItem.get(position).getCategory());
                            functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                            functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                        }
                        functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                        functionsClass.removeLine(".SplitSuper", createdSplitListItem.get(position).getCategory());
                        splitShortcuts.deleteFile(createdSplitListItem.get(position).getCategory());
                        splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                } else {
                    PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                    functionsClass.saveFileAppendLine(".SplitSuper", PublicVariable.categoryName);
                    functionsClass.saveFileEmpty(PublicVariable.categoryName);
                    splitShortcuts.startActivity(new Intent(splitShortcuts, SplitAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(splitShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                }
            }
        });

        viewHolderBinder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!category.equals(splitShortcuts.getPackageName())) {
                        PublicVariable.categoryNameSelected = functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory());

                        File fileSelected = splitShortcuts.getFileStreamPath(PublicVariable.categoryNameSelected);
                        if (fileSelected.exists() && fileSelected.isFile()) {
                            splitShortcuts.deleteFile(PublicVariable.categoryNameSelected);
                            functionsClass.removeLine(".SplitSuperSelected", PublicVariable.categoryNameSelected);
                            if (functionsClass.mixShortcuts()) {
                                functionsClass.removeLine(".mixShortcuts", PublicVariable.categoryNameSelected);
                            }
                            viewHolderBinder.autoChoice.setChecked(false);
                            splitShortcuts.sendBroadcast(new Intent(splitShortcuts.getString(R.string.counterActionSplitShortcuts)));
                            splitShortcuts.sendBroadcast(new Intent(splitShortcuts.getString(R.string.dynamicShortcutsSplit)));
                        } else {
                            if (functionsClass.mixShortcuts()) {
                                if (functionsClass.countLine(".mixShortcuts") < functionsClass.getSystemMaxAppShortcut()) {
                                    String[] appsContent = functionsClass.readFileLine(createdSplitListItem.get(position).getCategory());
                                    for (String appContent : appsContent) {
                                        functionsClass.saveFileAppendLine(PublicVariable.categoryNameSelected, appContent);
                                    }
                                    functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryNameSelected);
                                    functionsClass.saveFileAppendLine(".mixShortcuts", PublicVariable.categoryNameSelected);

                                    viewHolderBinder.autoChoice.setChecked(true);
                                    splitShortcuts.sendBroadcast(new Intent(splitShortcuts.getString(R.string.counterActionSplitShortcuts)));
                                    splitShortcuts.sendBroadcast(new Intent(splitShortcuts.getString(R.string.dynamicShortcutsSplit)));
                                }
                            } else {
                                if (PublicVariable.SplitShortcutsMaxAppShortcutsCounter < PublicVariable.SplitShortcutsMaxAppShortcuts) {
                                    String[] appsContent = functionsClass.readFileLine(createdSplitListItem.get(position).getCategory());
                                    for (String appContent : appsContent) {
                                        functionsClass.saveFileAppendLine(PublicVariable.categoryNameSelected, appContent);
                                    }
                                    functionsClass.saveFileAppendLine(".SplitSuperSelected", PublicVariable.categoryNameSelected);

                                    viewHolderBinder.autoChoice.setChecked(true);
                                    splitShortcuts.sendBroadcast(new Intent(splitShortcuts.getString(R.string.counterActionSplitShortcuts)));
                                    splitShortcuts.sendBroadcast(new Intent(splitShortcuts.getString(R.string.dynamicShortcutsSplit)));
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
                if (!createdSplitListItem.get(position).getCategory().equals(splitShortcuts.getPackageName())) {
                    String[] categoryChoices = new String[]{
                            splitShortcuts.getString(R.string.addSuper),
                            splitShortcuts.getString(R.string.addToDesktop),
                            splitShortcuts.getString(R.string.deleteFolder)
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(splitShortcuts);
                    builder.setTitle(Html.fromHtml("<small>" + splitShortcuts.getString(R.string.option) + " " + "<b>" + createdSplitListItem.get(position).getCategory().split("_")[0] + "</b></small>", Html.FROM_HTML_MODE_LEGACY));
                    builder.setSingleChoiceItems(categoryChoices, 0, null);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            switch (selectedPosition) {
                                case 0:
                                    functionsClass.addToSuperShortcuts(createdSplitListItem.get(position).getCategory());
                                    break;
                                case 1:
                                    functionsClass.splitToDesktop(createdSplitListItem.get(position).getCategory());
                                    break;
                                case 2:
                                    try {
                                        if (splitShortcuts.getFileStreamPath(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory())).exists()) {
                                            functionsClass.removeLine(".SplitSuperSelected", functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory()));
                                            splitShortcuts.deleteFile(functionsClass.splitNameSelected(createdSplitListItem.get(position).getCategory()));
                                        }
                                        String[] packsToDelete = functionsClass.readFileLine(createdSplitListItem.get(position).getCategory());
                                        for (String packToDelete : packsToDelete) {
                                            splitShortcuts.deleteFile(packToDelete + createdSplitListItem.get(position).getCategory());
                                        }
                                        functionsClass.removeLine(".SplitSuper", createdSplitListItem.get(position).getCategory());
                                        splitShortcuts.deleteFile(createdSplitListItem.get(position).getCategory());
                                        functionsClass.removeHomeShortcut(".split.SplitTransparentPair", "load_split_action_pair_shortcut", Intent.CATEGORY_DEFAULT, createdSplitListItem.get(position).getCategory());
                                        splitShortcuts.sendBroadcast(new Intent(splitShortcuts.getString(R.string.checkboxActionSplitShortcuts)));
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
                if (isChecked) {
                    if (PublicVariable.SplitShortcutsMaxAppShortcutsCounter < PublicVariable.SplitShortcutsMaxAppShortcuts) {
                        PublicVariable.SplitShortcutsMaxAppShortcutsCounter++;
                    }
                } else if (!isChecked) {
                    PublicVariable.SplitShortcutsMaxAppShortcutsCounter = PublicVariable.SplitShortcutsMaxAppShortcutsCounter - 1;
                }
            }
        });

        PublicVariable.SplitShortcutsMaxAppShortcutsCounter = functionsClass.countLine(".SplitSuperSelected");
    }

    @Override
    public int getItemCount() {
        return createdSplitListItem.size();
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
