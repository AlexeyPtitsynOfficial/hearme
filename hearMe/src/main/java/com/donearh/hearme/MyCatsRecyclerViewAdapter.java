package com.donearh.hearme;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

public class MyCatsRecyclerViewAdapter extends RecyclerView.Adapter<MyCatsRecyclerViewAdapter.ViewHolder> {

    public static final int DRAWER_TYPE = 11;
    public static final int CAT_TYPE = 22;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CAT = 1;
    private static final int TYPE_FOOTER = 2;

    private int mType;
    private List<String> mHeaderList;
    private CategoryData mAllCat;
    private List<CategoryData> mValues;
    private final ArrayList<CategoryData> mCategoryData;
    private Context mContext;
    private FloatingActionButton mBtnBack;
    private TextView mHeaderText;

    private OnCatChoosedListener mListener;

    public interface OnCatChoosedListener {
        void onCatChoosed(String cat_tree, CategoryData cat);
    }

    public MyCatsRecyclerViewAdapter(int type, FloatingActionButton btn_back ,TextView header, Context context, ArrayList<CategoryData> items, OnCatChoosedListener listener) {
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cat_chooser_cats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).Id);
        Glide.with(mContext)
                .load(Urls.SERVER_URL+ mValues.get(position).icon_img)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.cat_all)
                .into(new BitmapImageViewTarget(holder.mCatIcon));
        holder.mCatName.setText(mValues.get(position).name);
        holder.mCatName.setTag(mValues.get(position));
        holder.mBtnSub.setTag(mValues.get(position));
        if(mType == CAT_TYPE || mValues.get(position).id.intValue() == -1){
            holder.mBtnSub.setVisibility(View.GONE);
        }
        else
            holder.mBtnSub.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {

        return TYPE_CAT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final ImageView mCatIcon;
        public final TextView mCatName;
        public final ImageButton mBtnSub;
        public CategoryData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCatIcon = (ImageView) view.findViewById(R.id.cat_icon);
            mCatName = (TextView) view.findViewById(R.id.cat_name);
            mBtnSub = (ImageButton) view.findViewById(R.id.btn_sub);
            mCatName.setOnClickListener(this);
            if(mType == DRAWER_TYPE)
                mBtnSub.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCatName.getText() + "'";
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
}
