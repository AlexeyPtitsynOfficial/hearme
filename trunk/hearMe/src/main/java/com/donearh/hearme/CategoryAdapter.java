package com.donearh.hearme;

import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donearh on 01.06.2016.
 */
public class CategoryAdapter extends BaseAdapter implements View.OnClickListener {

    public static final int DRAWER_TYPE = 11;
    public static final int CAT_TYPE = 22;

    private Context mContext;

    private int mType;
    private List<String> mHeaderList;
    private CategoryData mAllCat;
    private List<CategoryData> mValues;
    private final ArrayList<CategoryData> mCategoryData;
    private FloatingActionButton mBtnBack;
    private TextView mHeaderText;

    private OnCatChoosedListener mListener;

    public interface OnCatChoosedListener {
        void onCatChoosed(String cat_tree, CategoryData cat);
    }

    public enum CatRowType{
        HEADER,
        ITEM
    }

    public CategoryAdapter(int type, FloatingActionButton btn_back , TextView header, Context context, ArrayList<CategoryData> items, OnCatChoosedListener listener) {
        mType = type;
        mContext = context;
        mCategoryData = items;
        mListener = listener;
        mHeaderList = new ArrayList<>();
        mHeaderList.add(mContext.getString(R.string.main_cats));
        mBtnBack = btn_back;
        mHeaderText = header;
        mHeaderText.setText(mHeaderList.get(mHeaderList.size()-1));
        mValues = new ArrayList<CategoryData>();

        mAllCat = new CategoryData();
        mAllCat.id = -1;
        mAllCat.has_child = 0;
        mAllCat.name = mContext.getString(R.string.all_cats);
        mAllCat.icon_img = "";
        mValues.add(mAllCat);
        for (CategoryData cat : mCategoryData) {
            if(cat.parent_id == 0)
                mValues.add(cat);
        }
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView title;
        public ImageButton btn_sub;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cat_chooser_cats, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.cat_icon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.cat_name);
            viewHolder.title.setOnClickListener(this);
            viewHolder.btn_sub = (ImageButton) convertView.findViewById(R.id.btn_sub);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Glide.with(mContext)
                .load(Urls.SERVER_URL+ mValues.get(position).icon_img)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.cat_all)
                .into(new BitmapImageViewTarget(viewHolder.icon));
        viewHolder.title.setText(mValues.get(position).name);
        viewHolder.title.setTag(mValues.get(position));
        viewHolder.btn_sub.setTag(mValues.get(position));

        if(mValues.get(position).id.intValue() == -1)
            viewHolder.btn_sub.setVisibility(View.GONE);

        return convertView;
    }

    private String getCatTreeText(){
        String text ="";
        for (int i = 1; i<mHeaderList.size(); i++) {
            text += mHeaderList.get(i);
            if(i < mHeaderList.size()-1)
                text += " > ";
        }
        mHeaderList.remove(mHeaderList.size()-1);
        return text;
    }

    @Override
    public void onClick(View v) {
        CategoryData category = (CategoryData) v.getTag();
        mHeaderList.add(category.name);
        switch (v.getId()) {
            case R.id.cat_name:
                if (category.has_child == 0) {
                    String cat_text = "";
                    if(category.id == -1)
                        cat_text = mContext.getString(R.string.all_cats);
                    else
                        cat_text = getCatTreeText();
                    mListener.onCatChoosed(cat_text, category);
                    return;
                }

                mValues.clear();
                //mValues.add(mAllCat);
                for (CategoryData cat : mCategoryData) {
                    if (cat.parent_id.equals(category.id)) {
                        cat.icon_img = category.icon_img;
                        cat.left_color = category.left_color;
                        mValues.add(cat);
                    }
                }
                if (mValues.size() != 0) {
                    mBtnBack.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                    // ((LinearLayoutManager) MyCatsRecyclerViewAdapter.this.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                }

                mHeaderText.setText(mHeaderList.get(mHeaderList.size() - 1));
                break;
            case R.id.btn_sub:
                String cat_text = "";
                if(category.id == -1)
                    cat_text = mContext.getString(R.string.all_cats);
                else
                    cat_text = getCatTreeText();
                mListener.onCatChoosed(cat_text, category);
                break;

        }
    }

    public void backPress(){
        mHeaderList.remove(mHeaderList.size()-1);
        mHeaderText.setText(mHeaderList.get(mHeaderList.size()-1));
        Integer cat_id = mValues.get(1).parent_id;
        for(CategoryData cat : mCategoryData){
            if(cat.id.equals(cat_id)){
                cat_id = cat.parent_id;
                break;
            }
        }
        mValues.clear();

        for(CategoryData cat : mCategoryData){
            if(cat.parent_id.equals(cat_id)){
                mValues.add(cat);
            }
        }
        if(mValues.size() != 0){
            if(mValues.get(1).parent_id == 0) {
                mValues.add(0, mAllCat);
                mBtnBack.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }
    }

}
