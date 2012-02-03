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
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

public class HomeManager {
	public final String PackageScheme = "package";
	private PackageManager mPM;
	private ActivityManager mAM;
	
	public HomeManager(PackageManager pm, ActivityManager am) {
		mPM = pm;
		mAM = am;
	}

	public List<HomeInfo> getHomeInfos() {
	    List<ResolveInfo> rList = mPM.queryIntentActivities(getHomeIntent(), 0);        
	    List<HomeInfo> homeList = new ArrayList<HomeInfo>();
	    	 
	    for(ResolveInfo r : rList) {
	    	homeList.add(new HomeInfo(r, mPM, mAM));
	    }
	    setHomeDefault(homeList);
	    return homeList;
	}	
	
	public Class<?> getMockHomeClass() {
		return MockHome.class;
	}
	
	public void removeDefaultHome(Context cont, Class<?> actClz) {	
		Class<?> mkClz = getMockHomeClass();
    	ComponentName mhCN = new ComponentName(mkClz.getPackage().getName(), mkClz.getName());
    		    	
    	mPM.setComponentEnabledSetting(mhCN, 1, 1);
    	switchHome(cont);
    	mPM.setComponentEnabledSetting(mhCN, 0, 1);
    	cont.startActivity(new Intent(cont, actClz));
	 } 
	
	public ComponentName queryDefaultHome() {
		List<IntentFilter> intentList = new ArrayList<IntentFilter>();
		List<ComponentName> cnList = new ArrayList<ComponentName>();
		mPM.getPreferredActivities(intentList, cnList, null);
		    	
	    IntentFilter dhIF;       
	    for(int i = 0; i < cnList.size(); i++) {
	    	dhIF = intentList.get(i);
	    	if(dhIF.hasAction(Intent.ACTION_MAIN) && dhIF.hasCategory(Intent.CATEGORY_HOME) && dhIF.hasCategory(Intent.CATEGORY_DEFAULT)) {
	    		return cnList.get(i);
	    	}        	   		
	    }
		return null;
	}
	
	public int seekDefaultHome(List<HomeInfo> hInfos) {
		ComponentName dhCN = queryDefaultHome();
		return (dhCN != null) ? seekDefaultHome(hInfos, dhCN.getPackageName()) : -1;		
	}
	
	public int seekDefaultHome(List<HomeInfo> hInfos, String pkgName) {
		for(int i = 0; i < hInfos.size(); i++) {
			if(hInfos.get(i).checkDefault(pkgName)) return i;
		}
		return -1;
	}
	
	public void setHomeDefault(List<HomeInfo> hInfos) {
		int pos = seekDefaultHome(hInfos);
	    if(pos > -1 && pos < hInfos.size()) {
	    	for(HomeInfo hi : hInfos) hi.isDefault = false;
	    	hInfos.get(pos).isDefault = true;
	    } 	    	
	}
	
	public ResolveInfo queryHome(String pkgName) {
		Intent hi = getHomeIntent();
		hi.setPackage(pkgName);
		List<ResolveInfo> rList = mPM.queryIntentActivities(hi, 0);
		return rList.size() > 0 ? rList.get(0) : null;
	}
	
	public Intent getHomeIntent() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.addCategory(Intent.CATEGORY_DEFAULT);
		return homeIntent;
	}
	
	public Intent getHomeIntent(String pkgName) {
		return getHomeIntent().setPackage(pkgName);		
	}
	
	public void switchHome(Context cont) {
		cont.startActivity(getHomeIntent());
	}
	
	public void switchHome(Activity act, String pkgName) {		
		act.startActivity(getHomeIntent(pkgName));
	  	act.finish();
	}
	
	public void chooseHome(Context cont) {
		//Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
		//chooserIntent.putExtra(Intent.EXTRA_INTENT, homeIntent);				
		Intent chooser = Intent.createChooser(getHomeIntent(), "Home");
		cont.startActivity(chooser);
	}
	
	public Uri getHomeUri(String pkgName) {
		return Uri.fromParts(PackageScheme, pkgName, null);
	}
	
	public void startHomeInfo(Context cont, String pkgName) {
		Uri homeURI = getHomeUri(pkgName);
		Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, homeURI);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		
		cont.startActivity(i);
	}
	
	public void unInstallHome(Context cont, String pkgName) {
		Uri homeURI = getHomeUri(pkgName);
		Intent i = new Intent(Intent.ACTION_DELETE, homeURI);		
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		cont.startActivity(i);
	}
	
	public void deleteHome(HomeInfo hInfo) {		
		mAM.killBackgroundProcesses(hInfo.packageName);
		hInfo.calcHomeRunningInfo(mAM);
	}
	
}
