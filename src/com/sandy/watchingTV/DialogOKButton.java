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
	FragmentActivity mainActivity; //�D�{���e��
	AlertListBean listBean;//�`�س]�wBean
	int PgTime=0;//�`�خɶ�
	int PgDate=0;//�`�ؤ��
	int year=0;//�]�w�~
	int month=0;//�]�w��
	int day=0;//�]�w��
	int min=0;//�]�w��
	int hour=0;//�]�w��
	String SID=null;//�]�w�r��ID
	String SPGDate=null;//�]�w�`�ؤ���r��
	String SPGTime=null;//�]�w�`�خɶ��r��
	int ID=0;//�]�wINTGER ID
	Long million;//�]�w�x�aMILLION
	SharedPreferences settings ; //�]�w�s�J�`�ظ�TXML
	SharedPreferences.Editor editor;//�]�wEDITOR XML
	AlertDialog dialogForm; //�]�wDIALOG ALERT
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
		// TODO �۰ʲ��ͪ���k Stub
		setBean1(); //��ƶ�JBEAN
		setAlarm(); //�x�a�]�w
		saveToXml(); //�s�JXML
		if(simpleadapter!=null){
			System.out.println("Adapter Change");
			AlarmList mainActive=(com.sandy.watchingTV.AlarmList) mainActivity;
			mainActive.onFresh();
		} 
		
		Toast.makeText(mainActivity,listBean.getPGName()+"�w�]�w�x�a����", Toast.LENGTH_LONG).show();
		dialogForm.dismiss();
	}
/**
 * �ɶ��ഫMILLION
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
	intent.putExtra("SID", SID);//�sID��CLASS�I�s
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
	System.out.println(listBean.getPGName());//�`�ئW��
	System.out.println(listBean.getPGTime());//�`�خɶ�
	System.out.println(listBean.getSTime());//�]�w�ɶ�
	System.out.println(listBean.getSDate());//�]�w���
	System.out.println(listBean.getTVChannel());//�q���W�D
	String date[]=listBean.getSDate().split("-");//�]�w������Τ��
	String time[]=listBean.getSTime().split("-");//�]�w�ɶ��ɤ�
	String tempPgTime[]=listBean.getPGTime().split(",");//��`�ؼ��X�ɶ���,��
	String tempPgDate[]=tempPgTime[0].split(":");//��`�ؼ��X���
	String tempPgDate2[]=tempPgDate[1].split("-");//�`�ؼ��X����~���
	 PgTime=Integer.valueOf(tempPgTime[1]);//�`�ؼ��X�ɶ�
	 PgDate=Integer.valueOf(tempPgDate2[2]);//�`�ؼ��X���
	 year=Integer.valueOf(date[0]);//�]�w�~
	 month=Integer.valueOf(date[1]);//�]�w��
	 day=Integer.valueOf(date[2]);//�]�w��
	 min=Integer.valueOf(time[1]);//�]�w��
	 hour=Integer.valueOf(time[0]);//�]�w��
	 SPGDate=tempPgDate[1];
	 SPGTime=tempPgTime[1];
	 System.out.println("SPGDate"+SPGDate+SPGTime);
	 System.out.println(listBean.getTVChannel());
	 String ch=listBean.getTVChannel();
		if(ch.indexOf("CHP")>-1){
			SID=listBean.getTVChannel().replace("�W�D��:CHP", "1")//�]�wID �W�D+�`�ؤ��+�ɶ�
					+tempPgDate2[2]+
					SPGTime;
		}else if(ch.indexOf("CHO")>-1){
			SID=listBean.getTVChannel().replace("�W�D��:CHO", "1")//�]�wID �W�D+�`�ؤ��+�ɶ�
					+tempPgDate2[2]+
					SPGTime;
		}else if(ch.indexOf("CHM")>-1){
			SID=listBean.getTVChannel().replace("�W�D��:CHM", "1")//�]�wID �W�D+�`�ؤ��+�ɶ�
					+tempPgDate2[2]+
					SPGTime;
		}else if(ch.indexOf("CHD")>-1){
			SID=listBean.getTVChannel().replace("�W�D��:CHD", "1")//�]�wID �W�D+�`�ؤ��+�ɶ�
					+tempPgDate2[2]+
					SPGTime;
		}else{
		SID=listBean.getTVChannel().replace("�W�D��:CH", "0")//�]�wID �W�D+�`�ؤ��+�ɶ�
				+tempPgDate2[2]+
				SPGTime;
		}
	ID=Integer.valueOf(SID);//�ഫINTEGER
	System.out.println("SID_Print"+SID);
	System.out.println("ID_Print:"+ID);
	System.out.println(year+"."+month+"."+day+"."+hour+"."+min);
	 million=(Long)getMillion(year,month,day,hour,min);//�]�w�h�[�x�amillion
	System.out.println("set million"+million);
	return 1;
	}catch(Exception e){
		e.printStackTrace();
		return 0;
	}
}

}
