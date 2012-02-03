package com.wt.HomeSelector;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Debug;

public class HomeInfo {
	public String name;
	public String packageName;
	public String label;
	public Drawable icon;
	public boolean isDefault = false;
	public HomeRunningInfo runningInfo;
	
	public HomeInfo(ResolveInfo rInfo, PackageManager pm, ActivityManager am) {
		name = rInfo.activityInfo.name;
		packageName = rInfo.activityInfo.packageName;
		label = rInfo.loadLabel(pm).toString();
		icon = rInfo.loadIcon(pm);
		
		calcHomeRunningInfo(am);
	}			
	
	public boolean checkDefault(String pkgName) {
		return pkgName.equals(packageName);
	}
	
	public void calcHomeRunningInfo(ActivityManager am) { 
		runningInfo = HomeRunningInfo.getHomeRunningInfo(packageName, am);
    }

	
	public static class HomeRunningInfo {	
		public boolean isRunning = false;
		public int PID;
		public int MemoryUsed;
		
		private HomeRunningInfo() {}
		
		public static HomeRunningInfo getHomeRunningInfo(String pkgName, ActivityManager am) {
			RunningAppProcessInfo rInfo = null;
			HomeRunningInfo hrInfo = new HomeRunningInfo();
			
			for(RunningAppProcessInfo info : am.getRunningAppProcesses()) {
	    		if (info.processName.equals(pkgName)) rInfo = info;    		
	    		for (String pn : info.pkgList) {
	    			if (pn.equals(pkgName)) rInfo = info;
	    		}    		
	    	}	
			
			if(rInfo != null) {			
				hrInfo.isRunning = true;
				hrInfo.PID = rInfo.pid;					
				hrInfo.MemoryUsed = hrInfo.getMemoryUsed(am);
			}	
	    	return hrInfo;
		}
		
		public int getMemoryUsed(ActivityManager am) {				
			Debug.MemoryInfo mInfo = am.getProcessMemoryInfo(new int[]{PID})[0];
			return mInfo.getTotalPss();
		}
	}
	
}
