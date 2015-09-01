package com.cuccs.dreambox.objects;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.R.drawable;
import com.cuccs.dreambox.R.id;
import com.cuccs.dreambox.R.layout;
import com.cuccs.dreambox.layouts.MyGridView;
import com.cuccs.dreambox.utils.AsyncImageLoader;
import com.cuccs.dreambox.utils.AsyncImageLoader.ImageCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

/**
 *自定义的ExpandableListAdapter
 */
public class myExpandableListAdapter extends BaseExpandableListAdapter implements OnChildClickListener {
	private List<TreeNode> treeNodes = new ArrayList<TreeNode>(); 
	private ArrayList<ArrayList<String>> mAllPhotoPaths;
	private ArrayList<String> imageResourceArray = new ArrayList<String>(); 
	public static final int ItemHeight = 70;// 每项的高度  
    public static final int PaddingLeft = 36;// 左边距
    private int myPaddingLeft; 
	private MyGridView mGridView;
	private Context parentContext;
	public myExpandableListAdapter(Context view, int myPaddingLeft, ArrayList<ArrayList<String>> mAllPhotoPaths)  
    {  
        parentContext = view;  
        this.myPaddingLeft = myPaddingLeft;
        this.mAllPhotoPaths = mAllPhotoPaths;
    } 
	
