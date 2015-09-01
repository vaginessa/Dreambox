package com.cuccs.dreambox.layouts;


import com.cuccs.dreambox.R;
import com.cuccs.dreambox.layouts.SlidingMenuContainer.OnSlideListener;

import android.annotation.SuppressLint;
import android.content.Context;  
import android.util.AttributeSet;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.widget.Button;
import android.widget.FrameLayout;  
import android.widget.LinearLayout;  
  
  
/** 
 * ���������������ݽ�����ͼ 
 * @author TimeTraveler
 */  
public class ContentLayout extends FrameLayout {  
	
    private Button Btn_Menu; 
    private LinearLayout mAccount,mRecover, mBackup,mLookover;
    private OnSlideListener mOnSlideListener;  //�໬�˵�����������λ�� SlidingMenuContainer
    private OnContentSeletedListener monContentSeletedListener;	//�Զ���Listener�����ڴ���ѡ����Ϣ������Activity
      
    public ContentLayout(Context context) {  
        super(context);  
        setupViews();  
    }  
    public ContentLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        setupViews();  
    }
    
    public void setOnSlideListener(OnSlideListener onSlideListener) {  
        mOnSlideListener = onSlideListener;  
    }  
    
    private void setupViews() {  
        final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
        LinearLayout mContent = (LinearLayout) mLayoutInflater.inflate(R.layout.content, null);  
        addView(mContent);  

        Btn_Menu = (Button) mContent.findViewById(R.id.icon_menu);  
        Btn_Menu.setOnClickListener(new OnClickListener() {  
  
            public void onClick(View v) {
                if (mOnSlideListener != null) { 
                    mOnSlideListener.toRight();  
                }
            }  
        });  
        mAccount = (LinearLayout)mContent.findViewById(R.id.MyAccount);
        mRecover = (LinearLayout)mContent.findViewById(R.id.MyRecover);
        mBackup = (LinearLayout)mContent.findViewById(R.id.MyBackup);
        mLookover = (LinearLayout)mContent.findViewById(R.id.MyLookOver);
        mAccount.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
                if (monContentSeletedListener != null) { 
                	monContentSeletedListener.seletedContentChild(R.id.MyAccount);
                }
            }  
        });
        mRecover.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
                if (monContentSeletedListener != null) { 
                	monContentSeletedListener.seletedContentChild(R.id.MyRecover);
                }
            }  
        });
        mBackup.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (monContentSeletedListener != null) { 
                	monContentSeletedListener.seletedContentChild(R.id.MyBackup);
                }
            }  
        });
        mLookover.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (monContentSeletedListener != null) { 
                	monContentSeletedListener.seletedContentChild(R.id.MyLookOver);
                }
            }  
        });
    }  
    
    /** 
     * ���������ݴ�����ѡ�е�content�¼������� 
     * @param seletedListener 
     */  
    public void setOnContentSeletedListener(OnContentSeletedListener seletedListener) {  
    	monContentSeletedListener = seletedListener;  
    }
    /** 
     * �����ݴ�����ѡ�е�content�¼��������ӿ�
     */ 
    public interface OnContentSeletedListener {  
        /** 
         * ��ǰѡ�е�content�¼������� 
         * @param position ����Id
         */  
    	public abstract void seletedContentChild(int viewId);  
    }
  
}