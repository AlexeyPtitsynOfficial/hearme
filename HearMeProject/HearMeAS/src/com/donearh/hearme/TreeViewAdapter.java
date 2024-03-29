package com.donearh.hearme;

import java.util.ArrayList;
import java.util.List;

import com.donearh.imageloader.ImageFromRes;
import com.donearh.imageloader.ImageFromResource;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TreeViewAdapter  extends BaseAdapter{
	private static final int TREE_ELEMENT_PADDING_VAL = 25;
	private List<TreeElementI> fileList;
	private Context context;
	private Bitmap iconCollapse;
	private Bitmap iconExpand;
	private EditText textLabel;
	private MainControlBarActivity treeView;
	private ImageFromResource mImageFromRes;

	public TreeViewAdapter(Context context, List<TreeElementI> fileList, MainControlBarActivity treeView) {
	    this.context = context;
	    this.fileList = fileList;
	    this.treeView = treeView;
	    mImageFromRes = new ImageFromResource(context);
	    iconCollapse = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_ad_add);
	    iconExpand = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_ad_category);
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

	@Override
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder;

	    convertView = View.inflate(context, R.layout.catalog_list_item, null);
	    holder = new ViewHolder();
	    holder.cat_icon = (RecyclingImageView) convertView.findViewById(R.id.cat_icon);
	    holder.setTextView((TextView) convertView.findViewById(R.id.text1));
	    holder.setImageView((ImageView) convertView.findViewById(R.id.tree_icon));
	    holder.setLayout((LinearLayout)convertView.findViewById(R.id.tree_item_layout));
	    convertView.setTag(holder);
	    
	    final TreeElementI elem = (TreeElementI) getItem(position);

	    int level = elem.getLevel();
	    if(level != 0)
	    {
	    	int level_color = level * 30;
	        GradientDrawable g = new GradientDrawable(Orientation.TL_BR, 
	        		new int[] { Color.rgb(23, 194-level_color, 184-level_color), 
	        		Color.rgb(7, 80-level_color, 87-level_color)});
	        g.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	        
	        int sdk = android.os.Build.VERSION.SDK_INT;
	        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
	        	holder.getLayout().setBackgroundDrawable(g);
	        } else {
	        	holder.getLayout().setBackground(g);
	        }
	    }
	    //holder.getIcon().setPadding(TREE_ELEMENT_PADDING_VAL * (level + 1), holder.icon.getPaddingTop(), 0, holder.icon.getPaddingBottom());
	    
	    	//holder.cat_icon.setImageDrawable(elem.getIcon());
	    //if(elem.getIcon() != null)
		    //new ImageFromRes(this.context, 
		    //		holder.cat_icon, elem.getIcon()).execute();
	    mImageFromRes.loadBitmap(elem.getIcon(), holder.cat_icon);
	    
	    holder.getText().setText(elem.getOutlineTitle());
	    if (elem.isHasChild() && (elem.isExpanded() == false)) {
	        holder.getIcon().setImageBitmap(iconCollapse);
	    } else if (elem.isHasChild() && (elem.isExpanded() == true)) {
	        holder.getIcon().setImageBitmap(iconExpand);
	    } else if (!elem.isHasChild()) {
	        holder.getIcon().setImageBitmap(iconCollapse);
	        holder.getIcon().setVisibility(View.INVISIBLE);
	    }

	    IconClickListener iconListener = new IconClickListener(this, position);
	    TextClickListener txtListener = new TextClickListener((ArrayList<TreeElementI>) this.getListData(), position);
	    holder.getIcon().setOnClickListener(iconListener);
	    holder.getText().setOnClickListener(txtListener);
	    return convertView;
	}

	private class ViewHolder {
		ImageView cat_icon;
	    ImageView icon;
	    TextView text;
	    LinearLayout layout;

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
	    	for(int i=0; i<((MainControlBarActivity)context).mCategoryData.size(); i++){
	    		if(list.get(position).getId().equals(((MainControlBarActivity)context).mCategoryData.get(i).Id.toString())){
	    			((MainControlBarActivity)context).createNeedCatTree(i, Integer.valueOf(list.get(position).getId()));
	    			 break;
	    		}
	    	}
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

	    public IconClickListener(TreeViewAdapter adapter, int position) {
	        this.list = (ArrayList<TreeElementI>) adapter.getListData();
	        this.adapter = adapter;
	        this.position = position;
	    }

	    @Override
	    public void onClick(View v) {
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
	            TreeElementI obj = list.get(position);
	            obj.setExpanded(true);
	            int level = obj.getLevel();
	            int nextLevel = level + 1;

	            for (TreeElementI element : obj.getChildList()) {
	                element.setLevel(nextLevel);
	                element.setExpanded(false);
	                list.add(position + 1, element);
	            }
	            adapter.notifyDataSetChanged();
	        }
	    }
	}
}
