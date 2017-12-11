package com.sandy.watchingTV;
 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import bean.AlertListBean;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.AdapterView.*;
public class LongClickProgram implements OnItemLongClickListener {
	 AlertDialog dialog; //對話框物件
	 FragmentActivity mainActivity; //主程式物件
	 private TextView TVName,channel,showTime,Program; //文字方塊物件
	 private NumberPicker setDate; //日期物件
	 private TimePicker setTime; //時間物件
	 private HashMap Message; //容器
	 private TextView TVSet;//標題
	 int Hour;
	 int min;
	 AlertListBean listBean;
	 String avdanteDate[];
	 SharedPreferences settings ; //設定存入節目資訊XML
	 String SID,PGDate,PGTime,STime,SDate,PGName,TVChannel,STVName;
	 public LongClickProgram(FragmentActivity  FA) {
		// TODO 自動產生的建構子 Stub
		 mainActivity=FA;
		 settings=FA.getSharedPreferences("setAlarmText",0);
		 
	}
	 
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO 自動產生的方法 Stub
		  listBean=new AlertListBean();//傳遞設定值
		TextView mainText ;//List內部文字
		LayoutInflater inflater =mainActivity.getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		View priview=inflater.inflate(R.layout.set_time_alert,null);
		TVName=(TextView) priview.findViewById(R.id.TVName);
		channel=(TextView)priview.findViewById(R.id.channel);
		showTime=(TextView)priview.findViewById(R.id.showTime);
		Program=(TextView)priview.findViewById(R.id.Program);
		setDate=(NumberPicker) priview.findViewById(R.id.setDate);
		setTime=(TimePicker) priview.findViewById(R.id.setTime);
		mainText=(TextView) arg1.findViewById(R.id.setTVTime);
		TVSet=(TextView) priview.findViewById(R.id.TVSet);
		int color=mainText.getTextColors().getDefaultColor();//獲得文字顏色
		int XMLSize=settings.getAll().size();
		System.out.println("Color defind:"+color);
		System.out.println(String.format("#%06X", color));
		
		builder.setView(priview);
		builder.setPositiveButton("設定", null);
		builder.setNegativeButton("取消", null);
		
		if(color==-38476 && XMLSize<10 ){//顏色為粉紅色就打開DIALOG
		dialog=builder.create();
		dialog.show();
		System.out.println("XML Size:"+settings.getAll().size());//記得限制鬧鐘個數。
		Button Modify_button=dialog.getButton(Dialog.BUTTON_POSITIVE);
		ListView listView = (ListView) arg0;
		Message=(HashMap) listView.getItemAtPosition(arg2);
		avdanteDate=activeDate(Message.get("PDate").toString());
		System.out.println("Errortest:"+Message.get("PDate").toString());
		int maxValue=avdanteDate.length-1; //陣列大小
		setDate.setMaxValue(maxValue);
		setDate.setMinValue(0);
		setDate.setDisplayedValues(activeDateWeek(Message.get("PDate").toString()));//可設定時間陣列
		setDate.setValue(maxValue);//設定setDate物件大小
		
		 TVName.setText((CharSequence) Message.get("TVNameChannel"));//設定文字
		 channel.setText("頻道數:"+Message.get("TVNameChannel").toString().split(",")[1]);//設定文字
		 showTime.setText("時間:"+(CharSequence) Message.get("PDate")+","+(CharSequence) Message.get("PTime"));//設定文字
		 Program.setText("節目名稱:"+(CharSequence) Message.get("PName"));//設定文字
		System.out.println("PDate--"+Message.get("PDate")); 
		//取得TimePicker 時間方法
		setTime.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO 自動產生的方法 Stub
				Hour=hourOfDay;
				min=minute;
				
				String SHour;
				String Smin;
				if(Hour<10){//十進位補零
					SHour="0"+String.valueOf(Hour);
				}else{
					SHour=String.valueOf(Hour);
				}
				if(min<10){
					Smin="0"+String.valueOf(min);
				}else{
					Smin=String.valueOf(min);
				}
				System.out.println("timeset:"+SHour+":"+Smin);
				listBean.setSTime(SHour+"-"+Smin);
			}
		});
		//設定日期參數
		setDate.setOnValueChangedListener(new OnValueChangeListener(){

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				// TODO 自動產生的方法 Stub
				listBean.setSDate(avdanteDate[newVal]);
				//System.out.println(avdanteDate[newVal]);
			}
			
		});
		setString();
		System.out.println(setTime.getCurrentHour().toString());//列印預設時間
		listBean.setSTime(STime);
		listBean.setPGName("節目名稱:"+PGName);
		listBean.setPGTime("時間:"+PGDate+","+PGTime);
		listBean.setSDate(SDate);
		listBean.setPDate(PGDate);
		listBean.setTVChannel("頻道數:"+TVChannel);
		listBean.setTVName(STVName+","+TVChannel);
		System.out.println("listBean1:"+listBean.getSDate()+","+
		listBean.getSTime()+","+listBean.getPGName()+","+
				listBean.getPGTime()+","+listBean.getTVChannel()+","+
		listBean.getTVName()+","+listBean.getPDate());
		String SID=getSID();
		if(settings.contains(SID)){//已經有的SID列提醒只更改不新增
	
		TVSet.setText(Html.fromHtml("節目撥出前提醒<font color=\"#ff1493\">(此節目已設定鬧鈴，仍可改時間)</font>"));
		Toast.makeText(mainActivity, "此節目已設定排程，仍可在此更改時間",  Toast.LENGTH_LONG).show();
		
		}
		DialogOKButton dButton=new DialogOKButton(mainActivity,listBean,dialog);
		Modify_button.setOnClickListener(dButton);
		
		}else{
			if (XMLSize >= 10) {
				Toast.makeText(mainActivity, "為了您的寶貴空間，鬧鐘請勿設定超過十個",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(mainActivity, "此節目已播過，故無法設定定時",
						Toast.LENGTH_LONG).show();
			}
			}
		 return false;
	}
	/**
	 * 
	 * @param PDate 節目撥出日期
	 * @return 可設定日期陣列
	 */
	private String[] activeDate(String PDate){
		String calendar[]=new String[8];
		DateFormat dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		
		
		
		for(int i=0;i<8;i++){
			Calendar dateValue=turnStringToD(PDate);
			dateValue.add(Calendar.DATE, i+(-7));//節目撥出七天前可設定定時
			System.out.println("setTime is :"+dateFormat.format(dateValue.getTime()));
			calendar[i]=dateFormat.format(dateValue.getTime());	
		}
		return calendar;
	}
	/**
	 * 
	 * @param PDate 節目撥出日期
	 * @return 可設定日期陣列SHOW 星期
	 */
	private String[] activeDateWeek(String PDate){
		String calendar[]=new String[8];
		DateFormat dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		
		
		
		for(int i=0;i<8;i++){
			Calendar dateValue=turnStringToD(PDate);
			dateValue.add(Calendar.DATE, i+(-7));//節目撥出七天前可設定定時
			String temp=dateFormat.format(dateValue.getTime());
			System.out.println("setTime is :"+temp+getWeek(temp));
			calendar[i]=temp+getWeek(temp);	
		}
		return calendar;
	}
	/**
	 * 
	 * @param date
	 * @return 那天星期幾
	 */
	protected static String getWeek(String date){
		Calendar  week=turnStringToD(date);
		 ///SimpleDateFormat date2DayFormat = new SimpleDateFormat( "u" );
		  
		 switch(week.get(Calendar.DAY_OF_WEEK) ){
		 case Calendar.MONDAY:
			 return "(一)";
		 case Calendar.TUESDAY:
			 return "(二)";
		 case Calendar.WEDNESDAY:
			 return "(三)";
		 case Calendar.THURSDAY:
			 return "(四)";
		 case Calendar.FRIDAY:
			 return "(五)";
		 case Calendar.SATURDAY:
			 return "(六)";
		 case Calendar.SUNDAY:
			 return "(日)";
		 default:
				 return "";
		 }
	}
	/**
	 * 字串轉換日期物件
	 * @param date 轉換字串
	 * @return 日期物件
	 */
	
	protected static Calendar turnStringToD(String date){
		String YMD[]=date.split("-");
		int year=Integer.valueOf(YMD[0]);
		int month=Integer.valueOf(YMD[1]);
		int day=Integer.valueOf(YMD[2]);
		Calendar c=new GregorianCalendar(year,month-1,day );
		return c;
	}
