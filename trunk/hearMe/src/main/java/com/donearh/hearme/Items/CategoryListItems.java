package com.donearh.hearme.Items;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.donearh.hearme.CategoryAdapter;
import com.donearh.hearme.CategoryData;
import com.donearh.hearme.MyCatsRecyclerViewAdapter;
import com.donearh.hearme.R;
import com.donearh.hearme.Urls;

/**
 * Created by Donearh on 01.06.2016.
 */
public class CategoryListItems implements CategoryItems {

    private Context mContext;
    private final CategoryData mData;
    public CategoryListItems(Context context, CategoryData data){
        mContext = context;
        mData = data;
    }

    @Override
    public int getViewType() {
        return CategoryAdapter.CatRowType.ITEM.ordinal();
    }

    @Override
    public int getCatType() {
        return 0;
    }

    @Override
    public String getCatTitle() {
        return mData.name;
    }

    @Override
    public int getIconId() {
        return 0;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView title;
        public ImageButton btn_sub;
        public CategoryData mItem;
    }
    @Override
    public View getView(LayoutInflater inflater, View convertView) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cat_chooser_cats, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.cat_icon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.cat_name);
            viewHolder.btn_sub = (ImageButton) convertView.findViewById(R.id.btn_sub);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //holder.mIdView.setText(mValues.get(position).Id);
        Glide.with(mContext)
                .load(Urls.SERVER_URL+ mData.icon_img)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.cat_all)
                .into(new BitmapImageViewTarget(viewHolder.icon));
        viewHolder.title.setText(mData.name);
        viewHolder.title.setTag(mData);
        viewHolder.btn_sub.setTag(mData);

        if(mData.id.intValue() == -1)
            viewHolder.btn_sub.setVisibility(View.GONE);

        return convertView;
    }
}
