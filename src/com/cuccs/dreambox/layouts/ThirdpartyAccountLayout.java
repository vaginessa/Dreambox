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

public class ThirdpartyAccountLayout extends FrameLayout{
	Button Btn_Back;
	private OnSlideListener mOnSlideListener;
	
	public ThirdpartyAccountLayout(Context context) {  
        super(context);  
        setupViews();  
    }  
  
    public ThirdpartyAccountLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        setupViews();  
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {  
        mOnSlideListener = onSlideListener;  
    }
    
    private void setupViews() {  
        final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
        LinearLayout mThirdparty = (LinearLayout) mLayoutInflater.inflate(R.layout.menu_about, null);  
        addView(mThirdparty);
        
        Btn_Back = (Button) mThirdparty.findViewById(R.id.about_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
                if (mOnSlideListener != null) { 
                    mOnSlideListener.toRight();  
                }
            }  
        });
    }
}
