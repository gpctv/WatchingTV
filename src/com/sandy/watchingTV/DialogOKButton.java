package com.sandy.watchingTV;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import bean.AlertListBean;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DialogOKButton extends AlarmList  implements  android.view.View.OnClickListener {
	FragmentActivity mainActivity; //主程式容器
	AlertListBean listBean;//節目設定Bean
	int PgTime=0;//節目時間
	int PgDate=0;//節目日期
	int year=0;//設定年
	int month=0;//設定月
	int day=0;//設定日
	int min=0;//設定分
	int hour=0;//設定時
	String SID=null;//設定字串ID
	String SPGDate=null;//設定節目日期字串
	String SPGTime=null;//設定節目時間字串
	int ID=0;//設定INTGER ID
	Long million;//設定鬧鈴MILLION
	SharedPreferences settings ; //設定存入節目資訊XML
	SharedPreferences.Editor editor;//設定EDITOR XML
	AlertDialog dialogForm; //設定DIALOG ALERT
	SimpleAdapter simpleadapter;
	ListView AlarmList;
	AlertListBean alartBean[];
	ArrayList<HashMap<String, String>> dataList;
	public DialogOKButton(FragmentActivity  FA,AlertListBean listBean,AlertDialog dialog){
		 dialogForm=dialog;
		mainActivity=FA;
		settings=mainActivity.getSharedPreferences("setAlarmText", 0);
		editor=settings.edit();
		this.listBean=listBean;
		this.AlarmList=AlarmList;
	}
	public DialogOKButton(ActionBarActivity  FA,AlertListBean listBean,AlertDialog dialog,SimpleAdapter simplAdapter,ListView AlarmList){
		 dialogForm=dialog;
			mainActivity=FA;
			settings=mainActivity.getSharedPreferences("setAlarmText", 0);
			editor=settings.edit();
			this.listBean=listBean;
			this.simpleadapter=simplAdapter;
			
	}

	@Override
	public void onClick(View v) {
		// TODO 自動產生的方法 Stub
		setBean1(); //資料塞入BEAN
		setAlarm(); //鬧鈴設定
		saveToXml(); //存入XML
		if(simpleadapter!=null){
			System.out.println("Adapter Change");
			AlarmList mainActive=(com.sandy.watchingTV.AlarmList) mainActivity;
			mainActive.onFresh();
		} 
		
		Toast.makeText(mainActivity,listBean.getPGName()+"已設定鬧鈴完成", Toast.LENGTH_LONG).show();
		dialogForm.dismiss();
	}
/**
 * 時間轉換MILLION
 * @param year
 * @param month
 * @param date
 * @param hour
 * @param min
 * @return
 */
private long getMillion(int year ,int month ,int date,int hour ,int min){
	Calendar calandar=Calendar.getInstance();
	System.out.println("current"+System.currentTimeMillis());
	calandar.set(year, month-1, date, hour, min);	
	System.out.println(calandar);
	return calandar.getTimeInMillis();
}
private void saveToXml(){
	String setAlarmBean="0"+","+listBean.getTVChannel()+","+listBean.getTVName()+","+listBean.getPGName().replace(",", "")+","
                        +listBean.getPGTime()+","+listBean.getSDate()+","+listBean.getSTime()+
                        ","+String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day)+"-"
                        +String.valueOf(hour)+"-"+String.valueOf(min)+"-";
	System.out.println("writeXML:"+setAlarmBean);
	editor.putString(SID,setAlarmBean);
	editor.commit();
}

private int setAlarm(){
	try{
	System.out.println("setAlarm--"+SID);
	AlarmManager alarmManger=(AlarmManager)mainActivity.getSystemService(mainActivity.ALARM_SERVICE); 
	Intent intent= new Intent( );
	intent.setClass(mainActivity, AlarmReceiver.class);
	intent.putExtra("SID", SID);//存ID給CLASS呼叫
	PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	alarmManger.set(AlarmManager.RTC_WAKEUP, million, pendingIntent);
	
	
	return 1;
	}catch(Exception e){
		e.printStackTrace();
		return 0;
	}
}
private int setBean1(){
	try{
	System.out.println(listBean.getPGName());//節目名稱
	System.out.println(listBean.getPGTime());//節目時間
	System.out.println(listBean.getSTime());//設定時間
	System.out.println(listBean.getSDate());//設定日期
	System.out.println(listBean.getTVChannel());//電視頻道
	String date[]=listBean.getSDate().split("-");//設定日期分割月日
	String time[]=listBean.getSTime().split("-");//設定時間時分
	String tempPgTime[]=listBean.getPGTime().split(",");//抓節目撥出時間日,時
	String tempPgDate[]=tempPgTime[0].split(":");//抓節目撥出日期
	String tempPgDate2[]=tempPgDate[1].split("-");//節目撥出日期年月日
	 PgTime=Integer.valueOf(tempPgTime[1]);//節目撥出時間
	 PgDate=Integer.valueOf(tempPgDate2[2]);//節目撥出日期
	 year=Integer.valueOf(date[0]);//設定年
	 month=Integer.valueOf(date[1]);//設定月
	 day=Integer.valueOf(date[2]);//設定日
	 min=Integer.valueOf(time[1]);//設定分
	 hour=Integer.valueOf(time[0]);//設定時
	 SPGDate=tempPgDate[1];
	 SPGTime=tempPgTime[1];
	 System.out.println("SPGDate"+SPGDate+SPGTime);
	 System.out.println(listBean.getTVChannel());
	 String ch=listBean.getTVChannel();
		if(ch.indexOf("CHP")>-1){
			SID=listBean.getTVChannel().replace("頻道數:CHP", "1")//設定ID 頻道+節目日期+時間
					+tempPgDate2[2]+
					SPGTime;
		}else if(ch.indexOf("CHO")>-1){
			SID=listBean.getTVChannel().replace("頻道數:CHO", "1")//設定ID 頻道+節目日期+時間
					+tempPgDate2[2]+
					SPGTime;
		}else if(ch.indexOf("CHM")>-1){
			SID=listBean.getTVChannel().replace("頻道數:CHM", "1")//設定ID 頻道+節目日期+時間
					+tempPgDate2[2]+
					SPGTime;
		}else if(ch.indexOf("CHD")>-1){
			SID=listBean.getTVChannel().replace("頻道數:CHD", "1")//設定ID 頻道+節目日期+時間
					+tempPgDate2[2]+
					SPGTime;
		}else{
		SID=listBean.getTVChannel().replace("頻道數:CH", "0")//設定ID 頻道+節目日期+時間
				+tempPgDate2[2]+
				SPGTime;
		}
	ID=Integer.valueOf(SID);//轉換INTEGER
	System.out.println("SID_Print"+SID);
	System.out.println("ID_Print:"+ID);
	System.out.println(year+"."+month+"."+day+"."+hour+"."+min);
	 million=(Long)getMillion(year,month,day,hour,min);//設定多久鬧鈴million
	System.out.println("set million"+million);
	return 1;
	}catch(Exception e){
		e.printStackTrace();
		return 0;
	}
}

}
