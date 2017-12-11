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
	SharedPreferences settings ;//�n�쪺XML
	SharedPreferences.Editor editor;//�]�wEDITOR XML
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
		// TODO �۰ʲ��ͪ���k Stub
//		mainClass=(AlarmList) arg1.getBundleExtra("mainActivity").getSerializable("mainActivity");
//		
		 SID =arg1.getStringExtra("SID");  //��ID
		 
		int id=Integer.valueOf(SID);  //��ID integer
		settings=arg0.getSharedPreferences("setAlarmText", 0);//�n�쪺XML
		editor=settings.edit();//�x�a��N�g�J1��ܤw�x�a
		Intent notificationIntent=new Intent(arg0, MainActivity.class);//�q����n���檺CLASS		
		PendingIntent pendingIntent = PendingIntent.getActivity(arg0, id, notificationIntent, id);
		System.out.println("Alerm Test");
		System.out.println("notification:"+SID);
		Builder builder=new NotificationCompat.Builder(arg0);//�q������إ�
		NotificationManager manager = (NotificationManager) arg0.getSystemService(arg0.NOTIFICATION_SERVICE);//�q����������
		getPGSetInfo(SID);//��XML����
		Toast.makeText(arg0, "�`�ش���..."+PGName, Toast.LENGTH_LONG).show();
		
		builder.setSmallIcon(R.drawable.ic_launcher)//�]�w�p�ϥ�
		.setDefaults(Notification.DEFAULT_ALL)//�]�w�{���n�����w�]��
		.setContentText("���X"+PGDate+","+PGTime+","+fixPGName)//�]�w���e
		.setContentTitle(ChannelNo2+ChannelTVName)//�]�wTITLE
		.setContentIntent(pendingIntent)//�]�w�I�}��n���檺CLASS
		.setTicker("�`�ش���");
		saveToXml();
		builder.setAutoCancel(true);//�I�}��۰�����NOTIFICATION
		manager.notify(Integer.valueOf(SID), builder.build());//�o�X�q��
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
