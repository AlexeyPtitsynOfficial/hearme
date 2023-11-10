package com.donearh.hearme.Items;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Donearh on 01.06.2016.
 */
public interface CategoryItems {
    public int getViewType();
    public int getCatType();
    public String getCatTitle();
    public int getIconId();
    public View getView(LayoutInflater inflater, View convertView);
}
