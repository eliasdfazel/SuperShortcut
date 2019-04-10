package net.geekstools.supershortcuts.PRO.Util.NavAdapter;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {

    String packageName, className, appName, category;
    String[] packageNames;
    Drawable appIcon;

    public NavDrawerItem(String appName, String packageName, String className, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.className = className;
        this.appIcon = appIcon;
    }

    public NavDrawerItem(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
    }

    public NavDrawerItem(String category, String[] packageNames) {
        this.category = category;
        this.packageNames = packageNames;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getClassName() {
        return this.className;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getCategory() {
        return this.category;
    }

    public String[] getPackageNames() {
        return this.packageNames;
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }
}
