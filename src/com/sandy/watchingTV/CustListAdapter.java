package com.sandy.watchingTV;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CustListAdapter extends SimpleAdapter {
	 int checkAlarm=0; 
     View mainContext;
      
	 HashMap<String,String> HMDate;
	AlarmList main;
	 String TVName;
	 String PGName;
	 String PGDate;
	 String PGTime;
	 String PGOSetTime;
	 String PGOSetDate;
	 String PGNSetTime;
	 String PGNSetDate;
	 Long setMillion;
	public CustListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO 自動產生的建構子 Stub
		main=(AlarmList) context;
	}
	 @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO 自動產生的方法 Stub
			 mainContext=super.getView(position, convertView, parent);
			 TextView subText=(TextView) mainContext.findViewById(R.id.setTVTime);
			 TextView mainText=(TextView) mainContext.findViewById(R.id.setTVName);
			 HMDate=(HashMap<String,String>)getItem(position);
			boolean check= getCheck(HMDate.get("checkAlarm"));
			boolean check2=getOverTime(HMDate.get("setInfo"));
			boolean check3=getOverPGTime(HMDate.get("PGInfo"));
			System.out.println("TVInfo:"+HMDate.get("PGInfo"));
			 setColor(check, check2,check3, subText, mainText);
			 mainContext
             .setBackgroundColor(main.selectItem.get(position) ? 0xffffc0cb
                     : 0xffffffff);
			return mainContext;
			
		}
	 protected boolean getCheck(String check){
		 System.out.println("check"+check);
		 String temp[]=check.split(",");// sample 023152100:1,頻道數:CH23
		 String temp2[]=temp[0].split(":");//sample 023152100:1
		 String scheck=temp2[1];//1
		 System.out.println(scheck);
		 if(scheck.equals("1")){
			 System.out.println("true");
			 return true;
		 }else{
			 System.out.println("false");
			 return false;
		 }
		 
	 }
	 protected void setColor(Boolean check,Boolean check2,Boolean check3,TextView sub1,TextView sub2){
		 System.out.println("setColor:"+check);
		 System.out.println("setColor2:"+check2+"color3:"+check3);
		 if(check||check2||check3){
			 if(check){
				 sub1.setText(sub1.getText()+"(已鬧鈴)");
				 sub1.setTextColor(0xff000000);//設黑色，鬧鈴過時一樣能設定
				 sub2.setTextColor(0xff000000);
			 }
			 if(check2){
				 sub1.setText(sub1.getText()+"(鬧鈴過時)");
				 sub1.setTextColor(0xff000000);//設黑色，鬧鈴過時一樣能設定
				 sub2.setTextColor(0xff000000);
			 }
			 if(check3){
				 sub1.setText(sub1.getText()+"(節目過時)");
				 sub1.setTextColor(0xffBEBEBE);
				 sub2.setTextColor(0xffBEBEBE);
			 }
			  
		 }else{
			 sub1.setTextColor(0xff000000);
			 sub2.setTextColor(0xff000000);
		 }
		  
	 }
	
	 private long getMillion(int year ,int month ,int date,int hour ,int min){
			Calendar calandar=Calendar.getInstance();
			System.out.println("current"+System.currentTimeMillis());
			calandar.set(year, month-1, date, hour, min);	
			System.out.println(calandar);
			return calandar.getTimeInMillis();
		}
	 protected boolean getOverTime(String setTime){
		 System.out.println(setTime);
		 String temp[]=setTime.split(","); //  2016-06-15,0-2
         String time=temp[1]; //0-2
         String date=temp[0];//2016-06-15
         String timeSplit[]=time.split("-");
         String dateSplit[]=date.split("-");
         int hour=Integer.valueOf(timeSplit[0]);
         int min=Integer.valueOf(timeSplit[1]);
         int year=Integer.valueOf(dateSplit[0]);
         int month=Integer.valueOf(dateSplit[1]);
         int day=Integer.valueOf(dateSplit[2]);
         System.out.println("time2set:"+year+","+month+","+day+","+hour+","+min);
         Long million=getMillion(year, month, day, hour, min);
         System.out.println("setMillion:"+million);
         if(million<System.currentTimeMillis()){
        	 return true;//過時回傳真
         }else{
        	 return false;//為過時回傳假
         }
         
	 }
	 protected boolean getOverPGTime(String setTime){
		 System.out.println(setTime);
		 String tempS[]=setTime.split(",");// TVInfo:節目名稱:大胃女王吃遍日本,時間:2016-06-15,0320
		 String alltime=tempS[1]+","+tempS[2];//時間:2016-06-15,0320
		 String  tempTime[]=alltime.split(",");
		 String tempDate[]=tempTime[0].split(":");
		 String date[]=tempDate[1].split("-");
		 String Hour=tempTime[1].substring(0,2);
		 System.out.println("hour"+Hour);
		 String min=tempTime[1].substring(2);
		 System.out.println(min);
		 String year=date[0];
		 String month=date[1];
		 String day=date[2];
		 System.out.println("time3set:"+year+","+month+","+day+","+Hour+","+min);
		 Long million=getMillion(Integer.valueOf(year), Integer.valueOf(month)
				 ,Integer.valueOf(day), Integer.valueOf(Hour), Integer.valueOf(min));
		 System.out.println("million3.."+million);
		 if(million<System.currentTimeMillis()){
        	 return true;//過時回傳真
         }else{
        	 return false;//為過時回傳假
         }
	 }
}
