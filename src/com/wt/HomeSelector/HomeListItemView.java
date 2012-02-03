package com.wt.HomeSelector;

import com.wt.HomeSelector.HomeInfo.HomeRunningInfo;
import com.wt.HomeSelector.HomeListAdapter.HomeListItem;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeListItemView extends RelativeLayout {
	private HomeListItem mListItem;
	private ImageView mHomeIcon;
	private ImageView ivHomeSwitch;
	private TextView mHomeLabel;
	private LinearLayout mHomeStates;
	private TextView mHStateMem;
	private TextView mDefaultHome;
	private Button btnSwitchHome;
	
	public HomeListItemView(Context context) {
		super(context);		
	}
	
	public HomeListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setListItem(HomeListItem item) {
		mListItem = item;
	}
	
	public void bindListItem() {
		HomeInfo hi = mListItem.getHomeInfo();
		mHomeIcon.setImageDrawable(hi.icon);	
		mHomeLabel.setText(hi.label);
		
		HomeRunningInfo hrInfo = hi.runningInfo;
		if (hrInfo.isRunning) {
			mHomeStates = (LinearLayout) findViewById(R.id.llHomeState);
			mHomeStates.setVisibility(VISIBLE);
			mHStateMem.setText(String.valueOf(hrInfo.MemoryUsed).concat(" kB"));
		}
		
		if(hi.isDefault) {
			mDefaultHome = (TextView) findViewById(R.id.tvHomeStateDefault);
			mDefaultHome.setVisibility(VISIBLE);
		}
	}
	
	public void bind(HomeListItem item) {
		setListItem(item);
		bindListItem();
	}
	
	public ImageView getSwitchButton() {
		//return btnSwitchHome;
		return ivHomeSwitch;
	}
	
	@Override
	protected void onFinishInflate() {		
		super.onFinishInflate();
		mHomeIcon = (ImageView) findViewById(R.id.ivHomeIcon);
		mHomeLabel = (TextView) findViewById(R.id.tvHomeLabel);
				
		mHStateMem = (TextView) findViewById(R.id.tvHomeStateMem);
		btnSwitchHome = (Button) findViewById(R.id.btnSwitchHome);		
		ivHomeSwitch = (ImageView) findViewById(R.id.ivSwitchHome);	
	}

}
