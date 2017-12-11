package com.sandy.watchingTV;

import bean.AlertListBean;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver{
	SharedPreferences settings ;//要抓的XML
	SharedPreferences.Editor editor;//設定EDITOR XML
	String checkAlarm,ChannelNO1 ,ChannelTVName,ChannelNo2,PGName,PGDate,
	PGTime,PGSDate,PGSTime,PGSDT;
	String fixPGName;
	String SID ;
	AlarmList mainClass;
	public AlarmReceiver(){
		
	}
	public AlarmReceiver(FragmentActivity a){
				
		mainClass=(com.sandy.watchingTV.AlarmList) a;
		
	}
 
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO 自動產生的方法 Stub
//		mainClass=(AlarmList) arg1.getBundleExtra("mainActivity").getSerializable("mainActivity");
//		
		 SID =arg1.getStringExtra("SID");  //抓ID
		 
		int id=Integer.valueOf(SID);  //抓ID integer
		settings=arg0.getSharedPreferences("setAlarmText", 0);//要抓的XML
		editor=settings.edit();//鬧鈴後將寫入1表示已鬧鈴
		Intent notificationIntent=new Intent(arg0, MainActivity.class);//通知後要執行的CLASS		
		PendingIntent pendingIntent = PendingIntent.getActivity(arg0, id, notificationIntent, id);
		System.out.println("Alerm Test");
		System.out.println("notification:"+SID);
		Builder builder=new NotificationCompat.Builder(arg0);//通知物件建立
		NotificationManager manager = (NotificationManager) arg0.getSystemService(arg0.NOTIFICATION_SERVICE);//通知物件類型
		getPGSetInfo(SID);//抓XML的值
		Toast.makeText(arg0, "節目提醒..."+PGName, Toast.LENGTH_LONG).show();
		
		builder.setSmallIcon(R.drawable.ic_launcher)//設定小圖示
		.setDefaults(Notification.DEFAULT_ALL)//設定閃光聲音都預設值
		.setContentText("撥出"+PGDate+","+PGTime+","+fixPGName)//設定內容
		.setContentTitle(ChannelNo2+ChannelTVName)//設定TITLE
		.setContentIntent(pendingIntent)//設定點開後要執行的CLASS
		.setTicker("節目提醒");
		saveToXml();
		builder.setAutoCancel(true);//點開後自動關閉NOTIFICATION
		manager.notify(Integer.valueOf(SID), builder.build());//發出通知
		if(mainClass!=null){
			System.out.println("MainClass is not null");
			 mainClass.onFresh();
			
		 }
	}
	private void getPGSetInfo(String sid){
		System.out.println("SIDPrint2:"+sid);
		String vlaue=settings.getString(sid, " ");
		
		String temp[]=vlaue.split(",");
		checkAlarm=temp[0];
		ChannelNO1=temp[1];
		ChannelTVName=temp[2];
		ChannelNo2=temp[3];
		PGName=temp[4];
		PGDate=temp[5];
		PGTime=temp[6];
		PGSDate=temp[7];
		PGSTime=temp[8];
		PGSDT=temp[9];
		String tempx[]=PGName.split(":");
		fixPGName=tempx[1];
		System.out.println("ARPGName:"+PGName);
	}
private void saveToXml(){
	String setAlarmBean="1"+","+ChannelNO1+","+ChannelTVName+","+ChannelNo2+","+PGName+","
            +PGDate+","+PGTime+","+PGSDate+
            ","+PGSTime+","+PGSDT;
	System.out.println("AlarmReceiverView:"+setAlarmBean);
	editor.putString(SID,setAlarmBean);
	editor.commit();
}
}