	/**自定义的TreeNode*/
	public static class TreeNode
    {  
        public Object parent;  
        public List<Object> childs = new ArrayList<Object>();  
    }
	public List<TreeNode> GetTreeNode()  
    { return treeNodes; } 
	public void UpdateTreeNode(List<TreeNode> nodes)  
    {  treeNodes = nodes;}  
	public void RemoveAll()  
    {  treeNodes.clear();}
	static public TextView getTextView(Context context)  
    {  
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(  
                ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);  
  
        TextView textView = new TextView(context);  
        textView.setLayoutParams(lp);  
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);  
        return textView;  
    }  
	
	@Override
	public Object getChild(int groupPosition, int childPosition)  
    {  
        return treeNodes.get(groupPosition).childs.get(childPosition);  
    } 

	@Override
	public long getChildId(int groupPosition, int childPosition)  
    {  
		return childPosition;  
    } 

	@Override
	public int getChildrenCount(int groupPosition)  {  
        return treeNodes.get(groupPosition).childs.size();  
    }
	
	@Override
	public View getChildView(int groupPosition, int childPosition,  
            boolean isLastChild, View convertView, ViewGroup parent){
			// TODO Auto-generated method stub
			LayoutInflater layoutInflater = (LayoutInflater) parentContext  
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			convertView = layoutInflater.inflate(R.layout.mgridview, null); 
			mGridView = (MyGridView) convertView  
					.findViewById(R.id.loadphotos_gridview);
			imageResourceArray = mAllPhotoPaths.get(groupPosition);
			mGridView.setAdapter(getGridviewAdapter(imageResourceArray, mGridView));// 设置菜单Adapter
			
			mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
	            @Override
	            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
	                // Pause fetcher to ensure smoother scrolling when flinging
	            	Toast.makeText(parentContext, "mGridView:" + mGridView.getLastVisiblePosition(), Toast.LENGTH_SHORT).show();  
	                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
	                    //mImageFetcher.setPauseWork(true);
	                } else {
	                    //mImageFetcher.setPauseWork(false);
	                }
	                
	                switch (scrollState) {
	                // 当不滚动时
	                case OnScrollListener.SCROLL_STATE_IDLE:
	                	//Toast.makeText(parentContext, "mGridView:" + mGridView.getLastVisiblePosition(), Toast.LENGTH_SHORT).show();  
	                // 判断滚动到底部
	                if (mGridView.getLastVisiblePosition() == (mGridView.getCount() - 1)) {
	                             }
	                // 判断滚动到顶部
	                if(mGridView.getFirstVisiblePosition() == 0){
	                }

	                 break;
	                    } 
	            }

	            @Override
	            public void onScroll(AbsListView absListView, int firstVisibleItem,
	                    int visibleItemCount, int totalItemCount) {
	            }
	        });
		return convertView;  
	}
	
	@Override 
    public View getGroupView(int groupPosition, boolean isExpanded,  
            View convertView, ViewGroup parent) {  
		
		LayoutInflater layoutInflater = (LayoutInflater) parentContext  
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		convertView = layoutInflater.inflate(R.layout.expandablelistview_groups_items, null); 
		
		final View mview = convertView;
		AsyncImageLoader asyncImageLoader = new AsyncImageLoader();		//异步加载本地图片
		String imagePath = mAllPhotoPaths.get(groupPosition).get(0);
		ImageView imageview = (ImageView) convertView  
				.findViewById(R.id.expandablelistview_groups_icons);
		SoftReference<Bitmap> mSoftbitmap = asyncImageLoader.loadImageFromSrcPath(imagePath, new ImageCallback() {
			@Override
			public void NotifyImageLoaded(ImageView imageView,Object data) {
				imageView = (ImageView) mview  
						.findViewById(R.id.expandablelistview_groups_icons);
				
				if(imageView !=null && data instanceof Bitmap){
                    imageView.setImageBitmap((Bitmap)data);
                }
			}
        });
		if(mSoftbitmap == null){
			imageview.setBackgroundResource(R.drawable.empty_photo);
		}
		
		TextView maintext = (TextView) convertView  
				.findViewById(R.id.expandablelistview_groups_titles);
		maintext.setText(getGroup(groupPosition).toString()); 
		TextView subheadtext = (TextView) convertView  
				.findViewById(R.id.expandablelistview_groups_subheads);
		subheadtext.setText("图像： "+mAllPhotoPaths.get(groupPosition).size()+"张");
        return convertView;  
    }  
	
	public void onGroupCollapsed (int groupPosition){		//当组收缩状态的时候此方法被调用。
		
	}
	public void onGroupExpanded (int groupPosition){
//		if(mGridView!=null){
//		Toast.makeText(parentContext, "mGridView:" + mGridView.getLastVisiblePosition(), Toast.LENGTH_SHORT).show();  
//		}
	}
	
	@Override
	public Object getGroup(int groupPosition)  {  
        return treeNodes.get(groupPosition).parent;  
    }  
	
	@Override
    public int getGroupCount()  {  
        return treeNodes.size();  
    }
	
	@Override
	public long getGroupId(int groupPosition) {  
        return groupPosition;  
    }  

	
	@Override
	 public boolean hasStableIds(){  
        return true;  
    }

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition){  
        return true;  
    }  

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Log.e("onChildClick", " ");
		return true;
	}  
	
	/** 
     * 构造GridView的Adapter 
     *  
     * @param menuNameArray 名称 
     * @param imageResourceArray 图片 
     * @return SimpleAdapter 
     */  
    private SimpleAdapter getGridviewAdapter(ArrayList<String> imageResourceArray, MyGridView mygridView)  
    {  
    	AsyncImageLoader asyncImageLoader = new AsyncImageLoader();		//异步加载本地图片
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>(); 
        Log.e("imageResourceArray", imageResourceArray.size()+"");
        for (int i=0; i<imageResourceArray.size(); i++)  
        {  
        	String imagePath = imageResourceArray.get(i);
        	final int j = i;
        	final MyGridView gridView = mygridView;
        	SoftReference<Bitmap> mSoftbitmap = asyncImageLoader.loadImageFromSrcPath(imagePath, new ImageCallback() {
				@Override
				public void NotifyImageLoaded(ImageView imageView,Object data) {
					
					if(j<gridView.getChildCount()){
						imageView = (ImageView) gridView.getChildAt(j).findViewById(R.id.photoItems_imgsrc);
					}
					if(imageView !=null && data instanceof Bitmap){
                        imageView.setImageBitmap((Bitmap)data);
                    }
				}
            });
        				
            HashMap<String, Object> map = new HashMap<String, Object>();  
            if(mSoftbitmap == null){
            	map.put("itemImage", null);
            }else{
            	map.put("itemImage", mSoftbitmap);
            }
            map.put("itemcheck", null);  
            data.add(map);
        }  
        SimpleAdapter simperAdapter = new SimpleAdapter(parentContext, data,  
                R.layout.expandablelistview_child_items, new String[] { "itemImage", "itemcheck" },  
                new int[] { R.id.photoItems_imgsrc, R.id.photoItems_checkbox });
        
        simperAdapter.setViewBinder(new ViewBinder(){		//更新GridView,否则显示不出图片
			public boolean setViewValue(View view,Object data,String textRepresentation){
				if(view instanceof ImageView && data instanceof Bitmap){
				ImageView iv=(ImageView)view;
					iv.setImageBitmap((Bitmap)data);
				return true;
			}
			else return false;
			}
		}); 
        return simperAdapter;  
    }
	
}
