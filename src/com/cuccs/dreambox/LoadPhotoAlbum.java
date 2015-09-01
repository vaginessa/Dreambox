package com.cuccs.dreambox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cuccs.dreambox.objects.myExpandableListAdapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;


public class LoadPhotoAlbum extends Activity{
	private static final String[] PHOTOS_PROJECTION = new String[] {
        Media.DATA, Media._ID,Media.TITLE,Media.DISPLAY_NAME,Media.SIZE};
	
	private ArrayList<String> mDirName = new ArrayList<String>();		//图片目录的路径	
	private Bitmap[][] mPhotos;
	private ArrayList<ArrayList<String>> mAllPhotoPaths = new ArrayList<ArrayList<String>>();
	private ArrayList<String> mPhotoName = new ArrayList<String>();
	private ArrayList<Long> mPhotoId = new ArrayList<Long>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadphotosalbum);
		this.getGalleryPhotos();
		
		
		for(int i=0;i<mAllPhotoPaths.size();i++){
			for(int k=0;k<mAllPhotoPaths.get(i).size();k++){
				System.out.println(k+" :"+mAllPhotoPaths.get(i).get(k));
			}
		}
		final myExpandableListAdapter mexpandAdapter = new myExpandableListAdapter(this, 36, mAllPhotoPaths);
		ArrayList<String> groups = mDirName;
		String child = ""; 
		List<myExpandableListAdapter.TreeNode> treeNode = mexpandAdapter.GetTreeNode();
		for (int i = 0; i<mAllPhotoPaths.size(); i++)  
        {  
			myExpandableListAdapter.TreeNode node = new myExpandableListAdapter.TreeNode();
            node.parent = groups.get(i); 
            node.childs.add(child); 
            treeNode.add(node);  
        }  
		mexpandAdapter.UpdateTreeNode(treeNode);
		final ExpandableListView  expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setAdapter(mexpandAdapter);
        expandableListView.setGroupIndicator(null);		//去掉列表展开的箭头
        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {	//展开指定组时折叠其他组
			@Override
			public void onGroupExpand(int groupPosition) {
				mexpandAdapter.getGroupCount();
				for(int i=0;i<mexpandAdapter.getGroupCount();i++){
					if(i!=groupPosition){
						expandableListView.collapseGroup(i);
					}
				}
			}
		});
        
//		List<Map<String, String>> mGruops = new ArrayList<Map<String,String>>();
//		List<List<Map<String, String>>> mChildren = new ArrayList<List<Map<String,String>>>();
//		List<Map<String, String>> mChild_01 = new ArrayList<Map<String,String>>();
//		for(int i=0;i<mDirPath.size();i++){
//			 Map<String, String> titles = new HashMap<String, String>();
//			 titles.put("groups", mDirPath.get(i));
//			 mGruops.add(titles);
//		}
//		for(int i=0;i<3;i++){
//			 Map<String, String> contents = new HashMap<String, String>();
//			 contents.put("child", "    内容 "+i+"哦");
//			 mChild_01.add(contents);
//		}
//		for(int i=0;i<mDirPath.size();i++){
//			mChildren.add(mChild_01);
//		}
//		SimpleExpandableListAdapter expandListadapter = new SimpleExpandableListAdapter( 
//				this, mGruops, R.layout.expandablelistview_groups_items, new String[]{"groups"}, new int[]{R.id.expandablelistview_groups_titles},
//				mChildren, R.layout.expandablelistview_child_items, new String[]{"child"}, new int[]{R.id.expandablelistview_child_titles}); 
//		expandableListView.setAdapter(expandListadapter);

		
		
		
//		GridView gridview = (GridView) findViewById(R.id.loadphotos_gridview);
//		List<Map<String,Object>> gridItems = new ArrayList<Map<String,Object>>();		//创建list
//		for(int i=0;i<mPhotos.size();i++){
//			Map<String,Object> maps = new HashMap<String, Object>();			//实例化map对象
//			maps.put("image", mPhotos.get(i));		
//			maps.put("checkbox", R.drawable.btn_check_off_normal);
//			gridItems.add(maps);
//		}
//		SimpleAdapter adapter = new SimpleAdapter(this,gridItems,
//				R.layout.photo_items,new String[]{"image","checkbox"},new int[]{R.id.photoItems_imgsrc,R.id.photoItems_checkbox});
//		gridview.setAdapter(adapter);
//		adapter.setViewBinder(new ViewBinder(){		//更新GridView,否则显示不出图片
//			public boolean setViewValue(View view,Object data,String textRepresentation){
//				if(view instanceof ImageView && data instanceof Bitmap){
//					ImageView iv=(ImageView)view;
//					iv.setImageBitmap((Bitmap)data);
//				return true;
//			}
//			else return false;
//			}
//		});
	}

	/**读取手机图库的图片*/
	private void getGalleryPhotos() {
		ContentResolver resolver = this.getContentResolver();
		//获得外部存储卡上的图片
		Cursor photoCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PHOTOS_PROJECTION, null, null, 
        		null);
		/*int photoIndex = photoCursor.getColumnIndexOrThrow(Media.DATA);
		int photoidIndex = photoCursor.getColumnIndexOrThrow(Media._ID);		//获取照片每一项信息对应的序号
		int photonameIndex = photoCursor.getColumnIndexOrThrow(Media.DISPLAY_NAME);
		int photosizeIndex = photoCursor.getColumnIndexOrThrow(Media.SIZE);*/
		photoCursor.moveToFirst();
		Log.v("getCount", photoCursor.getCount()+"");
		for(int i=0; i<photoCursor.getCount(); i++){
			ArrayList<String> mSrcs = new ArrayList<String>();
			int index = photoCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			String src = photoCursor.getString(index);
			
			File f = new File(src);
			boolean addAllow = true;
			if(mDirName.size()==0){
				mDirName.add(f.getParentFile().getName());
				mAllPhotoPaths.add(mSrcs);
			}
			for(int m=0;m<mDirName.size();m++){		//判断图片目录是否已经保存
				if(f.getParentFile().getName().equals(mDirName.get(m))==true){
					addAllow = false;
				}
			}
			if(addAllow == true){
				mDirName.add(f.getParentFile().getName());
				mAllPhotoPaths.add(mSrcs);
				Log.i("getParentFile()", f.getParentFile().getName());
			}
			
			for(int r=0;r<mDirName.size();r++){
				if(f.getParentFile().getName().equals(mDirName.get(r))==true){
					mAllPhotoPaths.get(r).add(src);
				}
			}
			index = i;
			photoCursor.moveToNext();
		}
		photoCursor.close();
	}
	
	
	
	
}
