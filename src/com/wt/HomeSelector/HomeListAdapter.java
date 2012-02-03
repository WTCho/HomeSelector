package com.wt.HomeSelector;

import java.util.ArrayList;
import java.util.List;

import com.wt.HomeSelector.HomeSelectorAct.HomeListViewEventHandler;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class HomeListAdapter extends BaseAdapter {
	private List<HomeListItem> mItems = new ArrayList<HomeListItem>();
	private LayoutInflater layoutFactory;
	private HomeListViewEventHandler mEvtHandler;		
	
	public HomeListAdapter(Context pContext) {
		layoutFactory = LayoutInflater.from(pContext);
	}
	
	public void addItemList(List<HomeInfo> items) {
		if(items != null && items.size() > 0) {
			mItems.clear();
			for(HomeInfo hi : items) {
				mItems.add(new HomeListItem(hi));
			}
		}		
	}
	
	public void setEventHandler(HomeListViewEventHandler handler) {
		mEvtHandler = handler;
	}	
	
	@Override
	public int getCount() {	
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		HomeListItemView hView = (HomeListItemView) layoutFactory.inflate(R.layout.homelist_itemview, parent, false);
		HomeListItem hItem = mItems.get(position);
		final HomeInfo hInfo = hItem.getHomeInfo();		
		hView.bind(hItem);
		
		hView.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				if(hInfo.runningInfo.isRunning) {
					menu.add("Delete").setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							mEvtHandler.sendEventMessage(EvtMsgCode.DELETE, hInfo);
							return true;
						}					
					});	
				}
				
				menu.add("Home Info").setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						mEvtHandler.sendEventMessage(EvtMsgCode.HOMEINFO, hInfo);
						return true;
					}					
				});
				
				menu.add("Uninstall").setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						mEvtHandler.sendEventMessage(EvtMsgCode.UNINSTALL, hInfo);
						return true;
					}					
				});		
			}			
		});
		
		hView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				v.showContextMenu();
			}
		});
		
		hView.getSwitchButton().setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				mEvtHandler.sendEventMessage(EvtMsgCode.SWITCH, hInfo);		
			}
		});
		return hView;
	}	
	
	
	public class HomeListItem {
		private HomeInfo mHI;
		
		public HomeListItem(HomeInfo hi) {
			mHI = hi;
		}
		
		public HomeInfo getHomeInfo() {
			return mHI;
		}		
	}
}
