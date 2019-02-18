package com.merlin.crash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.merlin.debug.Debug;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {
	private Context mContext;
	
	public CrashHandler(Context c) {
		mContext = c;
	}

	/**
	 * Restart current application.
	 */
	private void restartApplication() {
//		Debug.D(getClass(), "set alarm restart task.");
//       final Intent intent = new Intent(mContext, KidbookService.class);
//       PendingIntent restartIntent = PendingIntent.getService(mContext.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//       AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//       mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,restartIntent);//restart after 1000ms
//		Debug.D(getClass(), "Exit current process."); //kill current application.
//	   android.os.Process.killProcess(android.os.Process.myPid());
//	   System.exit(1);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Debug.E(getClass(), "UncaughtException:" + ex.toString());
		ex.printStackTrace();
		Debug.E(getClass(), "Restart application.");
		restartApplication();
	}
}
