package com.donearh.hearme;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

public interface Item {
    public int getViewType();
    public int getMenuType();
    public String getMenuTitle();
    public Drawable getIcon();
    public int getIconId();
    public View getView(LayoutInflater inflater, View convertView);
}