private  String getSID(){
	String date[]=listBean.getSDate().split("-");//設定日期分割月日
	String time[]=listBean.getSTime().split("-");//設定時間時分
	String tempPgTime[]=listBean.getPGTime().split(",");//抓節目撥出時間日,時
	String tempPgDate[]=tempPgTime[0].split(":");//抓節目撥出日期
	String tempPgDate2[]=tempPgDate[1].split("-");//節目撥出日期年月日
	// int PgTime=Integer.valueOf(tempPgTime[1]);//節目撥出時間
	// int PgDate=Integer.valueOf(tempPgDate2[2]);//節目撥出日期
	String ch=listBean.getTVChannel();
	if(ch.indexOf("CHP")>-1){
		SID=listBean.getTVChannel().replace("頻道數:CHP", "1")//設定ID 頻道+節目日期+時間
				+tempPgDate2[2]+
				tempPgTime[1];
	}else if(ch.indexOf("CHO")>-1){
		SID=listBean.getTVChannel().replace("頻道數:CHO", "1")//設定ID 頻道+節目日期+時間
				+tempPgDate2[2]+
				tempPgTime[1];
	}else if(ch.indexOf("CHM")>-1){
		SID=listBean.getTVChannel().replace("頻道數:CHM", "1")//設定ID 頻道+節目日期+時間
				+tempPgDate2[2]+
				tempPgTime[1];
	}else if(ch.indexOf("CHD")>-1){
		SID=listBean.getTVChannel().replace("頻道數:CHD", "1")//設定ID 頻道+節目日期+時間
				+tempPgDate2[2]+
				tempPgTime[1];
	}else{
	SID=listBean.getTVChannel().replace("頻道數:CH", "0")//設定ID 頻道+節目日期+時間
			+tempPgDate2[2]+
			tempPgTime[1];
	}
	System.out.println("longClickGetID:"+SID);
	return SID;
}
private void setString(){
	STime=setTime.getCurrentHour().toString()+"-"+setTime.getCurrentMinute();
	SDate=avdanteDate[setDate.getValue()];
	PGName=Message.get("PName").toString();
	PGTime=Message.get("PTime").toString();
	PGDate=Message.get("PDate").toString();
	TVChannel=Message.get("TVNameChannel").toString().split(",")[1];
	STVName=Message.get("TVNameChannel").toString().split(",")[0];
	System.out.println("outPutString1:"+STime+","+
	SDate+","+PGName+","+PGTime+","+PGDate+","+
			TVChannel+","+STVName);
}
	
}
