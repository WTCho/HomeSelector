package com.wt.HomeSelector;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class HomeSelectorAct extends Activity {
	private ListView lvHome;
	private Button btnClearDefault;
	private Button btnSetDefault;
	
    private HomeManager mHManager;
    private HomeListAdapter hAdapter;
    private HomeListViewEventHandler mHLVEvtHandler;
    
    private Context thisCont = this;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);                   
        bindViews();
        init();
        initViewEvents();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	bindSource();
    }
    
    private void searchDefauleHome() {
    	PackageManager localPackageManager = getPackageManager();
    	List<IntentFilter> intentList = new ArrayList<IntentFilter>();
    	List<ComponentName> prefActList = new ArrayList<ComponentName>();
    	localPackageManager.getPreferredActivities(intentList, prefActList, null);
    	
    	int size = prefActList.size();
    	ComponentName localComponentName = null;
        IntentFilter localIntentFilter;
        
        for(int i = 0; i < size; i++) {
        	localIntentFilter = intentList.get(i);
        	
	        if(localIntentFilter.hasAction(Intent.ACTION_MAIN) && localIntentFilter.hasCategory(Intent.CATEGORY_HOME)) {
	        	localComponentName = prefActList.get(i);
	        	break;
	        }        	
        }
        
        if(localComponentName != null) {
        	String pn = localComponentName.getPackageName();
        	localPackageManager.clearPackagePreferredActivities(pn);
        }        	
    }
    
    public void bindViews() {
    	lvHome = (ListView) findViewById(R.id.lvHome);
    	btnClearDefault = (Button) findViewById(R.id.btnClearDefaultHome);
    	btnSetDefault = (Button) findViewById(R.id.btnSetDefaultHome);
    }
   
    public void init() {
    	PackageManager pm = getPackageManager();
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);  
        mHManager = new HomeManager(pm, am);
        mHLVEvtHandler = new HomeListViewEventHandler();
        hAdapter = new HomeListAdapter(this);
        hAdapter.setEventHandler(mHLVEvtHandler);  
    }
    
    public void initViewEvents() {
    	btnClearDefault.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mHManager.queryDefaultHome() != null) {
					mHManager.removeDefaultHome(thisCont, HomeSelectorAct.class);
				}				
			}
		});
    	
    	btnSetDefault.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mHManager.queryDefaultHome() != null) {
					mHManager.removeDefaultHome(thisCont, HomeSelectorAct.class);
				}
				mHManager.switchHome(thisCont);
			}
		});
    }
    
    public void bindSource() {
    	hAdapter.addItemList(mHManager.getHomeInfos());
        lvHome.setAdapter(hAdapter);         
    }
        
    
    public class HomeListViewEventHandler extends Handler {
		
    	public boolean sendEventMessage(int code, Object obj) {
    		return this.sendMessage(this.obtainMessage(code, obj));
    	}
    	
		@Override
		public void handleMessage(Message msg) {
			HomeInfo mInfo = (HomeInfo) msg.obj;
			switch(msg.what) {							
				case EvtMsgCode.SWITCH:
					mHManager.switchHome(HomeSelectorAct.this, mInfo.packageName);
					break;
				case EvtMsgCode.HOMEINFO:
					mHManager.startHomeInfo(getApplicationContext(), mInfo.packageName);
					break;
				case EvtMsgCode.UNINSTALL:
					mHManager.unInstallHome(getApplicationContext(), mInfo.packageName);					
					break;
				case EvtMsgCode.DELETE:					
					mHManager.deleteHome(mInfo);
					hAdapter.notifyDataSetChanged();
					break;				
				default:
					
			}
		}
	}
    
}