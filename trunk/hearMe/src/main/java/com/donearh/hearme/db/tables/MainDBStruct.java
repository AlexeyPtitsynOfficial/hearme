package com.donearh.hearme.db.tables;

import android.provider.BaseColumns;

/**
 * Created by Donearh on 01.05.2016.
 */
public final class MainDBStruct {

    public static abstract class UpdateChecker implements BaseColumns{

        public static final String TABLE_NAME = "data_update_checker";
        public static final String ID = "id";
        public static final String UPDATE_NAME = "name";
        public static final String UPDATE_DATETIME = "update_datetime";
        public static final String INFO_TEXT = "info_text";
        public static final String STATE = " state";
    }

    public static abstract class AreasTable implements BaseColumns{

        public static final String TABLE_NAME = "area_table";
        public static final String ID = "area_id";
        public static final String NAME = "area_name";
    }

    public static abstract class CatsTable implements BaseColumns{

        public static final String TABLE_NAME = "category_table";
        public static final String ID = "category_id";
        public static final String NAME = "category_name";
        public static final String PARENT_ID = "parent_id";
        public static final String COLOR_ID = "color_id";
        public static final String HAS_CHILD = "has_child";
        public static final String LEFT_COLOR = "left_color";
        public static final String RIGHT_COLOR = "right_color";
        public static final String ICON = "icon_res";
    }

    public static abstract class LowerBarTable implements BaseColumns{

        public static final String TABLE_NAME = "lower_bar_table";
        public static final String ID = "area_id";
        public static final String MENU_TYPE = "menu_type";
        public static final String ICON_RES = "icon_res";
        public static final String SORT_FIELD = "sort_field";
    }
}
