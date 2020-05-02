/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/2/20 2:30 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts.Adapters;

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

import net.geekstools.supershortcuts.PRO.FoldersShortcuts.ApplicationsSelectionProcess.FolderAppSelectionList;
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.io.File;
import java.util.ArrayList;

public class FolderShortcutsAdapter extends RecyclerView.Adapter<FolderShortcutsAdapter.ViewHolder> {

    private FolderShortcuts folderShortcuts;

    FunctionsClass functionsClass;

    String endEdited = "",
            endFocus = "";

    LoadCustomIcons loadCustomIcons;

    private ArrayList<AdapterItemsData> createdFolderListItem;

    public FolderShortcutsAdapter(FolderShortcuts folderShortcuts,
                                  ArrayList<AdapterItemsData> createdFolderListItem) {
        this.folderShortcuts = folderShortcuts;

        this.createdFolderListItem = createdFolderListItem;

        functionsClass = new FunctionsClass(folderShortcuts);

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(folderShortcuts, functionsClass.customIconPackageName());
        }
    }

    @Override
    public FolderShortcutsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new FolderShortcutsAdapter.ViewHolder(LayoutInflater.from(folderShortcuts).inflate(R.layout.item_advance_shortcuts, parent, false));
    }

    @Override
    public void onBindViewHolder(FolderShortcutsAdapter.ViewHolder viewHolderBinder, final int position) {

        final String category = createdFolderListItem.get(position).getCategory();
        final String[] packages = createdFolderListItem.get(position).getPackageNames();

        try {
            viewHolderBinder.categoryName.setText(createdFolderListItem.get(position).getCategory().split("_")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (category.equals(folderShortcuts.getPackageName())) {
            try {
                viewHolderBinder.categoryName.setText("");
                viewHolderBinder.autoChoice.setChecked(false);
                viewHolderBinder.addApps.setVisibility(View.INVISIBLE);
                viewHolderBinder.selectedApps.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File autoFile = folderShortcuts.getFileStreamPath(category);
            if (autoFile.exists() && autoFile.isFile()) {
                viewHolderBinder.selectedApps.removeAllViews();
                viewHolderBinder.selectedApps.setVisibility(View.VISIBLE);
                int previewItems = 7;
                if (packages.length < 7) {
                    previewItems = packages.length;
                }
                for (int i = 0; i < previewItems; i++) {
                    RelativeLayout selectedApplicationsPreview = (RelativeLayout) folderShortcuts.getLayoutInflater().inflate(R.layout.selected_apps_item, null);
                    ImageView imageView = (ImageView) selectedApplicationsPreview.findViewById(R.id.appSelectedItem);
                    if (functionsClass.isAppInstalled(packages[i])) {
                        imageView.setImageDrawable(functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(packages[i], functionsClass.appIconDrawable(packages[i])) : functionsClass.appIconDrawable(packages[i]));
                    }
                    viewHolderBinder.selectedApps.addView(selectedApplicationsPreview);
                    viewHolderBinder.addApps.setVisibility(View.VISIBLE);
                }
            }

            File autoFileSelected = folderShortcuts.getFileStreamPath(functionsClass.categoryNameSelected(category));
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

                    File file = folderShortcuts.getFileStreamPath(createdFolderListItem.get(position).getCategory());
                    if (file.exists() && file.isFile()) {
                        if (createdFolderListItem.get(position).getCategory().equals(PublicVariable.categoryName)) {
                            PublicVariable.categoryName = createdFolderListItem.get(position).getCategory();
                            folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                        } else {

                            //selectedCategory
                            if (folderShortcuts.getFileStreamPath(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory())).exists()) {
                                String[] appsContent = functionsClass.readFileLine(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                                for (String appContent : appsContent) {
                                    folderShortcuts.deleteFile(functionsClass.categoryNameSelected(appContent + createdFolderListItem.get(position).getCategory()));
                                    functionsClass.saveFileAppendLine(functionsClass.categoryNameSelected(PublicVariable.categoryName), appContent);
                                    functionsClass.saveFile(functionsClass.categoryNameSelected(appContent + PublicVariable.categoryName), appContent);
                                }
                                functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryName);
                                functionsClass.removeLine(".categorySuperSelected", createdFolderListItem.get(position).getCategory());
                                folderShortcuts.deleteFile(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                            }

                            //selectedSplit
                            if (functionsClass.mixShortcuts()) {
                                functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                            }

                            String[] appsContent = functionsClass.readFileLine(createdFolderListItem.get(position).getCategory());
                            for (String appContent : appsContent) {
                                folderShortcuts.deleteFile(appContent + createdFolderListItem.get(position).getCategory());
                                functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                                functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                            }
                            functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                            functionsClass.removeLine(".categorySuper", createdFolderListItem.get(position).getCategory());
                            folderShortcuts.deleteFile(createdFolderListItem.get(position).getCategory());
                            folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                        }
                    } else {
                        PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                        functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                        functionsClass.saveFileEmpty(PublicVariable.categoryName);
                        folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
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
                    if (!createdFolderListItem.get(position).getCategory().equals(folderShortcuts.getPackageName())) {
                        PublicVariable.categoryName = createdFolderListItem.get(position).getCategory();
                    } else {
                        if (endEdited.length() > 0) {
                            PublicVariable.categoryName = endEdited;
                        }
                    }
                }

                File file = folderShortcuts.getFileStreamPath(createdFolderListItem.get(position).getCategory());
                if (file.exists() && file.isFile()) {
                    if (createdFolderListItem.get(position).getCategory().equals(PublicVariable.categoryName)) {
                        PublicVariable.categoryName = createdFolderListItem.get(position).getCategory();
                        folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    } else {

                        //selectedCategory
                        if (folderShortcuts.getFileStreamPath(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory())).exists()) {
                            String[] appsContent = functionsClass.readFileLine(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                            for (String appContent : appsContent) {
                                folderShortcuts.deleteFile(functionsClass.categoryNameSelected(appContent + createdFolderListItem.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(functionsClass.categoryNameSelected(PublicVariable.categoryName), appContent);
                                functionsClass.saveFile(functionsClass.categoryNameSelected(appContent + PublicVariable.categoryName), appContent);
                            }
                            functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryName);
                            functionsClass.removeLine(".categorySuperSelected", createdFolderListItem.get(position).getCategory());
                            folderShortcuts.deleteFile(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                        }

                        //selectedSplit
                        if (functionsClass.mixShortcuts()) {
                            functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                            functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                        }

                        String[] appsContent = functionsClass.readFileLine(createdFolderListItem.get(position).getCategory());
                        for (String appContent : appsContent) {
                            folderShortcuts.deleteFile(appContent + createdFolderListItem.get(position).getCategory());
                            functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                            functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                        }
                        functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                        functionsClass.removeLine(".categorySuper", createdFolderListItem.get(position).getCategory());
                        folderShortcuts.deleteFile(createdFolderListItem.get(position).getCategory());
                        folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                } else {
                    PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                    functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                    functionsClass.saveFileEmpty(PublicVariable.categoryName);
                    folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                }
            }
        });
        viewHolderBinder.addApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!createdFolderListItem.get(position).getCategory().equals(folderShortcuts.getPackageName())) {
                    PublicVariable.categoryName = createdFolderListItem.get(position).getCategory();
                } else {
                    if (endEdited.length() > 0) {
                        PublicVariable.categoryName = endEdited;
                    }
                }

                File file = folderShortcuts.getFileStreamPath(createdFolderListItem.get(position).getCategory());
                if (file.exists() && file.isFile()) {
                    if (createdFolderListItem.get(position).getCategory().equals(PublicVariable.categoryName)) {
                        PublicVariable.categoryName = createdFolderListItem.get(position).getCategory();
                        folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    } else {

                        //selectedCategory
                        if (folderShortcuts.getFileStreamPath(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory())).exists()) {
                            String[] appsContent = functionsClass.readFileLine(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                            for (String appContent : appsContent) {
                                folderShortcuts.deleteFile(functionsClass.categoryNameSelected(appContent + createdFolderListItem.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(functionsClass.categoryNameSelected(PublicVariable.categoryName), appContent);
                                functionsClass.saveFile(functionsClass.categoryNameSelected(appContent + PublicVariable.categoryName), appContent);
                            }
                            functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryName);
                            functionsClass.removeLine(".categorySuperSelected", createdFolderListItem.get(position).getCategory());
                            folderShortcuts.deleteFile(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                        }

                        //selectedSplit
                        if (functionsClass.mixShortcuts()) {
                            functionsClass.removeLine(".mixShortcuts", functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                            functionsClass.saveFileAppendLine(".mixShortcuts", functionsClass.categoryNameSelected(PublicVariable.categoryName));
                        }

                        String[] appsContent = functionsClass.readFileLine(createdFolderListItem.get(position).getCategory());
                        for (String appContent : appsContent) {
                            folderShortcuts.deleteFile(appContent + createdFolderListItem.get(position).getCategory());
                            functionsClass.saveFileAppendLine(PublicVariable.categoryName, appContent);
                            functionsClass.saveFile(appContent + PublicVariable.categoryName, appContent);
                        }
                        functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                        functionsClass.removeLine(".categorySuper", createdFolderListItem.get(position).getCategory());
                        folderShortcuts.deleteFile(createdFolderListItem.get(position).getCategory());
                        folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                } else {
                    PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                    functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                    functionsClass.saveFileEmpty(PublicVariable.categoryName);
                    folderShortcuts.startActivity(new Intent(folderShortcuts, FolderAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(folderShortcuts, R.anim.down_up, android.R.anim.fade_out).toBundle());
                }
            }
        });

        viewHolderBinder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!category.equals(folderShortcuts.getPackageName())) {
                        PublicVariable.categoryNameSelected = functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory());

                        File fileSelected = folderShortcuts.getFileStreamPath(PublicVariable.categoryNameSelected);
                        if (fileSelected.exists() && fileSelected.isFile()) {
                            folderShortcuts.deleteFile(PublicVariable.categoryNameSelected);
                            functionsClass.removeLine(".categorySuperSelected", PublicVariable.categoryNameSelected);
                            if (functionsClass.mixShortcuts() == true) {
                                functionsClass.removeLine(".mixShortcuts", PublicVariable.categoryNameSelected);
                            }
                            viewHolderBinder.autoChoice.setChecked(false);

                            folderShortcuts.savedShortcutCounter();

                            folderShortcuts.reevaluateShortcutsInfo();

                        } else {
                            if (functionsClass.mixShortcuts() == true) {
                                if (functionsClass.countLine(".mixShortcuts") < functionsClass.getSystemMaxAppShortcut()) {
                                    String[] appsContent = functionsClass.readFileLine(createdFolderListItem.get(position).getCategory());
                                    for (String appContent : appsContent) {
                                        functionsClass.saveFileAppendLine(PublicVariable.categoryNameSelected, appContent);
                                    }
                                    functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryNameSelected);
                                    functionsClass.saveFileAppendLine(".mixShortcuts", PublicVariable.categoryNameSelected);

                                    viewHolderBinder.autoChoice.setChecked(true);

                                    folderShortcuts.savedShortcutCounter();

                                    folderShortcuts.reevaluateShortcutsInfo();

                                }
                            } else {
                                if (PublicVariable.advanceShortcutsMaxAppShortcutsCounter < PublicVariable.advanceShortcutsMaxAppShortcuts) {
                                    String[] appsContent = functionsClass.readFileLine(createdFolderListItem.get(position).getCategory());
                                    for (String appContent : appsContent) {
                                        functionsClass.saveFileAppendLine(PublicVariable.categoryNameSelected, appContent);
                                    }
                                    functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryNameSelected);

                                    viewHolderBinder.autoChoice.setChecked(true);

                                    folderShortcuts.savedShortcutCounter();

                                    folderShortcuts.reevaluateShortcutsInfo();

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
                if (!createdFolderListItem.get(position).getCategory().equals(folderShortcuts.getPackageName())) {
                    String[] categoryChoices = new String[]{
                            folderShortcuts.getString(R.string.addSuper),
                            folderShortcuts.getString(R.string.addToDesktop),
                            folderShortcuts.getString(R.string.deleteFolder)
                    };
                    final AlertDialog.Builder builder = new AlertDialog.Builder(folderShortcuts);
                    builder.setTitle(Html.fromHtml("<small>" + folderShortcuts.getString(R.string.option) + " " + "<b>" + createdFolderListItem.get(position).getCategory().split("_")[0] + "</b></small>", Html.FROM_HTML_MODE_LEGACY));
                    builder.setSingleChoiceItems(categoryChoices, 0, null);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            switch (selectedPosition) {
                                case 0:
                                    functionsClass.addToSuperShortcuts(createdFolderListItem.get(position).getCategory());
                                    break;
                                case 1:
                                    functionsClass.categoryToDesktop(createdFolderListItem.get(position).getCategory());
                                    break;
                                case 2:
                                    try {
                                        if (folderShortcuts.getFileStreamPath(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory())).exists()) {
                                            functionsClass.removeLine(".categorySuperSelected", functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                                            folderShortcuts.deleteFile(functionsClass.categoryNameSelected(createdFolderListItem.get(position).getCategory()));
                                        }
                                        String[] packsToDelete = functionsClass.readFileLine(createdFolderListItem.get(position).getCategory());
                                        for (String packToDelete : packsToDelete) {
                                            folderShortcuts.deleteFile(packToDelete + createdFolderListItem.get(position).getCategory());
                                        }
                                        functionsClass.removeLine(".categorySuper", createdFolderListItem.get(position).getCategory());
                                        folderShortcuts.deleteFile(createdFolderListItem.get(position).getCategory());
                                        functionsClass.removeHomeShortcut(".advanced.LoadCategoryItems", "load_category_action_shortcut", Intent.CATEGORY_DEFAULT, createdFolderListItem.get(position).getCategory());

                                        folderShortcuts.shortcutDeleted();

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
                    if (PublicVariable.advanceShortcutsMaxAppShortcutsCounter < PublicVariable.advanceShortcutsMaxAppShortcuts) {
                        PublicVariable.advanceShortcutsMaxAppShortcutsCounter++;
                    }
                } else if (isChecked == false) {
                    PublicVariable.advanceShortcutsMaxAppShortcutsCounter = PublicVariable.advanceShortcutsMaxAppShortcutsCounter - 1;
                }
            }
        });

        PublicVariable.advanceShortcutsMaxAppShortcutsCounter = functionsClass.countLine(".categorySuperSelected");
    }

    @Override
    public int getItemCount() {
        return createdFolderListItem.size();
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
