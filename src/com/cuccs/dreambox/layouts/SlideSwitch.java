// Copyright(C) 2010-2010 RabiSoft
// GNU Lesser General Public License

package com.cuccs.dreambox.layouts;

import com.cuccs.dreambox.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class SlideSwitch extends FrameLayout implements Checkable {

	public interface OnCheckedChangeListener {
		void onCheckedChanged(SlideSwitch view, boolean checked);
	}
	
	Thread m_thread;
	Handler m_handler = new Handler();
	float m_add = 0.0f;
	boolean m_checked;
	View m_view_bar;
	View m_view_background_off;
	View m_view_background_on;
	int m_orientation = HORIZONTAL;
	String m_text;

	static final float m_accele = 0.05f;
	static final long m_intarval = 50L;
	static final int m_height_bar = 70;
	static final int m_width_bar = 100;
	static final int HORIZONTAL = 0;
	static final int VERTICAL = 1;
	
	OnTouchListener m_listnerOnTouch = new OnTouchListener() {

	    int m_left;
	    int m_top;
	    float m_x_org;
	    float m_y_org;
	    float m_x_prev;
	    float m_y_prev;
	    long m_t_prev;

		public boolean onTouch(View v, MotionEvent me) {
			boolean bResult = false;
			float x = me.getRawX();
			float y = me.getRawY();
			long t = System.currentTimeMillis();			
			int action = me.getAction();
			
			switch( action ){
			case MotionEvent.ACTION_DOWN:{
			    	stopTimer();
					m_x_org = x;
					m_y_org = y;
			    	m_left = v.getLeft();
			    	m_top = v.getTop();
			    	bResult = true;
			    	m_view_bar.setBackgroundResource(R.drawable.slideswitch_bar_touch);
				}
				break;
			case MotionEvent.ACTION_UP:
				startTimer();
		    	m_view_bar.setBackgroundResource(R.drawable.slideswitch_bar);
				break;
			case MotionEvent.ACTION_MOVE:{
					stopTimer();
					switch(m_orientation) {
					case HORIZONTAL:
						m_add = (x - m_x_prev) / (t - m_t_prev);
						float x_move = x - m_x_org;
						moveBarX(m_left + (int)x_move);
						break;
					case VERTICAL:
						m_add = (y - m_y_prev) / (t - m_t_prev);
						float y_move = y - m_y_org;
						moveBarY(m_top + (int)y_move);
						break;
					}
				}
				break;
			default:
				// do nothing.
			}
			
			m_t_prev = t;
			m_x_prev = x;
			m_y_prev = y;
			
			return bResult;
		}
		
	};
	
	void moveBarX(int left) {
		
		int width = m_view_bar.getWidth();
		int widthParent = getWidth();
		int height = m_view_bar.getHeight();
		int leftMax = widthParent - width;
		
		int leftNew = left;
		if( leftNew <= 0) {
			stopTimer();
			leftNew = 0;
			updateState(false);
		} else if( leftMax <= leftNew ) {
			stopTimer();
			leftNew = leftMax;
			updateState(true);
		}
	
		int top = m_view_bar.getTop();
		int rightNew = leftNew + width;
		int bottom = top + height;
		m_view_bar.layout(leftNew, top, rightNew, bottom);

		int centerNew = leftNew + ( width / 2 );
		m_view_background_on.layout(0, top, centerNew, bottom);
		m_view_background_off.layout(centerNew, top, widthParent, bottom);

	}

	void moveBarY(int top) {
		
		int height = m_view_bar.getHeight();
		int heightParent = getHeight();
		int width = m_view_bar.getWidth();
		int topMax = heightParent - height;
		
		int topNew = top;
		if( topNew <= 0) {
			stopTimer();
			topNew = 0;
			updateState(true);
		} else if( topMax <= topNew ) {
			stopTimer();
			topNew = topMax;
			updateState(false);
		}
	
		int left = m_view_bar.getLeft();
		int bottomNew = topNew + height;
		int right = left + width;
		m_view_bar.layout(left, topNew, right, bottomNew);

		int centerNew = topNew + ( height / 2 );
		m_view_background_on.layout(left, centerNew, right, heightParent);
		m_view_background_off.layout(left, 0, right, centerNew);

	}

	void updateState(boolean checked) {

		if( m_checked == checked ) {
			return;
		}
		
		m_checked = checked;
		
		if( m_listener != null ) {
			m_listener.onCheckedChanged(SlideSwitch.this, m_checked);
		}

	}
	
	public SlideSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);

		{
	        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideSwitch);

	        m_checked = a.getBoolean(R.styleable.SlideSwitch_checked, false);
	        m_orientation = a.getInt(R.styleable.SlideSwitch_orientation, HORIZONTAL);
	        m_text = a.getString(R.styleable.SlideSwitch_text);
		}
		
		{
			int idRes;
			
			switch( m_orientation ) {
			case HORIZONTAL:
				idRes = R.layout.slide_switch_horizontal_layout;
				break;
			case VERTICAL:
				idRes = R.layout.slide_switch_vertical_layout;
				break;
			default:
				throw new AssertionError();
			}
						
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(idRes, this);
		}
		
		{
			int widthOn;
			int widthOff;
			int heightOn;
			int heightOff;
			
			switch(m_orientation) {
			case HORIZONTAL:
				heightOn = LayoutParams.FILL_PARENT;
				heightOff = LayoutParams.FILL_PARENT;
				if( m_checked ) {
					widthOn = LayoutParams.FILL_PARENT;
					widthOff = 0;
				} else {
					widthOn = 0;
					widthOff = LayoutParams.FILL_PARENT;
				}
				break;
			case VERTICAL:
				widthOn = LayoutParams.FILL_PARENT;
				widthOff = LayoutParams.FILL_PARENT;
				if( m_checked ) {
					heightOn = LayoutParams.FILL_PARENT;
					heightOff = 0;
				} else {
					heightOn = 0;
					heightOff = LayoutParams.FILL_PARENT;
				}
				break;
			default:
				throw new AssertionError();
			}
			
			{
				View v = findViewById(R.id.ViewOff);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthOff, heightOff);
				v.setLayoutParams(params);
				m_view_background_off = v;
			}

			{
				View v = findViewById(R.id.ViewOn);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthOn, heightOn);
				v.setLayoutParams(params);
				m_view_background_on = v;
			}
		}
		
		{
			int gravity;
			
			switch(m_orientation) {
			case HORIZONTAL:
				if( m_checked ) {
					gravity = Gravity.RIGHT;
				} else {
					gravity = Gravity.LEFT;
				}
				break;
			case VERTICAL:
				if( m_checked ) {
					gravity = Gravity.TOP;
				} else {
					gravity = Gravity.BOTTOM;
				}
				break;
			default:
				throw new AssertionError();
			}
			
			Button v = (Button)findViewById(R.id.ButtonBar);
			{
				FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)v.getLayoutParams();
				params.gravity = gravity;
				v.setLayoutParams(params);
			}
			v.setText(m_text);
			v.setOnTouchListener(m_listnerOnTouch);
			m_view_bar = v;
		}

	}
	
	public void setChecked(boolean checked) {
		
		m_checked = checked;
		
		switch( m_orientation ) {
		case HORIZONTAL:
			if( checked ) {
				int widthParent = getWidth();
				moveBarX(widthParent);
			} else {
				moveBarX(0);
			}
			break;
		case VERTICAL:
			if( checked ) {
				moveBarY(0);
			} else {
				int heightParent = getHeight();
				moveBarY(heightParent);
			}
			break;
		default:
			throw new AssertionError();
		}
		
	}

	void stopTimer() {
		if( m_thread == null ) {
			return;
		}
		
		m_thread.interrupt();
		m_thread = null;
	}

	void startTimer() {
		
		stopTimer();
		m_thread = new Thread(new Runnable() {
			public void run() {
				
				while(true) {
					try {
						Thread.sleep(m_intarval);
					} catch (InterruptedException e) {
						return;
					}
					
					m_handler.post(new Runnable() {
						public void run() {
							onTimer();
						}
					});
				}
				
			}
			
		});
		
		m_thread.start();
	}
	
	void onTimer() {
		
		switch( m_orientation ) {
		case HORIZONTAL:
			{
				int left = m_view_bar.getLeft();
				int width = m_view_bar.getWidth();
				int widthParent = SlideSwitch.this.getWidth();
				int centerParent = widthParent / 2;
				int center = left + (width / 2 );
				
				if( center < centerParent ) {
					m_add -= m_accele;
				} else {
					m_add += m_accele;
				}
				
				int move = (int)(m_add * m_intarval);
				int leftNew = left + move;
				moveBarX(leftNew);
			}
			break;
		case VERTICAL:
			{
				int top = m_view_bar.getTop();
				int height = m_view_bar.getHeight();
				int heightParent = SlideSwitch.this.getHeight();
				int centerParent = heightParent / 2;
				int center = top + (height / 2 );
				
				if( center < centerParent ) {
					m_add -= m_accele;
				} else {
					m_add += m_accele;
				}
				
				int move = (int)(m_add * m_intarval);
				int topNew = top + move;
				moveBarY(topNew);
			}
			break;
		default:
			throw new AssertionError();
		}
		
	}

	public boolean isChecked() {
		return m_checked;
	}

	public void toggle() {
		boolean checkedNew = ! m_checked;
		setChecked(checkedNew);
	}
	
	OnCheckedChangeListener m_listener;
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
		m_listener = listener;
	}

	public void playAnimation() {
		m_view_bar.setBackgroundResource(R.drawable.slideswitch_bar_touch);
		// 动画设定(指定移动动画) (x1, x2, y1, y2)
	    Animation am = new TranslateAnimation ( m_view_bar.getX(), m_view_bar.getX()+getWidth()-m_view_bar.getWidth(),
	    										m_view_bar.getY(), m_view_bar.getY());
	    // 动画开始到结束的执行时间(1000 = 1 秒)
	    am.setDuration ( 500 ); 
	    // 动画重复次数(-1 表示一直重复)
	    //am.setRepeatCount ( 0 ); 
	    // 图片配置动画
	    m_view_bar.startAnimation(am); 
	}
}
