package com.cuccs.dreambox.layouts;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.layouts.SlidingMenuContainer.OnSlideListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class PersonalAccountLayout extends FrameLayout{
	Button Btn_Back;
	private OnSlideListener mOnSlideListener;
	
	public PersonalAccountLayout(Context context) {  
        super(context);  
        setupViews();  
    }  
  
    public PersonalAccountLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        setupViews();  
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {  
        mOnSlideListener = onSlideListener;  
    }
    
    private void setupViews() {  
        final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
        LinearLayout mSet = (LinearLayout) mLayoutInflater.inflate(R.layout.menu_about, null);  
        addView(mSet);
        
        Btn_Back = (Button) mSet.findViewById(R.id.about_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
                if (mOnSlideListener != null) { 
                    mOnSlideListener.toRight();  
                }
            }  
        });
    }
}
