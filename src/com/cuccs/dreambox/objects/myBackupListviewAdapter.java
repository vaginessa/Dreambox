package com.cuccs.dreambox.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.R.drawable;
import com.cuccs.dreambox.R.id;
import com.cuccs.dreambox.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class myBackupListviewAdapter extends BaseAdapter{

	private LayoutInflater mLayoutInflater;
	private List<Map<String, Object>> mData; // 存储的TextView和图标信息
	private static HashMap<Integer, Boolean> isSelected;
	
	public myBackupListviewAdapter(Context context) {
		// TODO Auto-generated constructor stub
		mLayoutInflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
	}
	
	public void setData( List<Map<String, Object>> data) {
		mData = data;
		if (mData != null) {
			for (int i = 0; i < mData.size(); i++) {
				isSelected.put(i, false);
			}
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mData != null) {
			return mData.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView mIcons;
		TextView mTitle_1;
		TextView mTitle_2;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.content_backup_items, null); 
			mIcons = (ImageView)convertView.findViewById(R.id.backup_listview_icons);
			mTitle_1 = (TextView)convertView.findViewById(R.id.backup_listview_titles_1);
			mTitle_2 = (TextView)convertView.findViewById(R.id.backup_listview_titles_2);
			
			Object image = mData.get(position).get("image");
			if(image != null){
				mIcons.setImageResource(Integer.parseInt(image.toString()));
			}
			Object title = mData.get(position).get("title");
			if (title != null && !"".equals(title)) {
				mTitle_1.setText(title.toString());
			} else {
				mTitle_1.setText(" ");
			}
			title = mData.get(position).get("counter");
			if (title != null && !"".equals(title)) {
				mTitle_2.setText(title.toString());
			} else {
				mTitle_2.setText(" ");
			}
		}
		
		final CheckBox mcheckbox = (CheckBox)convertView.findViewById(R.id.backup_listview_checkbox);
		mcheckbox.setChecked(isSelected.get(position));
		mcheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked == true){
					mcheckbox.setBackgroundResource(R.drawable.btn_check_on_normal);
				}else{
					mcheckbox.setBackgroundResource(R.drawable.btn_check_off_normal);
				}
				isSelected.put(position, isChecked);
			}
		});
		return convertView;
	}
	
	
	public HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }
	
}
