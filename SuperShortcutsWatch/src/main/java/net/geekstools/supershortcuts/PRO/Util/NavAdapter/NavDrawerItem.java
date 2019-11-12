/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:22 PM
 * Last modified 11/11/19 7:21 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Util.NavAdapter;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {

    CharSequence charTitle;
    String title, desc, extra = "0", category;
    String[] packName;
    int widgetID;
    Drawable icon, run;
    boolean isExtraVisible = false;

    public NavDrawerItem(String title, String pack, Drawable icon) {
        this.desc = title;
        this.title = pack;
        this.icon = icon;
    }

    public NavDrawerItem(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public NavDrawerItem(CharSequence title, Drawable icon) {
        this.charTitle = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, Drawable icon, boolean isExtraVisible, String count, String desc) {
        this.title = title;
        this.desc = desc;
        this.icon = icon;
        this.isExtraVisible = isExtraVisible;
        this.extra = count;
    }

    public NavDrawerItem(String widgetTitle, Drawable appIcon, Drawable runWidget, int widgetID) {
        this.title = widgetTitle;
        this.icon = appIcon;
        this.run = runWidget;
        this.widgetID = widgetID;
    }

    public NavDrawerItem(String category, String[] packageNames) {
        this.category = category;
        this.packName = packageNames;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CharSequence getCharTitle() {
        return this.charTitle;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String newCategory) {
        this.category = newCategory;
    }

    public String[] getPackName() {
        return this.packName;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getRunIcon() {
        return this.run;
    }

    public int getWidgetID() {
        return this.widgetID;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String exrra) {
        this.extra = exrra;
    }

    public boolean getCounterVisibility() {
        return this.isExtraVisible;
    }

    public void setCounterVisibility(boolean isCounterVisible) {
        this.isExtraVisible = isExtraVisible;
    }
}
