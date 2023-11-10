package com.donearh.hearme;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donearh.imageloader.ImageCache.ImageCacheParams;
import com.donearh.imageloader.ImageFetcher;
import com.donearh.imageloader.ImageFromResource;

public class TreeViewAdapter  extends BaseAdapter{
	private static final int TREE_ELEMENT_PADDING_VAL = 25;
	private List<TreeElementI> fileList;
	private Context context;
	private Bitmap iconCollapse;
	private Bitmap iconExpand;
	private EditText textLabel;
	private MainControlBarActivity treeView;
	private ImageFromResource mImageFromRes;
	private ImageFetcher mImageFetcher;
	private ImageCacheParams cacheParams;

	public TreeViewAdapter(Context context, List<TreeElementI> fileList, MainControlBarActivity treeView) {
	    this.context = context;
	    this.fileList = fileList;
	    this.treeView = treeView;
	    mImageFromRes = new ImageFromResource(context);
	    iconCollapse = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree_open);
	    iconExpand = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree_close);
	    
	    
	    
	    cacheParams = new ImageCacheParams(context, "cats_icon");
	    cacheParams.setMemCacheSizePercent(0.10f);
	    mImageFetcher = new ImageFetcher(context, context.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
	    //mImageFetcher.setLoadingImage(R.drawable.slider_load);
	    mImageFetcher.addImageCache(((FragmentActivity) context).getSupportFragmentManager(), cacheParams);
	}

	public List<TreeElementI> getListData() {
	    return this.fileList;
	}

	@Override
	public int getCount() {
	    return this.fileList.size();
	}

	@Override
	public Object getItem(int position) {
	    return this.fileList.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}

	private class ViewHolder {
		ImageView cat_icon;
	    ImageView icon;
	    TextView text;
	    LinearLayout layout;
	    RelativeLayout cat_layout;

	    public TextView getText() {
	        return this.text;
	    }

	    public void setTextView(TextView text) {
	        this.text = text;
	    }

	    public ImageView getIcon() {
	        return this.icon;
	    }

	    public void setImageView(ImageView icon) {
	        this.icon = icon;
	    }
	    
	    public void setLayout(LinearLayout layout)
	    {
	    	this.layout = layout;
	    }
	    
	    public LinearLayout getLayout()
	    {
	    	return this.layout;
	    }
	}
	
	@Override
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder;
	    
	    convertView = View.inflate(context, R.layout.catalog_list_item, null);
	    holder = new ViewHolder();
	    holder.cat_layout = (RelativeLayout)convertView.findViewById(R.id.cat_icon_layout);
	    holder.cat_icon = (RecyclingImageView) convertView.findViewById(R.id.cat_icon);
	    holder.setTextView((TextView) convertView.findViewById(R.id.text1));
	    
	    holder.setImageView((ImageView) convertView.findViewById(R.id.tree_icon));
	    
	    holder.getIcon().setOnClickListener(new IconClickListener(this));
	    holder.getIcon().setFocusable(true);
	    holder.setLayout((LinearLayout)convertView.findViewById(R.id.tree_item_layout));
	    convertView.setTag(holder);
	    
	    final TreeElementI elem = (TreeElementI) getItem(position);

	    int level = elem.getLevel();
	    if(level != 0)
	    {
	    	int level_color = level * 30;
	    	int level_color2 = level * 10;
	        GradientDrawable g = new GradientDrawable(Orientation.TL_BR, 
	        		new int[] { Color.rgb(23, 104-level_color, 94-level_color), 
	        		Color.rgb(7, 60-level_color2, 67-level_color2)});
	        g.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	        
	        int sdk = android.os.Build.VERSION.SDK_INT;
	        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
	        	holder.getLayout().setBackgroundDrawable(g);
	        } else {
	        	holder.getLayout().setBackground(g);
	        }
	        
	        int cutter = holder.cat_layout.getLayoutParams().width/6*level;
	        int cutter_image = holder.cat_icon.getLayoutParams().width/8*level;
	        holder.cat_layout.setLayoutParams(
	        		new LinearLayout.LayoutParams(holder.cat_layout.getLayoutParams().width-cutter
	        		,holder.cat_layout.getLayoutParams().height-cutter));
	        holder.cat_icon.setLayoutParams(
	        		new RelativeLayout.LayoutParams(holder.cat_icon.getLayoutParams().width-cutter_image
	        		,holder.cat_icon.getLayoutParams().height-cutter_image));
	        holder.cat_icon.setImageLevel(level);
	    }
	    holder.getIcon().setTag(position);
	    
	    if(elem.getIcon() == "all")
	    	holder.cat_icon.setImageResource(R.drawable.cat_all);
	    else
	    	mImageFetcher.loadImage(elem.getIcon(), holder.cat_icon);
	    
	    
	    holder.getText().setText(elem.getOutlineTitle());
	    if(position != 0)
		    if (elem.isHasChild() && (elem.isExpanded() == false)) {
		        holder.getIcon().setImageBitmap(iconCollapse);
		    } else if (elem.isHasChild() && (elem.isExpanded() == true)) {
		        holder.getIcon().setImageBitmap(iconExpand);
		    } else if (!elem.isHasChild()) {
		        holder.getIcon().setImageBitmap(iconCollapse);
		        holder.getIcon().setVisibility(View.INVISIBLE);
		    }
	    
	    TextClickListener txtListener = new TextClickListener((ArrayList<TreeElementI>) this.getListData(), position);
	    holder.getText().setOnClickListener(txtListener);
	    
	    convertView.invalidate();
	    return convertView;
	}

	

	/**
	 * Listener For TreeElement Text Click
	 */
	private class TextClickListener implements View.OnClickListener {
	    private ArrayList<TreeElementI> list;
	    private int position;

	    public TextClickListener(ArrayList<TreeElementI> list, int position) {
	        this.list = list;
	        this.position = position;
	    }

	    @Override
	    public void onClick(View v) {
	       // treeView.setXValue(String.valueOf(list.get(position).getId()));
	    	((MainControlBarActivity)context).mCatIDsList.clear();
	    	((MainControlBarActivity)context).setMainParentId(-1);
	    	for(int i=0; i<((MainControlBarActivity)context).mCategoryData.size(); i++){
	    		if(list.get(position).getId().equals(((MainControlBarActivity)context).mCategoryData.get(i).Id.toString())){
	    			((MainControlBarActivity)context).createNeedCatTree(i, Integer.valueOf(list.get(position).getId()));
	    			((MainControlBarActivity)context).getMainParentId(i);
	    			 break;
	    		}
	    	}
	    	((MainControlBarActivity)context).getMainPage().setCurrentSlide(1);
	    	((MainControlBarActivity)context).getMainPage().getSliderPagerAdapterNoRotate().updateData(((MainControlBarActivity)context).getSelectedMainParentId());
	    	((MainControlBarActivity)context).selectItem(DrawerListAdapter.AD_ALL, -1);
	    }
	}
	
	
	/**
	 * Listener for TreeElement "Expand" button Click
	 */
	private class IconClickListener implements View.OnClickListener {
	    private ArrayList<TreeElementI> list;
	    private TreeViewAdapter adapter;
	    private int position;

	    public IconClickListener(TreeViewAdapter adapter) {
	        this.list = (ArrayList<TreeElementI>) adapter.getListData();
	        this.adapter = adapter;
	    }

	    @Override
	    public void onClick(View v) {
	    	
	    	
			
	    	position = (Integer)v.getTag();
	    	if(position == 0){
	    		((MainControlBarActivity)adapter.context).selectItem(DrawerListAdapter.AD_ALL, -1);
	    		return;
	    	}
	    		
	        if (!list.get(position).isHasChild()) {
	            return;
	        }
	        
	        if (list.get(position).isExpanded()) {
	            list.get(position).setExpanded(false);
	            TreeElementI element = list.get(position);
	            ArrayList<TreeElementI> temp = new ArrayList<TreeElementI>();

	            for (int i = position + 1; i < list.size(); i++) {
	                if (element.getLevel() >= list.get(i).getLevel()) {
	                    break;
	                }
	                temp.add(list.get(i));
	            }
	            list.removeAll(temp);
	            adapter.notifyDataSetChanged();
	        } else {
	        	for (int j=0; j<list.size(); j++) {
					if(list.get(j).isExpanded()
							&& list.get(j).getLevel() == list.get(position).getLevel()
							&& !list.get(j).equals(list.get(position))){
						int closed_pos = j;
						list.get(j).setExpanded(false);
			            TreeElementI element = list.get(j);
			            ArrayList<TreeElementI> temp = new ArrayList<TreeElementI>();

			            for (int i = j + 1; i < list.size(); i++) {
			                if (element.getLevel() >= list.get(i).getLevel()) {
			                    break;
			                }
			                temp.add(list.get(i));
			            }
			            int correct_size = temp.size();
			            list.removeAll(temp);
			            temp.clear();
			            temp = null;
			            if(closed_pos < position)
			            	position -= correct_size;
			            adapter.notifyDataSetChanged();
					}
				}
	        	
	            TreeElementI obj = list.get(position);
	            obj.setExpanded(true);
	            int level = obj.getLevel();
	            int nextLevel = level + 1;

	            for (int i=0; i < obj.getChildList().size(); i++) {
	            	obj.getChildList().get(i).setLevel(nextLevel);
	            	obj.getChildList().get(i).setExpanded(false);
	            	obj.getChildList().get(i).setIcon(list.get(position).getIcon());
	                if(i == 0)
	                	obj.getChildList().get(i).setlast(true);
	                list.add(position + 1, obj.getChildList().get(i));
	            }
	            adapter.notifyDataSetChanged();
	            ((MainControlBarActivity)adapter.context).getCatDrawerList().setSelection(position);
	            
	            /*((MainControlBarActivity)adapter.context).getCatDrawerList().post(new Runnable() 
	            {
	                @Override
	                public void run() 
	                {
	                	((MainControlBarActivity)adapter.context).getCatDrawerList().setSelection(position);
	                    View v = ((MainControlBarActivity)adapter.context).getCatDrawerList().getChildAt(position);
	                    if (v != null) 
	                    {
	                        v.requestFocus();
	                    }
	                }
	            });*/
	            
	        }
	        
	       
	    }
	}
	
}
