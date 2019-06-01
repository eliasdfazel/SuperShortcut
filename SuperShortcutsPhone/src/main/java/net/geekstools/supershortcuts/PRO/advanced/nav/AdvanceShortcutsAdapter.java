package net.geekstools.supershortcuts.PRO.advanced.nav;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.advanced.AdvanceAppSelectionList;

import java.io.File;
import java.util.ArrayList;

public class AdvanceShortcutsAdapter extends RecyclerView.Adapter<AdvanceShortcutsAdapter.ViewHolder> {

    FunctionsClass functionsClass;
    RelativeLayout item;
    LinearLayout[] selectedApps;
    CheckBox[] autoChoice;
    EditText[] categoryName;
    ImageView[] addApps;
    ImageView imageView;
    RelativeLayout freqLayout;
    String endEdited = "", endFocus = "";
    View view;
    ViewHolder viewHolder;
    LoadCustomIcons loadCustomIcons;
    private Context context;
    private Activity activity;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public AdvanceShortcutsAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        selectedApps = new LinearLayout[navDrawerItems.size()];
        autoChoice = new CheckBox[navDrawerItems.size()];
        categoryName = new EditText[navDrawerItems.size()];
        addApps = new ImageView[navDrawerItems.size()];
        functionsClass = new FunctionsClass(context, activity);

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
        }
    }

    @Override
    public AdvanceShortcutsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_advance_shortcuts, parent, false);
        viewHolder = new AdvanceShortcutsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdvanceShortcutsAdapter.ViewHolder viewHolderBinder, final int position) {
        item = viewHolderBinder.item;
        selectedApps[position] = viewHolderBinder.selectedApps;
        categoryName[position] = viewHolderBinder.categoryName;
        categoryName[position].bringToFront();
        autoChoice[position] = viewHolderBinder.autoChoice;
        addApps[position] = viewHolderBinder.addApps;
        addApps[position].bringToFront();

        final String category = navDrawerItems.get(position).getCategory();
        final String[] packages = navDrawerItems.get(position).getPackageNames();

        try {
            categoryName[position].setText(navDrawerItems.get(position).getCategory().split("_")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (category.equals(context.getPackageName())) {
            try {
                categoryName[position].setText("");
                autoChoice[position].setChecked(false);
                addApps[position].setVisibility(View.INVISIBLE);
                selectedApps[position].removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File autoFile = context.getFileStreamPath(category);
            if (autoFile.exists() && autoFile.isFile()) {
                selectedApps[position].removeAllViews();
                selectedApps[position].setVisibility(View.VISIBLE);
                int previewItems = 7;
                if (packages.length < 7) {
                    previewItems = packages.length;
                }
                for (int i = 0; i < previewItems; i++) {
                    freqLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.selected_apps_item, null);
                    imageView = (ImageView) freqLayout.findViewById(R.id.appSelectedItem);
                    if (functionsClass.appInstalledOrNot(packages[i])) {
                        imageView.setImageDrawable(functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packages[i], functionsClass.appIconDrawable(packages[i])) : functionsClass.appIconDrawable(packages[i]));
                    }
                    selectedApps[position].addView(freqLayout);
                    addApps[position].setVisibility(View.VISIBLE);
                }
            }

            File autoFileSelected = context.getFileStreamPath(functionsClass.categoryNameSelected(category));
            autoChoice[position].setChecked(false);
            if (autoFileSelected.exists() && autoFileSelected.isFile()) {
                autoChoice[position].setChecked(true);
            } else {
                autoChoice[position].setChecked(false);
            }
        }

        categoryName[position].setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PublicVariable.categoryName = textView.getText().toString();

                    File file = context.getFileStreamPath(navDrawerItems.get(position).getCategory());
                    if (file.exists() && file.isFile()) {
                        if (navDrawerItems.get(position).getCategory().equals(PublicVariable.categoryName)) {
                            PublicVariable.categoryName = navDrawerItems.get(position).getCategory();
                            context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                        } else {

                            //selectedCategory
                            if (context.getFileStreamPath(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory())).exists()) {
                                String[] appsContent = functionsClass.readFileLine(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
                                for (String appContent : appsContent) {
                                    context.deleteFile(functionsClass.categoryNameSelected(appContent + navDrawerItems.get(position).getCategory()));
                                    functionsClass.saveFileAppendLine(functionsClass.categoryNameSelected(PublicVariable.categoryName), appContent);
                                    functionsClass.saveFile(functionsClass.categoryNameSelected(appContent + PublicVariable.categoryName), appContent);
                                }
                                functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryName);
                                functionsClass.removeLine(".categorySuperSelected", navDrawerItems.get(position).getCategory());
                                context.deleteFile(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
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
                            functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                            functionsClass.removeLine(".categorySuper", navDrawerItems.get(position).getCategory());
                            context.deleteFile(navDrawerItems.get(position).getCategory());
                            context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                        }
                    } else {
                        PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                        functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                        functionsClass.saveFileEmpty(PublicVariable.categoryName);
                        context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                }
                return true;
            }
        });
        categoryName[position].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                endEdited = editable.toString();
                if (addApps[position].isShown()) {
                    addApps[position].setVisibility(View.INVISIBLE);
                }
            }
        });
        selectedApps[position].setOnClickListener(new View.OnClickListener() {
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
                        context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    } else {

                        //selectedCategory
                        if (context.getFileStreamPath(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory())).exists()) {
                            String[] appsContent = functionsClass.readFileLine(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
                            for (String appContent : appsContent) {
                                context.deleteFile(functionsClass.categoryNameSelected(appContent + navDrawerItems.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(functionsClass.categoryNameSelected(PublicVariable.categoryName), appContent);
                                functionsClass.saveFile(functionsClass.categoryNameSelected(appContent + PublicVariable.categoryName), appContent);
                            }
                            functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryName);
                            functionsClass.removeLine(".categorySuperSelected", navDrawerItems.get(position).getCategory());
                            context.deleteFile(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
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
                        functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                        functionsClass.removeLine(".categorySuper", navDrawerItems.get(position).getCategory());
                        context.deleteFile(navDrawerItems.get(position).getCategory());
                        context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                } else {
                    PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                    functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                    functionsClass.saveFileEmpty(PublicVariable.categoryName);
                    context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                }
            }
        });
        addApps[position].setOnClickListener(new View.OnClickListener() {
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
                        context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    } else {

                        //selectedCategory
                        if (context.getFileStreamPath(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory())).exists()) {
                            String[] appsContent = functionsClass.readFileLine(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
                            for (String appContent : appsContent) {
                                context.deleteFile(functionsClass.categoryNameSelected(appContent + navDrawerItems.get(position).getCategory()));
                                functionsClass.saveFileAppendLine(functionsClass.categoryNameSelected(PublicVariable.categoryName), appContent);
                                functionsClass.saveFile(functionsClass.categoryNameSelected(appContent + PublicVariable.categoryName), appContent);
                            }
                            functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryName);
                            functionsClass.removeLine(".categorySuperSelected", navDrawerItems.get(position).getCategory());
                            context.deleteFile(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
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
                        functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                        functionsClass.removeLine(".categorySuper", navDrawerItems.get(position).getCategory());
                        context.deleteFile(navDrawerItems.get(position).getCategory());
                        context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                    }
                } else {
                    PublicVariable.categoryName = PublicVariable.categoryName + "_" + System.currentTimeMillis();

                    functionsClass.saveFileAppendLine(".categorySuper", PublicVariable.categoryName);
                    functionsClass.saveFileEmpty(PublicVariable.categoryName);
                    context.startActivity(new Intent(context, AdvanceAppSelectionList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());
                }
            }
        });

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!category.equals(context.getPackageName())) {
                        PublicVariable.categoryNameSelected = functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory());

                        File fileSelected = context.getFileStreamPath(PublicVariable.categoryNameSelected);
                        if (fileSelected.exists() && fileSelected.isFile()) {
                            context.deleteFile(PublicVariable.categoryNameSelected);
                            functionsClass.removeLine(".categorySuperSelected", PublicVariable.categoryNameSelected);
                            if (functionsClass.mixShortcuts() == true) {
                                functionsClass.removeLine(".mixShortcuts", PublicVariable.categoryNameSelected);
                            }
                            autoChoice[position].setChecked(false);
                            context.sendBroadcast(new Intent(context.getString(R.string.counterActionAdvanceShortcuts)));
                            context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcutsAdvance)));
                        } else {
                            if (functionsClass.mixShortcuts() == true) {
                                if (functionsClass.countLine(".mixShortcuts") < functionsClass.getSystemMaxAppShortcut()) {
                                    String[] appsContent = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                                    for (String appContent : appsContent) {
                                        functionsClass.saveFileAppendLine(PublicVariable.categoryNameSelected, appContent);
                                    }
                                    functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryNameSelected);
                                    functionsClass.saveFileAppendLine(".mixShortcuts", PublicVariable.categoryNameSelected);

                                    autoChoice[position].setChecked(true);
                                    context.sendBroadcast(new Intent(context.getString(R.string.counterActionAdvanceShortcuts)));
                                    context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcutsAdvance)));
                                }
                            } else {
                                if (PublicVariable.advanceShortcutsMaxAppShortcutsCounter < PublicVariable.advanceShortcutsMaxAppShortcuts) {
                                    String[] appsContent = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                                    for (String appContent : appsContent) {
                                        functionsClass.saveFileAppendLine(PublicVariable.categoryNameSelected, appContent);
                                    }
                                    functionsClass.saveFileAppendLine(".categorySuperSelected", PublicVariable.categoryNameSelected);

                                    autoChoice[position].setChecked(true);
                                    context.sendBroadcast(new Intent(context.getString(R.string.counterActionAdvanceShortcuts)));
                                    context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcutsAdvance)));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!navDrawerItems.get(position).getCategory().equals(context.getPackageName())) {
                    String[] categoryChoices = new String[]{
                            context.getString(R.string.addSuper),
                            context.getString(R.string.addToDesktop),
                            context.getString(R.string.deleteCategory)
                    };
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(Html.fromHtml("<small>" + context.getString(R.string.option) + " " + "<b>" + navDrawerItems.get(position).getCategory().split("_")[0] + "</b></small>"));
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
                                    functionsClass.categoryToDesktop(navDrawerItems.get(position).getCategory());
                                    break;
                                case 2:
                                    try {
                                        if (context.getFileStreamPath(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory())).exists()) {
                                            functionsClass.removeLine(".categorySuperSelected", functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
                                            context.deleteFile(functionsClass.categoryNameSelected(navDrawerItems.get(position).getCategory()));
                                        }
                                        String[] packsToDelete = functionsClass.readFileLine(navDrawerItems.get(position).getCategory());
                                        for (String packToDelete : packsToDelete) {
                                            context.deleteFile(packToDelete + navDrawerItems.get(position).getCategory());
                                        }
                                        functionsClass.removeLine(".categorySuper", navDrawerItems.get(position).getCategory());
                                        context.deleteFile(navDrawerItems.get(position).getCategory());
                                        functionsClass.removeHomeShortcut(".advanced.LoadCategoryItems", "load_category_action_shortcut", Intent.CATEGORY_DEFAULT, navDrawerItems.get(position).getCategory());
                                        context.sendBroadcast(new Intent(context.getString(R.string.checkboxActionAdvanceShortcuts)));
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

        autoChoice[position].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
